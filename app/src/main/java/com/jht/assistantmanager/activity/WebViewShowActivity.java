package com.jht.assistantmanager.activity;

import java.io.UnsupportedEncodingException;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebViewShowActivity extends Activity {

	private WebView webView;

	private ProgressBar progressBar;

	private String url = "NUll";

	private TextView tv_title;

	private ImageButton img_bt_return;

	private String orderId;

	private Context mContext;

	private Dialog lodingDialog;

	private MyDBOperation db;

	private String code;

	private String result;

	private String error;

	private JSONObject object;

	private String memo = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_web_view);

		try {

			setTitleType();

			findView();

			initView();

			setListen();

		} catch (Exception e) {

			e.printStackTrace();

			ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

			openActivity(MyOrderActivity.class, 0);

			finish();

		}

	}

	private void setTitleType() {

		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		}
	}

	private void setListen() {

		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				Log.i("First", "wxurl = " + url);

				if (url.startsWith("http:") || url.startsWith("https:")) {

					return false;

				}

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

				startActivity(intent);

				return true;
			}

			// 监听到跳转到支付完成页面
			@Override
			public void onPageFinished(WebView view, String url) {

				super.onPageFinished(view, url);

				if (url.startsWith("http://wx1.jhtsoft.com/wxpay/phsuccess.jsp")) {

					CheckOrder();

				} else if (url.startsWith(
						"http://jhtsoft.oicp.net/SPYSPT_APP/interAction!updateStatealy?orderid=" + orderId)) {

					CheckOrder();
				}

			}

			// || url.startsWith(
			// "http://jhtsoft.oicp.net/SPYSPT_APP/interAction!updateStatealy?orderid="
			// + orderId)
		});

		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {

			}

			@Override
			public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
				return super.onJsConfirm(view, url, message, result);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				if (newProgress == 100) {
					progressBar.setVisibility(View.GONE);
				}

				progressBar.setProgress(newProgress);

				super.onProgressChanged(view, newProgress);
			}

		});

	}

	protected void CheckOrder() {

		try {

			lodingDialog = LoadingDialog.createLoadingDialog(mContext, "正在确认支付情况,请稍等...");

			lodingDialog.show();

			Thread.sleep(3000);

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfo();

				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.My_Order_Url, entity,
						GlobalData.ContentType, searchHttpResponseHandler);

			} else {

				lodingDialog.dismiss();

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

				openActivity(MyOrderActivity.class, 0);

			}

		} catch (Exception e) {

			lodingDialog.dismiss();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");

			openActivity(MyOrderActivity.class, 0);

		}

	}

	private void openActivity(Class<?> aimclass, int i) {

		Intent intent = new Intent(mContext, aimclass);

		intent.putExtra("type", i);

		startActivity(intent);

		finish();

	}

	private AsyncHttpResponseHandler searchHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultStr = new String(responseBody);

			Log.i("First", resultStr);

			if (resultStr != null && resultStr.length() > 0) {

				try {

					object = new JSONObject(resultStr);

					code = object.getString("code");

					error = object.getString("error");

					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						result = object.getString("result");

						JSONObject tempObject = new JSONObject(result);

						if (tempObject.getInt("orderstate") == 1) {

							lodingDialog.setTitle("支付成功，正在激活.....");

							Intent intent = new Intent(mContext, ActiveActivity.class);

							intent.putExtra("orderid", tempObject.getString("orderid"));

							intent.putExtra("money", tempObject.getDouble("price"));

							intent.putExtra("codes", tempObject.getString("codelist"));

							intent.putExtra("memo", memo);

							intent.putExtra("uuid", tempObject.getString("uuid"));

							startActivity(intent);

							finish();

						} else {

							lodingDialog.dismiss();

							openActivity(MyOrderActivity.class, 0);

							finish();

						}

					} else {// 支付失败

						lodingDialog.dismiss();

						ToastShow.ToastShowShort(mContext, "" + error);

						openActivity(MyOrderActivity.class, 0);

						finish();

					}
				} catch (JSONException e) {

					e.printStackTrace();

					lodingDialog.dismiss();

					openActivity(MyOrderActivity.class, 0);

					ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

					finish();

				}

			} else {

				lodingDialog.dismiss();

				openActivity(MyOrderActivity.class, 0);

				ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

				finish();

			}

		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

			lodingDialog.dismiss();

			openActivity(MyOrderActivity.class, 0);

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

			finish();

		}

	};

	private void initRequestInfo() throws JSONException {

		object = new JSONObject();

		object.put("usercode", db.getFlowCode());

		object.put("usertype", 1);

		object.put("orderid", orderId);

		object.put("method", "getordermanagerinfo");

		object.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(object, db.getMd5Key());

		object.put("sign", sign);

	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() throws UnsupportedEncodingException {

		url = getIntent().getStringExtra("url");

		orderId = getIntent().getStringExtra("orderid");

		memo = getIntent().getStringExtra("memo");

		if (memo == null) {

			memo = "";
		}

		webView.loadUrl(url);

		WebSettings webSettings = webView.getSettings();

		webSettings.setUseWideViewPort(true);// 设置此属性，可任意比例缩放

		webSettings.setLoadWithOverviewMode(true);

		webSettings.setJavaScriptEnabled(true);

		webSettings.setBuiltInZoomControls(true);

		webSettings.setSupportZoom(true);

		webView.requestFocusFromTouch();

		tv_title.setText(getIntent().getStringExtra("title"));

	}

	private void findView() {

		webView = (WebView) findViewById(R.id.wv_web_view_show);

		progressBar = (ProgressBar) findViewById(R.id.pb_web_view_show);

		tv_title = (TextView) findViewById(R.id.tv_web_show_title);

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_web_show_return);

		mContext = WebViewShowActivity.this;

		db = new MyDBOperation(mContext);

	}

}
