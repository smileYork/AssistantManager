package com.jht.assistantmanager.util;

import android.content.Context;
import android.content.Intent;

public class ActivityManage {

	private static Intent intent;

	public static void startActivity(Context mContext, Class<?> aimClass) {

		intent = new Intent();

		intent.setClass(mContext, aimClass);

		mContext.startActivity(intent);

	}

}
