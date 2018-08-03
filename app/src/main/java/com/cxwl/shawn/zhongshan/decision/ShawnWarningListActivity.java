package com.cxwl.shawn.zhongshan.decision;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cxwl.shawn.zhongshan.decision.adapter.WarningAdapter;
import com.cxwl.shawn.zhongshan.decision.dto.WarningDto;

import java.util.ArrayList;
import java.util.List;

/**
 * 预警列表
 * @author shawn_sun
 *
 */
public class ShawnWarningListActivity extends ShawnBaseActivity implements OnClickListener {
	
	private Context mContext;
	private EditText etSearch;
	private ImageView ivClear;
	private WarningAdapter mAdapter;
	private List<WarningDto> warningList = new ArrayList<>();//上个界面传过来的所有预警数据
	private List<WarningDto> showList = new ArrayList<>();//用于存放listview上展示的数据
	private List<WarningDto> searchList = new ArrayList<>();//用于存放搜索框搜索的数据

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shawn_activity_warning_list);
		mContext = this;
		initWidget();
		initListView();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		LinearLayout llBack = findViewById(R.id.llBack);
		llBack.setOnClickListener(this);
		TextView tvTitle = findViewById(R.id.tvTitle);
		tvTitle.setText("预警列表");
		etSearch = findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(watcher);
		ivClear = findViewById(R.id.ivClear);
		ivClear.setOnClickListener(this);

		warningList.clear();
		warningList.addAll(getIntent().getExtras().<WarningDto>getParcelableArrayList("warningList"));
		showList.clear();
		showList.addAll(warningList);
    }
	
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}
		@Override
		public void afterTextChanged(Editable arg0) {
			searchList.clear();
			if (!TextUtils.isEmpty(arg0.toString().trim())) {
				for (int i = 0; i < warningList.size(); i++) {
					WarningDto data = warningList.get(i);
					if (data.name.contains(arg0.toString().trim())) {
						searchList.add(data);
					}
				}
				ivClear.setVisibility(View.VISIBLE);
				showList.clear();
				showList.addAll(searchList);
				mAdapter.notifyDataSetChanged();
			}else {
				ivClear.setVisibility(View.GONE);
				showList.clear();
				showList.addAll(warningList);
				mAdapter.notifyDataSetChanged();
			}
		}
	};
	
	/**
	 * 初始化listview
	 */
	private void initListView() {
		ListView listView = findViewById(R.id.listView);
		mAdapter = new WarningAdapter(mContext, showList, false);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				WarningDto data = showList.get(arg2);
				Intent intentDetail = new Intent(mContext, ShawnWarningDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putParcelable("data", data);
				intentDetail.putExtras(bundle);
				startActivity(intentDetail);
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.llBack:
				finish();
				break;
			case R.id.ivClear:
				etSearch.setText("");
				break;

		default:
			break;
		}
	}

}
