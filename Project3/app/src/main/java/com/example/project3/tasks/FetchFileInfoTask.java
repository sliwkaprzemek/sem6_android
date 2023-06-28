package com.example.project3.tasks;

import android.os.AsyncTask;
import android.widget.TextView;

import com.example.project3.MainActivity;
import com.example.project3.R;
import com.example.project3.dto.FileInfo;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchFileInfoTask extends AsyncTask<String, Void, FileInfo> {
    private final WeakReference<MainActivity> mainActivityReference;

    public FetchFileInfoTask(MainActivity activity) {
        mainActivityReference = new WeakReference<>(activity);
    }

    @Override
    protected FileInfo doInBackground(String... strings) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(strings[0]).openConnection();
            int contentLength = connection.getContentLength();
            String contentType = connection.getContentType();
            return new FileInfo(contentLength, contentType);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(FileInfo fileInfo) {
        super.onPostExecute(fileInfo);

        TextView fileSizeValueTextView = mainActivityReference.get().findViewById(R.id.file_size_value);
        TextView fileTypeValueTextView = mainActivityReference.get().findViewById(R.id.file_type_value);

        fileSizeValueTextView.setText(String.valueOf(fileInfo.getSize()));
        fileTypeValueTextView.setText(fileInfo.getType());
    }
}
