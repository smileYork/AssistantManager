package com.jht.assistantmanager.util;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.util.Log;

//判断当前手机有没有网络的工具类

public class NetWorkJudgeUtil {

	public final static int NONE = 0;
	public final static int WIFI = 1;
	public final static int MOBILE = 2;

	// 判断当前网络是哪种类型，0。代表没有网络，1。代表当前是wifi环境，2代表当前是流量环境
	public static int getNetworkState(Context context) {

		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		
		if(NetWorkJudgeUtil.isTablet(context)){
			
			State	state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if(state == State.CONNECTED || state == State.CONNECTING) {
				return WIFI;
			}
		}else{
			State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			if(state == State.CONNECTED || state == State.CONNECTING) {
				return MOBILE;
			}
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			if(state == State.CONNECTED || state == State.CONNECTING) {
				return WIFI;
			}
		}
		return NONE;
	}

	// 单纯地判断当前网络可不可用，true代表可用，false代表不可用
	public static boolean isConnect(Context context) {
		
		try {
			
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			if (connectivity != null) {
				
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				
				if (info != null && info.isConnected()) {
					
					if (info.getState() == State.CONNECTED) {
						
						return true;
						
					}
					
				}
				
			}
			
		} catch (Exception e) {
			
			Log.e("First", e.toString());
			
		}
		
		return false;
	}
	
	  /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     * @param context
     * @return 平板返回 True，手机返回 False
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
