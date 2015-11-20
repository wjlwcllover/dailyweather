package com.dailyweather.app.util;

import java.net.ResponseCache;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.dailyweather.app.model.City;
import com.dailyweather.app.model.CoolWeatherDB;
import com.dailyweather.app.model.County;
import com.dailyweather.app.model.Province;

/**
 * 解析和处理服务器返回的数据
 * 
 * @author wanjiali
 * 
 */
public class Utility {

	/**
	 * 处理省份的编号格式数据
	 * 
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvinceResponse(
			CoolWeatherDB coolWeatherDB, String response) {

		if (!TextUtils.isEmpty(response)) {
			/**
			 * 使用 正则表达式 来分割 这个 String 类型的 对象 并且 存到 allProvinces 数组中
			 * 
			 */
			String[] allProvinces = response.split(",");

			if ((allProvinces != null) && (allProvinces.length > 0)) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();

					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);

					coolWeatherDB.savePronvince(province);

				}
				return true;
			}

		}

		return false;
	}

	/**
	 * 处理城市的 编号数据
	 * 
	 * @param coolWeatherDB
	 * @param reponse
	 *            :从数据库传回来的 编号的数据
	 * @param provinceId
	 *            :是同City表 相关联的Province表的外键。
	 * @return
	 */
	public synchronized static boolean handleCityResponse(
			CoolWeatherDB coolWeatherDB, String response, int provinceId) {

		if (!TextUtils.isEmpty(response)) {
			// 将从response传来的城市的编号信息，通过正则表达式处理，存到allCities
			// 数组中。也就是对应某一个省份的全部城市名称和编号存到allCities中。
			// 比如 allCities[0] 里面的信息格式为0001|成都，
			// 因为从服务器传递回来的信息格式是00021|成都,9002|眉山,的格式，用逗号来分割。
			// 所以直接用这个,就可以分割这个数据，存入到allCities对象中。
			String[] allCities = response.split(",");

			Log.d("Util", allCities[9]);
			if ((allCities != null) && (allCities.length > 0)) {
				for (String c : allCities) {
					// 将用 | 分割的城市名和城市编号分割开，并分别存储到对应的City对象中。进而同步到数据库中。

					String[] array = c.split("\\|");
					Log.d("Util ", array[1]);
					City city = new City();

					city.setCityCode(array[0]);
					city.setCityName(array[1]);

					// ProvinceId是City表与Province表相互关联的外键
					city.setProvinceId(provinceId);

					coolWeatherDB.saveCity(city);

				}
				return true;
			}

		}

		return false;
	}

	public synchronized static boolean handleCountyResponse(
			CoolWeatherDB coolWeatherDB, String response, int cityId) {

		if (!TextUtils.isEmpty(response)) {
			// 从返回读取所有的 县级名称
			String[] allCounties = response.split(",");

			Log.d("coutiescccccccccccccccccccc", allCounties[9]);
			Log.d("cityIDIDIDIDIDIDIDIDI", cityId + "");
			if ((allCounties != null) && (allCounties.length > 0)) {

				for (String c : allCounties) {
					String[] countyInfoString = c.split("\\|");

					County county = new County();
					Log.d("countyInfo", countyInfoString[1]);
					county.setCountyCode(countyInfoString[0]);
					county.setCountyName(countyInfoString[1]);
					county.setCityId(cityId);

					coolWeatherDB.saveCounty(county);

				}
				return true;
			}

		}

		return false;
	}

	/**
	 * 解析从服务器返回的JSON数据 也就是 天气数据
	 */

	public static void handleWeatherResponse(Context context, String response) {

		try {
			JSONObject jsonArray = new JSONObject(response);

			JSONObject jsonObject = jsonArray.getJSONObject("weatherinfo");

			String city = jsonObject.getString("city");
			String weatherCodeString = jsonObject.getString("cityid");
			String tem1 = jsonObject.getString("temp1");
			String temp2 = jsonObject.getString("temp2");
			String ptime = jsonObject.getString("ptime");
			String weatherDesp = jsonArray.getString("weather");

			saveWeatherInfo(context, city, weatherCodeString, weatherDesp,
					ptime, tem1, temp2);

		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void saveWeatherInfo(Context context, String city,
			String weatherCode, String weatherDesp, String ptime, String temp1,
			String temp2) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy年M月d日",Locale.CHINA);
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		
		editor.putBoolean( "city_selected", true);
		editor.putString( "city_name", city);
		editor.putString( "weathercode", weatherCode);
		editor.putString("weatherDesp",weatherDesp);
		
		editor.putString("temp1", temp1);
		
		editor.putString ( "tem2", temp2);
		editor.putString ( "ptime", ptime);
		
		editor.putString( "current_date", simpleDateFormat.format(new Date()));
		
		editor.commit();
		
	 
		
		
		
		

	}

}
