package com.dailyweather.app.model;

import java.util.ArrayList;
import java.util.List;

import com.dailyweather.app.db.CoolWeaterOpenHelper;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {

	public static final String DB_NAME = "cool_weather";

	public static final int VERSION = 1;

	private static CoolWeatherDB coolWeatherDB;

	private SQLiteDatabase db;

	private CoolWeatherDB(Context context) {
		CoolWeaterOpenHelper dbHelper = new CoolWeaterOpenHelper(context,
				DB_NAME, null, VERSION);

		db = dbHelper.getWritableDatabase();

	}

	/**
	 * 获取CoolWeatherDB的实例
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);

		}
		return coolWeatherDB;

	}

	public void savePronvince(Province province) {
		if (province == null) {
			ContentValues values = new ContentValues();

			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());

			db.insert("Province", null, values);

		}

	}

	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();

		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));

				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex("province_code")));

				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex("province_name")));

				list.add(province);

			} while (cursor != null);

		}

		if (cursor != null) {
			cursor.close();
		}
		return list;

	}

	/**
	 * 将城市信息 保存到数据库 的 操作
	 * 
	 * @param province
	 */
	public void saveCity(City city) {
		if (city == null) {
			ContentValues values = new ContentValues();

			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());

			db.insert("City", null, values);

		}

	}

	public List<City> loadCity(int provinceId) {
		List<City> list = new ArrayList<City>();

		Cursor cursor = db.query("City", null, "province_id = ?",
				new String[] { String.valueOf(provinceId) }, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));

				city.setCityCode(cursor.getString(cursor
						.getColumnIndex("city_code")));

				city.setCityName(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setProvinceId(cursor.getInt(cursor
						.getColumnIndex("province_id")));

				list.add(city);

			} while (cursor.moveToNext());

		}

		if (cursor != null) {
			cursor.close();
		}
		return list;

	}

	/**
	 * 将 县的信息 添加到 数据库的 操作  
	 * @param county
	 */
	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();

			values.put("city_id", county.getCityId());
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());

			db.insert("County", null, values);

		}

	}

	/**
	 * 从数据库 读取到 县级以下的数据   并且 返回作为 一个  泛型对象 用于后续处理
	 * @param cityId
	 * @return
	 */
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();

		Cursor cursor = db.query("County", null, "city_id = ?",
				new String[] { String.valueOf(cityId) }, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				County county = new County();

				county.setCityId(cityId);
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex("county_code")));
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));

				list.add(county);

			} while (cursor.moveToNext());
		}

		if (cursor != null) {
			cursor.close();
		}

		return list;

	}

}
