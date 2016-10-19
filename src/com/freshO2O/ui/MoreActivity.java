package com.freshO2O.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.freshO2O.R;
import com.freshO2O.ui.base.BaseActivity;
import com.freshO2O.utils.ToastUtil;

public class MoreActivity extends BaseActivity implements OnClickListener{
	
	
	Button add;
	
	EditText personal_favour_gallery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_more);
		
		add = (Button)findViewById(R.id.add);
		personal_favour_gallery = (EditText)findViewById(R.id.personal_favour_gallery);
		
		add.setOnClickListener(this);
		
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

		case R.id.add:
			String text =personal_favour_gallery.getText().toString();
			if(null == text || text.length() == 0){
				ToastUtil.showToast(this, "请输入内容！");
			}else{
				ToastUtil.showToast(this, "添加完成！");
				
			}
			
			break;
		

		default:
			break;
		}
	}

}
