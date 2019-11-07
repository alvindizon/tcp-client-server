package com.alvindizon.tcpclientserver.di.component;


import com.alvindizon.tcpclientserver.di.module.ViewModelModule;
import com.alvindizon.tcpclientserver.features.client.ClientFragment;
import com.alvindizon.tcpclientserver.features.clientserver.MainActivity;
import com.alvindizon.tcpclientserver.features.server.ServerFragment;

import dagger.Subcomponent;

@Subcomponent(modules = ViewModelModule.class)
public interface ViewModelComponent {
    void inject(MainActivity activity);
    void inject(ServerFragment fragment);
    void inject(ClientFragment fragment);
}
