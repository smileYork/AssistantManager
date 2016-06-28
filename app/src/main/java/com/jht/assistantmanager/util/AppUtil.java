/**
 * 
 */
package com.jht.assistantmanager.util;

import java.util.UUID;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class AppUtil {

	public static int[] getScreenDispaly(Context context) {

		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		Display display = windowManager.getDefaultDisplay();

		Point size = new Point();

		display.getSize(size);

		int width = size.x;

		int height = size.y;

		int result[] = { width, height };

		return result;
	}

	public static String getUUID() {

		String uuid = UUID.randomUUID().toString();

		return uuid.replace("-", "");
	}

	public static String generateOrderId(String khdm) {

		return "JHT" + khdm + System.currentTimeMillis();

	}

	public static boolean judgeCardNumber(String cardNumber) {

		if (cardNumber.length() >= 16) {

			String code = cardNumber.substring(cardNumber.length() - 16, cardNumber.length());
			// 在对后16位字符串进行判断，如果字符串后16位不为纯数字，则不合法

			if (code != null && code.length() > 0) {

				// value属于哪一类的扫描，code为编码
				return true;

			}

		}

		return false;

	}

	public static String getTypeTime(int state) {

		String code = "";

		if (state == 1) {
			code = "激活时间";
		} else if (state == 2) {
			code = "提货时间";
		} else {
			code = "创建时间";
		}
		return code;
	}
}
