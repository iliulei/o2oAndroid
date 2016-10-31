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
import com.ligao.shoppingcart.CheckOutActivity;
import com.ligao.shoppingcart.ShopBean;
import com.ligao.shoppingcart.ShoppingCanst;
import com.ligao.ui.base.BaseActivity;
import com.ligao.utils.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GoodsMoreInfoActivity extends BaseActivity  implements OnClickListener{
	
	private Goods goods;
	private ImageView imageView;
	private TextView goodsName ;
	private TextView goodsPrice ;
	private TextView goodsDesc;
	private Button add2card;
	private Button addorder;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cookbook_more_info);
		
		
		goods = (Goods)getIntent().getSerializableExtra("goods");
		
		imageView = (ImageView)findViewById(R.id.shop_photo);
		
		
		System.out.println(goods);
		System.out.println(goods.getGPic());
		
		ImageLoader.getInstance().displayImage(goods.getGPic(),imageView);
		
		
		goodsName = (TextView) findViewById(R.id.goods_name);
		goodsName.setText("商品名称 ： "+goods.getGName());
		
		goodsDesc = (TextView) findViewById(R.id.goods_description);
		goodsDesc.setText("商品描述：   "+goods.getGDesc());
		
		goodsPrice = (TextView) findViewById(R.id.goods_price);
		goodsPrice.setText("商品价格  ￥ "+goods.getGPrice());
		
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
		
		ShopBean bean = new ShopBean();
		bean.setShopId(goods.getGId());
		bean.setShopPicture(goods.getGPic());
		bean.setStoreName(goods.getGName());
		bean.setShopName(goods.getGName());
		bean.setShopDescription(goods.getGDesc());
		bean.setShopPrice((Double)goods.getGPrice());
		bean.setShopNumber(1);
		bean.setChoosed(false);
		ShoppingCanst.list.add(bean);
		
		ToastUtil.showToast(this, "加入成功");
	}
	
	private void addorder(){
		
		ShopBean bean = new ShopBean();
		bean.setShopId(goods.getGId());
		bean.setShopPicture(goods.getGPic());
		bean.setStoreName(goods.getGName());
		bean.setShopName(goods.getGName());
		bean.setShopDescription(goods.getGDesc());
		bean.setShopPrice((Double)goods.getGPrice());
		bean.setShopNumber(1);
		bean.setChoosed(false);
//		ShoppingCanst.list.add(bean);
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("bean", bean);

		Intent intent = new Intent(GoodsMoreInfoActivity.this, CheckOutActivity.class);
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);
		
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
