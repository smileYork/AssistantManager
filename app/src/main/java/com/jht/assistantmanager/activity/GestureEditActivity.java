package com.jht.assistantmanager.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.util.ActivityManage;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.view.GestureContentView;
import com.jht.assistantmanager.view.GestureDrawline;
import com.jht.assistantmanager.view.LockIndicator;

public class GestureEditActivity extends Activity implements OnClickListener {

	public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";

	public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";

	public static final String PARAM_IS_FIRST_ADVICE = "PARAM_IS_FIRST_ADVICE";

	private TextView mTextCancel;

	private LockIndicator mLockIndicator;

	private TextView mTextTip;

	private FrameLayout mGestureContainer;

	private GestureContentView mGestureContentView;

	private TextView mTextReset;

	private boolean mIsFirstInput = true;

	private String mFirstPassword = null;

	private SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_gesture_edit);

//		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
//		// 透明状态栏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//		// 透明导航栏
//		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//	}
		initView();

		setUpViews();

		setUpListeners();

	}

	private void initView() {

		preferences = getSharedPreferences(GlobalData.SHAREDPREFERENCES_NAME, MODE_PRIVATE);

	}

	private void setUpViews() {

		mTextCancel = (TextView) findViewById(R.id.text_cancel);

		mTextReset = (TextView) findViewById(R.id.text_reset);

		mTextReset.setClickable(false);

		mLockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);

		mTextTip = (TextView) findViewById(R.id.text_tip);

		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);

		mGestureContentView = new GestureContentView(this, false, "", new GestureDrawline.GestureCallBack() {

			@Override
			public void onGestureCodeInput(String inputCode) {

				if (!isInputPassValidate(inputCode)) {

					mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>至少连接四个点，请重新输入！</font>"));

					mGestureContentView.clearDrawlineState(0L);

					return;
				}
				if (mIsFirstInput) {

					mTextTip.setText("请再次绘制解锁图案");

					mFirstPassword = inputCode;

					updateCodeList(inputCode);

					mGestureContentView.clearDrawlineState(0L);

					mTextReset.setClickable(true);

					mTextReset.setText(getString(R.string.reset_gesture_code));

				} else {

					if (inputCode.equals(mFirstPassword)) {

						Toast.makeText(GestureEditActivity.this, "密码绘制成功!", Toast.LENGTH_SHORT).show();

						mGestureContentView.clearDrawlineState(0L);

						SharedPreferences.Editor ed = preferences.edit();

						ed.putString("photoPassword", PasswordUtil.getMd5ByString(mFirstPassword));

						ed.putBoolean("isSetPhotoPassword", true);

						ed.commit();

						ActivityManage.startActivity(GestureEditActivity.this, ShowMainActivity.class);

						finish();

					} else {

						mTextTip.setText(Html.fromHtml("<font color='#c70c1e'>与上次密码输入不一致，请重新输入！</font>"));

						Animation shakeAnimation = AnimationUtils.loadAnimation(GestureEditActivity.this, R.anim.shake);

						mTextTip.startAnimation(shakeAnimation);

						mGestureContentView.clearDrawlineState(1300L);

					}
				}
				mIsFirstInput = false;
			}

			@Override
			public void checkedSuccess() {

			}

			@Override
			public void checkedFail() {

			}
		});

		mGestureContentView.setParentView(mGestureContainer);

		updateCodeList("");
	}

	private void setUpListeners() {

		mTextCancel.setOnClickListener(this);

		mTextReset.setOnClickListener(this);

	}

	private void updateCodeList(String inputCode) {

		mLockIndicator.setPath(inputCode);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.text_cancel:

			this.finish();

			break;

		case R.id.text_reset:

			mIsFirstInput = true;

			updateCodeList("");

			mTextTip.setText(getString(R.string.set_gesture_pattern));

			break;

		default:
			break;
		}
	}

	private boolean isInputPassValidate(String inputPassword) {

		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {

			return false;

		}

		return true;
	}

}
