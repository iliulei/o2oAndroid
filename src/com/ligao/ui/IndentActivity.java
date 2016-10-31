package com.ligao.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ligao.R;
import com.ligao.app.MyApplication;
import com.ligao.bean.Configer;
import com.ligao.entity.Indent;
import com.ligao.utils.ToastUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class IndentActivity extends Activity {

	private TextView baseDataMaintainBack;
	
	private ListView listview;
	
	private ShopAdapter adapter;		//自定义适配器
	
	private int LOGIN_CODE = 100;
	
	private ComfirmDialog comfirmDialog;
	
	private static int orderid ; 
	
	private static int position;
	
	private 
	
	List<Indent> recomendGoods = new ArrayList<Indent>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.activity_indent);
		
		baseDataMaintainBack  = (TextView) findViewById(R.id.list);
		ClickListener cl = new ClickListener();
		baseDataMaintainBack.setOnClickListener(cl);
		
		getData();
	}
	
	private void initView() {
		
		baseDataMaintainBack  = (TextView) findViewById(R.id.list);
		ClickListener cl = new ClickListener();
		baseDataMaintainBack.setOnClickListener(cl);

		listview = (ListView) this.findViewById(R.id.listview);
		
		adapter = new ShopAdapter(this,recomendGoods,handler,R.layout.indent_list_main_item);
		listview.setAdapter(adapter);
		
	}
	
	class ShopAdapter extends BaseAdapter {

		private Handler mHandler;
		private int resourceId;				//适配器视图资源ID
		private Context context;			//上下午对象
		private List<Indent> list;		//数据集合List
		private LayoutInflater inflater;	//布局填充器
		@SuppressLint("UseSparseArrays")
		public ShopAdapter(Context context,List<Indent> list
				,Handler mHandler,int resourceId){
			this.list = list;
			this.context = context;
			this.mHandler = mHandler;
			this.resourceId = resourceId;
			inflater = LayoutInflater.from(context);
		}
	
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final Indent bean = list.get(position);
			ViewHolder holder = null;
			if(convertView == null){
				convertView = inflater.inflate(resourceId,null);
				holder = new ViewHolder();
				holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
				holder.shop_description = (TextView) convertView.findViewById(R.id.shop_description);
				holder.shop_price = (TextView) convertView.findViewById(R.id.shop_price);
				holder.save = (Button) convertView.findViewById(R.id.button_save);
				holder.delete = (Button) convertView.findViewById(R.id.button_del);
				holder.info = (Button) convertView.findViewById(R.id.info222);
				
				
				holder.info.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						System.out.println("click");
//						ToastUtil.showToast(IndentActivity.this, String.valueOf(bean.getId()));
						
						delOrder(bean.getId(),list.indexOf(bean));
						
						Bundle bundle = new Bundle();
						bundle.putSerializable("indent", bean);

						Intent intent = new Intent(IndentActivity.this, IndentMoreInfoActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, LOGIN_CODE);
					}
				});
				
				holder.delete.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						System.out.println("click");
//						ToastUtil.showToast(IndentActivity.this, String.valueOf(bean.getId()));
						
						delOrder(bean.getId(),list.indexOf(bean));
					}
				});
				
				holder.save.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						System.out.println("click");
//						ToastUtil.showToast(IndentActivity.this, String.valueOf(bean.getId()));
						if("已发货".equals(bean.getOState())){
							updateOrder(bean.getId(),list.indexOf(bean));
						}
					}
				});
				
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
	
			holder.shop_name.setText(bean.getGName());
			holder.shop_description.setText(bean.getOState());
			holder.shop_price.setText("￥"+(bean.getGPrice()*bean.getGNum()));
			
			holder.delete.setVisibility(View.GONE);
			
			
			String state  = bean.getOState();
			if("已发货".equals(state)){
				holder.save.setText("确认收货");
			} else if("已付款".equals(state)){
				holder.save.setText("已付款");
			} else if("订单已完成".equals(state)){
				holder.save.setText("订单已完成");
				holder.delete.setVisibility(View.VISIBLE);
			}
			
			
			return convertView;
		}
		
		private final class ViewHolder{
			public TextView shop_name;			//商品名称
			public TextView shop_description;	//商品描述
			public TextView shop_price;			//商品价格
			public Button save;
			public Button delete;
			public Button info;
		}
		
	}
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x00) {
				Log.v("PostGetJson", "" + result);

				if(null != result){
					try {
						JSONObject json= new JSONObject(result);  
						JSONArray jsonArray=json.getJSONArray("list");  
						recomendGoods.clear();
						for(int i=0;i<jsonArray.length();i++){  
						    JSONObject o =(JSONObject) jsonArray.get(i);  

						    Indent g= new Indent();
						    g.setAccount((String)o.get("account"));
						    g.setId((Integer)o.get("id"));
						    g.setGNum((Integer)o.get("GNum"));
						    g.setOState((String)o.get("OState"));
						    g.setGName((String)o.get("GName"));
						    g.setGPrice((Double)o.getDouble("GPrice"));
						    recomendGoods.add(g);
						    
						}
						Log.v("PostGetJson", "size : " + recomendGoods.size());
						initView();
						
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
	
	private Runnable getJson = new Runnable() {

		public void run() {
			// TODO Auto-generated method stub
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
	
	List<NameValuePair> params;
	private String result;
	private String myurl;
	
	private void getData(){
		params =new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("account", MyApplication.user.getAccount()));
		
		myurl = Configer.SERVER_HOST+"/indentSelect.action";
		
		new Thread(getJson).start();
	}
	
	
	private void delOrder(int id, int posi){
		
		
		comfirmDialog = new ComfirmDialog(this, R.style.mystyle);
		comfirmDialog.show();
		Listener listener = new Listener();
		comfirmDialog.init(listener);
		
		orderid = id;
		
		position = posi;
		
		
	}
	
	private void updateOrder(int id, int posi){
		
		
//		comfirmDialog = new ComfirmDialog(this, R.style.mystyle);
//		comfirmDialog.show();
//		Listener listener = new Listener();
//		comfirmDialog.init(listener);
		
		orderid = id;
		
		position = posi;
		
		params =new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("id", String.valueOf(orderid)));
		
		params.add(new BasicNameValuePair("state", String.valueOf(3)));
		
		myurl = Configer.SERVER_HOST+"/indentEdit.action";
		
//		recomendGoods.remove(position);
		
		recomendGoods.get(position).setOState("订单已完成");
		adapter.notifyDataSetChanged();
		new Thread(getJson).start();
		
//		getData();
		
		
	}
	
	private class Listener implements OnClickListener {
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.confirm_btn: // 全选
				comfirmDialog.dismiss();
				
				
				params =new ArrayList<NameValuePair>();

				params.add(new BasicNameValuePair("id", String.valueOf(orderid)));
				
				myurl = Configer.SERVER_HOST+"/indentDelete.action";
				
				recomendGoods.remove(position);
				
				adapter.notifyDataSetChanged();
				
				new Thread(getJson).start();
				
				break;
			case R.id.cancel_btn:
				comfirmDialog.dismiss();
				break;
			}
		}
	}
	
	// 事件点击监听器
	private final class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.list: // 返回
				IndentActivity.this.finish();
				break;
			}
		}
	}
}
