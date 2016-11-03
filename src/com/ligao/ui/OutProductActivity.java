package com.ligao.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.ksoap2.serialization.SoapObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ligao.R;
import com.ligao.app.MyApplication;
import com.ligao.bean.Configer;
import com.ligao.bean.Constants;
import com.ligao.entity.Goods;
import com.ligao.entity.JsonInfo;
import com.ligao.entity.Order;
import com.ligao.entity.Product;
import com.ligao.utils.KsoapUtil;
import com.ligao.utils.SpUtil;
import com.ligao.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 出库
 * @author Administrator
 *
 */
public class OutProductActivity extends Activity  implements OnClickListener {

	
//	private TextView baseDataMaintainBack;
	private TextView cate ;
	String category = "";
	private int LOGIN_CODE = 100;
	private ShopAdapter adapter;		//自定义适配器
	private EditText  mNumberEdt;
	private ListView listview;
	private TextView gobackTv;
	private Button operationBt;
	List<Goods> recomendGoods = new ArrayList<Goods>();
	List<Order> outOrderList = new ArrayList<Order>();
	List<Product> productList = new ArrayList<Product>();
	private List<String> list = new ArrayList<String>();
	private Gson gson = new Gson();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.out_product_list);
		
//		category = (String)getIntent().getSerializableExtra("category");
		
//		ToastUtil.showToast(BaseDataGridActivity.this, category);
		
//		cate = (TextView)findViewById(R.id.category);
//		cate.setText(category);
		
		productList = (List<Product>) getIntent().getSerializableExtra("productList");
		
		gobackTv= (TextView) findViewById(R.id.go_back);
		operationBt = (Button) findViewById(R.id.bt_operation);
		mNumberEdt = (EditText) findViewById(R.id.edt_number);
																			
		gobackTv.setOnClickListener(this);
		operationBt.setOnClickListener(this);
		//getData();
		initView();
		
	}

	private void initView() {
		
//		baseDataMaintainBack  = (TextView) findViewById(R.id.list);
		ClickListener cl = new ClickListener();
//		baseDataMaintainBack.setOnClickListener(cl);

		listview = (ListView) this.findViewById(R.id.listview);
		
		adapter = new ShopAdapter(this,productList,handler,R.layout.category_goods_list_main_item);
		listview.setAdapter(adapter);
		
		
	
//		gridview.setAdapter(new DataAdapter());
		listview.setOnItemClickListener(new ItemClickListener());
	}
	

	
	private void getData(){
		params =new ArrayList<NameValuePair>();
		if("蔬菜".equals(category)){
			params.add(new BasicNameValuePair("category_id", "1"));
		}else if("谷物".equals(category)){
			params.add(new BasicNameValuePair("category_id", "2"));
		}else if("肉类".equals(category)){
			params.add(new BasicNameValuePair("category_id", "3"));
		}else if("水果".equals(category)){
			params.add(new BasicNameValuePair("category_id", "4"));
		}else if("干货".equals(category)){
			params.add(new BasicNameValuePair("category_id", "5"));
		}else{
			params.add(new BasicNameValuePair("category_id", "6"));
		}
//		params.add(new BasicNameValuePair(key2, value2));
		
		myurl = Configer.SERVER_HOST+"/goodsSelectByCategory.action";
		
		new Thread(getJson).start();
	}
	
	List<NameValuePair> params;
	private String result;
	private String myurl,soap_action;

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
						    String gpic=(String) o.get("GPic"); 
						    
						    Double gprice =(Double) o.get("GPrice");
						    
						    System.out.println(gpic);
						    
						    System.out.println(gprice);
						    
						    Goods g= new Goods();
						    g.setGId((Integer) o.getInt("GId"));
						    g.setCategoryId(o.getInt("categoryId"));
						    g.setGName(o.getString("GName"));
						    g.setGDesc(o.getString("GDesc"));
						    g.setGPrice((Double) o.get("GPrice"));
						    g.setG_count((Integer)o.getInt("g_count"));
						    g.setG_reco(o.getString("g_reco"));
						    g.setGPic(Configer.SERVER_HOST+(String) o.get("GPic"));
						    g.setG_integration(o.getInt("g_integration"));
						    g.setG_discount(o.getInt("g_discount"));
						    recomendGoods.add(g);
						}
						initView();
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}
				
				
			} else if (msg.what == 0x02) {//下载出库单
				
				System.out.println(result);
				
				if(null != result){
					try {
						
						JsonInfo jsonInfo = gson.fromJson(result, JsonInfo.class);
						String jsonString = gson.toJson(jsonInfo.getJsonStr());
						getOutOrderExecute(jsonInfo.getMessage(),jsonString);
						
					} catch (Exception e) {
						e.printStackTrace();
					}  
					
				}
				
			}else if (msg.what == 0x01) {
				ToastUtil.showToast(OutProductActivity.this, "服务器链接错误!");
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
	
	/**
	 * 下载出库单Json
	 */
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
	
	
	class ItemClickListener implements OnItemClickListener {
		/**
		 * 点击项时触发事件
		 * 
		 * @param parent 发生点击动作的AdapterView
		 * @param view 在AdapterView中被点击的视图(它是由adapter提供的一个视图)。
		 * @param position 视图在adapter中的位置。
		 * @param rowid 被点击元素的行id。
		 */
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long rowid) {
			// 根据图片进行相应的跳转
			System.out.println("item click ..");
			System.out.println("position : "+position);

			Bundle bundle = new Bundle();
			bundle.putSerializable("goods", recomendGoods.get(position));

			Intent intent = new Intent(OutProductActivity.this, GoodsMoreInfoActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, LOGIN_CODE);
			
		}
	}

	/**
	* 从服务器取图片
	*http://bbs.3gstdy.com
	* @param url
	* @return
	*/
	public static Bitmap getHttpBitmap(String url) {
	     URL myFileUrl = null;
	     Bitmap bitmap = null;
	     try {
	          myFileUrl = new URL(url);
	     } catch (MalformedURLException e) {
	          e.printStackTrace();
	     }
	     try {
	          HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
	          conn.setConnectTimeout(0);
	          conn.setDoInput(true);
	          conn.connect();
	          InputStream is = conn.getInputStream();
	          bitmap = BitmapFactory.decodeStream(is);
	          is.close();
	     } catch (IOException e) {
	          e.printStackTrace();
	     }
	     return bitmap;
	}
	
	class ShopAdapter extends BaseAdapter {

		private Handler mHandler;
		private int resourceId;				//适配器视图资源ID
		private Context context;			//上下午对象
		private List<Product> list;		//数据集合List
		private LayoutInflater inflater;	//布局填充器
		@SuppressLint("UseSparseArrays")
		public ShopAdapter(Context context,List<Product> list
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
			Product bean = list.get(position);
			ViewHolder holder = null;
			if(convertView == null){
				convertView = inflater.inflate(resourceId,null);
				holder = new ViewHolder();
				//holder.shop_photo = (ImageView) convertView.findViewById(R.id.shop_photo);
				holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
				holder.shop_description = (TextView) convertView.findViewById(R.id.shop_description);
				holder.shop_price = (TextView) convertView.findViewById(R.id.shop_price);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			Log.d("GoodsList", "pic : "+ bean.getWId());
			
			System.out.println("pic : "+ bean.getWId());
			
//			holder.shop_photo.setImageBitmap(returnBitMap(bean.getGPic())); 
			
			
			//ImageLoader.getInstance().displayImage(bean.getWId(), holder.shop_photo);
			
			holder.shop_name.setText("产品名称:"+bean.getPName());
			holder.shop_description.setText("发货量(箱):"+bean.getPlanAmount());
			
			
			holder.shop_price.setText("规格:"+bean.getSpec());
			return convertView;
		}
		
		private final class ViewHolder{
			public ImageView shop_photo;		//商品图片
			public TextView shop_name;			//商品名称
			public TextView shop_description;	//商品描述
			public TextView shop_price;			//商品价格
		}
		
	}

	public Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	

	// 事件点击监听器
	private final class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.list: // 返回
				OutProductActivity.this.finish();
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.go_back:
			finish();
			break;
		case R.id.bt_operation:
			operation();
			break;
		default:
			break;
		}
		
	}

	/**
	 * 下载出库单得到服务器返回数据，进行操作
	 * @param re
	 * @param jsonString
	 */
	private void getOutOrderExecute(String re,String jsonString){
		ToastUtil.showToast(getApplicationContext(), re);
		if("下载成功".equals(re)){
			re = "[{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'cb2aa38c-1dab-47cd-ab0b-b24e78146fc5','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:50','OEndDateTime':'2016-11-01T14:40:59','CDateTime':'2016-11-01T14:40:59','State':1.0,'WCode':'20161101144050353','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'8d0644ee-0a40-43f1-988f-4bb099a4b206','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':4.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'cb2aa38c-1dab-47cd-ab0b-b24e78146fc5','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'8c88c300-8401-4797-8f52-cf5cde3fb340','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:37','OEndDateTime':'2016-11-01T14:40:47','CDateTime':'2016-11-01T14:40:47','State':1.0,'WCode':'20161101144037961','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'ff87ec78-d98f-4fe0-b61d-a25f7bc5acc0','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':6.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'8c88c300-8401-4797-8f52-cf5cde3fb340','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'2385aa63-e82f-4220-b9f9-13bff4713b2d','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:17:55','OEndDateTime':'2016-11-01T14:50:23','CDateTime':'2016-11-01T14:18:07','State':1.0,'WCode':'20161101141755351','ReceiveId':'','Operator':'管理员','WwaybillProducts':[{'Id':'07ede8fd-f4e4-4bff-92e0-12dcce07d87c','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':3.0,'PlanSingleAmount':0.0,'FactsAmount':1.0,'FactsSingleAmount':0.0,'WId':'2385aa63-e82f-4220-b9f9-13bff4713b2d','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0}]";
			SpUtil.putString(getApplicationContext(), Constants.OUT_ORDERS, re);
			
		}else if("暂时没有出库单".equals(re)){
			re = "[{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'cb2aa38c-1dab-47cd-ab0b-b24e78146fc5','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:50','OEndDateTime':'2016-11-01T14:40:59','CDateTime':'2016-11-01T14:40:59','State':1.0,'WCode':'20161101144050353','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'8d0644ee-0a40-43f1-988f-4bb099a4b206','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':4.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'cb2aa38c-1dab-47cd-ab0b-b24e78146fc5','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'8c88c300-8401-4797-8f52-cf5cde3fb340','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:37','OEndDateTime':'2016-11-01T14:40:47','CDateTime':'2016-11-01T14:40:47','State':1.0,'WCode':'20161101144037961','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'ff87ec78-d98f-4fe0-b61d-a25f7bc5acc0','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':6.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'8c88c300-8401-4797-8f52-cf5cde3fb340','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'2385aa63-e82f-4220-b9f9-13bff4713b2d','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:17:55','OEndDateTime':'2016-11-01T14:50:23','CDateTime':'2016-11-01T14:18:07','State':1.0,'WCode':'20161101141755351','ReceiveId':'','Operator':'管理员','WwaybillProducts':[{'Id':'07ede8fd-f4e4-4bff-92e0-12dcce07d87c','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':3.0,'PlanSingleAmount':0.0,'FactsAmount':1.0,'FactsSingleAmount':0.0,'WId':'2385aa63-e82f-4220-b9f9-13bff4713b2d','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0}]";
			SpUtil.putString(getApplicationContext(), Constants.OUT_ORDERS, re);
		}
	}
	/**
	 * 加载缓存内出库单
	 */
	private void loadOutOrder() {
		String outOrders = SpUtil.getString(getApplicationContext(), Constants.OUT_ORDERS, "");
		Type type = new TypeToken<ArrayList<Order>>() {}.getType(); 
		outOrderList = gson.fromJson(outOrders, type);
		 initView();
	}  
	
	/**
	 * 操作按钮点击事件
	 */
	private void operation() {
		 new AlertDialog.Builder(OutProductActivity.this).setTitle("操作提示")//设置对话框标题  
		  
	     .setMessage("请选择操作类型！")//设置显示的内容  
	  
	     .setPositiveButton("垛数量",new DialogInterface.OnClickListener() {
	  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {
	        	 new Thread(getJamNumberJson).start();
	         }  
	  
	     }).setNegativeButton("出库",new DialogInterface.OnClickListener() {
	  
	         @Override  
	  
	         public void onClick(DialogInterface dialog, int which) {//响应事件  
	        	 new Thread(getJamNumberJson).start();
	         }
	     }).show();//在按键响应事件中显示此对话框  
	  
	}
	
	/**
	 * 获取垛数量
	 */
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

}
