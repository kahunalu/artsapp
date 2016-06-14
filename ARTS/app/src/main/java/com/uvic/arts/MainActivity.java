package com.uvic.arts;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.toast_button).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                new IntentIntegrator(MainActivity.this).initiateScan();
                return false;
            }
        });
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

    class FetchContentTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... contentIds) {
            try {
                return requestManager.getContent(contentIds[0]);
            } catch (IOException ioException) {
                Toast.makeText(getApplicationContext(), "Error retrieving content", Toast.LENGTH_LONG).show();
            }

            return "";
        }

        protected void onPostExecute(String response) {
            TextView contentTextView = (TextView) findViewById(R.id.contentTextView);
            contentTextView.setMovementMethod(new ScrollingMovementMethod());
            contentTextView.setText(response);
            Intent intentARToolkit = new Intent(getApplicationContext(), ARToolkitActivity.class);
            startActivity(intentARToolkit);
        }
    }
}
