package com.freshO2O.bean;

import android.content.Context;

import com.freshO2O.utils.SpUtil;

public class Configer {
	
	
	public static final boolean DEBUG = true; // 定义系统的运行状态
	public static final String SERVER_HOST = "http://192.168.170.166:80801/freshO2O"; // 定义服务端地址
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
	
	

	//命名空间(http://tempuri.org/为默认)
	public static String NAMESPACE = "http://tempuri.org/";
	//SVC名称
	public static String SVCNAME = "/JlbTotalWareHouseService.svc";
	
	public static String SERVICENAME = "IJlbTotalWareHouseService";
	
	
	public static String SOAPACTION_FRONT = Configer.NAMESPACE+Configer.SERVICENAME+"/";

	/**
	 * 获取wcfUrl地址
	 * @param context
	 * @return
	 */
	public static String getWcfUrl(Context context){
		String serversettingsaddress = SpUtil.getString(context, Constants.SERVER_SETTING_ADDRESS, "");
		String port = SpUtil.getString(context, Constants.SERVER_SETTING_PORT, "");
		return "http://"+serversettingsaddress.trim()+":"+port.trim()+Configer.SVCNAME;
	}
	
	
	

	
	
	/**
	 * 测试连接是否畅通
	 */
	public static String WcfMethod_DoWork = "DoWork";
	/**
	 * 登录
	 */
	public static String WcfMethod_LoginService = "LoginService";
	
	
	
	
	
}
