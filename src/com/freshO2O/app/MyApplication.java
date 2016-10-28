package com.freshO2O.app;

import com.freshO2O.entity.User;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class MyApplication extends Application{
	private static MyApplication app;
	//同一个类中使用
	public static User user = new User();
	//夸activity使用
	public User userInfo;
	public static MyApplication getInstance(){		
		return app;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		
		System.out.println("=========== MyApplication ==============");

		Context context = this.getApplicationContext();
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			System.out.println("版本代码(int): " + packageInfo.versionCode);
			System.out.println("版本名称(String): " + packageInfo.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
        
        
	}

	public User getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(User userInfo) {
		this.userInfo = userInfo;
	}
	
	
	
}
