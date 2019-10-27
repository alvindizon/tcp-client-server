package com.alvindizon.tcpclientserver.di.component;


import com.alvindizon.tcpclientserver.di.module.ViewModelModule;
import com.alvindizon.tcpclientserver.features.clientserver.MainActivity;

import dagger.Subcomponent;

@Subcomponent(modules = ViewModelModule.class)
public interface ViewModelComponent {
    void inject(MainActivity activity);
}
