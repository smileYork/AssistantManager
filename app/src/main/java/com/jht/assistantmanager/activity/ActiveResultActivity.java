package com.jht.assistantmanager.activity;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
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
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActiveResultActivity extends Activity {

	private ImageButton title_back_image;

	private Button btn_continue, btn_miss;

	private TextView tv_activeResult, tv_orderNo, tv_orderPrice, tv_orderMemo, tv_orderDetail, tv_active_sucess,
			tv_order_fail;

	private Context mContext;

	private String orderId;

	private String codes;

	private String code;

	private String error;

	private String result;

	private String failure;

	private double payMoney;

	private String memo;

	private Dialog loadingDialog;

	private MyDBOperation db;

	private String uuid;

	private JSONObject bojectTemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_active_result);

		try {

			findViewById();

			getInfo();

			uploadResult();

			setListener();

		} catch (Exception e) {

			ToastShow.ToastShowShort(mContext, "网络故障，稍后再试");

			e.printStackTrace();
		}
	}

	private void getInfo() {

		Intent intent = getIntent();

		orderId = intent.getStringExtra("orderid");

		error = intent.getStringExtra("error");

		codes = intent.getStringExtra("codes");

		code = intent.getStringExtra("code");

		result = intent.getStringExtra("result");

		failure = intent.getStringExtra("failure");

		payMoney = intent.getDoubleExtra("payMoney", 0);

		memo = intent.getStringExtra("memo");

		if (memo == null) {

			memo = "";
		}

		uuid = intent.getStringExtra("uuid");

	}

	private void uploadResult() {

		try {

			loadingDialog = LoadingDialog.createLoadingDialog(mContext, "正在更新列表状态,请稍等...");

			loadingDialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestForUpload();

				// 设定编码格式
				StringEntity entity = new StringEntity(bojectTemp.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.My_Order_Url, entity,
						GlobalData.ContentType, uploadResultHttpResponseHandler);

			} else {

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

				finish();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initRequestForUpload() throws JSONException {

		bojectTemp = new JSONObject();

		int tempResult = getActiveResult();

		if (tempResult == 3) {

			bojectTemp.put("excdescribe", getFailInfo());

		} else {

			bojectTemp.put("excdescribe", "");

		}

		bojectTemp.put("orderid", orderId);

		bojectTemp.put("orderstate", tempResult);

		bojectTemp.put("uuid", uuid);

		bojectTemp.put("usercode", db.getFlowCode());

		bojectTemp.put("usertype", 1);

		bojectTemp.put("memo", memo);

		bojectTemp.put("method", "updateapporderstate");

		bojectTemp.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(bojectTemp, db.getMd5Key());

		bojectTemp.put("sign", sign);

	}

	private String getFailInfo() throws JSONException {

		String tempStr = "";

		JSONArray jsonArray = new JSONArray(failure);

		for (int i = 0; i < jsonArray.length(); i++) {

			if (i == jsonArray.length() - 1) {

				tempStr += jsonArray.getJSONObject(i).getString("code") + "("
						+ jsonArray.getJSONObject(i).getString("statusname") + ")";

			} else {

				tempStr += jsonArray.getJSONObject(i).getString("code") + "("
						+ jsonArray.getJSONObject(i).getString("statusname") + "),";
			}

		}

		return tempStr;
	}

	private int getActiveResult() throws JSONException {

		int temp = 0;

		if (!code.isEmpty() && !error.isEmpty() && code.equals("200") && error.equalsIgnoreCase("success")) {

			JSONArray jsonArray = new JSONArray(result);

			JSONArray jsonArray2 = new JSONArray(failure);

			// 激活全部成功
			if (jsonArray.length() != 0 && jsonArray2.length() == 0) {

				temp = 2;
				// 激活部分成功
			} else if (jsonArray.length() != 0 && jsonArray2.length() != 0) {

				temp = 3;
				// 激活部分成功
			} else if (jsonArray.length() == 0 && jsonArray2.length() != 0) {

				temp = 3;

			} else {

				temp = 1;

			}

		} else {

			temp = 1;

		}
		return temp;
	}

	private AsyncHttpResponseHandler uploadResultHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultStr = new String(responseBody);

			Log.i("First", resultStr);

			loadingDialog.dismiss();

			if (resultStr != null && resultStr.length() > 0) {

				try {

					JSONObject tempObject = new JSONObject(resultStr);

					String tempCode = tempObject.getString("code");

					String tempError = tempObject.getString("error");

					if (!tempCode.isEmpty() && !tempError.isEmpty() && tempCode.equals("200")
							&& tempError.equalsIgnoreCase("success")) {

						initView();

					} else {// 查询失败

						ToastShow.ToastShowShort(mContext, "" + tempError);

						finish();

					}

				} catch (JSONException e) {

					e.printStackTrace();

					ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

					finish();

				}
			}

		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
			loadingDialog.dismiss();

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

			finish();

		}


	};

	protected void findViewById() {

		title_back_image = (ImageButton) findViewById(R.id.img_bt_activite_result_back);

		btn_continue = (Button) findViewById(R.id.bt_activity_active_result_continue);

		btn_miss = (Button) findViewById(R.id.bt_activity_active_result_miss);

		tv_activeResult = (TextView) findViewById(R.id.tv_activite_result_show);

		tv_orderNo = (TextView) findViewById(R.id.tv_activite_result_orderno);

		tv_orderPrice = (TextView) findViewById(R.id.tv_activite_result_order_price);

		tv_orderMemo = (TextView) findViewById(R.id.tv_activite_result_ordermemo);

		tv_orderDetail = (TextView) findViewById(R.id.tv_activite_result_order_detail);

		tv_active_sucess = (TextView) findViewById(R.id.tv_activite_result_active_sucess);

		tv_order_fail = (TextView) findViewById(R.id.tv_activite_result_active_fail);

		mContext = ActiveResultActivity.this;

		db = new MyDBOperation(mContext);

	}

	protected void initView() throws JSONException {

		// 登录成功
		if (!code.isEmpty() && !error.isEmpty() && code.equals("200") && error.equalsIgnoreCase("success")) {

			tv_activeResult.setText("激活成功");

			JSONArray jsonArray = new JSONArray(result);

			JSONArray jsonArray2 = new JSONArray(failure);

			tv_active_sucess.setText(getResultStr(jsonArray, 0));

			tv_order_fail.setText(getResultStr(jsonArray2, 1));

		} else {

			tv_activeResult.setText(error + ",相关记录可在【我的订单】中查看");

			if (!(result.equals("") || result == null) || (failure.equals("") || failure == null)) {

				JSONArray jsonArray = new JSONArray(result);

				JSONArray jsonArray2 = new JSONArray(failure);

				tv_active_sucess.setText(getResultStr(jsonArray, 0));

				tv_order_fail.setText(getResultStr(jsonArray2, 1));

			}

		}

		tv_orderNo.setText(orderId);

		tv_orderPrice.setText("" + payMoney);

		tv_orderMemo.setText(memo);

		String[] str = codes.split(",");

		String oderListShow = "";

		for (int i = 0; i < str.length; i++) {

			if (i == str.length - 1) {

				oderListShow += str[i] + "\n";

			} else {

				oderListShow += str[i] + "\n\n";

			}

		}

		tv_orderDetail.setText(oderListShow);

	}

	private String getResultStr(JSONArray jsonArray, int from) throws JSONException {

		String resultCode = "";

		for (int i = 0; i < jsonArray.length(); i++) {

			if (i == jsonArray.length() - 1) {

				resultCode += "\n" + jsonArray.getJSONObject(i).getString("code");

				if (from == 1) {
					resultCode += "\t" + jsonArray.getJSONObject(i).getString("statusname");
				}

			} else {

				resultCode += "\n\n" + jsonArray.getJSONObject(i).getString("code");

				if (from == 1) {

					resultCode += "\t" + jsonArray.getJSONObject(i).getString("statusname");

				}

			}

		}

		return resultCode;

	}

	public void setListener() {

		title_back_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 底部按钮的点击事件
		btn_miss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(mContext, ShowMainActivity.class);

				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);

				finish();
			}
		});

		btn_continue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

	}

}