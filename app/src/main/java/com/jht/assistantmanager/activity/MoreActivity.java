package com.jht.assistantmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.AppManager;
import com.jht.assistantmanager.model.User;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.rotate.SweetAlertDialog;

public class MoreActivity extends Activity {

	private RelativeLayout rlyout_person;

	private RelativeLayout rlyout_order_manage;

	private RelativeLayout rlyout_help;

	private RelativeLayout rlyout_common_setting, rlyout_more_exit;

	private ImageButton img_bt_left;

	private Intent intent;

	private TextView tv_person_name;

	private MyDBOperation db;

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		AppManager.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_more);

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

	private void initView() {

		intent = new Intent();
		User user = db.getLocalUserInfo();
		if (null != user.getName()) {
			tv_person_name.setText(user.getName());
		} else {
			tv_person_name.setText(user.getUsername());
		}

	}

	private void findView() {
		AppManager.getInstance().addActivity(this);
		img_bt_left = (ImageButton) findViewById(R.id.img_bt_more_return);

		rlyout_person = (RelativeLayout) findViewById(R.id.rlyout_more_person_info);

		rlyout_order_manage = (RelativeLayout) findViewById(R.id.rlyout_more_order_manage);

		rlyout_help = (RelativeLayout) findViewById(R.id.rlyout_more_help);

		tv_person_name = (TextView) findViewById(R.id.tv_more_person_name);

		rlyout_common_setting = (RelativeLayout) findViewById(R.id.rlyout_more_common_setting);
		rlyout_more_exit = (RelativeLayout) findViewById(R.id.rlyout_more_exit);

		mContext = MoreActivity.this;

		db = new MyDBOperation(mContext);

	}

	private void setListen() {

		img_bt_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		rlyout_person.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				intent.setClass(MoreActivity.this, PersonInfoActivity.class);

				intent.putExtra("from", 0);

				startActivity(intent);

			}
		});

		rlyout_order_manage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.setClass(MoreActivity.this, OrderManageActivity.class);
				startActivity(intent);
			}
		});

		rlyout_help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.setClass(MoreActivity.this, AboutAndHelpActivity.class);
				startActivity(intent);
			}
		});
		rlyout_common_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.setClass(MoreActivity.this, CommonSettingActivity.class);
				startActivity(intent);
			}
		});

		rlyout_more_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE).setTitleText("退出应用")
						.setContentText("是否确定退出券卡微助理？").setCancelText("取 消").setConfirmText("确 定")
						.showCancelButton(true).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {
								// reuse previous dialog instance, keep widget
								// user state, reset them if you need

								sDialog.dismiss();
							}
						}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
							@Override
							public void onClick(SweetAlertDialog sDialog) {

								AppManager.getInstance().killAllActivity();

								AppManager.getInstance().AppExit(getApplicationContext());
								sDialog.dismiss();
							}
						}).show();

				// new
				// AlertDialogView(MoreActivity.this).builder().setTitle("退出当前账号").setMsg("是否确定退出券卡微助理？")
				// .setPositiveButton("确认退出", new OnClickListener() {
				// @Override
				// public void onClick(View v) {
				//
				// AppManager.getInstance().killAllActivity();
				//
				// AppManager.getInstance().AppExit(getApplicationContext());
				//
				// }
				// }).setNegativeButton("取消", new OnClickListener() {
				// @Override
				// public void onClick(View v) {
				//
				// }
				// }).show();
			}
		});
	}

}