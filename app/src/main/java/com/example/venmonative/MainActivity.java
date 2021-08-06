package com.example.venmonative;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> venmoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {

                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        String username = data.getStringExtra("VENMO_ACCOUNT_USERNAME");
                        TextView resultText = findViewById(R.id.text_result);
                        resultText.setText(username);
                    }

                    if(result.getResultCode() == Activity.RESULT_CANCELED) {
                        Intent data = result.getData();
                        String error = data.getStringExtra("ERROR");
                        TextView resultText = findViewById(R.id.text_result);
                        resultText.setText(error);
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ReactNative","MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button venmoButton = findViewById(R.id.venmo_button);

        venmoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VenmoActivity.class);
                intent.putExtra("AUTH", "______REPLCAE_WITH_ACTUAL_TOKEN_____");
                Log.d("ReactNative","MainActivity onClick");
                venmoActivityResultLauncher.launch(intent);
            }
        });
    }
}