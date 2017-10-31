package com.easygaadi.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easygaadi.adapter.GroupsAdapter;
import com.easygaadi.interfaces.GroupActionsInterface;
import com.easygaadi.models.GroupItem;
import com.easygaadi.models.GroupItemResponse;
import com.easygaadi.trucksmobileapp.CreateGroupActivity;
import com.easygaadi.trucksmobileapp.ListOfTrackingTrucks;
import com.easygaadi.trucksmobileapp.LocateTrucksActivity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ibraincpu6 on 05-12-2016.
 */

public class GroupsFragment extends Fragment implements GroupActionsInterface {
    View root;
    RecyclerView recyclerView;
    Context context;
    GroupsAdapter groupsAdapter;
    ArrayList<GroupItem> groupItems = new ArrayList<>();
    GroupItemResponse groupItemResponse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupItemResponse = (GroupItemResponse) getActivity().getIntent().getSerializableExtra(Constants.GROUP_RESPONSE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_groups, container, false);
        initializeViews();
        return root;
    }

    private void initializeViews() {
        context = getActivity();
        recyclerView = (RecyclerView) root.findViewById(R.id.group_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        groupItems.addAll(Arrays.asList(groupItemResponse.data));
        groupsAdapter = new GroupsAdapter(context, groupItems);
        groupsAdapter.setGroupActionsInterface(this);
        recyclerView.setAdapter(groupsAdapter);
    }

    @Override
    public void onListItemClick(GroupItem groupItem) {
        Intent intent = new Intent(context, ListOfTrackingTrucks.class);
        intent.putExtra(Constants.GROUP_ID, groupItem.id);
        intent.putExtra(Constants.GROUP_NAME, groupItem.groupname);
        startActivity(intent);
    }

    @Override
    public void onMapItemClick(GroupItem groupItem) {
        Intent intent = new Intent(context, LocateTrucksActivity.class);
        intent.putExtra(Constants.GROUP_ID, groupItem.id);
        intent.putExtra(Constants.GROUP_NAME, groupItem.groupname);
        startActivity(intent);
    }

    @Override
    public void onGroupItemClick(GroupItem groupItem) {
        Intent intent = new Intent(context, ListOfTrackingTrucks.class);
        intent.putExtra(Constants.GROUP_ID, groupItem.id);
        intent.putExtra(Constants.GROUP_NAME, groupItem.groupname);
        startActivity(intent);
    }

    @Override
    public void assignDevices(GroupItem groupItem) {
        Intent intent = new Intent(context, CreateGroupActivity.class);
        intent.putExtra(Constants.GROUP_ID, groupItem.id);
        intent.putExtra(Constants.GROUP_NAME, groupItem.groupname);
        intent.putExtra(Constants.CONTACT_NAME, groupItem.contactName);
        intent.putExtra(Constants.CONTACT_PHONE, groupItem.contactPhone);
        intent.putExtra(Constants.PASSWORD, groupItem.password);
        startActivity(intent);
    }
}
