package com.kas.clientservice.haiyansmartenforce.Http;

public class RequestUrl {
    public static final int CAMERA = 1;
    public static final int TYPE = 2;
    public static final int MAP = 3;
    // public static final  String URL = "http://112.11.105.171:8092//Handler/CollecterApi.aspx";
    public static final String baseUrl_leader = "http://117.149.146.131:88/";
    public static final String baseUrl_credit = "http://117.149.146.131:6111/";
//    public static final String baseUrl_leader = "http://111.1.31.184:88/";
    public static final String baseUrl_img = baseUrl_leader+"use/";
    public static final String baseUrl = "http://117.149.146.131:86/handler/collecterapi.aspx/";
    public static final String URL = "http://112.13.194.180:82//Handler/CollecterApi.aspx";
    public static final String URLCollecter = "http://112.13.194.180:82/Handler/CollecterApi.aspx";
    public static final String IMGURL = "http://112.13.194.180:82/Handler/";


    /**
     * 登录
     */
    public static final String Login = "system/theme/anjuan/wzlogin.ashx";
    /**
     * 获取大类
     */
    public static final String getBig = "geteventpartbigclass";
    /**
     * 获取小类
     */
    public static final String getSmall = "geteventpartsmallclass";
    /**
     * 上传图片
     */
    public static final String getImgUrl = "uploadPic";
    /**
     * 问题上报
     */
    public static final String issueUploading = "pdainsertproject";
    /**
     * 修改密码
     */
    public static final String ModifyPassword = "pdachangepassword";
    /**
     * 案卷查询
     */
    public static final String CaseQuery = "getcprojectcountlist";
    /**
     * 个人任务
     */
    public static final String getTask = "pdagetpdamsg";
    /**
     * 监督员核查回复
     */
    public static final String Reply = "pdamodifypdamsg";
    /**
     * app更新
     */
    public static final String updateapp = "updateapp";
    /**
     * 上下班打卡
     */
    public static final String OnAndOffWork = "OnAndOffWork";
    /**
     * 轨迹
     */
    public static final String trackUpload = "trackUpload";

    /**
     * 协同列表
     */
    public static final String URLLIST="http://117.149.146.131:86/Handler/CoordinationApi.aspx";
    
    //查询列表
    public static final String haiyanlist="getList";
    //案卷详情
    public static final String GetProjectDetail="getlprojectdetail";
    //習慣用語
    public static final String GetWording="Getdiction";
    //反饋
    public static final String GetFeedBackOk="resfeedback";
    //回退
    public static final String GetBackSpace="returnBack";
 /*
 * 违停查询
 * */
    public static final String illegalParkingSearch = "system/theme/anjuan/WFCXHandler.ashx";
    /*
    * 路段查询*/

    public static final String roadSearch = "system/theme/anjuan/WFroad.ashx";
}
