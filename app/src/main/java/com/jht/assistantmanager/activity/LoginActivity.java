package com.jht.assistantmanager.activity;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.model.User;
import com.jht.assistantmanager.model.Wxpayinfo;
import com.jht.assistantmanager.util.ActivityManage;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	private Button bt_submit;

	private EditText et_userName;

	private EditText et_password;

	private TextView tv_forget_password;

	private String userName;

	private String password;

	private Context mContext;

	private Dialog dialog;

	private SharedPreferences preferences;

	private User user;

	private MyDBOperation db;

	private String code;

	private String error;

	private String result;

	private String wxpayinfo;

	private JSONObject object;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_login_in);

		try {

			setTitleStyle();

			findView();

			initView();

			setListen();

		} catch (Exception e) {

			e.printStackTrace();

			ToastShow.ToastShowShort(mContext, "网络异常，请稍后再试");

		}

	}

	private void setTitleStyle() {

		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		}

	}

	private void initView() {

		mContext = LoginActivity.this;

		preferences = getSharedPreferences(GlobalData.SHAREDPREFERENCES_NAME, MODE_PRIVATE);

		et_userName.setText(preferences.getString("userName", ""));

		db = new MyDBOperation(mContext);

	}

	private void setListen() {

		bt_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				try {

					dialog = LoadingDialog.createLoadingDialog(mContext, "正在验证,请稍等...");

					dialog.show();

					userName = et_userName.getText().toString();

					password = et_password.getText().toString();

					if (userName.equals("") || password.equals("")) {

						ToastShow.ToastShowShort(mContext, "用户名或者密码不能为空！");

					} else {

						if (NetWorkJudgeUtil.getNetworkState(mContext) != NetWorkJudgeUtil.NONE) {

							// 设置请求数据
							initLoginInfo();

							// 设定编码格式
							StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

							// 设置监听回调接口
							GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Login_Url, entity,
									GlobalData.ContentType, loginResultHttpResponseHandler);

						} else {

							ToastShow.ToastShowLong(mContext, "网络访问失败，请检查手机网络!");

							dialog.dismiss();

						}
					}

				} catch (Exception e) {

					dialog.dismiss();

					ToastShow.ToastShowLong(mContext, "系统繁忙，请稍后再试!");

					e.printStackTrace();

				}

			}
		});

		tv_forget_password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ToastShow.ToastShowShort(getApplicationContext(), "请联系店长重置密码");

			}
		});

	}

	protected void initLoginInfo() throws JSONException {

		object = new JSONObject();

		object.put("username", userName);

		object.put("userpwd", PasswordUtil.getMD5Str(password));

		object.put("usertype", 1);

		object.put("method", "login");

		object.put("timestamp", System.currentTimeMillis());

	}

	private AsyncHttpResponseHandler loginResultHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultStr = new String(responseBody);

			if (resultStr != null && resultStr.length() > 0) {

				try {

					object = new JSONObject(resultStr);

					code = object.getString("code");

					error = object.getString("error");
					// 登录成功
					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {
						// 获取并更新本地数据
						getInfoAndSetLocalInfo(object);
						// 设置下一个跳转界面
						jumpActivity();

						dialog.dismiss();

						finish();

					} else {// 登录失败

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
			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

			dialog.dismiss();
		}
	};

	private void setLocalInfo(String name) {

		SharedPreferences.Editor sd = preferences.edit();

		sd.putString("userName", name);

		sd.commit();

	}

	protected void jumpActivity() {

		if (userName.equals(preferences.getString("userName", ""))) {

			// 用户已经设置了图案密码
			if (preferences.getBoolean("isSetPhotoPassword", false)) {

				int theme_type = preferences.getInt("theme_type", 0);
				if (theme_type == 1) {

					ActivityManage.startActivity(mContext, TestUIActivity.class);

				} else {

					ActivityManage.startActivity(mContext, ShowMainActivity.class);

				}

			} else {

				ActivityManage.startActivity(mContext, GestureEditActivity.class);

			}
		} else {// 新用户登录

			setLocalInfo(userName);

			ActivityManage.startActivity(mContext, GestureEditActivity.class);

		}

	}

	protected void getInfoAndSetLocalInfo(JSONObject allresult) throws JSONException {

		result = allresult.getString("result");

		wxpayinfo = allresult.getString("wxpayinfo");

		dialog.setTitle("验证成功，正在登录。。。");

		// 将数据存储到本地的数据库中
		Gson gson = new Gson();

		// 获取到WEB端传递过来的数据
		user = gson.fromJson(result, User.class);

		Wxpayinfo wi = gson.fromJson(wxpayinfo, Wxpayinfo.class);

		db.deleteLocalUserInfo();

		db.insertNewUser(user, wi.getWxjoincode(), wi.getWxcheckcode(), wi.getMd5key());

	}

	private void findView() {

		bt_submit = (Button) findViewById(R.id.bt_login_submit);

		et_userName = (EditText) findViewById(R.id.et_login_user_name);

		et_password = (EditText) findViewById(R.id.et_login_user_password);

		tv_forget_password = (TextView) findViewById(R.id.tv_login_forget_password);

	}

}
