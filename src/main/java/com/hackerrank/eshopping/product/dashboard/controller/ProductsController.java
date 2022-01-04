package com.hackerrank.eshopping.product.dashboard.controller;

import com.hackerrank.eshopping.product.dashboard.model.Product;
import com.hackerrank.eshopping.product.dashboard.repository.ProductsRepository;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/products")
public class ProductsController {

    private final ProductsRepository productsRepository;

    @Autowired
    public ProductsController(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        //TODO: Possible @Service method returning Product or null
        if (productsRepository.existsById(product.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Product newProduct = productsRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Optional<Product> productOptional = productsRepository.findById(id);
        if (!productOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Product foundProduct = productOptional.get();
        foundProduct.setRetailPrice(product.getRetailPrice());
        foundProduct.setDiscountedPrice(product.getDiscountedPrice());
        foundProduct.setAvailability(product.getAvailability());
        productsRepository.save(foundProduct);
        return ResponseEntity.ok(foundProduct);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Optional<Product> productOptional = productsRepository.findById(id);
        return productOptional.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping()
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String category,
                                                     @RequestParam(required = false) Boolean availability)
        throws UnsupportedEncodingException {
        if (category != null && availability != null) {
            // https://github.com/spring-projects/spring-framework/issues/16476
            String encodedCategory = URLDecoder.decode(category, "UTF-8");
            List<Product> productsByCategoryAndAvailability =
                productsRepository.getProductsByCategoryAndAvailability(encodedCategory, availability);
            return ResponseEntity.ok(productsByCategoryAndAvailability);
        } else if (category != null) {
            // https://github.com/spring-projects/spring-framework/issues/16476
            String encodedCategory = URLDecoder.decode(category, "UTF-8");
            List<Product> productsByCategory =
                productsRepository.getProductsByCategory(encodedCategory);

            return ResponseEntity.ok(productsByCategory);
        }

        return ResponseEntity.ok(productsRepository.findAll());
    }

}
