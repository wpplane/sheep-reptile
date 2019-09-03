package com.yuntable.test;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yuntable.sheep.common.entity.Result;
import com.yuntable.sheep.reptile.remote.entity.Attendance;
import com.yuntable.sheep.reptile.remote.entity.Course;
import com.yuntable.sheep.reptile.remote.service.IReptileEntranceService;
import com.yuntable.sheep.reptile.service.App;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class Test {

	@Autowired
	private IReptileEntranceService reptileEntranceService;
	
	@org.junit.Test
	public void test1() throws Exception {
		Result<Map<String, String>> result = reptileEntranceService.getInitCookie(10822);
		System.out.println(result);
	}
	
	@org.junit.Test
	public void test() throws Exception {
		Result<Map<String, String>> result = reptileEntranceService.getInitCookie(10822);
		System.out.println(result);
		Map<String, String> cookieMap = result.getData();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream("f:\\a.jpg");
			if (out != null) {
				Result<byte[]> buf = reptileEntranceService.getLoginValiCode(10822, cookieMap);
				out.write(buf.getData());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("请输入验证码：");
		String code = new Scanner(System.in).next();
		String loginStr = reptileEntranceService.login(10822, cookieMap, "201802004557", "13071519033.", code).getMsg();
		System.out.println(loginStr); 
		Result<List<Course>> courseInfo = reptileEntranceService.getCourseInfo(10822, cookieMap, 2019, 1);
		for(Course course: courseInfo.getData()) {
			System.out.println(course);
		}
		/*Result<List<Attendance>> attendanceInfo = reptileEntranceService.getAllAttendanceInfo(10822, cookieMap);
		 for(Attendance aa: attendanceInfo.getData()){
			 System.out.println(aa);
		 
	}*/
	}
}
