package com.example.machenike.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.machenike.coolweather.db.AreaSave;
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

import static android.content.Context.MODE_MULTI_PROCESS;

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
    private Province selectProvince;
    private City selectCity;

    private ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private List<AreaSave>  areaSaveList;
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
        initClick();
        queryProvince();
    }
    private void initClick(){
        area_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                if(currentLevel==PROVINCE_LEVEL){
                    selectProvince = provinceList.get(i);
                    queryCity();
                }else if(currentLevel==CITY_LEVEL){
                    selectCity =cityList.get(i);
                    queryCounty();
                }else if(currentLevel==COUNTY_LEVEL){

                    if(getActivity() instanceof  MainActivity){
                        Intent intent = new Intent(getActivity(),WeatherFragmentActivity.class);
//                        editor.putString("weather_id",countyList.get(i).getWeatherId());
//                        editor.commit();
                        intent.putExtra("weather_id",countyList.get(i).getWeatherId());
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof  WeatherFragmentActivity){
                        WeatherFragmentActivity activity=(WeatherFragmentActivity)getActivity();
//                        editor.putString("",countyList.get(i).getWeatherId());
//                        editor.commit();
                        activity.addWeather(countyList.get(i).getWeatherId());
                    }
//                            Intent intent = new Intent(getActivity(),WeatherFragmentActivity.class);
//                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                            SharedPreferences.Editor editor=sharedPreferences.edit();
//                            editor.putString("weather_id",countyList.get(i).getWeatherId()+"");
//                            editor.commit();
//                            intent.putExtra("weather_id",countyList.get(i).getWeatherId()+"");
//                            startActivity(intent);
//                            getActivity().finish();
//                            if(getActivity() instanceof WeatherActivity){
//                                WeatherActivity weatherActivity=(WeatherActivity)getActivity();
//                                weatherActivity.drawer_layout.closeDrawer(Gravity.START);
//                            }

                }
            }
        });
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel==CITY_LEVEL)
                    queryProvince();
                else if(currentLevel==COUNTY_LEVEL)
                    queryCity();
            }
        });
    }
    private void queryProvince(){
        choose_area_tv.setText("中国");
        back_bt.setVisibility(View.INVISIBLE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for (Province province : provinceList)
                dataList.add(province.getName());
            adapter.notifyDataSetChanged();
            area_lv.setSelection(0);
            currentLevel = PROVINCE_LEVEL;
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,PROVINCE);
        }
    }
    private void queryCity(){
        choose_area_tv.setText(selectProvince.getName());
        back_bt.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceCode = ?",String.valueOf(selectProvince.getCode()))
                .find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList)
                dataList.add(city.getName());
            adapter.notifyDataSetChanged();
            area_lv.setSelection(0);
            currentLevel = CITY_LEVEL;
        }else{
            String address = "http://guolin.tech/api/china/"+selectProvince.getCode();
            queryFromServer(address,CITY);
        }
    }
    private void queryCounty(){
        choose_area_tv.setText(selectCity.getName());
        back_bt.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityCode = ?",String.valueOf(selectCity.getCode()))
                .find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county : countyList)
                dataList.add(county.getName());
            adapter.notifyDataSetChanged();
            area_lv.setSelection(0);
            currentLevel = COUNTY_LEVEL;
        }else{
            String address = "http://guolin.tech/api/china/"+selectProvince.getCode()
                    +"/"+selectCity.getCode();
            queryFromServer(address,COUNTY);
        }
    }
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtils.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Log.d("--------",data);
                boolean judge =false;
                if(type.equals(PROVINCE))
                    judge = Utility.saveProvince(data);
                else if(type.equals(CITY))
                    judge =Utility.saveCity(data,selectProvince.getCode());
                else if(type.equals(COUNTY))
                    judge = Utility.saveCounty(data,selectCity.getCode());

                if(judge){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if(type.equals(PROVINCE))
                                queryProvince();
                            else if(type.equals(CITY))
                                queryCity();
                            else if(type.equals(COUNTY))
                                queryCounty();
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       closeProgressDialog();
                        Toast.makeText(getActivity(),"获取数据失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }


        });

    }
    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    private void initView(View view) {
        back_bt = (Button) view.findViewById(R.id.back_bt);
        choose_area_tv = (TextView) view.findViewById(R.id.choose_area_tv);
        area_lv = (ListView) view.findViewById(R.id.area_lv);
        provinceList = new ArrayList<>();
        cityList = new ArrayList<>();
        countyList = new ArrayList<>();
        dataList = new ArrayList<>();
        preferences= getActivity().getSharedPreferences("AreaSave",MODE_MULTI_PROCESS);
        editor=preferences.edit();
        areaSaveList = new ArrayList<>();
    }


}
