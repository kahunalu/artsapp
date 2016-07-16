package com.uvic.arts;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.artoolkit.ar.base.assets.AssetHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initializeInstance();

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        } else {
            android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.hide();
            }
        }

        setContentView(R.layout.activity_main);
        Button scanButton = (Button) findViewById(R.id.scan_button);

        if (scanButton != null) {
            scanButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    new IntentIntegrator(MainActivity.this).initiateScan();
                    return false;
                }
            });
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        requestManager = new RequestManager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                new FetchContentTask().execute(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Here we do one-off initialisation which should apply to all activities
    // in the application.
    protected void initializeInstance() {

        // Unpack assets to cache directory so native library can read them.
        // N.B.: If contents of assets folder changes, be sure to increment the
        // versionCode integer in the AndroidManifest.xml file.
        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(this, "Data");
    }

    class FetchContentTask extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            progressDialog.setMessage("Retrieving content...");
            progressDialog.show();

            super.onPreExecute();
        }

        protected String doInBackground(String... contentIds) {
            try {
                // Make sure we wait at least one second before starting ARToolKit's camera
                // otherwise it will fail to initialize, since we were just using the camera
                // to read the QR code. Hacky, but it works.
                Thread.sleep(1000);
                String response = requestManager.getContent(contentIds[0]);

                JSONObject jsonObject;
                String size = null;

                try {
                    jsonObject = new JSONObject(response);

                    // Get image size
                    size = jsonObject.getString(ARTSConstants.CONTENT_SIZE_KEY);

                    // Save imageData to a file, since it can be too big to pass in an intent
                    String imageData = jsonObject.getString(ARTSConstants.CONTENT_DATA_KEY);
                    File file = new File(MainActivity.this.getFilesDir(), ARTSConstants.CONTENT_DATA_FILENAME);
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(imageData.getBytes());

                } catch (JSONException | IOException exception) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                }

                return size;
            } catch (IOException ioException) {
                Toast.makeText(MainActivity.this, "Error retrieving content", Toast.LENGTH_LONG).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "";
        }

        protected void onPostExecute(String contentSize) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            // Create ARToolkit activity intent
            Intent intentARToolkitActivity = new Intent(MainActivity.this, ARToolkitActivity.class);

            // Add content size to the intent
            intentARToolkitActivity.putExtra(ARTSConstants.CONTENT_SIZE, contentSize);

            // Start Activity
            startActivity(intentARToolkitActivity);
        }
    }
}
