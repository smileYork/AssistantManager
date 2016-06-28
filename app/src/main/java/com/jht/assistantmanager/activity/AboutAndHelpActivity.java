package com.jht.assistantmanager.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.jht.assistantmanager.R;
import com.jht.assistantmanager.adapter.LoadingDialog;
import com.jht.assistantmanager.util.GlobalData;
import com.jht.assistantmanager.util.NetWorkJudgeUtil;
import com.jht.assistantmanager.util.ToastShow;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams")
public class AboutAndHelpActivity extends Activity {

    private ImageButton img_bt_return;

    private RelativeLayout rlyout_update;

    private RelativeLayout rlyout_help;

    private RelativeLayout rlyout_introduce;

    private TextView tv_update_time;

    private Dialog dialog;

    private Intent intent;

    private String localVersion;

    private String netVersion;

    private String updateUrl;

    private String apkName;

    private String path;

    private int apkSize = 1;

    private ProgressBar progressBar;

    private boolean cancelUpdate = false;

    // 更新进度条的对话框
    private Dialog mDownloadDialog;

    private TextView tv_progress;

    private long nextStep = 0;

    private String getApkSizeUrl;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_about_and_help);

        setTitleStyle();

        findView();

        initView();

        setListen();

    }

    private void setTitleStyle() {

        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }

    }

    private void setListen() {

        img_bt_return.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rlyout_update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {

                    dialog = LoadingDialog.createLoadingDialog(AboutAndHelpActivity.this, "正在检查版本,请稍等...");

                    dialog.show();

                    localVersion = getLocalVersionName();

                    updateUrl = getResources().getString(R.string.url_server);

                    checkVersion(updateUrl);

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }

        });

        rlyout_help.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                intent.setClass(AboutAndHelpActivity.this, WebViewShowActivity.class);

                intent.putExtra("url", "http://www.2wma.cn/");

                intent.putExtra("title", "帮助");

                startActivity(intent);

            }
        });

        rlyout_introduce.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                intent.setClass(AboutAndHelpActivity.this, WebViewShowActivity.class);

                intent.putExtra("url", "http://www.2wma.cn/");

                intent.putExtra("title", "功能介绍");

                startActivity(intent);

            }
        });
    }

    private void initView() {

        intent = new Intent();

        preferences = getSharedPreferences(GlobalData.SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String date = sDateFormat.format(new Date());

        tv_update_time.setText(preferences.getString("APKUpdateTime", date));

    }

    private void findView() {

        img_bt_return = (ImageButton) findViewById(R.id.img_bt_about_and_help_return);

        rlyout_update = (RelativeLayout) findViewById(R.id.rl_about_and_help_version_update);

        rlyout_help = (RelativeLayout) findViewById(R.id.rl_about_and_help_help);

        rlyout_introduce = (RelativeLayout) findViewById(R.id.rl_about_and_help_introduce_);

        tv_update_time = (TextView) findViewById(R.id.tv_about_and_help_update_time);

    }

    private String getLocalVersionName() throws Exception {

        // getPackageName()是你当前类的包名，0代表是获取版本信息

        PackageManager packageManager = getPackageManager();

        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);

        return packInfo.versionName;
    }

    // 检查输入的服务器地址是否有用
    public void checkVersion(String checkUrl) {
        try {

            if (NetWorkJudgeUtil.isConnect(AboutAndHelpActivity.this)) {

                GlobalData.getWithAbstrctAddress(checkUrl, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] arg2) {
                        try {

                            dialog.dismiss();

                            String result = new String(arg2);

                            getSettingInfo(result);

                            if (netVersion.equals(localVersion)) {

                                Toast.makeText(AboutAndHelpActivity.this, "已经是最新版本!", Toast.LENGTH_SHORT).show();

                            } else {

                                Builder dialog = new Builder(AboutAndHelpActivity.this);

                                dialog.setTitle("更新提示").setMessage("检测到新版本，是否更新?")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                showDownloadDialog();
                                            }
                                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create().show();

                            }

                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }

                    }


                    @Override
                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                        dialog.dismiss();

                        Toast.makeText(AboutAndHelpActivity.this, "检查失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    }


                });

            } else {

            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    protected void getSettingInfo(String result) throws DocumentException {

        Document document = DocumentHelper.parseText(result);

        Element root = document.getRootElement();

        Element element = root.element("version");

        netVersion = element.getText();

        element = root.element("download_url");

        updateUrl = element.getText();

        element = root.element("apkname");

        apkName = element.getText();

        element = root.element("getApkSize_url");

        getApkSizeUrl = element.getText();
    }

    private void downLoadApk() {

        GlobalData.getWithAbstrctAddress(updateUrl, new AsyncHttpResponseHandler() {

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

                super.onProgress(bytesWritten, totalSize);

                float temp = bytesWritten;

                float rate = temp / progressBar.getMax();

                tv_progress.setText((int) (rate * 100) + "%");

                progressBar.incrementProgressBy((int) (bytesWritten - nextStep));

                nextStep = bytesWritten;

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] arg2) {
                path = Environment.getExternalStorageDirectory() + "/ApkDownLoad";

                File file = new File(path);

                apkSize = arg2.length;

                // 如果文件不存在，新建目录
                if (!file.exists()) {
                    file.mkdir();
                }

                File apkFile = new File(path, apkName);

                try {

                    FileOutputStream oStream = new FileOutputStream(apkFile);

                    oStream.write(arg2);

                    oStream.flush();

                    oStream.close();

                } catch (Exception e) {

                    e.printStackTrace();

                }

                mDownloadDialog.dismiss();

                installApk();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                ToastShow.ToastShowLong(AboutAndHelpActivity.this, "下载失败,请稍后再试！");
            }

        });

    }

    private void showDownloadDialog() {

        // 获取APK的大小，供进度条使用
        if (NetWorkJudgeUtil.isConnect(AboutAndHelpActivity.this)) {

            GlobalData.getWithAbstrctAddress(getApkSizeUrl, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                    String result = new String(responseBody);

                    apkSize = Integer.parseInt(result);

                    openDownLoadDialog();
                }

                @Override
                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(AboutAndHelpActivity.this, "检查失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }

            });

        }
    }

    private void openDownLoadDialog() {

        Builder builder = new Builder(AboutAndHelpActivity.this);

        builder.setTitle("更新进度");

        final LayoutInflater inflater = LayoutInflater.from(AboutAndHelpActivity.this);

        View view = inflater.inflate(R.layout.layout_dailog_processbar, null);

        progressBar = (ProgressBar) view.findViewById(R.id.pb_dailog);

        tv_progress = (TextView) view.findViewById(R.id.tv_progress);

        progressBar.setMax(apkSize);

        builder.setView(view);

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogForInterface, int which) {
                dialogForInterface.dismiss();
                cancelUpdate = true;
            }
        });

        mDownloadDialog = builder.create();

        mDownloadDialog.show();

        downLoadApk();
    }

    private void installApk() {

        if (!cancelUpdate) {

            File file = new File(path + "/" + apkName);

            if (!file.exists()) {

                return;

            }

            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String date = sDateFormat.format(new Date());

            SharedPreferences.Editor ed = preferences.edit();

            ed.putString("APKUpdateTime", date);

            ed.commit();

            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");

            startActivity(intent);
        }
    }

}
