package com.birdcopy.BirdCopyApp.IM;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.birdcopy.BirdCopyApp.Component.UserManger.FlyingContext;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.map.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapController;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.PoiOverlay;
import com.tencent.tencentmap.mapsdk.search.GeocoderSearch;
import com.tencent.tencentmap.mapsdk.search.PoiItem;
import com.tencent.tencentmap.mapsdk.search.ReGeocoderResult;

import java.util.ArrayList;
import java.util.List;

import io.rong.message.LocationMessage;

import com.birdcopy.BirdCopyApp.R;

/**
 * Created by DragonJ on 14/11/21.
 */

@SuppressLint("ClickableViewAccessibility")
public class SOSOLocationActivity extends MapActivity implements
        TencentLocationListener, OnClickListener, Handler.Callback, View.OnTouchListener {

    MapView mMapView;
    Button mButton = null;
    LocationMessage mMsg;
    Handler mHandler;
    Handler mWorkHandler;
    MapController mMapController = null;
    TextView mTitle;
    /**
     * 当前地图地址的poi
     */
    private HandlerThread mHandlerThread;


    private final static int RENDER_POI = 1;
    private final static int SHWO_TIPS = 2;

    @Override
    /**
     *显示地图，启用内置缩放控件，并用MapController控制地图的中心点及Zoom级别
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.de_ac_soso_map);

        mHandlerThread = new HandlerThread("LocationThread");
        mHandlerThread.start();
        mWorkHandler = new Handler(mHandlerThread.getLooper());
        mHandler = new Handler(this);

        initView();


        if (getIntent().hasExtra("location")) {
            mMsg = getIntent().getParcelableExtra("location");
        }

        if (mMsg != null)
            mButton.setVisibility(View.GONE);
        mButton.setOnClickListener(this);

        mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件

        if (mMsg == null) {
            GeoPoint point = new GeoPoint((int) (39.90923 * 1E6), (int) (116.397428 * 1E6)); // 用给定的经纬度构造一个GeoPoint，单位是微度

            mMapView.getController().setCenter(point);
            mMapView.getController().setZoom(16);
            mMapView.setOnTouchListener(this);
            TencentLocationRequest request = TencentLocationRequest.create();
            TencentLocationManager.getInstance(this).requestLocationUpdates(request, this);

        } else {
            GeoPoint point = new GeoPoint((int) (mMsg.getLat() * 1E6), (int) (mMsg.getLng() * 1E6)); // 用给定的经纬度构造一个GeoPoint，单位是微度

            PoiItem poiItem = new PoiItem();
            poiItem.name = mMsg.getPoi();
            poiItem.point = point;

            mHandler.obtainMessage(RENDER_POI, poiItem).sendToTarget();
            findViewById(android.R.id.icon).setVisibility(View.GONE);
            mMapView.getController().setCenter(point);
            mMapView.getController().setZoom(16);
        }

    }


    private void initView() {
        mMapView = (MapView) findViewById(android.R.id.widget_frame);
        mTitle = (TextView) findViewById(android.R.id.title);
        mButton = (Button) this.findViewById(android.R.id.button1);
        mMapController = mMapView.getController();
    }

    @Override
    public void onLocationChanged(final TencentLocation tencentLocation,
                                  int code, String s) {
        if (TencentLocation.ERROR_OK == code) {
            Toast.makeText(this, "定位成功", Toast.LENGTH_SHORT).show();


            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    GeoPoint point = new GeoPoint((int) (tencentLocation.getLatitude() * 1E6),
                            (int) (tencentLocation.getLongitude() * 1E6)); // 用给定的经纬度构造一个GeoPoint，单位是微度
                    mMapView.getController().setCenter(point);
                    mWorkHandler.post(new POISearchRunnable());
                }
            });
            TencentLocationManager.getInstance(this).removeUpdates(this);
        } else {
            Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s2) {

    }

    @Override
    protected void onDestroy() {

        if (FlyingContext.getInstance().getLastLocationCallback() != null)
            FlyingContext.getInstance().getLastLocationCallback().onFailure("失败");

        FlyingContext.getInstance().setLastLocationCallback(null);
        TencentLocationManager.getInstance(this).removeUpdates(this);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        if (mMsg != null) {
            FlyingContext.getInstance().getLastLocationCallback().onSuccess(mMsg);
            FlyingContext.getInstance().setLastLocationCallback(null);
            finish();
        } else {
            FlyingContext.getInstance().getLastLocationCallback()
                    .onFailure("定位失败");
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == RENDER_POI) {
            PoiItem poiItem = (PoiItem) msg.obj;

            List<PoiItem> list = new ArrayList<PoiItem>();


            list.add(poiItem);

            mMapView.clearAllOverlays();

            PoiOverlay myPoiOverlay = new PoiOverlay(null);
            mMapView.addOverlay(myPoiOverlay);
            myPoiOverlay.setPoiItems(list);
            myPoiOverlay.showInfoWindow(0);


            Uri uri = Uri
                    .parse("http://apis.map.qq.com/ws/staticmap/v2").buildUpon().appendQueryParameter("size", "240*240")
                    .appendQueryParameter("key", "CQGBZ-DQRWU-CNYVP-4F2OT-4XMDH-U4BT5").appendQueryParameter("zoom", "16")
                    .appendQueryParameter("center", mMapView.getMapCenter().getLatitudeE6() / 1E6 + "," + mMapView.getMapCenter()
                            .getLongitudeE6() / 1E6).build();

            Log.d("uri",uri.toString());

            mMsg = LocationMessage.obtain(poiItem.point.getLatitudeE6() / 1E6,
                    poiItem.point.getLongitudeE6() / 1E6, poiItem.name, uri);
        } else if (msg.what == SHWO_TIPS) {

            PoiItem poiItem = (PoiItem) msg.obj;

            mTitle.setText(poiItem.name);
            mTitle.setVisibility(View.VISIBLE);

            Uri uri = Uri
                    .parse("http://apis.map.qq.com/ws/staticmap/v2").buildUpon().appendQueryParameter("size", "240*240")
                    .appendQueryParameter("key", "7JYBZ-4Y3W4-JMUU7-DJHQU-NOYH7-SRBBU").appendQueryParameter("zoom", "16")
                    .appendQueryParameter("center", mMapView.getMapCenter().getLatitudeE6() / 1E6 + "," + mMapView.getMapCenter()
                            .getLongitudeE6() / 1E6).build();

            mMsg = LocationMessage.obtain(poiItem.point.getLatitudeE6() / 1E6,
                    poiItem.point.getLongitudeE6() / 1E6, poiItem.name, uri);


        }
        return false;
    }

    POISearchRunnable mLastSearchRunnable;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mLastSearchRunnable != null)
                    mWorkHandler.removeCallbacks(mLastSearchRunnable);

                mTitle.setVisibility(View.INVISIBLE);
                mHandler.removeMessages(RENDER_POI);

                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mLastSearchRunnable = new POISearchRunnable();
                mWorkHandler.post(new POISearchRunnable());
                break;
            default:
                break;
        }

        return false;
    }


    private class POISearchRunnable implements Runnable {

        public void run() {
            try {
                GeocoderSearch geocodersearcher = new GeocoderSearch(SOSOLocationActivity.this);
                GeoPoint geoRegeocoder = new GeoPoint(mMapView.getMapCenter().getLatitudeE6(), mMapView.getMapCenter().getLongitudeE6());
                ReGeocoderResult regeocoderResult = geocodersearcher.searchFromLocation(geoRegeocoder);

                if (regeocoderResult == null || regeocoderResult.poilist == null || regeocoderResult.poilist.size() == 0)
                    return;

                PoiItem poiItem = new PoiItem();
                poiItem.name = regeocoderResult.poilist.get(0).name;
                poiItem.point = mMapView.getMapCenter();

                if(getIntent().hasExtra("location"))
                    mHandler.obtainMessage(RENDER_POI, poiItem).sendToTarget();
                else
                    mHandler.obtainMessage(SHWO_TIPS, poiItem).sendToTarget();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
