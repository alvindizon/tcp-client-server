package com.alvindizon.tcpclientserver.di.module;

import androidx.lifecycle.ViewModel;

import com.alvindizon.tcpclientserver.core.ViewModelFactory;
import com.alvindizon.tcpclientserver.data.TcpRepository;
import com.alvindizon.tcpclientserver.features.clientserver.MainViewModel;
import com.alvindizon.tcpclientserver.data.TcpHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import javax.inject.Provider;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

/*
    NB: ViewModels with default constructors (zero argument constructors) do not need ViewModelFactories
 */
@Module
public class ViewModelModule {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @MapKey
    @interface ViewModelKey {
        Class<? extends ViewModel> value();
    }

    @Provides
    ViewModelFactory provideViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> providerMap) {
        return new ViewModelFactory(providerMap);
    }

    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    ViewModel provideMainViewModel(TcpRepository tcpRepository) {
        return new MainViewModel(tcpRepository);
    }


}