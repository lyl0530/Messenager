package com.lyl.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by dengjifu on 18-8-20.
 */

public class MyService extends Service {
    private static final String TAG = "MyService lyl123";

    public static final int SERVER_MSG = 2;
    /*
    新建一个Messenger指向这个Handler，通过Messenger所发送的所有消息都会由
    创建一个指向给定Handler的新Messenger。
    通过此Messenger发送的任何Message对象都将出现在Handler中，
    就像直接调用{@link Handler #sendMessage（Message）Handler.sendMessage（Message）}一样。

     * Create a new Messenger pointing to the given Handler.  Any Message
     * objects sent through this Messenger will appear in the Handler as if
     * {@link Handler#sendMessage(Message) Handler.sendMessage(Message)} had
     * been called directly.
     *
     * @param target The Handler that will receive sent messages.

    public Messenger(Handler target) {
        mTarget = target.getIMessenger();
    */

    private Messenger messenger = new Messenger(new MessengerHandler());

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        return messenger.getBinder();
    }

    class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MainActivity.CLIENT_MSG:
                    Bundle data = msg.getData();
                    String str = data.getString("CLIENT");
                    Log.e(TAG, str + ", and will reply to client!");

                    Messenger fromClient = msg.replyTo;
                    Log.e(TAG, "fromClient = " + fromClient);
                    Message serverMsg = new Message();
                    serverMsg.what = SERVER_MSG;
                    Bundle bundle = new Bundle();
                    bundle.putString("SERVER", "fine, client");
                    serverMsg.setData(bundle);
                    try {
                        fromClient.send(serverMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
