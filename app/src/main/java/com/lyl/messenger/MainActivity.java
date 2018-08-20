package com.lyl.messenger;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity lyl123";
    public static final int CLIENT_MSG = 1;
    private Messenger mMessenger = new Messenger(new MessengerHandler());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindMyService();
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected");

            /*
            Create a Messenger from a raw IBinder,
            which had previously been retrieved with {@link #getBinder}.
            service是服务端onBind方法返回的值，是Messenger的getBinder()方法返回的值
            使用服务端传过来的参数service创建Messenger，
            */
            Messenger messenger = new Messenger(service);
            Message msg = Message.obtain(null, CLIENT_MSG);
            msg.replyTo = mMessenger;
            Log.e(TAG, "msg.replyTo = " + msg.replyTo);
            Bundle bundle = new Bundle();
            bundle.putString("CLIENT", "hi, server");
            msg.setData(bundle);
            try {
                Log.e(TAG, "will send [" + msg + "] to server");
                messenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private void bindMyService() {
        Log.e(TAG, "bindMyService");
        Intent service = new Intent(this, MyService.class);
        bindService(service, conn, BIND_AUTO_CREATE);
    }

    class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MyService.SERVER_MSG:
                    Bundle data = msg.getData();
                    String str = data.getString("SERVER");
                    Log.e(TAG, str);
                    break;
                default:
                    break;
            }
        }
    }
}
