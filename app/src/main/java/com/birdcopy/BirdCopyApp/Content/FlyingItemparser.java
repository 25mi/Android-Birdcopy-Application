package com.birdcopy.BirdCopyApp.Content;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_DIC_PUB;
import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_DIC_PUB;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincentsung on 1/5/16.
 */
public class FlyingItemparser extends DefaultHandler {

    private static final String  kItemList  = "word_list";
    private static final String  kBEItem    = "word";
    private static final String  kBEWord    = "beword";
    private static final String  kBEIndex   = "beindex";


    private static ArrayList<String> baseElements = new ArrayList<String>();
    private static ArrayList<String> mainElements = new ArrayList<String>();
    private static ArrayList<String> tagEments = new ArrayList<String>();

    private static HashMap<String, String> indexTagDic = new HashMap<String, String>();

    public  void initIndexTagDic()
    {
        baseElements.add(kBEWord);
        baseElements.add(kBEIndex);

        mainElements.add("ref");
        mainElements.add("description");
        mainElements.add("source");
        mainElements.add("target");
        mainElements.add("img");

        tagEments.add("hyph");
        tagEments.add("phonetic");
        tagEments.add("variant");
        tagEments.add("usage");
        tagEments.add("style");
        tagEments.add("field");
        tagEments.add("gram");
        tagEments.add("fre");

        indexTagDic.put("n","0");
        indexTagDic.put("v","1");
        indexTagDic.put("vt","1");
        indexTagDic.put("vi","1");
        indexTagDic.put("aux v","1");
        indexTagDic.put("adj","2");
        indexTagDic.put("adv","3");
        indexTagDic.put("pron","4");
        indexTagDic.put("art","5");
        indexTagDic.put("num","5");
        indexTagDic.put("prep","6");
        indexTagDic.put("conj","7");
        indexTagDic.put("int","8");
    }

    private StringBuilder content;

    private BE_DIC_PUB item;


    public ArrayList<BE_DIC_PUB> entries = new ArrayList<BE_DIC_PUB>();
    public int allRecordCount=0;

    public void startElement(String uri, String localName, String qName,
                             Attributes atts) throws SAXException {

        content = new StringBuilder();

        if(localName.equalsIgnoreCase(kItemList)) {

            allRecordCount = Integer.valueOf(atts.getValue("allRecordCount")).intValue();
        }
        else if(localName.equalsIgnoreCase(kBEItem)) {

            item = new BE_DIC_PUB();
            item.setBEWORD("");
            item.setBEINDEX(9);
            
            entries.add(item);
        }
    }

    public void endElement(String uri,
                           String localName,
                           String qName)
            throws SAXException {

        String aStr="<"+localName+">";
        String bStr="</"+localName+">";

        String tempContent = aStr + content.toString() + bStr;

        if (localName.equalsIgnoreCase(kBEWord)){

            item.setBEWORD(content.toString());
        }
        else if (localName.equalsIgnoreCase(kBEIndex)){

            item.setBEINDEX(Integer.valueOf(content.toString()));
        }
        else if (mainElements.contains(localName)) {

            if (item.getBEENTRY()!=null) {

                item.setBEENTRY(item.getBEENTRY()+tempContent);
            }
            else{

                item.setBEENTRY(tempContent);
            }
        }
        else if (tagEments.contains(localName)){

            if (item.getBETAG()!=null) {

                item.setBETAG(item.getBETAG()+tempContent);
            }
            else{
                item.setBETAG(tempContent);
            }
        }
    }

    public void characters(char[] ch, int start, int length)
            throws SAXException {
        content.append(ch, start, length);
    }

    public void endDocument() throws SAXException {
        // you can do something here for example send
        // the BE_DIC_PUB object somewhere or whatever.
    }
}
