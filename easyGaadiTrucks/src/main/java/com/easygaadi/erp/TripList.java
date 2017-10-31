package com.easygaadi.erp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.easygaadi.models.DataModel;
import com.easygaadi.trucksmobileapp.Party_Activity;
import com.easygaadi.trucksmobileapp.R;
import com.easygaadi.trucksmobileapp.Trips_Activty;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link } interface
 * to handle interaction events.
 * Use the {@link TripList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TripList extends Fragment {
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


    public TripList() {
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
    public static TripList newInstance(String param1, String param2) {
        TripList fragment = new TripList();
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
                startActivity(new Intent(getActivity(), Trips_Activty.class));
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

            TextView textViewPName,textVieCon,textViesrctodest_tv;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewPName = (TextView) itemView.findViewById(R.id.partyName_tv);
                this.textVieCon = (TextView) itemView.findViewById(R.id.partyContact_tv);
                this.textViesrctodest_tv = (TextView) itemView.findViewById(R.id.srctodest_tv);
            }
        }

        public CustomAdapter(ArrayList<DataModel> data) {
            this.dataSet = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pcatitem_layout, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }



        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView textViewPName = holder.textViewPName;
            TextView textVieCon = holder.textVieCon;
            TextView textViewmore = holder.textViesrctodest_tv;

            textViewPName.setText(dataSet.get(listPosition).getName());
            textVieCon.setText(dataSet.get(listPosition).getVersion());

        }



        @Override
        public int getItemCount() {
            return dataSet.size();
        }
    }



    public static class MyData {
        static String[] nameArray = {"Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb", "Ice Cream Sandwich","JellyBean", "Kitkat", "Lollipop", "Marshmallow"};
        static String[] versionArray = {"258711963", "9632887415", "11554465465", "987466212", "22012017", "2105512017", "209632012017", "196985741012017", "1796325012017", "1552682017","6258012017"};

        static Integer[] drawableArray = {R.drawable.car_damage, R.drawable.car_damage, R.drawable.car_damage,
                R.drawable.car_damage, R.drawable.car_damage, R.drawable.car_damage, R.drawable.car_damage,
                R.drawable.car_damage, R.drawable.car_damage, R.drawable.car_damage,R.drawable.car_damage};

        static Integer[] id_ = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

    }

}
