package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.SocketTimeoutException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private IWeatherData iWeatherData;
    private ImageView imageShow;
    private TextView textViewName, textViewEmail;
    private String api_base_url = "https://demo3.edufy.cloud/api/v1/";
    private String versionName = "api/v1/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(api_base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(callOkHttpClint())
                .build();

        iWeatherData = retrofit.create(IWeatherData.class);
        textViewName = findViewById(R.id.name);
        textViewEmail = findViewById(R.id.email);

        // getImagefromWeb();
        getClassData();
    }

    private void getClassData() {
        Call<GetClassMain> call =  iWeatherData.getSchoolClass();
        call.enqueue(new Callback<GetClassMain>() {
            @Override
            public void onResponse(Call<GetClassMain> call, Response<GetClassMain> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    fetchClass(response.body().getData());
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetClassMain> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Log.d(TAG, "onFailure: " + t.toString());
                } else {
                    Log.d(TAG, "onFailure: " + t.toString());
                }
            }
        });


    }

    private void getImagefromWeb() {
        Call<List<JsonPlaceModel>> call =  iWeatherData.getImage();
        call.enqueue(new Callback<List<JsonPlaceModel>>() {
            @Override
            public void onResponse(Call<List<JsonPlaceModel>> call, Response<List<JsonPlaceModel>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    useMethod(response.body());
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<JsonPlaceModel>> call, Throwable t) {

            }
        });
    }

    private void useMethod(List<JsonPlaceModel> body) {
        for (JsonPlaceModel js: body){
            textViewName.setText(js.getName());
            textViewEmail.setText(js.getEmail());
            Log.d(TAG, "useMethod: " + js.toString());
        }
    }

    private void fetchClass(List<SchoolClass> schoolClassList){
        for (SchoolClass sc: schoolClassList){
            textViewName.setText(String.valueOf(sc.getId()));
            textViewEmail.setText(sc.getName());
            Log.d(TAG, "useMethod: " + sc.toString());
        }
    }

    private final OkHttpClient.Builder httpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS);


    private HttpLoggingInterceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }


    private OkHttpClient callOkHttpClint() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            if (BuildConfig.DEBUG) {
                httpClient.addInterceptor(getLoggingInterceptor());
            }
            return httpClient.build();
        } else {
            return getUnsafeOkHttpClient().newBuilder().build();
        }
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}