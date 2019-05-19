package com.example.vkfriends.vk.requests;

import com.example.vkfriends.vk.models.VKUser;
import com.vk.api.sdk.requests.VKRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class VKUserRequest extends VKRequest<VKUser> {

    {
        addParam("fields", "photo_max");
    }

    public VKUserRequest() {
        super("users.get");
    }

    @Override
    public VKUser parse(@NotNull JSONObject r) throws Exception {
        JSONObject user = r.getJSONArray("response").getJSONObject(0);
        return VKUser.parse(user);
    }
}
