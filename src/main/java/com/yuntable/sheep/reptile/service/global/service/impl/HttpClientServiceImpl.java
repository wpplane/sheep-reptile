package com.yuntable.sheep.reptile.service.global.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yuntable.sheep.reptile.service.global.entity.HttpResult;

@Service
public class HttpClientServiceImpl{

	@Autowired
	private CloseableHttpClient httpClient;

	@Autowired
	private RequestConfig config;
	
	private static ResponseHandler<String> responseHandler = new BasicResponseHandler();

	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String doGet(String url) throws Exception {
		HttpGet httpGet = new HttpGet(url);

		httpGet.setConfig(config);

		CloseableHttpResponse response = this.httpClient.execute(httpGet);

		if (response.getStatusLine().getStatusCode() == 200) {
			return EntityUtils.toString(response.getEntity(), "UTF-8");
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String doGet(String url, Map<String, Object> map) throws Exception {
		URIBuilder uriBuilder = new URIBuilder(url);

		if (map != null) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				uriBuilder.setParameter(entry.getKey(), entry.getValue()
						.toString());
			}
		}

		return this.doGet(uriBuilder.build().toString());

	}

	/**
	 * 
	 * @param url
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public HttpResult doPost(String url, Map<String, Object> map)
			throws Exception {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(config);

		if (map != null) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue().toString()));
			}
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
					list, "UTF-8");

			httpPost.setEntity(urlEncodedFormEntity);
		}

		CloseableHttpResponse response = this.httpClient.execute(httpPost);
		return new HttpResult(response.getStatusLine().getStatusCode(),
				EntityUtils.toString(response.getEntity(), "UTF-8"));
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public HttpResult doPost(String url) throws Exception {
		return this.doPost(url, null);
	}
	
	/**
	 * @param headers
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private HttpPost basePost(Map<String, String> headers, 
			String url, Map<String, String> params) throws Exception{
		HttpPost post = new HttpPost(url);
		if (headers != null && headers.size() > 0) {
			for (String headerKey : headers.keySet()) {
				post.addHeader(headerKey, headers.get(headerKey));
			}
		}
		if (params != null && params.size() > 0) {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				list.add(new BasicNameValuePair(key, params.get(key)));
			}
			post.setEntity(new UrlEncodedFormEntity(list));
		}
		
		return post;
	}
	
	/**
	 * @param headers
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public CloseableHttpResponse postToResp(Map<String, String> headers, 
			String url, Map<String, String> params) throws Exception{
		return this.httpClient.execute(basePost(headers, url, params));
	}
	
	/**
	 * @param headers
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String postToStr(Map<String, String> headers, 
			String url, Map<String, String> params) throws Exception{
		return this.httpClient.execute(basePost(headers, url, params), responseHandler);
	}
}