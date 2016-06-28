package com.jht.assistantmanager.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.model.Goods;
import com.jht.assistantmanager.util.ToastShow;

public class Adapter_ListView_check extends BaseAdapter {

	private static HashMap<Integer, Boolean> isSelected;

	HolderView holderView = null;

	LayoutInflater inflater;

	private onCheckedChanged listener;

	private List<Goods> list_goods;

	private Goods gs = null;

	private Context context;

	private int selnum;

	private int hasChose = 0;

	public Adapter_ListView_check(Context context, List<Goods> list_goods, int selnum) {

		this.context = context;

		this.list_goods = list_goods;

		this.selnum = selnum;

		this.inflater = LayoutInflater.from(context);

		isSelected = new HashMap<Integer, Boolean>();

		initDate();

	}

	// 初始化isSelected的数据
	private void initDate() {
		for (int i = 0; i < list_goods.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	@Override
	public int getCount() {
		return (list_goods != null && list_goods.size() == 0) ? 0 : list_goods.size();
	}

	public Object getItem(int position) {
		return list_goods.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View currentView, ViewGroup parent) {
		gs = list_goods.get(position);

		if (currentView == null) {
			holderView = new HolderView();
			currentView = inflater.inflate(R.layout.adapter_listview_check, null);
			holderView.cart_goods_name = (TextView) currentView.findViewById(R.id.cart_goods_name);
			holderView.cb_choice = (CheckBox) currentView.findViewById(R.id.cb_choice);
			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}

		if (list_goods.size() != 0) {

			// 商品的名称
			holderView.cart_goods_name.setText(list_goods.get(position).getGoodsname());
			// 判断是否选中
			if (list_goods.get(position).getUse().equals("1")) {
				holderView.cb_choice.setVisibility(View.INVISIBLE);
				holderView.cart_goods_name.setTextColor(R.color.sandybrown);
			}

			holderView.cb_choice.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton arg0, boolean choice) {

					if (choice) {

						if (hasChose >= selnum) {

							ToastShow.ToastShowShort(context, "你最多只能选择" + selnum + "种套餐");

							arg0.setChecked(false);

						} else {

							hasChose++;

						}
					} else {

						hasChose--;
					}

				}
			});
		}
		return currentView;
	}

	public class HolderView {
		private TextView cart_goods_name;
		private CheckBox cb_choice;

	}

	public interface onCheckedChanged {
		public void getChoiceData(int position, boolean isChoice);

	}

	public void setOnCheckedChanged(onCheckedChanged listener) {
		this.listener = listener;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		Adapter_ListView_check.isSelected = isSelected;
	}

}