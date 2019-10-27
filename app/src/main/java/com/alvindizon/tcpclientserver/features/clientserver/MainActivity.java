package com.alvindizon.tcpclientserver.features.clientserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.alvindizon.tcpclientserver.R;
import com.alvindizon.tcpclientserver.core.Actions;
import com.alvindizon.tcpclientserver.core.ViewModelFactory;
import com.alvindizon.tcpclientserver.databinding.ActivityMainBinding;
import com.alvindizon.tcpclientserver.di.Injector;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Injector.getViewModelComponent().inject(this);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(MainViewModel.class);
        viewModel.observeReceivedTcpMsg().observe(this, message -> binding.receivedData.setText(message));
        binding.button.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(binding.outgoingData.getText().toString())) {
                viewModel.sendTcpMessage(binding.outgoingData.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!isMyServiceRunning(TcpService.class)) {
            doTcpServiceAction(Actions.START);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isMyServiceRunning(TcpService.class)) {
            doTcpServiceAction(Actions.STOP);
        }
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    public void doTcpServiceAction(Actions action) {
        Intent intent = new Intent(getApplicationContext(), TcpService.class);
        intent.setAction(action.name());
        startService(intent);
    }

}
