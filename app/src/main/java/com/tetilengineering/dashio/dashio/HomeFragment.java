package com.tetilengineering.dashio.dashio;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.hardkernel.odroid.serialport.SerialPort;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class HomeFragment extends Fragment {

    public View v = null;
    
    protected SerialPort mSerialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;

    public TextView mDI0indicator;
    public TextView mDI1indicator;
    public TextView mDI2indicator;
    public TextView mDI3indicator;
    public TextView mDI4indicator;
    public TextView mDI5indicator;

    public ToggleButton mDO0button;
    public ToggleButton mDO1button;
    public ToggleButton mDO2button;
    public ToggleButton mDO3button;
    public ToggleButton mDO4button;
    public ToggleButton mDO5button;

    public ImageButton mGoogleMapsButton;

    int mDigitalOutputs = 0;

    
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        fullScreen();
    }

    @Override
    public void onResume() {
        super.onResume();
        fullScreen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        mDI0indicator =(TextView)v.findViewById(R.id.DI0_indicator);
        mDI1indicator =(TextView)v.findViewById(R.id.DI1_indicator);
        mDI2indicator =(TextView)v.findViewById(R.id.DI2_indicator);
        mDI3indicator =(TextView)v.findViewById(R.id.DI3_indicator);
        mDI4indicator =(TextView)v.findViewById(R.id.DI4_indicator);
        mDI5indicator =(TextView)v.findViewById(R.id.DI5_indicator);

        mDO0button = (ToggleButton)v.findViewById(R.id.DO0_button);
        mDO1button = (ToggleButton)v.findViewById(R.id.DO1_button);
        mDO2button = (ToggleButton)v.findViewById(R.id.DO2_button);
        mDO3button = (ToggleButton)v.findViewById(R.id.DO3_button);
        mDO4button = (ToggleButton)v.findViewById(R.id.DO4_button);
        mDO5button = (ToggleButton)v.findViewById(R.id.DO5_button);

        mGoogleMapsButton = (ImageButton)v.findViewById(R.id.google_maps_btn);

        try {
            mSerialPort = new SerialPort(new File("/dev/ttyACM1"), 115200, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            //DisplayError(R.string.error_unknown);
        }

        mDO0button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeDigitalOutputs(isChecked, 0);
                setButtonIndicator(mDO0button, isChecked);
            }
        });

        mDO1button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeDigitalOutputs(isChecked, 1);
                setButtonIndicator(mDO1button, isChecked);
            }
        });

        mDO2button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeDigitalOutputs(isChecked, 2);
                setButtonIndicator(mDO2button, isChecked);
            }
        });

        mDO3button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeDigitalOutputs(isChecked, 3);
                setButtonIndicator(mDO3button, isChecked);
            }
        });

        mDO4button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeDigitalOutputs(isChecked, 4);
                setButtonIndicator(mDO4button, isChecked);
            }
        });

        mDO5button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                writeDigitalOutputs(isChecked, 5);
                setButtonIndicator(mDO5button, isChecked);
            }
        });

        mGoogleMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps"));
                startActivity(intent);
            }
        });

        return v;

    }

    public void setButtonIndicator(ToggleButton toggleButton, boolean state) {
        Drawable img = getActivity().getDrawable(android.R.drawable.button_onoff_indicator_off);
        if (state) {
            img = getActivity().getDrawable(android.R.drawable.button_onoff_indicator_on);
        }
        toggleButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, img);
    }

    public void writeDigitalOutputs(boolean state, int position) {
        int mask = (int)Math.pow(2, position);
        if (state) {
            mDigitalOutputs = mDigitalOutputs | mask;
        } else {
            mDigitalOutputs = mDigitalOutputs & ~mask;
        }

        try {
            mOutputStream.write(mDigitalOutputs);
        } catch (Exception e) {}
    }
    
    protected void onDataReceived(final byte[] buffer, final int size) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                int data = buffer[0];
                //Log.e("data:", Integer.toString(data));
                setDIindicator(mDI0indicator, data, 0);
                setDIindicator(mDI1indicator, data, 1);
                setDIindicator(mDI2indicator, data, 2);
                setDIindicator(mDI3indicator, data, 3);
                setDIindicator(mDI3indicator, data, 4);
                setDIindicator(mDI3indicator, data, 5);
            }
        });
    }

    public void setDIindicator(TextView textView, int data, int position) {
        int mask = (int)Math.pow(2, position);
        if ((data & mask) == mask) {
            textView.setBackgroundResource(R.drawable.digital_indicator_on);
        } else {
            textView.setBackgroundResource(R.drawable.digital_indicator);
        }
    }

    private void fullScreen() {
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
    
    @Override
    public void onDestroy() {
        if (mReadThread != null)
            mReadThread.interrupt();
        mSerialPort.close();
        mSerialPort = null;
        super.onDestroy();
    }



}
