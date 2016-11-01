package com.ligao.bean;

import android.content.Context;

import com.ligao.utils.SpUtil;

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
	 * <param name="logName">账号</param>
     * <param name="pwd">密码</param>
	 */
	public static String WcfMethod_LoginService = "LoginService";
	/**
	 * 工厂入库(未使用)
	 * <param name="jsonStr"></param>
	 */
	public static String WcfMethod_TotalWareHouseEnter = "TotalWareHouseEnter";
	/**
	 * 下载出库单
	 * mNode  (工程节点编码，读取登陆人的DeptCode)
	 * BillType（默认0）
	 */
	public static String WcfMethod_DownLoadOutOrder = "DownLoadOutOrder";
	
	/**
	 * 保存出库单
	 * outWareHouseJson
	 * 1
	 * /已经修改为新版本的出库，该出库不于入库相关联  1与入库无关，0有关
	 */
	public static String WcfMethod_SaveOutWareHouse = "SaveOutWareHouse";
	
	/**
	 * 垛管理
	 * type
	 * stackStrs
	 */
	public static String WcfMethod_StackManage = "StackManage";
	
	/**
	 * 查询一垛的数量
	 * boxCode
	 */
	public static String WcfMethod_QueryPileCount = "QueryPileCount";
	
	/**
	 * 获取一垛的所有垛码
	 * boxCode
	 */
	public static String WcfMethod_QueryPileCodes = "QueryPileCodes";
	/**
	 * 获取一垛的所有垛码
	 * code
	 */
	public static String WcfMethod_ExistsBarCode = "ExistsBarCode";
	
	/**
	 * 拆箱
	 * traceCodes   string[] 
	 */
	public static String WcfMethod_TraceDevanningByArray = "TraceDevanningByArray";
	/**
	 * 重组
	 * boxCode
	 * traceCodes string[] 
	 */
	public static String WcfMethod_TraceReerection = "TraceReerection";
	
}
