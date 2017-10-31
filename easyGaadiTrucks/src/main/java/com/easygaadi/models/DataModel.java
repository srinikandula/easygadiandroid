package com.easygaadi.models;

/**
 * Created by ssv on 23-10-2017.
 */

public class DataModel {
    String name;
    String version;
            int id_;
            int image;
    int isSelected;

    public int getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }



        public void setName(String name) {
                this.name = name;
        }

        public DataModel(String name, String version, int id_, int image,int isSelected) {
        this.name = name;
        this.version = version;
        this.id_ = id_;
        this.image=image;
        this.isSelected=isSelected;
        }

        public String getName() {
                return name;
        }

                public String getVersion() {
        return version;
        }

        public int getImage() {
        return image;
        }

        public int getId() {
                return id_;
        }


        }
