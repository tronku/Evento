package com.example.tronku.eventmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.http.SslError;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterEventActivity extends AppCompatActivity {

    private String ShowOrHideWebViewInitialUse = "show";
    @BindView(R.id.website) WebView webview;
    @BindView(R.id.webLink) TextView webLink;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_event);
        ButterKnife.bind(this);

        String url = getIntent().getStringExtra("website");
        webLink.setText(url);

        webview.setWebViewClient(new SSLTolerentWebViewClient());
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.loadUrl(url);
    }

    private class SSLTolerentWebViewClient extends WebViewClient {
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (ShowOrHideWebViewInitialUse.equals("show")) {
                webview.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            ShowOrHideWebViewInitialUse = "hide";
            progressBar.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onBackPressed() {
        if(webview.canGoBack() && webview.isFocused()) {
            webview.goBack();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
