package com.example.vkfriends.vk.requests;

import com.example.vkfriends.vk.models.VKUser;
import com.vk.api.sdk.requests.VKRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VKFriendsRequest extends VKRequest<List<VKUser>> {

    {
        addParam("count", 5);
        addParam("fields", "photo_max");
    }

    public VKFriendsRequest() {
        super("friends.get");
    }

    @Override
    public List<VKUser> parse(@NotNull JSONObject r) throws Exception {
        JSONArray users = r.getJSONObject("response").getJSONArray("items");
        List<VKUser> result = new ArrayList<>();
        for (int i = 0; i < users.length(); i++) {
            result.add(VKUser.parse(users.getJSONObject(i)));
        }
        return result;
    }
}
