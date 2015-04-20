package com.storelocator.altaiezior.api;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by altaiezior on 20/4/15.
 */
public interface ProductSearchServer {
    public static class ProductList{
        List<Product> products;

        public List<Product> getProducts() {
            return products;
        }
    }

    public static class Product{
        Long _id;
        String name;
        String description;
        Long popularity;
        Long category;
        String brand;

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Long getPopularity() {
            return popularity;
        }

        public String getBrand() {
            return brand;
        }
    }

    @GET("/search")
    ProductList search_product(@Query("name") String name,
                               @Query("parent_id") Long parent_id);
}
