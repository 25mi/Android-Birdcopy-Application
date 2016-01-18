package com.birdcopy.BirdCopyApp.Download;


import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
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
import java.io.OutputStream;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by vincentsung on 1/5/16.
 */
public class FlyingFileManager {

    //////////////////////////////////////////////////////////////
    //文件夹管理
    //////////////////////////////////////////////////////////////
    static final String KUserDataFoldName = ".birdcopy";
    static final String KUserDownloadsDir = "downloads";
    static final String kShareBaseDir     = "shareBase";
	static final String kUserMediaDir     = "media";
	static final String KUserCrushDir     = "crush";

	public static String getUserLocalDir() {

        String relativePath = KUserDataFoldName + File.separator + FlyingDataManager.getBirdcopyAppID();
	    File dir = getFile(relativePath);

        if (!dir.exists()) {

	        dir.mkdirs();
        }

        return relativePath;
    }

	public static String getUserDownloadDir() {

        String relativePath = getUserLocalDir()+ File.separator + KUserDownloadsDir;
		File dir = getFile(relativePath);

	    if (!dir.exists()) {

		    dir.mkdirs();
	    }

	    return relativePath;
    }

    //分享资源本地路径
    public static String getUserShareDir()
    {

        String relativePath = getUserLocalDir() + File.separator + kShareBaseDir;
	    File dir = getFile(relativePath);

	    if (!dir.exists()) {

		    dir.mkdirs();
	    }

	    return relativePath;
    }

	public static String getUseMediaDir() {

		String relativePath = getUserLocalDir() + File.separator + kUserMediaDir;
		File dir = getFile(relativePath);

		if (!dir.exists()) {

			dir.mkdirs();
		}

		return relativePath;
	}

	public static String getUserCrushDir() {

		String relativePath = getUserLocalDir() + File.separator + KUserCrushDir;
		File dir = getFile(relativePath);

		if (!dir.exists()) {

			dir.mkdirs();
		}

		return relativePath;
	}

	//课程资源本地的路径
    public static String getLessonDownloadDir(String lessonID)
    {

        String relativePath = getUserDownloadDir() + File.separator + lessonID;
	    File dir = getFile(relativePath);

	    if (!dir.exists()) {

		    dir.mkdirs();
	    }

	    return relativePath;
    }


	public static File getFile(String  relativePath)
	{
		File path = Environment.getExternalStorageDirectory();

		return new File(path, relativePath);
	}

	//////////////////////////////////////////////////////////////
	//文件路径管理
	//////////////////////////////////////////////////////////////

	public static String getAPPFilePath()
	{

		String targetPath = getUserDownloadDir()+File.separator+ShareDefine.KAppInstallFile;

		return targetPath;
	}

	//分享资源文件下载到本地的路径
	public static String getUserShareBaseFilePath()
	{
		String targetPath = getUserShareDir()+File.separator+ShareDefine.kShareBaseTempFile;

		return targetPath;
	}

    //课程的字幕本地文件路径
    public static String getLessonSubFilePath(String lessonID)
    {
        String fileName = lessonID+"."+ShareDefine.kContentSubtitleExt;
        String targetPath =  getLessonContentPathWithFileName(lessonID, fileName);

        return targetPath;
    }

    //课程的字典本地文件路径(ZIP文件)
    public static String getLessonDicZipFilePath(String lessonID)
    {
        String fileName = lessonID+"."+ShareDefine.kContentDicPatchExt;
        String targetPath =  getLessonContentPathWithFileName(lessonID, fileName);

        return targetPath;
    }

    public static String getLessonDicXMLFilePath(String lessonID)
    {
	    String fileName = ShareDefine.KLessonDicXMLFile;

	    String targetPath =  getLessonContentPathWithFileName(lessonID, fileName);

        return targetPath;
    }

    //课程的补充相关资源本地文件路径(文件)
    public static String getLessonRelatedZipFilePath(String lessonID)
    {
        String fileName = "relative"+"."+ShareDefine.kContentRelativPatcheExt;
        String targetPath =  FlyingFileManager.getLessonContentPathWithFileName(lessonID, fileName);

        return targetPath;
    }

    //课程的背景音乐相关资源本地文件路径
    public static String getLessonBackgroundFilePath(String lessonID)
    {
	    String fileName = ShareDefine.kResource_Background_filenmae;

	    String targetPath =  FlyingFileManager.getLessonContentPathWithFileName(lessonID,fileName);

        return targetPath;
    }

    //课程的主内容的本地文件路径
    public static String getLessonContentFilePath(String lessonID, String contentType, String contentURL)
    {
        if (contentType.equalsIgnoreCase(ShareDefine.KContentTypeVideo))
        {
            if(ShareDefine.checkMp4URL(contentURL))
            {
                String fileName =lessonID+"."+ShareDefine.kContentVedioExt;
                return   getLessonContentPathWithFileName(lessonID, fileName);
             }
            else if(ShareDefine.checkM3U8(contentURL))
            {
                String fileName =lessonID+"."+ShareDefine.kContentVedioLivingExt;
                return   getLessonContentPathWithFileName(lessonID, fileName);
            }
        }

        else if (contentType.equalsIgnoreCase(ShareDefine.KContentTypeAudio))
        {
            String fileName =lessonID+"."+ShareDefine.kContentAudioExt;
            return   getLessonContentPathWithFileName(lessonID, fileName);
        }
        else if (contentType.equalsIgnoreCase(ShareDefine.KContentTypeText))
        {

            String extension = ShareDefine.getExtension(contentURL);

            if(extension.equalsIgnoreCase("pdf"))
            {
                String fileName =lessonID+"."+extension;
                return   getLessonContentPathWithFileName(lessonID, fileName);
            }
        }
        else if (contentType.equalsIgnoreCase(ShareDefine.KContentTypePageWeb))
        {
            return null;
        }

        return   getLessonContentPathWithFileName(lessonID, lessonID + "." + ShareDefine.kContentUnkownExt);

    }

    //获取字典路径
    public static String getMyDicDBFilePath() {

        return getUserShareDir()+File.separator+"mydic.db";
    }

    private static String getLessonContentPathWithFileName(String lessonID, String fileName) {

        return getLessonDownloadDir(lessonID) + File.separator + fileName;
    }

    //////////////////////////////////////////////////////////////
    //文件操作管理
    //////////////////////////////////////////////////////////////
    public static boolean fileExists(String filePath) {

        boolean flag = false;

        if(filePath!=null)
        {
	        File file = getFile(filePath);

	        if (file.isFile() && file.exists()) {

                flag = true;
            }
        }

        return flag;
    }

    static  public String getStringFromFile(String filePath) throws IOException {

	    File file = getFile(filePath);

	    BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
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
    public static  boolean unzip( String fromPath, String toPath, boolean remove ) throws IOException
    {

	    File fromFile = getFile(fromPath);

        if (!fromFile.exists() || fromPath.equals(toPath))
            return false;

        unzip(new FileInputStream(fromFile), getFile(toPath));

        if (remove) deleteFile(fromPath);

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


	    File file = getFile(sPath);

        if (!file.exists()) {  // 不存在返回 false

	        return flag;
        }
        else {

            // 判断是否为文件
            if (file.isFile()) {  // 为文件时调用删除文件方法

	            return deleteFile(sPath);
            }
            else {  // 为目录时调用删除目录方法

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

	    File dirFile = getFile(sPath);        // 判断目录或文件是否存在

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

	    File file = getFile(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }


    private static MediaScannerConnection msConn = null;

    public static void savePhotoToLocal(Bitmap bmp, String name) {

	    File imageFileFolder = new File(getFile(getUseMediaDir()), "图片");

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
}
