package com.jht.assistantmanager.util;

import java.util.ArrayList;
import java.util.List;

import com.jht.assistantmanager.model.CardForSearch;
import com.jht.assistantmanager.model.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyDBOperation {

	private SQLiteDatabase db;

	private MySQLiteOpenHelp mysql;

	public MyDBOperation(Context context) {

		mysql = new MySQLiteOpenHelp(context);

		db = mysql.getReadableDatabase();

	}

	public long insertTempSaleItem(CardForSearch card) {

		ContentValues content = new ContentValues();

		content.put("code", card.getCode());

		content.put("cardprice", card.getCardprice());

		content.put("cardsmallprice", card.getCardsmallprice());

		content.put("packagename", card.getStatusname());

		content.put("actdate", card.getActdate());

		content.put("selnum", card.getSelnum());

		return db.insert(GlobalData.TABLE_SALE_CARD, null, content);
	}

	public boolean findSaleItemByCardId(String cardId) {

		String sql = "select count(*) as num from " + GlobalData.TABLE_SALE_CARD + " where code = '" + cardId + "'";

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext()) {

			int number = cursor.getInt(cursor

					.getColumnIndex("num"));

			if (number == 1) {
				return true;
			}
		}

		cursor.close();

		return false;

	}

	public List<CardForSearch> getLocalCardForSaleList() {

		List<CardForSearch> list_sale = new ArrayList<CardForSearch>();

		CardForSearch cardItem;

		String sql = "select * from " + GlobalData.TABLE_SALE_CARD;

		Cursor cursor = db.rawQuery(sql, null);

		while (cursor.moveToNext()) {

			cardItem = new CardForSearch();

			cardItem.setCode(cursor.getString(cursor

					.getColumnIndex("code")));

			cardItem.setCardprice(cursor.getDouble(cursor

					.getColumnIndex("cardprice")));

			cardItem.setCardsmallprice(cursor.getDouble(cursor

					.getColumnIndex("cardsmallprice")));

			cardItem.setStatusname(cursor.getString(cursor

					.getColumnIndex("packagename")));

			cardItem.setActdate(cursor.getString(cursor

					.getColumnIndex("actdate")));

			cardItem.setSelnum(cursor.getInt(cursor

					.getColumnIndex("selnum")));

			list_sale.add(cardItem);

			Log.i("First", cardItem.toString());
		}

		cursor.close();

		return list_sale;

	}

	public void deleteSaleItemFromLocalList(String cardId) {

		String sql = "delete from " + GlobalData.TABLE_SALE_CARD + " where code = '" + cardId + "'";

		db.execSQL(sql);
	}

	public int getSaleItemNumber() {

		int number = 0;

		String sql = "select count(1) as num from " + GlobalData.TABLE_SALE_CARD;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext()) {

			number = cursor.getInt(cursor

					.getColumnIndex("num"));

		}
		cursor.close();
		return number;
	}

	public void deleteAllLocalSaleItem() {

		String sql = "delete from " + GlobalData.TABLE_SALE_CARD;

		db.execSQL(sql);

	}

	public double getSaleListSumMoney() {

		double sumMoney = 0;

		String sql = "select sum(cardsmallprice) as sumMoney from " + GlobalData.TABLE_SALE_CARD;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext()) {

			sumMoney = cursor.getDouble(cursor

					.getColumnIndex("sumMoney"));

		}

		cursor.close();
		return sumMoney;

	}

	public void deleteLocalUserInfo() {

		String sql = "delete from " + GlobalData.TABLE_USER_INFO;

		db.execSQL(sql);

	}

	public long insertNewUser(User user, String wxjoincode, String wxcheckcode, String md5key) {

		// 添加本地我的订单表的数据
		ContentValues content = new ContentValues();
		content.put("id", user.getId());
		content.put("khdm", user.getKhdm());
		content.put("flowcode", user.getFlowcode());
		content.put("pflowcode", user.getPflowcode());
		content.put("username", user.getUsername());
		content.put("pwd", user.getPwd());
		content.put("objtype", user.getObjtype());
		content.put("name", user.getName());
		content.put("realname", user.getRealname());
		content.put("sex", user.getSex());
		content.put("mphone", user.getMphone());
		content.put("tphone", user.getTphone());
		content.put("email", user.getEmail());
		content.put("buse", user.getBuse());
		content.put("provincecode", user.getProvincecode());
		content.put("province", user.getProvince());
		content.put("citycode", user.getCitycode());
		content.put("city", user.getCity());
		content.put("districtcode", user.getDistrictcode());
		content.put("district", user.getDistrict());
		content.put("fulladdr", user.getFulladdr());
		content.put("addrdet", user.getAddrdet());
		content.put("power", user.getPower());
		content.put("storepower", user.getStorepower());
		content.put("tpower", user.getTpower());
		content.put("bassign", user.getBassign());
		content.put("breservation", user.getBreservation());
		content.put("bcandefprice", user.getBcandefprice());
		content.put("subaccountpower", user.getSubaccountpower());
		content.put("showpos", user.getShowpos());
		content.put("opercode", user.getOpercode());
		content.put("oper", user.getOper());
		content.put("credate", user.getCredate());
		content.put("lastupddate", user.getLastupddate());
		content.put("lastlogindate", user.getLastlogindate());
		content.put("ip", user.getIp());
		content.put("uid", user.getUid());
		content.put("syncdate", user.getSyncdate());
		content.put("ischeckpwd", user.getIscheckpwd());
		content.put("md5key", md5key);
		content.put("wxjoincode", wxjoincode);
		content.put("wxcheckcode", wxcheckcode);
		return db.insert(GlobalData.TABLE_USER_INFO, null, content);
	}

	public String getUserName() {
		String name = "";

		String sql = "select username from " + GlobalData.TABLE_USER_INFO;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext()) {

			name = cursor.getString(cursor

					.getColumnIndex("username"));

		}

		cursor.close();

		return name;
	}

	public String getMd5Key() {

		String key = "";

		String sql = "select md5key from " + GlobalData.TABLE_USER_INFO;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext()) {

			key = cursor.getString(cursor

					.getColumnIndex("md5key"));

		}

		cursor.close();
		return key;
	}

	public String getFlowCode() {

		String key = "";

		String sql = "select flowcode from " + GlobalData.TABLE_USER_INFO;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext()) {

			key = cursor.getString(cursor

					.getColumnIndex("flowcode"));

		}

		cursor.close();
		return key;
	}

	public User getLocalUserInfo() {

		User user = new User();

		String sql = "select * from " + GlobalData.TABLE_USER_INFO;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext()) {

			user.setUsername(cursor.getString(cursor

					.getColumnIndex("username")));

			user.setSex(cursor.getInt(cursor

					.getColumnIndex("sex")));

			user.setName(cursor.getString(cursor

					.getColumnIndex("name")));

			user.setEmail(cursor.getString(cursor

					.getColumnIndex("email")));

			user.setMphone(cursor.getString(cursor

					.getColumnIndex("mphone")));

			user.setFulladdr(cursor.getString(cursor

					.getColumnIndex("fulladdr")));

		}

		cursor.close();
		return user;
	}

	public String getIsUseMessageInfo() {

		String Flowcode = "";
		String sql = "select  ischeckpwd from " + GlobalData.TABLE_USER_INFO;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			Flowcode = cursor.getString(0);
		}
		cursor.close();
		return Flowcode;
	}

	public String getOrderCodeList() {

		String codes = "";

		String sql = "select group_concat(code) as codes from  " + GlobalData.TABLE_SALE_CARD;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext()) {
			codes = cursor.getString(0);
		}

		cursor.close();

		return codes;

	}

	public String getUserInfo(String needStr) {
		// 通过用户名查询秘钥
		String Flowcode = "";
		String sql = "select " + needStr + " from  " + GlobalData.TABLE_USER_INFO;
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToFirst()) {
			Flowcode = cursor.getString(0);
		}
		cursor.close();
		return Flowcode;
	}

}
