package com.freshO2O.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.freshO2O.AddressManageActivity;
import com.freshO2O.IntegrationActivity;
import com.freshO2O.R;
import com.freshO2O.app.MyApplication;
import com.freshO2O.entity.User;
import com.freshO2O.ui.base.BaseActivity;
import com.freshO2O.utils.ShareSharePreferenceUtil;
import com.freshO2O.utils.ToastUtil;
import com.freshO2O.widgets.CustomScrollView;

public class PersonalActivity extends BaseActivity implements OnClickListener {

	private ImageView mBackgroundImageView = null;
	private Button mLoginButton, mRegisterButton;
	private CustomScrollView mScrollView = null;
	private Intent mIntent = null;
	private LinearLayout afterlogin;
	private LinearLayout login,integrationview,addressmanage;
	private RelativeLayout personal,orderselect;
	private TextView username,grade;
	private int LOGIN_CODE = 100;
	

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
		integrationview = (LinearLayout) findViewById(R.id.integrationview);
		
		personal = (RelativeLayout) findViewById(R.id.personal);
		orderselect = (RelativeLayout) findViewById(R.id.orderselect);
		addressmanage = (LinearLayout) findViewById(R.id.addressmanage);
		username = (TextView) findViewById(R.id.username);
		
		grade = (TextView) findViewById(R.id.jobtitle);
	}

	@Override
	protected void initView() {
		mScrollView.setImageView(mBackgroundImageView);

		mLoginButton.setOnClickListener(this);
		mRegisterButton.setOnClickListener(this);
		orderselect.setOnClickListener(this);
		addressmanage.setOnClickListener(this);
		
		integrationview.setOnClickListener(this);

		String loginInfo = ShareSharePreferenceUtil.getLoginInfo(this);
		
		User u = ShareSharePreferenceUtil.getUser(this);
		
		if(null != u){
			if("2".equals(loginInfo)){
				personal.setVisibility(View.VISIBLE);
				afterlogin.setVisibility(View.VISIBLE);
				login.setVisibility(View.GONE);
				
				username.setText(u.getAccount());
			}else if("1".equals(loginInfo)){
				personal.setVisibility(View.GONE);
				afterlogin.setVisibility(View.GONE);
				login.setVisibility(View.VISIBLE);
			}else{
				personal.setVisibility(View.GONE);
				afterlogin.setVisibility(View.GONE);
				login.setVisibility(View.VISIBLE);
			}
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
			
		case R.id.orderselect:
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

}
