package com.freshO2O.bean;

import android.content.Context;

import com.freshO2O.utils.SpUtil;

public class Configer {
	public static final boolean DEBUG = true; // 定义系统的运行状态
	public static final String SERVER_HOST = "http://192.168.170.166:8080/freshO2O"; // 定义服务端地址
	
	/**
	 * 获取服务器地址
	 * @param context
	 * @return
	 */
	public static String getServerAddress(Context context){
		String serversettingsaddress = SpUtil.getString(context, Constants.SERVER_SETTING_ADDRESS, "");
		String port = SpUtil.getString(context, Constants.SERVER_SETTING_PORT, "");
		String projectname = SpUtil.getString(context, Constants.SERVER_SETTING_PROJECTNAME, "");
		String serverAddress = "http://"+serversettingsaddress+":"+port+"/"+projectname;
		return serverAddress;
	}
	
	

	
	
	
	
	
}
