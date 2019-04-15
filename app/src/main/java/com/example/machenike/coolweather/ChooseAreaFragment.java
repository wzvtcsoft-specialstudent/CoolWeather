package com.example.machenike.coolweather;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ChooseAreaFragment extends Fragment{
    private View view;
    private Button back_bt;
    private TextView choose_area_tv;
    private ListView area_lv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_area_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        back_bt = (Button) view.findViewById(R.id.back_bt);
        choose_area_tv = (TextView) view.findViewById(R.id.choose_area_tv);
        area_lv = (ListView) view.findViewById(R.id.area_lv);
    }


}
