package com.assalam.chatassalam.api;

import com.assalam.chatassalam.TergabungDiGroup;
import com.assalam.chatassalam.model.AddFriend;
import com.assalam.chatassalam.model.CheckFriend;
import com.assalam.chatassalam.model.Contact;
import com.assalam.chatassalam.model.EditProfil;
import com.assalam.chatassalam.model.EditProfilTaaruf;
import com.assalam.chatassalam.model.GetTime;
import com.assalam.chatassalam.model.GetTimeDifference;
import com.assalam.chatassalam.model.Group;
import com.assalam.chatassalam.model.Login;
import com.assalam.chatassalam.model.PembayaranTaaruf;
import com.assalam.chatassalam.model.Register;
import com.assalam.chatassalam.model.StatusTaaruf;
import com.assalam.chatassalam.model.Teman;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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

    @FormUrlEncoded
    @POST("assets/file/pembayaran_taaruf.php")
    Call<PembayaranTaaruf> pembayaranTaaruf(@FieldMap Map<String, String> param);

    @GET("assets/file/status_taaruf.php")
    Call<StatusTaaruf> statusTaaruf(@QueryMap Map<String, String> param);

    @GET("assets/file/search_user.php")
    Call<List<Contact>> searchUser(@QueryMap Map<String, String> param);

    @POST("assets/file/add_friend.php")
    Call<AddFriend> addFriend(@QueryMap Map<String, String> param);

    @GET("assets/file/check_friend.php")
    Call<CheckFriend> checkFriend(@QueryMap Map<String, String> param);

    @GET("assets/file/list_friend.php")
    Call<List<Contact>> listFriend(@QueryMap Map<String, String> param);

    @FormUrlEncoded
    @POST("assets/file/edit_profil.php")
    Call<EditProfil> editProfil(@FieldMap Map<String, String> param);

    @FormUrlEncoded
    @POST("assets/file/edit_profil_taaruf.php")
    Call<EditProfilTaaruf> editProfilTaaruf(@FieldMap Map<String, String> param);

    @GET("assets/file/tergabung_di_group.php")
    Call<TergabungDiGroup> tergabungDiGroup(@QueryMap Map<String, String> param);
}
