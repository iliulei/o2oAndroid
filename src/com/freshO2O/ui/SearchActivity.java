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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.freshO2O.R;
import com.freshO2O.bean.Configer;
import com.freshO2O.entity.Goods;
import com.freshO2O.ui.base.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;


public class SearchActivity extends BaseActivity implements OnItemClickListener{

	private ImageButton mImageButton = null;
	List<Goods> mList = new ArrayList<Goods>();
	private AutoCompleteTextView mACTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getData();
		findView();
	}
	
	private void buildAppData() {
		String[] names = { "jbc", "jllen", "jird", "jike", "jook", "cray",
				"javid", "jemon", "jclipse", "felling", "frank", "google",
				"green", "jill", "jook", "jin zhiwen", "jack", "jay", "king",
				"kevin", "kobe", "lily", "lucy", "mike", "nike", "nail",
				"open", "open cv", "panda", "pp", "queue", "ray allen", "risk",
				"tim cook", "T-MAC", "tony allen", "x man", "x phone", "yy",
				"world", "w3c", "zoom", "zhu ziqing","18601159149","1860115912249","1860421159149","1860113259149",
				"刘家辉","溜溜","刘洪还","柳长句","刘家窑"};

		mList = new ArrayList<Goods>();

		Goods pc = new Goods();
		
		
		mList.add(pc);

	}
	
	private void getData(){
		params =new ArrayList<NameValuePair>();
		
		params.add(new BasicNameValuePair("g_reco", "3"));
		
		myurl = Configer.SERVER_HOST+"/goodsSelect.action";
		
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
						    
						    mList.add(g);
						    
						}
						
						findView();
						
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
	
	private void findView() {
		mACTV = (AutoCompleteTextView) findViewById(R.id.mACTV);
		PhoneAdapter mAdapter = new PhoneAdapter(mList, getApplicationContext());
		mACTV.setAdapter(mAdapter);
		mACTV.setThreshold(1); // 设置输入一个字符 提示，默认为2

		mACTV.setOnItemClickListener(this);
	}

	@Override
	protected void findViewById() {
		mImageButton = (ImageButton) findViewById(R.id.search_button);
	}

	@Override
	protected void initView() {
		mImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
//				search();
				
			}
		});
	}
	
	class PhoneAdapter extends BaseAdapter implements Filterable{

		private ArrayFilter mFilter;  
	    private List<Goods> mList;  
	    private Context context;  
	    private ArrayList<Goods> mUnfilteredData;  
	      
	    public PhoneAdapter(List<Goods> mList, Context context) {  
	        this.mList = mList;  
	        this.context = context;  
	    }  
	  
	    @Override  
	    public int getCount() {  
	        return mList==null ? 0:mList.size();  
	    }  
	  
	    @Override  
	    public Object getItem(int position) {  
	        return mList.get(position);  
	    }  
	  
	    @Override  
	    public long getItemId(int position) {  
	          
	        return position;  
	    }  
	  
	    @Override  
	    public View getView(int position, View convertView, ViewGroup parent) {  
	        View view;  
	        ViewHolder holder;  
	        if(convertView==null){  
	            view = View.inflate(context, R.layout.search_list_item, null);  
	              
	            holder = new ViewHolder();  
	            holder.shop_photo = (ImageView) view.findViewById(R.id.shop_photo);
				holder.shop_name = (TextView) view.findViewById(R.id.shop_name);
				
				holder.shop_description = (TextView) view.findViewById(R.id.shop_description);
				holder.shop_price = (TextView) view.findViewById(R.id.shop_price);
				final TextView goodsname = (TextView) view.findViewById(R.id.shop_name);
				view.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						
						System.out.println("click"+goodsname.getText());
						
						String name = String.valueOf(goodsname.getText());
						
						int position = 0 ;
						
						for(int i = 0 ; i< mList.size() ;i++){
							if(mList.get(i).getGName().equals(name)){
								position = i;
								break;
							}
						}
						
						
						Bundle bundle = new Bundle();
						bundle.putSerializable("goods", mList.get(position));

						Intent intent = new Intent(SearchActivity.this, GoodsMoreInfoActivity.class);
						intent.putExtras(bundle);
						startActivityForResult(intent, 200);
						
					}
				});
				
				
				
				
				
	            view.setTag(holder);  
	        }else{  
	            view = convertView;  
	            holder = (ViewHolder) view.getTag();  
	        }  
	          
	        Goods bean = mList.get(position);
	          
	        ImageLoader.getInstance().displayImage(bean.getGPic(), holder.shop_photo);
			
			holder.shop_name.setText(bean.getGName());
			holder.shop_description.setText(bean.getGDesc());
			holder.shop_price.setText("￥"+bean.getGPrice());
	          
	        return view;  
	    }  
	    
	    
	    private final class ViewHolder{
			public ImageView shop_photo;		//商品图片
			public TextView shop_name;			//商品名称
			public TextView shop_description;	//商品描述
			public TextView shop_price;			//商品价格
		}
	    
	    @Override  
	    public Filter getFilter() {  
	        if (mFilter == null) {  
	            mFilter = new ArrayFilter();  
	        }  
	        return mFilter;  
	    }  
	  
	    private class ArrayFilter extends Filter {  
	  
	        @Override  
	        protected FilterResults performFiltering(CharSequence prefix) {  
	            FilterResults results = new FilterResults();  
	  
	            if (mUnfilteredData == null) {  
	                mUnfilteredData = new ArrayList<Goods>(mList);  
	            }  
	  
	            if (prefix == null || prefix.length() == 0) {  
	                ArrayList<Goods> list = mUnfilteredData;  
	                results.values = list;  
	                results.count = list.size();  
	            } else {  
	                String prefixString = prefix.toString().toLowerCase();  
	  
	                ArrayList<Goods> unfilteredValues = mUnfilteredData;  
	                int count = unfilteredValues.size();  
	  
	                ArrayList<Goods> newValues = new ArrayList<Goods>(count);  
	  
	                for (int i = 0; i < count; i++) {  
	                	Goods pc = unfilteredValues.get(i);  
	                    if (pc != null) {  
	                        if(pc.getGName()!=null && pc.getGName().startsWith(prefixString)){  
	                            newValues.add(pc);  
	                        }else if(pc.getGDesc()!=null && pc.getGDesc().startsWith(prefixString)){  
	                            newValues.add(pc);  
	                        }  
	                    }  
	                }  
	                results.values = newValues;  
	                results.count = newValues.size();  
	            }  
	  
	            return results;  
	        }  
	  
	        @Override  
	        protected void publishResults(CharSequence constraint,  
	                FilterResults results) {  
	            //noinspection unchecked  
	            mList = (List<Goods>) results.values;  
	            if (results.count > 0) {  
	                notifyDataSetChanged();  
	            } else {  
	                notifyDataSetInvalidated();  
	            }  
	        }  
	          
	    }  
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Toast.makeText(this, "click ",Toast.LENGTH_SHORT);
		
		Goods pc = mList.get(position);
//		mACTV.setText(pc.getName() + " " + pc.getPhone());
	}
	
	
}
