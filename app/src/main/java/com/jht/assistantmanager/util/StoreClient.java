package com.jht.assistantmanager.util;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @AsyncHttpClient 从服务器请求或者提交数据的方法类
 */
public class StoreClient {

    //控制中心的URL地址
//	public static  String CenterBASE_URL = "http://192.168.100.164:9999/diyws/";
    public static String BASE_URL;
    public static String CenterBASE_URL = "http://112.80.46.114:9999/diyws/";

    //public static  String CenterBASE_URL = "http://192.168.100.123:8080/diyws/";
//	public static final String CenterBASE_URL = "http://192.168.100.89:8108/diyws/";


    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        client.setTimeout(12000); // 设置链接超时，如果不设置，默认为12s
    }

    public static void get(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static int count = 0;
    public static String Url;
    public static String WebVerson;
    public static String Verson;
}
