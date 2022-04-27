package com.example.provapacketsniffer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
/
public class MainActivity extends AppCompatActivity {
    public Button btt = null;
    public TextView tv = null;
    public NetworkInterface[] list = null;
    public String str= null, info = null;
    public int x, choice;
    public JpcapCaptor captor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btt = findViewById(R.id.bttOk);
        tv = findViewById(R.id.tv);

        btt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            list = JpcapCaptor.getDeviceList();
                for(x=0; x<list.length; x++)
                {
                    Log.i("string", x+" -> "+list[x].description);

                }
            }
        });

    }
}