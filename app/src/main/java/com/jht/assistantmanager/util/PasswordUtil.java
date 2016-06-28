package com.jht.assistantmanager.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

public class PasswordUtil {

	public static String getMd5ByString(String str) {

		try {

			// 生成实现指定摘要算法的 MessageDigest 对象。

			MessageDigest md = MessageDigest.getInstance("MD5");

			// 使用指定的字节数组更新摘要。
			md.update(str.getBytes());

			// 通过执行诸如填充之类的最终操作完成哈希计算。
			byte b[] = md.digest();

			// 生成具体的md5密码到buf数组
			int i;

			StringBuffer buf = new StringBuffer("");

			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");

				buf.append(Integer.toHexString(i));

			}

			return buf.toString();// 32位的加密

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String generateSign(String content, String md5key) {

		String toSign = (content == null ? "" : content) + md5key;

		// 返回的签名
		String sign = null;

		try {

			sign = new String(Hex.encodeHex(DigestUtils.md5(toSign.getBytes())));

			// sign = DigestUtils.md5Hex(getContentBytes(toSign, "utf-8"));

		} catch (Exception e) {

			e.printStackTrace();

			Log.i("First", "获取签名错误：" + e.toString());

		}

		return sign;
	}

	/**
	 * 根据map集合获取签名
	 * 
	 * @param
	 * @param md5key
	 * @return
	 */
	public static String generateSign(JSONObject jsonParam, String md5key) {
		// 返回的签名
		String sign = null;
		// 需要签名的内容
		String toSign = null;

		try {

			toSign = createSignString(jsonConvertMap(jsonParam));

			toSign = (toSign == null ? "" : toSign) + md5key;

			sign = new String(Hex.encodeHex(DigestUtils.md5(toSign.getBytes())));

			// DigestUtils.md5Hex(toSign.getBytes());

			// sign = getMd5ByString(toSign);

		} catch (Exception e) {

			e.printStackTrace();

		}
		return sign;
	}

	/**
	 * 把数组所有元素排序
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组,无连接符
	 * @return 拼接后字符串
	 */
	public static String createSignString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = (String) params.get(key);
			// 去掉空置和 sign 签名
			if (value != null && !value.equals("") && !key.equalsIgnoreCase("sign")) {
				prestr = prestr + key + value;
			}
		}
		return prestr;
	}

	/**
	 * json对象数据转成map结构
	 * 
	 * @param jsonParam
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> jsonConvertMap(JSONObject jsonParam) throws JSONException {
		Map<String, String> params = new HashMap<String, String>();
		Iterator it = jsonParam.keys();
		// 遍历jsonObject数据，添加到Map对象
		while (it.hasNext()) {
			String key = String.valueOf(it.next());
			String value = jsonParam.getString(key);
			params.put(key, value);
		}
		return params;
	}

	protected static byte[] getContentBytes(String content, String charset) throws UnsupportedEncodingException {

		if (charset==null||charset.length()==0||charset.equals("")) {

			return content.getBytes();

		}
		return content.getBytes(charset);
	}

	public static String getMD5Str(String str) {

		MessageDigest messageDigest = null;

		try {

			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));

		} catch (NoSuchAlgorithmException e) {

			System.out.println("NoSuchAlgorithmException caught!");

			System.exit(-1);

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {

			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {

				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));

			} else {

				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));

			}
		}

		return md5StrBuff.toString();
	}

}
