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
import com.cxwl.shawn.zhongshan.decision.dto.FactDto;

import java.util.List;

/**
 * 实况列表-城市筛选
 */
public class FactCityAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<FactDto> mArrayList;

	private final class ViewHolder{
		TextView tvName;
	}

	public FactCityAdapter(Context context, List<FactDto> mArrayList) {
		mContext = context;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_fact_city, null);
			mHolder = new ViewHolder();
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		FactDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.city)) {
			mHolder.tvName.setText(dto.city);
		}

		if (dto.isSelected) {
			mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.white));
			mHolder.tvName.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
		}else {
			mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_color3));
			mHolder.tvName.setBackgroundColor(Color.TRANSPARENT);
		}

		return convertView;
	}

}
