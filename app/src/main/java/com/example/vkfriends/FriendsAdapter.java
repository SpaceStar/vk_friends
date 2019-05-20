package com.example.vkfriends;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vkfriends.databinding.ListItemBinding;
import com.example.vkfriends.vk.models.VKUser;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.UserViewHolder> {
    List<VKUser> users = new ArrayList<>();
    private boolean initialized = false;

    public void setUsers(List<VKUser> users) {
        this.users = users;
        initialized = true;
        notifyDataSetChanged();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public List<VKUser> getUsers() {
        return users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemBinding itemBinding = DataBindingUtil.inflate(inflater, R.layout.list_item, parent, false);
        return new UserViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private ListItemBinding binding;

        public UserViewHolder(@NonNull ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(VKUser user) {
            binding.setUser(user);
        }
    }
}
