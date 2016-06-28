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
import android.widget.ImageView;
import android.widget.TextView;
import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.BadgeView;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.ToastShow;

public class CardSaleSearchActivity extends Activity {

	private Button bt_submit;

	private EditText et_card_number;

	private TextView tv_card_input_type;

	private String cardNumber;

	private ImageButton img_bt_camera;

	private ImageButton img_bt_return;

	private ImageView img_car;

	private Intent intent;

	private BadgeView badge;

	private Context mContext;

	private MyDBOperation db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_card_sale_search);

		findView();

		setTitleStyle();

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

					} else {// 输入券卡号非空

						if (NetWorkJudgeUtil.isConnect(mContext)) {

							intent.setClass(CardSaleSearchActivity.this, CardSaleActivity.class);

							intent.putExtra("cardNumber", cardNumber);

							startActivity(intent);

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

				intent.setClass(CardSaleSearchActivity.this, CaptureActivity.class);

				intent.putExtra("type", 2);

				startActivity(intent);

			}
		});

		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

		img_car.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				intent.setClass(mContext, CardSaleListActivity.class);

				startActivity(intent);

			}
		});
	}

	private void initView() {

		if (db.getSaleItemNumber() != 0) {

			badge.setText("" + db.getSaleItemNumber());

			badge.setTextSize(10);

			badge.show();

		} else {

			badge.setVisibility(View.GONE);

		}
	}

	private void findView() {

		et_card_number = (EditText) findViewById(R.id.et_card_sale_search_number);

		tv_card_input_type = (TextView) findViewById(R.id.tv_card_sale_search_type_change);

		img_bt_camera = (ImageButton) findViewById(R.id.img_bt_card_sale_search_camera);

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_card_sale_search_return);

		bt_submit = (Button) findViewById(R.id.bt_card_sale_search_submit);

		img_car = (ImageView) findViewById(R.id.img_card_sale_search_car);

		intent = new Intent();

		badge = new BadgeView(this, img_car);

		mContext = CardSaleSearchActivity.this;

		db = new MyDBOperation(mContext);

	}

	@Override
	protected void onResume() {

		super.onResume();

		initView();
	}

}
