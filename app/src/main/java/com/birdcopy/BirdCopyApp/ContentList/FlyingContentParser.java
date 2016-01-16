package com.birdcopy.BirdCopyApp.ContentList;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by songbaoqiang on 6/11/14.
 */
public class FlyingContentParser {

    private static final String  kLessonList  = "lesson_list";
    private static final String  kEntryStr    = "lesson";
    private static final String  kTitleStr    = "title";
    private static final String  kDescripStr  = "description";
    private static final String  kImageURLStr = "coverImageURL";
    private static final String  kContentURL  = "contentURL";
    private static final String  kSubURLStr   = "subtitleURL";
    private static final String  kProURLStr   = "mindURL";
    private static final String  kLevelStr    = "diffLevel";
    private static final String  kDurationStr = "duration";
    private static final String  kStartTimeStr= "startTime";
    private static final String  kTagStr      = "ln_tag";
    private static final String  kPriceStr    = "ln_price";
    private static final String  kWebUrlStr   = "ln_url";
    private static final String  kISBNStr     = "ln_isbn";
    private static final String  kRelativeStr = "ln_relatve";

    private static BE_PUB_LESSON lesson;
    public static String allRecordCount;
    public  static ArrayList<BE_PUB_LESSON> resultList;


    public  static void initParserData()
    {
        resultList = new ArrayList<BE_PUB_LESSON>();
    }

    public static void parser (String parserString)
            throws XmlPullParserException, IOException
    {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(parserString));

        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {

            switch (eventType) {

                case XmlPullParser.START_DOCUMENT: {
                    initParserData();

                    break;
                }
                case XmlPullParser.START_TAG:
                {
                    String tag = xpp.getName();

                    if(kLessonList.equalsIgnoreCase(tag)) {

                        allRecordCount = xpp.getAttributeValue(null, "allRecordCount");
                    }
                    else if(kEntryStr.equalsIgnoreCase(tag)) {

                        lesson = new BE_PUB_LESSON();
                        lesson.setBEDLPERCENT(0.0);
                        lesson.setBELESSONID(xpp.getAttributeValue(null, "id"));
                        lesson.setBEOFFICIAL(true);
	                    lesson.setBETAG("");

                        String tempType=xpp.getAttributeValue(null, "res_type");

                        if(tempType.equals("pdf"))
                        {
                            tempType ="docu";
                        }

                        lesson.setBECONTENTTYPE(tempType);
                        resultList.add(lesson);
                    }
                    else if(kContentURL.equalsIgnoreCase(tag)){

                        String type =xpp.getAttributeValue(null, "type");

                        lesson.setBECONTENTURL(xpp.nextText());

                        lesson.setBEDOWNLOADTYPE(type);
                    }
                    else if(kTitleStr.equalsIgnoreCase(tag)) {

                        lesson.setBETITLE(xpp.nextText());
                    }
                    else if(kDescripStr.equalsIgnoreCase(tag)){
                        lesson.setBEDESC(xpp.nextText());
                    }
                    else if(kImageURLStr.equalsIgnoreCase(tag)){
                        lesson.setBEIMAGEURL(xpp.nextText());
                    }
                    else if(kContentURL.equalsIgnoreCase(tag)){
                        lesson.setBECONTENTURL(xpp.nextText());
                    }
                    else if(kSubURLStr.equalsIgnoreCase(tag)){
                        lesson.setBESUBURL(xpp.nextText());
                    }
                    else if(kDurationStr.equalsIgnoreCase(tag)){

                        lesson.setBEDURATION(new Double(0));
                    }
                    else  if(kStartTimeStr.equalsIgnoreCase(tag)){

                        lesson.setBESTARTTIME(new Double(0));
                    }
                    else if(kProURLStr.equalsIgnoreCase(tag)){
                        lesson.setBEPROURL(xpp.nextText());
                    }
                    else if(kLevelStr.equalsIgnoreCase(tag)){
                        lesson.setBELEVEL(xpp.nextText());
                    }
                    else if(kTagStr.equalsIgnoreCase(tag)){
                        lesson.setBETAG(xpp.nextText());
                    }
                    else if(kWebUrlStr.equalsIgnoreCase(tag)){
                        lesson.setBEWEBURL(xpp.nextText());
                    }
                    else if(kISBNStr.equalsIgnoreCase(tag)){
                        lesson.setBEISBN(xpp.nextText());
                    }
                    else if(kPriceStr.equalsIgnoreCase(tag)){

                        lesson.setBECoinPrice(Integer.valueOf(xpp.nextText()));
                    }
                    else if(kRelativeStr.equalsIgnoreCase(tag)){
                        lesson.setBERELATIVEURL(xpp.nextText());
                    }

                    break;
                }
            }

            eventType = xpp.next();
        }

        System.out.println("End document");
    }
}
