package com.example.project3;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.project3.services.DownloadFileService;
import com.example.project3.tasks.FetchFileInfoTask;

public class MainActivity extends AppCompatActivity {
    TextView urlInput;
    TextView downloadedSize;
    TextView fileSize;
    TextView fileType;
    ProgressBar progressBar;
    Button fetchFileInfoButton;
    Button downloadFileButton;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            int downloaded = bundle.getInt("downloaded");
            int total = bundle.getInt("total");
            String msg = downloaded + " / " + total;
            downloadedSize.setText(msg);
            progressBar.setMax(total);
            progressBar.setProgress(downloaded);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlInput = findViewById(R.id.url_input);
        fileSize = findViewById(R.id.file_size_value);
        fileType = findViewById(R.id.file_type_value);
        fetchFileInfoButton = findViewById(R.id.fetch_file_info_button);
        downloadFileButton = findViewById(R.id.download_file_button);
        downloadedSize = findViewById(R.id.downloaded_size_value);
        progressBar = findViewById(R.id.download_progress_bar);

        fetchFileInfoButton.setOnClickListener(view -> {
            String url = urlInput.getText().toString();
            if (url.isEmpty()) {
                urlInput.setError("Please enter a URL");
                return;
            }

            new FetchFileInfoTask(this).execute(url);
        });

        downloadFileButton.setOnClickListener(view -> {
            String url = urlInput.getText().toString();
            if (url.isEmpty()) {
                urlInput.setError("Please enter a URL");
                return;
            }

            if (ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
            }

            DownloadFileService.startFileDownload(this, url);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(DownloadFileService.NOTIFICATION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", urlInput.getText().toString());
        outState.putString("fileSize", fileSize.getText().toString());
        outState.putString("fileType", fileType.getText().toString());
        outState.putString("bytesDownloaded", downloadedSize.getText().toString());
        outState.putInt("progressMax", progressBar.getMax());
        outState.putInt("progress", progressBar.getProgress());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        urlInput.setText(savedInstanceState.getString("url"));
        fileSize.setText(savedInstanceState.getString("fileSize"));
        fileType.setText(savedInstanceState.getString("fileType"));
        downloadedSize.setText(savedInstanceState.getString("bytesDownloaded"));
        progressBar.setMax(savedInstanceState.getInt("progressMax"));
        progressBar.setProgress(savedInstanceState.getInt("progress"));
    }
}