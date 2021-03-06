package com.ligao.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ligao.R;

public class WebViewActivity extends Activity  {
	private WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_webview);        
	    init();
	}

	 private void init(){
	        webView = (WebView) findViewById(R.id.webView);
	        //WebView加载web资源
	       
	        //http://www.realgoal.com.cn/
	        webView.loadUrl("http://h1.rrxiu.me/v/rqlb1l?iframe=1&from_code=53150ab529dbf287a98b20eb75926075");
	       
	       WebSettings settings = webView.getSettings();
	       settings.setJavaScriptEnabled(true);
	       settings.setUseWideViewPort(true); 
	       settings.setLoadWithOverviewMode(true); 
	       //支持屏幕缩放
	        settings.setSupportZoom(true);
	        settings.setBuiltInZoomControls(true);
	        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
	       webView.setWebViewClient(new WebViewClient(){
	           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            // TODO Auto-generated method stub
	               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	             view.loadUrl(url);
	            return true;
	        }
	       });
	    }
	    
}
