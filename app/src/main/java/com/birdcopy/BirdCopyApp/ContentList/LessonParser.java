package com.birdcopy.BirdCopyApp.ContentList;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by songbaoqiang on 6/11/14.
 */
public class LessonParser extends DefaultHandler {

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

    private StringBuilder content;

    private BE_PUB_LESSON lesson;


    public ArrayList<BE_PUB_LESSON> entries = new ArrayList<BE_PUB_LESSON>();
    public int allRecordCount=0;

    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {

        content = new StringBuilder();

        if(localName.equalsIgnoreCase(kLessonList)) {

            allRecordCount = Integer.valueOf(atts.getValue("allRecordCount")).intValue();
        } else if(localName.equalsIgnoreCase(kEntryStr)) {

            lesson = new BE_PUB_LESSON();
            lesson.setBEDLPERCENT(0.0);
            lesson.setBELESSONID(atts.getValue("id"));

            String tempType=atts.getValue("res_type");

            if(tempType.equals("pdf"))
            {
                tempType ="docu";
            }

            lesson.setBECONTENTTYPE(tempType);
            entries.add(lesson);
        }
        else if(localName.equalsIgnoreCase(kContentURL)){
            lesson.setBEDOWNLOADTYPE(atts.getValue("type"));
        }
    }

    public void endElement(String uri,
                           String localName,
                           String qName)
            throws SAXException {

        if(localName.equalsIgnoreCase(kTitleStr)) {

            lesson.setBETITLE(content.toString());
         }
        else if(localName.equalsIgnoreCase(kDescripStr)){
            lesson.setBEDESC(content.toString());
        }
        else if(localName.equalsIgnoreCase(kImageURLStr)){
            lesson.setBEIMAGEURL(content.toString());
        }
        else if(localName.equalsIgnoreCase(kContentURL)){
            lesson.setBECONTENTURL(content.toString());
        }
        else if(localName.equalsIgnoreCase(kSubURLStr)){
            lesson.setBESUBURL(content.toString());
        }
        else if(localName.equalsIgnoreCase(kDurationStr)){

            lesson.setBEDURATION(new Double(0));
        }
        else  if(localName.equalsIgnoreCase(kStartTimeStr)){

            lesson.setBESTARTTIME(new Double(0));
        }
        else if(localName.equalsIgnoreCase(kProURLStr)){
            lesson.setBEPROURL(content.toString());
        }
        else if(localName.equalsIgnoreCase(kLevelStr)){
            lesson.setBELEVEL(content.toString());
        }
        else if(localName.equalsIgnoreCase(kTagStr)){
            lesson.setBETAG(content.toString());
        }
        else if(localName.equalsIgnoreCase(kWebUrlStr)){
            lesson.setBEWEBURL(content.toString());
        }
        else if(localName.equalsIgnoreCase(kISBNStr)){
            lesson.setBEISBN(content.toString());
        }
        else if(localName.equalsIgnoreCase(kPriceStr)){

            lesson.setBECoinPrice(Integer.valueOf(content.toString()));
        }
        else if(localName.equalsIgnoreCase(kRelativeStr)){
            lesson.setBERELATIVEURL(content.toString());
        }

    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        content.append(ch, start, length);
    }

    public void endDocument() throws SAXException {
        // you can do something here for example send
        // the BE_PUB_LESSON object somewhere or whatever.
    }
}
