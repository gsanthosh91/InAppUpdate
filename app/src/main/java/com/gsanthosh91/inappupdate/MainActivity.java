package com.gsanthosh91.inappupdate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;

import java.io.File;
import java.util.Locale;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class MainActivity extends AppCompatActivity {

    TextView content;
    ProgressBar progressBar;
    String TAG = "PRDOWNLOAD";
    String url = "https://raw.githubusercontent.com/xmuSistone/VegaLayoutManager/master/app-debug.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        content = findViewById(R.id.content);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int downloadId = downloadFile(url);
            }
        });
    }


    private int downloadFile(String url) {

        final String fileName = url.substring(url.lastIndexOf('/') + 1);
        File imagePath = new File(getFilesDir(), "Download");
        final String dirPath = imagePath.getPath();


        int downloadId = PRDownloader.download(url, dirPath, fileName)
                .build()

                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                        progressBar.setProgress((int) progressPercent);

                        content.setText(getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                        progressBar.setIndeterminate(false);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.d(TAG, "onDownloadComplete: ");
                        initInstall(fileName);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(getApplicationContext(), "SOMETHING ERROR OCCURED", Toast.LENGTH_SHORT).show();
                    }
                });

        return downloadId;
    }

    private void initInstall(String fileName) {

        File filePath = new File(getFilesDir(), "Download");
        File newFile = new File(filePath, fileName);

        Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.gsanthosh91.fileprovider", newFile);

        Log.d(TAG, "initInstall: " + contentUri);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        startActivity(intent);


    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }
}
