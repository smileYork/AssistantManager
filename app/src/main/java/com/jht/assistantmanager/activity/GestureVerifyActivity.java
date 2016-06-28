package com.jht.assistantmanager.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.ToastShow;
import com.jht.assistantmanager.view.GestureContentView;
import com.jht.assistantmanager.view.GestureDrawline;

public class GestureVerifyActivity extends Activity {

	public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";

	public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";

	private TextView mTextTip;

	private FrameLayout mGestureContainer;

	private GestureContentView mGestureContentView;

	private SharedPreferences preferences;

	private int failNumber;

	private SharedPreferences.Editor ed;

	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_gesture_verify);

		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		ObtainExtraData();

		setUpViews();
	}

	private void ObtainExtraData() {

		preferences = getSharedPreferences(GlobalData.SHAREDPREFERENCES_NAME, MODE_PRIVATE);

		ed = preferences.edit();

		mContext = GestureVerifyActivity.this;

	}

	private void setUpViews() {

		mTextTip = (TextView) findViewById(R.id.text_tip);

		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);

		mGestureContentView = new GestureContentView(this, true, preferences.getString("photoPassword", ""),
				new GestureDrawline.GestureCallBack() {

					@Override
					public void onGestureCodeInput(String inputCode) {

					}

					@Override
					public void checkedSuccess() {

						ed.putInt("GestureFailNumber", 0);

						ed.commit();

						mGestureContentView.clearDrawlineState(0L);

						Toast.makeText(GestureVerifyActivity.this, "验证成功", Toast.LENGTH_SHORT).show();

						Intent intent = new Intent();

						intent.setClass(GestureVerifyActivity.this, GiveCashActivity.class);

						intent.putExtra("orderid", getIntent().getStringExtra("orderid"));

						intent.putExtra("money", getIntent().getDoubleExtra("money", 0));

						intent.putExtra("codes", getIntent().getStringExtra("codes"));

						intent.putExtra("memo", getIntent().getStringExtra("memo"));

						intent.putExtra("uuid", getIntent().getStringExtra("uuid"));

						startActivity(intent);

						finish();

					}

					@Override
					public void checkedFail() {

						failNumber = preferences.getInt("GestureFailNumber", 0);

						if (failNumber < 4) {

							mGestureContentView.clearDrawlineState(1300L);

							mTextTip.setVisibility(View.VISIBLE);

							mTextTip.setText(
									Html.fromHtml("<font color='#c70c1e'>验证失败,还有 " + (4 - failNumber) + " 次机会</font>"));

							Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this,

									R.anim.shake);

							mTextTip.startAnimation(shakeAnimation);

						} else {

							ToastShow.ToastShowLong(GestureVerifyActivity.this, "失败次数已超过五次，请稍后再试");

							ed.putLong("GestureFailTime", System.currentTimeMillis());

							finish();
						}

						failNumber++;

						ed.putInt("GestureFailNumber", failNumber);

						ed.commit();

					}
				});

		mGestureContentView.setParentView(mGestureContainer);
	}

}
