package com.dailyweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * �ӷ��������Province�� City�� County�ı����Ϣ���ڴ洢��response �����У�ͨ��HttpCallBackListener �ӿڵ�onFinish�����ص������ݳ�ȥ��
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
					 * ���� ��ȡ  �� Connection ��������  �� �浽  response��StringBuilder �� �� ���� �� 
					 */
					while ((line=bf.readLine()) != null)
					{
						response.append(line);
						
					}
					
					/**
					 * �ж�listener �Ƿ� Ϊ�� �� ��Ϊ�վͻص�onFinish() ���� �� �ر���� ���������
					 * @param response : StringBuilder �� ����Ҫ ת���� String ���͵� ��ʽ��
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
					//�����Ƕ� �׳� �쳣 ����Ҫ��� Connection  ��û�� �ر� �����û�йرյĻ�����ر����  ���� Connection 
					if (connection != null)
					{
						//���� disconnect ���� ���ر����  Connection 
						connection.disconnect();
					}
				}
				
			}
		}).start();
	}


}
