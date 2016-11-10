package com.ligao.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * KsoapUtil 工具类
 * @author Administrator
 *
 */
public class KsoapUtil {
	
	    /**
	     * 发送消息
	     * @param rpc
	     * @param myUrl
	     * @param soap_action
	     * @return 服务器返回数据
	     */
	    public static   String GetJsonWcf(SoapObject rpc, String myUrl,String soap_action) {
		String result = "ok";
    	// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本  
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);  
        envelope.bodyOut = rpc;  
        // 设置是否调用的是dotNet开发的WebService  
        envelope.dotNet = true;
        HttpTransportSE transport = new HttpTransportSE(myUrl,10*1000);  
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
}
