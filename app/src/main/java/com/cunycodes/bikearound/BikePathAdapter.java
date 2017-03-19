package com.cunycodes.bikearound;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BikePathAdapter extends RecyclerView.Adapter<BikePathAdapter.BikePathViewHolder> {

    private ArrayList<PopularPaths> pathList;

    public BikePathAdapter(ArrayList<PopularPaths> list) {
        pathList = list;
    }


    @Override
    public int getItemCount() {
        return pathList.size();
    }

    @Override
    public void onBindViewHolder(BikePathViewHolder bikeViewHolder, int i) {
        PopularPaths path = pathList.get(i);
        bikeViewHolder.title.setText(path.getCardName());
        bikeViewHolder.address.setText(path.getAddress());
        bikeViewHolder.image.setImageResource(path.getImageResourceId());
    }

    @Override
    public BikePathViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.recycle_cards, viewGroup, false);

        return new BikePathViewHolder(itemView);
    }

    public static class BikePathViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView address;
        public ImageView image;

        public BikePathViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.titleTextView);
            address = (TextView) itemView.findViewById(R.id.addressTextView);
            image = (ImageView) itemView.findViewById(R.id.pathImageView);

            ///add onClickListener here
        }
    }
}
