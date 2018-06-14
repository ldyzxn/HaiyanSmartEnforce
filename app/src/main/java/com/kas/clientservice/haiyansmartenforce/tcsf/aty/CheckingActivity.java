package com.kas.clientservice.haiyansmartenforce.tcsf.aty;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.kas.clientservice.haiyansmartenforce.R;
import com.kas.clientservice.haiyansmartenforce.tcsf.base.HTTP_HOST;
import com.kas.clientservice.haiyansmartenforce.tcsf.base.NetResultBean;
import com.kas.clientservice.haiyansmartenforce.tcsf.base.TitleActivity;
import com.kas.clientservice.haiyansmartenforce.tcsf.bean.CheckFeeBean;
import com.kas.clientservice.haiyansmartenforce.tcsf.intf.BeanCallBack;
import com.kas.clientservice.haiyansmartenforce.tcsf.util.DateUtil;
import com.kas.clientservice.haiyansmartenforce.tcsf.util.TimePickerUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.feezu.liuli.timeselector.Utils.TextUtil;

public class CheckingActivity extends TitleActivity implements View.OnClickListener {

    private TextView tev_startTime, tev_endTime, tev_query;

    private LinearLayout llt_detail;
    private TextView tev_total, tev_money, tev_weixin, tev_count;

    private TimePickerView timePickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tev_startTime:
                String startTime = getText(tev_startTime);
                if (TextUtil.isEmpty(startTime)) {
                    timePickerView.setDate(DateUtil.str2Calendar(DateUtil.currentTime(), DateUtil.YMDHM));
                } else {
                    timePickerView.setDate(DateUtil.str2Calendar(startTime, DateUtil.YMDHM));
                }
                timePickerView.show(tev_startTime);
                break;
            case R.id.tev_endTime:
                String endTime = getText(tev_endTime);
                if (TextUtil.isEmpty(endTime)) {
                    timePickerView.setDate(DateUtil.str2Calendar(DateUtil.currentTime(), DateUtil.YMDHM));
                } else {
                    timePickerView.setDate(DateUtil.str2Calendar(endTime, DateUtil.YMDHM));
                }
                timePickerView.show(tev_endTime);
                break;
            case R.id.tev_query:
                doQuery();
                break;

        }
    }


    private void doQuery() {
        String startTime = getText(tev_startTime);
        String endTime = getText(tev_endTime);

        if (TextUtil.isEmpty(startTime) || TextUtil.isEmpty(endTime)) {
            warningShow("查询开始时间和结束时间不能为空");
            return;
        }
        OkHttpUtils.post().url(HTTP_HOST.URL_RECONCILIATIONS)
                .addParams("Opername", getOpername())
                .addParams("Starttime1", startTime)
                .addParams("Starttime2", endTime)
                .build().execute(new BeanCallBack(aty, "查询中") {
            @Override
            public void handleBeanResult(NetResultBean bean) {
                if (bean.State && bean.total == 1) {
                    llt_detail.setVisibility(View.VISIBLE);
                    CheckFeeBean feeBean = bean.getResultBean(CheckFeeBean.class);
                    tev_total.setText(feeBean.sfze);
                    tev_money.setText(feeBean.xzze);
                    tev_weixin.setText(feeBean.wxze);
                    tev_count.setText(feeBean.zfbs);

                } else {
                    warningShow(bean.ErrorMsg);
                    llt_detail.setVisibility(View.GONE);
                }
            }
        });
    }


    @Override
    protected void findViewBy() {
        tev_startTime = (TextView) findViewById(R.id.tev_startTime);
        tev_endTime = (TextView) findViewById(R.id.tev_endTime);
        tev_query = (TextView) findViewById(R.id.tev_query);
        llt_detail = (LinearLayout) findViewById(R.id.llt_detail);
        tev_total = (TextView) findViewById(R.id.tev_total);
        tev_money = (TextView) findViewById(R.id.tev_money);
        tev_weixin = (TextView) findViewById(R.id.tev_weixin);
        tev_count = (TextView) findViewById(R.id.tev_count);

    }

    @Override
    protected void setTitleTxt() {
        tv_header_title.setText("收费对账");
    }

    @Override
    protected void initData() {
        timePickerView = TimePickerUtil.getYMDHMTimePicker(aty);
        tev_startTime.setOnClickListener(this);
        tev_endTime.setOnClickListener(this);
        tev_query.setOnClickListener(this);

    }
}