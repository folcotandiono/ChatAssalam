package com.assalam.chatassalam.api;

import com.assalam.chatassalam.model.Contact;
import com.assalam.chatassalam.model.GetTime;
import com.assalam.chatassalam.model.GetTimeDifference;
import com.assalam.chatassalam.model.Group;
import com.assalam.chatassalam.model.Login;
import com.assalam.chatassalam.model.Register;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by folcotandiono on 2/1/2019.
 */

public interface ApiInterface {
    @FormUrlEncoded
    @POST("assets/file/register.php")
    Call<Register> register(@FieldMap Map<String, String> param);

    @FormUrlEncoded
    @POST("assets/file/login.php")
    Call<Login> login(@FieldMap Map<String, String> param);

    @FormUrlEncoded
    @POST("assets/file/contact_list.php")
    Call<List<Contact>> contactList(@FieldMap Map<String, String> param);

    @GET("assets/file/get_time.php")
    Call<GetTime> getTime();

    @GET("assets/file/get_time_diff.php")
    Call<GetTimeDifference> getTimeDifference(@QueryMap Map<String, String> param);

    @GET("assets/file/get_user.php")
    Call<Contact> getUser(@QueryMap Map<String, String> param);

    @GET("assets/file/get_group.php")
    Call<Group> getGroup(@QueryMap Map<String, String> param);

    @FormUrlEncoded
    @POST("assets/file/make_group.php")
    Call<Group> makeGroup(@FieldMap Map<String, String> param);

    @GET("assets/file/group_member.php")
    Call<List<Contact>> groupMember(@QueryMap Map<String, String> param);
}
