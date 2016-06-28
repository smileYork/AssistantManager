package com.jht.assistantmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.ToastShow;

public class CheckMoneyActivity extends Activity {

	private ImageButton img_bt_return;

	private RelativeLayout rl_cash;

	private RelativeLayout rl_alipay;

	private RelativeLayout rl_wechat;

	private TextView tv_order_id;

	private TextView tv_order_detail;

	private TextView tv_order_price;

	private EditText tv_memo;

	private String orderId;

	private String orderDetail;

	private Double OrderPrice;

	private String memo;

	private Intent intent;

	private Context mContext;

	private Intent intentForFore;

	private String uuid;

	private MyDBOperation db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_check_money);

		try {

			findView();

			initView();

			setListen();

		} catch (Exception e) {

			e.printStackTrace();

			ToastShow.ToastShowShort(mContext, "网络异常，请稍后再试");

			finish();
		}

	}

	private void initView() {

		orderId = intentForFore.getStringExtra("orderid");

		orderDetail = intentForFore.getStringExtra("codes");

		OrderPrice = intentForFore.getDoubleExtra("money", 0);

		memo = intentForFore.getStringExtra("memo");

		uuid = intentForFore.getStringExtra("uuid");

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

		tv_memo.setText(memo);

	}

	private void setListen() {

		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		rl_cash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openActivity(0);

			}
		});

		rl_alipay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openActivity(1);

			}
		});

		rl_wechat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openActivity(2);

			}
		});

	}

	protected void openActivity(int pay_type) {

		memo = tv_memo.getText().toString();

		if (pay_type == 0) {// 现金支付

			intent.setClass(mContext, GestureVerifyActivity.class);

			intent.putExtra("orderid", orderId);

			intent.putExtra("money", OrderPrice);

			intent.putExtra("codes", orderDetail);

			intent.putExtra("memo", memo);

			intent.putExtra("uuid", uuid);

		} else if (pay_type == 1) {// 支付宝支付

			intent.setClass(mContext, WebViewShowActivity.class);

			String subject = "券卡激活";

			String URL = GlobalData.aly + "out_trade_no=" + orderId + "&subject=" + subject + "&total_fee=" + 0.01
					+ "&body=" + orderDetail + "&khdm=" + db.getUserInfo("khdm");

			intent.putExtra("url", URL);

			intent.putExtra("orderid", orderId);

			intent.putExtra("codes", orderDetail);

			intent.putExtra("uuid", uuid);

			intent.putExtra("memo", memo);

			intent.putExtra("title", "支付宝支付");

		} else if (pay_type == 2) {// 微信支付

			intent.setClass(mContext, WebViewShowActivity.class);

			String URL = GlobalData.wx1 + "body=券卡激活&total_fee=" + 1 + "&out_trade_no=" + orderId
					+ "&sync_url=dd&async_url=" + GlobalData.async_url + "&visittype=1&shakecode="
					+ db.getUserInfo("wxjoincode") + "&checkcode=" + db.getUserInfo("wxcheckcode") + "&khdm="
					+ db.getUserInfo("khdm") + "";

			intent.putExtra("url", URL);

			intent.putExtra("orderid", orderId);

			intent.putExtra("uuid", uuid);

			intent.putExtra("memo", memo);

			intent.putExtra("title", "微信支付");

		}

		startActivity(intent);

		finish();

	}

	private void findView() {

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_check_money_return);

		rl_cash = (RelativeLayout) findViewById(R.id.rlyout_check_money_cash);

		rl_alipay = (RelativeLayout) findViewById(R.id.rlyout_check_money_alipay);

		rl_wechat = (RelativeLayout) findViewById(R.id.rlyout_check_money_wechat);

		tv_order_id = (TextView) findViewById(R.id.tv_activity_order_oderno);

		tv_order_detail = (TextView) findViewById(R.id.tv_activity_order_oderdetail);

		tv_order_price = (TextView) findViewById(R.id.tv_activity_order_oderprice);

		tv_memo = (EditText) findViewById(R.id.dt_activity_order_memo);

		intent = new Intent();

		intentForFore = getIntent();

		mContext = CheckMoneyActivity.this;

		db = new MyDBOperation(mContext);

	}

}