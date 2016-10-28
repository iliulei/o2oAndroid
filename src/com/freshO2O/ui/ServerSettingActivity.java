package com.freshO2O.ui;


import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.freshO2O.R;
import com.freshO2O.bean.Configer;
import com.freshO2O.bean.Constants;
import com.freshO2O.ui.base.BaseActivity;
import com.freshO2O.utils.SpUtil;
import com.freshO2O.utils.ToastUtil;

/**
 * @author Administrator
 *服务器设置页面
 */
public class ServerSettingActivity extends BaseActivity implements OnClickListener {

	private EditText serversettingsaddressEdt,portEdt,projectnameEdt;
	private Button submitBtn;
	private Intent mIntent;
	String serversettingsaddress,port,projectname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_settings);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		serversettingsaddressEdt = (EditText) this.findViewById(R.id.edt_serversettingsAddress);
		portEdt = (EditText) this.findViewById(R.id.edt_port);
		//projectnameEdt = (EditText) this.findViewById(R.id.edt_projectname);
		submitBtn = (Button) this.findViewById(R.id.bt_serversettingSubmit);
	}

	@Override
	protected void initView() {
		serversettingsaddress = SpUtil.getString(getApplicationContext(), Constants.SERVER_SETTING_ADDRESS, "");
		port = SpUtil.getString(getApplicationContext(), Constants.SERVER_SETTING_PORT, "");
		//projectname = SpUtil.getString(getApplicationContext(), Constants.SERVER_SETTING_PROJECTNAME, "");
		serversettingsaddressEdt.setText(serversettingsaddress);
		portEdt.setText(port);
		//projectnameEdt.setText(projectname);
		submitBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_serversettingSubmit:
			submitBtn();
			break;
		default:
			break;
		}
	}
	String myurl,result,soap_action;
	
	private void submitBtn() {
		serversettingsaddress = serversettingsaddressEdt.getText().toString();
		port =  portEdt.getText().toString();
		//projectname = projectnameEdt.getText().toString();
		
		if(serversettingsaddress ==null||serversettingsaddress.equals("")){
			ToastUtil.showToast(getApplicationContext(), "IP地址不允许为空!");
		}else if(port==null||port.equals("")){
			ToastUtil.showToast(getApplicationContext(), "端口号不允许为空!");
		}/*else if(projectname==null||projectname.equals("")){
			ToastUtil.showToast(getApplicationContext(), "项目名称不允许为空!");
		}*/else{
			myurl = Configer.getServerAddress(getApplicationContext())+"/login.action";
			new Thread(getJson).start();
		}
	}
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x00) {
				
				SpUtil.putString(getApplicationContext(),Constants.SERVER_SETTING_ADDRESS,serversettingsaddress.trim());
				SpUtil.putString(getApplicationContext(),Constants.SERVER_SETTING_PORT,port.trim());
				//SpUtil.putString(getApplicationContext(),Constants.SERVER_SETTING_PROJECTNAME,projectname.trim());
				ToastUtil.showToast(getApplicationContext(), "连接服务器成功，保存服务器地址!");
				finish();
			} else{
				ToastUtil.showToast(ServerSettingActivity.this, "服务器连接错误,请重新输入!");
			}
		}
	};
	
	private Runnable getJson = new Runnable() {
		public void run() {
			try {
				
				//指定WebService的命名空间和调用的方法名 
		    	SoapObject rpc = new SoapObject(Configer.NAMESPACE, Configer.WcfMethod_DoWork);
		    	myurl = "http://"+serversettingsaddress.trim()+":"+port.trim()+Configer.SVCNAME;
		    	soap_action = Configer.SOAPACTION_FRONT+Configer.WcfMethod_DoWork;
				
		    	Boolean success = GetJsonWcf(rpc, myurl,soap_action);
				if(success){//连接成功
					handler.sendEmptyMessage(0x00);
				}else{//连接失败
					handler.sendEmptyMessage(0x01);
				}
			} catch (Exception e) {
				handler.sendEmptyMessage(0x02);
			}
		}
	};
	

	private Boolean GetJsonWcf(SoapObject rpc, String myUrl,String soap_action) {
		Boolean success = true;
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
            success = false;
            System.out.println("连接失败!");
        }  
        // 获取返回的数据  
        SoapObject object = (SoapObject) envelope.bodyIn; 
        if(null==object){
        	//return;
        }else
        // 获取返回的结果  
        {
        	System.out.println("连接成功");
        }
		return success;
	}



}
