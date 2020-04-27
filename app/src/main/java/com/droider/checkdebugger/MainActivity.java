package com.droider.checkdebugger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    boolean portused = false;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private boolean isDebuggerConnected() {
        if ((getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return true;
        }
        return android.os.Debug.isDebuggerConnected();
    }

    private void checkPort(int port) {
        final int port_ = port;
        new Thread() {
            @Override
            public void run() {
                try {
                    InetAddress addr = InetAddress.getByName("127.0.0.1");
                    Socket socket = new Socket(addr, port_);
                    portused = true;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    portused = false;
                }

                try {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (portused) {
                                tv.setText("Yes!");
                            }
                            else {
                                tv.setText("No!");
                            }
                        }
                    });
                    Thread.sleep(300);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView)findViewById(R.id.textview);

        Button checkdbg_btn = (Button)findViewById(R.id.checkdbg);
        checkdbg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText(isDebuggerConnected() ? "Yes!": "No!");
            }
        });

        Button checkport_btn = (Button)findViewById(R.id.checkport);
        checkport_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPort(23946);
            }
        });

        Button checkstatus_btn = (Button)findViewById(R.id.checkstatus);
        checkstatus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText(checkStatus() ? "Yes!": "No!");
            }
        });

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     * @return
     */
    public native boolean checkStatus();
}
