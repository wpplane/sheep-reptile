package com.yuntable.sheep.reptile.service.module.baiyun.util;

public interface BaiYunJsMethods {
	/*
	 * 加密验证�?
	 */
	public String chkyzm(String valiCode);
	
	/*
	 * 加密密码
	 */
	public String chkpwd(String stuId, String pwd);
	
	/*
	 * 获取txt_mm_expression(密码信息)
	 */
	public String get_txt_mm_expression(String pwd);
	
	/*
	 * 获取txt_mm_userzh(userzh为学�?
	 */
	public String get_txt_mm_userzh(String stuId, String password);
	
}