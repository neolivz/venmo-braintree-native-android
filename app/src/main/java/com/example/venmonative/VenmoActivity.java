package com.example.venmonative;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.VenmoClient;
import com.braintreepayments.api.VenmoPaymentMethodUsage;
import com.braintreepayments.api.VenmoRequest;

public class VenmoActivity extends AppCompatActivity {

    BraintreeClient braintreeClient;
    VenmoClient venmoClient;
    DataCollector dataCollector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ReactNative","VenmoActivity onCreate");
        super.onCreate(savedInstanceState);
        setTitle("Venmo Activity");

        Intent intent = getIntent();
        String authorization = intent.getStringExtra("AUTH");

        braintreeClient = new BraintreeClient(this, authorization);
        venmoClient = new VenmoClient(braintreeClient);
        dataCollector = new DataCollector(braintreeClient);
        this.tokenizeVenmoAccount();
    }

    private void tokenizeVenmoAccount() {
        VenmoRequest request = new VenmoRequest(VenmoPaymentMethodUsage.MULTI_USE);
        Log.d("ReactNative","VenmoActivity tokenizeVenmoAccount");
        request.setShouldVault(false);

        venmoClient.tokenizeVenmoAccount( this, request, (error) -> {
            Log.d("ReactNative","VenmoActivity venmoClient.tokenizeVenmoAccount");
            if (error != null) {
                Log.d("ReactNative","VenmoActivity venmoClient.tokenizeVenmoAccount error");
                Intent intent = getIntent();

                intent.putExtra("ERROR", error.getMessage());
                setResult(-1, intent);
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ReactNative","AndroidActivity onActivityResult");
        venmoClient.onActivityResult(this, resultCode, data, (venmoAccountNonce, venmoError) -> {
            dataCollector.collectDeviceData(VenmoActivity.this, (deviceData, dataCollectorError) -> {
                // send venmoAccountNonce.getString() and deviceData to server
                Intent resultIntent = new Intent();
                if(dataCollectorError != null) {
                    resultIntent.putExtra("DATA_COLLECTOR_ERROR", dataCollectorError.getMessage());
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                } else if(venmoError != null) {
                    resultIntent.putExtra("ERROR", venmoError.getMessage());
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                }else {
                    resultIntent.putExtra("DEVICE_DATA", deviceData);
                    resultIntent.putExtra("VENMO_ACCOUNT_NONCE", venmoAccountNonce.getString());
                    resultIntent.putExtra("VENMO_ACCOUNT_USERNAME", venmoAccountNonce.getUsername());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

            });
        });
        Log.d("ReactNative","AndroidActivity onActivityResult 2");
    }
}