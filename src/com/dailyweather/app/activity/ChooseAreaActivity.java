package com.dailyweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.dailyweather.app.model.City;
import com.dailyweather.app.model.CoolWeatherDB;
import com.dailyweather.app.model.County;
import com.dailyweather.app.model.Province;
import com.dailyweather.app.util.HttpCallBackListener;
import com.dailyweather.app.util.HttpUtil;
import com.dailyweather.app.util.Utility;

import android.R;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 遍历省市县数据的活动，搭配choose_area布局使用
 * 
 * @author wanjiali
 * 
 */
public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleTextView;
	private ListView listView;

	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	private List<Province> provinces;

	private List<City> cities;

	private List<County> counties;

	private Province selectedProvince;

	private City selectedCity;

	private County selectedCounty;

	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.dailyweather.app.R.layout.choose_area);// 不要忘记加上 包名

		listView = (ListView) findViewById(com.dailyweather.app.R.id.list_view);

		titleTextView = (TextView) findViewById(com.dailyweather.app.R.id.text_title);

		adapter = new ArrayAdapter<String>(ChooseAreaActivity.this,
				android.R.layout.simple_expandable_list_item_1, dataList);

		listView.setAdapter(adapter);

		coolWeatherDB = CoolWeatherDB.getInstance(this);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				/**
				 * 当前显示的 如果是 province 名称 点击 这个item 时候就查询这个province的城市名称。
				 * 如果当前显示的是城市的名称，那么点击这个item时候就查询这个city的县级名陈。
				 */
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinces.get(position);

					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cities.get(position);

					queryCounties();
				}

			}
		});

		/**
		 * 默认加载province信息。
		 */
		queryProvinces();

	}

	/**
	 * 查询city信息，并且加载到当前视图中来
	 */
	public void queryCities() {

		cities = coolWeatherDB.loadCity(selectedProvince.getId());

		if (cities.size() == 0) {
			dataList.clear();
			for (City c : cities) {
				dataList.add(c.getCityName());// 将更新的信息加载到视图中来
			}

			// 检查隐藏的数据是否有更新，有更新时候直接更新
			adapter.notifyDataSetChanged();

			// 设置初始时选择的位置，默认为第一个元素，也就是O的位置。
			listView.setSelection(0);

			// 设置TextView 中的文字，也即是当前的province名称。
			titleTextView
					.setText(selectedProvince.getProvinceName().toString());

			currentLevel = LEVEL_CITY;

		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * 查询province信息，并且加载到当前视图中来
	 */
	public void queryProvinces() {
		provinces = coolWeatherDB.loadProvinces();
		if (provinces.size() == 0) {
			dataList.clear();

			for (Province p : provinces) {
				// 将从数据库读出来的province 列表加载到 dataList 中从而显示到 listview中去。
				dataList.add(p.getProvinceName());

			}

			/*
			 * Notifies the attached observers that the underlying data has been
			 * changed and any View reflecting the data set should refresh
			 * itself.
			 */
			adapter.notifyDataSetChanged();

			listView.setSelection(0);

			titleTextView.setText("CHINA");

			currentLevel = LEVEL_PROVINCE;

		} else {
			// 如果从本地数据库不能直接读取province信息，就从服务器直接读取。
			queryFromServer(null, "province");
		}

	}

	/**
	 * 查询county信息，并且加载到当前视图中来
	 */
	public void queryCounties() {

		counties = coolWeatherDB.loadCounties(selectedCity.getId());
		if (counties.size() == 0) {
			dataList.clear();
			for (County c : counties) {
				dataList.add(c.getCountyName());
			}

			adapter.notifyDataSetChanged();

			listView.setSelection(0);

			titleTextView.setText(selectedCity.getCityName());

			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCounty.getCountyCode(), "county");

		}
	}

	/**
	 * 从服务器加载信息
	 * 
	 * @param code
	 * @param type
	 */
	public void queryFromServer(final String code, final String type) {

		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";

		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}

		// 显示进度条
		showProgressDialog();

		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;

				if ("province".equals(type)) {
					result = Utility.handleProvinceResponse(coolWeatherDB,
							response);

				} else if ("city".equals(type)) {
					result = Utility.handleCityResponse(coolWeatherDB,
							response, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountyResponse(coolWeatherDB,
							response, selectedCity.getId());
				}

				if (result) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}

						}
					});
				}

			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
								"load fails...", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	/**
	 * 显示进度条对话框
	 */
	public void showProgressDialog() {

		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("loading...");
			progressDialog.setCanceledOnTouchOutside(false);

		}
		progressDialog.show();
	}

	public void closeProgressDialog() {

		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	/**
	 * 作为返回按键的按下的响应， 按键按下之前如果是当前ListView 中显示的是 City的信息，就回到上一层Province
	 * 如果是County信息，就返回上一层City。
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (currentLevel == LEVEL_CITY) {
			queryProvinces();

		} else if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else {

			/*
			 * Call this when your activity is done and should be closed. The
			 * ActivityResult is propagated back to whoever launched you via
			 * onActivityResult().
			 */
			finish();// 销魂活动
		}

	}

}
