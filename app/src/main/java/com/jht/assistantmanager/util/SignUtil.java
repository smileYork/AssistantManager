package com.jht.assistantmanager.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SignUtil {
    public static String generateSign(JSONObject jsonParam, String md5key) {
        //返回的签名
        String sign = null;
        //需要签名的内容
        String toSign = null;
        try {
            toSign = createSignString(jsonConvertMap(jsonParam));
            toSign = (toSign == null ? "" : toSign) + md5key;
            sign = new String(Hex.encodeHex(DigestUtils.md5(toSign)));

        } catch (Exception e) {
            System.out.println(e);
        }
        return sign;
    }


    @SuppressWarnings("rawtypes")
    public static Map<String, String> jsonConvertMap(JSONObject jsonParam) {
        Map<String, String> params = new HashMap<String, String>();
        Iterator it = jsonParam.keys();
        // 遍历jsonObject数据，添加到Map对象
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            String value;
            try {
                value = jsonParam.getString(key);
                params.put(key, value);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                System.out.println(e);
            }
        }
        return params;
    }

    /**
     * 把数组所有元素排序
     *
     * @param params 需要排序并参与字符拼接的参数组,无连接符
     * @return 拼接后字符串
     */
    public static String createSignString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = (String) params.get(key);
            //去掉空置和 sign 签名
            if (value != null && !value.equals("") && !key.equalsIgnoreCase("sign")) {
                prestr = prestr + key + value;
            }
        }
        return prestr;
    }

    /**
     * 得到字符串的某种编码
     *
     * @param content
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    protected static byte[] getContentBytes(String content, String charset) throws UnsupportedEncodingException {
        if (charset.isEmpty()) {
            return content.getBytes();
        }
        return content.getBytes(charset);
    }
}

