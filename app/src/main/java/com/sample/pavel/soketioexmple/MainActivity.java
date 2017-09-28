package com.sample.pavel.soketioexmple;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sample.pavel.soketioexmple.socket.Client;

public class MainActivity extends AppCompatActivity implements Client.OnMessageCallback {

    private EditText ipAddress;
    private EditText port;
    private Client mClient;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipAddress = findViewById(R.id.addressEditText);
        port = findViewById(R.id.portEditText);

        mClient = new Client(this);

        this.<Button>findViewById(R.id.connectButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                   mClient.connect(Integer.valueOf(port.getText().toString()),ipAddress.getText().toString());
                    }
                });

        this.<Button>findViewById(R.id.clearButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity.this.<TextView>findViewById(R.id.responseTextView).setText("");
                    }
                });

        this.<Button>findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClient.sendMessage(MainActivity.this,MainActivity.this.<EditText>findViewById(R.id.messageEdt).getText().toString());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient != null) {
            mClient.disconnect();
            mClient = null;
        }
    }

    @Override
    public void onStartConnection() {
        if (mProgressDialog==null){
            mProgressDialog = new ProgressDialog(this);
        }

        mProgressDialog.show();
    }

    @Override
    public void onConnected() {
      if (mProgressDialog!=null&&mProgressDialog.isShowing()){
          mProgressDialog.dismiss();
      }
    }

    @Override
    public void onStopConncetion() {
        if (mProgressDialog!=null&&mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onMessage(String message) {
        this.<TextView>findViewById(R.id.responseTextView).setText(message);
    }
}
