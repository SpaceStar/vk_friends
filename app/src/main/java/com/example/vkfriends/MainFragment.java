package com.example.vkfriends;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            LogIn logIn = (LogIn) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement LogIn");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_main, container, false);

        Button authButton = fragment.findViewById(R.id.mainAuthButton);
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity != null) {
                    ((LogIn)activity).vkLogIn();
                }
            }
        });

        return fragment;
    }

    public interface LogIn {
        void vkLogIn();
    }
}
