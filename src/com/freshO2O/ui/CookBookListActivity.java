package com.freshO2O.ui;

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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.freshO2O.R;
import com.freshO2O.bean.Configer;
import com.freshO2O.entity.CookBook;
import com.freshO2O.ui.base.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CookBookListActivity extends BaseActivity  {

	private ListView catergory_listview;
	private LayoutInflater layoutInflater;
	
	private TextView userAddBack;
	
	static List<CookBook> list = new ArrayList<CookBook>(); 

	private int LOGIN_CODE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_cookbook_list);
		
		getData();
		
		findViewById();
		initView();
	}
	
	private void getData(){
		
		list.clear();
		
		params =new ArrayList<NameValuePair>();
		
//		params.add(new BasicNameValuePair(key2, value2));
		
		myurl = Configer.SERVER_HOST+"/cookBookSelect.action";
		
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
						   
						    
						    CookBook c= new CookBook();
						    c.setCname(o.getString("cname"));
						    c.setCmethod(o.getString("cmethod"));
						    c.setCsource(o.getString("csource"));
						    c.setCpic(Configer.SERVER_HOST+(String) o.get("cpic"));
						    list.add(c);
						    
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
	
	@Override
	protected void findViewById() {
		
		userAddBack  = (TextView) findViewById(R.id.back);
		
		ClickListener cl = new ClickListener();
		userAddBack.setOnClickListener(cl); 
		
		// catergory_listview.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> adapterview, View view,
		// int parent, long id) {
		//
		//
		// }
		// });
	}
	
	//事件点击监听器
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back:
				CookBookListActivity.this.finish();
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub
		
		catergory_listview = (ListView) this.findViewById(R.id.catergory_listview);

		catergory_listview.setAdapter(new CatergoryAdapter());
	}

	private class CatergoryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@SuppressWarnings("null")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = new ViewHolder();
			layoutInflater = LayoutInflater.from(CookBookListActivity.this);

			// 组装数据
			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.activity_cookbook_item, null);
				holder.image = (ImageView) convertView.findViewById(R.id.catergory_image);
				holder.title = (TextView) convertView.findViewById(R.id.catergoryitem_title);
				final TextView text = (TextView) convertView.findViewById(R.id.catergoryitem_title);
				
				convertView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						System.out.println("click");
						
						String title = (String) text.getText();
						System.out.println("title : "+title);
						Bundle bundle = new Bundle();
						
						for(int i = 0 ; i<list.size() ;i ++){
							if(title.equals(list.get(i).getCname())){
								bundle.putSerializable("cookbook", list.get(i));
							}
						}
						

						Intent intent = new Intent(CookBookListActivity.this, CookBookMoreInfoActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, LOGIN_CODE);
						
					}
				});
				
				// 使用tag存储数据
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
//			holder.image.setImageResource(mImageIds[position]);
			
			ImageLoader.getInstance().displayImage(list.get(position).getCpic(),holder.image);
			
			holder.title.setText(list.get(position).getCname());

			return convertView;
		}
	}


	// 适配显示的图片数组
//	private Integer[] mImageIds = { R.drawable.catergory_appliance,
//			R.drawable.catergory_book, R.drawable.catergory_digtcamer,
//			R.drawable.catergory_furnitrue, R.drawable.catergory_mobile };
//	// 给照片添加文字显示(Title)
//	private String[] mTitleValues = { "水果", "蔬菜", "肉类", "谷物", "干货" };


	public static class ViewHolder {
		ImageView image;
		TextView title;
	}

}
