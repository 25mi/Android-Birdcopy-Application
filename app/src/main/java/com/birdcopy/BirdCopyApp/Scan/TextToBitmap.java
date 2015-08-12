package com.birdcopy.BirdCopyApp.Scan;

/**
 * Created by songbaoqiang on 6/25/14.
 */

import java.util.Hashtable;

import android.graphics.*;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class TextToBitmap {
    // 要生成的文字
    private String text;
    // 生成图片的大小
    private int QR_WIDTH, QR_HEIGHT;

    public TextToBitmap(String text, int QR_WIDTH, int QR_HEIGHT) {
        this.text = text;
        this.QR_HEIGHT = QR_HEIGHT;
        this.QR_WIDTH = QR_WIDTH;
    }

    public Bitmap getBitmap() {
        try {
            // 判断文字是否为空
            if (TextUtils.isEmpty(text)) {
                return null;
            }
            // 设置二维码属性，如编码格式，大小，颜色等
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text,
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            // 建立一个bitmap图片，用来接受二维码的颜色。
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap createQRCodeBitmapWithPortrait(Bitmap qr,Bitmap logoBmp)
    {


        Matrix matrix = new Matrix();

        int QR_WIDTH=qr.getWidth();
        int portrait_W=QR_WIDTH/5;
        float roundPx = portrait_W*(float)(0.61*0.61*0.61);
        float scaleWith =(float)(portrait_W-roundPx)/logoBmp.getWidth();

        // 缩放原图
        matrix.postScale(scaleWith,scaleWith);
        Bitmap dstbmp = Bitmap.createBitmap(logoBmp, 0, 0, logoBmp.getWidth(), logoBmp.getHeight(),
                matrix, true);

        // 设置头像要显示的位置，即居中显示
        int left = (int)((QR_WIDTH - portrait_W)/2);

        // 取得qr二维码图片上的画笔，即要在二维码图片上绘制我们的头像
        Canvas canvas = new Canvas(qr);

        //圆角白边
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(left, left, (int)portrait_W, (int)portrait_W);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        // 开始绘制
        canvas.drawBitmap(dstbmp,left+roundPx/2,left+roundPx/2,null);

        return qr;
    }
}
