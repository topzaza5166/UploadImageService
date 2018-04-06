package com.vertice.teepop.uploadimageservice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.github.oliveiradev.lib.Rx2Photo;

import java.io.ByteArrayOutputStream;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class MainActivity extends AppCompatActivity {

    private Button button;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initInstance();
    }

    private void initInstance() {
        button = findViewById(R.id.uploadButton);
        button.setOnClickListener(view -> {
            pickImage();
        });
    }

    private void pickImage() {
        Rx2Photo.with(this).requestMultiBitmap()
                .flatMap(bitmaps -> Observable.fromIterable(bitmaps)
                        .subscribeOn(Schedulers.io()))
                .map(this::getByteFromBitmap)
                .toList()
                .subscribe(bytes -> {
                    for (byte[] image : bytes) {
                        startUplloadService(image);
                    }
                }, Throwable::printStackTrace);
    }

    private void startUplloadService(byte[] image) {
        Intent intent = new Intent(this, UploadService.class);
        intent.putExtra("image", image);
        startService(intent);
    }

    //.flatMapSingle(this::getUploadSingle)
    private byte[] getByteFromBitmap(Bitmap bitmap) {
        byte[] file;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        file = stream.toByteArray();
        return file;
    }

}

