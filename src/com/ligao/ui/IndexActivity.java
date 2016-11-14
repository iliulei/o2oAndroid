package com.ligao.ui;

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

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.ligao.R;
import com.ligao.adapter.IndexGalleryAdapter;
import com.ligao.bean.Configer;
import com.ligao.entity.Goods;
import com.ligao.entity.IndexGalleryItemData;
import com.ligao.ui.base.BaseActivity;
import com.ligao.utils.CommonTools;
import com.ligao.utils.ToastUtil;
import com.ligao.widgets.HomeSearchBarPopupWindow;
import com.ligao.widgets.HomeSearchBarPopupWindow.onSearchBarItemClickListener;
import com.ligao.widgets.jazzviewpager.JazzyViewPager;
import com.ligao.widgets.jazzviewpager.JazzyViewPager.TransitionEffect;
import com.ligao.widgets.jazzviewpager.OutlineContainer;
import com.nostra13.universalimageloader.core.ImageLoader;

public class IndexActivity extends BaseActivity implements OnClickListener,
		onSearchBarItemClickListener {
	public static final String TAG = IndexActivity.class.getSimpleName();

	private int LOGIN_CODE = 100;
	
	private ImageView mMiaoShaImage = null;
	private TextView mIndexHour = null;
	private TextView mIndexMin = null;
	private TextView mIndexSeconds = null;
	private TextView mIndexPrice = null;
	private TextView mIndexRawPrice = null;
	private long mExitTime;
	//=============中部导航栏模块=====
//	private ImageButton shake;
	private Intent mIntent;
	
	// ============== 广告切换 ===================
	private JazzyViewPager mViewPager = null;
	/**
	 * 装指引的ImageView数组
	 */
	private ImageView[] mIndicators;

	/**
	 * 装ViewPager中ImageView的数组
	 */
	private ImageView[] mImageViews;
	private List<String> mImageUrls = new ArrayList<String>();
	private LinearLayout mIndicator = null;
	private String mImageUrl = null;
	private static final int MSG_CHANGE_PHOTO = 1;
	/** 图片自动切换时间 */
	private static final int PHOTO_CHANGE_TIME = 3000;
	// ============== 广告切换 ===================

	private Gallery mStormGallery = null;
	private Gallery mPromotionGallery = null;
	private IndexGalleryAdapter mStormAdapter = null;
	private IndexGalleryAdapter mPromotionAdapter = null;
	private List<IndexGalleryItemData> mStormListData = new ArrayList<IndexGalleryItemData>();
	private List<IndexGalleryItemData> mPromotionListData = new ArrayList<IndexGalleryItemData>();
	private IndexGalleryItemData mItemData = null;
	private HomeSearchBarPopupWindow mBarPopupWindow = null;
	private EditText mSearchBox = null;
	private ImageButton mCamerButton = null;
	private LinearLayout mTopLayout = null;
	
	private MyAdapter MyAdapter;
	
	private ImageButton inIbt,outIbt,installJamIbt,breakAJamIbt,exitIbt,dry,reco;
	
	
	List<Goods> recomendGoods = new ArrayList<Goods>();
	

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO总仓首页
		setContentView(R.layout.activity_index); 
		// TODO分仓首页
		//setContentView(R.layout.activity_index_in); 
		mHandler = new Handler(getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_CHANGE_PHOTO:
					int index = mViewPager.getCurrentItem();
					if (index == mImageUrls.size() - 1) {
						index = -1;
					}
					mViewPager.setCurrentItem(index + 1);
					mHandler.sendEmptyMessageDelayed(MSG_CHANGE_PHOTO,
							PHOTO_CHANGE_TIME);
				}
			}
		};
		getData();
		initData();
		findViewById();
		exitIbt = (ImageButton)findViewById(R.id.ibt_exit);
		exitIbt.setOnClickListener(indexClickListener);
		
		//TODO 主分仓，选择index 需注释此代码
		//inIbt = (ImageButton)findViewById(R.id.ibt_in);
		//inIbt.setOnClickListener(indexClickListener);
		
		outIbt = (ImageButton)findViewById(R.id.ibt_out);
		outIbt.setOnClickListener(indexClickListener);
		installJamIbt = (ImageButton)findViewById(R.id.ibt_installJam);
		installJamIbt.setOnClickListener(indexClickListener);
		breakAJamIbt = (ImageButton)findViewById(R.id.ibt_breakAJam);
		breakAJamIbt.setOnClickListener(indexClickListener);
		
	}
	
	
	private OnClickListener indexClickListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent;
			switch (v.getId()) {
			case R.id.ibt_exit:	//退出
				finish();
				break;
			case R.id.ibt_in:	//入库
				intent = new Intent(IndexActivity.this, InActivity.class);
				startActivity(intent);
				break;
			case R.id.ibt_out://出库
				intent = new Intent(IndexActivity.this, OutActivity.class);
				startActivity(intent);
				break;
			case R.id.ibt_installJam:   
				intent = new Intent(IndexActivity.this, InstallJamActivity.class);
				startActivity(intent);
				break;
			case R.id.ibt_breakAJam:
				intent = new Intent(IndexActivity.this, BreakAJamActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
			
		}
	};
	
	List<NameValuePair> params;
	private String result;
	private String myurl;
	
	private void getData(){
		params =new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("g_reco", "1"));
//		params.add(new BasicNameValuePair(key2, value2));
		myurl = Configer.SERVER_HOST+"/goodsSelect.action";
		new Thread(getJson).start();
	}
	
	private Runnable getJson=new Runnable()
	{

		public void run() {
			// TODO Auto-generated method stub
			try
			{
				
				result=GetJson(myurl, params);
				handler.sendEmptyMessage(0x00);
			}
			catch(Exception e)
			{
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
						    g.setGPrice((Double)o.get("GPrice"));
						    g.setG_count((Integer)o.getInt("g_count"));
						    g.setG_reco(o.getString("g_reco"));
						    g.setGPic(Configer.SERVER_HOST+gpic);
						    g.setG_integration(o.getInt("g_integration"));
						    g.setG_discount(o.getInt("g_discount"));
						    
						    recomendGoods.add(g);
						    
						    mImageUrl = Configer.SERVER_HOST+gpic;
						    
						    System.out.println("mImageUrl : "+mImageUrl);
						    
						    mImageUrls.add(mImageUrl);
						    
						}
						
						
						
						System.out.println("mImageUrls : "+mImageUrls.size());
						
						
						
						initView();
//						MyAdapter.notifyDataSetChanged();
						
						
						
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}else{//不通过JAVA服务器设置图片，直接添加固定图片地址
					initView();
				}
				
				
			} else if (msg.what == 0x01) {
				Toast.makeText(getApplicationContext(), "获取失败",
						Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	protected void findViewById() {
		// TODO Auto-generated method stub
		mIndexHour = (TextView) findViewById(R.id.index_miaosha_hour);
		mIndexMin = (TextView) findViewById(R.id.index_miaosha_min);
		mIndexSeconds = (TextView) findViewById(R.id.index_miaosha_seconds);
		mIndexPrice = (TextView) findViewById(R.id.index_miaosha_price);
		mIndexRawPrice = (TextView) findViewById(R.id.index_miaosha_raw_price);

		mMiaoShaImage = (ImageView) findViewById(R.id.index_miaosha_image);
		mViewPager = (JazzyViewPager) findViewById(R.id.index_product_images_container);
		mIndicator = (LinearLayout) findViewById(R.id.index_product_images_indicator);

		mStormGallery = (Gallery) findViewById(R.id.index_jingqiu_gallery);
		mPromotionGallery = (Gallery) findViewById(R.id.index_tehui_gallery);

	/*	mSearchBox = (EditText) findViewById(R.id.index_search_edit);
		mCamerButton = (ImageButton) findViewById(R.id.index_camer_button);
		mTopLayout = (LinearLayout) findViewById(R.id.index_top_layout);
		屏蔽头部搜索
		*/
		
//		shake=(ImageButton)findViewById(R.id.index_shake);
//		
//		//添加事件
//		shake.setOnClickListener(indexClickListener);
	}

	
	
	
	@Override
	protected void initView() {
		
		// TODO Auto-generated method stub
		ImageLoader.getInstance().displayImage(
				"drawable://" + R.drawable.miaosha, mMiaoShaImage);

		mIndexHour.setText("00");
		mIndexMin.setText("53");
		mIndexSeconds.setText("49");
		mIndexPrice.setText("￥269.99");
		mIndexRawPrice.setText("￥459.99");
		mIndexRawPrice.getPaint().setFlags(
				Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

		// ======= 初始化ViewPager ========
		//轮播图设置一个固定图片地址
		mImageUrls.add("http://pic.58pic.com/58pic/16/88/34/33e58PICt8q_1024.jpg");
		
		mIndicators = new ImageView[mImageUrls.size()];
		if (mImageUrls.size() <= 1) {
			mIndicator.setVisibility(View.GONE);
		}

		for (int i = 0; i < mIndicators.length; i++) {
			ImageView imageView = new ImageView(this);
			LayoutParams params = new LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1.0f);
			if (i != 0) {
				params.leftMargin = 5;
			}
			imageView.setLayoutParams(params);
			mIndicators[i] = imageView;
			if (i == 0) {
				mIndicators[i]
						.setBackgroundResource(R.drawable.android_activities_cur);
			} else {
				mIndicators[i]
						.setBackgroundResource(R.drawable.android_activities_bg);
			}

			mIndicator.addView(imageView);
		}

		mImageViews = new ImageView[mImageUrls.size()];

		for (int i = 0; i < mImageViews.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			mImageViews[i] = imageView;
		}
		mViewPager.setTransitionEffect(TransitionEffect.CubeOut);
		mViewPager.setCurrentItem(0);
		mHandler.sendEmptyMessageDelayed(MSG_CHANGE_PHOTO, PHOTO_CHANGE_TIME);
		MyAdapter = new MyAdapter();
		mViewPager.setAdapter(MyAdapter);
		mViewPager.setOnPageChangeListener(new MyPageChangeListener());
		mViewPager.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (mImageUrls.size() == 0 || mImageUrls.size() == 1)
					return true;
				else
					return false;
			}
		});
		
		// ======= 初始化ViewPager ========

		mStormAdapter = new IndexGalleryAdapter(this,
				R.layout.activity_index_gallery_item, mStormListData,
				new int[] { R.id.index_gallery_item_image,
						R.id.index_gallery_item_text });

		mStormGallery.setAdapter(mStormAdapter);

		mPromotionAdapter = new IndexGalleryAdapter(this,
				R.layout.activity_index_gallery_item, mPromotionListData,
				new int[] { R.id.index_gallery_item_image,
						R.id.index_gallery_item_text });

		mPromotionGallery.setAdapter(mPromotionAdapter);
		
		
		
		
		
		
		mStormGallery.setSelection(1);
		mPromotionGallery.setSelection(1);

		mBarPopupWindow = new HomeSearchBarPopupWindow(this,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mBarPopupWindow.setOnSearchBarItemClickListener(this);

		//mCamerButton.setOnClickListener(this);
		//mSearchBox.setOnClickListener(this);

		//mSearchBox.setInputType(InputType.TYPE_NULL);
	}

	private void initData() {

		mItemData = new IndexGalleryItemData();
		mItemData.setId(1);
		//mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_01);
		mItemData.setPrice("￥12.00");
		mStormListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(2);
		//mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_02);
		mItemData.setPrice("￥10.00");
		mStormListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(3);
		//mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_03);
		mItemData.setPrice("￥8.00");
		mStormListData.add(mItemData);



		mItemData = new IndexGalleryItemData();
		mItemData.setId(4);
		//mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_08);
		mItemData.setPrice("￥35.00");
		mPromotionListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(5);
		//mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_09);
		mItemData.setPrice("￥29.00");
		mPromotionListData.add(mItemData);

		mItemData = new IndexGalleryItemData();
		mItemData.setId(6);
		//mItemData.setImageUrl("drawable://" + R.drawable.index_gallery_10);
		mItemData.setPrice("￥45.00");
		mPromotionListData.add(mItemData);


	}

	
	class Data {
		int posi;

		public int getPosi() {
			return posi;
		}

		public void setPosi(int posi) {
			this.posi = posi;
		}
		
		
		
	}
	
	public class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageViews.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof OutlineContainer) {
				return ((OutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(mViewPager
					.findViewFromObject(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			ImageLoader.getInstance().displayImage(mImageUrls.get(position),
					mImageViews[position]);
			
			final Data posi = new Data();
			posi.setPosi(position);
			
			mImageViews[position].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
/*                    int i = posi.getPosi();
                    System.out.println(i);
                    
                    Goods g = recomendGoods.get(i);
                    
                    System.out.println("gName : "+g.getGName());
                    System.out.println("gId : "+g.getGId());
                    
                    Bundle bundle = new Bundle();
        			bundle.putSerializable("goods", recomendGoods.get(i));
        			
        			Intent intent = new Intent(IndexActivity.this, GoodsMoreInfoActivity.class);
        			intent.putExtras(bundle);
        			startActivityForResult(intent, LOGIN_CODE);*/
        			
                	Intent intent = new Intent(IndexActivity.this, WebViewActivity.class);
        			startActivity(intent);
                    
                }
            });
              
			((ViewPager) container).addView(mImageViews[position], 0);
			mViewPager.setObjectForPosition(mImageViews[position], position);
			return mImageViews[position];
		}

	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			setImageBackground(position);
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * 设置选中的tip的背景
	 * 
	 * @param selectItemsIndex
	 */
	private void setImageBackground(int selectItemsIndex) {
		for (int i = 0; i < mIndicators.length; i++) {
			if (i == selectItemsIndex) {
				mIndicators[i]
						.setBackgroundResource(R.drawable.android_activities_cur);
			} else {
				mIndicators[i]
						.setBackgroundResource(R.drawable.android_activities_bg);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		//相机
/*		case R.id.index_camer_button:
			int height = mTopLayout.getHeight()
					+ CommonTools.getStatusBarHeight(this);
			mBarPopupWindow.showAtLocation(mTopLayout, Gravity.TOP, 0, height);
			break;
        //搜索框
		case R.id.index_search_edit:
			openActivity(SearchActivity.class);
			break;*/

		default:
			break;
		}
	}

	@Override
	public void onBarCodeButtonClick() {
		// TODO Auto-generated method stub
//		CommonTools.showShortToast(this, "条码购");
//		mIntent=new Intent(IndexActivity.this, CaptureActivity.class);
//		startActivity(mIntent);
	}

//	@Override
//	public void onCameraButtonClick() {
//		// TODO Auto-generated method stub
////		CommonTools.showShortToast(this, "拍照购");
//	}

	@Override
	public void onColorButtonClick() {
		// TODO Auto-generated method stub
//		CommonTools.showShortToast(this, "颜色购");
	}
	
	
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		   if (keyCode == KeyEvent.KEYCODE_BACK) {
               if ((System.currentTimeMillis() - mExitTime) > 2000) {
                       Object mHelperUtils;
                       ToastUtil.showToast(getApplicationContext(),"再按一次退出程序");
                      // Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                       mExitTime = System.currentTimeMillis();
               } else {
                       finish();
               }
               return true;
       }
       return super.onKeyDown(keyCode, event);
	}
	

}
