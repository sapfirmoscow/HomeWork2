package ru.sberbank.homework2;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class MyService extends IntentService {

    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_VALUE = 3;
    public static final int MSG_STOP_SERVICE = 4;
    private static boolean shoudFinish = false;
    private Random random = new Random();
    private ArrayList<Messenger> mClients = new ArrayList<>();

    private Messenger mMessenger = new Messenger(new IncomingHandler());

    public MyService() {
        super("MyFirstService");
    }

    public static final Intent newInteent(Context context) {
        return new Intent(context, MyService.class);
    }

    public static void setShoudFinish(boolean shoudFinish) {
        MyService.shoudFinish = shoudFinish;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        for (int i = 0; i <= 50; i++) {
            if (shoudFinish) {
                sendToClients(Message.obtain(null, MSG_STOP_SERVICE, null));
                return;
            }

            sendToClients(Message.obtain(null, MSG_VALUE, getRandomNumber(100)));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private int getRandomNumber(int maxValue) {
        return random.nextInt(maxValue);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private void sendToClients(Message message) {
        for (Messenger messenger : mClients) {
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
