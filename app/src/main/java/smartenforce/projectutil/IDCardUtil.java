package smartenforce.projectutil;


import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Request;
import smartenforce.impl.MyStringCallBack;
import smartenforce.widget.ProgressDialogUtil;


public class IDCardUtil {
    private static final String grant_type="client_credentials";
    private static final String API_Key="YjcPlmgbEiyO3GLR5ci9mSEh";
    private static final String Secret_Key="5ine6XQUgYfmZxYczcpSvoeR0WZFOGdr";
    private static final String Token_Url="https://aip.baidubce.com/oauth/2.0/token";
    private static final String IdCard_Url="https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";

    private  String accessToken=null;

    private static  IDCardUtil util=null;


    public interface onIdCardCallBack {
        void onSuccess(String name, String idCardNum, String sex, String nation, String address);

        void onErroMsg(String msg);
    }

    public static IDCardUtil getInstance(){
        if (util==null){
            synchronized (IDCardUtil.class){
                if (util==null){
                     util=new IDCardUtil();
                    }
                }

            }
        return util;
    }

    private IDCardUtil(){}


   //获取token,有效期一般默认为一个月，暂不缓存，在应用初始化的 时候调用一次
    public void getAuthToken(){
        OkHttpUtils.post().url(Token_Url)
                .addParams("grant_type", grant_type)
                .addParams("client_id", API_Key)
                .addParams("client_secret", Secret_Key)
                .build().execute(new MyStringCallBack(){

            @Override
            public void onError(Call call, Exception e, int id) {
                  super.onError(call, e, id);
                   accessToken=null;
            }

            @Override
            public void onResponse(String response, int id) {
                  super.onResponse(response, id);
                    try {
                        JSONObject jo = new JSONObject(response);
                        accessToken=jo.getString("access_token");
                    } catch (Exception e) {
                        accessToken=null;
                    }
            }
        });

    }

    public  void getIdCardInfo(final Activity aty, String base64IdCard, final onIdCardCallBack callBack) {
        OkHttpUtils.post().url(IdCard_Url)
                .addParams("access_token", accessToken)
                .addParams("id_card_side", "front")
                .addParams("image", base64IdCard)
                .build().execute(new MyStringCallBack(){
            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);
                ProgressDialogUtil.show(aty,"解析中...");
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                callBack.onErroMsg("身份证解析失败");
                ProgressDialogUtil.hide();
            }

            @Override
            public void onResponse(String response, int id) {
                 super.onResponse(response, id);
                 CardBean cardBean= JSON.parseObject(response,CardBean.class);
                if (cardBean.error_code!=0){
                    callBack.onErroMsg(cardBean.error_msg);
                }else{
                    try{
                    String name=cardBean.words_result.getName();
                    String idCardNum=cardBean.words_result.getNumber();
                    String sex=cardBean.words_result.getSex();
                    String nation=cardBean.words_result.getNation();
                    String address=cardBean.words_result.getAddress();
                    callBack.onSuccess(name,idCardNum,sex,nation,address);
                    }catch (Exception e){
                        callBack.onErroMsg("身份证解析失败");
                    }
                }
                ProgressDialogUtil.hide();
            }
        });
    }
}