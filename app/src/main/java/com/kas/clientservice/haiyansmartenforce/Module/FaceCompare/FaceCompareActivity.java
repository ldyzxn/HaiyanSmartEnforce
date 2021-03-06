package com.kas.clientservice.haiyansmartenforce.Module.FaceCompare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.kas.clientservice.haiyansmartenforce.Base.BaseActivity;
import com.kas.clientservice.haiyansmartenforce.Entity.FaceAddEntity;
import com.kas.clientservice.haiyansmartenforce.Entity.FaceCompareEntity;
import com.kas.clientservice.haiyansmartenforce.Entity.FaceDetectEntity;
import com.kas.clientservice.haiyansmartenforce.Entity.FaceSearchEntity;
import com.kas.clientservice.haiyansmartenforce.Entity.IdentityCardScanEntity;
import com.kas.clientservice.haiyansmartenforce.Module.IllegalParking.IllegalParkingCommitImgRvAdapter;
import com.kas.clientservice.haiyansmartenforce.R;
import com.kas.clientservice.haiyansmartenforce.Utils.BitmapToBase64;
import com.kas.clientservice.haiyansmartenforce.Utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

import static com.kas.clientservice.haiyansmartenforce.Utils.Utils.getImageCropUri;

public class FaceCompareActivity extends BaseActivity implements View.OnClickListener, TakePhoto.TakeResultListener {
    @BindView(R.id.iv_heaer_back)
    ImageView iv_back;
    @BindView(R.id.iv_faceCompare_face1)
    ImageView iv_face1;
    @BindView(R.id.iv_faceCompare_face2)
    ImageView iv_face2;
    @BindView(R.id.tv_faceCompare_rate)
    TextView tv_rate;
    @BindView(R.id.tv_faceCompare_btn)
    TextView tv_btn;
    @BindView(R.id.iv_faceCompare_identity)
    ImageView iv_identity;
    @BindView(R.id.tv_faceCompare_idInfo)
    TextView tv_idInfo;
    @BindView(R.id.tv_faceCompare_idScan)
    TextView tv_idScan;
    @BindView(R.id.tv_faceCompare_add)
    TextView tv_add;
    @BindView(R.id.tv_faceCompare_search)
    TextView tv_search;
    @BindView(R.id.iv_faceCompare_merge)
    ImageView iv_merge;
    @BindView(R.id.tv_faceCompare_margeInfo)
    TextView tv_mergeInfo;

    TakePhoto takePhoto;
    List<Bitmap> arr_image;
    List<String> arr_uri = new ArrayList<>();
    IllegalParkingCommitImgRvAdapter adapter;
    private CompressConfig compressConfig;
    Uri uri;
    int flag;
    Bitmap bitmap1, bitmap2, bitmap_id, bitmap_search;
    String key = "sKj1t5aXkYE3YxuW7C1zHOGedavfYcuy";
    String secret = "L30fJ2ZlJ3n4RVuBGKNPrNdJtEotIzfC";
    String faceset_token = "f303a4c5d30558e28befe8924750ff71";
    String faceToken = "";

    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = new TakePhotoImpl(this, this);
        }
        return takePhoto;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
//        Log.i(TAG, "onActivityResult: "+data.toString());
        Log.i(TAG, "onActivityResult: " + requestCode + "  " + resultCode);
//        if (requestCode == Constants.RESULTCODE_TIANDITU) {
//            longitude = data.getStringExtra("Longitude");
//            latitude = data.getStringExtra("Latitude");
//            Log.i(TAG, "onActivityResult: "+longitude+"  "+latitude);
//        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_compare;
    }

    @Override
    protected String getTAG() {
        return this.toString();
    }

    @Override
    protected void initResAndListener() {
        super.initResAndListener();
        iv_back.setOnClickListener(this);
        iv_face1.setOnClickListener(this);
        iv_face2.setOnClickListener(this);
        tv_btn.setOnClickListener(this);
        iv_identity.setOnClickListener(this);
        tv_idScan.setOnClickListener(this);
        tv_add.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        iv_merge.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_faceCompare_face1:
                flag = 1;
                takePhoto();
                break;
            case R.id.iv_faceCompare_face2:
                flag = 2;
                takePhoto();
                break;
            case R.id.iv_heaer_back:
                finish();
                break;
            case R.id.tv_faceCompare_btn:
                if (bitmap1 != null && bitmap2 != null) {
                    compare();
                } else {
                    ToastUtils.showToast(mContext, "拍摄照片！");
                }
                break;
            case R.id.iv_faceCompare_identity:
                flag = 3;
                takePhoto();
                break;
            case R.id.tv_faceCompare_idScan:
                if (bitmap_id != null) {
                    scan();
                }
                break;
            case R.id.tv_faceCompare_add:
                if (bitmap_search != null) {
                    if (faceToken.equals("")) {
                        detect();
                    } else {
                        add(faceToken);
                    }
                }
//                add();
                break;
            case R.id.tv_faceCompare_search:
                if (bitmap_search != null) {
                    search();
                }
                break;
            case R.id.iv_faceCompare_merge:
                flag = 4;
                takePhoto();
                break;
        }
    }

    private void detect() {
        showLoadingDialog();
        OkHttpUtils.post().url("https://api-cn.faceplusplus.com/facepp/v3/detect")
                .addParams("api_key", key)
                .addParams("api_secret", secret)
                .addParams("image_base64", BitmapToBase64.bitmapToBase64(bitmap_search))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onError: " + e.toString());
                showNetErrorToast();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onResponse: " + response);
                FaceDetectEntity faceDetectEntity = gson.fromJson(response, FaceDetectEntity.class);
                if (faceDetectEntity.getFaces() != null && faceDetectEntity.getFaces().size() > 0) {
                    String token = faceDetectEntity.getFaces().get(0).getFace_token();
                    add(token);
                } else {
                    ToastUtils.showToast(mContext, "未检测到人脸");
                }
            }
        });
    }

    private void search() {
        showLoadingDialog();
        OkHttpUtils.post().url("https://api-cn.faceplusplus.com/facepp/v3/search")
                .addParams("api_key", key)
                .addParams("api_secret", secret)
                .addParams("image_base64", BitmapToBase64.bitmapToBase64(bitmap_search))
                .addParams("faceset_token", faceset_token)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onError: " + e.toString());
                showNetErrorToast();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onResponse: " + response);
                FaceSearchEntity faceSearchEntity = gson.fromJson(response, FaceSearchEntity.class);
                if (faceSearchEntity.getResults() != null && faceSearchEntity.getResults().size() > 0) {
                    FaceSearchEntity.ResultsBean resultsBean = faceSearchEntity.getResults().get(0);
                    double confidence = resultsBean.getConfidence();
                    String token = resultsBean.getFace_token();
                    tv_mergeInfo.setText("face_token:"+token+"\n"+"匹配度："+confidence);
                } else {
                    ToastUtils.showToast(mContext, "未得到匹配数据");
                }
            }
        });
    }

    private void add(String token) {
        showLoadingDialog();
        OkHttpUtils.post().url("https://api-cn.faceplusplus.com/facepp/v3/faceset/addface")
                .addParams("api_key", key)
                .addParams("api_secret", secret)
                .addParams("faceset_token", faceset_token)
                .addParams("face_tokens", token)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onError: " + e.toString());
                showNetErrorToast();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onResponse: " + response);
                FaceAddEntity faceAddEntity = gson.fromJson(response, FaceAddEntity.class);
                if (faceAddEntity.getFace_added() > 0) {
                    ToastUtils.showToast(mContext, "成功录入" + faceAddEntity.getFace_added() + "张照片，共" + faceAddEntity.getFace_count() + "张照片");
                    faceToken = "";
                } else {
                    ToastUtils.showToast(mContext, "录入失败");
                }
            }
        });
    }

    private void scan() {
        showLoadingDialog();
        OkHttpUtils.post().url("https://api-cn.faceplusplus.com/cardpp/v1/ocridcard")
                .addParams("api_key", key)
                .addParams("api_secret", secret)
                .addParams("image_base64", BitmapToBase64.bitmapToBase64(bitmap_id))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onError: " + e.toString());
                showNetErrorToast();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onResponse: " + response);
                IdentityCardScanEntity identityCardScanEntity = gson.fromJson(response, IdentityCardScanEntity.class);
                if (identityCardScanEntity.getCards() != null && identityCardScanEntity.getCards().size() > 0) {
                    IdentityCardScanEntity.CardsBean cardsBean = identityCardScanEntity.getCards().get(0);
                    if (cardsBean.getSide().equals("front")) {
                        tv_idInfo.setText(cardsBean.getName() + "\n" + cardsBean.getBirthday() + "\n" + cardsBean.getId_card_number() + "\n" + cardsBean.getAddress());
                    } else {
                        tv_idInfo.setText(cardsBean.getIssued_by() + "\n" + cardsBean.getValid_date());
                    }
                } else {
                    ToastUtils.showToast(mContext, "未识别到证件");
                }
            }
        });
    }

    private void compare() {
        showLoadingDialog();
        OkHttpUtils.post().url("https://api-cn.faceplusplus.com/facepp/v3/compare")
                .addParams("api_key", key)
                .addParams("api_secret", secret)
                .addParams("image_base64_1", BitmapToBase64.bitmapToBase64(bitmap1))
                .addParams("image_base64_2", BitmapToBase64.bitmapToBase64(bitmap2))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onError: " + e.toString());
                showNetErrorToast();
            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoadingDialog();
                Log.i(TAG, "onResponse: " + response);
                FaceCompareEntity faceCompareEntity = gson.fromJson(response, FaceCompareEntity.class);
                if (faceCompareEntity.confidence != 0) {
                    tv_rate.setText(faceCompareEntity.confidence + "");
                } else {
                    ToastUtils.showToast(mContext, "未检测到人脸");
                }
            }
        });
    }

    public void takePhoto() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    100);
        } else {
            //查看图片权限
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        101);
            } else {
                uri = getImageCropUri();
//                cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();
                //设置压缩参数
                compressConfig = new CompressConfig.Builder().setMaxSize(150 * 1024).setMaxPixel(2000).create();
                takePhoto.onEnableCompress(compressConfig, true); //设置为需要压缩
                takePhoto.onPickFromCapture(uri);
            }
        }
    }

    @Override
    public void takeSuccess(String imagePath) {
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        if (flag == 1) {
            bitmap1 = bmp;
            iv_face1.setImageBitmap(bmp);
        } else if (flag == 2) {
            bitmap2 = bmp;
            iv_face2.setImageBitmap(bmp);
        } else if (flag == 3) {
            bitmap_id = bmp;
            iv_identity.setImageBitmap(bmp);
        } else if (flag == 4) {
            bitmap_search = bmp;
            iv_merge.setImageBitmap(bmp);
        }
    }

    @Override
    public void takeFail(String msg) {

    }

    @Override
    public void takeCancel() {

    }
}
