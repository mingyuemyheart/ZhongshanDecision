package com.cxwl.shawn.zhongshan.decision.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.dto.WarningDto;

import java.util.HashMap;
import java.util.List;

/**
 * 预警筛选
 */
public class WarningListAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<WarningDto> mArrayList;
	private int dataType = 1;//1为预警种类，2为预警等级，3为发布区域
	public HashMap<Integer, Boolean> isSelected = new HashMap<>();
	private int totalCount = 0;
	
	private final class ViewHolder {
		TextView tvName;//预警信息名称
	}
	
	public WarningListAdapter(Context context, List<WarningDto> mArrayList, int dataType) {
		mContext = context;
		this.dataType = dataType;
		this.mArrayList = mArrayList;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		for (int i = 0; i < mArrayList.size(); i++) {
			if (i == 0) {
				isSelected.put(i, true);
			}else {
				isSelected.put(i, false);
			}
			totalCount += mArrayList.get(i).count;
		}
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_warning_list_screen, null);
			mHolder = new ViewHolder();
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		WarningDto dto = mArrayList.get(position);
		if (dataType == 1) {
			if (!TextUtils.equals(dto.type, "999999")) {
				mHolder.tvName.setText(dto.name+"("+dto.count+")");
			}else {
				mHolder.tvName.setText(dto.name+"("+totalCount+")");
			}
		}else if (dataType == 2) {
			if (!TextUtils.equals(dto.color, "999999")) {
				mHolder.tvName.setText(dto.name+"("+dto.count+")");
			}else {
				mHolder.tvName.setText(dto.name+"("+totalCount+")");
			}
		}else if (dataType == 3) {
			if (!TextUtils.equals(dto.cId, "999999")) {
				mHolder.tvName.setText(dto.name+"("+dto.count+")");
			}else {
				mHolder.tvName.setText(dto.name+"("+totalCount+")");
			}
		}

		
		if (isSelected.get(position)) {
			mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.white));
			mHolder.tvName.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
		}else {
			mHolder.tvName.setTextColor(mContext.getResources().getColor(R.color.text_color3));
			mHolder.tvName.setBackgroundColor(mContext.getResources().getColor(R.color.white));
		}
		
		return convertView;
	}

}
