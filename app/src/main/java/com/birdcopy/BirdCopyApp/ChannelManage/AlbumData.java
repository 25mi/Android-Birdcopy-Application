package com.birdcopy.BirdCopyApp.ChannelManage;

/**
 * Created by songbaoqiang on 6/23/14.
 */
public class AlbumData
{

    String mTagString;
    String mImageURL;
    int     mCount;

    public AlbumData()
    {
        mTagString="";
        mImageURL="";
        mCount=0;
    }

    public void setTagString(String tagString)
    {
        mTagString=tagString;
    }

    public void setImageURL(String imageURL)
    {
        mImageURL = imageURL;
    }

    public void setCount(int count)
    {
        mCount=count;
    }

    public String getTagString()
    {
        return mTagString;
    }

    public String getImageURL()
    {
        return mImageURL;
    }

    public int getCount()
    {
        return mCount;
    }
}



