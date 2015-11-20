package com.dailyweather.app.activity;

import com.dailyweather.app.util.HttpCallBackListener;
import com.dailyweather.app.util.HttpUtil;
import com.dailyweather.app.util.Utility;

import android.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {

	private LinearLayout weatherInfoLayout;

	private TextView cityNameTextView;

	private TextView publishTextView;

	private TextView weaherDespTextView;

	private TextView temp1TextView;

	private TextView temp2TextView;

	private TextView currentDateTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(com.dailyweather.app.R.layout.weather_layout);

		weatherInfoLayout = (LinearLayout) findViewById(com.dailyweather.app.R.id.weather_info_layout_ll);
		
		cityNameTextView = (TextView) findViewById(com.dailyweather.app.R.id.city_name);

		publishTextView = (TextView) findViewById(com.dailyweather.app.R.id.publish_text);

		temp1TextView = (TextView) findViewById(com.dailyweather.app.R.id.temp1);

		temp2TextView = (TextView) findViewById(com.dailyweather.app.R.id.temp2);

		weaherDespTextView = (TextView) findViewById(com.dailyweather.app.R.id.weather_desp);
		
		currentDateTextView = (TextView) findViewById(com.dailyweather.app.R.id.weather_date);

		String countyCodeString = getIntent().getStringExtra("county_code");

		if (TextUtils.isEmpty(countyCodeString)) {
			publishTextView.setText("Í¬²½ÖÐ");

			weatherInfoLayout.setVisibility(View.INVISIBLE);

			cityNameTextView.setVisibility(View.INVISIBLE);

			queryWeatherCode(countyCodeString);

		} else {
			showWeather();
		}
	}

	public void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";

		queryFromServer(address, "countyCode");

	}

	public void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	public void showWeather() {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(WeatherActivity.this);

		cityNameTextView.setText( sharedPreferences.getString( "city_name",  ""));
		temp1TextView.setText(sharedPreferences.getString( "temp1",  ""));
		temp2TextView.setText(sharedPreferences.getString( "temp2", ""));
		weaherDespTextView.setText(sharedPreferences.getString( "weather_desp", ""));
		
		publishTextView.setText(sharedPreferences.getString( "ptime",  ""));
		
		currentDateTextView.setText(sharedPreferences.getString( "current_date",  ""));
		
		weatherInfoLayout.setVisibility(View.VISIBLE);
		
		cityNameTextView.setVisibility(View.VISIBLE);
		
	}

	private void queryFromServer(final String address, final String code) {
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if ("countyCode".equals(code)) {

					if (!TextUtils.isEmpty(response)) {
						String[] arrayString = response.split("\\|");

						if ((arrayString != null) && (arrayString.length == 2)) {

							String weatherCodeString = arrayString[1];
							queryWeatherInfo(weatherCodeString);

						}
					}
				}

				if ("weatherCode".equals(code)) {
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub

			}
		});
	}
}
