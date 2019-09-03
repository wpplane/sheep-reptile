package com.yuntable.sheep.reptile.service.global.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.yuntable.sheep.common.entity.Result;
import com.yuntable.sheep.reptile.remote.entity.Attendance;
import com.yuntable.sheep.reptile.remote.entity.Course;
import com.yuntable.sheep.reptile.remote.entity.Score;
import com.yuntable.sheep.reptile.remote.entity.StudentInfo;
import com.yuntable.sheep.reptile.remote.service.IReptileEntranceService;
import com.yuntable.sheep.reptile.remote.service.IReptileService;
import com.yuntable.sheep.reptile.service.module.baiyun.service.impl.BaiYunServiceImpl;

@Service
@Component
public class ReptileEntranceServiceImpl implements IReptileEntranceService{
	
	@Autowired
	private BaiYunServiceImpl baiYunService;
	
	private IReptileService createReptileThread(int schoolCode){
		IReptileService reptileService = null;
		
		switch(schoolCode){
			case 10822:
				reptileService = baiYunService;
				break;
		}
		
		return reptileService;
	}
	
	@Override
	public Result<Map<String, String>> getInitCookie(int schoolCode) {
		IReptileService reptileService = createReptileThread(schoolCode);
		return reptileService.getInitCookie();
	}

	@Override
	public Result<byte[]> getLoginValiCode(int schoolCode, Map<String, String> cookieMap) {
		IReptileService reptileService = createReptileThread(schoolCode);
		return reptileService.getLoginValiCode(cookieMap);
	}

	@Override
	public Result<Object> login(int schoolCode, Map<String, String> cookieMap, String stuId, String password, String valiCode) {
		IReptileService reptileService = createReptileThread(schoolCode);
		return reptileService.login(cookieMap, stuId, password, valiCode);
	}

	@Override
	public Result<List<Course>> getCourseInfo(int schoolCode, Map<String, String> cookieMap, int schoolYear, int term) {
		IReptileService reptileService = createReptileThread(schoolCode);
		return reptileService.getCourseInfo(cookieMap, schoolYear, term);
	}

	@Override
	public Result<StudentInfo> getPersonInfo(int schoolCode, Map<String, String> cookieMap) {
		IReptileService reptileService = createReptileThread(schoolCode);
		return reptileService.getPersonInfo(cookieMap);
	}

	@Override
	public Result<List<Score>> getScoreInfo(int schoolCode, Map<String, String> cookieMap, int schoolYear, int term) {
		IReptileService reptileService = createReptileThread(schoolCode);
		return reptileService.getScoreInfo(cookieMap, schoolYear, term);
	}

	@Override
	public Result<List<Attendance>> getAllAttendanceInfo(int schoolCode, Map<String, String> cookieMap) {
		IReptileService reptileService = createReptileThread(schoolCode);
		return reptileService.getAllAttendanceInfo(cookieMap);
	}

}
