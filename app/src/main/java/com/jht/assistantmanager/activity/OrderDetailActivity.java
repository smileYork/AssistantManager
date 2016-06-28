package com.jht.assistantmanager.activity;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.model.OrderRecord;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderDetailActivity extends Activity {

	private ImageButton img_bt_return;

	private String orderNumber;

	private Context mContext;

	private Dialog dialog;

	private MyDBOperation db;

	private String code;

	private String error;

	private String result;

	private JSONObject object;

	private Gson gson;

	private OrderRecord orderRecord;

	private TextView tv_orderNo;

	private TextView tv_orderContent;

	private TextView tv_address;

	private TextView tv_orderType;

	private TextView tv_orderPrice;

	private TextView tv_orderTime;

	private TextView tv_orderMemo;
	private View viewshow;
	private LinearLayout linaddr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_order_detail);

		// 沉浸式状态栏
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		}

		findView();

		getOrderInfo();

		setListen();
	}

	private void findView() {

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_order_detail_return);

		mContext = OrderDetailActivity.this;

		orderNumber = getIntent().getStringExtra("orderNumber");
		
		System.out.println(orderNumber);

		db = new MyDBOperation(mContext);

		tv_orderNo = (TextView) findViewById(R.id.tv_order_detail_no);

		tv_orderContent = (TextView) findViewById(R.id.tv_order_detail_content);

		tv_address = (TextView) findViewById(R.id.tv_order_detail_address);

		tv_orderType = (TextView) findViewById(R.id.tv_order_detail_order_type);

		tv_orderPrice = (TextView) findViewById(R.id.tv_order_detail_price);

		tv_orderTime = (TextView) findViewById(R.id.tv_order_detail_time);

		tv_orderMemo = (TextView) findViewById(R.id.tv_order_detail_memo);

		viewshow = (View) findViewById(R.id.viewshow);

		linaddr = (LinearLayout) findViewById(R.id.linaddr);

	}

	private void initView(OrderRecord tempOrderRecord) {

		tv_orderNo.setText(tempOrderRecord.getOrderno());

		String arr[] = tempOrderRecord.getOrdercontent().split("[; ]");

		String content = "";

		for (int i = 0; i < arr.length; i++) {

			content += "\n\n" + arr[i] + "\n\n";
		}

		tv_orderContent.setText(content);

		if (null != tempOrderRecord.getFulladdr()) {
			tv_address.setText(tempOrderRecord.getFulladdr());
		} else {
			viewshow.setVisibility(View.GONE);
			linaddr.setVisibility(View.GONE);
		}

		tv_orderType.setText(tempOrderRecord.getStatusname());

		tv_orderPrice.setText("" + tempOrderRecord.getCardprice());

		tv_orderTime.setText(tempOrderRecord.getSubmitdate());

		tv_orderMemo.setText(tempOrderRecord.getBuyermemo());

	}

	private void setListen() {
		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});
	}

	private void getOrderInfo() {
		try {
			dialog = LoadingDialog.createLoadingDialog(mContext, "正在查询券卡号,请稍等...");
			dialog.show();
			if (NetWorkJudgeUtil.isConnect(mContext)) {
				initRequestInfo();
				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Card_Search_Order_Url, entity,
						GlobalData.ContentType, searchHttpResponseHandler);
			} else {
				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");
			}

		} catch (Exception e) {

			dialog.dismiss();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");

		}

	}

	private void initRequestInfo() throws JSONException {

		object = new JSONObject();
		object.put("usercode", db.getFlowCode());

		object.put("usertype", 1);

		object.put("method", "queryorderno");

		object.put("orderno", orderNumber);

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

					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						result = object.getString("result");

						gson = new Gson();

						orderRecord = gson.fromJson(result, OrderRecord.class);

						initView(orderRecord);

					} else {// 查询失败

						dialog.dismiss();
						ToastShow.ToastShowShort(mContext, "" + error);
						finish();
					}

				} catch (JSONException e) {

					e.printStackTrace();

					dialog.dismiss();

					ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

				}

			}
		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

			dialog.dismiss();

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

		}

	};

}