package com.dailyweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 从服务器获得Province， City， County的编号信息，在存储到response 对象中，通过HttpCallBackListener 接口的onFinish方法回调，传递出去。
 * @author wanjiali
 *
 */
public class HttpUtil 
{
	public static void sendHttpRequest(final String address, final HttpCallBackListener listener) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				try {
					URL url=  new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					
					connection.setRequestMethod("GET");
					
					connection.setReadTimeout( 8000);
					
					connection.setConnectTimeout(8000);
					
					InputStream in = connection.getInputStream();
					
					BufferedReader bf = new BufferedReader(new InputStreamReader(in));
					
					StringBuilder response = new StringBuilder();
					
					String line;
					
					/**
					 * 按行 读取  从 Connection 来的数据  并 存到  response（StringBuilder 类 ） 变量 中 
					 */
					while ((line=bf.readLine()) != null)
					{
						response.append(line);
						
					}
					
					/**
					 * 判断listener 是否 为空 ， 不为空就回调onFinish() 方法 来 关闭这个 网络监听器
					 * @param response : StringBuilder 类 ，需要 转化乘 String 类型的 形式。
					 */
					if (listener != null)
					{
						listener.onFinish(response.toString());
					}
					
					
				} catch (Exception e) {
					// TODO: handle exception
					if(listener != null)
					{
						listener.onError(e);
						
					}
				}
				finally {
					//不论是都 抛出 异常 ，都要检查 Connection  有没有 关闭 ，如果没有关闭的话必须关闭这个  网络 Connection 
					if (connection != null)
					{
						//调用 disconnect 方法 来关闭这个  Connection 
						connection.disconnect();
					}
				}
				
			}
		}).start();
	}


}
