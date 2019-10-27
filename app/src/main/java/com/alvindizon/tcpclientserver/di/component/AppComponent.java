package com.alvindizon.tcpclientserver.di.component;


import com.alvindizon.tcpclientserver.di.module.AppModule;
import com.alvindizon.tcpclientserver.di.module.ViewModelModule;
import com.alvindizon.tcpclientserver.features.clientserver.TcpService;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AppModule.class})
@Singleton
public interface AppComponent  {
    void inject(TcpService service);
    ViewModelComponent viewModelComponent(ViewModelModule viewModelModule);
}