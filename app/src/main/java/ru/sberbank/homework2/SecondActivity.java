package ru.sberbank.homework2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    private Button stopButton;
    private TextView outputText;

    private Messenger mService;
    private Messenger mMessenger = new Messenger(new IncomingHandler());
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            Message message = Message.obtain(null, MyService.MSG_REGISTER_CLIENT);
            message.replyTo = mMessenger;

            try {
                mService.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    public static final Intent newIntent(Context context) {
        return new Intent(context, SecondActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initViews();
        initListeners();
    }

    private void setFromService(Object obj) {
        outputText.setText(obj.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();

    }

    @Override
    protected void onPause() {
        super.onPause();
        doUnBindService();


    }


    private void initListeners() {
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyService.setShoudFinish(true);
            }
        });
    }

    private void doBindService() {
        bindService(MyService.newInteent(SecondActivity.this), mServiceConnection, BIND_AUTO_CREATE);

    }

    private void doUnBindService() {
        Message message = Message.obtain(null, MyService.MSG_UNREGISTER_CLIENT);
        message.replyTo = mMessenger;

        try {
            mService.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        unbindService(mServiceConnection);
    }

    private void initViews() {
        stopButton = findViewById(R.id.stop_button);
        outputText = findViewById(R.id.output_text);
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyService.MSG_VALUE:
                    setFromService(msg.obj);
                    break;
                case MyService.MSG_STOP_SERVICE:
                    outputText.setText("Service finished");
            }
        }
    }
}
