package com.jht.assistantmanager.activity;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.util.AlterDialog;

public class GiveCashActivity extends Activity {

	private ImageButton img_bt_return;

	private TextView tv_cash_number;

	private Button bt_click;

	private Context mContext;

	private Intent intent;

	private String orderId;

	private double payMoney;

	private String codes;

	private String memo;

	private String uuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_give_cash);

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

				AlterDialog.alterDailog(mContext, "提示", "您有未完成订单，确定返回?订单可以在【我的订单】里查看",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								dialog.cancel();

								finish();

							}
						}, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								dialog.cancel();

							}
						}).setCancelable(false).create().show();

			}
		});

		bt_click.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlterDialog.alterDailogForCash(mContext, "警告", "请确认已收到相应现金，激活的券卡将不能被撤销",

						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface suredialog, int which) {

								suredialog.cancel();

								Intent intent = new Intent(mContext, ActiveActivity.class);

								intent.putExtra("orderid", orderId);

								intent.putExtra("codes", codes);

								intent.putExtra("money", payMoney);

								intent.putExtra("memo", memo);

								intent.putExtra("uuid", uuid);

								mContext.startActivity(intent);

								finish();
							}

						}, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {

								dialog.cancel();

							}
						}).create().show();
			}

		});

	}

	protected void openActivity(Class<?> aimClass) {

		intent.setClass(mContext, aimClass);

		startActivity(intent);

	}

	private void initView() {

		tv_cash_number.setText("" + getIntent().getDoubleExtra("money", 0) + "元");

	}

	private void findView() {

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_give_cash_return);

		tv_cash_number = (TextView) findViewById(R.id.tv_cash_number);

		bt_click = (Button) findViewById(R.id.bt_cash_give_click);

		mContext = GiveCashActivity.this;

		intent = new Intent();

		orderId = getIntent().getStringExtra("orderid");

		payMoney = getIntent().getDoubleExtra("money", 0);

		codes = getIntent().getStringExtra("codes");

		memo = getIntent().getStringExtra("memo");

		uuid = getIntent().getStringExtra("uuid");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		AlterDialog.alterDailog(mContext, "提示", "您有未完成订单，确定返回?订单可以在【我的订单】里查看", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();

				finish();

			}
		}, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();

			}
		}).setCancelable(false).create().show();

		return false;
	}

}
