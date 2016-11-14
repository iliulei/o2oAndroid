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
import android.widget.CheckBox;
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
import com.ligao.utils.DateUtil;
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
public class OutProductActivity extends Activity implements OnClickListener {

	// private TextView baseDataMaintainBack;
	private TextView cate;
	String category = "";
	private ShopAdapter adapter; // 自定义适配器
	private EditText mNumberEdt;
	private ListView listview;
	private TextView gobackTv,scanNumberTv;
	private Button operationBt,goBt;
	private CheckBox isStackCb;
	private List<Goods> recomendGoods = new ArrayList<Goods>();
	private List<Order> outOrderList = new ArrayList<Order>();
	private List<Product> overallProductList = new ArrayList<Product>();
	Product overallProduct;
	private Order outOrder;
	private List<String> boxCodeList = new ArrayList<String>();
	
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
		isStackCb = (CheckBox) findViewById(R.id.cb_isStack);
		scanNumberTv = (TextView) findViewById(R.id.tv_scanNumber);
		
		isStackCb.setOnClickListener(this);
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
                go();
            }
        };
        mFilter = new IntentFilter("ACTION_BAR_SCAN");
      //---广播end---
        CountMessage();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		listview = (ListView) this.findViewById(R.id.listview);
		adapter = new ShopAdapter(this, outOrder.getWwaybillProducts(), handler,
				R.layout.list_main_item);
		listview.setAdapter(adapter);
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
			}else if (msg.what == 0x03) {// 验证条形码是否存在
				if(null != result){
					existsBarCodeExecute(result);
				}else{
				}
			}else if (msg.what == 0x04) {// 根据箱码获取所在垛的垛码
				if(null != result){
					System.out.println(result);
					JsonInfo jsonInfo = gson.fromJson(result,JsonInfo.class);
					String jsonString = "";
					if(jsonInfo.getJsonStr()!=null) jsonString = jsonInfo.getJsonStr().toString();
					queryPileCodesExecute(jsonInfo.getMessage(),jsonString);
				}else{
				}
			} else if (msg.what == 0x05) {// 出库
				if(null != result){
					System.out.println(result);
					saveOutWareHouseExecute(result);
				}else{
				}
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
	
	/**
	 * 验证条形码是否存在
	 * @param result
	 */
	private void existsBarCodeExecute(String re) {
		System.out.println("LeoL 验证条形码是否存在RE输出:"+re);
		
		if("0".equals(re)){
			if(isStackCb.isChecked()){//勾选,垛
				new Thread(getQueryPileCodesJson).start();
   			}else{//未勾选,箱
   				overallProductList = outOrder.getWwaybillProducts();
   				overallProductList.remove(overallProduct);
   				BoxList.add(mNumberEdt.getText().toString().trim());
   				overallProduct.setBoxCodeList(BoxList);
   				overallProductList.add(overallProduct);
   				CountMessage();
   				LocalizationInformation();
   			}
		}
		else{
			DiaLogUtils.showDialog(OutProductActivity.this, re, false);
		}
	}
	/**
	 * 根据箱码获取所在垛的箱码集合
	 * @param re
	 */
	private void queryPileCodesExecute(String re,String boxCodes){
		if("操作成功".equals(re)){
			System.out.println(re);
			String [] boxCodeArray = boxCodes.split(",");
			boolean panduan = true;
				for (String  boxCode : boxCodeArray) {//循环返回的箱码集合
					for (String StackBox : StackBoxList) {//循环已扫描垛码集合内存储的箱码
						if(boxCode.equals(StackBox)) {
							panduan = false;
							break;
						}
					}
				}
				if(panduan)StackBoxList.add(mNumberEdt.getText().toString().trim());
				else 	DiaLogUtils.showDialog(OutProductActivity.this, "此垛已扫描，请勿重复扫描!", false);
			overallProductList = outOrder.getWwaybillProducts();
			overallProductList.remove(overallProduct);
			overallProduct.setStackCodeList(StackBoxList);
			overallProductList.add(overallProduct);
			CountMessage();
			LocalizationInformation();
		}else{
			DiaLogUtils.showDialog(OutProductActivity.this, re, false);
		}
	}
	/**
	 * 出库
	 */
	private void saveOutWareHouseExecute(String re){
		if(re.indexOf("操作成功") != -1){
			   //进行中出库单
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
			   
			   //完成的出库单
			   lsOrder = null;
			   String finishOutOrders = SpUtil.getString(getApplicationContext(),
						Constants.FINISH_OUT_ORDERS, ""); 
			   List<Order> finishOutOrderList = gson.fromJson(finishOutOrders, type);
			   if(finishOutOrderList==null) finishOutOrderList = new ArrayList<Order>();
			   boolean isExist = false;
			   for (Order lsOutOrder : finishOutOrderList) {
					if(lsOutOrder.getWCode().equals(outOrder.getWCode())){
						isExist = true;
						lsOrder = lsOutOrder;
						break; 
					}
				}
			   if(lsOrder!=null)finishOutOrderList.remove(lsOrder);
			   outOrder.setHandStatus("2");//进行中
			   outOrder.setFinishDateTime(DateUtil.sdf.format(new Date()));//完成时间
			   finishOutOrderList.add(outOrder);
			   finishOutOrders = gson.toJson(finishOutOrderList);
			   SpUtil.putString(getApplicationContext(), Constants.FINISH_OUT_ORDERS, finishOutOrders);
			   ToastUtil.showToast(getApplicationContext(), "出库成功!");
			   //	DiaLogUtils.showDialog(OutProductActivity.this, "出库成功,请返回出库单列表页!", false);
			   finish();
			}else{
				DiaLogUtils.showDialog(OutProductActivity.this, re , false);
				//DiaLogUtils.showDialog(OutProductActivity.this, "出库遇到错误!", false);
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
	 * 出库
	 */
	private void outWarehouse() {
		if(outOrder.getState()==2 &&"1".equals(outOrder.getHandStatus())){
			new Thread(getSaveOutWareHouseJson).start();
		}else{
			DiaLogUtils.showDialog(OutProductActivity.this,"没有扫描条码，不可出库!", false);
		}
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
						outWarehouse();
						//new Thread(getQueryPileCodesJson).start();
					}
				}).show();// 在按键响应事件中显示此对话框
	}
	
	/**
	 * go/扫码后按钮点击事件
	 */
	private void go() {
		String barCode = mNumberEdt.getText().toString().trim();
           if (VerificationBarCode(barCode))
           {
               BarCodeIsRepeat(barCode);
           }
	}
	
	   /**
	    * 验证条形码
	 * @param barCode
	 * @return
	 * 2016年11月8日 10:23:23
	 * leol
	 */
	private boolean VerificationBarCode(String barCode){
			boolean panduan = true,exist = false;
           if (barCode.length() !=22) //22为箱码长度
           {
        	   DiaLogUtils.showDialog(OutProductActivity.this,"扫瞄条码格式不正确!", false);
        	   panduan = false;
        	   return false;
           }
           barCode = barCode.substring(1, 7);  //7为条码截取长度,截取出的字符串为产品编码
           for (Product product : outOrder.getWwaybillProducts()) {
        	   if(product.getPCode().equals(barCode))
        	   { 	
        		   exist =true;
        	   		break;
        	   	}
           }
           if (!exist)
           {
               DiaLogUtils.showDialog(OutProductActivity.this,"扫瞄条码不在出库产品中!", false);
               panduan = false;
               return false;
           }
           return panduan;
       }
	   /**
	    * 追溯码添加
	    * @param barCode
	    */
	   private void BarCodeIsRepeat(final String barCode){
		    isRemove = false;
		   List<Product> productList =  outOrder.getWwaybillProducts(); //产品集合
           for (Product product : productList) {
        	   
        	   if(!product.getPCode().equals(barCode.substring(1, 7))) continue;//判断产品和箱码是否对应
        	   
        	   overallProduct = product;
        	   list = isStackCb.isChecked()?product.getStackCodeList() : product.getBoxCodeList();//垛，箱区分
        	   BoxList = product.getBoxCodeList() !=null ? product.getBoxCodeList() : new ArrayList<String>();
        	   StackBoxList =product.getStackCodeList() !=null ? product.getStackCodeList() : new ArrayList<String>();
        	   
        	   findResult = BoxList.indexOf(barCode);
        	   findStackResult = StackBoxList.indexOf(barCode);
        	   
        	   
        	   if(findResult !=-1 || findStackResult != -1){
	        	  new AlertDialog.Builder(OutProductActivity.this)
	   				.setTitle("操作提示")
	   				// 设置对话框标题
	   				.setMessage("此箱码已扫描出库，是否移除此箱码出库信息?")
	   				// 设置显示的内容
	   				.setPositiveButton("确定",
	   						new DialogInterface.OnClickListener() {
	   							@Override
	   							public void onClick(DialogInterface dialog,
	   									int which) {
	   								
	   								overallProductList = outOrder.getWwaybillProducts();
		   			   				overallProductList.remove(overallProduct);
		   							  if (findResult != -1)  { 
		   								  BoxList.remove(barCode);
		   								  overallProduct.setBoxCodeList(BoxList);
		   							  } else{
		   								  StackBoxList.remove(barCode);
		   								  overallProduct.setStackCodeList(StackBoxList);
		   							   }
		   							  isRemove = true;
	   			   				overallProductList.add(overallProduct);
	   			   				CountMessage();
	   			   				LocalizationInformation();
	   							}
	   						})
	   				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	   					@Override
	   					public void onClick(DialogInterface dialog, int which) {// 响应事件
	   					}
	   				}).show();// 在按键响应事件中显示此对话框
        	   }else{
        		   new Thread(getExistsBarCodeJson).start();
        	   }
        	   

        	   break;
           }
		   

		 }
       /**
     * 计算总共扫瞄量
     */
	   private void CountMessage(){
		   int singleCount = 0, stackCount = 0;
		   
		   System.out.println(overallProductList);
		   if(overallProductList.size()!=0){
			   outOrder.setWwaybillProducts(overallProductList);
		   }
		   for (Product product : outOrder.getWwaybillProducts()) {
			   if(product.getBoxCodeList()!=null)
			   singleCount += product.getBoxCodeList().size();
			   if(product.getStackCodeList()!=null)
               stackCount += product.getStackCodeList().size();
		   }
		   String text = "已扫瞄:"+stackCount+"垛"+singleCount+"箱";
		   scanNumberTv.setText(text);
       }
	   /**
	 * 扫描缓存修改
	 */
	   private void LocalizationInformation(){
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
		   
		   //进行中出库单
		   lsOrder = null;
		   String startOutOrders = SpUtil.getString(getApplicationContext(),
					Constants.START_OUT_ORDERS, ""); //获取xml中为进行中出库单
		   List<Order> startOutOrderList = gson.fromJson(startOutOrders, type);
		   if(startOutOrderList==null) startOutOrderList = new ArrayList<Order>();
		   boolean isExist = false;
		   for (Order lsOutOrder : startOutOrderList) {
				if(lsOutOrder.getWCode().equals(outOrder.getWCode())){
					isExist = true;
					lsOrder = lsOutOrder;
					break;
				}
			}
		   if(lsOrder!=null)startOutOrderList.remove(lsOrder);
		   outOrder.setHandStatus("1");//进行中
		   outOrder.setState(2);//导入
		   startOutOrderList.add(outOrder);
		   startOutOrders = gson.toJson(startOutOrderList);
		   SpUtil.putString(getApplicationContext(), Constants.START_OUT_ORDERS, startOutOrders);
		   
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
	 * 验证条形码是否存在
	 */
	private Runnable getExistsBarCodeJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE,
						Configer.WcfMethod_ExistsBarCode);
				rpc.addProperty("code", mNumberEdt.getText().toString()
						.trim());
				myurl = Configer.getWcfUrl(getApplicationContext());
				soap_action = Configer.SOAPACTION_FRONT
						+ Configer.WcfMethod_ExistsBarCode;
				result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
				if ("error".equals(result))
					handler.sendEmptyMessage(0x01);
				else
					handler.sendEmptyMessage(0x03);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	/**
	 * 根据箱码获取所在垛的箱码集合
	 */
	private Runnable getQueryPileCodesJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE,
						Configer.WcfMethod_QueryPileCodes);
				rpc.addProperty("boxCode", mNumberEdt.getText().toString()
						.trim());
				myurl = Configer.getWcfUrl(getApplicationContext());
				soap_action = Configer.SOAPACTION_FRONT
						+ Configer.WcfMethod_QueryPileCodes;
				result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
				if ("error".equals(result))
					handler.sendEmptyMessage(0x01);
				else
					handler.sendEmptyMessage(0x04);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	
	/**
	 * 出库
	 */
	private Runnable getSaveOutWareHouseJson = new Runnable() {
		public void run() {
			try {
				SoapObject rpc = new SoapObject(Configer.NAMESPACE,
						Configer.WcfMethod_SaveOutWareHouse);
				String json = gson.toJson(outOrder);
				rpc.addProperty("outWareHouseJson",  json);
				rpc.addProperty("type", 1);
				myurl = Configer.getWcfUrl(getApplicationContext());
				soap_action = Configer.SOAPACTION_FRONT
						+ Configer.WcfMethod_SaveOutWareHouse;
				result = KsoapUtil.GetJsonWcf(rpc, myurl, soap_action);
				if ("error".equals(result))
					handler.sendEmptyMessage(0x01);
				else
					handler.sendEmptyMessage(0x05);
			} catch (Exception e) {
				handler.sendEmptyMessage(0x01);
			}
		}
	};
	
	
	private boolean isRemove;
	private int findResult;
	private int findStackResult;
	private List<String> BoxList,StackBoxList,list = new ArrayList<String>();
	
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
