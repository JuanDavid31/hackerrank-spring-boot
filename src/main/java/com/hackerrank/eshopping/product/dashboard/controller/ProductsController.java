package com.hackerrank.eshopping.product.dashboard.controller;

import com.hackerrank.eshopping.product.dashboard.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/products")
public class ProductsController {

    private static ArrayList<Product> products = new ArrayList<>();

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        if(findProduct(product.getId()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        products.add(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product foundProduct = findProduct(id);
        if (foundProduct == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        foundProduct.setRetailPrice(product.getRetailPrice());
        foundProduct.setDiscountedPrice(product.getDiscountedPrice());
        foundProduct.setAvailability(product.getAvailability());
        return ResponseEntity.ok(foundProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = findProduct(id);
        return product != null ? ResponseEntity.ok(product) : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping()
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String category,
                                                     @RequestParam(required = false) Boolean availability) throws UnsupportedEncodingException {
        List<Product> resultingProducts = products;

        if (category != null && availability != null) {
            String encodedCategory = URLDecoder.decode(category, "UTF-8");
            resultingProducts = resultingProducts.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(encodedCategory))
                .filter(product -> product.getAvailability().equals(availability))
                .sorted(Comparator.comparingDouble(this::getDiscountedPercentage).reversed()
                    .thenComparing(Product::getDiscountedPrice)
                    .thenComparing(Product::getId)
                )
                .collect(Collectors.toList());
        } else if (category != null) {
            String encodedCategory = URLDecoder.decode(category, "UTF-8");
            resultingProducts = resultingProducts.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(encodedCategory))
                .sorted(Comparator.comparing(Product::getAvailability).reversed()
                            .thenComparing(Product::getDiscountedPrice)
                            .thenComparing(Product::getId)
                )
                .collect(Collectors.toList());
        }

        return ResponseEntity.ok(resultingProducts);
    }

    private Product findProduct(Long id) {
        for (Product currentProduct : products) {
            if (currentProduct.getId().equals(id)) {
                return currentProduct;
            }
        }
        return null;
    }

    public double getDiscountedPercentage(Product product) {
        double v = ((product.getRetailPrice() - product.getDiscountedPrice()) / product.getRetailPrice()) * 100;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        return Double.parseDouble(df.format(v));
    }
}
