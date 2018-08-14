package com.cxwl.shawn.zhongshan.decision.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.dto.WindDto;

import java.util.List;

/**
 * 地图，更多里风场数据类型
 */
public class WindMapTypeAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<WindDto> mArrayList;
	public String windHeight;
	public boolean isGFS = true;

	private final class ViewHolder {
		TextView tvName;//预警信息名称
	}

	public WindMapTypeAdapter(Context context, List<WindDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_map_wind_type, null);
			mHolder = new ViewHolder();
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		WindDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.windHeight)) {
			if (dto.isGFS) {
				mHolder.tvName.setText("GFS("+dto.windHeight+"hPa)");
			}else {
				mHolder.tvName.setText("T639("+dto.windHeight+"hPa)");
			}
		}

		if (TextUtils.equals(windHeight, dto.windHeight) && dto.isGFS == isGFS) {
			mHolder.tvName.setBackgroundResource(R.drawable.shawn_bg_corner_map_press);
		}else {
			mHolder.tvName.setBackgroundResource(R.drawable.shawn_bg_corner_map);
		}

		return convertView;
	}

}
