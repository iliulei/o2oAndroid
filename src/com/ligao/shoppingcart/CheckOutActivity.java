package com.ligao.shoppingcart;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ligao.R;
import com.ligao.app.MyApplication;
import com.ligao.bean.Configer;
import com.ligao.layout.NoScrollListView;

public class CheckOutActivity extends Activity {
	
	private Button sureCheckOut;		//确认购买
	private TextView addresseeName;		//收货人姓名
	private TextView smearedAddress;	//收货人区地址
	private TextView detailAddress;		//收货人详细地址
	private TextView checkOutAllPrice;	//结算的总金额
	private TextView title_left;		//title左标题,返回
	private TextView title_center;		//title中间标题
	private RelativeLayout addressRelative;	  //显示收货人信息的布局
	private NoScrollListView checkOutListView;//商品列表
	
	private CheckOutAdapter adapter;
	private List<ShopBean> list = new ArrayList<ShopBean>();			  //结算商品数据集合
	private List<AddressBean> addressList;	  //收货人地址数据集合
	
	private static int REQUESTCODE = 1;		  //跳转请求码
	private float allPrice = 0;				  //购买商品需要的总金额
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkout);
		initView();
		
		ShopBean bean  = (ShopBean)getIntent().getSerializableExtra("bean");
		if(null != bean){
			list.add(bean);
		}
		init();
	}
	
	//初始化数据
	private void init(){
		
		String shopIndex = getIntent().getStringExtra("shopIndex");
		if(shopIndex != null){
			String[] shopIndexs = shopIndex.split(",");
			for(String s : shopIndexs){
				int position = Integer.valueOf(s);
				ShopBean bean = ShoppingCanst.list.get(position);
				allPrice += bean.getShopNumber()*bean.getShopPrice();
				list.add(bean);
			}
		}
		
		getAddressData();
		addressList = ShoppingCanst.addressList;
		checkOutAllPrice.setText("总共有"+list.size()+"个商品       总价￥"+allPrice);
		showInfo(0);	//默认显示第一条地址信息
		
		adapter = new CheckOutAdapter(this, list, R.layout.checkout_item);
		checkOutListView.setAdapter(adapter);
	}
	
	//初始化UI界面
	private void initView(){
		sureCheckOut = (Button) findViewById(R.id.sureCheckOut);
		addresseeName = (TextView) findViewById(R.id.addresseeName);
		smearedAddress = (TextView) findViewById(R.id.smearedAddress);
		detailAddress = (TextView) findViewById(R.id.detailAddress);
		checkOutAllPrice = (TextView) findViewById(R.id.checkOutAllPrice);
		title_left = (TextView) findViewById(R.id.title_left);
		title_center = (TextView) findViewById(R.id.title_center);
		checkOutListView = (NoScrollListView) findViewById(R.id.checkOutListView);
		addressRelative = (RelativeLayout) findViewById(R.id.addressRelative);
		
		ClickListener cl = new ClickListener();
		title_left.setText(R.string.sureOrder);
		title_center.setText(R.string.checkOut);
		title_left.setOnClickListener(cl);
		sureCheckOut.setOnClickListener(cl);
		addressRelative.setOnClickListener(cl);
	}
	
	//显示收货人姓名地址等信息
	private void showInfo(int index){
		AddressBean bean = addressList.get(index);
		addresseeName.setText("");
		smearedAddress.setText("");
		detailAddress.setText(MyApplication.user.getAddr());
	}
	
	//获取收货人地址数据集合
	private void getAddressData(){
		ShoppingCanst.addressList = new ArrayList<AddressBean>();
		AddressBean bean = new AddressBean();
		bean.setName("张三");
		bean.setSmearedAddress("湖北省武汉市武昌区");
		bean.setDetailAddress("东湖风景区黄家大湾特1号  15527196048");
		ShoppingCanst.addressList.add(bean);
		AddressBean bean2 = new AddressBean();
		bean2.setName("李四");
		bean2.setSmearedAddress("湖北省武汉市武昌区");
		bean2.setDetailAddress("黄家大湾特1号东湖风景区  18140549110");
		ShoppingCanst.addressList.add(bean2);
	}
	
	//修改地址
	private void updateAddress(){
		Intent intent = new Intent(CheckOutActivity.this,UpdateAddressActivity.class);
		startActivityForResult(intent, REQUESTCODE);
	}
	
	List<NameValuePair> params;
	private String result;
	private String myurl;
	
	//事件点击监听器
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			
			int rid = v.getId();
			if(rid == R.id.sureCheckOut){	//确认点击按钮
				Toast.makeText(getApplicationContext(), "结算完成，总共花费￥"+allPrice, Toast.LENGTH_LONG).show();
			
				
				params =new ArrayList<NameValuePair>();
				
				String orderStr = "";
				
				for(int i = 0 ; i <list.size();i ++){
					ShopBean bean = list.get(i);
					orderStr += MyApplication.user.getAccount()+","+bean.getShopId()+","+bean.getShopName()+","+bean.getShopPrice()+","+bean.getShopNumber()+","+MyApplication.user.getAddr()+";";             
				}
				System.out.println("orderStr : "+orderStr);
				params.add(new BasicNameValuePair("orderStr", orderStr));
				
				myurl = Configer.SERVER_HOST+"/orderAdd.action";
				
				new Thread(getJson).start();
				
				
			
			}else if(rid == R.id.addressRelative){	//修改地址
//				updateAddress();
			}else if(rid == R.id.title_left){		//左标题返回
				finish();
			}
		}
	}
	
	private Runnable getJson = new Runnable() {

		public void run() {
			try {
				result = GetJson(myurl, params);
				handler.sendEmptyMessage(0x00);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	
	/**
	 * 发送post请求获取json字符串
	 * @param url 网站
	 * @param params 参数List
	 * @return json字符串
	 */
	private String GetJson(String url, List<NameValuePair> params) {
		HttpPost httpRequest = new HttpPost(url);
		/*
		 * NameValuePair实现请求参数的封装
		 */
		String strResult = null;
		/* 添加请求参数到请求对象 */
		HttpParams httpParameters1 = new BasicHttpParams();
		HttpConnectionParams
		.setConnectionTimeout(httpParameters1, 10 * 1000);
		HttpConnectionParams.setSoTimeout(httpParameters1, 10 * 1000);
		//设置超时参数
		try {
			httpRequest.setParams(httpParameters1);
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) 
			{
				/* 读返回数据 */
				strResult = EntityUtils.toString(httpResponse.getEntity());
			} 
			else 
			{
				// 获取出现错误
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strResult;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUESTCODE && resultCode == RESULT_OK){
			Bundle bundle = data.getExtras();
			handler.sendMessage(handler.obtainMessage(1, bundle.getInt("addressIndex")));
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
//			if(msg.what == 1){		//异步更改地址	
//				int tempIndex = (Integer)msg.obj;
//				showInfo(tempIndex);
//			}
			
			if (msg.what == 0x00) {
				Log.v("PostGetJson", "" + result);

				if(null != result){
					try {
						JSONObject json= new JSONObject(result);  
						String re = (String)json.get("result");
						String addr = (String)json.get("addr");
						
//						MyApplication.user.setAddr(addr);
						
//						execute(re);
						
						
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}
				
				
			} else if (msg.what == 0x01) {
				Toast.makeText(getApplicationContext(), "获取失败",
						Toast.LENGTH_LONG).show();
			}
			
			
		}
	};
}
