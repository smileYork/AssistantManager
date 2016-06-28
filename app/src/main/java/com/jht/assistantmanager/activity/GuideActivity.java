package com.jht.assistantmanager.activity;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.ViewPagerAdapterForGuide;
import com.jht.assistantmanager.util.ActivityManage;
import com.jht.assistantmanager.util.GlobalData;

@SuppressLint("InflateParams")
public class GuideActivity extends Activity implements OnPageChangeListener {

	private ViewPager viewPager;

	private ViewPagerAdapterForGuide vpAdapter;

	private List<View> views;

	private ImageView[] dots;

	private ImageView imageView;

	private ViewGroup group;

	private Button bt_start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_guide);

		try {

			findView();

			// 初始化页面
			initViews();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void findView() {

		viewPager = (ViewPager) findViewById(R.id.vp_guide);

		group = (ViewGroup) findViewById(R.id.dot_group);

	}

	private void initViews() {

		LayoutInflater inflater = LayoutInflater.from(GuideActivity.this);

		views = new ArrayList<View>();

		views.add(inflater.inflate(R.layout.view_guide1, null));

		views.add(inflater.inflate(R.layout.view_guide4, null));

		views.add(inflater.inflate(R.layout.view_guide2, null));

		views.add(inflater.inflate(R.layout.view_guide3, null));

		// 初始化Adapter
		vpAdapter = new ViewPagerAdapterForGuide(views);

		dots = new ImageView[views.size()];

		for (int i = 0; i < views.size(); i++) {
			imageView = new ImageView(this);

			LinearLayout.LayoutParams params_linear = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params_linear.setMargins(20, 20, 20, 0);
			imageView.setLayoutParams(params_linear);

			dots[i] = imageView;
			if (i == 0) {
				imageView.setBackgroundResource(R.drawable.home_img_ratio_selected);
			} else {
				imageView.setBackgroundResource(R.drawable.home_img_ratio);
			}

			group.addView(imageView);
		}

		viewPager.setAdapter(vpAdapter);
		viewPager.setOnPageChangeListener(this);
		viewPager.setCurrentItem(0);
	}

	// 当滑动状态改变时调用
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int arg0) {
		// 设置底部小点选中状态
		setImageBackground(arg0 % views.size());

		if (arg0 % views.size() == views.size() - 1) {

			bt_start = (Button) findViewById(R.id.bt_guide3_start);

			bt_start.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					SharedPreferences preferences = getSharedPreferences(GlobalData.SHAREDPREFERENCES_NAME,
							MODE_PRIVATE);
					SharedPreferences.Editor sd = preferences.edit();

					sd.putBoolean("isFirstLoad", false);

					sd.commit();

					ActivityManage.startActivity(GuideActivity.this, LoginActivity.class);

					finish();
				}
			});
		}
	}

	private void setImageBackground(int selectItems) {
		for (int i = 0; i < dots.length; i++) {
			if (i == selectItems) {
				dots[i].setBackgroundResource(R.drawable.home_img_ratio_selected);
			} else {
				dots[i].setBackgroundResource(R.drawable.home_img_ratio);
			}
		}
	}

}