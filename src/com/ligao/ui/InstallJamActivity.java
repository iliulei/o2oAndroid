package com.ligao.ui;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.ligao.utils.SpUtil;
import com.ligao.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 组垛
 * @author Administrator
 *
 */
public class InstallJamActivity extends BaseActivity  implements OnClickListener{
	
	private EditText  mNumberEdt;
	private TextView goBack,scanNumberTv;
	private Button installJamBt,saveBt;
	private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;
    /**
     *箱码集合 
     */
    private ArrayList<String> boxCodeList = new ArrayList<String>();
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_installjam);
		
		installJamBt = (Button)findViewById(R.id.bt_installjam);
		saveBt = (Button)findViewById(R.id.bt_save);
		goBack = (TextView) findViewById(R.id.go_back);
		scanNumberTv = (TextView) findViewById(R.id.tv_scanNumber);
		mNumberEdt = (EditText) findViewById(R.id.edt_number);
		installJamBt.setOnClickListener(this);
		saveBt.setOnClickListener(this);
		goBack.setOnClickListener(this);
		
		
		//---获取缓存的箱码集合
		String text = SpUtil.getString(getApplicationContext(),Constants.INSTALLJAM_BOXCODES, "");
		String [] textArr = text.split(",");
		for (int i = 0; i < textArr.length; i++) {
			if(!textArr[i].equals(""))
			boxCodeList.add(textArr[i]);
		}
		mNumberEdt.setText(text);
		scanNumberTv.setText("已扫描："+boxCodeList.size()+"(箱)");
		//---获取缓存的箱码集合
		
		//---广播begin---
	    mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	Log.i(TAG, "receive");

                //此处获取扫描结果信息
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                String text="";
                boolean pd = true;
                for (int i = 0; i < boxCodeList.size(); i++) { //判断是否重复扫码
                	if(boxCodeList.get(i).equals(scanResult) || boxCodeList.get(i) == scanResult){
                		pd =false;
                		break;
                		}
				}
                
                if(pd) {boxCodeList.add(0, scanResult);}
                else {ToastUtil.showToast(getApplicationContext(), scanResult+"码已扫描,请勿重复扫描!");}
                	
                for (int i = 0; i < boxCodeList.size(); i++) {//循环拼接箱码字符串
                	text+=boxCodeList.get(i)+",";
				}
                Log.i(TAG, "scanResult = "+scanResult);
                mNumberEdt.setText(text);
                scanNumberTv.setText("已扫描："+boxCodeList.size()+"(箱)");
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
	 * 组垛
	 */
	private void installJam(){
		new Thread(getInstallJamJson).start();
	}
	private void installJamExecute(String re){
		ToastUtil.showToast(getApplicationContext(), re);
		scanNumberTv.setText(re);
	}
	
	/**
	 * 保存
	 */
	private void save() {
		SpUtil.putString(getApplicationContext(),Constants.INSTALLJAM_BOXCODES, mNumberEdt.getText().toString().trim());
		ToastUtil.showToast(this, "保存成功!");
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_installjam:
			installJam();
			break;
		case R.id.bt_save:
			save();
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
			if (msg.what == 0x02) {
				Log.v("PostGetJson", "" + result);
				if(null != result){
					try {
						String re = result;
						installJamExecute(re);
					} catch (Exception e) {
						e.printStackTrace();
					}  
				}else{
					//execute(Constants.SERVERERROR);
				}
				
			} else if (msg.what == 0x01) {
				ToastUtil.showToast(InstallJamActivity.this, "服务器链接错误!");
				/*Toast.makeText(getApplicationContext(), "获取失败",
						Toast.LENGTH_LONG).show();*/
			}
		}
	};
	private String result;
	private String myurl,soap_action;
	private Runnable getInstallJamJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE, Configer.WcfMethod_StackManage);
				rpc.addProperty("type", "0");
				rpc.addProperty("stackStrs", mNumberEdt.getText().toString().trim());
		    	myurl = Configer.getWcfUrl(getApplicationContext());
		    	soap_action = Configer.SOAPACTION_FRONT+Configer.WcfMethod_StackManage;
		    	result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
				//result = GetJson(myurl, params);
		    	System.out.println(result);
				if("error".equals(result))
					handler.sendEmptyMessage(0x01);
				else
					handler.sendEmptyMessage(0x02);
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
