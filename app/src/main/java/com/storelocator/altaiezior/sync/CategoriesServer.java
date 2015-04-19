package com.storelocator.altaiezior.sync;

import android.util.Log;

import java.util.List;

import com.storelocator.altaiezior.database.CategoryItem;

import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface CategoriesServer {

    /**
     * Change the IP to the address of your server
     */
    // Server-app uses no prefixes in the URL
    // Server on App Engine will have a Base URL like this
    //TODO: Change this IP when deployed
    public static final String API_URL = "http://10.0.2.2:8080/_ah/api/storelocator/v1";

    public static class CategoryItems {
        String latestTimestamp;
        List<CategoryMSG> categories;
    }

    /**
     * We could have used CategoryItem class directly instead.
     * But to make it compatible with both servers, I chose
     * to make this converter class to handle the deleted field.
     * Converting the integer to boolean for the JSON message.
     */
    public static class CategoryMSG {
        Long _id;
        Long parent_id;
        String name;
        boolean deleted;
        String timestamp;

        public CategoryMSG(CategoryItem category) {
            _id = category._id;
            parent_id = category.parent_id;
            name = category.name;
            deleted = (category.deleted == 1);
        }

        public CategoryItem toDBItem() {
            final CategoryItem item = new CategoryItem();
            if(parent_id!=null)
                item.parent_id = parent_id;
            item.name = name;
            item._id = _id;
            item.timestamp = timestamp;
            if (deleted) {
                item.deleted = 1;
            }
            return item;
        }
    }

    public static class Dummy {
        // Methods must have return type
    }

    @GET("/categories")
    CategoryItems listCategories(@Header("Authorization") String token,
                        @Query("showDeleted") String showDeleted,
                        @Query("timestampMin") String timestampMin);

    @DELETE("/categories/{_id}")
    Dummy deleteCategory(@Header("Authorization") String token, @Path("_id") Long _id);

    @POST("/categories")
    CategoryMSG addCategory(@Header("Authorization") String token, @Body CategoryMSG item);
}