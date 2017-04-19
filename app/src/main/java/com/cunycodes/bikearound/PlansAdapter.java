package com.cunycodes.bikearound;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by j on 4/18/2017.
 */

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.PlansViewHolder> {

    private ArrayList<EventPlan> plansList;
    private SparseBooleanArray itemsSelected;
    private PlansViewHolder.ClickListener listener;

    public PlansAdapter(ArrayList<EventPlan> plansList) {

        this.plansList = plansList;
        itemsSelected = new SparseBooleanArray();
    }

    public PlansAdapter (PlansViewHolder.ClickListener listener){
        this.listener = listener;
    }

    public PlansAdapter (ArrayList<EventPlan> lists, PlansViewHolder.ClickListener listener){
        plansList = lists;
        this.listener = listener;
        itemsSelected = new SparseBooleanArray();
    }
    @Override
    public PlansViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);

        return new PlansViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(PlansViewHolder holder, int position) {

        EventPlan plan = plansList.get(position);
        holder.place.setText(plan.getPlace());
        holder.date.setText(plan.getDate());
        holder.time.setText(plan.getTime());

        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE :View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return plansList.size();
    }

    public void toggleSelection(int i){

        selectView(i, !itemsSelected.get(i));
    }

    public int getSelectedCount(){
        return itemsSelected.size();
    }

    public void selectView(int i, boolean b) {
        if (b)
            itemsSelected.put(i, b);
        else
            itemsSelected.delete(i);

        notifyDataSetChanged();
    }

    public SparseBooleanArray getItemsSelected(){
        return itemsSelected;
    }

    public void removeSelection(){
        itemsSelected = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void removePlan(EventPlan plan){
        plansList.remove(plan);
        notifyDataSetChanged();
    }

    public void addPlan(EventPlan plan){
        plansList.add(plan);
        notifyDataSetChanged();
    }

    public boolean isSelected(int position){
        return getSelectedItems().contains(position);
    }

    public List<Integer> getSelectedItems(){
        List<Integer> items = new ArrayList<>(itemsSelected.size());
        for (int i = 0; i< itemsSelected.size(); i++){
            items.add(itemsSelected.keyAt(i));
        }

        return items;
    }

    public void removeItem(int position){
        plansList.remove(position);
        notifyItemRemoved(position);
    }

    public EventPlan getPlan(int position){
        return plansList.get(position);
    }

    public void upDateItem(int position){
        notifyItemChanged(position);
    }

  /*  public int getPosition(EventPlan plan){
         return plansList.indexOf(plan);
    } */

    public static class PlansViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView place;
        private TextView time;
        private TextView date;
        private View selectedOverlay;
        private final Context context;
        private ClickListener listener;


        public PlansViewHolder(View itemView, ClickListener listener) {
            super(itemView);
            context = itemView.getContext();

            selectedOverlay = (View) itemView.findViewById(R.id.selected_overlay);
            place = (TextView) itemView.findViewById(R.id.place_name);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);

            this.listener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (listener != null){
                listener.onItemClicked(getLayoutPosition());
                ((PlanActivity)context).setPosition(getAdapterPosition());
            }
            Log.d("PlansAdapter", "A view was pressed");
        }


        @Override
        public boolean onLongClick(View v) {

            if (listener != null){
                return listener.onItemLongClicked(getLayoutPosition());
            }
            Log.d("PlansAdapter", "Long Click was pressed");
            return false;
        }

        public interface ClickListener{
            public void onItemClicked(int position);
            public boolean onItemLongClicked(int position);

        }
    }

}
