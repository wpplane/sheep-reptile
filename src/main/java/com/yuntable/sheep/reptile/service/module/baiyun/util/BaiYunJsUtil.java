package com.yuntable.sheep.reptile.service.module.baiyun.util;

import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class BaiYunJsUtil {

	private static BaiYunJsMethods baiyunJsMethods;

	private BaiYunJsUtil() {
	}

	public static BaiYunJsMethods getInstance() {
		if (baiyunJsMethods == null) {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("js");
			try {
				String path = BaiYunJsUtil.class.getResource("../resource/baiyun.js").getPath().replaceAll("%20", " ");
				// FileReader的参数为所要执行的js文件的路径
				engine.eval(new FileReader(path));
				if (engine instanceof Invocable) {
					Invocable invocable = (Invocable) engine;
					BaiYunJsMethods executeMethod = invocable.getInterface(BaiYunJsMethods.class);
					return executeMethod;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}
}
