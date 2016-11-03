package com.ligao.ui;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ligao.R;
import com.ligao.app.MyApplication;
import com.ligao.bean.Configer;
import com.ligao.entity.User;
import com.ligao.ui.base.BaseActivity;
import com.ligao.utils.KsoapUtil;
import com.ligao.utils.ToastUtil;

/**
 * 拆垛
 * @author Administrator
 *
 */
public class BreakAJamActivity extends BaseActivity  implements OnClickListener{
	
	private EditText  mNumberEdt;
	private TextView goBack,nowNumberTv;
	private Button jamNumberBt,breakajamBt;
	private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_breakajam);
		
		breakajamBt = (Button)findViewById(R.id.bt_breakajam);
		jamNumberBt = (Button)findViewById(R.id.bt_jamNumber);
		goBack = (TextView) findViewById(R.id.go_back);
		nowNumberTv =  (TextView) findViewById(R.id.tv_nowNumber);
		mNumberEdt = (EditText) findViewById(R.id.edt_number);
		breakajamBt.setOnClickListener(this);
		jamNumberBt.setOnClickListener(this);
		goBack.setOnClickListener(this);
		//隐藏Android输入法窗口
	/*	 InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(mNumberEdt.getWindowToken(),0);*/
		
		//---广播begin---
	    mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	Log.i(TAG, "receive");
                //此处获取扫描结果信息
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                Log.i(TAG, "scanResult = "+scanResult);
                mNumberEdt.setText(scanResult);
            }
        };
        mFilter = new IntentFilter("ACTION_BAR_SCAN");
      //---广播end---
		
	}

	@Override
	protected void findViewById() {
		
	}

	@Override
	protected void initView() {
		
	}

	/**
	 * 拆垛
	 */
	private void breakAJam(){
		new Thread(getBreakAJamJson).start();
	}
	private void breakAJamExecute(String re){
		ToastUtil.showToast(getApplicationContext(), re);
		nowNumberTv.setText(re);
	}
	
	/**
	 * 查询垛数量
	 */
	private void jamNumber() {
		new Thread(getJamNumberJson).start();
	}
	private void jamNumberExecute(String re,String jamNumber){
		if("查询成功".equals(re)){
			ToastUtil.showToast(getApplicationContext(), re);
			nowNumberTv.setText("当前垛数量："+jamNumber);
		}else if("箱码不存在".equals(re)){
			ToastUtil.showToast(getApplicationContext(), re);
			nowNumberTv.setText(re);
		}else if("此箱码未分垛".equals(re)){
			ToastUtil.showToast(getApplicationContext(), re);
			nowNumberTv.setText(re);
		}
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_breakajam:
			breakAJam();
			break;
		case R.id.bt_jamNumber:
			jamNumber();
			break;
		case R.id.go_back:
			finish();
			break;
		default:
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x02) {//拆垛
				Log.v("PostGetJson", "" + result);
				
				if(null != result){
					try {
						String re = result;
						breakAJamExecute(re);
					} catch (Exception e) {
						e.printStackTrace();
					}  
				}else{
					//execute(Constants.SERVERERROR);
				}
				
			}else if (msg.what == 0x03) {//垛数量
				Log.v("PostGetJson", "" + result);
				if(null != result){
					try {
						JSONObject json= new JSONObject(result);  
						String re = (String)json.get("Message");
						Object JsonStr = (Object)json.get("JsonStr");
						String jamNumber = "";
						
						if(!JsonStr.equals(null)){
							jamNumber = JsonStr.toString();
						}
						jamNumberExecute(re,jamNumber);
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}else{
					//execute(Constants.SERVERERROR);
				}
				
			} else if (msg.what == 0x01) {
				ToastUtil.showToast(BreakAJamActivity.this, "服务器链接错误!");
			}
		}
	};
	private String result;
	private String myurl,soap_action;
	private Runnable getBreakAJamJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE, Configer.WcfMethod_StackManage);
				rpc.addProperty("type", "1");
				rpc.addProperty("stackStrs", mNumberEdt.getText().toString().trim());
		    	myurl = Configer.getWcfUrl(getApplicationContext());
		    	soap_action = Configer.SOAPACTION_FRONT+Configer.WcfMethod_StackManage;
		    	result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
				//result = GetJson(myurl, params);
				if("error".equals(result))
					handler.sendEmptyMessage(0x01);
				else
					handler.sendEmptyMessage(0x02);
				
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	
	private Runnable getJamNumberJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE, Configer.WcfMethod_QueryPileCount);
				rpc.addProperty("boxCode", mNumberEdt.getText().toString().trim());
		    	myurl = Configer.getWcfUrl(getApplicationContext());
		    	soap_action = Configer.SOAPACTION_FRONT+Configer.WcfMethod_QueryPileCount;
		    	result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
				//result = GetJson(myurl, params);
				if("error".equals(result))
					handler.sendEmptyMessage(0x01);
				else
					handler.sendEmptyMessage(0x03);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};

	   @Override
	    protected void onResume() {
	        super.onResume();
	      //注册广播来获取扫描结果
	        this.registerReceiver(mReceiver, mFilter);
	    }
	    @Override
	    protected void onPause() {
	        //注销获取扫描结果的广播
	        this.unregisterReceiver(mReceiver);
	        super.onPause();
	    }
	    @Override
	    protected void onDestroy() {
	        mReceiver = null;
	        mFilter = null;
	        super.onDestroy();
	    }

}
