package com.jht.assistantmanager.adapter;

import java.util.List;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.activity.ActiveActivity;
import com.jht.assistantmanager.activity.ToCheckCodeTypeActivity;
import com.jht.assistantmanager.model.Apporderrecord;

public class ListForMyOrder extends BaseAdapter {

	private List<Apporderrecord> list_order;
	private Context mContext;

	public ListForMyOrder(Context context, List<Apporderrecord> list) {
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
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder viewHolder = null;

		if (view == null) {

			viewHolder = new ViewHolder();

			view = LayoutInflater.from(mContext).inflate(R.layout.item_for_my_order, null);

			viewHolder.orderNumber = (TextView) view.findViewById(R.id.tv_my_order_ordernumber);

			viewHolder.packageName = (TextView) view.findViewById(R.id.tv_my_order_ordercontent);

			viewHolder.money = (TextView) view.findViewById(R.id.tv_my_order_orderamount);

			viewHolder.orderTime = (TextView) view.findViewById(R.id.tv_my_order_ordertime);

			viewHolder.goodsNumber = (TextView) view.findViewById(R.id.tv_my_order_goodsnumber);

			viewHolder.memo = (TextView) view.findViewById(R.id.tv_my_order_ordermemo);

			viewHolder.error_decripe = (TextView) view.findViewById(R.id.tv_my_order_error_descripe);

			viewHolder.bt_click = (Button) view.findViewById(R.id.bt_my_order_click);

			view.setTag(viewHolder);
		}

		String str[] = list_order.get(position).getCodelist().split(",");

		String codelist = "\n\n";

		for (int i = 0; i < str.length; i++) {

			codelist += str[i] + "\n\n";

		}

		viewHolder = (ViewHolder) view.getTag();

		viewHolder.orderNumber.setText(list_order.get(position).getOrderid());

		viewHolder.packageName.setText(codelist);

		viewHolder.money.setText("￥" + list_order.get(position).getPrice());

		viewHolder.orderTime.setText(list_order.get(position).getCreatetime());

		viewHolder.memo.setText(list_order.get(position).getMemo());

		if (list_order.get(position).getOrderstate() == 0) {

			viewHolder.bt_click.setText("去支付");

			viewHolder.bt_click.setTextColor(Color.RED);

			viewHolder.bt_click.setBackgroundResource(R.drawable.bt_set_rebuy);

			viewHolder.goodsNumber.setText("共 " + str.length + " 张券卡  未付款: ");

			viewHolder.error_decripe.setVisibility(View.GONE);

		} else if (list_order.get(position).getOrderstate() == 1) {

			viewHolder.bt_click.setText("去激活");

			viewHolder.bt_click.setTextColor(Color.RED);

			viewHolder.bt_click.setBackgroundResource(R.drawable.bt_set_rebuy);

			viewHolder.goodsNumber.setText("共 " + str.length + " 张券卡  已付款: ");

			viewHolder.error_decripe.setVisibility(View.GONE);

		} else if (list_order.get(position).getOrderstate() == 2) {

			viewHolder.bt_click.setText("已完成");

			viewHolder.bt_click.setTextColor(R.color.gray);

			viewHolder.bt_click.setBackgroundColor(Color.TRANSPARENT);

			viewHolder.goodsNumber.setText("共 " + str.length + " 张券卡  已付款: ");

			viewHolder.error_decripe.setVisibility(View.GONE);

		} else if (list_order.get(position).getOrderstate() == 3) {

			viewHolder.bt_click.setText(list_order.get(position).getExcdescribe());

			viewHolder.bt_click.setTextColor(Color.RED);

			viewHolder.bt_click.setBackgroundColor(Color.TRANSPARENT);

			viewHolder.goodsNumber.setText("共 " + str.length + " 张券卡  已付款: ");

			viewHolder.error_decripe.setVisibility(View.VISIBLE);

			viewHolder.error_decripe.setTextColor(Color.RED);

		} else if (list_order.get(position).getOrderstate() == 4) {

			viewHolder.bt_click.setText(list_order.get(position).getExcdescribe());

			viewHolder.bt_click.setTextColor(Color.RED);

			viewHolder.bt_click.setBackgroundColor(Color.TRANSPARENT);

			viewHolder.goodsNumber.setText("共 " + str.length + " 张券卡  未付款: ");

			viewHolder.error_decripe.setVisibility(View.VISIBLE);

			viewHolder.error_decripe.setTextColor(Color.RED);
		}

		viewHolder.bt_click.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (list_order.get(position).getOrderstate() == 0) {

					Intent intent = new Intent(mContext, ToCheckCodeTypeActivity.class);

					intent.putExtra("orderid", list_order.get(position).getOrderid());

					intent.putExtra("codes", list_order.get(position).getCodelist());

					intent.putExtra("money", list_order.get(position).getPrice());

					intent.putExtra("memo", list_order.get(position).getMemo());

					intent.putExtra("uuid", list_order.get(position).getUuid());

					mContext.startActivity(intent);

				} else if (list_order.get(position).getOrderstate() == 1) {

					Intent intent = new Intent(mContext, ActiveActivity.class);

					intent.putExtra("orderid", list_order.get(position).getOrderid());

					intent.putExtra("codes", list_order.get(position).getCodelist());

					intent.putExtra("money", list_order.get(position).getPrice());

					intent.putExtra("memo", list_order.get(position).getMemo());

					intent.putExtra("uuid", list_order.get(position).getUuid());

					mContext.startActivity(intent);

				}

			}
		});

		return view;

	}

	private final static class ViewHolder {

		TextView orderNumber;

		TextView money;

		TextView goodsNumber;

		TextView orderTime;

		TextView packageName;

		TextView memo;

		TextView error_decripe;

		Button bt_click;

	}
}
