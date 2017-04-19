package com.cunycodes.bikearound;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by j on 4/18/2017.
 */

public class PlaceFragment extends DialogFragment implements TextView.OnEditorActionListener {
    private EditText editText;
    private Button btn_set;
    private Button   btn_cancel;
    private TextView textView;

    public PlaceFragment() {
    }

    public static PlaceFragment newInstance(String title){

        PlaceFragment fragment = new PlaceFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.place_fragment, container);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    /*    editText = (EditText) view.findViewById(R.id.editText);
      //  String title = getArguments().getString("title", "Enter Destination");
        getDialog().setTitle("Enter Destination");

        editText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); */

        editText = (EditText) view.findViewById(R.id.editText);
        textView = (TextView) view.findViewById(R.id.required);
        textView.setVisibility(View.INVISIBLE);

        btn_set = (Button) view.findViewById(R.id.btn_set);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = ((PlanActivity) getActivity()).getPosition();

                if (position != -1){
                    EventPlan plan = ((PlanActivity) getActivity()).adapter.getPlan(position);
                    String place = editText.getText().toString();
                    if (place.length() != 0) {
                        plan.setPlace(place);
                        ((PlanActivity) getActivity()).updatePlanDate(plan);
                        getDialog().dismiss();
                        Log.d("PlaceFragment", "Place Updated");
                    } else {
                        textView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getActivity(), "ButtonSetwasClicked", Toast.LENGTH_SHORT).show();
                    String place = editText.getText().toString();
                    if (place.length() != 0) {
                        ((PlanActivity) getActivity()).setPlace(place);
                        ((PlanActivity) getActivity()).addPlan();
                        getDialog().dismiss();
                        getDialog().cancel();
                    } else {
                        textView.setVisibility(View.VISIBLE);
                    }
                }

            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId){
            Toast.makeText(getActivity(), "EditActionRecieved", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
