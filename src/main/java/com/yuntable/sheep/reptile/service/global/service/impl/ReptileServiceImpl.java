package com.yuntable.sheep.reptile.service.global.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yuntable.sheep.common.entity.Result;
import com.yuntable.sheep.reptile.remote.entity.Attendance;
import com.yuntable.sheep.reptile.remote.entity.Course;
import com.yuntable.sheep.reptile.remote.entity.Score;
import com.yuntable.sheep.reptile.remote.entity.StudentInfo;
import com.yuntable.sheep.reptile.remote.service.IReptileService;
import com.yuntable.sheep.reptile.service.global.util.HttpClientUtil;

@Service
public class ReptileServiceImpl implements IReptileService {

	private static Result functionClosedResult = Result.fail("该功能未开通");

	@Autowired
	protected HttpClientServiceImpl httpClientService;

	// 内部方法获取初始化cookie
	protected Map<String, String> getInitCookie(Map<String, String> oldCookie, String initUrl) {
		// 自定义cookie
		if (oldCookie != null && oldCookie.size() > 0) {
			return oldCookie;
		}
		// 未传入cookie，因此初始化cookie
		Map<String, String> headerMap = new HashMap<String, String>();
		try {
			CloseableHttpResponse initResp = HttpClientUtil.getHttpClientService().postToResp(null, initUrl, null);
			if (initResp != null) {
				Header[] headers = initResp.getHeaders("Set-Cookie");
				if (headers != null && headers.length > 0) {
					for (Header headerKey : headers) {
						headerMap.put(headerKey.getName(), headerKey.getValue());
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return headerMap;
	}

	// 外部方法获取初始化cookie
	public Result<Map<String, String>> getInitCookie() {
		return functionClosedResult;
	}

	@Override
	public Result<byte[]> getLoginValiCode(Map<String, String> cookieMap) {
		return functionClosedResult;
	}

	@Override
	public Result<Object> login(Map<String, String> cookieMap, String stuId, String password, String valiCode) {
		return functionClosedResult;
	}

	@Override
	public Result<List<Course>> getCourseInfo(Map<String, String> cookieMap, int schoolYear, int term) {
		return functionClosedResult;
	}

	@Override
	public Result<StudentInfo> getPersonInfo(Map<String, String> cookieMap) {
		return functionClosedResult;
	}

	@Override
	public Result<List<Score>> getScoreInfo(Map<String, String> cookieMap, int schoolYear, int term) {
		return functionClosedResult;
	}

	@Override
	public Result<List<Attendance>> getAllAttendanceInfo(Map<String, String> cookieMap) {
		return functionClosedResult;
	}

}
