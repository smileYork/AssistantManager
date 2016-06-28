package com.jht.assistantmanager.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.adapter.time.ScreenInfo;
import com.jht.assistantmanager.adapter.time.WheelMain;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class OrderManageActivity extends Activity {

	private RelativeLayout rlyout_not_send;

	private RelativeLayout rlyout_have_send;

	private RelativeLayout rlyout_all_order;

	private ImageButton img_bt_left;

	private Button bt_set_date;

	private Button bt_searh;

	private EditText et_input_order;

	private int year;

	private Dialog lodingDialog;

	private Context mContext;

	private MyDBOperation db;

	private String code;

	private String error;

	private JSONObject object;

	private TextView tv_nosend;

	private TextView tv_havesend;

	private TextView tv_allorder;

	private TextView tv_fnosend;

	private TextView tv_fhavesend;

	private TextView tv_fallorder;

	private TextView tv_snosend;

	private TextView tv_shavesend;

	private TextView tv_sallorder;

	private TextView tv_tnosend;

	private TextView tv_thavesend;

	private TextView tv_tallorder;

	private TextView tv_fournosend;

	private TextView tv_fourhavesend;

	private TextView tv_fourallorder;

	WheelMain wheelMain;

	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_order_manage);

		findView();

		initView();

		getOrderManagerrInfo();

		setListen();

	}

	private void getOrderManagerrInfo() {

		try {

			lodingDialog = LoadingDialog.createLoadingDialog(mContext, "正在加载数据,请稍等...");

			lodingDialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfo();

				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Orderall, entity, GlobalData.ContentType,
						searchHttpResponseHandler);

			} else {

				lodingDialog.dismiss();

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

			}

		} catch (Exception e) {

			lodingDialog.dismiss();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");

		}

	}

	private void initRequestInfo() throws JSONException {

		object = new JSONObject();

		object.put("usercode", db.getFlowCode());

		object.put("usertype", 1);

		object.put("year", year);

		object.put("method", "getordermanagerinfo");

		object.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(object, db.getMd5Key());

		object.put("sign", sign);

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

						reSetView(object);

						lodingDialog.dismiss();

					} else {// 查询失败

						lodingDialog.dismiss();

						ToastShow.ToastShowShort(mContext, "" + error);

						finish();
					}

				} catch (JSONException e) {

					e.printStackTrace();

					lodingDialog.dismiss();

					ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

				}

			} else {

				lodingDialog.dismiss();

				ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

			}


		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

			lodingDialog.dismiss();

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");


		}

	};

	private void initView() {

		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);

		Date mydate = new Date(); // 获取当前日期Date对象

		mycalendar.setTime(mydate);//// 为Calendar对象设置时间为当前日期

		year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年

		bt_set_date.setText("" + year);
		db = new MyDBOperation(mContext);

	}

	protected void reSetView(JSONObject resultObject) throws JSONException {

		tv_nosend.setText("" + resultObject.getString("waitsend"));

		tv_havesend.setText("" + resultObject.getString("havesend"));

		tv_allorder.setText("" + resultObject.getString("haveover"));

		tv_fnosend.setText("" + resultObject.getString("fnosend"));

		tv_fhavesend.setText("" + resultObject.getString("fhaveover"));

		tv_fallorder.setText("" + resultObject.getString("fallorder"));

		tv_snosend.setText("" + resultObject.getString("snosend"));

		tv_shavesend.setText("" + resultObject.getString("shaveover"));

		tv_sallorder.setText("" + resultObject.getString("sallorder"));

		tv_tnosend.setText("" + resultObject.getString("tnosend"));

		tv_thavesend.setText("" + resultObject.getString("thaveover"));

		tv_tallorder.setText("" + resultObject.getString("tallorder"));

		tv_fournosend.setText("" + resultObject.getString("fournosend"));

		tv_fourhavesend.setText("" + resultObject.getString("fourhaveover"));

		tv_fourallorder.setText("" + resultObject.getString("fourallorder"));

	}

	private void findView() {

		img_bt_left = (ImageButton) findViewById(R.id.img_bt_order_manage_return);

		rlyout_not_send = (RelativeLayout) findViewById(R.id.rlyout_order_manage_not_send);

		rlyout_have_send = (RelativeLayout) findViewById(R.id.rlyout_order_manage_have_send);

		rlyout_all_order = (RelativeLayout) findViewById(R.id.rlyout_order_manage_all_order);

		bt_set_date = (Button) findViewById(R.id.bt_order_manage_set_date);

		et_input_order = (EditText) findViewById(R.id.et_order_manage_search_order);

		bt_searh = (Button) findViewById(R.id.bt_order_manage_search);

		tv_nosend = (TextView) findViewById(R.id.tv_order_manager_nosend);

		tv_havesend = (TextView) findViewById(R.id.tv_order_manager_havesend);

		tv_allorder = (TextView) findViewById(R.id.tv_order_manager_allorder);

		tv_fnosend = (TextView) findViewById(R.id.tv_order_manage_fnosend);

		tv_fhavesend = (TextView) findViewById(R.id.tv_order_manage_fhavesend);

		tv_fallorder = (TextView) findViewById(R.id.tv_order_manage_fallorder);

		tv_snosend = (TextView) findViewById(R.id.tv_order_manage_snosend);

		tv_shavesend = (TextView) findViewById(R.id.tv_order_manage_shavesend);

		tv_sallorder = (TextView) findViewById(R.id.tv_order_manage_sallorder);

		tv_tnosend = (TextView) findViewById(R.id.tv_order_manage_tnosend);

		tv_thavesend = (TextView) findViewById(R.id.tv_order_manage_thavesend);

		tv_tallorder = (TextView) findViewById(R.id.tv_order_manage_tallorder);

		tv_fournosend = (TextView) findViewById(R.id.tv_order_manage_fournosend);

		tv_fourhavesend = (TextView) findViewById(R.id.tv_order_manage_fourhavesend);

		tv_fourallorder = (TextView) findViewById(R.id.tv_order_manage_fourallorder);

		mContext = OrderManageActivity.this;

	}

	private void setListen() {

		img_bt_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bt_set_date.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ShowYearPickerDialog();

			}
		});

		bt_searh.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String cardNumber = et_input_order.getText().toString();

				if (cardNumber.equals("")) {

					ToastShow.ToastShowShort(mContext, "订单号不能为空");

				} else {

					lodingDialog = LoadingDialog.createLoadingDialog(OrderManageActivity.this, "加载数据中,请稍等...");

					lodingDialog.show();

					Intent intent = new Intent();

					intent.setClass(OrderManageActivity.this, OrderManagerDetailActivity.class);

					intent.putExtra("orderNumber", et_input_order.getText().toString());

					startActivity(intent);

					lodingDialog.dismiss();

				}
			}
		});

		rlyout_not_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();

				intent.setClass(OrderManageActivity.this, OrderManagerListActivity.class);

				intent.putExtra("type", 0);

				startActivity(intent);
			}
		});

		rlyout_have_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				intent.setClass(OrderManageActivity.this, OrderManagerListActivity.class);

				intent.putExtra("type", 1);

				startActivity(intent);
			}
		});

		rlyout_all_order.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				intent.setClass(OrderManageActivity.this, OrderManagerListActivity.class);

				intent.putExtra("type", 2);

				startActivity(intent);
			}
		});
	}

	public void ShowYearPickerDialog() {

		LayoutInflater inflater = LayoutInflater.from(OrderManageActivity.this);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(OrderManageActivity.this);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		long l = System.currentTimeMillis();
		// new日期对象
		Date date = new Date(l);
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date);

		int year1 = calendar1.get(Calendar.YEAR);
		int month1 = calendar1.get(Calendar.MONTH);
		int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year1, month1, day1);
		new AlertDialog.Builder(OrderManageActivity.this).setTitle("请选择年份").setView(timepickerview)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						year = Integer.valueOf(wheelMain.getTime());

						bt_set_date.setText(wheelMain.getTime());

						Log.i("First", "DatePickerDialog");

						getOrderManagerrInfo();

					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();

	}

}
