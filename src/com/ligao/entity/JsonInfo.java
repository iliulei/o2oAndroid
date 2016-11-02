package com.ligao.entity;
/**
 * 服务器返回JSON
 * @author Administrator
 */
public class JsonInfo {

	private String Message;
	private Object JsonStr;
	public String getMessage() {
		return Message;
	}
	public void setMessage(String message) {
		Message = message;
	}

	public Object getJsonStr() {
		return JsonStr;
	}
	public void setJsonStr(Object jsonStr) {
		JsonStr = jsonStr;
	}
	@Override
	public String toString() {
		return "JsonInfo [Message=" + Message + ", JsonStr=" + JsonStr + "]";
	}
	
	
	
	
}
