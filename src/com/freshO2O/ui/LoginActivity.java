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
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

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
import android.widget.TextView;
import android.widget.Toast;

import com.freshO2O.R;
import com.freshO2O.app.MyApplication;
import com.freshO2O.bean.Configer;
import com.freshO2O.bean.Constants;
import com.freshO2O.entity.User;
import com.freshO2O.ui.base.BaseActivity;
import com.freshO2O.utils.ShareSharePreferenceUtil;
import com.freshO2O.utils.ToastUtil;

public class LoginActivity extends BaseActivity implements OnClickListener {

	
	
	private EditText loginaccount, loginpassword ,verifycodeText;
	private TextView serversettingTv;
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
		serversettingTv = (TextView) this.findViewById(R.id.tv_serversetting);
		rememberme = (CheckBox) findViewById(R.id.rememberme);
		autologin = (CheckBox) findViewById(R.id.autologin);

		rememberme.setOnCheckedChangeListener(listener);  
		autologin.setOnCheckedChangeListener(listener);  
		
	}

	@Override
	protected void initView() {

		loginBtn.setOnClickListener(this);
		serversettingTv.setOnClickListener(this);
		String loginInfo = ShareSharePreferenceUtil.getLoginInfo(this);
		
		User u = ShareSharePreferenceUtil.getUser(this);
		
		if("1".equals(loginInfo)){
			if(null != u){
				loginaccount.setText(u.getAccount());
				loginpassword.setText(u.getPassword());
				rememberme.setChecked(true);
			}
		}else if("2".equals(loginInfo)){
			if(null != u){
				loginaccount.setText(u.getAccount());
				loginpassword.setText(u.getPassword());
				rememberme.setChecked(true);
				autologin.setChecked(true);
				userlogin();
			}
			
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login:
			userlogin();
			break;
		case R.id.tv_serversetting:
			Intent intent = new Intent(LoginActivity.this, ServerSettingActivity.class);
			startActivity(intent);
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
			if(isChecked&&box.getId() == R.id.autologin){
				rememberme.setChecked(true);
			}
//			Toast.makeText(getApplicationContext(),
//					"获取的值:" + isChecked + "xxxxx" + box.getText(),
//					Toast.LENGTH_LONG).show();

		}
	};
	
	List<NameValuePair> params;
	private String result;
	private String myurl,soap_action;
	
	private void verify(){
		
		username = loginaccount.getText().toString().trim();
		password = loginpassword.getText().toString().trim();
//		verifycode = verifycodeText.getText().toString().trim();
		params =new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("verifycode", verifycode));
		
		myurl = Configer.getServerAddress(getApplicationContext())+"/login.action";
		
		new Thread(getJson).start();
	}
	
	private Runnable getJson = new Runnable() {

		public void run() {
			try {
				
				SoapObject rpc = new SoapObject(Configer.NAMESPACE, Configer.WcfMethod_LoginService);
				rpc.addProperty("logName", username);
				rpc.addProperty("pwd", password);
				
		    	myurl = Configer.getWcfUrl(getApplicationContext());
		    	soap_action = Configer.SOAPACTION_FRONT+Configer.WcfMethod_LoginService;
		    	result = GetJsonWcf(rpc,myurl,soap_action);
				//result = GetJson(myurl, params);
				if("error".equals(result))
					handler.sendEmptyMessage(0x01);
				else
					handler.sendEmptyMessage(0x00);
				
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	
	private void execute(String re){
		if("登录成功".equals(re)){
			
			ToastUtil.showToast(LoginActivity.this, "登录成功");
			username = loginaccount.getText().toString().trim();
			password = loginpassword.getText().toString().trim();
			
			MyApplication.user.setAccount(username);
			MyApplication.user.setPassword(password);
			
			ShareSharePreferenceUtil.saveLoginInfo(this, "0");
			
			if(rememberme.isChecked()){
				ShareSharePreferenceUtil.saveUser(this, MyApplication.user);
				
				System.out.println(MyApplication.user);
				
				ShareSharePreferenceUtil.saveLoginInfo(this, "1");
			}
			
			if(autologin.isChecked()){
				ShareSharePreferenceUtil.saveLoginInfo(this, "2");
			}
			finish();
			//跳转到首页
			Intent intent =  new Intent(LoginActivity.this,HomeActivity.class);
			startActivity(intent);	
		}else if(Constants.SERVERERROR.equals(re)){
			ToastUtil.showToast(LoginActivity.this, "服务器链接错误!");
		}else {
			ToastUtil.showToast(LoginActivity.this, "登录失败，请检查账号和密码是否正确!");
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x00) {
				Log.v("PostGetJson", "" + result);
				
				if(null != result){
					try {
						JSONObject json= new JSONObject(result);  
						String re = (String)json.get("Message");
						Object JsonStr = (Object)json.get("JsonStr");
						if(!JsonStr.equals(null)){
							json=  new JSONObject(JsonStr.toString());
							MyApplication application= (MyApplication)getApplication();
							User user = new User();
							user.setName((String)json.get("Name"));
							user.setLoginName((String)json.get("LoginName"));
							user.setDeptCode((String)json.get("DeptCode"));
							user.setDeptName((String)json.get("DeptName"));
							application.setUserInfo(user);
						}
						execute(re);
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}else{
					execute(Constants.SERVERERROR);
				}
				
				
			} else if (msg.what == 0x01) {
				ToastUtil.showToast(LoginActivity.this, "服务器链接错误!");
				/*Toast.makeText(getApplicationContext(), "获取失败",
						Toast.LENGTH_LONG).show();*/
			}
		}
	};
	

	
	
	private String GetJsonWcf(SoapObject rpc, String myUrl,String soap_action) {
		String result = "ok";
    	// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本  
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);  
        envelope.bodyOut = rpc;  
        // 设置是否调用的是dotNet开发的WebService  
        envelope.dotNet = true;
        HttpTransportSE transport = new HttpTransportSE(myUrl,5*1000);  
        try {  
            // 调用WebService  
            transport.call(soap_action, envelope);  
        } catch (Exception e) {  
        	//代表连接失败
            e.printStackTrace();  
            result = "error";
            System.out.println("连接失败!");
        }  
        // 获取返回的数据  
        SoapObject object = (SoapObject) envelope.bodyIn; 
        if(null==object){
        	//return;
        }else
        // 获取返回的结果  
        {
        	  result = object.getProperty(0).toString();
        }
		return result;
	}
	
	
	/**
	 * 发送post请求获取json字符串
	 * @param url 网站
	 * @param params 参数List
	 * @return json字符串
	 */
/*	private String GetJson(String url, List<NameValuePair> params) {
		HttpPost httpRequest = new HttpPost(url);
		
		 * NameValuePair实现请求参数的封装
		 
		String strResult = null;
		 添加请求参数到请求对象 
		HttpParams httpParameters1 = new BasicHttpParams();
		HttpConnectionParams
		.setConnectionTimeout(httpParameters1, 3 * 1000);
		HttpConnectionParams.setSoTimeout(httpParameters1, 5 * 1000);
		//设置超时参数
		try {
			httpRequest.setParams(httpParameters1);
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			 发送请求并等待响应 
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			 若状态码为200 ok 
			if (httpResponse.getStatusLine().getStatusCode() == 200) 
			{
				 读返回数据 
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
	}*/

}
