package com.jht.assistantmanager.adapter;

import java.util.List;

import com.example.first.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChoseAdapter extends BaseAdapter {

	private List<String> list_code;
	private Context mContext;

	public ChoseAdapter(Context context, List<String> list_order) {
		this.mContext = context;
		this.list_code = list_order;
	}

	@Override
	public int getCount() {
		return list_code.size();
	}

	@Override
	public Object getItem(int position) {
		return list_code.get(position);
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
			view = LayoutInflater.from(mContext).inflate(R.layout.adapter_listview_chose, null);
			viewHolder.cardName = (TextView) view.findViewById(R.id.cart_goods_chosename);

			view.setTag(viewHolder);
		}

		viewHolder = (ViewHolder) view.getTag();

		viewHolder.cardName.setText(list_code.get(position).toString());

		return view;

	}

	final static class ViewHolder {
		TextView cardName;

	}
}