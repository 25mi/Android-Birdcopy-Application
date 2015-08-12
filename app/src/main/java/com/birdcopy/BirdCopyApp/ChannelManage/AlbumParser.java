package com.birdcopy.BirdCopyApp.ChannelManage;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class AlbumParser extends DefaultHandler {

    private static final String kTagList     = "tag_list";
    private static final String kEntryStr    = "tag";
    private static final String kTagStr      = "tagString";
    private static final String kCount       = "tagCount";
    private static final String kImageURLStr = "coverImageURL";
    
    private StringBuilder content;

    private AlbumData albumData;


    public ArrayList<AlbumData> entries = new ArrayList<AlbumData>();
    public int allRecordCount=0;

    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {

        content = new StringBuilder();

        if(localName.equalsIgnoreCase(kTagList)) {

            allRecordCount = Integer.valueOf(atts.getValue("allRecordCount")).intValue();
        } else if(localName.equalsIgnoreCase(kEntryStr)) {

            albumData = new AlbumData();
            entries.add(albumData);
        }
    }

    public void endElement(String uri,
                           String localName,
                           String qName)
                throws SAXException
    {

        if(localName.equalsIgnoreCase(kTagStr)) {

            albumData.setTagString(content.toString());
        }
        else if(localName.equalsIgnoreCase(kImageURLStr)){
            albumData.setImageURL(content.toString());
        }
        else if(localName.equalsIgnoreCase(kCount)){

            albumData.setCount(Integer.valueOf(content.toString()));
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        content.append(ch, start, length);
    }

    public void endDocument() throws SAXException {
        // you can do something here for example send
        // the AlbumData object somewhere or whatever.
    }
}
