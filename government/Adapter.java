package com.wisaterhunep.government;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.view.ViewGroup;

/*
Adapter needs to extend Adapter in RecyclerView with ViewHolder.
This class will take in a list of government officials and be called from the main activity class.
The class is tied to the official layout
 */
public class Adapter extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "ADAPTER";
    private ArrayList<GovernmentOfficial> governmentOfficialList;
    private MainActivity mainActivity;

    public Adapter(MainActivity mainActivity, ArrayList<GovernmentOfficial> governmentOfficialList) {
        this.governmentOfficialList = governmentOfficialList;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: New Government Official from Official Layout");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.official, parent, false);
        itemView.setOnClickListener(mainActivity);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: set data on Recycler in Main Activity");

        //This will set the fields of each government official: role title, name, and political party
        GovernmentOfficial governmentOfficial = governmentOfficialList.get(position);
        holder.roleTitle.setText(governmentOfficial.getRole());
        holder.name.setText(governmentOfficial.getName());
        holder.politicalParty.setText(String.format("(%s)", governmentOfficial.getPoliticalParty()));
    }

    @Override
    public int getItemCount() {
        return governmentOfficialList.size();
    }
}
