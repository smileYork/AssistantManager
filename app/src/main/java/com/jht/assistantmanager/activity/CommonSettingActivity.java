package com.jht.assistantmanager.activity;


import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.AppManager;
import com.jht.assistantmanager.util.ActivityManage;
import com.jht.assistantmanager.util.AlterDialog;

public class CommonSettingActivity extends Activity {

	private Builder builder;

	private TextView tv_pay_type;

	private TextView tv_input_type;

	private TextView tv_theme_type;

	private ImageButton img_bt_return;

	private TextView tv_title;

	private SharedPreferences sharePreference;

	private SharedPreferences.Editor sp_edit;

	private RelativeLayout rlyout_pay_type;

	private RelativeLayout rlyout_input_type;

	private RelativeLayout rlyout_theme_type;

	private int input_type;

	private int pay_type;

	private int theme_type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_common_setting);

		// 沉浸式状态栏
//		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//		// 透明状态栏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		// 透明导航栏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//	}

		findView();

		initView();

		setListen();

	}

	private void setListen() {

		rlyout_pay_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				builder = AlterDialog.singleDailog(CommonSettingActivity.this, "选择支付方式", "扫码支付", "面对面支付",
						pay_type_selectListen, pay_type);

				builder.show();
			}
		});

		rlyout_input_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder = AlterDialog.singleDailog(CommonSettingActivity.this, "选择券码输入方式", "扫码输入", "手动输入",
						input_type_selectListen, input_type);

				builder.show();
			}
		});

		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		rlyout_theme_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				builder = AlterDialog.singleDailog(CommonSettingActivity.this, "主题选择", "简洁", "现代", theme_type_selectListen,
						theme_type);

				builder.show();
			}
		});

	}

	final DialogInterface.OnClickListener input_type_selectListen = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {

			input_type = which;

			if (which == 0) {

				tv_input_type.setText("扫码输入");

			} else {

				tv_input_type.setText("手动输入");

			}

			sp_edit.putInt("input_type", which);

			sp_edit.commit();

			dialog.dismiss();
		}
	};

	final DialogInterface.OnClickListener pay_type_selectListen = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {

			pay_type = which;

			if (which == 0) {

				tv_pay_type.setText("扫码支付");

			} else {

				tv_pay_type.setText("面对面支付");

			}

			sp_edit.putInt("pay_type", which);

			sp_edit.commit();

			dialog.dismiss();
			
			

		}

	};

	final DialogInterface.OnClickListener theme_type_selectListen = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {

			theme_type = which;

			if (which == 0) {

				tv_theme_type.setText("简洁");

			} else {

				tv_theme_type.setText("现代");

			}

			sp_edit.putInt("theme_type", which);

			sp_edit.commit();

			dialog.dismiss();
			AppManager.getInstance().killAllActivity();

			AppManager.getInstance().AppExit(getApplicationContext()); 
			
			if (theme_type == 1) {

				ActivityManage.startActivity(CommonSettingActivity.this, TestUIActivity.class);

			} else {

				ActivityManage.startActivity(CommonSettingActivity.this, ShowMainActivity.class);

			}
			
			
			
		}
	};

	private void initView() {

		sharePreference = getSharedPreferences("system_setting", Activity.MODE_PRIVATE);

		sp_edit = sharePreference.edit();

		input_type = sharePreference.getInt("input_type", 0);

		pay_type = sharePreference.getInt("pay_type", 0);

		theme_type = sharePreference.getInt("theme_type", 0);

		if (pay_type == 0) {
			tv_pay_type.setText("扫码支付");
		} else {
			tv_pay_type.setText("面对面支付");
		}

		if (input_type == 0) {
			tv_input_type.setText("扫码输入");
		} else {
			tv_input_type.setText("手动输入");
		}

		if (theme_type == 0) {
			tv_theme_type.setText("简洁");
		} else {
			tv_theme_type.setText("现代");
		}

		tv_title.setText("通用");
	}

	private void findView() {
		 AppManager.getInstance().addActivity(this);
		rlyout_pay_type = (RelativeLayout) findViewById(R.id.rlyout_more_pay_type);

		rlyout_input_type = (RelativeLayout) findViewById(R.id.rlyout_more_input_type);

		tv_pay_type = (TextView) findViewById(R.id.tv_more_pay_type);

		tv_input_type = (TextView) findViewById(R.id.tv_more_input_type);

		tv_theme_type = (TextView) findViewById(R.id.tv_common_setting_theme);

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_title_bar_return);

		tv_title = (TextView) findViewById(R.id.tv_title_bar_title);

		rlyout_theme_type = (RelativeLayout) findViewById(R.id.rlyout_common_setting_theme);
		
		
		rlyout_pay_type.setVisibility(View.GONE);

	}

}
