package com.birdcopy.BirdCopyApp.LocalContent;

import com.birdcopy.BirdCopyApp.DataManager.ActiveDAO.BE_PUB_LESSON;
import com.birdcopy.BirdCopyApp.DataManager.FlyingContentDAO;
import com.birdcopy.BirdCopyApp.ShareDefine;

import java.util.ArrayList;
import java.util.List;

public class MylessonsData {

    private ArrayList<BE_PUB_LESSON> allData = new ArrayList<BE_PUB_LESSON>();
    private int allRecordCount=0;
    private FlyingContentDAO mDao;


    public interface DealResult {

        public void parseOK(ArrayList<BE_PUB_LESSON> list);
        public void setMaxItems(int itemCount);
    }

    private DealResult delegate;

    public void setDelegate(DealResult delegate ){

        this.delegate = delegate;

        initdata();
    }

    private void initdata()
    {

        mDao = new FlyingContentDAO();
        allData =(ArrayList<BE_PUB_LESSON>)mDao.loadAllData();

        allRecordCount=allData.size();
    }

    public void loadMoreLessonData(int pageNumber)
    {
        int totalPage = 0;

        if (totalPage != allRecordCount*1.0/ShareDefine.kperpageLessonCount) totalPage ++;

        ArrayList<BE_PUB_LESSON> result= new ArrayList<BE_PUB_LESSON>();

        if(pageNumber<totalPage)
        {
            int start=ShareDefine.kperpageLessonCount*pageNumber;
            int end=ShareDefine.kperpageLessonCount*(pageNumber+1);

            if(end>allRecordCount) end=allRecordCount;

            List<BE_PUB_LESSON> tempList = allData.subList(start,end);

            for(BE_PUB_LESSON item :tempList)
            {

                result.add(item);
            }
        }
        delegate.setMaxItems(allRecordCount);
        delegate.parseOK(result);
    }

}



