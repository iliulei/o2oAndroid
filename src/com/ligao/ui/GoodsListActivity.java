package com.ligao.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

import com.ligao.R;
import com.ligao.bean.Configer;
import com.ligao.entity.Goods;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GoodsListActivity extends Activity {

	private TextView baseDataMaintainBack;
	private TextView cate ;
	String category = "";
	private int LOGIN_CODE = 100;
	private ShopAdapter adapter;		//自定义适配器
	private ListView listview;
	
	List<Goods> recomendGoods = new ArrayList<Goods>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.goods_list);
		category = (String)getIntent().getSerializableExtra("category");
		
//		ToastUtil.showToast(BaseDataGridActivity.this, category);
		
		cate = (TextView)findViewById(R.id.category);
		cate.setText(category);
		
		getData();
	}

	private void initView() {
		
		baseDataMaintainBack  = (TextView) findViewById(R.id.list);
		ClickListener cl = new ClickListener();
		baseDataMaintainBack.setOnClickListener(cl);

		listview = (ListView) this.findViewById(R.id.listview);
		
		adapter = new ShopAdapter(this,recomendGoods,handler,R.layout.goods_list_main_item);
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
		}else {
			params.add(new BasicNameValuePair("category_id", "5"));
		}
//		params.add(new BasicNameValuePair(key2, value2));
		
		myurl = Configer.getServerAddress(getApplicationContext())+"/goodsSelectByCategory.action";
		
		new Thread(getJson).start();
	}
	
	List<NameValuePair> params;
	private String result;
	private String myurl;

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
				
				
			} else if (msg.what == 0x01) {
				Toast.makeText(getApplicationContext(), "获取失败",
						Toast.LENGTH_LONG).show();
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

			Intent intent = new Intent(GoodsListActivity.this, GoodsMoreInfoActivity.class);
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
		private List<Goods> list;		//数据集合List
		private LayoutInflater inflater;	//布局填充器
		@SuppressLint("UseSparseArrays")
		public ShopAdapter(Context context,List<Goods> list
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
			Goods bean = list.get(position);
			ViewHolder holder = null;
			if(convertView == null){
				convertView = inflater.inflate(resourceId,null);
				holder = new ViewHolder();
				holder.shop_photo = (ImageView) convertView.findViewById(R.id.shop_photo);
				holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
				holder.shop_description = (TextView) convertView.findViewById(R.id.shop_description);
				holder.shop_price = (TextView) convertView.findViewById(R.id.shop_price);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			Log.d("GoodsList", "pic : "+ bean.getGPic());
			
			System.out.println("pic : "+ bean.getGPic());
			
//			holder.shop_photo.setImageBitmap(returnBitMap(bean.getGPic())); 
			
			
			ImageLoader.getInstance().displayImage(bean.getGPic(), holder.shop_photo);
			
			holder.shop_name.setText(bean.getGName());
			holder.shop_description.setText(bean.getGDesc());
			holder.shop_price.setText("￥"+bean.getGPrice());
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
				GoodsListActivity.this.finish();
				break;
			}
		}
	}
	
}
