package com.storelocator.altaiezior.api;

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

    @POST("/user")
    UpdateUserResponse updateUser( @Query("fname") String fname,
                                   @Query("lname") String lname,
                                   @Query("email") String email,
                                   @Query("phone") String phone,
                                   @Query("id") Long id);
}
