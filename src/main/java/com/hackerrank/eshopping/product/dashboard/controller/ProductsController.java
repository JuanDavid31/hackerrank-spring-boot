package com.hackerrank.eshopping.product.dashboard.controller;

import com.hackerrank.eshopping.product.dashboard.model.Product;
import com.hackerrank.eshopping.product.dashboard.service.ProductService;
import com.hackerrank.eshopping.product.dashboard.util.URLUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/products")
public class ProductsController {

    private final ProductService productsService;

    @Autowired
    public ProductsController(ProductService productService) {
        this.productsService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        Product existingProduct = productsService.insertProduct(product);

        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(productsService.insertProduct(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updatedProduct = productsService.updateProduct(id, product);
        if (updatedProduct == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = productsService.findProductById(id);
        return product != null ? ResponseEntity.ok(product)
            : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @GetMapping()
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String category,
                                                     @RequestParam(required = false) Boolean availability) {
        if (category != null && availability != null) {
            // https://github.com/spring-projects/spring-framework/issues/16476
            String encodedCategory = URLUtils.decodeString(category);
            List<Product> productsByCategoryAndAvailability =
                productsService.getProductsByCategoryAndAvailability(encodedCategory, availability);
            return ResponseEntity.ok(productsByCategoryAndAvailability);
        } else if (category != null) {
            // https://github.com/spring-projects/spring-framework/issues/16476
            String encodedCategory = URLUtils.decodeString(category);
            List<Product> productsByCategory =
                productsService.getProductsByCategory(encodedCategory);

            return ResponseEntity.ok(productsByCategory);
        }

        return ResponseEntity.ok(productsService.getAll());
    }

}
