package com.hackerrank.eshopping.product.dashboard.repository;

import com.hackerrank.eshopping.product.dashboard.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductsRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM PRODUCTS WHERE category = ?1 " +
        "ORDER BY availability DESC, discounted_price, id",
        nativeQuery = true)
    List<Product> getProductsByCategory(String category);

    @Query(value = "SELECT PRODUCTS.*, ROUND(((retail_price - discounted_price) / retail_price) * 100, 2) as discounted_percentage " +
        "FROM PRODUCTS WHERE category = ?1 AND availability = ?2 " +
        "ORDER BY discounted_percentage DESC, discounted_price, id",
        nativeQuery = true)
    List<Product> getProductsByCategoryAndAvailability(String category, boolean availability);
}
