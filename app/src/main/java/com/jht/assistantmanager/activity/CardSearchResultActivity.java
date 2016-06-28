package com.jht.assistantmanager.activity;

import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.util.AppUtil;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class CardSearchResultActivity extends Activity {

	private ImageButton img_bt_return;

	private TextView tv_number;

	private TextView tv_type;

	private TextView tv_price;

	private TextView tv_combine;

	private TextView tv_vip;

	private TextView tv_changeTimeName;

	private TextView tv_time;

	private String cardNumber;

	private Context mContext;

	private Dialog dialog;

	private MyDBOperation db;

	private String code;

	private String error;

	private String result;

	private String failure;

	private JSONObject object;

	private Button bt_click;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try {

			super.onCreate(savedInstanceState);

			this.requestWindowFeature(Window.FEATURE_NO_TITLE);

			setContentView(R.layout.activity_card_serach_result);

			setTitleStyle();

			findView();

			getCardInfo();

			setListen();

		} catch (Exception e) {

			e.printStackTrace();

			ToastShow.ToastShowShort(CardSearchResultActivity.this, "网络异常,请稍后再试1");

		}
	}

	private void setTitleStyle() {

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

		bt_click.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void getCardInfo() {

		try {

			dialog = LoadingDialog.createLoadingDialog(mContext, "正在查询券卡号,请稍等...");

			dialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfo();

				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Card_Search_Card_Url, entity,
						GlobalData.ContentType, searchHttpResponseHandler);

			} else {

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

				finish();

			}

		} catch (Exception e) {

			dialog.dismiss();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");

			finish();

		}

	}

	private void initRequestInfo() throws JSONException {

		object = new JSONObject();

		object.put("usercode", db.getFlowCode());

		object.put("usertype", 1);

		object.put("method", "querycardno");

		object.put("code", cardNumber);

		object.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(object, db.getMd5Key());

		object.put("sign", sign);

	}

	private AsyncHttpResponseHandler searchHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultStr = new String(responseBody);

			dialog.dismiss();

			if (resultStr != null && resultStr.length() > 0) {

				try {

					object = new JSONObject(resultStr);

					code = object.getString("code");

					error = object.getString("error");

					// 登录成功
					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						result = object.getString("result");

						Log.i("First", result);

						initView(result);

					} else {// 查询失败

						failure = object.getString("error");

						dialog.dismiss();

						ToastShow.ToastShowShort(mContext, failure);

						finish();
					}

				} catch (JSONException e) {

					e.printStackTrace();

					dialog.dismiss();

					ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

					finish();

				}

			}

		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
			dialog.dismiss();

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

			finish();
		}


	};

	private void findView() {

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_card_search_result_return);

		tv_number = (TextView) findViewById(R.id.tv_card_search_result_number);

		tv_type = (TextView) findViewById(R.id.tv_card_search_result_type);

		tv_price = (TextView) findViewById(R.id.tv_card_search_result_price);

		tv_combine = (TextView) findViewById(R.id.tv_card_search_result_combine);

		tv_vip = (TextView) findViewById(R.id.tv_card_search_result_small_price);

		tv_time = (TextView) findViewById(R.id.tv_card_search_result_time);

		tv_changeTimeName = (TextView) findViewById(R.id.tv_card_search_result_time_change);

		cardNumber = getIntent().getStringExtra("cardNumber");

		bt_click = (Button) findViewById(R.id.bt_card_search_result_click);

		mContext = CardSearchResultActivity.this;

		db = new MyDBOperation(mContext);

		if (!AppUtil.judgeCardNumber(cardNumber)) {

			ToastShow.ToastShowShort(mContext, "编码格式错误");

			finish();
		}

		cardNumber = cardNumber.substring(cardNumber.length() - 16, cardNumber.length());
	}

	private void initView(String result2) throws JSONException {

		object = new JSONObject(result2);

		tv_number.setText("" + object.getString("code"));

		tv_price.setText("" + object.getDouble("cardprice"));

		tv_time.setText(object.getString("actdate"));

		tv_type.setText(object.getString("statusname"));

		tv_vip.setText("" + object.getDouble("cardsmallprice"));

		tv_changeTimeName.setText(AppUtil.getTypeTime(object.getInt("state")));

		JSONObject tempObject = object.getJSONObject("defaultgoods");

		Iterator<?> it = tempObject.keys();

		if (it.hasNext()) {

			tv_combine.setText(object.getInt("selnum") + "种默认套餐");

		} else {

			if (object.toString().contains("goodslist")) {

				JSONArray tempArray = object.getJSONArray("goodslist");

				tv_combine.setText(tempArray.length() + "选" + object.getInt("selnum") + "套餐");

			} else {

				ToastShow.ToastShowShort(mContext, "没有可选套餐");

				finish();

			}

		}

	}
}