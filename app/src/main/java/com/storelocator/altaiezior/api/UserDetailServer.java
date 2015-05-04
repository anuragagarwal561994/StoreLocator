package com.storelocator.altaiezior.api;

import com.storelocator.altaiezior.UserDetail;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by altaiezior on 20/4/15.
 */
public interface UserDetailServer {
    public class UpdateUserResponse{
        Boolean status;

        public Boolean getStatus() {
            return status;
        }
    }

    @GET("/shopkeeper")
    UserDetailItem getShopkeeper(@Query("id") Long id);

    @POST("/shopkeeper")
    UpdateUserResponse updateUser( @Query("fname") String fname,
                                   @Query("lname") String lname,
                                   @Query("shop_name") String shop_name,
                                   @Query("shop_address") String shop_address,
                                   @Query("mobile") Long mobile,
                                   @Query("id") Long id);
}
