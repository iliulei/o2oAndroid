package com.ligao.ui;

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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ligao.R;
import com.ligao.app.MyApplication;
import com.ligao.bean.Configer;
import com.ligao.bean.Constants;
import com.ligao.entity.Goods;
import com.ligao.entity.User;
import com.ligao.shoppingcart.CheckOutActivity;
import com.ligao.shoppingcart.ShopBean;
import com.ligao.shoppingcart.ShoppingCanst;
import com.ligao.ui.base.BaseActivity;
import com.ligao.utils.KsoapUtil;
import com.ligao.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class InActivity extends BaseActivity  implements OnClickListener{
	
	private EditText  mNumberEdt;
	
	private TextView goBack;
	private Button sumbitBt,downloadOutOrderBt;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_in);
		
		
		sumbitBt = (Button)findViewById(R.id.bt_submit);
		downloadOutOrderBt = (Button)findViewById(R.id.bt_DownloadOutOrder);
		goBack = (TextView) findViewById(R.id.go_back);
		mNumberEdt = (EditText) findViewById(R.id.edt_in_mNumber);
		
		sumbitBt.setOnClickListener(this);
		downloadOutOrderBt.setOnClickListener(this);
		goBack.setOnClickListener(this);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	private void submit(){
		new Thread(getJson).start();
		ToastUtil.showToast(this, "确定");
	}
	
	/**
	 * 下载出库单
	 */
	private void download() {
		new Thread(getOutOrderJson).start();
		ToastUtil.showToast(this, "下载出库单");
		
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bt_submit:
			submit();
			break;
		case R.id.bt_DownloadOutOrder:
			download();
		case R.id.go_back:
			finish();
			break;
		default:
			break;
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
						//execute(re);
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}else{
					//execute(Constants.SERVERERROR);
				}
				
			} else if (msg.what == 0x01) {
				ToastUtil.showToast(InActivity.this, "服务器链接错误!");
				/*Toast.makeText(getApplicationContext(), "获取失败",
						Toast.LENGTH_LONG).show();*/
			}
		}
	};
	private String result;
	private String myurl,soap_action;
	private Runnable getJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE, Configer.WcfMethod_TotalWareHouseEnter);
				rpc.addProperty("wwaybillInfoJson", mNumberEdt.getText().toString().trim());
		    	myurl = Configer.getWcfUrl(getApplicationContext());
		    	soap_action = Configer.SOAPACTION_FRONT+Configer.WcfMethod_TotalWareHouseEnter;
		    	result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
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
	
	private Runnable getOutOrderJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE, Configer.WcfMethod_DownLoadOutOrder);
				MyApplication application= (MyApplication)getApplication();
				rpc.addProperty("mNode", application.getUserInfo().getDeptCode());
				rpc.addProperty("BillType", 0);
		    	myurl = Configer.getWcfUrl(getApplicationContext());
		    	soap_action = Configer.SOAPACTION_FRONT+Configer.WcfMethod_DownLoadOutOrder;
		    	result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
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


}
