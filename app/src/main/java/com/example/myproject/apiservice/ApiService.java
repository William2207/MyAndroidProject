package com.example.myproject.apiservice;

import com.example.myproject.models.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @FormUrlEncoded
    @POST("auth/register")
    Call<String> registerUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("mail") String mail
    );

    @FormUrlEncoded
    @POST("auth/login")
    Call<String> login(
            @Field("username") String username,
            @Field("password") String password

    );

    @GET("user/profile/{username}")
    Call<User> getUserByUsername(
            @Header("Authorization") String token,
            @Path("username") String username
    );

    @FormUrlEncoded
    @POST("user/profile/edit")
    Call<User> editUserProfile(
            @Header("Authorization") String token,
            @Field("userId") int id,
            @Field("username") String username,
            @Field("bio") String bio,
            @Field("email") String email
    );

    @Multipart
    @POST("media/upload-profile-image")
    Call<String> uploadProfileImage(
            @Header("Authorization") String token, // Token JWT nếu backend yêu cầu xác thực
            @Part("username") String username,     // Tên người dùng
            @Part MultipartBody.Part file          // File hình ảnh
    );


}
