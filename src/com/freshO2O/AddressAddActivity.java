package com.freshO2O;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.freshO2O.app.MyApplication;
import com.freshO2O.bean.Configer;
import com.freshO2O.entity.Receiveaddr;
import com.freshO2O.utils.ToastUtil;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddressAddActivity extends Activity {

	private TextView userAddBack;
	private TextView add;
	private EditText uname;
	private EditText phoneText;
	private EditText addressText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_add);
		
		userAddBack  = (TextView) findViewById(R.id.user_add_back);
		
		ClickListener cl = new ClickListener();
		userAddBack.setOnClickListener(cl); 
		
		add = (TextView) findViewById(R.id.user_add);
		add.setOnClickListener(cl);
		uname 		 = (EditText) findViewById(R.id.username);
		phoneText 	 = (EditText) findViewById(R.id.phone);
		addressText = (EditText) findViewById(R.id.address);
		
	}
	
	List<NameValuePair> params;
	private String result;
	private String myurl;
	
	//增加用户
	private void addUser(){
		String username 		= uname.getText().toString();
		String phone 	= phoneText.getText().toString();
		String address = addressText.getText().toString();
		
		if(null == username || username.length() == 0){
			ToastUtil.showToast(this, "收货人不能为空");
			return;
		}
		if(null == phone || phone.length() == 0){
			ToastUtil.showToast(this, "手机不能为空");
			return;
		}
		if(null == address || address.length() == 0){
			ToastUtil.showToast(this, "地址不能为空");
			return;
		}
		
		params =new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", MyApplication.user.getAccount()));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("phone", phone));
		params.add(new BasicNameValuePair("address", address));
		
		myurl = Configer.SERVER_HOST+"/receiveaddrAdd.action";
		
		new Thread(getJson).start();
		
		
		
	}
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x00) {
				Log.v("PostGetJson", "" + result);

				if(null != result){
					try {
						JSONObject json= new JSONObject(result);  
						
						String re = (String)json.get("result");
						
						
						ToastUtil.showToast(AddressAddActivity.this, "添加成功");
						
//						execute(re);

						
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}
				
				
			} else if (msg.what == 0x01) {
				Toast.makeText(getApplicationContext(), "获取失败",
						Toast.LENGTH_LONG).show();
			}
		}
	};
	
	private Runnable getJson=new Runnable()
	{

		public void run() {
			// TODO Auto-generated method stub
			try
			{
				result=GetJson(myurl, params);
				handler.sendEmptyMessage(0x00);
			}
			catch(Exception e)
			{
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	
	/**
	 * 发送post请求获取json字符串
	 * @param url 网站
	 * @param params 参数List
	 * @return json字符串
	 */
	private String GetJson(String url, List<NameValuePair> params) {
		HttpPost httpRequest = new HttpPost(url);
		/*
		 * NameValuePair实现请求参数的封装
		 */
		String strResult = null;
		/* 添加请求参数到请求对象 */
		HttpParams httpParameters1 = new BasicHttpParams();
		HttpConnectionParams
		.setConnectionTimeout(httpParameters1, 10 * 1000);
		HttpConnectionParams.setSoTimeout(httpParameters1, 10 * 1000);
		//设置超时参数
		try {
			httpRequest.setParams(httpParameters1);
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) 
			{
				/* 读返回数据 */
				strResult = EntityUtils.toString(httpResponse.getEntity());
			} 
			else 
			{
				// 获取出现错误
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strResult;
	}
	
	//事件点击监听器
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.user_add_back:
				AddressAddActivity.this.finish();
				break;
			case R.id.user_add:
				addUser();
				break;
			default:
				break;
			}
		}
	}
}
