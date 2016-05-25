package com.tetilengineering.dashio.dashio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.hardkernel.odroid.serialport.SerialPort;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    protected SerialPort mSerialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    public TextView mDI0indicator;
    public TextView mDI1indicator;
    public TextView mDI2indicator;
    public TextView mDI3indicator;

    public Button mDO0button;
    public Button mDO1button;
    public Button mDO2button;
    public Button mDO3button;

    
    private class ReadThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[32];
                    if (mInputStream == null)
                        return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDI0indicator =(TextView)findViewById(R.id.DI0_indicator);
        mDI1indicator =(TextView)findViewById(R.id.DI1_indicator);
        mDI2indicator =(TextView)findViewById(R.id.DI2_indicator);
        mDI3indicator =(TextView)findViewById(R.id.DI3_indicator);

        mDO0button = (Button)findViewById(R.id.DO0_button);
        mDO1button = (Button)findViewById(R.id.DO1_button);
        mDO2button = (Button)findViewById(R.id.DO2_button);
        mDO3button = (Button)findViewById(R.id.DO3_button);


        try {
            mSerialPort = new SerialPort(new File("/dev/ttyACM0"), 115200, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            //DisplayError(R.string.error_unknown);
        }

        mDO0button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }
    
    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                int data = buffer[0];
                //Log.e("data:", Integer.toString(data));
                setDIindicators(mDI0indicator, data, 1);
                setDIindicators(mDI1indicator, data, 2);
                setDIindicators(mDI2indicator, data, 4);
                setDIindicators(mDI3indicator, data, 8);
            }
        });
    }

    public void setDIindicators(TextView textView, int data, int mask) {
        if ((data & mask) == mask) {
            textView.setBackgroundResource(R.drawable.digital_indicator_on);
        } else {
            textView.setBackgroundResource(R.drawable.digital_indicator);
        }
    }
    
    @Override
    protected void onDestroy() {
        if (mReadThread != null)
            mReadThread.interrupt();
        mSerialPort.close();
        mSerialPort = null;
        super.onDestroy();
    }

}
