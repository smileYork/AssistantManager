package com.jht.assistantmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.model.OrderRecord;

import java.util.List;

public class ListForOrderAdapter extends BaseAdapter {

	private List<OrderRecord> list_order;
	private Context mContext;

	public ListForOrderAdapter(Context context, List<OrderRecord> list) {
		this.mContext = context;
		this.list_order = list;
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

			view = LayoutInflater.from(mContext).inflate(R.layout.item_order_list, null);

			viewHolder.id = (TextView) view.findViewById(R.id.tv_item_order_id);

			viewHolder.orderNumber = (TextView) view.findViewById(R.id.tv_item_order_number);

			viewHolder.packageName = (TextView) view.findViewById(R.id.tv_item_order_package_name);

			viewHolder.money = (TextView) view.findViewById(R.id.tv_item_order_money);

			viewHolder.orderTime = (TextView) view.findViewById(R.id.tv_item_order_time);

			view.setTag(viewHolder);
		}

		viewHolder = (ViewHolder) view.getTag();

		int id = position + 1;

		viewHolder.id.setText("" + id);

		viewHolder.orderNumber.setText(list_order.get(position).getCode());

		viewHolder.packageName.setText(list_order.get(position).getOrdercontent());

		viewHolder.money.setText("" + list_order.get(position).getCardprice());

		viewHolder.orderTime.setText(list_order.get(position).getSubmitdate());

		return view;

	}

	final static class ViewHolder {

		TextView id;
		TextView orderNumber;
		TextView packageName;
		TextView money;
		TextView orderTime;
	}
}
