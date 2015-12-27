package com.birdcopy.BirdCopyApp.Component.Base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.artifex.mupdfdemo.SystemHelper;
import com.birdcopy.BirdCopyApp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by songbaoqiang on 6/9/14.
 */
public class ShareDefine
{
    //文件格式和文件名
    public static final String KUserDBResource = "userModel";
    public static final String KDicModelName = "dicModel";
    public static final String KDBType = "db";
    public static final String kLessonVedioExt = "mp4";
    public static final String kLessonAudioExt = "mp3";
    public static final String kLessonHtmlExt = "html";
    public static final String kLessonVedioLivingExt = "m3u8";
    public static final String kLessonDocExt = "beunknown";
    public static final String kLessonSubtitleType = "srt";
    public static final String kLessonCoverType = "jpg";
    public static final String kLessonProType = "zip";
    public static final String kLessonRelativeType = "zip";
    public static final String kLessonUnkownType = "beunknown";

    public static final String kResource_Background = "bmu_doc_url";
    public static final String kResource_Background_filenmae = "background.mp3";

    public static final String KGiftCountNow = "giftCountNow";

    public static final String KPNGType = "png";
    public static final String KJPGType = "jpg";

    public static final String KBaseDatdbaseFilename = "mydic.db";
    public static final String KUserDatdbaseFilename = "myuser.db";
    public static final String KRongUserDatdbaseFilename = "myronguser.db";

    public static final int K_BEID_MD5_DIGEST_LENGTH = 1024;

    public static final String kWebCommand_Play_Vedio = "BE_Local_Play_Vedio";
    public static final String kM3U8_NotFound = "BE_M3U8_NotFound";

    public static final String PlayIconIPhone = "Play";
    public static final String PlayIconIPAD = "Play=2x";

    public static final String KPriceIDstr = "金币";

    //API相关
    public static final int kperpageLessonCount = 20;
    public static final int kperpageCoverCount = 6;

    public static final int MAX_INT = 2147483647;

    //内容类型
    public static final String KContentTypePageWeb = "web_pg";
    public static final String KContentTypeVideo = "video";
    public static final String KContentTypeAudio = "audio";
    public static final String KContentTypeText = "docu";

    //下载相关
    public static final int NSOffState = 0;
    public static final int NSOnState = 1;
    public static final int NSMixedState = 2;

    public static final String KDownloadTypeNormal = "mp4";
    public static final String KDownloadTypeM3U8 = "m3u8";
    public static final String KDownloadTypeMagnet = "magnet";

    private static final String KUSerDataFoldName = ".birdcopy";

    public static final String KDownloadTypeForK12 = "no_magnet";
    private static final String KUserDownloadsDir = "Downloads";
    private static final String KUserCacheDir = "Cache";

    public static final String KlessonStateChange = "KlessonStateChange";
    public static final int maxDownloadLessonThread = 2;

    //安卓客户端辅助参数
    public static final String KLessonID = "KLessonID";
    public static final String KLessonTitle = "KLessonTitle";

    public static final String KIntenCorParameter = "KIntenCorParameter";

    public static final String KAppDownloadURL = "KAppDownloadURL";

    // APP下载参数
    public static String downloadURL = null;

    //单个课程资源API相关
    public static final String KlessonResouceQRType = "lnview_matrix_str";

    //扫描码参数
    public static final String KQRTyepeChargeCard = "KQRTyepeChargeCard";
    public static final String KQRTyepeWebURL = "KQRTyepeWebURL";
    public static final String KQRTyepeCode = "KQRTyepeCode";
    public static final String KQRTypeLogin = "KQRTypeLogin";
    public static final String KQRTypeUnkonow = "KQRTypeUnkonow";

    public static final String KBEWebLesssonIDFlag = "_lnviewlnid=";
    public static final String KBELoginFlag = "_loginsenid=";

    private static MediaScannerConnection msConn = null;

    // 请求CODE
    public final static int CHANNEL_REQUEST = 1;
    public final static int SCANNIN_REQUEST_CODE = 2;
    public final static int SEARCHING_REQUEST_CODE = 3;
    public final static int CAMERA_WITH_DATA_REQUEST = 4;
    public final static int PHOTO_WITH_DATA_REQUEST = 5;
    public final static int SHOWLESSON_REQUEST_CODE = 6;

    //全局广播
    public final static String KMessagerUserdata = "KMessagerUserdata";
    public final static String KMessagerPortrait = "KMessagerPortrait";

    //聊天相关
    public final static String KCustomerServiceId = "KEFU1408935920969";
    public final static String KIMAPPKEY = "KEFU1408935920969";
    public final static String KIMAPPSECRETc = "lSqEkeediUF";

    public final static String KIMNIKENAME = "imNikeName";
    public final static String KIMPORTRAITURI = "imPortraitUri";
    public final static String KIMTOKEN = "imToken";

    //新的好友
    public static final int FRIENDLIST_REQUESTCODE = 1001;
    //搜索
    public static final int SEARCH_REQUESTCODE = 1002;
    //添加好友
    public static final int PERSONAL_REQUESTCODE = 1003;
    //加入群组
    public static final int GROUP_JOIN_REQUESTCODE = 1004;
    //退出群组
    public static final int GROUP_QUIT_REQUESTCODE = 1005;
    //修改用户名称
    public static final int FIX_USERNAME_REQUESTCODE = 1006;
    //删除好友
    public static final int DELETE_USERNAME_REQUESTCODE = 1007;
    //修改讨论组名称
    public static final int FIX_DISCUSSION_NAME = 1008;
    //修改群名片
    public static final int FIX_GROUP_INFO = 1010;
    //修改设置页面
    public static final int UPDATE_DISCUTION_NUMBER = 1009;
    //@消息
    public static final int RONG_MESSAGE_REPLY = 1010;
    public static final String RONG_DEFAULT = "default";
    public static final String RONG_TOKEN = "RONG_TOKEN";
    //public static final String APP_USER_ID = "DEMO_USERID";
    //public static final String APP_USER_NAME = "DEMO_USER_NAME";
    //public static final String APP_USER_PORTRAIT = "DEMO_USER_PORTRAIT";

    //启动相关
    public final static int KMAXCountsLoading = 16;

    /**
     * 调整返回的RESULTCODE
     */
    public final static int CHANNELRESULT = 10;

    public final static String SAVED_OBJECT_KEY = "SAVED_OBJECT_KEY";


    //年费临时性功能相关

    public final static int KPricePerYear = 68800;
    //public final static int    KPricePerYear   = 1;

    public static String getPingplusOnePayURL() {
        String result = "http://" +
                ShareDefine.getServerNetAddress() +
                "/pa_get_o_charge_from_tn.action";

        return result;
    }

    public static String getLessonAccount(
            String contentType,
            String downloadType) {

        if (contentType == null) contentType = "";
        if (downloadType == null) downloadType = "";

        String result = "http://" +
                ShareDefine.getServerNetAddress() +
                "/la_get_ln_list_for_hp.action?vc=3" +
                "&url_2_type=" +
                downloadType +
                "&res_type=" +
                contentType +
                "&ln_owner=" +
                ShareDefine.getLessonOwner() +
                "&type=rc";

        return result;
    }

    public static String getLessonListByTagURL(
            String contentType,
            String downloadType,
            String tag,
            int pageNumber,
            boolean sortByTime) {
        String sortBy;

        if (sortByTime) {
            sortBy = "upd_time desc";
        } else {
            sortBy = "upd_time";
        }

        if (contentType == null) contentType = "";
        if (downloadType == null) downloadType = "";
        if (tag == null) tag = "";

        try {
            tag = URLEncoder.encode(tag, "utf-8");
            sortBy = URLEncoder.encode(sortBy, "utf-8");
        } catch (Exception e) {
            return null;
        }

        String result = "http://" +
                ShareDefine.getServerNetAddress() +
                "/la_get_ln_list_for_hp.action?vc=3&perPageCount=" +
                ShareDefine.kperpageLessonCount +
                "&page=" +
                pageNumber +
                "&url_2_type=" +
                downloadType +
                "&ln_tag=" +
                tag +
                "&res_type=" +
                contentType +
                "&ln_owner=" +
                ShareDefine.getLessonOwner() +
                "&sortindex=" +
                sortBy;

        return result;
    }

    public static String getCoverLessonList(boolean homeRec,
                                            int pageNumber) {

        String result = "http://" +
                ShareDefine.getServerNetAddress() +
                "/la_get_ln_list_for_hp.action?vc=3&perPageCount=" +
                ShareDefine.kperpageCoverCount +
                "&page=" +
                pageNumber +
                "&ln_owner=" +
                ShareDefine.getLessonOwner();

        result += "&owner_recom=1";

        if (homeRec) {
            result += "&owner_recom=1";
        } else {
            result += "&owner_recom_c=1";
        }

        return result;
    }

    public static String getLessonDataByID(String lessonID) {

        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/la_get_ln_detail_for_hp.action?ln_id=" +
                lessonID;
    }

    public static String getresourceOfLessonID(String lessonID, String resourceType, String resourceFormat) {
        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/la_get_ln_rel_url_for_hp.action?type=" +
                resourceType +
                "&getType=" +
                resourceFormat;
    }

    public static String getLessonResource(String lessonID, String resourceType) {
        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/la_get_ln_rel_url_for_hp.action?md5_value=" +
                lessonID +
                "&type=" +
                resourceType;
    }

    public static String getAlbumListByTagURL(String lessonType,
                                              int pageNumber,
                                              boolean sortByTime,
                                              boolean homeRec) {
        String sortBy;

        if (sortByTime) {

            sortBy = "upd_time desc";
        } else {
            sortBy = "upd_time";
        }

        try {
            sortBy = URLEncoder.encode(sortBy, "utf-8");
        } catch (Exception e) {
            return null;
        }

        if (lessonType == null) lessonType = "";

        String result = "http://" +
                ShareDefine.getServerNetAddress() +
                "/la_get_tag_list_for_hp.action?perPageCount=" +
                ShareDefine.kperpageLessonCount +
                "&page=" +
                pageNumber +
                "&res_type=" +
                lessonType +
                "&tag_owner=" +
                ShareDefine.getLessonOwner();

        if (homeRec) {
            result += "&owner_recom=1";
        }

        return result;
    }

    public static String getTagListStrByTag(String tag, int count) {

        if (tag == null) {
            tag = "";
        }

        try {
            tag = URLEncoder.encode(tag, "utf-8");
        } catch (Exception e) {
            return null;
        }

        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/la_get_tag_string_for_hp.action?vc=3&perPageCount=" +
                count +
                "&page=" +
                1 +
                "&ln_tag=" +
                tag +
                "&ln_owner=" +
                ShareDefine.getLessonOwner();
    }

    public static String getAppVersionAndURL() {
        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/aa_get_app_info_from_hp.action?app_id=" +
                ShareDefine.getLocalAppID() +
                "&type=max";
    }

    public static String getAppBroadURL() {
        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/aa_get_app_info_from_hp.action?app_id=" +
                ShareDefine.getLocalAppID() +
                "&type=img1";
    }

    public static String getAppWebURL() {
        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/aa_get_app_info_from_hp.action?app_id=" +
                ShareDefine.getLocalAppID() +
                "&type=page";
    }

    public static String getChatURL(String lessonID) {
        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/mwc.screen?ln_id=" +
                lessonID;
    }

    public static String getRongTokenURL(String openID) {
        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/tu_rc_get_urt_from_hp.action?tuser_key=" +
                openID;
    }

    public static String getRefreshUseInfoURL(String passport, String name, String portraitURL) {
        if (passport != null) {
            String result = "http://" +
                    ShareDefine.getServerNetAddress() +
                    "/tu_rc_sync_urb_from_hp.action?tuser_key=" +
                    passport;

            if (name != null) {
                result += "&name=";
                result += name;
            }

            if (portraitURL != null) {
                result += "&portrait_uri=";
                result += portraitURL;
            }

            return result;
        } else {
            return null;
        }
    }

    public static String getUsrInfoByRongID(String rongID) {
        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/tu_rc_get_usr_from_hp.action?user_id=" +
                rongID;
    }


    public static String getExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            int mark = filename.lastIndexOf('?');

            if ((dot > -1) && (dot < (filename.length() - 1))) {
                if (mark > -1) {
                    return filename.substring(dot + 1, mark);
                }

                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public static boolean createFolderName(String folderName) {
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    public static String getUserDownloadPath() {

        String downloadPath = KUSerDataFoldName + "/" + KUserDownloadsDir;

        File folderName = Environment.getExternalStoragePublicDirectory(downloadPath);

        if (!folderName.exists()) {

            folderName.mkdirs();
        }

        return downloadPath;
    }

    public static File getUserCachePath() {

        String downloadPath = KUSerDataFoldName + "/" + KUserCacheDir;

        File folderName = Environment.getExternalStoragePublicDirectory(downloadPath);

        if (!folderName.exists()) {

            folderName.mkdirs();
        }

        return folderName;
    }

    public static String getLessonDownloadPath(String lessonID) {
        String path = KUSerDataFoldName + "/" + KUserDownloadsDir + "/" + lessonID;

        File folderName = Environment.getExternalStoragePublicDirectory(path);

        if (!folderName.exists()) {

            folderName.mkdirs();
        }

        return folderName.getAbsolutePath();
    }

    public static String getLessonContentFilename(String lessonID, String contentURL) {

        return lessonID + "." + ShareDefine.getExtension(contentURL);
    }


    public static String getLessonContentPathWithFileName(String lessonID, String fileName) {
        return ShareDefine.getLessonDownloadPath(lessonID) + "/" + fileName;
    }

    public static String getLessonContentPath(String lessonID, String contentURL) {

        return ShareDefine.getLessonDownloadPath(lessonID) + "/" + ShareDefine.getLessonContentFilename(lessonID, contentURL);
    }

    public static String getCrushFolder() {

        String path = KUSerDataFoldName + "/" + "crush";

        File folderName = Environment.getExternalStoragePublicDirectory(path);

        if (!folderName.exists()) {

            folderName.mkdirs();
        }

        return folderName.getAbsolutePath();
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

    public static String bytesToString(byte[] _bytes) {
        String file_string = "";

        for (int i = 0; i < _bytes.length; i++) {
            file_string += (char) _bytes[i];
        }

        return file_string;
    }

    public static boolean checkNetWorkStatus() {

        Context context = MyApplication.getInstance().getBaseContext();

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //check for wifi also
        WifiManager connec = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (cm != null) {
            NetworkInfo.State wifi = cm.getNetworkInfo(1).getState();
            if (connec.isWifiEnabled()
                    && wifi.toString().equalsIgnoreCase("CONNECTED")) {
                return true;
            }
        }
        //check for network
        try {
            if (cm != null) {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Log.e("Network Avail Error", ex.getMessage());
        }
        return false;
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

    public static String judgeScanType(String scanStr) {
        if (scanStr.matches("[0-9a-zA-Z]+") && scanStr.length() == 33) {
            return KQRTyepeChargeCard;
        }
        if (scanStr.contains("matrix_loginsenid=")) {
            return KQRTypeLogin;
        } else if (scanStr.contains("http://")) {
            return KQRTyepeWebURL;
        } else if (scanStr.matches("[0-9]+")) {

            return KQRTyepeCode;
        } else {
            return KQRTypeUnkonow;
        }
    }

    public static String getLessonIDFromOfficalURL(String webURL) {

        int start = webURL.indexOf(KBEWebLesssonIDFlag);

        if (start != -1) {

            start += KBEWebLesssonIDFlag.length();
            int end = start + 32;

            return webURL.substring(start, end);
        } else {
            return null;
        }
    }

    public static String getLoginIDFromQR(String qrStr) {

        int start = qrStr.indexOf(KBELoginFlag);

        if (start != -1) {

            start += KBELoginFlag.length();
            int end = qrStr.length();
            return qrStr.substring(start, end);
        } else {
            return null;
        }
    }

    public static boolean checkM3U8(String sPath) {

        return sPath.contains("m3u8");
    }

    public static int getVersionCode() throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = MyApplication.getInstance().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(MyApplication.getInstance().getPackageName(), 0);
        return packInfo.versionCode;
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String savePhotoToSDCard(Bitmap photoBitmap, String path,
                                           String photoName) {

        String filePath = "";
        if (ShareDefine.checkSDcardStatus()) {
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

    public static void broadUserDataChange() {
        Intent intent = new Intent(ShareDefine.getKUSERDATA_CHNAGE_RECEIVER_ACTION());

        MyApplication.getInstance().sendBroadcast(intent);
    }

    public static void broadPortraitChange() {
        Intent intent = new Intent(KMessagerPortrait);

        MyApplication.getInstance().sendBroadcast(intent);
    }


    public static String getMD5(String string) {
        if (string == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte[] md5hash = md.digest();
            StringBuilder builder = new StringBuilder();
            for (byte b : md5hash) {
                builder.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            return builder.toString();
        } catch (NoSuchAlgorithmException nsae) {
            System.out.println("Cannot find digest algorithm");
            System.exit(1);
        }
        return null;
    }

    public static DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_image) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.icon)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.error_image)  //设置图片加载/解码过程中错误时候显示的图片
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .build();//构建完成

        return options;
    }

    public static String getServerNetAddress() {
        return MyApplication.getInstance().getResources().getString(R.string.KServerNetAddress);
    }

    public static String getLessonOwner() {
        return MyApplication.getInstance().getResources().getString(R.string.KlessonQwner);
    }

    public static String getLocalAppID() {
        return MyApplication.getInstance().getResources().getString(R.string.KLocalAppID);
    }

    public static String getpakagename() {
        return MyApplication.getInstance().getResources().getString(R.string.KPakagename);
    }

    public static String getKSERVICE_ACTION() {
        return MyApplication.getInstance().getResources().getString(R.string.KSERVICE_ACTION);
    }

    public static String getKRECEIVER_ACTION() {
        return MyApplication.getInstance().getResources().getString(R.string.KRECEIVER_ACTION);
    }

    public static String getKUSERDATA_CHNAGE_RECEIVER_ACTION() {
        return MyApplication.getInstance().getPackageName() + ShareDefine.KMessagerUserdata;
    }

    public static boolean thereIsConnection(Context context) {

        if (SystemHelper.isEmulator(context)) {
            return true;
        }

        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null) {
            return false;
        }
        if (!i.isConnected()) {
            return false;
        }
        if (!i.isAvailable()) {
            return false;
        }
        return true;
    }

    public static boolean checkURL(String string) {

        final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher("example.com");//replace with string to compare
        if (m.find()) {

            return true;
        } else {
            return false;
        }
    }

    public static String getpingplusURL(String currentPassport) {
        return "http://" +
                ShareDefine.getServerNetAddress() +
                "/ua_get_user_info_from_hp.action?tuser_key=" +
                currentPassport +
                "&app_id=" +
                ShareDefine.getLocalAppID() +
                "&type=validth";
    }

    public  static  String getdayhoursecond(String dateFormat)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String result=null;
        try
        {
            Date d1 = new Date();

            Date d2 = df.parse(dateFormat);

            long diff = d1.getTime() - d2.getTime();

            long day=diff/(24*60*60*1000);

            if(day>0)
            {
                result= day+"天前";
            }
            else
            {
                long hour=diff/(60*60*1000);

                if (hour>0)
                {
                    result= hour+"小时前";
                }
                else
                {
                    long min=diff/(60*1000);

                    if (min>0)
                    {
                        result= min+"分钟前";
                    }
                    else
                    {
                        long sec =diff/1000;
                        result= sec+"秒前";
                    }
                }
            }
        }
        catch (Exception e)

        {

        }

        return result;
    }
}
