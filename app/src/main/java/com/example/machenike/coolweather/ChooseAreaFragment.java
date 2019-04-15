package com.example.machenike.coolweather;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.machenike.coolweather.db.City;
import com.example.machenike.coolweather.db.County;
import com.example.machenike.coolweather.db.Province;
import com.example.machenike.coolweather.util.HttpUtils;
import com.example.machenike.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment{
    private View view;
    private Button back_bt;
    private TextView choose_area_tv;
    private ListView area_lv;
    //省 市 县 的代号，以及当前所选
    private final int PROVINCE_LEVEL=0;
    private final int CITY_LEVEL=1;
    private final int COUNTY_LEVEL=2;
    private final String PROVINCE ="province";
    private final String CITY="city";
    private final String COUNTY="county";
    private int currentLevel;


    //保存从litepal中取出的数据
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    //显示数据所需的列表
    private List<String> dataList;

    //所选中的省 市
    private Province chooseProvince;
    private City chooseCity;

    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.choose_area_layout, container, false);
        initView(view);
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,dataList);
        area_lv.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        queryProvince();
    }
    private void queryProvince(){
        choose_area_tv.setText("中国");
        back_bt.setVisibility(View.INVISIBLE);
        currentLevel = PROVINCE_LEVEL;
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for (Province province : provinceList)
                dataList.add(province.getName());
            adapter.notifyDataSetChanged();
            area_lv.setSelection(0);
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,PROVINCE);
        }
    }
    private void queryFromServer(String address,final String type){
        HttpUtils.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                boolean judge =false;
                if(type.equals(PROVINCE))
                    judge = Utility.saveProvince(data);

                if(judge){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(type.equals(PROVINCE))
                                queryProvince();
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }


        });

    }
    private void initView(View view) {
        back_bt = (Button) view.findViewById(R.id.back_bt);
        choose_area_tv = (TextView) view.findViewById(R.id.choose_area_tv);
        area_lv = (ListView) view.findViewById(R.id.area_lv);
        provinceList = new ArrayList<>();
        cityList = new ArrayList<>();
        countyList = new ArrayList<>();
        dataList = new ArrayList<>();
    }


}
