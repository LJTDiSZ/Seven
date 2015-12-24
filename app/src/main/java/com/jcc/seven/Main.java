package com.jcc.seven;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

public class Main extends AppCompatActivity {
    private  EditText urlText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlText = (EditText)findViewById(R.id.url_field);
        urlText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER){
                    //openBrowser();
                    WebView webView = (WebView)findViewById(R.id.web_view);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadUrl(urlText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    public void buttonGoClick(View view) {
        openBrowser();
    }

    private void openBrowser(){
        Uri url = Uri.parse(urlText.getText().toString());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        startActivity(intent);
    }

    public void buttonLocalBrowserClick(View view) {
        Intent intent = new Intent(this, LocalBrowser.class);
        startActivity(intent);
    }

    public void buttonTranslateClick(View view) {
        Intent intent = new Intent(this, Translate.class);
        startActivity(intent);
    }

    public void buttonLocationClick(View view) {
        Intent intent = new Intent(this, Location.class);
        startActivity(intent);
    }

    public void buttonSqlClick(View view) {
        Intent intent = new Intent(this, SQL.class);
        startActivity(intent);
    }

    public void buttonMapClick(View view) {
    }
}
