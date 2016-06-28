package com.jht.assistantmanager.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.AppManager;
import com.jht.assistantmanager.adapter.ViewPagerAdapterForMain;
import com.jht.assistantmanager.util.ActionSheetDialog;
import com.jht.assistantmanager.util.ActionSheetDialog.OnSheetItemClickListener;

public class ShowMainActivity extends Activity {

	private RelativeLayout rl_bt1;

	private RelativeLayout rl_bt2;

	private RelativeLayout rl_bt3;

	private RelativeLayout rl_bt4;

	private RelativeLayout rl_bt_mid;

	private ViewPager viewPager;

	private ViewPagerAdapterForMain vpAdapter;

	// 轮播图图片列表
	private List<View> views;

	// 圆点显示
	private ImageView[] dots;

	private String[] title = { "订单管理", "券卡查询", "券卡核销", "券卡销售" };

	private ImageView imageView;

	private ViewGroup group;

	private int cycleStep = 0;

	private boolean isRun = true;

	private TextView tv_vp_title;

	private RadioButton rb_more;

	private RadioButton rb_main;

	private RadioButton rb_order_manage;

	private Intent intent;

	private SharedPreferences sharePreference;

	private int input_type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_show);
		 AppManager.getInstance().addActivity(this);
		findView();

		initView();

		new Thread(thread).start();

		clickListen();

	}

	// 轮播图定时切换图片
	Thread thread = new Thread(new Runnable() {

		@Override
		public void run() {

			while (isRun) {

				try {

					Thread.sleep(6000);

					cycleStep = (cycleStep + 1) % views.size();

					Message msgMessage = new Message();

					msgMessage.what = cycleStep;

					mHandler.sendMessage(msgMessage);

				} catch (InterruptedException e) {

					e.printStackTrace();

				}
			}
		}

	});

	private void clickListen() {

		// 券卡搜索点击事件
		rl_bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				openActivityForSearch();
			}
		});

		// 订单管理点击事件
		rl_bt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				openActivity(OrderManageActivity.class, -1);

			}
		});

		// 券卡核销点击事件
		rl_bt3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				openActivityForGive();

			}
		});

		// 操作记录店家事件
		rl_bt4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				openActivityForOrderList();

			}
		});

		// 券卡销售点击事件
		rl_bt_mid.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				openActivityForSale();

			}
		});

		// 轮播图滑动事件
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				setImageBackground(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		// 更多点击事件
		rb_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				openActivity(MoreActivity.class, -1);

			}
		});

		// 券卡管理点击事件
		rb_order_manage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// 点击出现弹窗
				new ActionSheetDialog(ShowMainActivity.this).builder().setTitle("请选择操作").setCancelable(false)
						.setCanceledOnTouchOutside(false)
						.addSheetItem("待付款", ActionSheetDialog.SheetItemColor.Blue, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

								openActivity(MyOrderActivity.class, 1);

							}
						}).addSheetItem("待激活", ActionSheetDialog.SheetItemColor.Blue, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {
								openActivity(MyOrderActivity.class, 2);
							}
						})

						.addSheetItem("已完成", ActionSheetDialog.SheetItemColor.Blue, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

								openActivity(MyOrderActivity.class, 3);

							}
						})

						.addSheetItem("异常订单", ActionSheetDialog.SheetItemColor.Blue, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

								openActivity(MyOrderActivity.class, 4);

							}
						}).addSheetItem("全部订单", ActionSheetDialog.SheetItemColor.Blue, new OnSheetItemClickListener() {
							@Override
							public void onClick(int which) {

								openActivity(MyOrderActivity.class, 0);

							}
						}).show();

			}
		});

	}

	protected void openActivityForSearch() {

		if (input_type == 0) {

			openActivity(CaptureActivity.class, 0);

		} else {

			openActivity(CardSearchActivity.class, 0);

		}

	}

	protected void openActivityForSale() {

		if (input_type == 0) {

			openActivity(CaptureActivity.class, 2);

		} else {

			openActivity(CardSaleSearchActivity.class, 2);
		}

	}

	protected void openActivityForGive() {

		if (input_type == 0) {

			openActivity(CaptureActivity.class, 1);

		} else {

			openActivity(CardSearchActivity.class, 1);

		}

	}

	protected void openActivityForOrderList() {

		openActivity(OrderListActivity.class, 3);

	}

	protected void openActivity(Class<?> aimClass, int type) {

		intent.setClass(ShowMainActivity.this, aimClass);

		intent.putExtra("type", type);

		startActivity(intent);

	}

	private void findView() {

		viewPager = (ViewPager) findViewById(R.id.vp_main_show);

		group = (ViewGroup) findViewById(R.id.dot_group_main);

		rl_bt1 = (RelativeLayout) findViewById(R.id.rl_main_show_first);

		rl_bt2 = (RelativeLayout) findViewById(R.id.rl_main_show_second);

		rl_bt3 = (RelativeLayout) findViewById(R.id.rl_main_show_third);

		rl_bt4 = (RelativeLayout) findViewById(R.id.rl_main_show_forth);

		rl_bt_mid = (RelativeLayout) findViewById(R.id.rl_main_show_mid);

		tv_vp_title = (TextView) findViewById(R.id.tv_vp_title);

		rb_more = (RadioButton) findViewById(R.id.rbSetting);

		rb_main = (RadioButton) findViewById(R.id.rbService);

		rb_order_manage = (RadioButton) findViewById(R.id.rbBlackList);

	}

	// 初始化轮播图和显示界面
	@SuppressLint("InflateParams")
	private void initView() {

		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();

		views.add(inflater.inflate(R.layout.view_main_page1, null));

		views.add(inflater.inflate(R.layout.view_main_page2, null));

		views.add(inflater.inflate(R.layout.view_main_page3, null));

		// 初始化Adapter
		vpAdapter = new ViewPagerAdapterForMain(views);

		dots = new ImageView[views.size()];

		for (int i = 0; i < views.size(); i++) {

			imageView = new ImageView(this);

			LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);

			params_linear.setMargins(7, 10, 7, 10);

			imageView.setLayoutParams(params_linear);

			params_linear.setMargins(7, 10, 7, 10);

			dots[i] = imageView;

			if (i == 0) {

				imageView.setBackgroundResource(R.drawable.home_img_ratio_selected);

			} else {

				imageView.setBackgroundResource(R.drawable.home_img_ratio);

			}

			group.addView(imageView);
		}

		viewPager.setAdapter(vpAdapter);

		viewPager.setCurrentItem(0);

		tv_vp_title.setText(title[0]);

		intent = new Intent();

		sharePreference = getSharedPreferences("system_setting", Activity.MODE_PRIVATE);

		input_type = sharePreference.getInt("input_type", 0);

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			setImageBackground(msg.what);

		}
	};

	private void setImageBackground(int selectItems) {

		viewPager.setCurrentItem(selectItems);

		tv_vp_title.setText(title[selectItems]);

		for (int i = 0; i < dots.length; i++) {

			if (i == selectItems) {

				dots[i].setBackgroundResource(R.drawable.home_img_ratio_selected);

			} else {

				dots[i].setBackgroundResource(R.drawable.home_img_ratio);

			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isRun = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		rb_main.setChecked(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		input_type = sharePreference.getInt("input_type", 0);
	}

}