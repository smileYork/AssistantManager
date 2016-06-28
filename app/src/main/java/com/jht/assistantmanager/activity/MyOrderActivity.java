package com.jht.assistantmanager.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.ListForMyOrder;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.model.Apporderrecord;
import com.jht.assistantmanager.pulltorefresh.PullToRefreshLayout;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class MyOrderActivity extends Activity {

	private ListView lv_order;

	private ListView lv_time;

	private List<Apporderrecord> list_apporder;

	private Apporderrecord order_item;

	private ListForMyOrder listAdapet;

	private ImageButton img_bt_return;

	private PopupWindow popupWindow;

	private Button bt_time;

	private Context mContext;

	private PullToRefreshLayout ptrl;

	private MyListener myListener;

	private int order_type;

	private Handler handler;

	private Dialog lodingDialog;

	private MyDBOperation db;

	private String code;

	private String result;

	private String error;

	private JSONObject object;

	private int PAGE_SIZE = 20;

	private Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_my_order);

		try {

			findView();

			initView();

			getOrderList(order_type);

			setListen();

		} catch (Exception e) {

			e.printStackTrace();

			ToastShow.ToastShowShort(mContext, "网络异常，请稍后再试");

		}

	}

	private void getOrderList(int order_type) {

		try {

			lodingDialog = LoadingDialog.createLoadingDialog(mContext, "正在加载数据,请稍等...");

			lodingDialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfo(order_type, 0);

				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.My_Order_Url, entity,
						GlobalData.ContentType, searchHttpResponseHandler);

			} else {

				lodingDialog.dismiss();

				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");

			}

		} catch (Exception e) {

			lodingDialog.dismiss();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");

		}

	}

	private AsyncHttpResponseHandler searchHttpResponseHandler = new AsyncHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

			String resultStr = new String(responseBody);

			Log.i("First", resultStr);

			if (resultStr != null && resultStr.length() > 0) {

				try {

					object = new JSONObject(resultStr);

					code = object.getString("code");

					error = object.getString("error");

					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						result = object.getString("result");

						SetView(result);

						listAdapet.notifyDataSetChanged();

						lodingDialog.dismiss();

					} else {// 查询失败

						lodingDialog.dismiss();

						ToastShow.ToastShowShort(mContext, "" + error);

						finish();

					}
				} catch (JSONException e) {

					e.printStackTrace();

					lodingDialog.dismiss();

					ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

				}

			} else {

				lodingDialog.dismiss();

				ToastShow.ToastShowShort(mContext, "网络故障，请稍后再试");

			}

		}

		@Override
		public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

			lodingDialog.dismiss();

			ToastShow.ToastShowLong(mContext, "服务器日常维护中，请稍后再试！");

		}


	};

	protected void SetView(String result2) throws JSONException {

		JSONArray temJsonArray = new JSONArray(result2);

		for (int i = 0; i < temJsonArray.length(); i++) {

			order_item = gson.fromJson(temJsonArray.get(i).toString(), Apporderrecord.class);

			list_apporder.add(order_item);

		}

	}

	private void initRequestInfo(int time, int lastId) throws JSONException {

		object = new JSONObject();

		object.put("usercode", db.getFlowCode());

		object.put("usertype", 1);

		object.put("method", "queryapporderlist");

		object.put("pagesize", PAGE_SIZE);

		object.put("lastid", lastId);

		object.put("orderType", time);

		object.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(object, db.getMd5Key());

		object.put("sign", sign);

	}

	private void getOrderInfo(int order_type2, int lastid) {

		try {

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfo(order_type2, lastid);

				GlobalData.post(GlobalData.My_Order_Url, object.toString(), callBack);

			} else {

				ToastShow.ToastShowShort(mContext, "网络不可用");

			}

		} catch (Exception e) {

			e.printStackTrace();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");

		}
	}

	private Callback callBack = new Callback() {

		@Override
		public void onFailure(Request arg0, IOException arg1) {
			sendMsgByCheckNum(-1, null);
		}

		@Override
		public void onResponse(Response response) throws IOException {

			String resultStr = response.body().string();

			if (resultStr != null && resultStr.length() > 0) {

				try {

					object = new JSONObject(resultStr);

					code = object.getString("code");

					error = object.getString("error");

					if (!code.isEmpty() && !error.isEmpty() && code.equals("200")
							&& error.equalsIgnoreCase("success")) {

						result = object.getString("result");

						sendMsgByCheckNum(1, result);

					} else {// 查询失败

						sendMsgByCheckNum(0, null);

					}
				} catch (JSONException e) {

					e.printStackTrace();

					sendMsgByCheckNum(-1, null);

				}

			} else {

				sendMsgByCheckNum(0, null);
			}

		}
	};

	// 与主线程进行通信
	private void sendMsgByCheckNum(int flat, String resultStr) {

		Message msg = new Message();

		msg.what = flat;

		Bundle bundle = new Bundle();

		bundle.putString("result", result);

		msg.setData(bundle);

		handler.sendMessage(msg);
	}

	private void findView() {

		lv_order = (ListView) findViewById(R.id.content_view_my_order);

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_my_order_return);

		bt_time = (Button) findViewById(R.id.bt_my_order_type);

		lv_time = new ListView(this);

		ptrl = ((PullToRefreshLayout) findViewById(R.id.refresh_view_my_order));

		mContext = MyOrderActivity.this;

		order_type = getIntent().getIntExtra("type", 0);

		setButtonShow(order_type);

		db = new MyDBOperation(mContext);

		gson = new Gson();

	}

	private void setListen() {

		img_bt_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});

		bt_time.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showPopupWindow(-90, 20);

			}
		});

		lv_time.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				list_apporder.clear();

				order_type = (arg2 + 1) % 5;

				getOrderList(order_type);

				setButtonShow(order_type);

				popupWindow.dismiss();

				listAdapet.notifyDataSetChanged();

			}
		});

	}

	protected void setButtonShow(int arg2) {
		if (arg2 == 0) {
			bt_time.setText("全部订单");
		} else if (arg2 == 1) {
			bt_time.setText("待付款");
		} else if (arg2 == 2) {
			bt_time.setText("待激活");
		} else if (arg2 == 3) {
			bt_time.setText("已完成");
		} else {
			bt_time.setText("异常订单");
		}
	}

	private void initView() {

		list_apporder = new ArrayList<Apporderrecord>();

		listAdapet = new ListForMyOrder(mContext, list_apporder);

		lv_order.setAdapter(listAdapet);

		listAdapet.notifyDataSetChanged();

		myListener = new MyListener();

		ptrl.setOnRefreshListener(myListener);

	}

	private void showPopupWindow(int xoff, int yoff) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_for_order_time,
				new String[] { "待付款", "待激活", "已完成", "异常订单", "全部订单" });

		lv_time.setAdapter(adapter);

		Display display = getWindowManager().getDefaultDisplay();

		Point size = new Point();

		display.getSize(size);

		int width = size.x;

		popupWindow = new PopupWindow(this);

		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

		popupWindow.setWidth((width / 4));

		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(255, 2, 187, 210)));

		popupWindow.setOutsideTouchable(true);

		popupWindow.setFocusable(true);

		popupWindow.setContentView(lv_time);

		popupWindow.showAsDropDown(bt_time, 0, 0);

	}

	private class MyListener implements PullToRefreshLayout.OnRefreshListener {

		// 下滑加载完毕事件
		@Override
		public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {

		}

		// 上拉加载完毕事件
		@Override
		public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {

			int lastid = 0;

			if (list_apporder.size() != 0) {

				lastid = list_apporder.get(list_apporder.size() - 1).getId();

				for (int i = 0; i < list_apporder.size(); i++) {

					if (list_apporder.get(i).getId() < lastid) {

						lastid = list_apporder.get(i).getId();

					}

				}

			}

			getOrderInfo(order_type, lastid);

			// 加载操作
			handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 1:

						try {

							SetView(msg.getData().getString("result"));

							listAdapet.notifyDataSetChanged();

							lv_order.setSelection(list_apporder.size());

							pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);

						} catch (JSONException e) {

							e.printStackTrace();

							pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);

						}

						break;
					default:
						pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.FAIL);
						break;
					}
				}
			};

		}
	}

}
