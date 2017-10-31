package com.easygaadi.erp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easygaadi.models.DataModel;
import com.easygaadi.trucksmobileapp.Maintenance_Activity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.Trunck_Activity;

import java.util.ArrayList;

/**
 * Created by ssv on 26-10-2017.
 */

public class MaintenanceList extends Fragment {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";
        // The request code must be 0 or greater.
        private static final int PLUS_ONE_REQUEST_CODE = 0;
        // The URL to +1.  Must be a valid URL.
        private final String PLUS_ONE_URL = "http://developer.android.com";
        // TODO: Rename and change types of parameters
        private String mParam1;
        private String mParam2;


        private static RecyclerView.Adapter adapter;
        private RecyclerView.LayoutManager layoutManager;
        private static RecyclerView recyclerView;
        private static ArrayList<DataModel> data;
        private static ArrayList<Integer> removedItems;

        private static ImageView addImage;


        public MaintenanceList() {
            // Required empty public constructor
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TrunkList.
         */
        // TODO: Rename and change types and number of parameters
        public static MaintenanceList newInstance(String param1, String param2) {
            MaintenanceList fragment = new MaintenanceList();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM1, param1);
            args.putString(ARG_PARAM2, param2);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_trunk_list, container, false);

            recyclerView = (RecyclerView) view.findViewById(R.id.quotes_rc);
            recyclerView.setHasFixedSize(true);


            addImage = (ImageView)view.findViewById(R.id.addImage);
            layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            data = new ArrayList<DataModel>();
            for (int i = 0; i < com.easygaadi.erp.TrunkList.MyData.nameArray.length; i++) {
                data.add(new DataModel(
                        com.easygaadi.erp.TrunkList.MyData.nameArray[i],
                        com.easygaadi.erp.TrunkList.MyData.versionArray[i],
                        com.easygaadi.erp.TrunkList.MyData.id_[i],
                        com.easygaadi.erp.TrunkList.MyData.drawableArray[i],
                        0
                ));
            }

            removedItems = new ArrayList<Integer>();

            adapter = new CustomAdapter(data);
            recyclerView.setAdapter(adapter);



            addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), Maintenance_Activity.class));
                }
            });




            return view;
        }



        /**
         * This interface must be implemented by activities that contain this
         * fragment to allow an interaction in this fragment to be communicated
         * to the activity and potentially other fragments contained in that
         * activity.
         * <p>
         * See the Android Training lesson <a href=
         * "http://developer.android.com/training/basics/fragments/communicating.html"
         * >Communicating with Other Fragments</a> for more information.
         */

        public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

            private ArrayList<DataModel> dataSet;

            public class MyViewHolder extends RecyclerView.ViewHolder {

                TextView textViewName;
                TextView textViewVersion,textViewmore,textViewamt,textViewRArea,textViewRType;

                public MyViewHolder(View itemView) {
                    super(itemView);
                    this.textViewName = (TextView) itemView.findViewById(R.id.truckRegNo_tv);
                    this.textViewVersion = (TextView) itemView.findViewById(R.id.tv_lastupadate);
                    this.textViewRType = (TextView) itemView.findViewById(R.id.repairtype_tv);
                    this.textViewRArea = (TextView) itemView.findViewById(R.id.repairarea_tv);
                    this.textViewamt = (TextView) itemView.findViewById(R.id.repairamt_tv);
                }
            }

            public CustomAdapter(ArrayList<DataModel> data) {
                this.dataSet = data;
            }

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mcatitem_layout, parent, false);
                MyViewHolder myViewHolder = new MyViewHolder(view);
                return myViewHolder;
            }



            @Override
            public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

                TextView textViewName = holder.textViewName;
                TextView textViewVersion = holder.textViewVersion;
                TextView textViewmore = holder.textViewmore;

                textViewName.setText(dataSet.get(listPosition).getName());
                textViewVersion.setText(dataSet.get(listPosition).getVersion());
                Log.d("isselected","-->"+dataSet.get(listPosition).getVersion());

            }



            @Override
            public int getItemCount() {
                return dataSet.size();
            }
        }



        public static class MyData {
            static String[] nameArray = {"Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwich","JellyBean", "Kitkat", "Lollipop", "Marshmallow"};
            static String[] versionArray = {"10/01/2017", "10/01/2017", "24/01/2017", "23/01/2017", "22/01/2017", "21/01/2017", "20/01/2017", "19/01/2017", "17/01/2017", "15/01/2017","6/01/2017"};

            static Integer[] drawableArray = {R.drawable.car_damage, R.drawable.car_damage, R.drawable.car_damage,
                    R.drawable.car_damage, R.drawable.car_damage, R.drawable.car_damage, R.drawable.car_damage,
                    R.drawable.car_damage, R.drawable.car_damage, R.drawable.car_damage,R.drawable.car_damage};

            static Integer[] id_ = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        }

    }

