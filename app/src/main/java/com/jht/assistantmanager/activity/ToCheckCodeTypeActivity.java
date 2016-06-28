package com.jht.assistantmanager.activity;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

public class ToCheckCodeTypeActivity extends Activity {

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

	private Dialog dialog;

	private Context mContext;

	private JSONObject bojectTemp;

	private String codesProblem = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		try {

			getInfo();

			CheckiSWXPay();

		} catch (Exception e) {

			e.printStackTrace();

			ToastShow.ToastShowShort(mContext, "网络故障,请稍后再试");
		}

	}

	// 微信支付结果查询接口
	private void CheckiSWXPay() {

		dialog = LoadingDialog.createLoadingDialog(mContext, "正在检查是否已经微信支付,请稍等...");

		dialog.show();

		RequestParams params = new RequestParams();

		params.put("out_trade_no", orderId);

		params.put("shakecode", db.getUserInfo("wxjoincode"));

		params.put("checkcode", db.getUserInfo("wxcheckcode"));

		AsyncHttpClient client = new AsyncHttpClient();

		client.setTimeout(12000);

		client.post(GlobalData.wxqueryOrder, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

				dialog.dismiss();

				String resultall = new String(responseBody);

				Log.i("First", "wxqueryOrder = " + resultall);

				if (resultall != null && resultall.length() > 0) {

					JSONObject allresult;

					String result_code = "";

					try {

						allresult = new JSONObject(resultall);

						String return_code = allresult.getString("return_code");

						if (return_code.contains("SUCCESS")) {

							result_code = allresult.getString("result_code");

							if (result_code.contains("SUCCESS")) {

								String trade_state = allresult.getString("trade_state");

								// 订单支付成功
								if (trade_state.contains("SUCCESS")) {

									ToastShow.ToastShowShort(mContext, "该订单已微信支付，正在激活。。。。");

									openActivity(0);

								} else {// 该订单未支付

									getalipaySearchFre();

								}

							} else {// 查无此订单

								getalipaySearchFre();

							}

						} else {// 调用微信支付接口失败

							ToastShow.ToastShowShort(mContext, "微信支付接口未开通，请稍后再试....");

							finish();

						}

					} catch (Exception e) {

						System.out.println("解析json异常");

						ToastShow.ToastShowShort(mContext, "微信支付接口未开通，请稍后再试....");

						finish();

					}

				} else {

					ToastShow.ToastShowShort(mContext, "网络异常，请稍后再试....");

					finish();

				}

			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

				dialog.dismiss();

				ToastShow.ToastShowShort(mContext, "网络异常，请稍后再试....");

				finish();

			}

		});

	}

	// 支付宝支付结果查询接口
	public void getalipaySearch(String sign, String orderno) {

		AsyncHttpClient client = new AsyncHttpClient();

		client.setTimeout(12000);

		RequestParams params = new RequestParams();

		params.put("out_trade_no", orderno);

		params.put("sign", sign);

		params.put("_input_charset", "utf-8");

		params.put("sign_type", "RSA");

		params.put("service", "single_trade_query");

		params.put("partner", db.getUserInfo("partner"));

		client.post("https://mapi.alipay.com/gateway.do?_input_charset=utf-8", params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				dialog.dismiss();

				String resultall = new String(responseBody);

				if (resultall.contains("TRADE_SUCCESS")) {

					ToastShow.ToastShowShort(mContext, "该订单支付宝已支付，正在激活。。。。");

					openActivity(0);

				} else {

					checkCode();

				}

			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				ToastShow.ToastShowShort(mContext, "网络异常，请稍后再试....");

				finish();
			}

		});
	}

	public void getalipaySearchFre() {

		dialog = LoadingDialog.createLoadingDialog(mContext, "正在检查是否已经支付宝支付,请稍等...");

		dialog.show();

		AsyncHttpClient client = new AsyncHttpClient();

		client.setTimeout(12000);

		RequestParams params = new RequestParams();

		params.put("out_trade_no", orderId);

		params.put("khdm", db.getUserInfo("khdm"));

		client.post(GlobalData.alipaySearch, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				String resultall = new String(responseBody);

				// 获取到签名
				getalipaySearch(resultall, orderId);


			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

				ToastShow.ToastShowShort(mContext, "网络异常，请稍后再试....");

				finish();


			}

		});
	}

	private void checkCode() {

		try {

			dialog = LoadingDialog.createLoadingDialog(mContext, "正在验证券卡号是否全部有效,请稍等...");

			dialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfo();

				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), "utf-8");

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Card_Search_Card_Url, entity,
						GlobalData.ContentType, searchHttpResponseHandler);

			} else {

				dialog.dismiss();

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

		object.put("method", "querycard");

		object.put("codes", codes);

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

					// 检测到支付成功
					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						result = object.getString("result");

						failure = object.getString("failure");

						if (JudgeCheckResult(result, failure)) {

							ToastShow.ToastShowShort(mContext, "该订单存在问题券卡，已失效，请重新下单");

							uploadOrderType();

						} else {// 检查支付宝支付结果

							openActivity(1);

						}

					} else {// 查询失败

						error = object.getString("error");

						if (object.toString().contains("failure")) {

							failure = object.getString("failure");

							JSONArray jsonArrayF = new JSONArray(failure);

							if (jsonArrayF.length() != 0) {

								ToastShow.ToastShowShort(mContext, "该订单存在问题券卡，已失效，请重新下单");

								uploadOrderType();

							} else {

								ToastShow.ToastShowShort(mContext, error);

								finish();

							}

						}

						ToastShow.ToastShowShort(mContext, error);

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
			dialog.dismiss();

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

			finish();
		}


	};

	protected boolean JudgeCheckResult(String result2, String failure2) throws JSONException {

		JSONArray jsonArrayR = new JSONArray(result2);

		JSONArray jsonArrayF = new JSONArray(failure2);

		JSONObject tempObject;

		for (int j = 0; j < jsonArrayR.length(); j++) {

			tempObject = jsonArrayR.getJSONObject(j);

			if (tempObject.getInt("state") != 0) {

				if (j == jsonArrayR.length() - 1) {

					codesProblem += tempObject.getString("code") + "(" + tempObject.getString("statusname") + ")";

				} else {

					codesProblem += tempObject.getString("code") + "(" + tempObject.getString("statusname") + "),";

				}

			}

		}

		if (jsonArrayF.length() != 0) {

			for (int i = 0; i < jsonArrayF.length(); i++) {

				tempObject = jsonArrayF.getJSONObject(i);

				if (i == jsonArrayF.length() - 1) {

					codesProblem += tempObject.getString("code") + tempObject.getString("statusname");

				} else {

					codesProblem += tempObject.getString("code") + tempObject.getString("statusname") + ",";

				}

			}

		}

		return !codesProblem.equals("");

	}

	protected void openActivity(int from) {

		Intent intent = new Intent();

		if (from == 0) {

			intent.setClass(mContext, ActiveActivity.class);

		}

		else {

			intent.setClass(mContext, CheckMoneyActivity.class);

		}

		intent.putExtra("orderid", orderId);

		intent.putExtra("codes", codes);

		intent.putExtra("money", payMoney);

		intent.putExtra("memo", memo);

		intent.putExtra("uuid", uuid);

		startActivity(intent);

		finish();

	}

	private void uploadOrderType() {

		try {

			dialog.setTitle("正在更新订单信息,请稍等...");

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestForUpload();

				// 设定编码格式
				StringEntity entity = new StringEntity(bojectTemp.toString(), "utf-8");

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.My_Order_Url, entity,
						GlobalData.ContentType, uploadResultHttpResponseHandler);

			} else {

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

				finish();

			}
		} catch (Exception e) {

			dialog.dismiss();

			e.printStackTrace();

		}

	}

	private void initRequestForUpload() throws JSONException {

		bojectTemp = new JSONObject();

		bojectTemp.put("excdescribe", codesProblem);

		bojectTemp.put("orderid", orderId);

		bojectTemp.put("orderstate", 4);

		bojectTemp.put("uuid", uuid);

		bojectTemp.put("memo", memo);

		bojectTemp.put("usercode", db.getFlowCode());

		bojectTemp.put("usertype", 1);

		bojectTemp.put("method", "updateapporderstate");

		bojectTemp.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(bojectTemp, db.getMd5Key());

		bojectTemp.put("sign", sign);

	}

	private AsyncHttpResponseHandler uploadResultHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultStr = new String(responseBody);

			Log.i("First", resultStr);

			dialog.dismiss();

			if (resultStr != null && resultStr.length() > 0) {

				try {

					JSONObject tempObject = new JSONObject(resultStr);

					String tempCode = tempObject.getString("code");

					String tempError = tempObject.getString("error");

					if (!tempCode.isEmpty() && !tempError.isEmpty() && tempCode.equals("200")
							&& tempError.equalsIgnoreCase("success")) {

						ToastShow.ToastShowShort(mContext, "修改订单状态成功");

						finish();

					} else {// 上传失败

						ToastShow.ToastShowShort(mContext, "修改订单状态失败，请稍后再试------");

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
			dialog.dismiss();

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

			finish();


		}

	};

	private void getInfo() {

		mContext = ToCheckCodeTypeActivity.this;

		db = new MyDBOperation(mContext);

		orderId = getIntent().getStringExtra("orderid");

		payMoney = getIntent().getDoubleExtra("money", 0);

		codes = getIntent().getStringExtra("codes");

		memo = getIntent().getStringExtra("memo");

		if (memo == null) {

			memo = "";
		}

		uuid = getIntent().getStringExtra("uuid");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return super.onKeyDown(keyCode, event);
	}

}
