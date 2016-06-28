package com.jht.assistantmanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.model.CardForSearch;

import java.util.List;

public class ListForSaleAdapter extends BaseAdapter {

	private List<CardForSearch> list_order;
	private Context mContext;
	private List<CardForSearch> failtureCodeList;

	public ListForSaleAdapter(Context context, List<CardForSearch> list_order2, List<CardForSearch> failtureCodeList2) {
		this.mContext = context;
		this.list_order = list_order2;
		this.failtureCodeList = failtureCodeList2;
	}

	@Override
	public int getCount() {
		return list_order.size();
	}

	@Override
	public Object getItem(int position) {
		return list_order.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (view == null) {

			viewHolder = new ViewHolder();

			view = LayoutInflater.from(mContext).inflate(R.layout.item_sale_list, null);

			viewHolder.cardId = (TextView) view.findViewById(R.id.tv_item_sale_list_id);

			viewHolder.cardName = (TextView) view.findViewById(R.id.tv_item_sale_list_name);

			viewHolder.money = (TextView) view.findViewById(R.id.tv_item_sale_list_money);

			view.setTag(viewHolder);
		}

		viewHolder = (ViewHolder) view.getTag();

		String sameResult = existInFailture(list_order.get(position).getCode());

		if (sameResult != null) {

			view.setBackgroundColor(Color.RED);

			viewHolder.cardId.setText(list_order.get(position).getCode() + "(" + sameResult + ")");

		} else {

			viewHolder.cardId.setText(list_order.get(position).getCode());

		}

		viewHolder.cardName.setText(list_order.get(position).getStatusname());

		viewHolder.money.setText("" + list_order.get(position).getCardsmallprice());

		return view;

	}

	private String existInFailture(String code) {

		for (int i = 0; i < failtureCodeList.size(); i++) {

			if (failtureCodeList.get(i).getCode().equals(code)) {

				return failtureCodeList.get(i).getStatusname();

			}
		}
		return null;
	}

	final static class ViewHolder {

		TextView cardId;
		TextView cardName;
		TextView money;
	}
}
