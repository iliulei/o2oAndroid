package com.ligao;

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

import com.ligao.R;
import com.ligao.app.MyApplication;
import com.ligao.bean.Configer;
import com.ligao.entity.Receiveaddr;
import com.ligao.ui.ComfirmDialog;
import com.ligao.utils.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Selection;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AddressManageActivity extends Activity {

	private TextView  userListBack;
	private Button    addButton;
	private Button    editButton;
	private Button    delButton;
	private CheckBox  checkBox;
	private ListView  listView;
	
	private static final int MESSAGETYPE_ERROR = 0x0006;
	
	private ComfirmDialog comfirmDialog;

	public List<Receiveaddr> list;         // 数据集合List
	private AddressAdapter addressAdapter;  // 自定义适配器
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_manage);
		
		checkBox 	 = (CheckBox) findViewById(R.id.all_check);
		userListBack = (TextView) findViewById(R.id.user_list_back);
		addButton 	 = (Button) findViewById(R.id.addButton);
		editButton 	 = (Button) findViewById(R.id.editButton);
		delButton 	 = (Button) findViewById(R.id.delButton);
		listView 	 = (ListView) findViewById(R.id.user_list);
		
		ClickListener cl = new ClickListener();
		checkBox.setOnClickListener(cl);
		userListBack.setOnClickListener(cl);
		addButton.setOnClickListener(cl);
		editButton.setOnClickListener(cl);
		delButton.setOnClickListener(cl);

		getData();
		
	}
	
	
	
	@Override
	protected void onResume() {
//		ToastUtil.showToast(this, "onResume");
//		getData();
		super.onResume();
		
	}



	private void getData(){
		
		list = new ArrayList<Receiveaddr>();
		
		params =new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", MyApplication.user.getAccount()));
		
		myurl = Configer.SERVER_HOST+"/receiveaddrSelect.action";
		
		new Thread(getJson).start();
	}
	
	private void initview(){
		
//		ToastUtil.showToast(this, ""+list.size());
		
		addressAdapter = new AddressAdapter(this, list, handler,R.layout.activity_address_manage_item);
		listView.setAdapter(addressAdapter);
	}
	
	Handler handler2 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0x00) {
				Log.v("PostGetJson", "" + result);

				if(null != result){
					try {
						JSONObject json= new JSONObject(result);  
						
						String m = json.getString("msg");
						
						Log.v("PostGetJson", "m : " + m);
						if("del".equals(m)){
							ToastUtil.showToast(AddressManageActivity.this, "删除成功");
							
							getData();

							return;
						}else if("setdefault".equals(m)){
							ToastUtil.showToast(AddressManageActivity.this, "设置成功");
						}
						
						JSONArray jsonArray=json.getJSONArray("list");  
						
						for(int i=0;i<jsonArray.length();i++){  
						    JSONObject o =(JSONObject) jsonArray.get(i);  
						  
						    
						    Receiveaddr g= new Receiveaddr();
						    g.setId((Integer)o.get("id"));
						    g.setUsername(o.getString("username"));
						    g.setPhone(o.getString("phone"));
						    g.setAddress(o.getString("address"));
						    g.setChoosed(false);
						    list.add(g);
						    
						}
						
						initview();
						
					} catch (JSONException e) {
						e.printStackTrace();
					}  
				}
				
				
			} else if (msg.what == 0x01) {
				Toast.makeText(getApplicationContext(), "获取失败",
						Toast.LENGTH_LONG).show();
			}
		}
	};
	
	private Runnable getJson=new Runnable()
	{

		public void run() {
			// TODO Auto-generated method stub
			try
			{
				result=GetJson(myurl, params);
				handler2.sendEmptyMessage(0x00);
			}
			catch(Exception e)
			{
				handler2.sendEmptyMessage(0x01);
			}
		}
	};
	
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
	
	List<NameValuePair> params;
	private String result;
	private String myurl;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			super.handleMessage(message);
			switch (message.what) {
			case MESSAGETYPE_ERROR:
				ToastUtil.showToast(AddressManageActivity.this, "未查询到数据");
				break;
			case 0x7777:
				System.out.println("UserManageActivity 查询用户成功 更新界面");
				addressAdapter.setList(list);
				addressAdapter.notifyDataSetChanged();
				break;
			case 0x8888:
				System.out.println("UserManageActivity 删除用户成功 更新界面");
				int size = list.size();
				for (int i = size; i > 0; i--) {
					if (list.get(i - 1).isChoosed()) {
						list.remove(i - 1);
					}
				}
				addressAdapter.notifyDataSetChanged();
				checkBox.setChecked(false);
				break;
			case 0x9999:
				ToastUtil.showToast(AddressManageActivity.this, "删除失败");
				break;
			}
		}
	};
	
	private void delUsers() {
		int size = list.size();
		int n = 0;
		for (int i = size; i > 0; i--) {
			if (list.get(i - 1).isChoosed()) {
				n++;
			}
		}
		if (n == 0) {
			ToastUtil.showToast(AddressManageActivity.this, "请选择后再进行删除");
			return;
		}
		comfirmDialog = new ComfirmDialog(this, R.style.mystyle);
		comfirmDialog.show();
		Listener listener = new Listener();
		comfirmDialog.init(listener);
	}
	
	// 事件点击监听器
	private final class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.all_check: 		// 全选
				selectedAll();
				break;
			case R.id.user_list_back: 	// 返回
				AddressManageActivity.this.finish();
				break;
			case R.id.addButton: 		// 增加
				startActivity(new Intent(AddressManageActivity.this, AddressAddActivity.class));// 启动增加Activity
				break;
			case R.id.editButton: 		// 修改
				editUser();
				break;
			case R.id.delButton: 		// 删除
				delUsers();
				break;
			default:
				break;
			}
		}
	}
	
	private void editUser() {
		int i = 0;
		Receiveaddr u = null;
		for (Receiveaddr user : list) {
			if (user.isChoosed()) {
				i++;
				u = user;
			}
		}

		if (i == 0 || i >= 2) {
			ToastUtil.showToast(AddressManageActivity.this, "请选择一个用户进行修改");
			return;
		}

		Intent intent = new Intent(AddressManageActivity.this, AddressEditActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("addr", u);
		intent.putExtra("key", bundle);
		startActivity(intent);// 启动另一个Activity
	}
	
	// 全选或全取消
	private void selectedAll() {
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setChoosed(this.checkBox.isChecked());
		}
		addressAdapter.notifyDataSetChanged();
	}
	
	private class Listener implements OnClickListener {
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.confirm_btn: // 全选
				comfirmDialog.dismiss();
				
				
					String addrIDs = "";
					int size = list.size();
					for (int i = size; i > 0; i--) {
						if (list.get(i - 1).isChoosed()) {
							addrIDs += list.get(i - 1).getId() + ",";
						}
					}
					
					params =new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("ids", addrIDs));
					
					myurl = Configer.SERVER_HOST+"/receiveaddrDel.action";
					
					new Thread(getJson).start();
					

				break;
			case R.id.cancel_btn:
				comfirmDialog.dismiss();
				break;
			}
		}
	}
	
	class AddressAdapter extends BaseAdapter {

		private Handler 				mHandler;
		private int 					resourceId;		//适配器视图资源ID
		private Context 				context;		//上下文对象
		public  List<Receiveaddr> 				list;			//数据集合List
		private LayoutInflater 			inflater;		//布局填充器
		
		public AddressAdapter(Context context,List<Receiveaddr> list
				,Handler mHandler,int resourceId){
			this.list 		= list;
			this.context 	= context;
			this.mHandler 	= mHandler;
			this.resourceId = resourceId;
			inflater 		= LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		public void setList(List<Receiveaddr> receiveaddrs){
			list = receiveaddrs;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Receiveaddr bean = list.get(position);
			ViewHolder holder = null;
			if(convertView == null){
				convertView = inflater.inflate(resourceId,null);
				
				holder = new ViewHolder();
				holder.username 	 	= (TextView) convertView.findViewById(R.id.username);
				holder.phone 	= (TextView) convertView.findViewById(R.id.phone);
				holder.address = (TextView) convertView.findViewById(R.id.address);
				holder.action = (Button) convertView.findViewById(R.id.action);
				holder.user_check 	= (CheckBox) convertView.findViewById(R.id.user_check);
				holder.user_check.setTag(position);
				holder.user_check.setOnCheckedChangeListener(new CheckBoxChangedListener());
				holder.action.setTag(position);
				holder.action.setOnClickListener(new ClickListener2());
				
				convertView.setTag(holder);

			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.username.setText(bean.getUsername());
			holder.phone.setText(bean.getPhone());
			holder.address.setText(bean.getAddress());
			
			holder.user_check.setChecked(bean.isChoosed());
			return convertView;
		}
		
		private final class ClickListener2 implements OnClickListener{

			@Override
			public void onClick(View arg0) {
				
				ToastUtil.showToast(AddressManageActivity.this, ""+list.get(Integer.parseInt(String.valueOf(arg0.getTag()))).getId());
				
				
				setDefaultAddr(list.get(Integer.parseInt(String.valueOf(arg0.getTag()))).getAddress());
				
			}
		}
		
		private void setDefaultAddr(String addr){
			params =new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("account", MyApplication.user.getAccount()));
			params.add(new BasicNameValuePair("address", addr));
			
			myurl = Configer.SERVER_HOST+"/receiveaddrSetDefault2.action";
			
			new Thread(getJson).start();
		}
		
		private final class ViewHolder{
			public TextView username;
			public TextView phone;
			public TextView address;
			public TextView flaglogin;
			public TextView action;
			public CheckBox user_check;		//选择按钮
		}


		//CheckBox选择改变监听器
		private final class CheckBoxChangedListener implements OnCheckedChangeListener{
			@Override
			public void onCheckedChanged(CompoundButton cb, boolean flag) {
				int position = (Integer)cb.getTag();
				Receiveaddr bean = list.get(position);
				bean.setChoosed(flag);
				//如果所有的物品全部被选中，则全选按钮也默认被选中
				mHandler.sendMessage(mHandler.obtainMessage(11, isAllSelected()));
			}
		}
		
		/**
		 * 判断是所有的用户是否全部被选中
		 * @return	true所有条目全部被选中
		 * 			false还有条目没有被选中
		 */
		private boolean isAllSelected(){
			boolean flag = true;
			for(int i=0;i<list.size();i++){
				if(!list.get(i).isChoosed()){
					flag = false;
					break;
				}
			}
			return flag;
		}

		// 将EditText的光标定位到字符的最后面
		public void setEditTextCursorLocation(EditText editText) {
			CharSequence text = editText.getText();
			if (text instanceof Spannable) {
				Spannable spanText = (Spannable) text;
				Selection.setSelection(spanText, text.length());
			}
		}
	}
}
