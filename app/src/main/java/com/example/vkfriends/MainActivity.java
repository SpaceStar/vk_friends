package com.example.vkfriends;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainFragment.LogIn {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager manager = getSupportFragmentManager();

        Fragment container = manager.findFragmentById(R.id.fragmentContainer);
        if (container == null) {
            Fragment fragment;
            if (!VK.isLoggedIn()) {
                fragment = new MainFragment();
            } else {
                fragment = new ContentFragment();
            }
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragmentContainer, fragment);
            transaction.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        VKAuthCallback callback = new VKAuthCallback() {
            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                ContentFragment fragment = new ContentFragment();
                transaction.replace(R.id.fragmentContainer, fragment);
                transaction.commitAllowingStateLoss();
            }

            @Override
            public void onLoginFailed(int i) {
                Toast.makeText(MainActivity.this, R.string.allow_access, Toast.LENGTH_SHORT).show();
            }
        };
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void vkLogIn() {
        ArrayList<VKScope> vkScopes = new ArrayList<>();
        vkScopes.add(VKScope.OFFLINE);
        vkScopes.add(VKScope.FRIENDS);
        VK.login(this, vkScopes);
    }
}
