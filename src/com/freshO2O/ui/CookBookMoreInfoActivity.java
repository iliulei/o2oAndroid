package com.freshO2O.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.freshO2O.R;
import com.freshO2O.entity.CookBook;
import com.freshO2O.entity.Goods;
import com.freshO2O.shoppingcart.CheckOutActivity;
import com.freshO2O.shoppingcart.ShopBean;
import com.freshO2O.shoppingcart.ShoppingCanst;
import com.freshO2O.ui.base.BaseActivity;
import com.freshO2O.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CookBookMoreInfoActivity extends BaseActivity  implements OnClickListener{
	
	private CookBook c;
	private ImageView imageView;
	private TextView cname ;
	private TextView csource ;
	private TextView cmethod;
	private Button add2card;
	private Button addorder;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cookbook_more_info);
		
		
		c = (CookBook)getIntent().getSerializableExtra("cookbook");
		
		imageView = (ImageView)findViewById(R.id.shop_photo);
		
		
		
		ImageLoader.getInstance().displayImage(c.getCpic(),imageView);
		
		
		cname = (TextView) findViewById(R.id.goods_name);
		cname.setText("名称 ： "+c.getCname());
		
		cmethod = (TextView) findViewById(R.id.goods_description);
		cmethod.setText("做法：   "+c.getCmethod());
		
		csource = (TextView) findViewById(R.id.goods_price);
		csource.setText("原料:"+c.getCsource());
		
		add2card = (Button)findViewById(R.id.add2card);
		addorder = (Button)findViewById(R.id.addorder);
		
		add2card.setOnClickListener(this);
		addorder.setOnClickListener(this);
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	private void addToCard(){
		
//		ShopBean bean = new ShopBean();
//		bean.setShopId(goods.getGId());
//		bean.setShopPicture(goods.getGPic());
//		bean.setStoreName(goods.getGName());
//		bean.setShopName(goods.getGName());
//		bean.setShopDescription(goods.getGDesc());
//		bean.setShopPrice((Double)goods.getGPrice());
//		bean.setShopNumber(1);
//		bean.setChoosed(false);
//		ShoppingCanst.list.add(bean);
		
		ToastUtil.showToast(this, "加入成功");
	}
	
	private void addorder(){
		
//		ShopBean bean = new ShopBean();
//		bean.setShopId(goods.getGId());
//		bean.setShopPicture(goods.getGPic());
//		bean.setStoreName(goods.getGName());
//		bean.setShopName(goods.getGName());
//		bean.setShopDescription(goods.getGDesc());
//		bean.setShopPrice((Double)goods.getGPrice());
//		bean.setShopNumber(1);
//		bean.setChoosed(false);
//		ShoppingCanst.list.add(bean);
		
//		Bundle bundle = new Bundle();
//		bundle.putSerializable("bean", bean);
//
//		Intent intent = new Intent(CookBookMoreInfoActivity.this, CheckOutActivity.class);
//		intent.putExtras(bundle);
//		startActivityForResult(intent, 1);
		
		ToastUtil.showToast(this, "加入成功");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.add2card:

			addToCard();

			break;
			
		case R.id.addorder:

			addorder();

			break;

		default:
			break;
		}
	}

}
