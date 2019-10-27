package com.alvindizon.tcpclientserver.di;


import com.alvindizon.tcpclientserver.application.CustomApplication;
import com.alvindizon.tcpclientserver.di.component.AppComponent;
import com.alvindizon.tcpclientserver.di.component.ViewModelComponent;

public class Injector {
    public static AppComponent get() {
        return CustomApplication.get().getAppComponent();
    }

    public static ViewModelComponent getViewModelComponent() {
        return CustomApplication.get().getViewModelComponent();
    }
}
