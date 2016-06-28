package com.jht.assistantmanager.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Constants {

	public static final int POINT_STATE_NORMAL = 0; // ����״̬

	public static final int POINT_STATE_SELECTED = 1; // ����״̬

	public static final int POINT_STATE_WRONG = 2; // ����״̬

	// 周一的日期
	public static String getMonday() {

		String monday = "";
		long l = System.currentTimeMillis();
		// new日期对象
		Date date = new Date(l);

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		cal.add(Calendar.DAY_OF_MONTH, -1); // 解决周日会出现 并到下一周的情况
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		monday = dateFormat.format(cal.getTime());

		return monday;

	}

	// 当天的日期
	public static String getNowday() {
		String nonday = "";

		// 得到long类型当前时间
		long l = System.currentTimeMillis();
		// new日期对象
		Date date = new Date(l);
		// 转换提日期输出格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		// 当日时间（年月日）
		nonday = dateFormat.format(date);

		return nonday;
	}

	// 一个月之前的日期
	public static String lastMonth(int allMonth) {
		Date date = new Date();
		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(date)) - allMonth;
		int day = Integer.parseInt(new SimpleDateFormat("dd").format(date));
		if (month <= 0) {
			int yearFlag = (month * (-1)) / 12 + 1;
			int monthFlag = (month * (-1)) % 12;
			year -= yearFlag;
			month = monthFlag * (-1) + 12;
		} else if (day > 28) {
			if (month == 2) {
				if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
					day = 29;
				} else
					day = 28;
			} else if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 31) {
				day = 30;
			}
		}
		String y = year + "";
		String m = "";
		String d = "";
		if (month < 10)
			m = "0" + month;
		else
			m = month + "";
		if (day < 10)
			d = "0" + day;
		else
			d = day + "";

		return y + "-" + m + "-" + d;
	}

	// 一个月的第一天的时间
	public static String getMonthFirstDay() {
		String nonday = "";

		// 得到long类型当前时间
		long l = System.currentTimeMillis();
		// new日期对象
		Date date = new Date(l);
		// 转换提日期输出格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-");
		// 当日时间（年月日）
		nonday = dateFormat.format(date) + "01";

		return nonday;
	}
}
