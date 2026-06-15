package com.zilong.dubao;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;

public class DuMaActivity extends AppCompatActivity {
    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_du_ma);
        LinearLayout container = findViewById(R.id.web_container);

        // 2. 构建 AgentWeb 实例
        mAgentWeb = AgentWeb.with(this) // 传入 Activity
                .setAgentWebParent(container, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)) // 设置父控件和布局参数
                .useDefaultIndicator() // 使用默认进度条
                .createAgentWeb() // 创建 AgentWeb 对象
                .ready() // 准备就绪
//                .go("http://office.angerdream21.top:8080/");
                .go("file:///android_asset/index.html");
        // 注意：如果 HTML 中使用了 JavaScript，需要手动启用
        if (mAgentWeb != null && mAgentWeb.getAgentWebSettings() != null) {
            mAgentWeb.getAgentWebSettings().getWebSettings().setJavaScriptEnabled(true);
        }
        WebView webView = mAgentWeb.getWebCreator().getWebView();
        WebSettings settings = webView.getSettings();
        settings.setAllowFileAccess(true);           // 允许访问文件
        settings.setAllowFileAccessFromFileURLs(true);  // 允许 file:// 下的跨文件访问
        settings.setAllowUniversalAccessFromFileURLs(true); // 允许所有 file:// 下的跨文件访问
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
    }
}