package com.freshO2O.ui;


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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.freshO2O.R;
import com.freshO2O.app.MyApplication;
import com.freshO2O.bean.Configer;
import com.freshO2O.entity.User;
import com.freshO2O.ui.base.BaseActivity;
import com.freshO2O.utils.ShareSharePreferenceUtil;
import com.freshO2O.utils.ToastUtil;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private EditText loginaccount, loginpassword ,verifycodeText;
	private Button loginBtn;
	private Intent mIntent;
	String username;
	String password;
	String verifycode;
	private CheckBox rememberme,autologin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		loginaccount = (EditText) this.findViewById(R.id.loginaccount);
		loginpassword = (EditText) this.findViewById(R.id.loginpassword);
//		verifycodeText = (EditText) this.findViewById(R.id.verifycode);

		loginBtn = (Button) this.findViewById(R.id.login);
		
		rememberme = (CheckBox) findViewById(R.id.rememberme);
		autologin = (CheckBox) findViewById(R.id.autologin);

		rememberme.setOnCheckedChangeListener(listener);  
		autologin.setOnCheckedChangeListener(listener);  
		
	}

	@Override
	protected void initView() {

		loginBtn.setOnClickListener(this);
		
		String loginInfo = ShareSharePreferenceUtil.getLoginInfo(this);
		
		User u = ShareSharePreferenceUtil.getUser(this);
		
		if("1".equals(loginInfo)){
			if(null != u){
				loginaccount.setText(u.getAccount());
				loginpassword.setText(u.getPassword());
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		

		case R.id.login:

			userlogin();

			break;

		default:
			break;
		}

	}

	// 之前的方式太繁瑣了
	private void userlogin() {
		username = loginaccount.getText().toString().trim();
		password = loginpassword.getText().toString().trim();
//		verifycode = verifycodeText.getText().toString().trim();

		if (username.equals("")) {
			DisplayToast("用户名不能为空!");
		}else if (password.equals("")) {
			DisplayToast("密码不能为空!");
		}
//		else if (verifycode.equals("")) {
//			DisplayToast("验证码不能为空!");
//		}
		else{
			verify();
		}

	}

	CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			CheckBox box = (CheckBox) buttonView;

//			Toast.makeText(getApplicationContext(),
//					"获取的值:" + isChecked + "xxxxx" + box.getText(),
//					Toast.LENGTH_LONG).show();

		}
	};
	
	List<NameValuePair> params;
	private String result;
	private String myurl;
	
	private void verify(){
		
		
		username = loginaccount.getText().toString().trim();
		password = loginpassword.getText().toString().trim();
//		verifycode = verifycodeText.getText().toString().trim();
		
		params =new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("verifycode", verifycode));
		
		myurl = Configer.SERVER_HOST+"/login.action";
		
		new Thread(getJson).start();
	}
	
	private Runnable getJson = new Runnable() {

		public void run() {
			try {
				result = GetJson(myurl, params);
				handler.sendEmptyMessage(0x00);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	
	private void execute(String re){
		if("ok".equals(re)){
			
			ToastUtil.showToast(LoginActivity.this, "登录成功");
			username = loginaccount.getText().toString().trim();
			password = loginpassword.getText().toString().trim();
			
			
			
			MyApplication.user.setAccount(username);
			MyApplication.user.setPassword(password);
			
			ShareSharePreferenceUtil.saveLoginInfo(this, "0");
			
			if(rememberme.isChecked()){
				ShareSharePreferenceUtil.saveUser(this, MyApplication.user);
				ShareSharePreferenceUtil.saveLoginInfo(this, "1");
				
			}
			
			if(autologin.isChecked()){
				ShareSharePreferenceUtil.saveLoginInfo(this, "2");
			}
			
			finish();
			
		}else{
			ToastUtil.showToast(LoginActivity.this, "登录失败，请检查账号和密码是否正确！");
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
						String addr = (String)json.get("addr");
						
						MyApplication.user.setRecvaddr(addr);
						
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

}
