package com.easygaadi.interfaces;

import com.easygaadi.models.GroupItem;

/**
 * Created by ibraincpu6 on 05-12-2016.
 */

public interface GroupActionsInterface {
    void onListItemClick(GroupItem groupItem);

    void onMapItemClick(GroupItem groupItem);

    void onGroupItemClick(GroupItem groupItem);

    void assignDevices(GroupItem groupItem);
}
