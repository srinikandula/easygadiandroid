package com.easygaadi.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.easygaadi.interfaces.GroupActionsInterface;
import com.easygaadi.models.GroupItem;
import com.easygaadi.trucksmobileapp.R;

import java.util.List;

/**
 * Created by ibraincpu6 on 05-12-2016.
 */

public class GroupsAdapter extends BaseRecyclerViewAdapter<GroupItem> {

    GroupActionsInterface groupActionsInterface;

    public GroupsAdapter(Context context, List<GroupItem> mList) {
        super(context, mList);
    }

    public void setGroupActionsInterface(GroupActionsInterface groupActionsInterface) {
        this.groupActionsInterface = groupActionsInterface;
    }

    @Override
    public void bindItem(final GroupItem groupItem, ViewHolder viewHolder, Context context, int position) {
        TextView textView = (TextView) viewHolder.getView(R.id.group_name);
        textView.setText(groupItem.groupname);
        (viewHolder.getView(R.id.group_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupActionsInterface != null) {
                    groupActionsInterface.assignDevices(groupItem);
                }
            }
        });

        (viewHolder.getView(R.id.group_list)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupActionsInterface != null) {
                    groupActionsInterface.onListItemClick(groupItem);
                }
            }
        });
        (viewHolder.getView(R.id.group_maps)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupActionsInterface != null) {
                    groupActionsInterface.onMapItemClick(groupItem);
                }
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupActionsInterface != null) {
                    groupActionsInterface.onGroupItemClick(groupItem);
                }
            }
        });
    }

    @Override
    public int getResource(int var1) {
        return R.layout.item_group;
    }
}
