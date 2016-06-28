package com.jht.assistantmanager.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.AppManager;
import com.jht.assistantmanager.adapter.ListAdapterForManagerOrderList;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.model.OrderRecord;
import com.jht.assistantmanager.pulltorefresh.PullToRefreshLayout;
import com.jht.assistantmanager.util.Constants;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.MyDBOperation;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.PasswordUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class OrderManagerListActivity extends Activity {

	// 用于显示订单信息的listview
	private ListView lv_order;

	// 用于显示本月、本周、本日的listview
	private ListView lv_time;

	private ListAdapterForManagerOrderList listAdapet;

	private int PAGE_SIZE = 50;

	private TextView tv_title;

	private ImageButton img_bt_return;

	private PopupWindow popupWindow;

	private LinearLayout topchose;

	private Button bt_time;

	private int type;

	private Gson gson;

	private Dialog lodingDialog;

	private Context mContext;

	private MyDBOperation db;

	private String code;

	private String result;

	private String error;

	private JSONObject object;

	private List<OrderRecord> list;

	private OrderRecord tempOrder;

	private PullToRefreshLayout ptrl;

	private MyListener myListener;

	private Handler handler;

	private ImageButton buttonchose;

	private int time_type = 2;// 当前选择的日期，默认是本月

	private int startId = 0;

	private TextView tv_order_no;

	private TextView tv_order_content;

	private TextView tv_order_money;

	private TextView tv_order_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_order_list);

		AppManager.getInstance().addActivity(this);

		setTitleStyle();

		findView();

		initView();

		// 默认首先展示本月的 0 本日 1本周 2本月
		getOrderList();

		setListen();

	}

	private void setTitleStyle() {
		// 沉浸式设计
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

		}
	}

	private void getOrderList() {
		try {

			lodingDialog = LoadingDialog.createLoadingDialog(mContext, "正在加载数据,请稍等...");

			lodingDialog.show();

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				initRequestInfo();

				// 设定编码格式
				StringEntity entity = new StringEntity(object.toString(), HTTP.UTF_8);

				// 设置监听回调接口
				GlobalData.postEntityWithAbstructAddress(mContext, GlobalData.Orderall, entity, GlobalData.ContentType,
						searchHttpResponseHandler);
			} else {
				lodingDialog.dismiss();
				ToastShow.ToastShowShort(mContext, "不能联网，请检查手机网络状态");
			}

		} catch (Exception e) {

			lodingDialog.dismiss();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");
		}

	}

	private void initRequestInfo() throws JSONException {

		object = new JSONObject();

		object.put("usercode", db.getFlowCode());

		object.put("usertype", 1);

		object.put("method", "queryorder");

		switch (time_type) {
		case 0:
			// 本日
			object.put("startdate", Constants.getNowday());

			object.put("enddate", Constants.getNowday());

			break;
		case 1:
			// 本周
			object.put("startdate", Constants.getMonday());

			object.put("enddate", Constants.getNowday());

			break;

		case 2:
			// 本月
			object.put("startdate", Constants.getMonthFirstDay());

			object.put("enddate", Constants.getNowday());
			break;
		case 3:
			// 上一个月
			object.put("startdate", Constants.lastMonth(1));

			object.put("enddate", Constants.getNowday());
			break;

		default:
			break;
		}

		object.put("pagesize", PAGE_SIZE);

		object.put("type", type);

		object.put("time", time_type);

		object.put("startid", startId);

		object.put("timestamp", System.currentTimeMillis());

		String sign = PasswordUtil.generateSign(object, db.getMd5Key());

		object.put("sign", sign);

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

	// 与主线程进行通信
	private void sendMsgByCheckNum(int flat, String result) {

		Message msg = new Message();

		msg.what = flat;

		Bundle bundle = new Bundle();

		bundle.putString("result", result);

		msg.setData(bundle);

		handler.sendMessage(msg);
	}

	private void findView() {

		lv_order = (ListView) findViewById(R.id.content_view);

		tv_title = (TextView) findViewById(R.id.tv_order_list_title);

		img_bt_return = (ImageButton) findViewById(R.id.img_bt_order_list_return);

		bt_time = (Button) findViewById(R.id.bt_order_list_time);

		lv_time = new ListView(this);

		ptrl = ((PullToRefreshLayout) findViewById(R.id.refresh_view));

		topchose = (LinearLayout) findViewById(R.id.topchose);

		buttonchose = (ImageButton) findViewById(R.id.buttonchose);

		tv_order_no = (TextView) findViewById(R.id.tv_order_list_order_number);

		tv_order_content = (TextView) findViewById(R.id.tv_order_list_order_content);

		tv_order_money = (TextView) findViewById(R.id.tv_order_list_order_money);

		tv_order_time = (TextView) findViewById(R.id.tv_order_list_order_time);

		tv_order_no.setText("订单编号");

		tv_order_content.setText("订单内容");

		tv_order_money.setText("金额");

		tv_order_time.setText("下单时间");

	}

	protected void SetView(String result2) throws JSONException {

		JSONArray temJsonArray = new JSONArray(result2);

		for (int i = 0; i < temJsonArray.length(); i++) {

			tempOrder = gson.fromJson(temJsonArray.get(i).toString(), OrderRecord.class);

			list.add(tempOrder);

		}

	}

	private void initView() {

		mContext = OrderManagerListActivity.this;

		Calendar mycalendar = Calendar.getInstance(Locale.CHINA);

		Date mydate = new Date(); // 获取当前日期Date对象

		mycalendar.setTime(mydate);//// 为Calendar对象设置时间为当前日期

		db = new MyDBOperation(mContext);

		gson = new Gson();

		list = new ArrayList<OrderRecord>();

		listAdapet = new ListAdapterForManagerOrderList(mContext, list);

		lv_order.setAdapter(listAdapet);

		listAdapet.notifyDataSetChanged();

		type = getIntent().getIntExtra("type", 0);

		if (type == 0) {
			tv_title.setText("未发货");
		} else if (type == 1) {
			tv_title.setText("已发货");
		} else if (type == 2) {
			tv_title.setText("全部订单");
		} else {
			tv_title.setText("核销记录");
		}
		bt_time.setText("本月");
		myListener = new MyListener();
		ptrl.setOnRefreshListener(myListener);

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

		buttonchose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(-90, 20);
			}
		});

		topchose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(-90, 20);
			}
		});

		lv_time.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				time_type = arg2;

				startId = 0;

				if (arg2 == 0) {
					bt_time.setText("本日");
					list.clear();
					getOrderList();

				} else if (arg2 == 1) {
					bt_time.setText("本周");
					list.clear();

					getOrderList();
				} else if (arg2 == 2) {
					bt_time.setText("本月");
					list.clear();
					getOrderList();
				} else {
					bt_time.setText("全部");
					list.clear();
					getOrderList();
				}
				popupWindow.dismiss();
			}
		});

		lv_order.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Intent intent = new Intent();

				intent.setClass(mContext, OrderManagerDetailActivity.class);

				intent.putExtra("orderNumber", list.get(arg2).getOrderno());

				startActivity(intent);
			}
		});
	}

	private void showPopupWindow(int xoff, int yoff) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_for_order_time,
				new String[] { "本日", "本周", "本月", "全部" });

		lv_time.setAdapter(adapter);

		Display display = getWindowManager().getDefaultDisplay();

		Point size = new Point();

		display.getSize(size);

		int width = size.x;

		popupWindow = new PopupWindow(this);

		popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

		popupWindow.setWidth((width / 5) - 10);

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

			addOrderInfo(pullToRefreshLayout);

		}

		// 上拉加载完毕事件
		@Override
		public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {

			addOrderInfo(pullToRefreshLayout);

		}
	};

	private void getCardInfo() {

		try {

			if (NetWorkJudgeUtil.isConnect(mContext)) {

				if (list.size() != 0) {

					startId = list.get(list.size() - 1).getId();
				}

				for (int i = 0; i < list.size(); i++) {

					if (list.get(i).getId() <= startId) {

						startId = list.get(i).getId();
					}

				}

				initRequestInfo();

				GlobalData.post(GlobalData.Orderall, object.toString(), callBack);

			} else {

				ToastShow.ToastShowShort(mContext, "网络不可用");

			}

		} catch (Exception e) {

			e.printStackTrace();

			ToastShow.ToastShowShort(mContext, "服务器维护中，请稍后再试");

		}

	}

	public void addOrderInfo(final PullToRefreshLayout pullToRefreshLayout) {

		getCardInfo();

		Log.i("First", "上拉加载完毕事件");

		// 加载操作
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 千万别忘了告诉控件加载完毕了哦！
				switch (msg.what) {
				case 1:

					try {

						SetView(msg.getData().getString("result"));

						listAdapet.notifyDataSetChanged();

						lv_order.setSelection(list.size());

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

	private Callback callBack = new Callback() {

		@Override
		public void onFailure(Request arg0, IOException arg1) {

			sendMsgByCheckNum(0, null);

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

}