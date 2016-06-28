package com.jht.assistantmanager.activity;

import java.util.Calendar;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.AppManager;
import com.jht.assistantmanager.util.ActionSheetDialog;
import com.jht.assistantmanager.util.MyScrollView;
import com.jht.assistantmanager.util.homebutton.HomeButton;
import com.jht.assistantmanager.util.homebutton.HomeClickListener;

@SuppressLint("HandlerLeak")
public class TestUIActivity extends BaseActivity implements OnGestureListener, OnTouchListener, OnClickListener {
	private TextView date_TextView;
	private ViewFlipper viewFlipper;
	private boolean showNext = true;
	private boolean isRun = true;
	private int currentPage = 0;
	private final int SHOW_NEXT = 0011;
	private static final int FLING_MIN_DISTANCE = 50;
	private static final int FLING_MIN_VELOCITY = 0;
	private GestureDetector mGestureDetector;
	private LinearLayout home_img_bn_Layout, cam_img_bn_layout, show_img_bn_layout;
	private HomeButton cardSearch, cardCheck, cardBuy, cardRecord, cardOrder;
	private ImageView mViewFliper_iv;
	private static final int JUMP_FLAG1 = 1;
	private static final int JUMP_FLAG2 = 2;
	private static final int JUMP_FLAG3 = 3;
	private static final int JUMP_FLAG4 = 4;
	private static final int JUMP_FLAG5 = 5;
	private int input_type;
	private Intent intent;

	private SharedPreferences sharePreference;

	public static Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case JUMP_FLAG1:
				Jump_page(1);
				break;
			case JUMP_FLAG2:
				Jump_page(2);
				break;
			case JUMP_FLAG3:
				Jump_page(3);
				break;
			case JUMP_FLAG4:
				Jump_page(4);
				break;
			case JUMP_FLAG5:
				Jump_page(5);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main_home);
		AppManager.getInstance().addActivity(this);
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
		findViewById();
		initView();

	}

	@SuppressWarnings("deprecation")
	@Override
	protected void findViewById() {
		date_TextView = (TextView) findViewById(R.id.home_date_tv);
		date_TextView.setText(getDate());
		home_img_bn_Layout = (LinearLayout) findViewById(R.id.bottom_home_layout_ly);
		cam_img_bn_layout = (LinearLayout) findViewById(R.id.bottom_cam_layout_ly);
		show_img_bn_layout = (LinearLayout) findViewById(R.id.bottom_show_layout_ly);

		viewFlipper = (ViewFlipper) findViewById(R.id.mViewFliper_vf);
		mGestureDetector = new GestureDetector(this);
		viewFlipper.setOnTouchListener(this);
		viewFlipper.setLongClickable(true);
		viewFlipper.setOnClickListener(clickListener);
		displayRatio_selelct(currentPage);
		MyScrollView myScrollView = (MyScrollView) findViewById(R.id.viewflipper_scrollview);
		myScrollView.setOnTouchListener(onTouchListener);
		myScrollView.setGestureDetector(mGestureDetector);
		thread.start();
		home_img_bn_Layout.setSelected(true);
		cardSearch = (HomeButton) findViewById(R.id.cardSearch);
		cardCheck = (HomeButton) findViewById(R.id.cardCheck);
		cardBuy = (HomeButton) findViewById(R.id.cardBuy);
		cardRecord = (HomeButton) findViewById(R.id.cardRecord);
		cardOrder = (HomeButton) findViewById(R.id.cardOrder);
		mViewFliper_iv = (ImageView) findViewById(R.id.mViewFliper_iv);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels; // 屏幕高度（像素）
		System.out.println("width=" + width);
		System.out.println("height=" + height);
		intent = new Intent();
		sharePreference = getSharedPreferences("system_setting", Activity.MODE_PRIVATE);
		input_type = sharePreference.getInt("input_type", 0);

		// 计算轮播图控件的宽和高度
		ViewTreeObserver vto = mViewFliper_iv.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mViewFliper_iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mViewFliper_iv.getHeight();
				mViewFliper_iv.getWidth();
				System.out.println(mViewFliper_iv.getHeight() + "  " + mViewFliper_iv.getWidth());
			}
		});
	}

	@Override
	protected void initView() {
		// set_ImageButton.setOnClickListener(this);
		home_img_bn_Layout.setOnClickListener(clickListener_home);
		cam_img_bn_layout.setOnClickListener(clickListener_cam);
		show_img_bn_layout.setOnClickListener(clickListener_show);
		// titlebar.setText(R.string.home_tv);
		/* 券卡查询图片的点击事件 */
		cardSearch.setOnHomeClick(new HomeClickListener() {
			@Override
			public void onclick() {
				// CommonUtils.toastInfo("券卡查询");
				openActivityForSearch();
			}
		});
		/* 券卡核销图片的点击事件 */
		cardCheck.setOnHomeClick(new HomeClickListener() {
			@Override
			public void onclick() {
				// CommonUtils.toastInfo("券卡核销");
				openActivityForGive();

			}
		});
		/* 券卡销售图片的点击事件 */
		cardBuy.setOnHomeClick(new HomeClickListener() {
			@Override
			public void onclick() {
				// CommonUtils.toastInfo("券卡销售");
				// 销售前清除掉本地添加在列表中的数据
				openActivityForSale();
			}
		});
		/* 券卡记录图片的点击事件 */
		cardRecord.setOnHomeClick(new HomeClickListener() {
			@Override
			public void onclick() {
				// CommonUtils.toastInfo("操作记录");
				openActivityForOrderList();
			}
		});

		/* 券卡查询图片的点击事件 */
		cardOrder.setOnHomeClick(new HomeClickListener() {
			@Override
			public void onclick() {

				openActivity(OrderManageActivity.class, -1);

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.title_set_bn:
		// CommonUtils.toastInfo("设置属性");
		// break;

		default:
			break;
		}
	}

	/* 主页轮播图图片的点击事件 */
	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// toastInfo("点击事件");

		}
	};

	private OnTouchListener onTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return mGestureDetector.onTouchEvent(event);
		}
	};

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_NEXT:
				if (showNext) {
					showNextView();
				} else {
					showPreviousView();
				}
				break;

			default:
				break;
			}
		}
	};

	private String getDate() {
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		int w = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0) {
			w = 0;
		}
		int month = c.get(Calendar.MONTH) + 1;
		String mDate = c.get(Calendar.YEAR) + "年" + month + "月" + c.get(Calendar.DATE) + "日  " + weekDays[w];
		return mDate;
	}

	private OnClickListener clickListener_home = new OnClickListener() {

		@Override
		public void onClick(View v) {
			home_img_bn_Layout.setSelected(true);
			cam_img_bn_layout.setSelected(false);
			show_img_bn_layout.setSelected(false);
		}
	};

	private OnClickListener clickListener_cam = new OnClickListener() {

		@Override
		public void onClick(View v) {
			home_img_bn_Layout.setSelected(true);
			cam_img_bn_layout.setSelected(false);
			show_img_bn_layout.setSelected(false);

			// 点击出现弹窗
			new ActionSheetDialog(TestUIActivity.this).builder().setTitle("请选择操作").setCancelable(false)
					.setCanceledOnTouchOutside(false)
					.addSheetItem("全部订单", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {

							openActivity(MyOrderActivity.class, 0);

						}
					}).addSheetItem("待付款", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {

							openActivity(MyOrderActivity.class, 1);

						}
					}).addSheetItem("待激活", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {
							openActivity(MyOrderActivity.class, 2);
						}
					})

					.addSheetItem("已完成", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
						@Override
						public void onClick(int which) {

							openActivity(MyOrderActivity.class, 3);

						}
					}).show();

		}
	};

	// 更多选项卡的点击事件
	private OnClickListener clickListener_show = new OnClickListener() {

		@Override
		public void onClick(View v) {
			home_img_bn_Layout.setSelected(true);
			cam_img_bn_layout.setSelected(false);
			show_img_bn_layout.setSelected(false);
			openActivity(MoreActivity.class, -1);

		}
	};

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		Log.e("view", "onFling");
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			Log.e("fling", "left");
			showNextView();
			showNext = true;
			return true;
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			Log.e("fling", "right");
			showPreviousView();
			showNext = false;
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	Thread thread = new Thread() {

		@Override
		public void run() {
			while (isRun) {
				try {
					Thread.sleep(1000 * 8);
					Message msg = new Message();
					msg.what = SHOW_NEXT;
					mHandler.sendMessage(msg);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};

	private void showNextView() {

		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
		viewFlipper.showNext();
		currentPage++;
		if (currentPage == viewFlipper.getChildCount()) {
			displayRatio_normal(currentPage - 1);
			currentPage = 0;
			displayRatio_selelct(currentPage);
		} else {
			displayRatio_selelct(currentPage);
			displayRatio_normal(currentPage - 1);
		}
		// Log.e("currentPage", currentPage + "");
	}

	private void showPreviousView() {
		displayRatio_selelct(currentPage);
		viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
		viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
		viewFlipper.showPrevious();
		currentPage--;
		if (currentPage == -1) {
			displayRatio_normal(currentPage + 1);
			currentPage = viewFlipper.getChildCount() - 1;
			displayRatio_selelct(currentPage);
		} else {
			displayRatio_selelct(currentPage);
			displayRatio_normal(currentPage + 1);
		}
		// Log.e("currentPage", currentPage + "");
	}

	private void displayRatio_selelct(int id) {
		int[] ratioId = { R.id.home_ratio_img_04, R.id.home_ratio_img_03, R.id.home_ratio_img_02,
				R.id.home_ratio_img_01 };
		ImageView img = (ImageView) findViewById(ratioId[id]);
		img.setSelected(true);
	}

	private void displayRatio_normal(int id) {
		int[] ratioId = { R.id.home_ratio_img_04, R.id.home_ratio_img_03, R.id.home_ratio_img_02,
				R.id.home_ratio_img_01 };
		ImageView img = (ImageView) findViewById(ratioId[id]);
		img.setSelected(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			isRun = false;
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		finish();
		super.onDestroy();
	}

	public static void Jump_page(int t) {
		switch (t) {
		case 1:
			// CommonUtils.toastInfo("券卡核销");

			break;
		case 2:

			break;
		case 3:
			// CommonUtils.toastInfo("券卡销售");
			break;
		case 4:
			// CommonUtils.toastInfo("订单管理");
			break;
		case 5:
			// CommonUtils.toastInfo("操作记录");
			break;
		default:
			break;

		}

	}

	protected void openActivity(Class<?> aimClass, int type) {
		intent.setClass(TestUIActivity.this, aimClass);

		intent.putExtra("type", type);

		startActivity(intent);

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

}