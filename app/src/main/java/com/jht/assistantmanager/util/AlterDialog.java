package com.jht.assistantmanager.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class AlterDialog {

	public static Builder singleDailog(Context context, String title, String item1, String item2,
			OnClickListener listen, int choice) {
		Builder dialog = new Builder(context).setTitle(title)
				.setSingleChoiceItems(new String[] { item1, item2 }, choice, listen);
		return dialog;
	}

	public static Builder alterDailog(Context context, String title, String message,
			OnClickListener sure, OnClickListener cancel) {

		Builder alterDialog = new Builder(context);

		alterDialog.setTitle(title).setIcon(android.R.drawable.ic_menu_info_details).setMessage(message)
				.setPositiveButton("确定", sure).setNegativeButton("取消", cancel);

		return alterDialog;
	}

	public static Builder alterDailogWithOutCancel(Context context, String title, String message,
			OnClickListener sure) {

		Builder alterDialog = new Builder(context);

		alterDialog.setTitle(title).setIcon(android.R.drawable.ic_menu_info_details).setMessage(message)
				.setPositiveButton("确定", sure);

		return alterDialog;
	}

	public static Builder alterDailogForCash(Context context, String title, String message,
			OnClickListener sure, OnClickListener cancel) {

		Builder alterDialog = new Builder(context);
		alterDialog.setTitle(title).setIcon(android.R.drawable.ic_menu_info_details).setMessage(message)
				.setPositiveButton("确定激活", sure).setNegativeButton("取消操作", cancel);

		return alterDialog;
	}

}
