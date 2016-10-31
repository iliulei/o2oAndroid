package com.ligao.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ligao.R;
import com.ligao.entity.Goods;
import com.ligao.entity.Indent;
import com.ligao.shoppingcart.CheckOutActivity;
import com.ligao.shoppingcart.ShopBean;
import com.ligao.shoppingcart.ShoppingCanst;
import com.ligao.ui.base.BaseActivity;
import com.ligao.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class IndentMoreInfoActivity extends BaseActivity  implements OnClickListener{
	
	private Indent indent;
	private ImageView imageView;
	private TextView goodsName ;
	private TextView goodsPrice ;
	private TextView goodsDesc,address;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.indent_more_info);
		
		
		indent = (Indent)getIntent().getSerializableExtra("indent");
		
		imageView = (ImageView)findViewById(R.id.shop_photo);
		
		
		goodsName = (TextView) findViewById(R.id.goods_name);
		goodsName.setText("商品名称 ： "+indent.getGName());
		
		goodsDesc = (TextView) findViewById(R.id.goods_description);
		goodsDesc.setText("商品数量：   "+indent.getGNum());
		
		goodsPrice = (TextView) findViewById(R.id.goods_price);
		goodsPrice.setText("商品价格:  ￥ "+indent.getGPrice()*indent.getGNum());
		
		address = (TextView) findViewById(R.id.address);
		address.setText("送货地址:   "+"深圳市龙岗");
		
	}

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		

		default:
			break;
		}
	}

}
