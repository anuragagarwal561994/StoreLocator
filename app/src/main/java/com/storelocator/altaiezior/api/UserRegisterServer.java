package com.storelocator.altaiezior.api;

import retrofit.http.PUT;
import retrofit.http.Query;

/**
 * Created by altaiezior on 20/4/15.
 */
public interface UserRegisterServer {
    public static class RegisterResponse{
        Boolean status;
        Long id;

        public Boolean getStatus() {
            return status;
        }

        public Long getId() {
            return id;
        }
    }
    @PUT("/register")
    RegisterResponse register_user(@Query("email") String email,
                                     @Query("password") String password);
}
