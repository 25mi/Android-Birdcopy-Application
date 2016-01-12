package com.birdcopy.BirdCopyApp.ChannelManage;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class FlyingAlbumParser {

    private static final String kTagList     = "tag_list";
    private static final String kEntryStr    = "tag";
    private static final String kTagStr      = "tagString";
    private static final String kCount       = "tagCount";
    private static final String kImageURLStr = "coverImageURL";
    
    public  static void initParserData()
    {
        resultList = new ArrayList<AlbumData>();
    }

    private static AlbumData albumData;

    public  static ArrayList<AlbumData> resultList;
    public  static String allRecordCount;

    public static void parser (String parserString)
            throws XmlPullParserException, IOException
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(parserString));

        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT)
        {

            switch (eventType) {

                case XmlPullParser.START_DOCUMENT:
                {
                    initParserData();

                    break;
                }
                case XmlPullParser.START_TAG:
                {
                    String tag = xpp.getName();

                    if(kTagList.equalsIgnoreCase(tag)) {

                        allRecordCount = xpp.getAttributeValue(0);
                    }
                    else if (kEntryStr.equalsIgnoreCase(tag)) {

                        albumData = new AlbumData();
                        resultList.add(albumData);
                    }
                    if(kTagStr.equalsIgnoreCase(tag)) {

                        albumData.setTagString(xpp.nextText());
                    }
                    else if(kImageURLStr.equalsIgnoreCase(tag)){
                        albumData.setImageURL(xpp.nextText());
                    }
                    else if(kCount.equalsIgnoreCase(tag)){

                        albumData.setCount(Integer.parseInt(xpp.nextText()));
                    }

                    break;
                }
            }

            eventType = xpp.next();
        }
    }
}
