package com.jht.assistantmanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.AppManager;
import com.jht.assistantmanager.model.User;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.rotate.SweetAlertDialog;

public class PersonInfoActivity extends Activity {

	private TextView tv_user_count;

	private TextView tv_user_name;

	private TextView tv_sex;

	private TextView tv_phone;

	private RelativeLayout rlyout_change_password;

	private TextView tv_email;

	private TextView tv_address;

	private Button tb_login_out;

	private ImageButton img_bt_return;

	private int from;

	private MyDBOperation db;

	private Context mContext;

	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		AppManager.getInstance().addActivity(this);
		judgeFrom();

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_person_detail);

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

	private void judgeFrom() {
		from = getIntent().getIntExtra("from", 0);
		if (from == 1) {
			finish();
		}
	}

	private void setListen() {

		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		rlyout_change_password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PersonInfoActivity.this, ChangePasswordActivity.class);
				startActivity(intent);
			}
		});

		tb_login_out.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getLoginShow();

			}

		});
	}

	private void initView() {

		user = db.getLocalUserInfo();

		tv_user_count.setText(user.getUsername());

		tv_user_name.setText(user.getName());

		tv_phone.setText(user.getMphone());

		tv_email.setText(user.getEmail());

		tv_address.setText(user.getFulladdr());

		if (user.getSex() == 0) {

			tv_sex.setText("男");

		} else {

			tv_sex.setText("女");

		}

	}

	private void findView() {

		tv_user_count = (TextView) findViewById(R.id.tv_person_detail_count);

		tv_user_name = (TextView) findViewById(R.id.tv_person_detail_user_name);

		tv_sex = (TextView) findViewById(R.id.tv_person_detail_sex);

		tv_phone = (TextView) findViewById(R.id.tv_person_detail_phone);

		tv_email = (TextView) findViewById(R.id.tv_person_detail_email);

		tv_address = (TextView) findViewById(R.id.tv_person_detail_address);

		rlyout_change_password = (RelativeLayout) findViewById(R.id.rl_person_detail_change_password);

		tb_login_out = (Button) findViewById(R.id.bt_person_detail_login_out);

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_person_detail_return);

		mContext = PersonInfoActivity.this;

		db = new MyDBOperation(mContext);

	}

	public void getExit() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(PersonInfoActivity.this);

		dialog.setTitle("退出当前账号").setMessage("是否确定退出当前账户？")

				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent i = new Intent(PersonInfoActivity.this, PersonInfoActivity.class);

						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

						i.putExtra("from", 1);

						startActivity(i);

					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();// 取消弹出框
					}
				}).create().show();
	}

	public void getLoginShow() {

		new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE).setTitleText("提 示").setContentText("是否确定退出当前账号？")
				.setCancelText("取 消").setConfirmText("确 定").showCancelButton(true)
				.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sDialog) {
						// reuse previous dialog instance, keep widget user
						// state, reset them if you need

						sDialog.dismiss();
					}
				}).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
					@Override
					public void onClick(SweetAlertDialog sDialog) {

						AppManager.getInstance().killAllActivity();

						AppManager.getInstance().AppExit(getApplicationContext());
						sDialog.dismiss();

						Intent intent = new Intent();
						intent.setClass(PersonInfoActivity.this, LoginActivity.class);
						startActivity(intent);
						PersonInfoActivity.this.finish();
					}
				}).show();

		// new
		// AlertDialogView(PersonInfoActivity.this).builder().setTitle("退出当前账号").setMsg("是否确定退出券卡微助理？")
		// .setPositiveButton("确认退出", new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		//
		// AppManager.getInstance().killAllActivity();
		// AppManager.getInstance().AppExit(getApplicationContext());
		//
		// Intent intent=new Intent();
		// intent.setClass(PersonInfoActivity.this, LoginActivity.class);
		// startActivity(intent);
		// PersonInfoActivity.this.finish();
		//
		//
		//
		// }
		// }).setNegativeButton("取消", new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		//
		// }
		// }).show();

	}

}
