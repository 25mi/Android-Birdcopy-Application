package com.birdcopy.BirdCopyApp.IM;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.sea_monster.resource.Resource;

import java.util.List;

import io.rong.imkit.RLog;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

import com.birdcopy.BirdCopyApp.R;

/**
 * Created by zhjchen on 8/12/15.
 */
public abstract class LocationMapActivity extends BasicMapActivity implements AMap.OnMarkerClickListener, AMapLocationListener {
    private LocationManagerProxy mLocationManagerProxy;

    protected Conversation.ConversationType conversationType;
    protected String targetId = null;

    @Override
    protected void initData() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);

        mLocationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, 5 * 1000, 1, this);

        mLocationManagerProxy.setGpsEnable(true);

        Log.e("locationActivity", "---------init-------------");
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getAMapException().getErrorCode() == 0) {
            //获取位置信息
            Double geoLat = amapLocation.getLatitude();
            Double geoLng = amapLocation.getLongitude();

            Log.e("locationActivity", "--geoLat-" + geoLat);
//            moveMarker(new LatLng(geoLat, geoLng), DemoContext.getInstance().getCurrentUserInfo());

            RongIMClient.getInstance().updateRealTimeLocationStatus(conversationType, targetId, geoLat, geoLng);
        }
    }

    public void addMarker(LatLng latLng, final UserInfo userInfo) {

        final String url = userInfo.getPortraitUri().toString();
        Log.d("LocationMapActivity", "LocationMapActivity addMarker-- url:" + url);

        final MarkerOptions markerOption = new MarkerOptions();

        markerOption.position(latLng);

        View view = LayoutInflater.from(this).inflate(R.layout.rc_icon_rt_location_marker, null);
        AsyncImageView imageView = (AsyncImageView) view.findViewById(android.R.id.icon);
        ImageView locImageView = (ImageView) view.findViewById(android.R.id.icon1);

        if (userInfo.getUserId().equals(RongIMClient.getInstance().getCurrentUserId())) {
            locImageView.setImageResource(R.drawable.rc_rt_loc_myself);
        } else {
            locImageView.setImageResource(R.drawable.rc_rt_loc_other);
        }

        imageView.setResource(new Resource(url));

        markerOption.anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromView(view));

        Marker marker = getaMap().addMarker(markerOption);
        marker.setObject(userInfo.getUserId());

        RLog.e(this, "addMarker", "addMarker:userId---" + userInfo.getUserId());
    }

    public void removeMarker(String userId) {

        RLog.e(this, "removeMarker", "removeMarker:userId---" + userId);

        List<Marker> markers = getaMap().getMapScreenMarkers();

        for (Marker marker : markers) {
            RLog.e(this, "removeMarker", "removeMarker:getObject---" + marker.getObject());
            if (marker.getObject() != null && userId.equals(marker.getObject())) {
                marker.remove();
                break;
            }
        }
        RLog.e(this, "removeMarker", "------------------end");
    }


    public void moveMarker(final LatLng latLng, final UserInfo userInfo) {
        removeMarker(userInfo.getUserId());
        addMarker(latLng, userInfo);
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        return true;
    }

    /**
     * ------ begin ------
     * 定位
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * ----- end ------
     * 定位
     *
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        mLocationManagerProxy.setGpsEnable(false);
        mLocationManagerProxy.destroy();
        super.onDestroy();
    }
}
