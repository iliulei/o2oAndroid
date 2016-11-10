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
import java.util.Date;
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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.ligao.utils.DiaLogUtils;
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
public class OutActivity extends Activity implements OnClickListener {

	private TextView cate;
	String category = "";
	private int LOGIN_CODE = 100;
	private ShopAdapter adapter; // 自定义适配器

	private ListView listview;
	private TextView gobackTv;
	private Button operationBt;
	List<Goods> recomendGoods = new ArrayList<Goods>();
	List<Order> outOrderList = new ArrayList<Order>();

	private List<String> list = new ArrayList<String>();
	private Gson gson = new Gson();
	Type type = new TypeToken<ArrayList<Order>>() {}.getType();
	
	int ii = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.out_order_list);

		gobackTv = (TextView) findViewById(R.id.go_back);
		operationBt = (Button) findViewById(R.id.bt_operation);
		gobackTv.setOnClickListener(this);
		operationBt.setOnClickListener(this);
		
		listview = (ListView) this.findViewById(R.id.listview);
		listview.setEmptyView(findViewById(R.id.layout_empty));
		loadOutOrder();
	}
	
	
	

	private void initView() {
		adapter = new ShopAdapter(this, outOrderList, handler,
				R.layout.list_main_item);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new ItemClickListener());
		listview.setOnItemLongClickListener(new ItemLongClickListener());
		
	}

	private String result;
	private String myurl, soap_action;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x02) {// 下载出库单
				if (null != result) {
					try {
						JsonInfo jsonInfo = gson.fromJson(result,
								JsonInfo.class);
						String jsonString = gson.toJson(jsonInfo.getJsonStr());
						getOutOrderExecute(jsonInfo.getMessage(), jsonString);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (msg.what == 0x01) {
				ToastUtil.showToast(OutActivity.this, "服务器链接错误!");
			}
		}
	};

	/**
	 * 下载出库单Json
	 */
	private Runnable getOutOrderJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE,
						Configer.WcfMethod_DownLoadOutOrder);
				MyApplication application = (MyApplication) getApplication();
				rpc.addProperty("mNode", application.getUserInfo()
						.getDeptCode());
				rpc.addProperty("BillType", 0);
				myurl = Configer.getWcfUrl(getApplicationContext());
				soap_action = Configer.SOAPACTION_FRONT
						+ Configer.WcfMethod_DownLoadOutOrder;
				result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
				System.out.println(result);
				if ("error".equals(result))
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
			Bundle bundle = new Bundle();
			bundle.putSerializable("outOrder", (Serializable) outOrderList
					.get(position));
			Intent intent = new Intent(OutActivity.this,
					OutProductActivity.class);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	/**
	 * 长按listview方法
	 * @author Administrator
	 *
	 */
	class ItemLongClickListener implements OnItemLongClickListener{
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			final Order outOrder = outOrderList.get(position);
			
			String WCode = outOrder.getWCode();
			final Integer State = outOrder.getState();
			 new AlertDialog.Builder(OutActivity.this)
				.setTitle("操作提示")
				// 设置对话框标题
				.setMessage("确定删除订单号为:"+WCode+"订单吗?")
				// 设置显示的内容
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
								if(State == 1){//未进行
									   //未开始出库单
									   String outOrders = SpUtil.getString(getApplicationContext(),
												Constants.NOT_START_OUT_ORDERS, ""); //获取xml中为开始出库单
										Type type = new TypeToken<ArrayList<Order>>() {}.getType();
										outOrderList = gson.fromJson(outOrders, type);
										Order lsOrder = null;
										for (Order lsOutOrder : outOrderList) {
											if(lsOutOrder.getWCode().equals(outOrder.getWCode())){
												lsOrder = lsOutOrder;break;
											}
										}
										if(lsOrder!=null)outOrderList.remove(lsOrder);//删除
									   String notStartOutOrders = gson.toJson(outOrderList);
									   SpUtil.putString(getApplicationContext(), Constants.NOT_START_OUT_ORDERS, notStartOutOrders);
									
								}else if(State == 2){//进行中
									 String outOrders = SpUtil.getString(getApplicationContext(),
												Constants.START_OUT_ORDERS, ""); 
										Type type = new TypeToken<ArrayList<Order>>() {}.getType();
										outOrderList = gson.fromJson(outOrders, type);
										Order lsOrder = null;
										for (Order lsOutOrder : outOrderList) {
											if(lsOutOrder.getWCode().equals(outOrder.getWCode())){
												lsOrder = lsOutOrder;break;
											}
										}
										if(lsOrder!=null)outOrderList.remove(lsOrder);//删除
									   String  startOutOrders = gson.toJson(outOrderList);
									   SpUtil.putString(getApplicationContext(), Constants.START_OUT_ORDERS, startOutOrders);
								}
								   //进行中出库单
								   Order lsOrder = null;
								   String deleteOutOrders = SpUtil.getString(getApplicationContext(),
											Constants.DELETE_OUT_ORDERS, ""); 
								   List<Order> deleteOutOrderList = gson.fromJson(deleteOutOrders, type);
								   if(deleteOutOrderList==null) deleteOutOrderList = new ArrayList<Order>();
								   boolean isExist = false;
								   for (Order lsOutOrder : deleteOutOrderList) {
										if(lsOutOrder.getWCode().equals(outOrder.getWCode())){
											isExist = true;
											lsOrder = lsOutOrder;
											break;
										}
									}
								   if(lsOrder!=null)deleteOutOrderList.remove(lsOrder);
								   outOrder.setHandStatus("3");//进行中
								   deleteOutOrderList.add(outOrder);
								   deleteOutOrders = gson.toJson(deleteOutOrderList);
								   SpUtil.putString(getApplicationContext(), Constants.DELETE_OUT_ORDERS, deleteOutOrders);
								   
								   loadOutOrder();
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {// 响应事件
							}
						}).show();// 在按键响应事件中显示此对话框
			return true;
			
		}
	}
	
	

	class ShopAdapter extends BaseAdapter {
		private Handler mHandler;
		private int resourceId; // 适配器视图资源ID
		private Context context; // 上下午对象
		private List<Order> list; // 数据集合List
		private LayoutInflater inflater; // 布局填充器

		@SuppressLint("UseSparseArrays")
		public ShopAdapter(Context context, List<Order> list, Handler mHandler,
				int resourceId) {
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
			Order bean = list.get(position);
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
			holder.name_One.setText("订单号:" + bean.getWCode());
			holder.name_Two.setText("状态:" + bean.getHandStatusName());
			holder.name_Two.setTextColor(Color.parseColor("#ff0000"));
			holder.name_Three.setText("接收地址:" + bean.getOName());
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
				OutActivity.this.finish();
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
	 * 
	 * @param re
	 * @param jsonString
	 */
	private void getOutOrderExecute(String re, String jsonString) {
		
		if ("下载成功".equals(re)) {
			
			//获取缓存内未进行出库单
			String outOrders = SpUtil.getString(getApplicationContext(),
					Constants.NOT_START_OUT_ORDERS, "");
			if(!"".equals(outOrders))
			outOrderList = gson.fromJson(outOrders, type);
			//将下载的出库单填进缓存
			List<Order> downloadOrderList = new ArrayList<Order>();
			downloadOrderList = gson.fromJson(jsonString, type);
			for (Order order : downloadOrderList) {
				order.setHandStatus("0");
				outOrderList.add(order);
			}
			SpUtil.putString(getApplicationContext(), Constants.NOT_START_OUT_ORDERS, gson.toJson(outOrderList));
			DiaLogUtils.showDialog(OutActivity.this, re, false);
		} else if ("暂时没有出库单".equals(re)) {
			DiaLogUtils.showDialog(OutActivity.this, "暂时没有需要下载的出库单", false);
	//		re = "[{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产11工厂','ONode':'0001','OName':'总111仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'cb2aa38c-1dab-47cd-ab0b-b24e78146fc5','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:50','OEndDateTime':'2016-11-01T14:40:59','CDateTime':'2016-11-01T14:40:59','State':1.0,'WCode':'20161101144050353','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'8d0644ee-0a40-43f1-988f-4bb099a4b206','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':4.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'cb2aa38c-1dab-47cd-ab0b-b24e78146fc5','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'8c88c300-8401-4797-8f52-cf5cde3fb340','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:37','OEndDateTime':'2016-11-01T14:40:47','CDateTime':'2016-11-01T14:40:47','State':1.0,'WCode':'20161101144037961','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'ff87ec78-d98f-4fe0-b61d-a25f7bc5acc0','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':6.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'8c88c300-8401-4797-8f52-cf5cde3fb340','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'2385aa63-e82f-4220-b9f9-13bff4713b2d','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:17:55','OEndDateTime':'2016-11-01T14:50:23','CDateTime':'2016-11-01T14:18:07','State':1.0,'WCode':'201611011417553521','ReceiveId':'','Operator':'管理员','WwaybillProducts':[{'Id':'07ede8fd-f4e4-4bff-92e0-12dcce07d87c','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':3.0,'PlanSingleAmount':0.0,'FactsAmount':1.0,'FactsSingleAmount':0.0,'WId':'2385aa63-e82f-4220-b9f9-13bff4713b2d','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0}]";
			re = "[{'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总2222仓','FCode':'','ODateTime':'2016-11-01T14:40:24','OEndDateTime':'2016-11-01T14:40:33','CDateTime':'2016-11-01T14:40:33','State':1.0,'WCode':'20161101144024245','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'cb96329f-551f-400b-a1d8-5af4c8b2fb87','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':7.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'ef693368-61dd-4605-8d50-7fbe48a3a348','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'cb2aa38c-1dab-47cd-ab0b-b24e78146fc5','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:50','OEndDateTime':'2016-11-01T14:40:59','CDateTime':'2016-11-01T14:40:59','State':1.0,'WCode':'20161101144050353','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'8d0644ee-0a40-43f1-988f-4bb099a4b206','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':4.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'cb2aa38c-1dab-47cd-ab0b-b24e78146fc5','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'8c88c300-8401-4797-8f52-cf5cde3fb340','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:40:37','OEndDateTime':'2016-11-01T14:40:47','CDateTime':'2016-11-01T14:40:47','State':1.0,'WCode':'20161101144037961','ReceiveId':'','Operator':'','WwaybillProducts':[{'Id':'ff87ec78-d98f-4fe0-b61d-a25f7bc5acc0','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':6.0,'PlanSingleAmount':0.0,'FactsAmount':0.0,'FactsSingleAmount':0.0,'WId':'8c88c300-8401-4797-8f52-cf5cde3fb340','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0},{'WId':'2385aa63-e82f-4220-b9f9-13bff4713b2d','WType':0.0,'MNode':'1001','MName':'生产工厂','ONode':'0001','OName':'总仓','FCode':'','ODateTime':'2016-11-01T14:17:55','OEndDateTime':'2016-11-01T14:50:23','CDateTime':'2016-11-01T14:18:07','State':1.0,'WCode':'20161101141755351','ReceiveId':'','Operator':'管理员','WwaybillProducts':[{'Id':'07ede8fd-f4e4-4bff-92e0-12dcce07d87c','PCode':'000001','PName':'小帅羊金装婴儿配方羊奶粉','Unit':'箱','PlanAmount':3.0,'PlanSingleAmount':0.0,'FactsAmount':1.0,'FactsSingleAmount':0.0,'WId':'2385aa63-e82f-4220-b9f9-13bff4713b2d','Spec':'800g/听','Fcode':'69210204-1','PDate':''}],'BillType':0.0}]";
			//---------测试数据---------
			Order mOrder = new Order();
			ii = ii+1;
			mOrder.setOName("仓库测试编号:"+ii);
			mOrder.setWCode(String.valueOf(new Date().getTime()));
			mOrder.setState(1);
			mOrder.setHandStatus("0");
			Product mProduct = new Product();
			mProduct.setSpec("650g/斤");
			mProduct.setPlanAmount(20);
			mProduct.setPName("测试产品"+ii);
			mProduct.setPCode("000001");
			Product mProduct2 = new Product();
			mProduct2.setSpec("300g/斤");
			mProduct2.setPlanAmount(20);
			mProduct2.setPName("演示产品"+ii);
			mProduct2.setPCode("000002");
			List<Product> products = new ArrayList<Product>();
			products.add(mProduct);
			products.add(mProduct2);
			mOrder.setWwaybillProducts(products);
			//---------测试数据---------
			
			//获取缓存内未进行出库单
			String outOrders = SpUtil.getString(getApplicationContext(),
					Constants.NOT_START_OUT_ORDERS, "");
			if(!"".equals(outOrders))
			outOrderList = gson.fromJson(outOrders, type);
			//将下载的出库单填进缓存
				List<Order> downloadOrderList = new ArrayList<Order>();
			downloadOrderList = gson.fromJson(re, type);
			for (Order order : downloadOrderList) {
				outOrderList.add(order);
			}
			
			outOrderList.add(mOrder);
			SpUtil.putString(getApplicationContext(), Constants.NOT_START_OUT_ORDERS, gson.toJson(outOrderList));
		}
		loadOutOrder();
	}

	/**
	 * 加载缓存内出库单
	 */
	private void loadOutOrder() {
		String outOrders = SpUtil.getString(getApplicationContext(),
				Constants.NOT_START_OUT_ORDERS, "");
		String startOutOrders = SpUtil.getString(getApplicationContext(),
				Constants.START_OUT_ORDERS, "");
		outOrderList = new ArrayList<Order>();
		if(!"".equals(startOutOrders)){
			outOrderList.addAll((List<Order>)gson.fromJson(startOutOrders, type));
		}
		if(!"".equals(outOrders)){
			outOrderList.addAll((List<Order>)gson.fromJson(outOrders, type));
		}
		initView();
	}

	
	/**
	 * 下载
	 */
	private void operation() {
			new Thread(getOutOrderJson).start();
	}
	@Override
	protected void onStart() {
		loadOutOrder();
		super.onStart();
	}
	
	/**
	 * 操作按钮点击事件  dilog方式，（下载出库单，加载出库单） 2016年11月4日 14:39:19
	private void operation() {
		
		new AlertDialog.Builder(OutActivity.this)
				.setTitle("操作提示")
				// 设置对话框标题
				.setMessage("请选择操作类型！")
				// 设置显示的内容
				.setPositiveButton("下载出库单",
						new DialogInterface.OnClickListener() {// 添加下载出库单按钮
							@Override
							public void onClick(DialogInterface dialog,
									int which) {// 下载出库单按钮的响应事件
								new Thread(getOutOrderJson).start();
							}
						})
				.setNegativeButton("加载出库单",
						new DialogInterface.OnClickListener() {// 添加加载出库单按钮

							@Override
							public void onClick(DialogInterface dialog,
									int which) {// 响应事件
								loadOutOrder();
								ToastUtil.showToast(getApplicationContext(),
										"加载");

							}

						}).show();// 在按键响应事件中显示此对话框

	}*/

}
