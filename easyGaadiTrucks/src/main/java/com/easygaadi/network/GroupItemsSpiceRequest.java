package com.easygaadi.network;

/**
 * Created by ibraincpu6 on 27-06-2015.
 */

import com.easygaadi.interfaces.ServerCommands;
import com.easygaadi.models.GroupItemResponse;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;


public class GroupItemsSpiceRequest extends RetrofitSpiceRequest<GroupItemResponse, ServerCommands> {
    private String accountId;

    public GroupItemsSpiceRequest(String accountId) {
        super(GroupItemResponse.class, ServerCommands.class);
        this.accountId = accountId;
    }

    @Override
    public GroupItemResponse loadDataFromNetwork() {
        return getService().getListOfGroups(accountId);
    }
}
