package com.dailyweather.app.util;

import java.net.ResponseCache;

import android.R.integer;
import android.text.TextUtils;
import android.util.Log;

import com.dailyweather.app.model.City;
import com.dailyweather.app.model.CoolWeatherDB;
import com.dailyweather.app.model.County;
import com.dailyweather.app.model.Province;

/**
 * �����ʹ�����������ص�����
 * 
 * @author wanjiali
 * 
 */
public class Utility {

	/**
	 * ����ʡ�ݵı�Ÿ�ʽ����
	 * 
	 * @param coolWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvinceResponse(
			CoolWeatherDB coolWeatherDB, String response) {

		if (!TextUtils.isEmpty(response)) {
			/**
			 * ʹ�� ������ʽ ���ָ� ��� String ���͵� ���� ���� �浽 allProvinces ������
			 * 
			 */
			String[] allProvinces = response.split(",");

			if ((allProvinces != null) &&( allProvinces.length > 0)) {
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
	 * ������е� �������
	 * 
	 * @param coolWeatherDB
	 * @param reponse
	 *            :�����ݿ⴫������ ��ŵ�����
	 * @param provinceId
	 *            :��ͬCity�� �������Province��������
	 * @return
	 */
	public synchronized static boolean handleCityResponse(
			CoolWeatherDB coolWeatherDB, String response, int provinceId) {

		if (!TextUtils.isEmpty(response)) {
			// ����response�����ĳ��еı����Ϣ��ͨ��������ʽ�����浽allCities
			// �����С�Ҳ���Ƕ�Ӧĳһ��ʡ�ݵ�ȫ���������ƺͱ�Ŵ浽allCities�С�
			// ���� allCities[0] �������Ϣ��ʽΪ0001|�ɶ���
			// ��Ϊ�ӷ��������ݻ�������Ϣ��ʽ��00021|�ɶ�,9002|üɽ,�ĸ�ʽ���ö������ָ
			// ����ֱ�������,�Ϳ��Էָ�������ݣ����뵽allCities�����С�
			String[] allCities = response.split(",");

			Log.d( "Util", allCities[9]);
			if ((allCities != null) && (allCities.length > 0)) {
				for (String c : allCities) {
					// ���� | �ָ�ĳ������ͳ��б�ŷָ�����ֱ�洢����Ӧ��City�����С�����ͬ�������ݿ��С�

					String[] array = c.split("\\|");
					Log.d( "Util ", array[1]);
					City city = new City();

					city.setCityCode(array[0]);
					city.setCityName(array[1]);

					// ProvinceId��City����Province���໥���������
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

		if (! TextUtils.isEmpty(response)) {
			//�ӷ��ض�ȡ���е� �ؼ�����
			String[] allCounties = response.split(",");

		    Log.d( "coutiescccccccccccccccccccc", allCounties[9]);
		    Log.d("cityIDIDIDIDIDIDIDIDI", cityId +"");
			if ((allCounties != null) && (allCounties.length > 0)) {
				
				for (String c : allCounties) {
					String[] countyInfoString = c.split("\\|");

					County county = new County();
					Log.d( "countyInfo", countyInfoString[1]);
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

}
