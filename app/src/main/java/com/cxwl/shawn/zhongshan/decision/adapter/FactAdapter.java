package com.cxwl.shawn.zhongshan.decision.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.dto.FactDto;
import com.cxwl.shawn.zhongshan.decision.util.CommonUtil;

import java.util.List;

/**
 * 实况列表
 */
public class FactAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private List<FactDto> mArrayList;
	private String RAIN1 = "1h降水",RAIN3 = "3h降水",RAIN6 = "6h降水",RAIN12 = "12h降水",RAIN24 = "24h降水",
			TEMP1 = "1h温度",WINDJD1 = "1h极大风",WINDJD24 = "24h极大风",WINDZD1 = "1h最大风",WINDZD24 = "24h最大风";

	private final class ViewHolder{
		TextView tvCity,tvDis,tvStationName,tvValue;
	}

	public FactAdapter(Context context, List<FactDto> mArrayList) {
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
			convertView = mInflater.inflate(R.layout.shawn_adapter_fact, null);
			mHolder = new ViewHolder();
			mHolder.tvCity = convertView.findViewById(R.id.tvCity);
			mHolder.tvDis = convertView.findViewById(R.id.tvDis);
			mHolder.tvStationName = convertView.findViewById(R.id.tvStationName);
			mHolder.tvValue = convertView.findViewById(R.id.tvValue);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		FactDto dto = mArrayList.get(position);

		if (!TextUtils.isEmpty(dto.city)) {
			mHolder.tvCity.setText(dto.city);
		}else {
			mHolder.tvCity.setText("");
		}

		if (!TextUtils.isEmpty(dto.dis)) {
			mHolder.tvDis.setText(dto.dis);
		}else {
			mHolder.tvDis.setText("");
		}

		if (!TextUtils.isEmpty(dto.stationName)) {
			mHolder.tvStationName.setText(dto.stationName);
		}else {
			mHolder.tvStationName.setText("");
		}

		if (TextUtils.equals(dto.columnName, RAIN1)) {//1小时降水
			mHolder.tvValue.setText(dto.rain+"");
		}else if (TextUtils.equals(dto.columnName, RAIN3)) {//3小时降水
			mHolder.tvValue.setText(dto.rain+"");
		}else if (TextUtils.equals(dto.columnName, RAIN6)) {//6小时降水
			mHolder.tvValue.setText(dto.rain+"");
		}else if (TextUtils.equals(dto.columnName, RAIN12)) {//12小时降水
			mHolder.tvValue.setText(dto.rain+"");
		}else if (TextUtils.equals(dto.columnName, RAIN24)) {//24小时降水
			mHolder.tvValue.setText(dto.rain+"");
		}else if (TextUtils.equals(dto.columnName, TEMP1)) {//1小时温度
			mHolder.tvValue.setText(dto.temp+"");
		}else if (TextUtils.equals(dto.columnName, WINDJD1)) {//1小时极大风
			mHolder.tvValue.setText(CommonUtil.getWindDirection(dto.windD)+"风 "+dto.windS);
		}else if (TextUtils.equals(dto.columnName, WINDJD24)) {//24小时极大风
			mHolder.tvValue.setText(CommonUtil.getWindDirection(dto.windD)+"风 "+dto.windS);
		}else if (TextUtils.equals(dto.columnName, WINDZD1)) {//1小时最大风
			mHolder.tvValue.setText(CommonUtil.getWindDirection(dto.windD)+"风 "+dto.windS);
		}else if (TextUtils.equals(dto.columnName, WINDZD24)) {//24小时最大风
			mHolder.tvValue.setText(CommonUtil.getWindDirection(dto.windD)+"风 "+dto.windS);
		}

		return convertView;
	}

}
