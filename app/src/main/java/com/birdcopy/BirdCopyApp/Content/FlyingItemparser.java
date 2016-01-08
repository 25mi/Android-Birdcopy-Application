package com.birdcopy.BirdCopyApp.Content;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_DIC_PUB;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincentsung on 1/5/16.
 */
public class FlyingItemparser {

    private static final String  kItemList  = "word_list";
    private static final String  kBEItem    = "word";
    private static final String  kBEWord    = "beword";
    private static final String  kBEIndex   = "beindex";
    private static final String  kBEEntry   = "beentry";
    private static final String  kBETag     = "betag";


    private static ArrayList<String> baseElements = new ArrayList<String>();
    private static ArrayList<String> mainElements = new ArrayList<String>();
    private static ArrayList<String> tagEments = new ArrayList<String>();

    private static HashMap<String, String> indexTagDic = new HashMap<String, String>();

    private static HashMap<String, String> keyMap = new HashMap<String, String>();


    public  static void initIndexTagDic()
    {
        baseElements.add(kBEWord);
        baseElements.add(kBEIndex);

        mainElements.add("ref");
        mainElements.add("description");
        mainElements.add("source");
        mainElements.add("target");
        mainElements.add("usage");

        keyMap.put("ref", "[参见]");
        keyMap.put("description", "[释意]");
        keyMap.put("source", "[例句]");
        keyMap.put("target", "[中文]");
        keyMap.put("usage", "[用法]");

        tagEments.add("hyph");
        tagEments.add("phonetic");
        tagEments.add("variant");
        tagEments.add("usage");
        tagEments.add("field");
        tagEments.add("gram");
        tagEments.add("fre");

        keyMap.put("hyph","[发音]");
        keyMap.put("phonetic","[音标]");
        keyMap.put("variant","[变形]");
        keyMap.put("derivative","[衍生]");
        keyMap.put("field","[使用领域]");
        keyMap.put("gram","[语法]");
        keyMap.put("fre","[词频]");

        keyMap.put("0","[名词]");
        keyMap.put("1","[动词]");
        keyMap.put("2","[形容词]");
        keyMap.put("3","[副词]");
        keyMap.put("4","[代词]");
        keyMap.put("5","[限定词]");
        keyMap.put("6","[介词]");
        keyMap.put("7","[连词]");
        keyMap.put("8","[叹词]");
        keyMap.put("9","[其它]");

        resultList = new ArrayList<BE_DIC_PUB>();
    }

    private static BE_DIC_PUB item;
    public  static ArrayList<BE_DIC_PUB> resultList;
    public  static int allRecordCount=0;

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
                    initIndexTagDic();

                    break;
                }
                case XmlPullParser.START_TAG:
                {
                    String tag = xpp.getName();

                    if(kItemList.equalsIgnoreCase(tag)) {

                        allRecordCount = Integer.valueOf(xpp.getAttributeValue(0)).intValue();
                    }
                    else if (kBEItem.equalsIgnoreCase(tag)) {

                        item = new BE_DIC_PUB();
                        item.setBEWORD("");
                        item.setBEINDEX(9);

                        resultList.add(item);
                    }
                    else if (kBEWord.equalsIgnoreCase(tag)) {

                        item.setBEWORD(xpp.nextText());
                    }
                    else if (kBEIndex.equalsIgnoreCase(tag)) {

                        item.setBEINDEX(Integer.valueOf(xpp.nextText()));
                    }
                    else {

                        if (mainElements.contains(tag)) {

                            if (item.getBEENTRY()!=null) {

                                item.setBEENTRY(item.getBEENTRY()+keyMap.get(tag)+xpp.nextText());
                            }
                            else{

                                item.setBEENTRY(keyMap.get(tag)+xpp.nextText());
                            }
                        }
                        else if (tagEments.contains(tag)) {

                            if (item.getBETAG()!=null) {

                                item.setBETAG(item.getBETAG()+keyMap.get(tag)+xpp.nextText());
                            }
                            else{
                                item.setBETAG(keyMap.get(tag)+xpp.nextText());
                            }
                        }
                    }

                    break;
                }
            }

            eventType = xpp.next();
        }

        System.out.println("End document");
    }
}
