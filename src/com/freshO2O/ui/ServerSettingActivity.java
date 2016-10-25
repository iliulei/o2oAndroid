package com.freshO2O.ui;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.freshO2O.R;
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
		projectnameEdt = (EditText) this.findViewById(R.id.edt_projectname);
		submitBtn = (Button) this.findViewById(R.id.bt_serversettingSubmit);
	}

	@Override
	protected void initView() {
		serversettingsaddress = SpUtil.getString(getApplicationContext(), Constants.SERVER_SETTING_ADDRESS, "");
		port = SpUtil.getString(getApplicationContext(), Constants.SERVER_SETTING_PORT, "");
		projectname = SpUtil.getString(getApplicationContext(), Constants.SERVER_SETTING_PROJECTNAME, "");
		serversettingsaddressEdt.setText(serversettingsaddress);
		portEdt.setText(port);
		projectnameEdt.setText(projectname);
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
	private void submitBtn() {
		serversettingsaddress = serversettingsaddressEdt.getText().toString();
		port =  portEdt.getText().toString();
		projectname = projectnameEdt.getText().toString();
		
		if(serversettingsaddress ==null||serversettingsaddress.equals("")){
			ToastUtil.showToast(getApplicationContext(), "IP地址不允许为空!");
		}else if(port==null||port.equals("")){
			ToastUtil.showToast(getApplicationContext(), "端口号不允许为空!");
		}else if(projectname==null||projectname.equals("")){
			ToastUtil.showToast(getApplicationContext(), "项目名称不允许为空!");
		}else{
			SpUtil.putString(getApplicationContext(),Constants.SERVER_SETTING_ADDRESS,serversettingsaddress.trim());
			SpUtil.putString(getApplicationContext(),Constants.SERVER_SETTING_PORT,port.trim());
			SpUtil.putString(getApplicationContext(),Constants.SERVER_SETTING_PROJECTNAME,projectname.trim());
			ToastUtil.showToast(getApplicationContext(), "保存服务器地址成功!");
			finish();
		}
	}


}
