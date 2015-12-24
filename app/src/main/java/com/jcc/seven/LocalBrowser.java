package com.jcc.seven;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class LocalBrowser extends AppCompatActivity {
    private static final String TAG = "LocalBrowser";
    private final Handler handler = new Handler();
    private WebView webView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_browser);

        webView = (WebView)findViewById(R.id.web_view_1);
        textView = (TextView)findViewById(R.id.text_view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "android");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                Log.d(TAG, "onJsAlert(" + view + ", " + url + ", " + message + ", " + result + ")");
                Toast.makeText(LocalBrowser.this, message, 3000).show();
                result.confirm();
                return true;
            }
        });

        webView.loadUrl("file:///android_asset/index.html");
    }

    public void button1Click(View view) {
        Log.d(TAG, "onClick(" + view + ")");
        webView.loadUrl("javascript:callJS('Hello from Android')");
    }

    private class AndroidBridge{
        @JavascriptInterface
        public void callAndroid(final String arg){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "callAndroid(" + arg + ")");
                    textView.setText(arg);
                }
            });
        }
    }
}
