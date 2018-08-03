package com.cxwl.shawn.zhongshan.decision.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.R;
import com.cxwl.shawn.zhongshan.decision.common.CONST;
import com.cxwl.shawn.zhongshan.decision.dto.WarningDto;
import com.cxwl.shawn.zhongshan.decision.util.CommonUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 预警列表
 */
public class WarningAdapter extends BaseAdapter {
	
	private Context mContext;
	private LayoutInflater mInflater;
	private List<WarningDto> mArrayList;
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
	private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
	private boolean isMarkerCell;

	private final class ViewHolder {
		ImageView imageView;//预警icon
		TextView tvName,tvTime;//预警信息名称
	}
	
	private ViewHolder mHolder = null;
	
	public WarningAdapter(Context context, List<WarningDto> mArrayList, boolean isMarkerCell) {
		mContext = context;
		this.isMarkerCell = isMarkerCell;
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
			if (!isMarkerCell) {
				convertView = mInflater.inflate(R.layout.shawn_adapter_warning1, null);
			}else {
				convertView = mInflater.inflate(R.layout.shawn_adapter_warning2, null);
			}
			mHolder = new ViewHolder();
			mHolder.imageView = convertView.findViewById(R.id.imageView);
			mHolder.tvName = convertView.findViewById(R.id.tvName);
			mHolder.tvTime = convertView.findViewById(R.id.tvTime);
			convertView.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) convertView.getTag();
		}
		
		WarningDto dto = mArrayList.get(position);
		
        Bitmap bitmap = null;
		if (dto.color.equals(CONST.blue[0])) {
			bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.blue[1]+CONST.imageSuffix);
			if (bitmap == null) {
				bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.blue[1]+CONST.imageSuffix);
			}
		}else if (dto.color.equals(CONST.yellow[0])) {
			bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.yellow[1]+CONST.imageSuffix);
			if (bitmap == null) {
				bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.yellow[1]+CONST.imageSuffix);
			}
		}else if (dto.color.equals(CONST.orange[0])) {
			bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.orange[1]+CONST.imageSuffix);
			if (bitmap == null) {
				bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.orange[1]+CONST.imageSuffix);
			}
		}else if (dto.color.equals(CONST.red[0])) {
			bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+dto.type+CONST.red[1]+CONST.imageSuffix);
			if (bitmap == null) {
				bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.red[1]+CONST.imageSuffix);
			}
		}
		if (bitmap == null) {
			bitmap = CommonUtil.getImageFromAssetsFile(mContext,"warning/"+"default"+CONST.imageSuffix);
		}
		mHolder.imageView.setImageBitmap(bitmap);
		
		if (!TextUtils.isEmpty(dto.name)) {
			if (!isMarkerCell) {
				mHolder.tvName.setText(dto.name);
			}else {
				mHolder.tvName.setText(dto.name.replace("发布", "发布\n"));
			}
		}
		
		try {
			mHolder.tvTime.setText(sdf2.format(sdf1.parse(dto.time)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return convertView;
	}

}
