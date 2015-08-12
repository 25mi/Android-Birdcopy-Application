package com.birdcopy.BirdCopyApp.Scan;


import android.graphics.Bitmap;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Hashtable;

public class BitmapToText {
    // 要读取的二维码图片

    private Bitmap bitmap;

    public BitmapToText(Bitmap bitmap) {

        this.bitmap = bitmap;
    }

    // 获得文字
    public String getText()
    {
        int mWidth, mHeight;
        String value = null;

        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        // 设置解析编码
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();
        int[] pixels = new int[mWidth * mHeight];
        bitmap.getPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight);
        RGBLuminanceSource source = new RGBLuminanceSource(mWidth, mHeight,
                pixels);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader2 = new QRCodeReader();
        Result result = null;
        try {
            result = reader2.decode(bitmap1, hints);
            value = result.getText();
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ChecksumException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }
}
