package com.wisaterhunep.government;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView roleTitle;
    TextView name;
    TextView politicalParty;

    ViewHolder(View view) {
        super(view);
        roleTitle = view.findViewById(R.id.roleTitle);
        name = view.findViewById(R.id.name);
        politicalParty = view.findViewById(R.id.policalParty);
    }
}
