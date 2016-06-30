package com.uvic.arts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.artoolkit.ar.base.assets.AssetHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public RequestManager requestManager;

    private String arContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.initializeInstance();

        setContentView(R.layout.activity_main);
        Button scanButton = (Button) findViewById(R.id.scan_button);
        Button launchArButton = (Button) findViewById(R.id.launch_ar);

        if (scanButton != null) {
            scanButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    new IntentIntegrator(MainActivity.this).initiateScan();
                    return false;
                }
            });
        }

        if (launchArButton != null) {
            launchArButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    Intent intentARToolkit = new Intent(MainActivity.this, ARToolkitActivity.class);
                    intentARToolkit.putExtra(ARTSConstants.INTENT_CONTENT, arContent);
                    startActivity(intentARToolkit);
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
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
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
                return requestManager.getContent(contentIds[0]);
            } catch (IOException ioException) {
                Toast.makeText(MainActivity.this, "Error retrieving content", Toast.LENGTH_LONG).show();
            }

            return "";
        }

        protected void onPostExecute(String response) {
            TextView contentTextView = (TextView) findViewById(R.id.content_text);
            ImageView contentImageView = (ImageView) findViewById(R.id.content_image);
            Button launchARButton = (Button) findViewById(R.id.launch_ar);
            JSONObject jsonObject;

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            try {
                jsonObject = new JSONObject(response);

                // Check if content is text or an image, and handle it accordingly
                if (jsonObject.getString(ARTSConstants.CONTENT_TYPE_KEY).equals(ARTSConstants.TEXT_CONTENT)) {
                    arContent = jsonObject.getString(ARTSConstants.CONTENT_DATA_KEY);
                    if (contentTextView != null) {
                        contentTextView.setVisibility(View.VISIBLE);
                        contentTextView.setText(arContent);
                    }

                    if (contentImageView != null) {
                        contentImageView.setVisibility(View.GONE);
                    }

                    if (launchARButton != null) {
                        launchARButton.setVisibility(View.VISIBLE);
                    }
                } else if (jsonObject.getString(ARTSConstants.CONTENT_TYPE_KEY).equals(ARTSConstants.IMAGE_CONTENT)) {
                    arContent = jsonObject.getString(ARTSConstants.CONTENT_DATA_KEY);
                    if (contentTextView != null) {
                        contentTextView.setVisibility(View.GONE);
                    }

                    if (contentImageView != null) {
                        contentImageView.setVisibility(View.VISIBLE);
                        // Strip data that isn't the bytes themselves - Might remove this from server response
                        // in the future if we don't need it for AR purposes
                        String formattedArContent = arContent.replace("data:image/jpeg;base64,", "");

                        // Decode image and display
                        byte[] decodedString = Base64.decode(formattedArContent, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        //TODO Add in the texture to AR thing
                        Intent intentARToolkit = new Intent(MainActivity.this, ARToolkitActivity.class);

                        // Add bitmap for texture to intent
                        intentARToolkit.putExtra("BitmapImage", bitmap);

                        // Start Activity
                        startActivity(intentARToolkit);
                    }

                    if (launchARButton != null) {
                        launchARButton.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException jsonException) {
                Toast.makeText(MainActivity.this, jsonException.getMessage(), Toast.LENGTH_LONG).show();
                if (launchARButton != null) {
                    launchARButton.setVisibility(View.GONE);
                }
            }
        }
    }
}
