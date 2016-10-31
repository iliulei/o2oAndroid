package com.ligao.ui;

import com.ligao.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Button;


public class ComfirmDialog extends AlertDialog {

	Button confirm_btn;
	Button cancel_btn;

	public ComfirmDialog(Context context, int theme) {
		super(context, theme);
	}

	public ComfirmDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.comfirmdialog);
		confirm_btn = (Button) findViewById(R.id.confirm_btn);
		cancel_btn = (Button) findViewById(R.id.cancel_btn);
		this.setCancelable(false);

		this.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface arg0, int arg1,
					KeyEvent arg2) {
				if (arg1 == KeyEvent.KEYCODE_BACK) {
					if (null != ComfirmDialog.this
							&& ComfirmDialog.this.isShowing()) {
						Message msg = Message.obtain();
						msg.what = 0x0000;
						handler.sendMessage(msg);
					}
				}
				return false;
			}
		});
//		init();
	}

	public void init(android.view.View.OnClickListener listener) {
//		Listener listener = new Listener();
		confirm_btn.setOnClickListener(listener);
		cancel_btn.setOnClickListener(listener);
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case 0x0000:
				ComfirmDialog.this.dismiss();
				break;
			}
		}
	};

}