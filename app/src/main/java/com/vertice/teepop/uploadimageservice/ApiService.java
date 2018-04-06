package com.vertice.teepop.uploadimageservice;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by topza on 4/6/2018.
 */

public interface ApiService {

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
