package com.tetilengineering.dashio.dashio;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.arksine.easycam.EasycamFragment;

/**
 * Created by gtetil on 6/15/2016.
 */
public class MainActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment() {
        return new HomeFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

}
