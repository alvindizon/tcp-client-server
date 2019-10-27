package com.alvindizon.tcpclientserver.application;

import android.app.Application;

import com.alvindizon.tcpclientserver.di.component.AppComponent;
import com.alvindizon.tcpclientserver.di.component.DaggerAppComponent;
import com.alvindizon.tcpclientserver.di.component.ViewModelComponent;
import com.alvindizon.tcpclientserver.di.module.AppModule;
import com.alvindizon.tcpclientserver.di.module.ViewModelModule;
import com.jakewharton.threetenabp.AndroidThreeTen;

public class CustomApplication extends Application {
    private static CustomApplication INSTANCE;

    AppComponent appComponent;
    ViewModelComponent viewModelComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);
        INSTANCE = this;
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        viewModelComponent = appComponent.viewModelComponent(new ViewModelModule());
    }

    public static CustomApplication get() {
        return INSTANCE;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public ViewModelComponent getViewModelComponent() {
        return viewModelComponent;
    }
}
