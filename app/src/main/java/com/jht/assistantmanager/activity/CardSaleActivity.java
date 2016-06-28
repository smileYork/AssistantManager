package com.jht.assistantmanager.activity;

import java.util.Iterator;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.BadgeView;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.model.CardForSearch;
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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CardSaleActivity extends Activity {

	private ImageButton img_bt_return;

	private TextView tv_number;

	private TextView tv_type;

	private TextView tv_price;

	private TextView tv_combine;

	private TextView tv_small_price;

	private TextView tv_time;

	private TextView tv_alterinfo;

	private TextView tv_timeChangeName;

	private Button bt_click;

	private String cardNumber;

	private Context mContext;

	private Dialog dialog;

	private MyDBOperation db;

	private ImageView img_car;

	private BadgeView badge;

	private String code;

	private String error;

	private String result;

	private String failure;

	private JSONObject object;

	private CardForSearch card;

	private boolean isCanSale = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_card_sale);

		setTitleStyle();

		findView();

		getCardInfo();

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

		img_car.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openActivity();

				finish();

			}
		});

		bt_click.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!isCanSale) {

					finish();

				}

				else if (card != null) {

					if (db.findSaleItemByCardId("" + card.getCode())) {

						ToastShow.ToastShowShort(mContext, "该券卡已在待销售列表,请勿重复添加！");

						openActivity();

						finish();

					} else {

						if (db.insertTempSaleItem(card) > 0) {

							ToastShow.ToastShowShort(mContext, "添加券卡到待销售列表成功");

							setBadge();

							openActivity();

							finish();

						} else {

							ToastShow.ToastShowShort(mContext, "添加券卡到待销售列表失败,请稍后再试");

						}
					}

				} else {

					ToastShow.ToastShowShort(mContext, "添加失败，请重新操作");

				}

			}
		});
	}

	protected void openActivity() {

		Intent intent = new Intent();

		intent.setClass(CardSaleActivity.this, CardSaleListActivity.class);

		startActivity(intent);

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

						judgeType(result);

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

	protected void judgeType(String result2) throws JSONException {

		JSONObject objectTemp = new JSONObject(result2);

		if (objectTemp.getInt("state") != 0) {

			tv_alterinfo.setVisibility(View.VISIBLE);

			tv_alterinfo.setText("该券卡不能销售!");

			isCanSale = false;

		}

		initView(objectTemp);

		dialog.dismiss();

	}

	protected void updateInsertTime() {

		if (db.getSaleItemNumber() == 1) {

			SharedPreferences preferences = getSharedPreferences(GlobalData.SHAREDPREFERENCES_NAME, MODE_PRIVATE);

			SharedPreferences.Editor sd = preferences.edit();

			sd.putLong("createOrderTime", System.currentTimeMillis());

			sd.commit();

		}
	}

	private void setBadge() {

		if (db.getSaleItemNumber() != 0) {

			badge.setText("" + db.getSaleItemNumber());

			badge.setTextSize(10);

			badge.show();
		}

	}

	private void initView(JSONObject objectTemp) throws JSONException {

		setBadge();

		String packageName;

		card = new CardForSearch();

		card.setCode(objectTemp.getString("code"));

		card.setActdate(objectTemp.getString("actdate"));

		card.setCardprice(objectTemp.getDouble("cardprice"));

		card.setCardsmallprice(objectTemp.getDouble("cardsmallprice"));

		card.setSelnum(objectTemp.getInt("selnum"));

		tv_number.setText("" + objectTemp.getString("code"));

		tv_price.setText("" + objectTemp.getDouble("cardprice"));

		tv_time.setText(objectTemp.getString("actdate"));

		tv_timeChangeName.setText(AppUtil.getTypeTime(objectTemp.getInt("state")));

		if (!isCanSale) {

			tv_type.setTextColor(Color.RED);

			bt_click.setText("返回");

		}

		tv_type.setText(objectTemp.getString("statusname"));

		tv_small_price.setText("" + objectTemp.getDouble("cardsmallprice"));

		JSONObject tempObject = objectTemp.getJSONObject("defaultgoods");

		Iterator<?> it = tempObject.keys();

		if (it.hasNext()) {

			packageName = "" + objectTemp.getInt("selnum") + "种默认套餐";

			tv_combine.setText(packageName);

			card.setStatusname(packageName);

		} else {

			if (objectTemp.toString().contains("goodslist")) {

				JSONArray tempArray = objectTemp.getJSONArray("goodslist");

				packageName = tempArray.length() + "选" + objectTemp.getInt("selnum") + "套餐";

				tv_combine.setText(packageName);

				card.setStatusname(packageName);

			} else {

				ToastShow.ToastShowShort(mContext, "没有可选套餐");

				finish();

			}

		}

	}

	private void findView() {

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_card_sale_return);

		tv_number = (TextView) findViewById(R.id.tv_card_sale_number);

		tv_type = (TextView) findViewById(R.id.tv_card_sale_type);

		tv_price = (TextView) findViewById(R.id.tv_card_sale_price);

		tv_combine = (TextView) findViewById(R.id.tv_card_sale_combine);

		tv_small_price = (TextView) findViewById(R.id.tv_card_sale_samll_price);

		tv_alterinfo = (TextView) findViewById(R.id.activity_card_sale_alterinfo);

		tv_timeChangeName = (TextView) findViewById(R.id.activity_card_sale_time_change_name);

		tv_time = (TextView) findViewById(R.id.tv_card_sale_time);

		bt_click = (Button) findViewById(R.id.bt_card_sale_submit);

		img_car = (ImageView) findViewById(R.id.img_card_sale_search_result_car);

		mContext = CardSaleActivity.this;

		badge = new BadgeView(mContext, img_car);

		db = new MyDBOperation(mContext);

		cardNumber = getIntent().getStringExtra("cardNumber");

		if (!AppUtil.judgeCardNumber(cardNumber)) {

			ToastShow.ToastShowShort(mContext, "编码格式错误");

			finish();
		}

		cardNumber = cardNumber.substring(cardNumber.length() - 16, cardNumber.length());
	}

}