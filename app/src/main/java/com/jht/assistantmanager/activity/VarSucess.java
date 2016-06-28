package com.jht.assistantmanager.activity;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jht.assistantmanager.R;

public class VarSucess extends Activity {

	private ImageButton title_back_image;

	private Button btn_continue, btn_miss;

	private TextView varresult, search_ucode, search_state, search_pkgname, search_orderno, search_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_card_varsucess);

		findViewById();

		initView();

		setListener();

	}

	protected void findViewById() {

		title_back_image = (ImageButton) findViewById(R.id.title_back_image);

		btn_continue = (Button) findViewById(R.id.btn_continue);

		btn_miss = (Button) findViewById(R.id.btn_miss);

		varresult = (TextView) findViewById(R.id.varresult);

		search_ucode = (TextView) findViewById(R.id.search_ucode);

		search_state = (TextView) findViewById(R.id.search_state);

		search_pkgname = (TextView) findViewById(R.id.search_pkgname);

		search_orderno = (TextView) findViewById(R.id.search_orderno);

		search_time = (TextView) findViewById(R.id.search_time);
	}

	protected void initView() {

		Intent intent = getIntent();

		String code = intent.getStringExtra("code");

		String error = intent.getStringExtra("error");

		if (code.equals("300")) {

			varresult.setText(error);

		}

		String result = intent.getStringExtra("result");

		getresult(result);

	}

	private void getresult(String result) {

		JSONObject allresult;

		try {

			allresult = new JSONObject(result);

			String statusname = allresult.getString("statusname");

			String submitdate = allresult.getString("submitdate");

			String codes = allresult.getString("code");

			String orderno = allresult.getString("orderno");

			String ordercontent = allresult.getString("ordercontent");

			search_ucode.setText(codes);

			search_state.setText(statusname);

			search_pkgname.setText(ordercontent);

			search_orderno.setText(orderno);

			search_time.setText(submitdate);

		} catch (JSONException e) {

			System.err.println("解析异常");

		}

	}

	public void setListener() {
		title_back_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		// 底部按钮的点击事件
		btn_miss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(VarSucess.this, ShowMainActivity.class);

				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

				startActivity(i);

				finish();
			}
		});
		btn_continue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

	}

}