package com.jht.assistantmanager.activity;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.util.AppUtil;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class UploadOrderActivity extends Activity {

	private ImageButton img_bt_return;

	private TextView tv_order_id;

	private TextView tv_order_detail;

	private TextView tv_order_price;

	private EditText ed_memo;

	private String orderId;

	private String orderDetail;

	private Double OrderPrice;

	private Dialog dialog;

	private Button bt_click;

	private Context mContext;

	private MyDBOperation db;

	private String code;

	private String error;

	private JSONObject object;

	private String memo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_upload_order);

		setTitleStyle();

		findView();

		initView();

		setListen();
	}

	private void setTitleStyle() {

		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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

				uploadOrderToServer();

			}
		});
	}

	protected void uploadOrderToServer() {

		try {

			memo = ed_memo.getText().toString();

			dialog = LoadingDialog.createLoadingDialog(mContext, "正在上传订单信息,请稍等...");

			dialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfo();

				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Orderall, entity, GlobalData.ContentType,
						searchHttpResponseHandler);

			} else {

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

				finish();

			}

		} catch (

		Exception e) {

			dialog.dismiss();

			ToastShow.ToastShowLong(mContext, "系统繁忙，请稍后再试!");

			e.printStackTrace();

		}

	}

	private void initRequestInfo() throws JSONException {

		object = new JSONObject();

		JSONObject bojectTemp = new JSONObject();

		bojectTemp.put("codelist", orderDetail);

		bojectTemp.put("excdescribe", "");

		bojectTemp.put("handlerstate", -1);

		bojectTemp.put("orderid", orderId);

		bojectTemp.put("paydescribe", memo);

		bojectTemp.put("orderstate", 0);

		bojectTemp.put("price", OrderPrice);

		bojectTemp.put("uuid", AppUtil.getUUID());

		object.put("usercode", db.getFlowCode());

		object.put("usertype", 1);

		object.put("method", "insertapporder");

		object.put("jsondate", bojectTemp);

		object.put("timestamp", System.currentTimeMillis());

		object.put("memo", "");

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

						ToastShow.ToastShowShort(mContext, "创建订单成功！");

						db.deleteAllLocalSaleItem();

						Intent intent = new Intent(mContext, CheckMoneyActivity.class);

						intent.putExtra("orderid", orderId);

						intent.putExtra("codes", orderDetail);

						intent.putExtra("money", OrderPrice);

						intent.putExtra("memo", memo);

						startActivity(intent);

						finish();

					} else {// 查询失败

						dialog.dismiss();

						ToastShow.ToastShowShort(mContext, "上传订单失败，" + error);

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

	private void initView() {

		orderId = AppUtil.generateOrderId(db.getUserInfo("khdm"));

		orderDetail = db.getOrderCodeList();

		OrderPrice = db.getSaleListSumMoney();

		String[] str = orderDetail.split(",");

		String oderListShow = "";

		for (int i = 0; i < str.length; i++) {

			if (i == str.length - 1) {

				oderListShow += str[i] + "\n";

			} else {

				oderListShow += str[i] + "\n\n";

			}

		}

		tv_order_id.setText(orderId);

		tv_order_detail.setText(oderListShow);

		tv_order_price.setText("" + OrderPrice);

	}

	private void findView() {

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_upload_order_return);

		tv_order_id = (TextView) findViewById(R.id.tv_activity_upload_order_oderno);

		tv_order_detail = (TextView) findViewById(R.id.tv_activity_upload_order_oderdetail);

		tv_order_price = (TextView) findViewById(R.id.tv_activity_upload_order_oderprice);

		bt_click = (Button) findViewById(R.id.bt_upload_order_cehck);

		ed_memo = (EditText) findViewById(R.id.tv_activity_upload_order_odermemo);

		mContext = UploadOrderActivity.this;

		db = new MyDBOperation(mContext);

	}

}