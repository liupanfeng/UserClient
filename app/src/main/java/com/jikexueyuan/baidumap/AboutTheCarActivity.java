package com.jikexueyuan.baidumap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.jikexueyuan.baidumap.lis.onLocationCallBackListener;
import com.jikexueyuan.baidumap.service.ChatService;
import com.jikexueyuan.baidumap.util.SPUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AboutTheCarActivity extends Activity implements onLocationCallBackListener, View.OnClickListener {
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    // 定位相关
    private LocationClient mLocClient;
    boolean isFirstLoc = true; // 是否首次定位
    public MyLocationListenner myListener = new MyLocationListenner();

    private  double latitude;
    private double longitude;

    private boolean type=false;

    private ChatService chatService;
    private   GeoCoder geoCoder;
    private Button btn_input_des,btn_input_current,btn_start_take_car;
    private String address;
    ServiceConnection mSc = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChatService.MyBinder myBinder=(ChatService.MyBinder)service;
            chatService=myBinder.getService();
            chatService.setOnLocationCallBackListener(AboutTheCarActivity.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initView();
        startService();
        initData();
        initSetting();
        App.getInstance().showSnackMessage(null, (String) SPUtil.getInstant().get(SPUtil.USER_NAME, ""));
    }

    private void startService() {
        Intent service = new Intent(this.getApplicationContext(),ChatService.class);
        this.bindService(service, mSc, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）


        mMapView = (MapView) findViewById(R.id.mapview);
        btn_input_des=(Button)findViewById(R.id.btn_input_des);
        btn_input_des.setOnClickListener(this);
        btn_input_current=(Button)findViewById(R.id.btn_input_current);
//        btn_input_current.setOnClickListener(this);
        btn_start_take_car=(Button)findViewById(R.id.btn_start_take_car);
        btn_start_take_car.setOnClickListener(this);
        setWidth(width, btn_input_des);
        setWidth(width, btn_input_current);
        setWidth(width, btn_start_take_car);
    }

    private void setWidth(int width,Button btn) {
        LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) btn.getLayoutParams();
        params.width=width*2/3;
        btn.setLayoutParams(params);
    }

    private void initData() {
        geoCoder = GeoCoder.newInstance();
        type=true;
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);

    }

    private void initSetting() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Override
    public void callBackData(String lat, String lon) {
        StartLocation(lat, lon);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_input_des:
                Intent intentDes=new Intent(AboutTheCarActivity.this,InputDesActivity.class);
                startActivityForResult(intentDes,1);
                break;
            case R.id.btn_start_take_car:
                if (btn_input_des.getText().toString().equals("填目的地")){
                    Toast.makeText(getApplicationContext(),"请填写目的地！！！",Toast.LENGTH_SHORT).show();
                    return;
                }
//                if (btn_input_current.getText().toString().equals("填出发地")){
//                    Toast.makeText(getApplicationContext(),"请填写出发地！！！",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Intent intentT=new Intent(AboutTheCarActivity.this,TakeCarActivity.class);
                startActivity(intentT);
                break;
            case R.id.btn_input_current:
                Intent intent=new Intent(AboutTheCarActivity.this,InputCurrentActivity.class);
                startActivityForResult(intent, 2);
                break;
        }

    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
             latitude = location.getLatitude();
             longitude = location.getLongitude();

            if (chatService!=null){
                chatService.send(longitude,latitude);
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(latitude).longitude(longitude)
                    .build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) { // 首次定位，做这些处理
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                latlngToAddress(ll);
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                        .newMapStatus(builder.build()));
            }
        }
        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    private void latlngToAddress(LatLng latlng) {
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null ||  result.error != GeoCodeResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(getApplicationContext(), "找不到该地址!",Toast.LENGTH_SHORT).show();
                    return;
                }
                address= result.getAddress();
                btn_input_current.setText(address);
            }
        });
    }
    /**
     *  拿到经纬度，进行定位
     * @param lat
     * @param lon
     */
    public void StartLocation(String lat,String lon) {
        if(type){
            LatLng ptCenter = new LatLng((Float.valueOf(lat)),
                    (Float.valueOf(lon)));
            mBaiduMap.clear();
            mBaiduMap.addOverlay(new MarkerOptions().position(ptCenter)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marka)));
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(ptCenter));
        }
    }

    @Override
    protected void onPause() {
        type=true;
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        type=true;
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        type=false;
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        unbindService(mSc);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                btn_input_des.setText(data.getStringExtra("data"));
                break;
            case 2:
                btn_input_current.setText(data.getStringExtra("data"));
                break;
        }
    }
}
