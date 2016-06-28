package com.jht.assistantmanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.jht.assistantmanager.R;

public class ExitActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_about_and_help);

		finish();

	}

}
