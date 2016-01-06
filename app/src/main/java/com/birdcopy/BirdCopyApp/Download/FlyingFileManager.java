package com.birdcopy.BirdCopyApp.Download;


import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.birdcopy.BirdCopyApp.MyApplication;
import com.birdcopy.BirdCopyApp.ShareDefine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by vincentsung on 1/5/16.
 */
public class FlyingFileManager {

    //////////////////////////////////////////////////////////////
    //文件位置管理
    //////////////////////////////////////////////////////////////
    static final String KUSerDataFoldName = ".birdcopy";
    static final String KUserDownloadsDir = "Downloads";
    static final String KUserCacheDir     = "Cache";
    static final String kShareBaseDir     = "shareBase";

    public static String getUserDownloadPath() {

        String downloadPath = KUSerDataFoldName + "/" + KUserDownloadsDir;

        File folderName = Environment.getExternalStoragePublicDirectory(downloadPath);

        if (!folderName.exists()) {

            folderName.mkdirs();
        }

        return downloadPath;
    }

    //分享资源本地路径
    public static String getUserSharePath()
    {

        String path = getUserDownloadPath() + "/" + kShareBaseDir;

        File folderName = Environment.getExternalStoragePublicDirectory(path);

        if (!folderName.exists()) {

            folderName.mkdirs();
        }

        return folderName.getAbsolutePath();
    }

    //分享资源文件下载到本地的路径
    public static String getUserShareTargetPath()
    {
        String targetPath = FlyingFileManager.getUserDownloadPath()+"/"+ShareDefine.kShareBaseTempFile;

        return targetPath;
    }

    public static String getLessonDownloadPath(String lessonID)
    {

        String path = getUserDownloadPath() + "/" + lessonID;

        File folderName = Environment.getExternalStoragePublicDirectory(path);

        if (!folderName.exists()) {

            folderName.mkdirs();
        }

        return folderName.getAbsolutePath();
    }

    //课程的字幕本地文件路径
    public static String getLessonSubTargetPath(String lessonID)
    {
        String fileName = lessonID+"."+ShareDefine.kLessonSubtitleType;
        String targetPath =  FlyingFileManager.getLessonContentPathWithFileName(lessonID, fileName);

        return targetPath;
    }

    //课程的字典本地文件路径(ZIP文件)
    public static String getLessonDicPatchTargetPath(String lessonID)
    {
        String fileName = lessonID+"."+ShareDefine.kLessonProType;
        String targetPath =  FlyingFileManager.getLessonContentPathWithFileName(lessonID, fileName);

        return targetPath;
    }

    public static String getLessonDicXMLTargetPath(String lessonID)
    {
        String fileName = lessonID+"."+ShareDefine.kLessonProType;

        String targetPath =  FlyingFileManager.getLessonContentPathWithFileName(lessonID, fileName);

        return targetPath;
    }

    //课程的补充相关资源本地文件路径(文件)
    public static String getLessonRelatedTargetPath(String lessonID)
    {
        String fileName = "relative"+"."+ShareDefine.kLessonProType;
        String targetPath =  FlyingFileManager.getLessonContentPathWithFileName(lessonID, fileName);

        return targetPath;
    }

    //课程的背景音乐相关资源本地文件路径
    public static String getLessonBackgroundTargetPath(String lessonID)
    {
        String targetPath =  FlyingFileManager.getLessonContentPathWithFileName(lessonID,ShareDefine.kResource_Background_filenmae);

        return targetPath;
    }

    public static String getLessonContentPath(String lessonID, String contentURL) {

        return getLessonDownloadPath(lessonID) + "/" + getLessonContentFilename(lessonID, contentURL);
    }

    private static String getLessonContentPathWithFileName(String lessonID, String fileName) {

        return getLessonDownloadPath(lessonID) + "/" + fileName;
    }

    public static String getLessonContentFilename(String lessonID, String contentURL) {

        return lessonID + "." + ShareDefine.getExtension(contentURL);
    }

    public static String getCrushFolder() {

        String path = KUSerDataFoldName + "/" + "crush";

        File folderName = Environment.getExternalStoragePublicDirectory(path);

        if (!folderName.exists()) {

            folderName.mkdirs();
        }

        return folderName.getAbsolutePath();
    }

    public static File getUserCachePath() {

        String downloadPath = KUSerDataFoldName + "/" + KUserCacheDir;

        File folderName = Environment.getExternalStoragePublicDirectory(downloadPath);

        if (!folderName.exists()) {

            folderName.mkdirs();
        }

        return folderName;
    }

    //////////////////////////////////////////////////////////////
    //文件操作
    //////////////////////////////////////////////////////////////

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean fileExists(String sPath) {

        boolean flag = false;

        File file = new File(sPath);
        if (file.isFile() && file.exists()) {

            flag = true;
        }
        return flag;
    }


    /**
     *读取文件到字符串
     */
    static  public String getStringFromFile(String fileName) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }


    /**
     *ZIP解压缩
     */
    public static  boolean unzip( String fromStr, String toStr, boolean remove ) throws IOException
    {
        File from = new File(fromStr);
        if (!from.exists() || fromStr.equals(toStr))
            return false;

        unzip(new FileInputStream(from), new File(toStr));

        if (remove) deleteFile(fromStr);

        return true;
    }

    public static void unzip( InputStream fromIs, File toFolder) throws IOException
    {
        if (!toFolder.exists())
            toFolder.mkdirs();

        long sumBytes = 0;
        ZipInputStream zis = new ZipInputStream(fromIs);
        try
        {
            ZipEntry ze = zis.getNextEntry();
            byte[] buffer = new byte[8 * 1024];
            while (ze != null)
            {
                if (ze.isDirectory())
                {
                    new File(toFolder, ze.getName()).mkdir();
                } else
                {
                    double factor = 1;
                    if (ze.getCompressedSize() > 0 && ze.getSize() > 0)
                        factor = (double) ze.getCompressedSize() / ze.getSize();

                    File newFile = new File(toFolder, ze.getName());
                    FileOutputStream fos = new FileOutputStream(newFile);
                    try
                    {
                        int len;
                        while ((len = zis.read(buffer)) > 0)
                        {
                            fos.write(buffer, 0, len);
                            sumBytes += len * factor;
                        }
                    } finally
                    {
                        fos.close();
                    }
                }

                ze = zis.getNextEntry();
            }
            zis.closeEntry();
        } finally
        {
            zis.close();
        }
    }

    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     *
     * @param sPath 要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
     */
    public static boolean DeleteFolderOrFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 判断目录或文件是否存在
        if (!file.exists()) {  // 不存在返回 false
            return flag;
        } else {
            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法
                return deleteFile(sPath);
            } else {  // 为目录时调用删除目录方法
                return deleteDirectory(sPath);
            }
        }
    }

    /**
     * 删除目录（文件夹）以及目录下的文件
     *
     * @param sPath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    //////////////////////////////////////////////////////////////
    //图片文件操作
    //////////////////////////////////////////////////////////////

    public static String savePhotoToSDCard(Bitmap photoBitmap, String path,
                                           String photoName) {

        String filePath = "";
        if (checkSDcardStatus()) {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File photoFile = new File(path, photoName + ".png");
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                            fileOutputStream)) {
                        fileOutputStream.flush();
                        filePath = photoFile.toString();
                        System.out.println(filePath);
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    private static MediaScannerConnection msConn = null;

    public static void savePhoto(Bitmap bmp, String name) {
        File imageFileFolder = new File(Environment.getExternalStorageDirectory(), "图片");
        imageFileFolder.mkdir();
        FileOutputStream out = null;
        Calendar c = Calendar.getInstance();
        if (name == null) {
            name = String.valueOf(c.get(Calendar.MONTH))
                    + String.valueOf(c.get(Calendar.DAY_OF_MONTH))
                    + String.valueOf(c.get(Calendar.YEAR))
                    + String.valueOf(c.get(Calendar.HOUR_OF_DAY))
                    + String.valueOf(c.get(Calendar.MINUTE))
                    + String.valueOf(c.get(Calendar.SECOND));
        }

        File imageFileName = new File(imageFileFolder, name.toString() + ".jpg");
        try {
            out = new FileOutputStream(imageFileName);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            scanPhoto(imageFileName.toString());
            out = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scanPhoto(final String imageFileName) {

        msConn = new MediaScannerConnection(MyApplication.getInstance(), new MediaScannerConnection.MediaScannerConnectionClient() {
            public void onMediaScannerConnected() {
                msConn.scanFile(imageFileName, null);
                Log.i("msClient objy", "connection established");
            }

            public void onScanCompleted(String path, Uri uri) {
                msConn.disconnect();
                Log.i("msClient Utility", "scan completed");
            }
        });
        msConn.connect();
    }

    public static boolean createFolderName(String folderName) {
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    public static boolean checkSDcardStatus() {
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(file.getPath());
            long availableBlock = statfs.getAvailableBlocks();
            long blockSize = statfs.getBlockSize();
            long availableSize = availableBlock * blockSize / 1024 / 1024;
            if (availableSize >= 50) {
                return true;
            } else {

            }
        }
        return false;
    }
}
