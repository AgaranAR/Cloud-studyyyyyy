package com.example.clo;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayFileActivity extends AppCompatActivity {

    private WebView displayWebView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_file);

        displayWebView = findViewById(R.id.displayWebView);
        displayWebView.setWebViewClient(new WebViewClient());

        // Enable JavaScript if necessary
        WebSettings webSettings = displayWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Get file URL and type from intent
        Intent intent = getIntent();
        String fileUrl = intent.getStringExtra("fileUrl");
        String fileType = intent.getStringExtra("fileType");

        if (fileType != null && fileType.equals("pdf")) {
            showPdf(fileUrl);
        } else {
            showImage(fileUrl);
        }
    }

    private void showPdf(String fileUrl) {
        // Load the PDF using Google Docs viewer
        displayWebView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + fileUrl);
    }

    private void showImage(String fileUrl) {
        // Directly load the image URL in the WebView
        displayWebView.loadUrl(fileUrl);
    }
}
