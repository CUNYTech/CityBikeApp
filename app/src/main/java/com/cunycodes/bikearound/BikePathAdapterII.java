package com.cunycodes.bikearound;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class BikePathAdapterII extends RecyclerView.Adapter<BikePathAdapterII.BikePathViewHolder> {

    private ArrayList<BikePath> bikeList;
    private Context context;

    public BikePathAdapterII(Context context, ArrayList<BikePath> list){
        this.context = context;
        bikeList = list;}


    @Override
    public  BikePathViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.recycle_cards, parent, false);

        return new BikePathAdapterII.BikePathViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BikePathViewHolder holder, int position) {
        BikePath path = bikeList.get(position);
        holder.title.setText(path.getCardName());
        holder.address.setText(path.getAddress());
        holder.lat.setText(String.valueOf(path.getLat()));
        holder.lon.setText(String.valueOf(path.getLon()));
        Picasso.with(this.context).load(path.getImageURL()).fit().into(holder.image);

    }

    @Override
    public int getItemCount() {
        return bikeList.size();
    }

    public static class BikePathViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title;
        public TextView address;
        public ImageView image;
        private final Context context;
        private TextView lon;
        private TextView lat;

        public BikePathViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            title = (TextView) itemView.findViewById(R.id.titleTextView);
            address = (TextView) itemView.findViewById(R.id.addressTextView);
            image = (ImageView) itemView.findViewById(R.id.pathImageView);
            lon = (TextView) itemView.findViewById(R.id.lon_value);
            lat = (TextView) itemView.findViewById(R.id.lat_value);

            ///add onClickListener here
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            String newAddress = title.getText().toString()+" "+address.getText().toString();
            final Intent intent;
            intent = new Intent(context, MapsActivity.class);
            StringBuilder builder = new StringBuilder("");
            builder.append(lat.getText().toString());
            builder.append(", ");
            builder.append(lon.getText().toString());

            intent.putExtra("address", String.valueOf(builder));
            context.startActivity(intent);
        }

    }
}
