package com.yuntable.sheep.reptile.service.module.baiyun.service.impl;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yuntable.sheep.common.entity.Result;
import com.yuntable.sheep.common.entity.TwoString;
import com.yuntable.sheep.common.util.MD5Util;
import com.yuntable.sheep.reptile.remote.entity.Attendance;
import com.yuntable.sheep.reptile.remote.entity.Constant;
import com.yuntable.sheep.reptile.remote.entity.Course;
import com.yuntable.sheep.reptile.remote.entity.CourseTime;
import com.yuntable.sheep.reptile.remote.entity.Score;
import com.yuntable.sheep.reptile.remote.entity.StudentInfo;
import com.yuntable.sheep.reptile.service.global.service.impl.HttpClientServiceImpl;
import com.yuntable.sheep.reptile.service.global.service.impl.ReptileServiceImpl;
import com.yuntable.sheep.reptile.service.global.util.HttpClientUtil;
import com.yuntable.sheep.reptile.service.module.baiyun.util.BaiYunJsMethods;
import com.yuntable.sheep.reptile.service.module.baiyun.util.BaiYunJsUtil;

@Service
public class BaiYunServiceImpl extends ReptileServiceImpl {

	@Autowired
	private HttpClientServiceImpl httpClientService;

	// 初始化地址
	private static final String initUrl = "http://jwgl.baiyunu.edu.cn/_data/login_home.aspx";
	// 验证码地址
	private static final String loginValiCodeUrl = "http://jwgl.baiyunu.edu.cn/sys/ValidateCode.aspx";
	// 登录地址
	private static final String loginUrl = "http://jwgl.baiyunu.edu.cn/_data/login_home.aspx";
	// 课程地址前准备地址
	private static final String coursePreUrl = "http://jwgl.baiyunu.edu.cn/znpk/Pri_StuSel.aspx";
	// 课程地址
	private static final String courseUrl = "http://jwgl.baiyunu.edu.cn/znpk/Pri_StuSel_rpt.aspx?m=";
	// 个人信息地址
	private static final String stuInfoUrl = "http://jwgl.baiyunu.edu.cn/xsxj/Stu_MyInfo_RPT.aspx";
	// 课程成绩地址
	private static final String scoreUrl = "http://jwgl.baiyunu.edu.cn/xscj/Stu_MyScore_rpt.aspx";
	// 考勤记录地址
	private static final String attendanceUrl = "http://jwgl.baiyunu.edu.cn/JXKQ/Stu_kqjg_rpt.aspx";

	private static BaiYunJsMethods jsMethods = BaiYunJsUtil.getInstance();

	private static Map<String, Integer> dayOfWeekMap;
	private static Map<String, Integer> requiredTypeMap;
	private static Map<String, Integer> contentTypeMap;
	private static Map<String, Integer> teachTypeMap;
	private static Map<String, Integer> examMethodMap;
	private static Map<TwoString, String> termCourseCodeMap;
	private static Map<String, Integer> studyNation;
	private static Map<String, Integer> attendanceReasonMap;

	static {
		// 星期
		dayOfWeekMap = new HashMap<String, Integer>();
		dayOfWeekMap.put("一", 1);
		dayOfWeekMap.put("二", 2);
		dayOfWeekMap.put("三", 3);
		dayOfWeekMap.put("四", 4);
		dayOfWeekMap.put("五", 5);
		// 课程要求类型
		requiredTypeMap = new HashMap<String, Integer>();
		requiredTypeMap.put("必修课", Constant.REQUIREDTYPE_REQUIRED);
		requiredTypeMap.put("选修课", Constant.REQUIREDTYPE_ELECTIVE);
		requiredTypeMap.put("任选课", Constant.REQUIREDTYPE_FREE_ELECTIVE);
		requiredTypeMap.put("其它", Constant.REQUIREDTYPE_OTHER);
		// 课程内容类型
		contentTypeMap = new HashMap<String, Integer>();
		contentTypeMap.put("专业课", Constant.CONTENTTYPE_PROFESSION);
		contentTypeMap.put("公共课", Constant.CONTENTTYPE_PUBLIC);
		contentTypeMap.put("课程设计", Constant.CONTENTTYPE_CURRICULUM_DESIGN);
		contentTypeMap.put("其它", Constant.CONTENTTYPE_OTHER);
		// 授课方式
		teachTypeMap = new HashMap<String, Integer>();
		teachTypeMap.put("讲授", Constant.TEACHTYPE_FACE);
		// 考核方式
		examMethodMap = new HashMap<String, Integer>();
		examMethodMap.put("统考", Constant.EXAMMETHOD_GENERAL);
		examMethodMap.put("非统考", Constant.EXAMMETHOD_NOGENERAL);
		examMethodMap.put("考查", Constant.EXAMMETHOD_NORMAL);
		// 课表的学期代码
		studyNation = new HashMap<String, Integer>();
		studyNation.put("初修", Constant.STUDYNATION_FIRST);
		// 课表的学期代码
		termCourseCodeMap = new HashMap<TwoString, String>();
		termCourseCodeMap.put(new TwoString("2018", "2"), "20181");
		termCourseCodeMap.put(new TwoString("2019", "1"), "20190");
		
		// 缺勤原因
		attendanceReasonMap = new HashMap<String, Integer>();
		attendanceReasonMap.put("请假", Constant.ATTENDANCEREASON_LEAVE);
		attendanceReasonMap.put("旷课", Constant.ATTENDANCEREASON_TRUANCY);
		attendanceReasonMap.put("早退", Constant.ATTENDANCEREASON_EARLYLEAVE);
	}

	// 外部方法获取初始化cookie
	public Result<Map<String, String>> getInitCookie() {
		Map<String, String> cookieMap = getInitCookie(null, initUrl);
		if (cookieMap == null || cookieMap.size() <= 0) {
			return Result.fail();
		}

		return Result.ok(cookieMap);
	}

	public Result<byte[]> getLoginValiCode(Map<String, String> cookieMap) {
		// 获取并设置请求头
		Map<String, String> headerMap = getInitCookie(cookieMap, initUrl);
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
		headerMap.put("Origin", "http://jwgl.baiyunu.edu.cn");
		headerMap.put("Host", "jwgl.baiyunu.edu.cn");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch");
		headerMap.put("Content-Type", "application/x-www-form-urlencoded");
		headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Referer", "http://jwgl.baiyunu.edu.cn/_data/login_home.aspx");
		try {
			CloseableHttpResponse response = httpClientService.postToResp(headerMap, loginValiCodeUrl, null);
			if (response != null) {
				HttpEntity entity = response.getEntity();

				if (entity != null) {
					return Result.ok(EntityUtils.toByteArray(entity));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Result.fail();
	}

	/*
	 * 登录
	 */
	public Result<Object> login(Map<String, String> cookieMap, String stuId, String password, String valiCode) {
		// 获取并设置请求头
		Map<String, String> headerMap = getInitCookie(cookieMap, initUrl);
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36");
		headerMap.put("Origin", "http://jwgl.baiyunu.edu.cn");
		headerMap.put("Host", "jwgl.baiyunu.edu.cn");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch");
		headerMap.put("Content-Type", "application/x-www-form-urlencoded");
		headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,/;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Referer", "http://jwgl.baiyunu.edu.cn/_data/login_home.aspx");
		// 设置参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("__VIEWSTATE",
				"dDwtNDU3MzUyODE0O3Q8O2w8aTwwPjtpPDE+O2k8Mj47PjtsPHQ8cDxsPFRleHQ7PjtsPOW5v+S4nOeZveS6keWtpumZojs+Pjs7Pjt0PHA8bDxUZXh0Oz47bDxcPHNjcmlwdCB0eXBlPSJ0ZXh0L2phdmFzY3JpcHQiXD4KZnVuY3Rpb24gQ2hrVmFsdWUoKXsKIHZhciB2VT0kKCdVSUQnKS5pbm5lckhUTUxcOwogdlU9dlUuc3Vic3RyaW5nKDAsMSkrdlUuc3Vic3RyaW5nKDIsMylcOwogdmFyIHZjRmxhZyA9ICJZRVMiXDsgaWYgKCQoJ3R4dF9hc21jZGVmc2Rkc2QnKS52YWx1ZT09JycpewogYWxlcnQoJ+mhu+W9leWFpScrdlUrJ++8gScpXDskKCd0eHRfYXNtY2RlZnNkZHNkJykuZm9jdXMoKVw7cmV0dXJuIGZhbHNlXDsKfQogZWxzZSBpZiAoJCgndHh0X3Bld2Vyd2Vkc2Rmc2RmZicpLnZhbHVlPT0nJyl7CiBhbGVydCgn6aG75b2V5YWl5a+G56CB77yBJylcOyQoJ3R4dF9wZXdlcndlZHNkZnNkZmYnKS5mb2N1cygpXDtyZXR1cm4gZmFsc2VcOwp9CiBlbHNlIGlmICgkKCd0eHRfc2RlcnRmZ3NhZHNjeGNhZHNhZHMnKS52YWx1ZT09JycgJiYgdmNGbGFnID09ICJZRVMiKXsKIGFsZXJ0KCfpobvlvZXlhaXpqozor4HnoIHvvIEnKVw7JCgndHh0X3NkZXJ0ZmdzYWRzY3hjYWRzYWRzJykuZm9jdXMoKVw7cmV0dXJuIGZhbHNlXDsKfQogZWxzZSB7ICQoJ2RpdkxvZ05vdGUnKS5pbm5lckhUTUw9J1w8Zm9udCBjb2xvcj0icmVkIlw+5q2j5Zyo6YCa6L+H6Lqr5Lu96aqM6K+BLi4u6K+356iN5YCZIVw8L2ZvbnRcPidcOwogICBkb2N1bWVudC5nZXRFbGVtZW50QnlJZCgidHh0X3Bld2Vyd2Vkc2Rmc2RmZiIpLnZhbHVlID0gJydcOwogZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoInR4dF9zZGVydGZnc2Fkc2N4Y2Fkc2FkcyIpLnZhbHVlID0gJydcOyAKIHJldHVybiB0cnVlXDt9CiB9CmZ1bmN0aW9uIFNlbFR5cGUob2JqKXsKIHZhciBzPW9iai5vcHRpb25zW29iai5zZWxlY3RlZEluZGV4XS5nZXRBdHRyaWJ1dGUoJ3VzcklEJylcOwogJCgnVUlEJykuaW5uZXJIVE1MPXNcOwogc2VsVHllTmFtZSgpXDsKfQpmdW5jdGlvbiBvcGVuV2luTG9nKHRoZVVSTCx3LGgpewp2YXIgVGZvcm0scmV0U3RyXDsKZXZhbCgiVGZvcm09J3dpZHRoPSIrdysiLGhlaWdodD0iK2grIixzY3JvbGxiYXJzPW5vLHJlc2l6YWJsZT1ubyciKVw7CiBpZih0aGVVUkwuaW5kZXhPZignUmVTZXRfUGFzc1dvcmQuYXNweCcpXD4tMSAmJiAnMTA4MjInIT0nMTA0ODInKSB7CiBwYXJlbnQuZG9Nb2JpbGVSZXNldCgpXDsgfSBlbHNlIHsKcG9wPXdpbmRvdy5vcGVuKHRoZVVSTCwnd2luS1BUJyxUZm9ybSlcOyAvL3BvcC5tb3ZlVG8oMCw3NSlcOwpldmFsKCJUZm9ybT0nZGlhbG9nV2lkdGg6Iit3KyJweFw7ZGlhbG9nSGVpZ2h0OiIraCsicHhcO3N0YXR1czpub1w7c2Nyb2xsYmFycz1ub1w7aGVscDpubyciKVw7CnBvcC5tb3ZlVG8oKHNjcmVlbi53aWR0aC13KS8yLChzY3JlZW4uaGVpZ2h0LWgpLzIpXDtpZih0eXBlb2YocmV0U3RyKSE9J3VuZGVmaW5lZCcpIGFsZXJ0KHJldFN0cilcOwp9Cn0KZnVuY3Rpb24gc2hvd0xheShkaXZJZCl7CnZhciBvYmpEaXYgPSBldmFsKGRpdklkKVw7CmlmIChvYmpEaXYuc3R5bGUuZGlzcGxheT09Im5vbmUiKQp7b2JqRGl2LnN0eWxlLmRpc3BsYXk9IiJcO30KZWxzZXtvYmpEaXYuc3R5bGUuZGlzcGxheT0ibm9uZSJcO30KfQpmdW5jdGlvbiBzZWxUeWVOYW1lKCl7CiAgJCgndHlwZU5hbWUnKS52YWx1ZT0kTignU2VsX1R5cGUnKVswXS5vcHRpb25zWyROKCdTZWxfVHlwZScpWzBdLnNlbGVjdGVkSW5kZXhdLnRleHRcOwp9CndpbmRvdy5vbmxvYWQ9ZnVuY3Rpb24oKXsKCXZhciBzUEM9TVNJRT93aW5kb3cubmF2aWdhdG9yLnVzZXJBZ2VudCt3aW5kb3cubmF2aWdhdG9yLmNwdUNsYXNzK3dpbmRvdy5uYXZpZ2F0b3IuYXBwTWlub3JWZXJzaW9uKycgU046TlVMTCc6d2luZG93Lm5hdmlnYXRvci51c2VyQWdlbnQrd2luZG93Lm5hdmlnYXRvci5vc2NwdSt3aW5kb3cubmF2aWdhdG9yLmFwcFZlcnNpb24rJyBTTjpOVUxMJ1w7CnRyeXskKCdwY0luZm8nKS52YWx1ZT1zUENcO31jYXRjaChlcnIpe30KdHJ5eyQoJ3R4dF9hc21jZGVmc2Rkc2QnKS5mb2N1cygpXDt9Y2F0Y2goZXJyKXt9CnRyeXskKCd0eXBlTmFtZScpLnZhbHVlPSROKCdTZWxfVHlwZScpWzBdLm9wdGlvbnNbJE4oJ1NlbF9UeXBlJylbMF0uc2VsZWN0ZWRJbmRleF0udGV4dFw7fWNhdGNoKGVycil7fQp9CmZ1bmN0aW9uIG9wZW5XaW5EaWFsb2codXJsLHNjcix3LGgpCnsKdmFyIFRmb3JtXDsKZXZhbCgiVGZvcm09J2RpYWxvZ1dpZHRoOiIrdysicHhcO2RpYWxvZ0hlaWdodDoiK2grInB4XDtzdGF0dXM6IitzY3IrIlw7c2Nyb2xsYmFycz1ub1w7aGVscDpubyciKVw7CndpbmRvdy5zaG93TW9kYWxEaWFsb2codXJsLDEsVGZvcm0pXDsKfQpmdW5jdGlvbiBvcGVuV2luKHRoZVVSTCl7CnZhciBUZm9ybSx3LGhcOwp0cnl7Cgl3PXdpbmRvdy5zY3JlZW4ud2lkdGgtMTBcOwp9Y2F0Y2goZSl7fQp0cnl7Cmg9d2luZG93LnNjcmVlbi5oZWlnaHQtMzBcOwp9Y2F0Y2goZSl7fQp0cnl7ZXZhbCgiVGZvcm09J3dpZHRoPSIrdysiLGhlaWdodD0iK2grIixzY3JvbGxiYXJzPW5vLHN0YXR1cz1ubyxyZXNpemFibGU9eWVzJyIpXDsKcG9wPXBhcmVudC53aW5kb3cub3Blbih0aGVVUkwsJycsVGZvcm0pXDsKcG9wLm1vdmVUbygwLDApXDsKcGFyZW50Lm9wZW5lcj1udWxsXDsKcGFyZW50LmNsb3NlKClcO31jYXRjaChlKXt9Cn0KZnVuY3Rpb24gY2hhbmdlVmFsaWRhdGVDb2RlKE9iail7CnZhciBkdCA9IG5ldyBEYXRlKClcOwpPYmouc3JjPSIuLi9zeXMvVmFsaWRhdGVDb2RlLmFzcHg/dD0iK2R0LmdldE1pbGxpc2Vjb25kcygpXDsKfQpmdW5jdGlvbiBjaGtwd2Qob2JqKSB7ICBpZihvYmoudmFsdWUhPScnKSAgeyAgICB2YXIgcz1tZDUoZG9jdW1lbnQuYWxsLnR4dF9hc21jZGVmc2Rkc2QudmFsdWUrbWQ1KG9iai52YWx1ZSkuc3Vic3RyaW5nKDAsMzApLnRvVXBwZXJDYXNlKCkrJzEwODIyJykuc3Vic3RyaW5nKDAsMzApLnRvVXBwZXJDYXNlKClcOyAgIGRvY3VtZW50LmFsbC5kc2RzZHNkc2R4Y3hkZmdmZy52YWx1ZT1zXDt9IGVsc2UgeyBkb2N1bWVudC5hbGwuZHNkc2RzZHNkeGN4ZGZnZmcudmFsdWU9b2JqLnZhbHVlXDt9IGNoZWNrcHdkKG9iailcO2Noa0x4c3RyKG9iai52YWx1ZSlcOyB9ICBmdW5jdGlvbiBjaGt5em0ob2JqKSB7ICBpZihvYmoudmFsdWUhPScnKSB7ICAgdmFyIHM9bWQ1KG1kNShvYmoudmFsdWUudG9VcHBlckNhc2UoKSkuc3Vic3RyaW5nKDAsMzApLnRvVXBwZXJDYXNlKCkrJzEwODIyJykuc3Vic3RyaW5nKDAsMzApLnRvVXBwZXJDYXNlKClcOyAgIGRvY3VtZW50LmFsbC5mZ2ZnZ2ZkZ3R5dXV5eXV1Y2tqZy52YWx1ZT1zXDt9IGVsc2UgeyAgICBkb2N1bWVudC5hbGwuZmdmZ2dmZGd0eXV1eXl1dWNramcudmFsdWU9b2JqLnZhbHVlLnRvVXBwZXJDYXNlKClcO319ZnVuY3Rpb24gY2hlY2twd2Qob0lucHV0KQp7CnZhciBwd2QgPSBvSW5wdXQudmFsdWVcOwp2YXIgcmVzdWx0ID0gMFw7CmZvcih2YXIgaSA9IDAsIGxlbiA9IHB3ZC5sZW5ndGhcOyBpIFw8IGxlblw7ICsraSkKewoJcmVzdWx0IHw9IGNoYXJUeXBlKHB3ZC5jaGFyQ29kZUF0KGkpKVw7Cn0KJCgidHh0X21tX2V4cHJlc3Npb24iKS52YWx1ZSA9IHJlc3VsdFw7CiQoInR4dF9tbV9sZW5ndGgiKS52YWx1ZSA9IHB3ZC5sZW5ndGhcOwp2YXIgdXNlcnpoID0gJCgidHh0X2FzbWNkZWZzZGRzZCIpLnZhbHVlXDsKdmFyIGludXNlcnpoID0gIjAiXDsKaWYoIHB3ZC50b0xvd2VyQ2FzZSgpLnRyaW0oKS5pbmRleE9mKHVzZXJ6aC50b0xvd2VyQ2FzZSgpLnRyaW0oKSlcPi0xKQp7CglpbnVzZXJ6aCA9ICIxIlw7Cn0KJCgidHh0X21tX3VzZXJ6aCIpLnZhbHVlID0gaW51c2VyemhcOwp9ZnVuY3Rpb24gY2hhclR5cGUobnVtKQp7CmlmKG51bSBcPj0gNDggJiYgbnVtIFw8PSA1NykKewoJcmV0dXJuIDhcOwp9CmlmIChudW0gXD49IDk3ICYmIG51bSBcPD0gMTIyKSAKewoJcmV0dXJuIDRcOwp9CmlmIChudW0gXD49IDY1ICYmIG51bSBcPD0gOTApIAp7CglyZXR1cm4gMlw7Cn0KcmV0dXJuIDFcOwp9IGZ1bmN0aW9uIGNoa0x4c3RyKHN0cikgCiB7CiBpZiAoc3RyIT0nJykgeyB2YXIgYXJyID0gc3RyLnNwbGl0KCcnKVw7CmZvciAodmFyIGkgPSAxXDsgaSBcPCBhcnIubGVuZ3RoLTFcOyBpKyspIHsKICAgdmFyIGZpcnN0SW5kZXggPSBhcnJbaS0xXS5jaGFyQ29kZUF0KClcOwogICB2YXIgc2Vjb25kSW5kZXggPSBhcnJbaV0uY2hhckNvZGVBdCgpXDsKICAgdmFyIHRoaXJkSW5kZXggPSBhcnJbaSsxXS5jaGFyQ29kZUF0KClcOwogICB0aGlyZEluZGV4IC0gc2Vjb25kSW5kZXggPT0gMVw7CiAgICBzZWNvbmRJbmRleCAtIGZpcnN0SW5kZXg9PTFcOwogICBpZigoKHRoaXJkSW5kZXggLSBzZWNvbmRJbmRleCA9PSAxKSYmKHNlY29uZEluZGV4IC0gZmlyc3RJbmRleD09MSkgKSB8fCAodGhpcmRJbmRleD09c2Vjb25kSW5kZXggJiYgc2Vjb25kSW5kZXg9PWZpcnN0SW5kZXgpKXsKICAgICAgZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoJ3R4dF9tbV9seHBkJykudmFsdWU9JzEnXDsgCiAgIH0KIH0KIH0KfQoKXDwvc2NyaXB0XD47Pj47Oz47dDw7bDxpPDE+Oz47bDx0PDtsPGk8MD47PjtsPHQ8cDxsPFRleHQ7PjtsPFw8b3B0aW9uIHZhbHVlPSdTVFUnIHVzcklEPSflrabjgIDlj7cnXD7lrabnlJ9cPC9vcHRpb25cPgpcPG9wdGlvbiB2YWx1ZT0nVEVBJyB1c3JJRD0n5bel44CA5Y+3J1w+5pWZ5biI5pWZ6L6F5Lq65ZGYXDwvb3B0aW9uXD4KXDxvcHRpb24gdmFsdWU9J1NZUycgdXNySUQ9J+W4kOOAgOWPtydcPueuoeeQhuS6uuWRmFw8L29wdGlvblw+Clw8b3B0aW9uIHZhbHVlPSdBRE0nIHVzcklEPSfluJDjgIDlj7cnXD7pl6jmiLfnu7TmiqTlkZhcPC9vcHRpb25cPgo7Pj47Oz47Pj47Pj47Pj47PgniGdKE4R5EZcB0kZKqdvrVeOWQ");
		params.put("pcInfo",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36undefined5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36 SN:NULL");
		params.put("txt_mm_expression", jsMethods.get_txt_mm_expression(password)); // 密码衍生出的字段
		params.put("txt_mm_length", password.length() + ""); // 密码长度
		params.put("txt_mm_userzh", jsMethods.get_txt_mm_userzh(stuId, password)); // 密码衍生出的字段
		params.put("typeName", "学生");
		params.put("dsdsdsdsdxcxdfgfg", jsMethods.chkpwd(stuId, password)); // 密码
		params.put("fgfggfdgtyuuyyuuckjg", jsMethods.chkyzm(valiCode)); // 验证码
		params.put("Sel_Type", "STU");
		params.put("txt_asmcdefsddsd", stuId);
		params.put("txt_pewerwedsdfsdff", "");
		params.put("txt_psasas", ""); // 密码

		try {
			String html = httpClientService.postToStr(headerMap, loginUrl, params);
			Document docs = Jsoup.parse(html);
			String msg = Jsoup.parse(html).selectFirst("#divLogNote font").text();
			if (!"正在加载，请稍候...".equals(msg)) {
				return Result.fail(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail();
		}

		return Result.ok();
	}

	/*
	 * 获取课表信息
	 */
	public Result<List<Course>> getCourseInfo(Map<String, String> cookieMap, int schoolYear, int term) {
		// 获取并设置请求头
		Map<String, String> headerMap = getInitCookie(cookieMap, initUrl);
		// 所选学期是否查得到
		TwoString schoolYearAndTerm = new TwoString(String.valueOf(schoolYear), String.valueOf(term));
		String termCode = termCourseCodeMap.get(schoolYearAndTerm);
		if (termCode == null) {
			return Result.fail("该学期暂不支持查询");
		}
		// 查询课表
		List<Course> courseList = new ArrayList<Course>();
		String preStr = null;
		try {
			// 获取第二次请求所需参数：hidyzm
			preStr = HttpClientUtil.getHttpClientService().postToStr(headerMap, coursePreUrl, null);

			Document doc = Jsoup.parse(preStr);
			String hidyzm = doc.selectFirst("input[name='hidyzm']").attr("value");

			// 生成15个随机文字当链接随机参数
			int len = 15;
			char[] m = new char[len];
			int i = 0;
			while (i < len) {
				m[i] = (char) ('0' + Math.random() * 10);
				i++;
				m[i] = (char) ('a' + Math.random() * 26);
				i++;
				m[i] = (char) ('A' + Math.random() * 26);
				i++;
			}
			// 设置参数
			Map<String, String> params = new HashMap<String, String>();
			params.put("hidsjyzm", MD5Util.MD5Encode("10822" + termCode + m, "UTF-8").toUpperCase());
			params.put("hidyzm", hidyzm);
			params.put("px", "0");
			params.put("rad", "1");
			params.put("Sel_XNXQ", termCode);
			params.put("txt_yzm", "");
			// 发送请求
			String html = HttpClientUtil.getHttpClientService().postToStr(headerMap, courseUrl + m, params);

			Document docs = Jsoup.parse(html);
			Elements trs = null;
			try {
				trs = docs.select("tbody").get(1).select("tr");
			}catch(Exception e) {
				return Result.ok(null);	//课程为空
			}

			// 遍历课程
			for (int j = 2; j < trs.size() - 1; j++) {
				Elements tds = trs.get(j).select("td");
				// 假如为课程行则创建课程对象
				String codeAndName = tds.get(1).text();
				if (codeAndName.trim().length() > 0) {
					Course course = new Course();

					// 设置课程代码和名称
					int begin = codeAndName.indexOf("[");
					int end = codeAndName.indexOf("]");
					String courseCode = codeAndName.substring(begin + 1, end);
					String courseName = codeAndName.substring(end + 1);

					// 设置课程内容类型和是否必修
					String[] courseTypes = tds.get(6).text().split("/");
					course.setContentType(contentTypeMap.get(courseTypes[0]));
					course.setRequiredType(requiredTypeMap.get(courseTypes[1]));

					String dayAndLesson = tds.get(11).text();
					// 设置星期几
					int dayOfWeek = dayOfWeekMap.get(dayAndLesson.substring(0, 1));
					// 设置开始节数和结束节数
					String lessonStr = dayAndLesson.substring(dayAndLesson.indexOf("[") + 1,
							dayAndLesson.lastIndexOf("节"));
					String[] lessonArr = lessonStr.split("-");
					int beginLesson = Integer.parseInt(lessonArr[0]);
					int endLesson = Integer.parseInt(lessonArr[lessonArr.length - 1]);

					// 设置上课周数
					List<CourseTime> courseTimeList = new ArrayList<CourseTime>();
					String weeksStr = tds.get(10).text();
					String[] weeksStrArr = weeksStr.split(",");
					if (weeksStrArr != null && weeksStrArr.length > 0) {
						for (String weeks : weeksStrArr) {
							CourseTime courseTime = new CourseTime();
							courseTime.setBeginLesson(beginLesson);
							courseTime.setEndLesson(endLesson);
							courseTime.setDayOfWeek(dayOfWeek);
							String[] weekArr = weeks.split("-");
							if (weekArr != null && weekArr.length > 1) {
								courseTime.setBeginWeek(Integer.parseInt(weekArr[0]));
								courseTime.setEndWeek(Integer.parseInt(weekArr[1]));
							} else {
								courseTime.setBeginWeek(Integer.parseInt(weeks));
								courseTime.setEndWeek(Integer.parseInt(weeks));
							}
							courseTimeList.add(courseTime);
						}
					}
					course.setCourseTimeList(courseTimeList);

					// 设置课程其他信息
					course.setCourseCode(courseCode);
					course.setCourseName(courseName);
					course.setStudyScore(Double.parseDouble(tds.get(2).text()));
					course.setTotalStudyTime(Integer.parseInt(tds.get(3).text()));
					course.setTeachStudyTime(Integer.parseInt(tds.get(4).text()));
					course.setOptionStudyTime(Integer.parseInt(tds.get(5).text()));
					course.setTeachType(teachTypeMap.get(tds.get(7).text()));
					course.setExamMethod(examMethodMap.get(tds.get(8).text()));
					course.setTeacher(tds.get(9).text());
					course.setPlace(tds.get(12).text());
					course.setSchoolYear(schoolYear);
					course.setTerm(term);

					courseList.add(course);
				}
				// 假如为上课则添加课程时间
				else {
					Course course = courseList.get(courseList.size() - 1);

					String dayAndLesson = tds.get(11).text();
					// 设置星期几
					int dayOfWeek = dayOfWeekMap.get(dayAndLesson.substring(0, 1));
					// 设置开始节数和结束节数
					String lessonStr = dayAndLesson.substring(dayAndLesson.indexOf("[") + 1,
							dayAndLesson.lastIndexOf("节"));
					String[] lessonArr = lessonStr.split("-");
					int beginLesson = Integer.parseInt(lessonArr[0]);
					int endLesson = Integer.parseInt(lessonArr[lessonArr.length - 1]);

					// 设置上课周数
					List<CourseTime> courseTimeList = course.getCourseTimeList();
					String weeksStr = tds.get(10).text();
					String[] weeksStrArr = weeksStr.split(",");
					if (weeksStrArr != null && weeksStrArr.length > 0) {
						for (String weeks : weeksStrArr) {
							CourseTime courseTime = new CourseTime();
							courseTime.setBeginLesson(beginLesson);
							courseTime.setEndLesson(endLesson);
							courseTime.setDayOfWeek(dayOfWeek);
							String[] weekArr = weeks.split("-");
							courseTime.setBeginWeek(Integer.parseInt(weekArr[0]));
							courseTime.setEndWeek(Integer.parseInt(weekArr[weekArr.length - 1]));
							courseTimeList.add(courseTime);
						}
					}
					course.setCourseTimeList(courseTimeList);
				}

			}

			return Result.ok(courseList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Result.fail();
	}

	@Override
	public Result<StudentInfo> getPersonInfo(Map<String, String> cookieMap) {
		// 获取并设置请求头
		Map<String, String> headerMap = getInitCookie(cookieMap, initUrl);
		try {
			String html = HttpClientUtil.getHttpClientService().postToStr(headerMap, stuInfoUrl, null);
			Document docs = Jsoup.parse(html);
			Elements trs = docs.select("tbody tr");

			StudentInfo stuInfo = new StudentInfo();
			stuInfo.setStuId(trs.get(1).select("td").get(1).text());
			stuInfo.setName(trs.get(1).selectFirst("td[colspan='2']").text());
			stuInfo.setSex("男".equals(trs.get(3).select("td").get(1).text()) ? 0 : 1);
			stuInfo.setEntryYear(Integer.parseInt(trs.get(19).select("td").get(1).text()));
			stuInfo.setCollege(trs.get(23).select("td").get(1).text());
			stuInfo.setProfession(trs.get(23).select("td").get(3).text());
			stuInfo.setClazz(trs.get(23).select("td").get(5).text());
			stuInfo.setEduSystem(Integer.parseInt(trs.get(18).select("td").get(5).text()));

			return Result.ok(stuInfo);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Result.fail();
	}

	@Override
	public Result<List<Score>> getScoreInfo(Map<String, String> cookieMap, int schoolYear, int term) {
		// 获取并设置请求头
		Map<String, String> headerMap = getInitCookie(cookieMap, initUrl);
		// 设置参数
		Map<String, String> params = new HashMap<String, String>();
		params.put("sel_xn", String.valueOf(schoolYear));
		params.put("sel_xq", String.valueOf(term - 1));
		params.put("SJ", "1");
		params.put("btn_search", new String("检索".getBytes(), Charset.forName("gb2312")));
		params.put("SelXNXQ", "2");
		params.put("zfx_flag", "0");
		params.put("zxf", "0");

		try {
			String html = HttpClientUtil.getHttpClientService().postToStr(headerMap, scoreUrl, params);
			Document docs = Jsoup.parse(html);
			Elements trs = docs.select("#ID_Table tr");
			List<Score> scoreList = new ArrayList<Score>();
			for (Element tr : trs) {
				Elements tds = tr.select("td");
				Score score = new Score();
				String courseCodeAndName = tds.get(1).text();
				int begin = courseCodeAndName.indexOf("[");
				int end = courseCodeAndName.indexOf("]");
				score.setCourseCode(courseCodeAndName.substring(begin + 1, end));
				score.setCourseName(courseCodeAndName.substring(end + 1));
				score.setStudyScore(Double.parseDouble(tds.get(2).text()));

				// 设置课程内容类型和是否必修
				String courseType = tds.get(3).text();
				if ("课程设计".equals(courseType)) {
					score.setContentType(contentTypeMap.get(tds.get(3).text()));
				} else {
					String[] courseTypes = tds.get(3).text().split("/");
					score.setContentType(contentTypeMap.get(courseTypes[0]));
					score.setRequiredType(requiredTypeMap.get(courseTypes[1]));
				}

				score.setExamMethod(examMethodMap.get(tds.get(5).text()));
				score.setStudyNation(studyNation.get(tds.get(6).text()));

				String scoreStr = tds.get(7).text();
				try {
					score.setScore(Double.parseDouble(scoreStr));
				} catch (Exception e) {
					score.setScoreStr(scoreStr);
				}

				score.setHaveGetStudyScore(Double.parseDouble(tds.get(8).text()));
				score.setAchiPoint(Double.parseDouble(tds.get(9).text()));
				score.setStudyScoreAndAchiPoint(Double.parseDouble(tds.get(10).text()));

				score.setSchoolYear(schoolYear);
				score.setTerm(term);

				scoreList.add(score);
			}

			return Result.ok(scoreList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Result.fail();
	}

	public Result<List<Attendance>> getAllAttendanceInfo(Map<String, String> cookieMap) {
		// 获取并设置请求头
		Map<String, String> headerMap = getInitCookie(cookieMap, initUrl);

		try {
			List<Attendance> attendanceList = new ArrayList<Attendance>();

			String html = HttpClientUtil.getHttpClientService().postToStr(headerMap, attendanceUrl, null);
			Document doc = Jsoup.parse(html);
			Elements tabs = doc.select("#pageRpt table");

			int schoolYear = -1, term = -1;
			for (int i = 0; i < tabs.size(); i++) {
				// 获取当前学年和学期
				if (i % 2 == 0) {
					String schoolYearAndTermStr = tabs.get(i).text();
					schoolYear = Integer.parseInt(schoolYearAndTermStr.substring(5, 9));
					int index = schoolYearAndTermStr.lastIndexOf("学期") - 1;
					term = ("一".equals(schoolYearAndTermStr.substring(index, index + 1)) ? 1 : 2);
				}
				// 获取考勤记录
				else {
					Elements trs = tabs.get(i).select("tr[class='B'],tr[class='H']");
					for (Element tr : trs) {
						Elements tds = tr.select("td");
						Attendance attendance = new Attendance();

						String courseCodeAndName = tds.get(0).text();
						int begin = courseCodeAndName.indexOf("[");
						int end = courseCodeAndName.indexOf("]");
						attendance.setCourseCode(courseCodeAndName.substring(begin + 1, end));
						attendance.setCourseName(courseCodeAndName.substring(end + 1));

						String teacherCodeAndName = tds.get(1).text();
						int begin2 = teacherCodeAndName.indexOf("[");
						int end2 = teacherCodeAndName.indexOf("]");
						attendance.setTeacherCode(teacherCodeAndName.substring(begin2 + 1, end2));
						attendance.setTeacherName(teacherCodeAndName.substring(end2 + 1));

						attendance.setWeek(Integer.parseInt(tds.get(3).text()));

						String dayOfWeekAndLesson = tds.get(4).text();

						attendance.setDayOfWeek(dayOfWeekMap.get(dayOfWeekAndLesson.substring(0, 1)));

						int begin3 = dayOfWeekAndLesson.indexOf("(");
						int end3 = dayOfWeekAndLesson.indexOf("节");
						attendance.setBeginLesson(Integer.parseInt(dayOfWeekAndLesson.substring(begin3 + 1, end3)));
						attendance.setEndLesson(attendance.getBeginLesson());

						attendance.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(tds.get(5).text()));

						attendance.setReason(attendanceReasonMap.get(tds.get(6).text()));
						// 判断是否跟上条记录相同（课程，老师，时间，节数）
						if (attendanceList != null && attendanceList.size() > 0) {
							Attendance lastAttendance = attendanceList.get(attendanceList.size() - 1);
							if (lastAttendance.getCourseCode().equals(attendance.getCourseCode())
									&& lastAttendance.getTeacherCode().equals(attendance.getTeacherCode())
									&& lastAttendance.getDate().compareTo(attendance.getDate()) == 0
									&& lastAttendance.getEndLesson() + 1 == attendance.getBeginLesson()) {
								lastAttendance.setEndLesson(attendance.getEndLesson());
							} else {
								attendanceList.add(attendance);
							}
						} else {
							attendanceList.add(attendance);
						}
					}
				}
			}

			return Result.ok(attendanceList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Result.fail();
	}

}
