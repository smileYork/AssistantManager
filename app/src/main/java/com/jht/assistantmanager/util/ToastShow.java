package com.jht.assistantmanager.util;

import android.content.Context;
import android.widget.Toast;

public class ToastShow {

	public static void ToastShowLong(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void ToastShowShort(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
