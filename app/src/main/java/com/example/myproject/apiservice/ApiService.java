package com.example.myproject.apiservice;

import com.example.myproject.dto.CommentDTO;
import com.example.myproject.dto.LikeDTO;
import com.example.myproject.dto.PostDTO;
import com.example.myproject.dto.SaveDTO;
import com.example.myproject.models.ChatRequest;
import com.example.myproject.models.ChatResponse;
import com.example.myproject.models.Message;
import com.example.myproject.models.PostCollection;
import com.example.myproject.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    @GET("user/profile/search")
    Call<List<User>> search(
            @Header("Authorization") String token,
            @Query("query") String query
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
            @Header("Authorization") String token,    // Token JWT
            @Part MultipartBody.Part file,          // File hình ảnh
            @Part("username") String username     // Tên người dùng
    );

    @POST("post/create")
    Call<PostCollection> createPost(
            @Header("Authorization") String token,
            @Body PostCollection post
    );
    @POST("post/create-reel")
    Call<PostCollection> createReel(
            @Header("Authorization") String token,
            @Body PostCollection reel
    );

    @Multipart
    @POST("media/upload")
    Call<List<String>> uploadImages(
            @Header("Authorization") String token,
            @Part MultipartBody.Part[] files
    );

    @Multipart
    @POST("media/upload-reel")
    Call<String> uploadReel(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file
    );

    @GET("post/posts")
    Call<List<PostDTO>> getPosts(
            @Header("Authorization") String token,
            @Query("userId") int userId
    );
    @GET("post/reels")
    Call<List<PostDTO>> getReels(
            @Header("Authorization") String token,
            @Query("userId") int userId
    );
    @GET("post/user/{userId}/posts")
    Call<List<PostDTO>> getUserPosts(
            @Header("Authorization") String token,
            @Path("userId") int userId
    );
    @GET("post/{userId}/liked-posts")
    Call<List<PostDTO>> getLikedPosts(
            @Header("Authorization") String token,
            @Path("userId") int userId
    );
    @GET("post/{userId}/saved-posts")
    Call<List<PostDTO>> getSavedPosts(
            @Header("Authorization") String token,
            @Path("userId") int userId
    );

    @POST("post/like")
    Call<Boolean> toggleLike(@Header("Authorization") String authToken, @Body LikeDTO likeDTO);

    @POST("post/save")
    Call<Boolean> toggleSave(@Header("Authorization") String authToken, @Body SaveDTO saveDTO);

    @GET("comment/comments")
    Call<List<CommentDTO>> getComments(
            @Header("Authorization") String token,
            @Query("postId") int postId
    );

    @POST("comment/create")
    Call<CommentDTO> createComment(
            @Header("Authorization") String token,
            @Query("userId") int userId,
            @Query("postId") int postId,
            @Body CommentDTO commentDTO
    );

    @POST("follow/{userId}")
    Call<Void> followUser(@Header("Authorization") String token,@Path("userId") int userId, @Query("username") String username);

    @DELETE("follow/{userId}")
    Call<Void> unfollowUser(@Header("Authorization") String token,@Path("userId") int userId, @Query("username") String username);

    @GET("follow/{userId}")
    Call<Boolean> isFollowing(@Header("Authorization") String token,@Path("userId") int userId, @Query("username") String username);


    @GET("user/profile/followings")
    Call<List<User>> getFollowingUsers(
            @Header("Authorization") String token,
            @Query("userId") int userId
    );

    @POST("messages/send")
    Call<Message> sendMessages(@Header("Authorization") String token,@Body Message message);

    @GET("messages/{userId}/{otherUserId}")
    Call<List<Message>> getMessages(
            @Header("Authorization") String token,
            @Path("userId") String userId,
            @Path("otherUserId") String otherUserId);
    @PUT("messages/read")
    Call<Void> markAsRead(@Header("Authorization") String token,@Body List<String> messageIds);

    @POST("chat/completions")
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}
