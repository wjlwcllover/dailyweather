package com.dailyweather.app.model;

/**
 * 身份 信息 表 对应的 实体类， 可以对 数据库中的 Province 表  中 对应的数据进行处理 ，从 外部输入信息。
 * @author wanjiali
 *
 */
public class Province {

	private int id;
	private String provinceName;
	private String provinceCode;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

}
