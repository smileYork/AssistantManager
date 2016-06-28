package com.jht.assistantmanager.activity;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

public class ActiveActivity extends Activity {

	private MyDBOperation db;

	private String code;

	private String error;

	private String result = "";

	private String failure = "";

	private JSONObject object;

	private String orderId;

	private double payMoney;

	private String codes;

	private String memo;

	private String uuid;

	private Dialog loadingDialog;

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		getInfo();

		activeCode();

	}

	private void activeCode() {

		try {

			loadingDialog = LoadingDialog.createLoadingDialog(mContext, "正在激活所有券卡,请稍等...");

			loadingDialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestForjihuo();

				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Card_Search_Card_Url, entity,
						GlobalData.ContentType, activeResultHttpResponseHandler);

			} else {

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

				finish();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void initRequestForjihuo() throws JSONException {

		object = new JSONObject();

		object.put("usercode", db.getFlowCode());

		object.put("usertype", 1);

		object.put("method", "activecard");

		object.put("uid", AppUtil.getUUID());

		object.put("codes", codes);

		object.put("payprice", "" + payMoney);

		object.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(object, db.getMd5Key());

		object.put("sign", sign);

		Log.i("First", "initRequestForjihuo = " + object.toString());

	}

	private AsyncHttpResponseHandler activeResultHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultStr = new String(responseBody);

			Log.i("First", "activeResultHttpResponseHandler" + resultStr);

			loadingDialog.dismiss();

			if (resultStr != null && resultStr.length() > 0) {

				try {

					object = new JSONObject(resultStr);

					code = object.getString("code");

					error = object.getString("error");

					if (object.toString().contains("result") && object.toString().contains("failure")) {

						result = object.getString("result");

						failure = object.getString("failure");

					}

					Intent intent = new Intent(mContext, ActiveResultActivity.class);

					intent.putExtra("code", code);

					intent.putExtra("error", error);

					intent.putExtra("result", result);

					intent.putExtra("failure", failure);

					intent.putExtra("codes", codes);

					intent.putExtra("orderid", orderId);

					intent.putExtra("payMoney", payMoney);

					intent.putExtra("memo", memo);

					intent.putExtra("uuid", uuid);

					startActivity(intent);

					finish();

				} catch (JSONException e) {

					e.printStackTrace();

					loadingDialog.dismiss();

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

	private void getInfo() {

		mContext = ActiveActivity.this;

		db = new MyDBOperation(mContext);

		orderId = getIntent().getStringExtra("orderid");

		payMoney = getIntent().getDoubleExtra("money", 0);

		codes = getIntent().getStringExtra("codes");

		memo = getIntent().getStringExtra("memo");

		uuid = getIntent().getStringExtra("uuid");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return super.onKeyDown(keyCode, event);
	}

}
