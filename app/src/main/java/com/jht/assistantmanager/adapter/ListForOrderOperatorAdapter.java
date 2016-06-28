package com.jht.assistantmanager.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.model.OrderForOperator;

import java.util.List;

public class ListForOrderOperatorAdapter extends BaseAdapter {

	private List<OrderForOperator> list_order;
	private Context mContext;

	public ListForOrderOperatorAdapter(Context context, List<OrderForOperator> list_order) {
		this.mContext = context;
		this.list_order = list_order;
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

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if (view == null) {

			viewHolder = new ViewHolder();

			view = LayoutInflater.from(mContext).inflate(R.layout.item_operator_list, null);

			viewHolder.cardNumber = (TextView) view.findViewById(R.id.tv_item_operator_card_id);

			viewHolder.packageName = (TextView) view.findViewById(R.id.tv_item_operator_package_name);

			viewHolder.operator_type = (TextView) view.findViewById(R.id.tv_item_operator_type);

			viewHolder.operatorTime = (TextView) view.findViewById(R.id.tv_item_operator_time);

			view.setTag(viewHolder);
		}

		viewHolder = (ViewHolder) view.getTag();

		viewHolder.cardNumber.setText(list_order.get(position).getId());

		viewHolder.packageName.setText(list_order.get(position).getPackageName());

		viewHolder.operator_type.setText("" + list_order.get(position).getOperator_type());

		viewHolder.operatorTime.setText(list_order.get(position).getOperator_time());

		return view;

	}

	final static class ViewHolder {

		TextView cardNumber;
		TextView packageName;
		TextView operator_type;
		TextView operatorTime;
	}
}
