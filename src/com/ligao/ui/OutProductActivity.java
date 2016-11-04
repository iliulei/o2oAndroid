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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
 * 
 * @author Administrator
 * 
 */
public class OutProductActivity extends Activity implements OnClickListener {

	// private TextView baseDataMaintainBack;
	private TextView cate;
	String category = "";
	private ShopAdapter adapter; // 自定义适配器
	private EditText mNumberEdt;
	private ListView listview;
	private TextView gobackTv;
	private Button operationBt,goBt;
	List<Goods> recomendGoods = new ArrayList<Goods>();
	List<Order> outOrderList = new ArrayList<Order>();
	List<Product> productList = new ArrayList<Product>();
	private Order outOrder;
	private List<String> list = new ArrayList<String>();
	private Gson gson = new Gson();
	private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.out_product_list);
		outOrder = (Order) getIntent().getSerializableExtra(
				"outOrder");
		gobackTv = (TextView) findViewById(R.id.go_back);
		operationBt = (Button) findViewById(R.id.bt_operation);
		goBt =  (Button) findViewById(R.id.bt_go);
		mNumberEdt = (EditText) findViewById(R.id.edt_number);
		gobackTv.setOnClickListener(this);
		operationBt.setOnClickListener(this);
		goBt.setOnClickListener(this);
		initView();
		
		//---广播begin---
	    mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //此处获取扫描结果信息
                final String scanResult = intent.getStringExtra("EXTRA_SCAN_DATA");
                mNumberEdt.setText(scanResult);
            }
        };
        mFilter = new IntentFilter("ACTION_BAR_SCAN");
      //---广播end---
		
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		listview = (ListView) this.findViewById(R.id.listview);
		adapter = new ShopAdapter(this, outOrder.getWwaybillProducts(), handler,
				R.layout.list_main_item);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new ItemClickListener());
	}

	private String result;
	private String myurl, soap_action;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x02) {// 查询垛数量
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
				}
			}else if (msg.what == 0x03) {// 出库
				
			} else if (msg.what == 0x01) {
				ToastUtil.showToast(OutProductActivity.this, "服务器链接错误!");
			}
		}
	};

	/**
	 * 查询垛数量
	 * @param re
	 * @param jamNumber
	 */
	private void jamNumberExecute(String re,String jamNumber){
		if("查询成功".equals(re)){
			ToastUtil.showToast(getApplicationContext(), "当前垛数量："+jamNumber);
			//nowNumberTv.setText("当前垛数量："+jamNumber);
		}else if("箱码不存在".equals(re)){
			ToastUtil.showToast(getApplicationContext(), re);
			//nowNumberTv.setText(re);
		}else if("此箱码未分垛".equals(re)){
			ToastUtil.showToast(getApplicationContext(), re);
			//nowNumberTv.setText(re);
		}
	}
	
	class ItemClickListener implements OnItemClickListener {
		/**
		 * 点击项时触发事件
		 * 
		 * @param parent
		 *            发生点击动作的AdapterView
		 * @param view
		 *            在AdapterView中被点击的视图(它是由adapter提供的一个视图)。
		 * @param position
		 *            视图在adapter中的位置。
		 * @param rowid
		 *            被点击元素的行id。
		 */
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long rowid) {

		}
	}

	class ShopAdapter extends BaseAdapter {
		private Handler mHandler;
		private int resourceId; // 适配器视图资源ID
		private Context context; // 上下午对象
		private List<Product> list; // 数据集合List
		private LayoutInflater inflater; // 布局填充器

		@SuppressLint("UseSparseArrays")
		public ShopAdapter(Context context, List<Product> list,
				Handler mHandler, int resourceId) {
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
			if (convertView == null) {
				convertView = inflater.inflate(resourceId, null);
				holder = new ViewHolder();
				holder.name_One = (TextView) convertView
						.findViewById(R.id.name_One);
				holder.name_Two = (TextView) convertView
						.findViewById(R.id.name_Two);
				holder.name_Three = (TextView) convertView
						.findViewById(R.id.name_Three);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name_One.setText("产品名称:" + bean.getPName());
			holder.name_Two.setText("发货量(箱):" + bean.getPlanAmount());
			holder.name_Three.setText("规格:" + bean.getSpec());
			return convertView;
		}

		private final class ViewHolder {
			public TextView name_One;
			public TextView name_Two;
			public TextView name_Three;
		}

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
		case R.id.bt_go:
			go();
			break;
		default:
			break;
		}

	}

	/**
	 * 下载出库单得到服务器返回数据，进行操作
	 * 
	 * @param re
	 * @param jsonString
	 */
	private void getOutOrderExecute(String re, String jsonString) {
	}

	/**
	 * 加载缓存内出库单
	 */
	private void loadOutOrder() {
		String outOrders = SpUtil.getString(getApplicationContext(),
				Constants.NOT_START_OUT_ORDERS, "");
		Type type = new TypeToken<ArrayList<Order>>() {
		}.getType();
		outOrderList = gson.fromJson(outOrders, type);
		initView();
	}

	/**
	 * 操作按钮点击事件
	 */
	private void operation() {
		new AlertDialog.Builder(OutProductActivity.this)
				.setTitle("操作提示")
				// 设置对话框标题
				.setMessage("请选择操作类型！")
				// 设置显示的内容
				.setPositiveButton("垛数量",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								new Thread(getJamNumberJson).start();
							}
						})
				.setNegativeButton("出库", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {// 响应事件
						new Thread(getJamNumberJson).start();
					}
				}).show();// 在按键响应事件中显示此对话框
	}
	
	/**
	 * go/扫码后按钮点击事件
	 */
	private void go() {
		String  boxCode = mNumberEdt.getText().toString().trim();
		
	}
	/**
	 * 获取垛数量
	 */
	private Runnable getJamNumberJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE,
						Configer.WcfMethod_QueryPileCount);
				rpc.addProperty("boxCode", mNumberEdt.getText().toString()
						.trim());
				myurl = Configer.getWcfUrl(getApplicationContext());
				soap_action = Configer.SOAPACTION_FRONT
						+ Configer.WcfMethod_QueryPileCount;
				result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
				// result = GetJson(myurl, params);
				if ("error".equals(result))
					handler.sendEmptyMessage(0x01);
				else
					handler.sendEmptyMessage(0x02);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	/**
	 * 查询箱码状态
	 */
	private Runnable getBoxCodeStatusJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE,
						Configer.WcfMethod_QueryPileCount);
				rpc.addProperty("boxCode", mNumberEdt.getText().toString()
						.trim());
				myurl = Configer.getWcfUrl(getApplicationContext());
				soap_action = Configer.SOAPACTION_FRONT
						+ Configer.WcfMethod_QueryPileCount;
				result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
				// result = GetJson(myurl, params);
				if ("error".equals(result))
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
