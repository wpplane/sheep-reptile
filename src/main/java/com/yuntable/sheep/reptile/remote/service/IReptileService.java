package com.yuntable.sheep.reptile.remote.service;

import java.util.List;
import java.util.Map;

import com.yuntable.sheep.common.entity.Result;
import com.yuntable.sheep.reptile.remote.entity.Attendance;
import com.yuntable.sheep.reptile.remote.entity.Course;
import com.yuntable.sheep.reptile.remote.entity.Score;
import com.yuntable.sheep.reptile.remote.entity.StudentInfo;

public interface IReptileService{
	
	public Result<Map<String, String>> getInitCookie();
	
	/*
	 * 获取登录验证码
	 */
	public Result<byte[]> getLoginValiCode(Map<String, String> cookieMap);

	/*
	 * 登录
	 */
	public Result<Object> login(Map<String, String> cookieMap,
			String stuId, String password, String valiCode);

	/*
	 * 获取课程信息
	 */
	public Result<List<Course>> getCourseInfo(Map<String, String> cookieMap,
			int schoolYear, int term);

	/*
	 * 获取学生信息
	 */
	public Result<StudentInfo> getPersonInfo(Map<String, String> cookieMap);
	
	/*
	 * 获取成绩信息
	 */
	public Result<List<Score>> getScoreInfo(Map<String, String> cookieMap,
			int schoolYear, int term);

	/*
	 * 获取所有考勤信息
	 */
	public Result<List<Attendance>> getAllAttendanceInfo
		(Map<String, String> cookieMap);
	
}
