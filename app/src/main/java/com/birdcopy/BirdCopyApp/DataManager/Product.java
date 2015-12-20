package com.birdcopy.BirdCopyApp.DataManager;

/**
 * Created by birdcopy on 15/7/26.
 */
public class Product {

    public String productName;
    public int count;
    public int productPrice;

    public Product(String productName,int productPrice,int count){

        this.productName=productName;
        this.productPrice=productPrice;
        this.count=count;
    }
}

