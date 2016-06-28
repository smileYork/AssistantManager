package com.jht.assistantmanager.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.util.GlobalData;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {

	boolean isFirstIn = false;

	private static final int GO_HOME = 1;

	private static final int GO_GUIDE = 0;

	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 2500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		getWindow().setBackgroundDrawableResource(R.drawable.first_sow);

		// 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
		if (isFirstLoad()) {

			mMainHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);

		} else {

			mMainHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);

		}

	}

	private boolean isFirstLoad() {

		SharedPreferences preferences = getSharedPreferences(GlobalData.SHAREDPREFERENCES_NAME, MODE_PRIVATE);

		return preferences.getBoolean("isFirstLoad", true);

	}

	private Handler mMainHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			Intent intent = new Intent();

			switch (msg.what) {

			case GO_GUIDE:
				// 第一次设定默认的主题方式

				intent.setClass(getApplication(), GuideActivity.class);

				break;

			case GO_HOME:

				intent.setClass(getApplication(), LoginActivity.class);

				break;

			}

			startActivity(intent);

			finish();
		}
	};

}
