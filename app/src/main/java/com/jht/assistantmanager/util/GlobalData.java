package com.jht.assistantmanager.util;

import java.io.IOException;
import org.apache.http.entity.StringEntity;
import android.app.DownloadManager;
import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

public class GlobalData {

	public static final String SHAREDPREFERENCES_NAME = "system_setting";

	public static final String TABLE_SALE_CARD = "tb_sale_card";

	public static final String TABLE_USER_INFO = "tb_user_info";

	public static String Login_Url = "http://192.168.100.123:8181/SPYSPT_INTERFACE/login";

	public static String Card_Search_Card_Url = "http://192.168.100.123:8181/SPYSPT_INTERFACE/actCodeinfo";

	public static String Card_Search_Order_Url = "http://192.168.100.123:8181/SPYSPT_INTERFACE/actOrderinfo";

	public static String Orderall = "http://192.168.100.123:8181/SPYSPT_APP/actOrderinfo";

	public static String My_Order_Url = "http://192.168.100.123:8181/SPYSPT_APP/interAction";

	public static String Alipay_Url = "http://192.168.100.123:8181/AlipayScan/";

	// 微信接口调用链接
	public static String wx1 = "http://wx1.jhtsoft.com/pay!preOrder?";

	public static String alipaySearch = "http://192.168.100.123:8181/SPYSPT_APP/interAction!getalipaySearch?";

	// 微信回调支付接口
	public static String async_url = "http://jhtsoft.oicp.net/SPYSPT_APP/interAction!updateState";

	// APP插入订单的接口2
	public static String insertOrdermd5 = "interAction!actInter";

	// 微信查询接口
	public static String wxqueryOrder = "http://wx1.jhtsoft.com/pay!queryOrder?";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static String aly = "http://jhtsoft.oicp.net/SPYSPT_APP/interAction!getalipay?";

	// 设定转换类型
	public static String ContentType = "application/json; charset=UTF-8";

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	private static OkHttpClient clientForOk = new OkHttpClient();

	static {
		client.setTimeout(12000);
	}

	public static void getWithAbstrctAddress(String url, RequestParams params, AsyncHttpResponseHandler handle) {
		client.get(url, params, handle);
	}

	public static void postCS_URL(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}

	public static void getWithAbstrctAddress(String url, AsyncHttpResponseHandler handle) {
		client.get(url, handle);
	}

	public static void getFileWithAbstrctAddress(String url, BinaryHttpResponseHandler handle) {
		client.get(url, handle);
	}


}
