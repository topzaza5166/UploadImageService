package com.vertice.teepop.uploadimageservice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.github.oliveiradev.lib.Rx2Photo;
import com.github.oliveiradev.lib.shared.TypeRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
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

        Retrofit.Builder builder = new Retrofit.Builder();
        Retrofit retrofit = builder.baseUrl("http://192.168.1.46:65/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        initInstance();
    }

    private void initInstance() {
        button = findViewById(R.id.uploadButton);
        button.setOnClickListener(view -> {
            upload();
        });
    }

    private void upload() {
        Rx2Photo.with(this).requestMultiBitmap()
                .flatMap(bitmaps -> Observable.fromIterable(bitmaps)
                        .subscribeOn(Schedulers.io()))
                .map(this::getByteFromBitmap)
                .toList()
                .subscribe(bytes -> {
                    //TODO: run intent service
                    Intent intent = new Intent(this, UploadService.class);
                    startService(intent);
                }, Throwable::printStackTrace);
    }

    //.flatMapSingle(this::getUploadSingle)
    private byte[] getByteFromBitmap(Bitmap bitmap) {
        byte[] file;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        file = stream.toByteArray();
        return file;
    }

    private Single<JsonResponse> getUploadSingle(byte[] image) {
        return apiService.upload(
                createPartFromString("1qaz2wsx3edc4rfv"),
                createPartFromString("satei_transaction"),
                createPartFromString("upload_image"),
                createPartFromString("{\"staff_code\" : \"00001\",\"kyoten_code\" : \"00001\",\"group_code\" : \"99\",\"syaten_code\" : \"00001\"}"),
                createPartFromString("00001000010000000010"),
                createPartFromString("1"),
                createPartFromString("1"),
                getPartFromByte(image)
        ).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public MultipartBody.Part getPartFromByte(byte[] image) {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        image
                );
        return MultipartBody.Part.createFormData("image", "filename", requestFile);
    }

    interface ApiService {

        @Multipart
        @POST("main.php")
        Single<JsonResponse> upload(@Part("auth_key") RequestBody auth_key,
                                    @Part("func") RequestBody func,
                                    @Part("action") RequestBody action,
                                    @Part("user_info") RequestBody jsonUserInfo,
                                    @Part("satei_code") RequestBody sateiCode,
                                    @Part("status_flg") RequestBody statusFlg,
                                    @Part("picture_number") RequestBody pictureNumber,
                                    @Part MultipartBody.Part image);
    }

    private RequestBody createPartFromString(String s) {
        return RequestBody.create(okhttp3.MultipartBody.FORM, s);
    }
}

