package com.birdcopy.BirdCopyApp.Comment;

import java.util.ArrayList;

/**
 * Created by vincentsung on 12/17/15.
 */
public class CommentDataResult {


    public String rc;      //操作结果
    public String rm;    //反馈信息

    public String allRecordCount;       //全部记录数

    public String allPageCount;        //全部页数

    public String currentPage;        //当前页数
    public String perPageCount;        //每页记录数
    public ArrayList<FlyingCommentData> rs;    //数据

}
