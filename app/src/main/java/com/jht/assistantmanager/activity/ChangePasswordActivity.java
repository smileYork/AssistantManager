package com.jht.assistantmanager.activity;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class ChangePasswordActivity extends Activity {

	private ImageButton img_bt_return;

	private EditText et_old_password;

	private EditText et_new_password;

	private EditText et_new_again_password;

	private Button bt_submit;

	private Button bt_clear;

	private String oldPassword;

	private String newPassword;

	private String newPasswordSure;

	private Context mContext;

	private Dialog dialog;

	private JSONObject object;

	private MyDBOperation db;

	private String code;

	private String error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_change_password);

		setTitleStyle();

		findView();

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

				oldPassword = et_old_password.getText().toString();

				newPassword = et_new_password.getText().toString();

				newPasswordSure = et_new_again_password.getText().toString();

				if (oldPassword.equals("")) {

					ToastShow.ToastShowShort(mContext, "原密码不能为空");

				} else if (newPassword.equals("")) {

					ToastShow.ToastShowShort(mContext, "新密码不能为空");

				} else if (!newPassword.equals(newPasswordSure)) {

					ToastShow.ToastShowShort(mContext, "新密码两次输入不一致");

				} else if (newPassword.length() < 6) {

					ToastShow.ToastShowShort(mContext, "新密码长度至少为6");

				} else {

					try {
						if (NetWorkJudgeUtil.isConnect(mContext)) {

							dialog = LoadingDialog.createLoadingDialog(mContext, "正在修改密码,请稍等...");

							dialog.show();

							// 设置请求数据

							initChangePwdInfo();

							// 设定编码格式
							StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

							// 设置监听回调接口
							GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Login_Url, entity,
									GlobalData.ContentType, ChangePasswordResultHttpResponseHandler);

						} else {

							ToastShow.ToastShowLong(mContext, "网络访问失败，请检查手机网络!");

							dialog.dismiss();

						}

					} catch (Exception e) {

						e.printStackTrace();

					}

				}

			}
		});

		bt_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				et_old_password.setText("");

				et_new_password.setText("");

				et_new_again_password.setText("");

			}
		});

		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	protected void initChangePwdInfo() throws JSONException {

		object = new JSONObject();

		object.put("username", db.getUserName());

		object.put("usertype", 1);

		object.put("method", "updatepwd");

		object.put("oldpwd", PasswordUtil.getMD5Str(oldPassword));

		object.put("userpwd", PasswordUtil.getMD5Str(newPassword));

		object.put("timestamp", System.currentTimeMillis());

	}

	private AsyncHttpResponseHandler ChangePasswordResultHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultStr = new String(responseBody);

			dialog.dismiss();

			if (resultStr != null && resultStr.length() > 0) {

				try {

					object = new JSONObject(resultStr);

					code = object.getString("code");

					error = object.getString("error");

					// 登录成功
					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						dialog.dismiss();

						ToastShow.ToastShowShort(mContext, "密码修改成功,请重新登录！");

						Intent i = new Intent(mContext, LoginActivity.class);

						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

						startActivity(i);

						finish();

					} else {// 修改失败

						dialog.dismiss();

						ToastShow.ToastShowShort(mContext, "" + error);

					}

				} catch (JSONException e) {

					e.printStackTrace();

					dialog.dismiss();

					ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

				}

			}

		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

			dialog.dismiss();

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

		}

	};

	private void findView() {

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_change_password_return);

		et_old_password = (EditText) findViewById(R.id.et_change_password_oringin);

		et_new_password = (EditText) findViewById(R.id.et_change_password_new);

		et_new_again_password = (EditText) findViewById(R.id.et_change_password_again);

		bt_submit = (Button) findViewById(R.id.bt_change_password_submit);

		bt_clear = (Button) findViewById(R.id.bt_change_password_clear);

		mContext = ChangePasswordActivity.this;

		db = new MyDBOperation(mContext);
	}

}