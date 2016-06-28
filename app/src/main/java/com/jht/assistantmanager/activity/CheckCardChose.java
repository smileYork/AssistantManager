package com.jht.assistantmanager.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.Adapter_ListView_check;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.model.Goods;
import com.jht.assistantmanager.model.GoodsCheck;
import com.jht.assistantmanager.util.AppUtil;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

public class CheckCardChose extends Activity {

	private ListView lv_card_chose_list;

	private Adapter_ListView_check listAdapet;

	private ImageButton title_back_image;

	private ImageView img_card_sale_search_car;

	private List<Goods> listcheck;

	private Goods gs;

	private GoodsCheck gc;

	private int selnum = 0;

	private String code = "";

	private String cardNumber = "";

	private Dialog loadingDialog;

	private Button bt_card_sale_list_cehck;

	private Context mContext;

	List<String> mTagList = new ArrayList<String>();

	private JSONObject temp_object;

	private MyDBOperation db;

	private String error;

	private String result;

	private String failure;

	private JSONObject object;

	private String goodslist = "";

	private String goodsListForVerify = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_card_var_chose);

		try {

			findView();

			getCardInfo();

			setListen();

		} catch (Exception e) {

			e.printStackTrace();

			ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

		}

	}

	private void initView(String result2) throws JSONException {

		object = new JSONObject(result2);

		Gson gson = new Gson();

		// 有默认商品
		if (hasDefaultGoods(object)) {

			goodslist = object.getString("defaultgoods");

			gs = new Goods();

			gs = gson.fromJson(goodslist, Goods.class);

			listcheck = new ArrayList<Goods>();

			listcheck.add(gs);

		} else {

			// 没有默认商品，只有可选商品
			if (object.toString().contains("goodslist")) {

				goodslist = object.getString("goodslist");

				Type typebuff = new TypeToken<List<Goods>>() {

				}.getType();

				listcheck = gson.fromJson(goodslist, typebuff);

			} else {

				ToastShow.ToastShowShort(mContext, "没有可选套餐");

				finish();

			}

		}

		gc = new GoodsCheck();

		for (int i = 0; i < listcheck.size(); i++) {

			gs = new Goods();

			gs = listcheck.get(i);

			gc.setGoodscode(gs.getGoodscode());

			gc.setGoodsname(gs.getGoodsname());

			gc.setIscheck(0);

		}

		selnum = object.getInt("selnum");

		listAdapet = new Adapter_ListView_check(mContext, listcheck, selnum);

		lv_card_chose_list.setAdapter(listAdapet);

		loadingDialog.dismiss();

	}

	private void setListen() {

		title_back_image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bt_card_sale_list_cehck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 统计选中的数量
				int number = 0;

				String tempGoodslist = "";

				for (int i = 0; i < listcheck.size(); i++) {

					if (((CheckBox) (lv_card_chose_list.getChildAt(i)).findViewById(R.id.cb_choice)).isChecked()) {

						number++;

						if (tempGoodslist.equals("")) {

							tempGoodslist += listcheck.get(i).getGoodscode();

						} else {

							tempGoodslist += "," + listcheck.get(i).getGoodscode();

						}

					}
				}

				if (number != selnum) {

					ToastShow.ToastShowShort(mContext, "请选择" + selnum + "种套餐");

				} else {

					goodsListForVerify = tempGoodslist;

					// 需要输入验证码
					if (db.getIsUseMessageInfo().contains("1")) {

						getDialoEditText();

					} else {

						getCheckMethod(goodsListForVerify);

					}
				}

			}
		});

		img_card_sale_search_car.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 清空所有选项
				for (int i = 0; i < listcheck.size(); i++) {
					// 如果选中了全选，那么就将列表的每一行都选中
					((CheckBox) (lv_card_chose_list.getChildAt(i)).findViewById(R.id.cb_choice)).setChecked(false);
				}
			}
		});

	}

	protected void getDialoEditText() {

		final EditText inputServer = new EditText(this);

		inputServer.setWidth(android.view.WindowManager.LayoutParams.WRAP_CONTENT);

		inputServer.setPaddingRelative(20, 20, 20, 20);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("请输入验证码").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)

				.setNegativeButton("取消", null);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				String short_message = inputServer.getText().toString();

				if (short_message.equals("") || short_message.isEmpty()) {

					ToastShow.ToastShowShort(mContext, "验证码不能为空，请输入验证码！");

					getDialoEditText();

				} else {

					getCheckMethod(goodslist);

				}
			}
		});
		builder.show();

	}

	public void getCheckMethod(String goodsListTemp) {
		try {

			loadingDialog = LoadingDialog.createLoadingDialog(mContext, "正在进行券卡核销,请稍等...");

			loadingDialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfoForCard(goodsListTemp);

				// 设定编码格式
				StringEntity entity = new StringEntity(temp_object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Card_Search_Order_Url, entity,
						GlobalData.ContentType, verifyHttpResponseHandler);

			} else {

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

				finish();

			}

		} catch (

		Exception e) {

			loadingDialog.dismiss();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");

			finish();

		}

	}

	private void initRequestInfoForCard(String goodsListTemp) throws JSONException {

		temp_object = new JSONObject();

		if (hasDefaultGoods(object)) {

			temp_object.put("goodscodes", 0);

		} else {

			temp_object.put("goodscodes", goodsListTemp);

		}

		temp_object.put("memo", "备注");

		temp_object.put("timestamp", System.currentTimeMillis());

		temp_object.put("usercode", db.getFlowCode());

		temp_object.put("usertype", 1);

		temp_object.put("code", cardNumber);

		temp_object.put("method", "verifycard");

		temp_object.put("uid", AppUtil.getUUID());

		temp_object.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(temp_object, db.getMd5Key());

		temp_object.put("sign", sign);

	}

	private AsyncHttpResponseHandler verifyHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultall = new String(responseBody);

			Log.i("First", resultall);

			if (resultall != null && resultall.length() > 0) {

				JSONObject allresult;

				try {
					allresult = new JSONObject(resultall);

					code = allresult.getString("code");

					error = allresult.getString("error");

					result = allresult.getString("result");

					if (!code.isEmpty() && !error.isEmpty() && !result.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						Intent intent = new Intent(mContext, VarSucess.class);

						intent.putExtra("result", result);

						intent.putExtra("error", error);

						intent.putExtra("code", code);

						startActivity(intent);

						finish();

					}
				} catch (Exception e) {
					ToastShow.ToastShowShort(mContext, "网络不稳定，请稍后在尝试！");
					System.out.println("解析json异常");
				}

			} else {
				ToastShow.ToastShowShort(mContext, "服务器异常！");
			}


		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
			ToastShow.ToastShowShort(mContext, "服务器异常");
		}
	};

	private void getCardInfo() {

		try {

			loadingDialog = LoadingDialog.createLoadingDialog(mContext, "正在查询券卡号,请稍等...");

			loadingDialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfo();

				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Card_Search_Card_Url, entity,
						GlobalData.ContentType, searchHttpResponseHandler);

			} else {

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

				finish();

			}

		} catch (Exception e) {

			loadingDialog.dismiss();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");

			finish();

		}

	}

	private void initRequestInfo() throws JSONException {

		object = new JSONObject();

		object.put("usercode", db.getFlowCode());

		object.put("usertype", 1);

		object.put("method", "querycardno");

		object.put("code", cardNumber);

		object.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(object, db.getMd5Key());

		object.put("sign", sign);

	}

	private AsyncHttpResponseHandler searchHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultStr = new String(responseBody);

			if (resultStr != null && resultStr.length() > 0) {

				try {

					object = new JSONObject(resultStr);

					code = object.getString("code");

					error = object.getString("error");

					// 登录成功
					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						result = object.getString("result");

						Log.i("First", result);

						initView(result);

					} else {// 查询失败

						failure = object.getString("error");

						loadingDialog.dismiss();

						ToastShow.ToastShowShort(mContext, failure);

						finish();
					}

				} catch (JSONException e) {

					e.printStackTrace();

					loadingDialog.dismiss();

					ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

					finish();

				}

			}
		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

			loadingDialog.dismiss();

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

		}


	};

	private boolean hasDefaultGoods(JSONObject objectTemp) throws JSONException {

		JSONObject tempObject = objectTemp.getJSONObject("defaultgoods");

		Iterator<?> it = tempObject.keys();

		// 有默认商品
		if (it.hasNext()) {

			return true;

		} else {

			return false;
		}

	}

	private void findView() {

		lv_card_chose_list = (ListView) findViewById(R.id.lv_card_chose_list);

		title_back_image = (ImageButton) findViewById(R.id.img_bt_check_card_return);

		img_card_sale_search_car = (ImageView) findViewById(R.id.img_card_check_chose_delete);

		bt_card_sale_list_cehck = (Button) findViewById(R.id.bt_card_sale_list_cehck);

		cardNumber = getIntent().getStringExtra("code");

		mContext = CheckCardChose.this;

		db = new MyDBOperation(mContext);

		listcheck = new ArrayList<Goods>();

	}

}
