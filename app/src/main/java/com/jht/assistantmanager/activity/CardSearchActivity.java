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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.ToastShow;

public class CardSearchActivity extends Activity {

	private Button bt_submit;

	private EditText et_card_number;

	private TextView tv_card_input_type;

	private TextView tv_card_title;

	private String cardNumber;

	private ImageButton img_bt_camera;

	private ImageButton img_bt_return;

	private int search_type;

	private Intent intent;

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_card_search);

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

		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {

					cardNumber = et_card_number.getText().toString();

					if (cardNumber.equals("")) {

						ToastShow.ToastShowShort(mContext, "券卡号不能为空!");

					} else if (cardNumber.length() != 16) {

						ToastShow.ToastShowShort(mContext, "请输入正确的16位编码！");

					} else {

						if (NetWorkJudgeUtil.isConnect(mContext)) {

							openActivity(cardNumber);

						} else {

							ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态!");

						}
					}

				} catch (Exception e) {

					ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试!");

				}
			}
		});

		tv_card_input_type.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ToastShow.ToastShowShort(getApplicationContext(), "请查看券卡背面卡号");
			}
		});

		img_bt_camera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				intent.setClass(CardSearchActivity.this, CaptureActivity.class);

				intent.putExtra("type", search_type);

				startActivity(intent);
			}
		});

		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	protected void openActivity(String cardNumber) {

		if (search_type == 0) {

			setBundleAndStartActivity(CardSearchResultActivity.class);

		} else {

			setBundleAndStartActivity(CardGiveActivity.class);

		}

	}

	private void setBundleAndStartActivity(Class<?> aimClass) {

		intent.putExtra("cardNumber", cardNumber);

		intent.setClass(mContext, aimClass);

		startActivity(intent);

	}

	private void initView() {

		search_type = getIntent().getIntExtra("type", 0);

		intent = new Intent();

		mContext = CardSearchActivity.this;

		if (search_type == 0) {

			tv_card_title.setText("券卡查询");

		} else {

			tv_card_title.setText("券卡核销");
		}

	}

	private void findView() {

		et_card_number = (EditText) findViewById(R.id.et_card_search_number);

		tv_card_input_type = (TextView) findViewById(R.id.tv_card_search_type_change);

		img_bt_camera = (ImageButton) findViewById(R.id.img_bt_card_search_camera);

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_card_search_return);

		bt_submit = (Button) findViewById(R.id.bt_card_search_submit);

		tv_card_title = (TextView) findViewById(R.id.tv_card_search_title);

	}

}
