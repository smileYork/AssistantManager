package com.jht.assistantmanager.activity;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.ListForSaleAdapter;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.model.CardForSearch;
import com.jht.assistantmanager.util.ActivityManage;
import com.jht.assistantmanager.util.AppUtil;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.jht.assistantmanager.util.rotate.SweetAlertDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CardSaleListActivity extends Activity {

	private ListView lv_sales;

	private List<CardForSearch> list_order;

	private ListForSaleAdapter listAdapet;

	private ImageButton img_bt_return;

	private Button bt_add;

	private Button bt_check;

	private int delete_position;

	private TextView tv_card_number;

	private TextView tv_money;

	private double sumMoney = 0;

	private ImageView img_delete;

	private MyDBOperation db;

	private Context mContext;

	private SharedPreferences preferences;

	private Dialog dialog;

	private String code;

	private String error;

	private String result;

	private String failure;

	private String orderId;

	private String uuid;

	private JSONObject object;

	private JSONObject objectForUpload;

	private List<CardForSearch> failtureCodeList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_card_sale_list);

		setTitleStyle();

		findView();

		initView();

		setListen();
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

		bt_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bt_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (list_order.size() == 0) {

					ToastShow.ToastShowShort(mContext, "没有待销售的券卡，请先去添加券卡");

				} else {

					try {

						// 失败时间在5分钟之内，并且失败次数超过5次
						if ((judegeGestureFailTime() < 5) && (judegeGestureFailNumber() >= 5)) {

							ToastShow.ToastShowShort(mContext,
									"图案解锁错误超过5次，请过" + (5 - judegeGestureFailTime()) + "分钟后再试");

						} // 失败次数超过五次，并且已经过了5分钟
						else if ((judegeGestureFailTime() > 5) && (judegeGestureFailNumber() >= 5)) {

							// 验证失败次数清零
							SharedPreferences.Editor ed = preferences.edit();

							ed.putInt("GestureFailNumber", 0);

							ed.commit();

							ActivityManage.startActivity(mContext, GestureVerifyActivity.class);

						} // 本次销售已经过了15分钟
						else if (judgeOperateTime() > 15) {

							ToastShow.ToastShowShort(mContext, "销售操作时间已经超过15分钟，将清空待销售列表，请重新添加");

							db.deleteAllLocalSaleItem();

							finish();

						} else {

							CheckAllCodes();

						}

					} catch (Exception e) {

						ToastShow.ToastShowShort(mContext, "操作失败，请稍后再试");

						e.printStackTrace();
					}
				}

			}

		});

		lv_sales.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				delete_position = position;

				new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE).setTitleText("提 示")
						.setContentText(" 确定删除 " + list_order.get(delete_position).getCode() + " 这张券卡吗? ")
						.setCancelText("取 消").setConfirmText("确 定").showCancelButton(true)
						.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.setTitleText("删除失败").setContentText("您已取消删除！").setConfirmText("确定")
										.showCancelButton(false).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

											@Override
											public void onClick(SweetAlertDialog sweetAlertDialog) {
												sweetAlertDialog.dismiss();
											}
										}).changeAlertType(SweetAlertDialog.ERROR_TYPE);

							}
						}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								db.deleteSaleItemFromLocalList(list_order.get(delete_position).getCode());

								sumMoney -= list_order.get(delete_position).getCardsmallprice();

								list_order.remove(delete_position);

								listAdapet.notifyDataSetChanged();

								tv_card_number.setText("" + list_order.size());

								tv_money.setText("" + sumMoney);

								sDialog.setTitleText("删除成功").setContentText("成功删除并解除锁定一张券卡").showCancelButton(false)
										.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

											@Override
											public void onClick(SweetAlertDialog sweetAlertDialog) {
												sweetAlertDialog.dismiss();
											}
										}).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

							}
						}).show();

				return false;
			}
		});

		img_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE).setTitleText("提 示")
						.setContentText(" 确定删除全部卡券吗? ").setCancelText("取 消").setConfirmText("确 定")
						.showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								sDialog.setTitleText("删除失败").setContentText("您已取消删除！").setConfirmText("确定")
										.showCancelButton(false).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

											@Override
											public void onClick(SweetAlertDialog sweetAlertDialog) {
												sweetAlertDialog.dismiss();
											}
										}).changeAlertType(SweetAlertDialog.ERROR_TYPE);

							}
						}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								db.deleteAllLocalSaleItem();

								list_order.clear();

								listAdapet.notifyDataSetChanged();

								tv_card_number.setText("" + list_order.size());

								tv_money.setText("" + 0);

								sDialog.setTitleText("删除成功").setContentText("券卡已全部删除！").showCancelButton(false)
										.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

											@Override
											public void onClick(SweetAlertDialog sweetAlertDialog) {
												sweetAlertDialog.dismiss();
											}
										}).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

							}
						}).show();

			}

		});

	}

	// 预支付
	protected void CheckAllCodes() {

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

		String codes = "";

		for (int i = 0; i < list_order.size(); i++) {

			if (i == list_order.size() - 1) {

				codes += list_order.get(i).getCode();

			} else {

				codes += list_order.get(i).getCode() + ",";

			}

		}

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

					// 登录成功
					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						result = object.getString("result");

						failure = object.getString("failure");

						if (!JudgeCheckResult(result, failure)) {
							new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE).setTitleText("验证失败!")
									.setContentText("请删除问题券卡( 列表中已标记红色 )")
									.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

										@Override
										public void onClick(SweetAlertDialog sweetAlertDialog) {

											sweetAlertDialog.dismiss();
										}
									}).show();

							listAdapet.notifyDataSetChanged();

						} else {
							new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE).setTitleText("验证通过!")
									.setContentText("确定结算这些券卡？").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

								@Override
								public void onClick(SweetAlertDialog sweetAlertDialog) {
									uploadOrderInfo();
									sweetAlertDialog.dismiss();
								}
							}).show();

						}

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

	// 判断从添加第一个券卡起是否已经超过15分钟
	protected long judgeOperateTime() {

		return (System.currentTimeMillis() - preferences.getLong("createOrderTime", System.currentTimeMillis()))
				/ (1000 * 60);
	}

	protected void uploadOrderInfo() {
		try {

			dialog = LoadingDialog.createLoadingDialog(mContext, "正在上传订单信息,请稍等...");

			dialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfoForUpload();

				// 设定编码格式
				StringEntity entity = new StringEntity(objectForUpload.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.My_Order_Url, entity,
						GlobalData.ContentType, uploadHttpResponseHandler);

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

	private void initRequestInfoForUpload() throws JSONException {

		objectForUpload = new JSONObject();

		JSONObject bojectTemp = new JSONObject();

		bojectTemp.put("codelist", db.getOrderCodeList());

		bojectTemp.put("excdescribe", "");

		bojectTemp.put("handlerstate", -1);

		orderId = AppUtil.generateOrderId(db.getUserInfo("khdm"));

		uuid = AppUtil.getUUID();

		bojectTemp.put("orderid", orderId);

		bojectTemp.put("paydescribe", "");

		bojectTemp.put("orderstate", 0);

		bojectTemp.put("price", db.getSaleListSumMoney());

		bojectTemp.put("uuid", uuid);

		objectForUpload.put("usercode", db.getFlowCode());

		objectForUpload.put("usertype", 1);

		objectForUpload.put("method", "insertapporder");

		objectForUpload.put("jsondate", bojectTemp);

		objectForUpload.put("timestamp", System.currentTimeMillis());

		objectForUpload.put("memo", "");

		String sign = PasswordUtil.generateSign(objectForUpload, db.getMd5Key());

		objectForUpload.put("sign", sign);

	}

	private AsyncHttpResponseHandler uploadHttpResponseHandler = new AsyncHttpResponseHandler() {

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

						Intent intent = new Intent(mContext, CheckMoneyActivity.class);

						intent.putExtra("orderid", orderId);

						intent.putExtra("codes", db.getOrderCodeList());

						intent.putExtra("money", db.getSaleListSumMoney());

						intent.putExtra("memo", "");

						intent.putExtra("uuid", uuid);

						db.deleteAllLocalSaleItem();

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

	protected boolean JudgeCheckResult(String result2, String failure2) throws JSONException {

		JSONArray jsonArrayR = new JSONArray(result2);

		JSONArray jsonArrayF = new JSONArray(failure2);

		JSONObject tempObject;

		CardForSearch tempCard;

		failtureCodeList.clear();

		for (int j = 0; j < jsonArrayR.length(); j++) {

			tempObject = jsonArrayR.getJSONObject(j);

			if (tempObject.getInt("state") != 0) {

				tempCard = new CardForSearch();

				tempCard.setCode(tempObject.getString("code"));

				tempCard.setStatusname(tempObject.getString("statusname"));

				failtureCodeList.add(tempCard);

			}

		}

		for (int i = 0; i < jsonArrayF.length(); i++) {

			tempCard = new CardForSearch();

			tempObject = jsonArrayR.getJSONObject(i);

			tempCard.setCode(tempObject.getString("code"));

			tempCard.setStatusname(tempObject.getString("statusname"));

			failtureCodeList.add(tempCard);

		}

		return failtureCodeList.size() == 0;

	}

	private long judegeGestureFailTime() {

		return (System.currentTimeMillis() - preferences.getLong("GestureFailTime", System.currentTimeMillis()))
				/ (1000 * 60);
	}

	private int judegeGestureFailNumber() {

		return preferences.getInt("GestureFailNumber", 0);
	}

	private void initView() {

		list_order = db.getLocalCardForSaleList();

		failtureCodeList = new ArrayList<CardForSearch>();

		listAdapet = new ListForSaleAdapter(mContext, list_order, failtureCodeList);

		lv_sales.setAdapter(listAdapet);

		listAdapet.notifyDataSetChanged();

		tv_card_number.setText("" + list_order.size());

		for (int i = 0; i < list_order.size(); i++) {

			sumMoney += list_order.get(i).getCardsmallprice();

		}

		tv_money.setText("" + sumMoney);

	}

	private void findView() {

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_card_sale_list_return);

		bt_add = (Button) findViewById(R.id.bt_card_sale_list_add);

		bt_check = (Button) findViewById(R.id.bt_card_sale_list_cehck);

		lv_sales = (ListView) findViewById(R.id.lv_card_sale_list);

		tv_card_number = (TextView) findViewById(R.id.tv_card_sale_list_number);

		tv_money = (TextView) findViewById(R.id.tv_card_sale_list_price);

		img_delete = (ImageView) findViewById(R.id.img_card_sale_search_delete);

		mContext = CardSaleListActivity.this;

		db = new MyDBOperation(mContext);

		preferences = getSharedPreferences(GlobalData.SHAREDPREFERENCES_NAME, MODE_PRIVATE);

	}
}
