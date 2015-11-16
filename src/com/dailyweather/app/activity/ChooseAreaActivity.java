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
 * ����ʡ�������ݵĻ������choose_area����ʹ��
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
		setContentView(com.dailyweather.app.R.layout.choose_area);// ��Ҫ���Ǽ��� ����

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
				 * ��ǰ��ʾ�� ����� province ���� ��� ���item ʱ��Ͳ�ѯ���province�ĳ������ơ�
				 * �����ǰ��ʾ���ǳ��е����ƣ���ô������itemʱ��Ͳ�ѯ���city���ؼ����¡�
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
		 * Ĭ�ϼ���province��Ϣ��
		 */
		queryProvinces();

	}

	/**
	 * ��ѯcity��Ϣ�����Ҽ��ص���ǰ��ͼ����
	 */
	public void queryCities() {

		cities = coolWeatherDB.loadCity(selectedProvince.getId());

		if (cities.size() == 0) {
			dataList.clear();
			for (City c : cities) {
				dataList.add(c.getCityName());// �����µ���Ϣ���ص���ͼ����
			}

			// ������ص������Ƿ��и��£��и���ʱ��ֱ�Ӹ���
			adapter.notifyDataSetChanged();

			// ���ó�ʼʱѡ���λ�ã�Ĭ��Ϊ��һ��Ԫ�أ�Ҳ����O��λ�á�
			listView.setSelection(0);

			// ����TextView �е����֣�Ҳ���ǵ�ǰ��province���ơ�
			titleTextView
					.setText(selectedProvince.getProvinceName().toString());

			currentLevel = LEVEL_CITY;

		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}
	}

	/**
	 * ��ѯprovince��Ϣ�����Ҽ��ص���ǰ��ͼ����
	 */
	public void queryProvinces() {
		provinces = coolWeatherDB.loadProvinces();
		if (provinces.size() == 0) {
			dataList.clear();

			for (Province p : provinces) {
				// �������ݿ��������province �б���ص� dataList �дӶ���ʾ�� listview��ȥ��
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
			// ����ӱ������ݿⲻ��ֱ�Ӷ�ȡprovince��Ϣ���ʹӷ�����ֱ�Ӷ�ȡ��
			queryFromServer(null, "province");
		}

	}

	/**
	 * ��ѯcounty��Ϣ�����Ҽ��ص���ǰ��ͼ����
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
	 * �ӷ�����������Ϣ
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

		// ��ʾ������
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
	 * ��ʾ�������Ի���
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
	 * ��Ϊ���ذ����İ��µ���Ӧ�� ��������֮ǰ����ǵ�ǰListView ����ʾ���� City����Ϣ���ͻص���һ��Province
	 * �����County��Ϣ���ͷ�����һ��City��
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
			finish();// ����
		}

	}

}
