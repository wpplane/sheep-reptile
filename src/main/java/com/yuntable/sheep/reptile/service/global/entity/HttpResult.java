package com.yuntable.sheep.reptile.service.global.entity;

import lombok.Data;

@Data
public class HttpResult {
    // 响应码
    private Integer code;
    // 响应体
    private String body;
    
	public HttpResult(Integer code, String body) {
		super();
		this.code = code;
		this.body = body;
	}
    
}