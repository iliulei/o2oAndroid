package com.ligao.shoppingcart;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ligao.R;


public class ShopActivity extends Activity {

	private CheckBox checkBox;
	private ListView listView;
	private TextView popTotalPrice;		//结算的价格
	private TextView popDelete;			//删除
	private TextView popCheckOut;		//结算
	private LinearLayout layout;		//结算布局
	private ShopAdapter adapter;		//自定义适配器
	private List<ShopBean> list;		//购物车数据集合
	
	private boolean flag = true;		//全选或全取消
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_main);
		initViews();
		init();
	}
	
	//初始化UI界面
	private void initViews(){
		checkBox = (CheckBox) findViewById(R.id.all_check);
		listView = (ListView) findViewById(R.id.main_listView);
		popTotalPrice = (TextView) findViewById(R.id.shopTotalPrice);
		popDelete = (TextView) findViewById(R.id.delete);
//		popRecycle = (TextView) findViewById(R.id.collection);
		popCheckOut = (TextView) findViewById(R.id.checkOut);
		layout = (LinearLayout) findViewById(R.id.price_relative);
		
		ClickListener cl = new ClickListener();
		checkBox.setOnClickListener(cl);
		popDelete.setOnClickListener(cl);
		popCheckOut.setOnClickListener(cl);
//		popRecycle.setOnClickListener(cl);
	}
	
	//初始化数据
	private void init(){
		//getListData();
		list = ShoppingCanst.list;
		adapter = new ShopAdapter(this,list,handler,R.layout.main_item);
		listView.setAdapter(adapter);
	}
	
	//获取集合数据
	private void getListData(){
		ShoppingCanst.list = new ArrayList<ShopBean>();
		ShopBean bean = new ShopBean();
		bean.setShopId(1);
//		bean.setShopPicture(R.drawable.shoes1);
		bean.setStoreName("猪肉");
		bean.setShopName("新鲜猪肉");
		bean.setShopDescription("猪肉啦");
		bean.setShopPrice(32);
		bean.setShopNumber(1);
		bean.setChoosed(false);
//		ShoppingCanst.list.add(bean);
		ShopBean bean2 = new ShopBean();
		bean2.setShopId(2);
//		bean2.setShopPicture(R.drawable.shoes2);
		bean2.setStoreName("蔬菜");
		bean2.setShopName("白萝卜");
		bean2.setShopDescription("白萝卜");
		bean2.setShopPrice(8);
		bean2.setShopNumber(1);
		bean2.setChoosed(false);
//		ShoppingCanst.list.add(bean2);

	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter = new ShopAdapter(this,list,handler,R.layout.main_item);
		listView.setAdapter(adapter);
	}



	//事件点击监听器
	private final class ClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.all_check:		//全选
				selectedAll();
				break;
			case R.id.delete:			//删除
				String shopIndex = deleteOrCheckOutShop();
				showDialogDelete(shopIndex);
				break;
			case R.id.checkOut:			//结算
				goCheckOut();
				break;
			}
		}
	}
	
	//结算
	private void goCheckOut(){
		String shopIndex = deleteOrCheckOutShop();
		Intent checkOutIntent = new Intent(ShopActivity.this,CheckOutActivity.class);
		checkOutIntent.putExtra("shopIndex", shopIndex);
		startActivity(checkOutIntent);
	}
	
	//全选或全取消
	private void selectedAll(){
		for(int i=0;i<list.size();i++){
			ShopAdapter.getIsSelected().put(i, flag);
		}
		adapter.notifyDataSetChanged();
	}
	
	//删除或结算商品
	private String deleteOrCheckOutShop(){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<list.size();i++){
			if(ShopAdapter.getIsSelected().get(i)){
				sb.append(i);
				sb.append(",");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	//弹出对话框询问用户是否删除被选中的商品
	private void showDialogDelete(String str){
		final String[] delShopIndex = str.split(",");
		new AlertDialog.Builder(ShopActivity.this)
		.setMessage("您确定删除这"+delShopIndex.length+"商品吗？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				for(String s : delShopIndex){
					int index = Integer.valueOf(s);
					list.remove(index);
					ShoppingCanst.list.remove(index);
					//连接服务器之后，获取数据库中商品对应的ID，删除商品
//					list.get(index).getShopId();
				}
				flag = false;
				selectedAll();	//删除商品后，取消所有的选择
				flag = true;	//刷新页面后，设置Flag为true，恢复全选功能
			}
		}).setNegativeButton("取消", null)
		.create().show();
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 10){	//更改选中商品的总价格
				float price = (Float)msg.obj;
				if(price > 0){
					popTotalPrice.setText("￥"+price);
					layout.setVisibility(View.VISIBLE);
				}else{
					layout.setVisibility(View.GONE);
				}
			}else if(msg.what == 11){
				//所有列表中的商品全部被选中，让全选按钮也被选中
				//flag记录是否全被选中
				flag = !(Boolean)msg.obj;
				checkBox.setChecked((Boolean)msg.obj);
			}
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

