package com.cunycodes.bikearound;

import android.content.Context;
import android.content.Intent;
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

    public static class BikePathViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title;
        public TextView address;
        public ImageView image;
        private final Context context;

        public BikePathViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            title = (TextView) itemView.findViewById(R.id.titleTextView);
            address = (TextView) itemView.findViewById(R.id.addressTextView);
            image = (ImageView) itemView.findViewById(R.id.pathImageView);

            ///add onClickListener here
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            final Intent intent;

            if (getAdapterPosition() == 0){
                intent = new Intent(context, MapsActivity.class);
                intent.putExtra("address", title.getText().toString());
            } else if (getAdapterPosition() == 1){
                intent = new Intent(context, MapsActivity.class);
                intent.putExtra("address", title.getText().toString());
            } else if (getAdapterPosition() == 2 ){
                intent = new Intent(context, MapsActivity.class);
                intent.putExtra("address", title.getText().toString());
            } else if (getAdapterPosition() == 3) {
                intent = new Intent(context, MapsActivity.class);
                intent.putExtra("address", title.getText().toString());
            } else if (getAdapterPosition()==3){
                intent = new Intent(context, MapsActivity.class);
                intent.putExtra("address", title.getText().toString());
            } else if (getAdapterPosition() == 4) {
                intent = new Intent(context, MapsActivity.class);
                intent.putExtra("address", title.getText().toString());
            } else {
                intent = new Intent(context, MapsActivity.class);
                intent.putExtra("address", title.getText().toString());
            }

            context.startActivity(intent);
        }


    }
}
