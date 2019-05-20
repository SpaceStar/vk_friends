package com.example.vkfriends;

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

    private boolean userDownloaded;
    private boolean friendsDownloaded;

    private Toast errorToast;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_content, container, false);
        View fragment = contentBinding.getRoot();

        ToolbarBinding.bind(getActivity().findViewById(R.id.toolbar)).setDownloading(downloading);
        contentBinding.setDownloading(downloading);

        if (savedInstanceState != null) {
            VKUser user = savedInstanceState.getParcelable(KEY_USER);
            List<VKUser> friends = savedInstanceState.getParcelableArrayList(KEY_FRIENDS);
            if (user == null) {
                getUser();
            } else {
                contentBinding.setUser(user);
                userDownloaded = true;
                updateDownloadStatus();
            }
            if (friends == null) {
                getFriends();
            } else {
                friendsAdapter.setUsers(friends);
                friendsDownloaded = true;
                updateDownloadStatus();
            }
        } else {
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

    private void updateDownloadStatus() {
        if (userDownloaded && friendsDownloaded) {
            downloading.set(false);
        } else {
            downloading.set(true);
        }
    }

    private void getFriends() {
        friendsDownloaded = false;
        updateDownloadStatus();
        VK.execute(new VKFriendsRequest(), new VKApiCallback<List<VKUser>>() {
            @Override
            public void success(List<VKUser> vkUsers) {
                friendsAdapter.setUsers(vkUsers);
                friendsDownloaded = true;
                updateDownloadStatus();
            }

            @Override
            public void fail(@NotNull VKApiExecutionException e) {
                getErrorToast().show();
            }
        });
    }

    private void getUser() {
        userDownloaded = false;
        updateDownloadStatus();
        VK.execute(new VKUserRequest(), new VKApiCallback<VKUser>() {
            @Override
            public void success(VKUser vkUser) {
                contentBinding.setUser(vkUser);
                userDownloaded = true;
                updateDownloadStatus();
            }

            @Override
            public void fail(@NotNull VKApiExecutionException e) {
                getErrorToast().show();
            }
        });
    }
}
