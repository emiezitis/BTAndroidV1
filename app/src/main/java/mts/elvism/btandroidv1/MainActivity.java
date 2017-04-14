package mts.elvism.btandroidv1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

import static com.braintreepayments.api.dropin.view.PaymentButton.REQUEST_CODE;

public class MainActivity extends AppCompatActivity {


    private String clientToken;
    private String nonce;


    private static final String SERVER_URL = "http://your-server/checkout";
    private static final String BASE_URL = "https://your-server/client_token";
    private static final String TAG = MainActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a client token  https://developers.braintreepayments.com/start/hello-client/android/v1#get-a-client-token

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(BASE_URL + "/token", new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {


               clientToken = responseString;
            }
        });

    }

//Try it now   https://developers.braintreepayments.com/start/hello-client/android/v1#try-it-now

    public void onBraintreeSubmit(View v) {
        Intent intent = new Intent(this, BraintreePaymentActivity.class);
        intent.putExtra(BraintreePaymentActivity.EXTRA_CLIENT_TOKEN, clientToken);


        startActivityForResult(intent, REQUEST_CODE);
    }


//Present Drop-in UI  https://developers.braintreepayments.com/start/hello-client/android/v1#present-drop-in-ui

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case BraintreePaymentActivity.RESULT_OK:

                    String paymentMethodNonce = data
                            .getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);


                    nonce = paymentMethodNonce;

                    // Send the resulting payment method nonce to your server
                    postNonceToServer(nonce);

                    

                    break;
                case BraintreePaymentActivity.BRAINTREE_RESULT_DEVELOPER_ERROR:
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_ERROR:
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_UNAVAILABLE:
                    // handle errors here, a throwable may be available in
                    // data.getSerializableExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE)
                    break;
                default:
                    break;
            }
        }
    }

    //Send payment method nonce to server https://developers.braintreepayments.com/start/hello-client/android/v1#send-payment-method-nonce-to-server
//Send the resulting payment method nonce to your server (again, this example uses Android Async Http Client - adapt to your own setup):

    private void postNonceToServer(String nonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
       params.put("payment_method_nonce", nonce);
       params.put("amount", "10.00");

       client.post(SERVER_URL + "/payment" , params , new TextHttpResponseHandler() {


           @Override
           public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
               Toast.makeText(MainActivity.this, responseString, Toast.LENGTH_LONG).show();
               Log.d(TAG, "Error: " + responseString);
           }

           @Override
           public void onSuccess(int statusCode, Header[] headers, String responseString) {

               Toast.makeText(MainActivity.this, responseString, Toast.LENGTH_LONG).show();
               Log.d(TAG, "Success: " + responseString);

           }
       });
        
    }

}