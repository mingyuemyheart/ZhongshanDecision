package com.cxwl.shawn.zhongshan.decision.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.dto.ValueDto;

import java.util.List;

/**
 * 数值预报
 */
public class ValueForecastAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<ValueDto> mArrayList;

	private final class ViewHolder{
		TextView tvName;
	}

	public ValueForecastAdapter(Context context, List<ValueDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.shawn_adapter_value_forecast, null);
			mHolder = new ViewHolder();
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		ValueDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.time)) {
			mHolder.tvName.setText(dto.time);
		}

		if (dto.isSelected) {
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
		}else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}

		return convertView;
	}

}
