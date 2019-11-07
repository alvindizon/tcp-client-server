package com.alvindizon.tcpclientserver.features.server;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alvindizon.tcpclientserver.core.Actions;
import com.alvindizon.tcpclientserver.core.ViewModelFactory;
import com.alvindizon.tcpclientserver.databinding.FragmentServerBinding;
import com.alvindizon.tcpclientserver.di.Injector;
import com.alvindizon.tcpclientserver.features.clientserver.MainActivity;
import com.alvindizon.tcpclientserver.features.clientserver.MainViewModel;
import com.alvindizon.tcpclientserver.features.clientserver.TcpService;

import javax.inject.Inject;

public class ServerFragment extends Fragment {
    private static final String TAG = ServerFragment.class.getSimpleName();

    @Inject
    ViewModelFactory viewModelFactory;

    private FragmentServerBinding binding;
    private MainViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Injector.getViewModelComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentServerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.observeReceivedTcpMsg().observe(getViewLifecycleOwner(), message -> binding.receivedData.append(message));
        binding.button.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(binding.outgoingData.getText().toString())) {
                viewModel.sendTcpMessage(binding.outgoingData.getText().toString());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onStart");
        if(!((MainActivity) requireActivity()).isMyServiceRunning(TcpService.class)) {
            ((MainActivity) requireActivity()).doTcpServiceAction(Actions.START);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if(((MainActivity) requireActivity()).isMyServiceRunning(TcpService.class)) {
            ((MainActivity) requireActivity()).doTcpServiceAction(Actions.STOP);
        }
    }

}
