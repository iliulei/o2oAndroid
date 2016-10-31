package com.ligao.utils;

import java.io.UnsupportedEncodingException;

import com.ligao.bean.Constants;
import com.ligao.entity.User;


import android.app.Activity;
import android.content.SharedPreferences;

public class ShareSharePreferenceUtil {

	/**
	 * 清除登录状态
	 */
	public static void clearLoginInfo(Activity context) {
		System.out.println("---------------清除登录状态----------------");
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString("loginState", "");
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 保存登录状态
	 */
	public static void saveLoginInfo(Activity context, String loginState) {
		System.out.println("---------------保存登录状态----------------");
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString("loginState", loginState);
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 获取登录状态
	 */
	public static String getLoginInfo(Activity context) {
		System.out.println("---------------获取登录状态----------------");
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		return mySharedPreferences.getString("loginState", "");
	}

	/**
	 * 清除working3DESKey
	 */
	public static void clearUser(Activity context) {
		System.out.println("---------------清除登录状态----------------");
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString("account", "");
		editor.putString("password", "");
		editor.putString("addr", "");
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 保存working3DESKey
	 */
	public static void saveUser(Activity context, User user) {
		System.out.println("---------------保存登录状态----------------");
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		// 用putString的方法保存数据
		editor.putString("account", user.getAccount());
		editor.putString("password", user.getPassword());
		editor.putString("addr", user.getRecvaddr());
		
		// 提交当前数据
		editor.commit();
	}

	/**
	 * 获取working3DESKey
	 */
	public static User getUser(Activity context) {
		System.out.println("---------------获取登录状态----------------");
		SharedPreferences mySharedPreferences = context.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Activity.MODE_PRIVATE);
		User u = new User();
		u.setAccount(mySharedPreferences.getString("account", ""));
		u.setPassword(mySharedPreferences.getString("password", ""));
		u.setRecvaddr(mySharedPreferences.getString("addr", ""));
		return u;
	}


}
