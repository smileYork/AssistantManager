package com.jht.assistantmanager.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelp extends SQLiteOpenHelper {

	private static final int version = 1;

	public static final String DATABASE_NAME = "MessagePlat.db";

	public MySQLiteOpenHelp(Context context) {

		super(context, DATABASE_NAME, null, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String TABLE_ASSISTANCE_CARDFORSEARCH = "create table " + GlobalData.TABLE_SALE_CARD
				+ " (code varchar(24) primary key," + //
				"cardprice double," + //
				"cardsmallprice double," + //
				"packagename varchar(24)," + //
				"actdate varchar(32)," + //
				"selnum interger" + //
				");";
		db.execSQL(TABLE_ASSISTANCE_CARDFORSEARCH);

		// 创建用户登陆表
		String TB_USER_INFO = "create table " + GlobalData.TABLE_USER_INFO + "(id INTEGER ," + "khdm varchar(4),"
				+ "flowcode varchar(36) ," + "pflowcode varchar(36) ," + "username varchar(40)," + "pwd varchar(32),"
				+ "objtype INTEGER," + "name varchar(40)," + "realname varchar(28)," + "birthday  varchar(40),"
				+ "sex INTEGER," + "mphone varchar(20)," + "tphone varchar(20)," + "email varchar(50),"
				+ "buse INTEGER," + "provincecode  varchar(16)," + "province varchar(40)," + "citycode  varchar(16),"
				+ "city varchar(40)," + "districtcode  varchar(16)," + "district varchar(40),"
				+ "fulladdr  varchar(230)," + "addrdet varchar(100)," + "power  varchar(30)," + "storepower varchar(8),"
				+ "tpower  varchar(20)," + "bassign INTEGER," + "breservation  INTEGER," + "bcandefprice INTEGER,"
				+ "subaccountpower  varchar(10)," + "showpos INTEGER," + "opercode  varchar(36)," + "oper varchar(40),"
				+ "credate  varchar(36)," + "lastupddate varchar(36)," + "lastlogindate  varchar(36),"
				+ "ip varchar(20)," + "uid  varchar(36)," + "syncdate varchar(20)," + "wxjoincode  varchar(20),"
				+ "wxcheckcode varchar(50)," + "md5key  varchar(40)," + "partner  varchar(50),"
				+ "alipaysellerid varchar(50)," + "alipaypubkey  varchar(2000)," + "alipayseckey varchar(2000),"
				+ "alipayopenstate  varchar(20)," + "weixincontrol varchar(20)," + "ischeckpwd INTEGER" + ");";

		db.execSQL(TB_USER_INFO);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {

	}

}
