package com.alvindizon.tcpclientserver.features.clientserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
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
import com.alvindizon.tcpclientserver.features.client.ClientFragment;
import com.alvindizon.tcpclientserver.features.server.ServerFragment;

import javax.inject.Inject;

import static com.alvindizon.tcpclientserver.core.Const.LAST_TAB_POS;
import static com.alvindizon.tcpclientserver.core.Const.PAGE_COUNT;
import static com.alvindizon.tcpclientserver.core.Const.TAB_TITLE_1;
import static com.alvindizon.tcpclientserver.core.Const.TAB_TITLE_2;

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

        binding.viewpager.setAdapter(new CustomFragmentPagerAdapter(getSupportFragmentManager()));
        binding.slidingTabs.setupWithViewPager(binding.viewpager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_TAB_POS, binding.slidingTabs.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        binding.viewpager.setCurrentItem(savedInstanceState.getInt(LAST_TAB_POS));
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

    class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] {TAB_TITLE_1, TAB_TITLE_2};

        public CustomFragmentPagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return new ServerFragment();
            } else {
                return new ClientFragment();
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
