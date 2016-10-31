package com.ligao.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ligao.R;
import com.ligao.AddressManageActivity;
import com.ligao.IntegrationActivity;
import com.ligao.app.MyApplication;
import com.ligao.entity.User;
import com.ligao.ui.base.BaseActivity;
import com.ligao.utils.ShareSharePreferenceUtil;
import com.ligao.utils.ToastUtil;
import com.ligao.widgets.CustomScrollView;

public class PersonalActivity extends BaseActivity implements OnClickListener {

	private ImageView mBackgroundImageView = null;
	private Button mLoginButton, mRegisterButton;
	private CustomScrollView mScrollView = null;
	private Intent mIntent = null;
	private LinearLayout afterlogin;
	private LinearLayout login,out;//integrationview,addressmanage
	private RelativeLayout personal;//orderselect
	private TextView username,grade;
	private int LOGIN_CODE = 100;
	private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal);
		findViewById();
		initView();
	}

	@Override
	protected void findViewById() {
		mBackgroundImageView = (ImageView) findViewById(R.id.personal_background_image);
		mLoginButton = (Button) findViewById(R.id.personal_login_button);
		mRegisterButton = (Button) findViewById(R.id.personal_register_button);
		mScrollView = (CustomScrollView) findViewById(R.id.personal_scrollView);

		afterlogin = (LinearLayout) findViewById(R.id.afterlogin);
		
		login = (LinearLayout) findViewById(R.id.login);
		//integrationview = (LinearLayout) findViewById(R.id.integrationview);
		
		personal = (RelativeLayout) findViewById(R.id.personal);
		//orderselect = (RelativeLayout) findViewById(R.id.orderselect);
		//addressmanage = (LinearLayout) findViewById(R.id.addressmanage);
		out = (LinearLayout) findViewById(R.id.out);
		
		username = (TextView) findViewById(R.id.username);
		
		grade = (TextView) findViewById(R.id.jobtitle);
	}

	@Override
	protected void initView() {
		mScrollView.setImageView(mBackgroundImageView);

		mLoginButton.setOnClickListener(this);
		mRegisterButton.setOnClickListener(this);
		//orderselect.setOnClickListener(this);
		//addressmanage.setOnClickListener(this);
		out.setOnClickListener(this);
		//integrationview.setOnClickListener(this);

		String loginInfo = ShareSharePreferenceUtil.getLoginInfo(this);
		
		User u = ShareSharePreferenceUtil.getUser(this);
		
		if(null != u){
			//if("2".equals(loginInfo)){
				personal.setVisibility(View.VISIBLE);
				afterlogin.setVisibility(View.VISIBLE);
				login.setVisibility(View.GONE);
				MyApplication application= (MyApplication)getApplication();
				username.setText(application.getUserInfo().getName());
		/*	}else if("1".equals(loginInfo)){
				personal.setVisibility(View.GONE);
				afterlogin.setVisibility(View.GONE);
				login.setVisibility(View.VISIBLE);
			}else{
				personal.setVisibility(View.GONE);
				afterlogin.setVisibility(View.GONE);
				login.setVisibility(View.VISIBLE);
			}*/
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personal_login_button:
			mIntent = new Intent(PersonalActivity.this, LoginActivity.class);

			startActivityForResult(mIntent, LOGIN_CODE);
			
			break;

		case R.id.personal_register_button:
			mIntent = new Intent(PersonalActivity.this,
					RegisterRegisterActivity.class);

			startActivityForResult(mIntent, LOGIN_CODE);
			break;
			
		/*case R.id.orderselect:
//			ToastUtil.showToast(this, "订单管理");
			mIntent = new Intent(PersonalActivity.this, IndentActivity.class);

			startActivityForResult(mIntent, LOGIN_CODE);
			break;
		case R.id.integrationview:
//			ToastUtil.showToast(this, "积分查询");
			mIntent = new Intent(PersonalActivity.this, IntegrationActivity.class);

			startActivityForResult(mIntent, LOGIN_CODE);
			break;
			
		case R.id.addressmanage:
//			ToastUtil.showToast(this, "积分查询");
			mIntent = new Intent(PersonalActivity.this, AddressManageActivity.class);

			startActivityForResult(mIntent, LOGIN_CODE);
			break;*/
		case R.id.out:
//			ToastUtil.showToast(this, "退出");
			mIntent = new Intent(PersonalActivity.this, LoginActivity.class);
			ShareSharePreferenceUtil.clearLoginInfo(PersonalActivity.this);
			ShareSharePreferenceUtil.clearUser(PersonalActivity.this);
			startActivity(mIntent);
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == 100) {
//			
//			
//		}
		
		if(null != MyApplication.user.getAccount()){
			personal.setVisibility(View.VISIBLE);
			afterlogin.setVisibility(View.VISIBLE);
			login.setVisibility(View.GONE);
			
			username.setText(MyApplication.user.getAccount());
		}else{
			personal.setVisibility(View.GONE);
			afterlogin.setVisibility(View.GONE);
			login.setVisibility(View.VISIBLE);
		}
		
		
		
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
