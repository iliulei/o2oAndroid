package com.ligao.bean;

import android.os.Environment;


public class Constants {

	//保存参数文件夹名称
	public static final String SHARED_PREFERENCE_NAME = "zd";
	
	public static String MOBILE_SERVERS_URL="http://mserver.e-cology.cn/servers.do";
	public static String serverAdd = "";
	public static String serverVersion = "";
	public static String sessionKey = "";
	public static String clientLogoPath="";
	public static String headpic="";
	public static String HOME_MODULE="0";
	public static String MORE_MODULE="-1";
	public static String SETTING_MODULE="-2";
	public static String ABOUT_MODULE="-3";
	public static String ADDRESSBOOK_MODULE="6";
	public static int createworkflow=0;
//	public static Config config = new Config();
//	public static ContactItem contactItem = new ContactItem();
	
	
	public static String clientVersion = "";
	public static String deviceid = "";
	public static String token = "";
	public static String clientOs = "";
	public static String clientOsVer = "";
	public static String language = "";
	public static String country = "";
	public static String user = "";
	public static String pass = "";
	
	/**
	 * 服务器链接错误
	 */
	public static String SERVERERROR = "servererror";
	
	// SDCard路径
		public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	// 图片存储路径
		public static final String BASE_PATH = SD_PATH + "/freshO2O/";
	
	// 缓存图片路径
		public static final String BASE_IMAGE_CACHE = BASE_PATH + "cache/images2/";
		
		//config内字段名称
		/**
		 * 服务器地址
		 */
		public static final String SERVER_SETTING_ADDRESS = "server_setting_address";
		/**
		 * 端口号
		 */
		public static final String SERVER_SETTING_PORT = "server_setting_port";
		/**
		 * 项目名称
		 */
		public static final String SERVER_SETTING_PROJECTNAME = "server_setting_projectname";
		
		/**
		 * 组垛箱码集合
		 */
		public static final String INSTALLJAM_BOXCODES = "installjam_boxcodes";
		
		/**
		 * 出库单号
		 */
		public static final String OUT_ORDERS = "out_orders";
	
}
