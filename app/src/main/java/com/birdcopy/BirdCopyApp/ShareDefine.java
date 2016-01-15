package com.birdcopy.BirdCopyApp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.birdcopy.BirdCopyApp.DataManager.FlyingDataManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public static final String KContentDBExt = "db";
    public static final String kContentVedioExt = "mp4";
    public static final String kContentAudioExt = "mp3";
    public static final String kContentHtmlExt = "html";
    public static final String kContentVedioLivingExt = "m3u8";
    public static final String kContentDocExt = "beunknown";
    public static final String kContentSubtitleExt = "srt";
    public static final String kContentCoverExt = "jpg";
    public static final String kContentDicPatchExt = "zip";
    public static final String kContentRelativPatcheExt = "zip";
    public static final String kContentUnkownExt = "beunknown";

    public static final String KGiftCountNow = "giftCountNow";

    public static final String KPNGType = "png";
    public static final String KJPGType = "jpg";

    public static final String KBaseDatdbaseFilename = "mydic.db";
    public static final String KUserDatdbaseFilename = "myuser.db";

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

    //课程相关资源
    public static final String  kResource_Title            = "title";       //标题(字符串)
    public static final String  kResource_Sub              = "cap";         //字幕url
    public static final String  kResource_Cover            = "img";         //封面url
    public static final String  kResource_Vedio            = "vio";         //视频url
    public static final String  kResource_description      = "des";         //描述(字符串)
    public static final String  kResource_Duration         = "dur";         //时长(字符串)
    public static final String  kResource_Pro              = "pro";         //语音字典(字符串)
    public static final String  kResource_Background       = "bmu_doc_url"; //相关音乐
    public static final String  kResource_Keypoint         = "sp_desc";     //相关重点
    public static final String  kResource_KeyWord          = "sp_word";     //相关单词

    public static final String  kResource_Background_filenmae   = "background.mp3";

    //字典相关
    public static final String  kShareBaseTempFile         = "tempdic.zip";
    public static final String  KBaseDicAllType            = "dic800_all_n";
    public static final String  KBaseDicMp3Type            = "dic800_mp3";
    public static final String  KBaseDicDefineType         = "dic800_define_n";

    public static final String  KLessonDicXMLFile          = "dic_mend_n.xml";

    //下载相关
    public static final int NSOffState = 0;
    public static final int NSOnState = 1;
    public static final int NSMixedState = 2;

    public static final String KDownloadTypeNormal = "mp4";
    public static final String KDownloadTypeM3U8 = "m3u8";
    public static final String KDownloadTypeMagnet = "magnet";


    public static final String KDownloadTypeForK12 = "no_magnet";

    public static final String KlessonStateChange = "KlessonStateChange";
    public static final int maxDownloadLessonThread = 2;

    public static final String KAppInstallFile = "temp.apk";

    //辅助参数
    public static final String KLessonID = "KLessonID";
    public static final String KLessonTitle = "KLessonTitle";

    public static final String KIntenCorParameter = "KIntenCorParameter";

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
    public final static String KIMAPPKEY = "KEFU1408935920969";

    public final static String KIMNIKENAME = "imNikeName";
    public final static String KNIKENAMEDEFAULT = "我的昵称";

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
    public static final String RONG_DEFAULT_TOKEN = "default";
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
                FlyingDataManager.getServerNetAddress() +
                "/pa_get_o_charge_from_tn.action";

        return result;
    }

    public static String getLessonAccount(
            String contentType,
            String downloadType) {

        if (contentType == null) contentType = "";
        if (downloadType == null) downloadType = "";

        String result = "http://" +
                FlyingDataManager.getServerNetAddress() +
                "/la_get_ln_list_for_hp.action?vc=3" +
                "&url_2_type=" +
                downloadType +
                "&res_type=" +
                contentType +
                "&ln_owner=" +
                FlyingDataManager.getLessonOwner() +
                "&type=rc";

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
                FlyingDataManager.getServerNetAddress() +
                "/la_get_tag_string_for_hp.action?vc=3&perPageCount=" +
                count +
                "&page=" +
                1 +
                "&ln_tag=" +
                tag +
                "&ln_owner=" +
                FlyingDataManager.getLessonOwner();
    }

    public static String getAppBroadURL() {
        return "http://" +
                FlyingDataManager.getServerNetAddress() +
                "/aa_get_app_info_from_hp.action?app_id=" +
                FlyingDataManager.getLocalAppID() +
                "&type=img1";
    }

    public static String getAppWebURL() {
        return "http://" +
                FlyingDataManager.getServerNetAddress() +
                "/aa_get_app_info_from_hp.action?app_id=" +
                FlyingDataManager.getLocalAppID() +
                "&type=page";
    }

    public static String getChatURL(String lessonID) {
        return "http://" +
                FlyingDataManager.getServerNetAddress() +
                "/mwc.screen?ln_id=" +
                lessonID;
    }

    public static String getRefreshUseInfoURL(String passport, String name, String portraitURL) {
        if (passport != null) {
            String result = "http://" +
                    FlyingDataManager.getServerNetAddress() +
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
                FlyingDataManager.getServerNetAddress() +
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

    public static String bytesToString(byte[] _bytes) {
        String file_string = "";

        for (int i = 0; i < _bytes.length; i++) {
            file_string += (char) _bytes[i];
        }

        return file_string;
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

        if(sPath==null)
        {
            return false;
        }
        else
        {
            return sPath.contains("m3u8");
        }
    }

    public static boolean checkMp4URL(String sPath) {

        if(sPath==null)
        {
            return false;
        }
        else
        {
            return sPath.contains(".mp4");
        }
    }

    public static boolean checkURL(String string) {

	    if(string!=null && string.length()>3)
	    {
		    final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

		    Pattern p = Pattern.compile(URL_REGEX);
		    Matcher m = p.matcher("birdcopy.com");//replace with string to compare
		    if (m.find()) {

			    return true;
		    } else {
			    return false;
		    }
	    }
	    else
	    {
		    return  false;
	    }
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

    public static String getKUSERDATA_CHNAGE_RECEIVER_ACTION() {
        return MyApplication.getInstance().getPackageName() + ShareDefine.KMessagerUserdata;
    }

    public static String getpingplusURL(String currentPassport) {
        return "http://" +
                FlyingDataManager.getServerNetAddress() +
                "/ua_get_user_info_from_hp.action?tuser_key=" +
                currentPassport +
                "&app_id=" +
                FlyingDataManager.getLocalAppID() +
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
