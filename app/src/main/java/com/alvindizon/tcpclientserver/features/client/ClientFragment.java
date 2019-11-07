package com.alvindizon.tcpclientserver.features.client;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.alvindizon.tcpclientserver.core.ViewModelFactory;
import com.alvindizon.tcpclientserver.databinding.FragmentClientBinding;
import com.alvindizon.tcpclientserver.di.Injector;
import com.alvindizon.tcpclientserver.features.clientserver.MainViewModel;

import javax.inject.Inject;

public class ClientFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private FragmentClientBinding binding;
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
        binding = FragmentClientBinding.inflate(inflater, container, false);
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
        binding.button2.setOnClickListener(v -> {
            if(!TextUtils.isEmpty(binding.editIp.getText().toString()) &&
                    !TextUtils.isEmpty(binding.editPort.getText().toString())) {
                String ipAddress = binding.editIp.getText().toString();
                int port = Integer.parseInt(binding.editPort.getText().toString());
                viewModel.initClient(ipAddress, port).observe(getViewLifecycleOwner(),
                        status -> binding.statusLabel.setText(status));
                viewModel.startClientListen();
            }
        });
    }
}
