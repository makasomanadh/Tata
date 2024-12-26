package com.example.Tata.Exception;

public class ProductNotFoundException extends RuntimeException  {

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
