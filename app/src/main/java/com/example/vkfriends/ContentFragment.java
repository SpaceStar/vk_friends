package com.example.vkfriends;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkfriends.databinding.FragmentContentBinding;
import com.example.vkfriends.databinding.ToolbarBinding;
import com.example.vkfriends.vk.models.VKUser;
import com.example.vkfriends.vk.requests.VKFriendsRequest;
import com.example.vkfriends.vk.requests.VKUserRequest;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.exceptions.VKApiExecutionException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ContentFragment extends Fragment {
    private static final String KEY_USER = "USER";
    private static final String KEY_FRIENDS = "FRIENDS";

    private FragmentContentBinding contentBinding;
    private FriendsAdapter friendsAdapter;
    private ObservableBoolean downloading = new ObservableBoolean();

    private Toast errorToast;
    private ConnectivityManager.NetworkCallback networkCallback;

    private Toast getErrorToast() {
        if (errorToast == null) {
            errorToast = Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT);
        }
        return errorToast;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        friendsAdapter = new FriendsAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    onInternetAvailable();
                }

                @Override
                public void onLost(Network network) {
                    onInternetLost();
                }
            };
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_content, container, false);
        View fragment = contentBinding.getRoot();

        ToolbarBinding.bind(getActivity().findViewById(R.id.toolbar)).setDownloading(downloading);
        contentBinding.setDownloading(downloading);

        boolean connected = true;
        if (!checkInternetConnection()) {
            connected = false;
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            downloading.set(false);
        }

        if (savedInstanceState != null) {
            VKUser user = savedInstanceState.getParcelable(KEY_USER);
            List<VKUser> friends = savedInstanceState.getParcelableArrayList(KEY_FRIENDS);
            if (user == null) {
                if (connected)
                    getUser();
            } else {
                contentBinding.setUser(user);
            }
            if (friends == null) {
                if (connected)
                    getFriends();
            } else {
                friendsAdapter.setUsers(friends);
            }
            if ((user != null) && (friends != null)) {
                networkCallback = null;
                updateDownloadStatus();
            }
        } else if (connected) {
            getUser();
            getFriends();
        }

        RecyclerView recyclerView = fragment.findViewById(R.id.contentList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(friendsAdapter);

        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        VKUser user = contentBinding.getUser();
        List<VKUser> friends = friendsAdapter.getUsers();
        if (user != null)
            outState.putParcelable(KEY_USER, user);
        if (friendsAdapter.isInitialized())
            outState.putParcelableArrayList(KEY_FRIENDS, new ArrayList<Parcelable>(friends));
    }

    @Override
    public void onStart() {
        super.onStart();

        if (networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            conMgr.registerNetworkCallback(request, networkCallback);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            conMgr.unregisterNetworkCallback(networkCallback);
        }
    }

    private void onInternetAvailable() {
        if (!isUserDownloaded())
            getUser();
        if (!isFriendsDownloaded())
            getFriends();
    }

    private void onInternetLost() {
        downloading.set(false);
        Toast.makeText(getActivity(), R.string.internet_connection_lost, Toast.LENGTH_SHORT).show();
    }

    private void updateDownloadStatus() {
        if (isUserDownloaded() && isFriendsDownloaded()) {
            downloading.set(false);
            if (networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                conMgr.unregisterNetworkCallback(networkCallback);
                networkCallback = null;
            }
        } else {
            downloading.set(true);
        }
    }

    private boolean isUserDownloaded() {
        VKUser user = contentBinding.getUser();
        if (user != null)
            return true;
        return false;
    }

    private boolean isFriendsDownloaded() {
        if (friendsAdapter.isInitialized())
            return true;
        return false;
    }

    private void getFriends() {
        updateDownloadStatus();
        VK.execute(new VKFriendsRequest(), new VKApiCallback<List<VKUser>>() {
            @Override
            public void success(List<VKUser> vkUsers) {
                friendsAdapter.setUsers(vkUsers);
                updateDownloadStatus();
            }

            @Override
            public void fail(@NotNull VKApiExecutionException e) {
                getErrorToast().show();
            }
        });
    }

    private void getUser() {
        updateDownloadStatus();
        VK.execute(new VKUserRequest(), new VKApiCallback<VKUser>() {
            @Override
            public void success(VKUser vkUser) {
                contentBinding.setUser(vkUser);
                updateDownloadStatus();
            }

            @Override
            public void fail(@NotNull VKApiExecutionException e) {
                getErrorToast().show();
            }
        });
    }

    private boolean checkInternetConnection() {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected())
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
