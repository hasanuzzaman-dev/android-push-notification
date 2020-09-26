package com.example.androidpushnotification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.androidpushnotification.api.NotificationApi;
import com.example.androidpushnotification.api.RetrofitClient;
import com.example.androidpushnotification.data.NotificationData;
import com.example.androidpushnotification.data.NotificationHelper;
import com.example.androidpushnotification.data.PushNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText titleET, messageET, tokenET;
    private Button sendBtn;
    public static final String TOPIC = "/topics/myTopic";
    public static final String CHANNEL_ID = "com.example.androidpushnotification.CHANNEL_ID";
    public static final String CHANNEL_NAME = "com.example.androidpushnotification.CHANNEL_NAME";
    public static final String CHANNEL_DESC = "com.example.androidpushnotification.CHANNEL_DESC";
    private String title, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        /* if android version is greater than or equal Oreo
        then we need to create a notification channel*/
        createChannel();

        /*click notification to go an activity*/
        initAlertDetails();

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);


        String token = tokenET.getText().toString();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 title = titleET.getText().toString();
                 message = messageET.getText().toString();

                if (!title.isEmpty() && !message.isEmpty()){

                    PushNotification pushNotification = new PushNotification(new NotificationData(title,message), TOPIC);
                    sendNotification(pushNotification);


                }

            }
        });
    }

    private void initAlertDetails() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(CHANNEL_DESC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

    }

    private void initView() {
        titleET = findViewById(R.id.titleET);
        messageET = findViewById(R.id.messageET);
        tokenET = findViewById(R.id.tokenEt);
        sendBtn = findViewById(R.id.sendBtn);

    }

    public void sendNotification(PushNotification pushNotification){
        try{

            Retrofit retrofit = RetrofitClient.getClient();
            NotificationApi notificationApi = retrofit.create(NotificationApi.class);

            Call<ResponseBody> responseBodyCall = notificationApi.postNotification(pushNotification);

            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200){
                        //Log.d(TAG, "Response: "+ new Gson().toJson(responseBodyCall));
                        Log.d(TAG, "onResponse: success");
                        ResponseBody responseBody = response.body();
                        NotificationHelper.displayNotification(MainActivity.this,title,message);
                    }else {
                        Log.d(TAG, "onResponse: response.code: "+response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    Log.d(TAG, "onFailure: "+t.getLocalizedMessage());
                }
            });

            /*if (bodyResponse.isSuccessful()){
                Log.d(TAG, "Response: "+ new Gson().toJson(bodyResponse));
            }else {
                Log.e(TAG, "sendNotification: not Response"+bodyResponse.errorBody().toString());
            }*/

        }catch (Exception e){
            Log.e(TAG, "sendNotification: "+e.toString());
        }
    }
}