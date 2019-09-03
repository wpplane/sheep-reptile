package com.yuntable.sheep.reptile.service.global.util;

import com.yuntable.sheep.reptile.service.global.service.impl.HttpClientServiceImpl;

public class HttpClientUtil {
	public static HttpClientServiceImpl getHttpClientService() {
		return (HttpClientServiceImpl) SpringUtil.getBean("httpClientServiceImpl");
	}
}
