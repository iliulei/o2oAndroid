package com.ligao.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ligao.R;
import com.ligao.bean.Configer;
import com.ligao.entity.Goods;
import com.ligao.ui.base.BaseActivity;
import com.ligao.utils.ToastUtil;

public class RegisterRegisterActivity extends BaseActivity implements OnClickListener{
	
	private Button register ;
	
	private EditText account, password ,phone,nickname ,addr,birthday,telphone ,sex;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_normal);
		
		register = (Button)findViewById(R.id.register);
		register.setOnClickListener(this);
		
		account = (EditText)findViewById(R.id.account);
		password = (EditText)findViewById(R.id.password);
		phone = (EditText)findViewById(R.id.phone);
		sex  = (EditText)findViewById(R.id.sex);
		nickname = (EditText)findViewById(R.id.nickname);
		birthday = (EditText)findViewById(R.id.birthday);
		telphone = (EditText)findViewById(R.id.telphone);
		addr = (EditText)findViewById(R.id.addr);
		
		
		
	}
	
	private void register(){
		String accountValue = account.getText().toString().trim();
		String passwordValue = password.getText().toString().trim();
		String phoneValue = phone.getText().toString().trim();
		String sexValue = sex.getText().toString().trim();
		String nicknameValue = nickname.getText().toString().trim();
		String birthdayValue = birthday.getText().toString().trim();
		String telphoneValue = telphone.getText().toString().trim();
		String addrValue = addr.getText().toString().trim();

		if (accountValue.equals("")) {
			DisplayToast("账号不能为空!");
		}else if (passwordValue.equals("")) {
			DisplayToast("密码不能为空!");
		}else if (passwordValue.length()<6) {
			DisplayToast("密码长度不能小于6!");
		}else if (phoneValue.equals("")) {
			DisplayToast("移动电话不能为空!");
		}else if (addrValue.equals("")) {
			DisplayToast("地址不能为空!");
		}else{
			params =new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("account", accountValue));
			params.add(new BasicNameValuePair("nickname", nicknameValue));
			params.add(new BasicNameValuePair("password", passwordValue));
//			params.add(new BasicNameValuePair("email", email));
			
			params.add(new BasicNameValuePair("phone", phoneValue));
			params.add(new BasicNameValuePair("addr", addrValue));
			
			myurl = Configer.SERVER_HOST+"/userAdd.action";
			
			new Thread(getJson).start();
		}
	}
	
	private void execute(String re){
		if("ok".equals(re)){
			ToastUtil.showToast(RegisterRegisterActivity.this, "注册成功");
		}else{
			ToastUtil.showToast(RegisterRegisterActivity.this, "注册失败，请检查帐户是否重复！");
		}
	}
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x00) {
				Log.v("PostGetJson", "" + result);

				if(null != result){
					try {
						JSONObject json= new JSONObject(result);  
						
						String re = (String)json.get("result");
						
						
						execute(re);

						
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
	
	List<NameValuePair> params;
	private String result;
	private String myurl;

	@Override
	protected void findViewById() {

	}

	@Override
	protected void initView() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register:
			
			register();
			
//			setResult(100);
//			finish(); 
			break;

		default:
			break;
		}
		
	}

}
