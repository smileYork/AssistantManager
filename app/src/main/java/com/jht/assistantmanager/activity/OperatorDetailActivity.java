package com.jht.assistantmanager.activity;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jht.assistantmanager.R;

public class OperatorDetailActivity extends Activity {

	private ImageButton img_bt_return;

	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_operator_detail);

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

	private void findView() {

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_title_bar_return);

		tv_title = (TextView) findViewById(R.id.tv_title_bar_title);
	}

	private void initView() {

		tv_title.setText("操作详情");

	}

	private void setListen() {
		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});
	}
}