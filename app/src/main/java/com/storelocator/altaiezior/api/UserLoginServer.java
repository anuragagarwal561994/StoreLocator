package com.storelocator.altaiezior.api;

import com.storelocator.altaiezior.LoginResponse;

import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Query;

public interface UserLoginServer {
    public static class UserAuthentication {
        LoginResponse status;
        UserDetailItem shopkeeperInformation;

        public LoginResponse getStatus() {
            return status;
        }

        public UserDetailItem getShopkeeperInformation() {
            return shopkeeperInformation;
        }
    }

    @POST("/login")
    UserAuthentication check_user_authentication(@Query("email") String email,
                                                 @Query("password") String password);
}