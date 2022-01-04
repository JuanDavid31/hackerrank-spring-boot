package com.hackerrank.eshopping.product.dashboard.service;

import com.hackerrank.eshopping.product.dashboard.model.Product;
import com.hackerrank.eshopping.product.dashboard.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;

    @Autowired
    public ProductService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public Product insertProduct(Product product) {
        if (productsRepository.existsById(product.getId())) {
            return null;
        }
        return productsRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Optional<Product> productOptional = productsRepository.findById(id);
        if (!productOptional.isPresent()) {
            return null;
        }
        Product foundProduct = productOptional.get();
        foundProduct.setRetailPrice(updatedProduct.getRetailPrice());
        foundProduct.setDiscountedPrice(updatedProduct.getDiscountedPrice());
        foundProduct.setAvailability(updatedProduct.getAvailability());
        return productsRepository.save(foundProduct);
    }

    public Product findProductById(Long id) {
        return productsRepository.findById(id).orElse(null);
    }

    public List<Product> getProductsByCategoryAndAvailability(String category, Boolean availability) {
        return productsRepository.getProductsByCategoryAndAvailability(category, availability);
    }

    public List<Product> getProductsByCategory(String category) {
        return productsRepository.getProductsByCategory(category);
    }

    public List<Product> getAll() {
        return productsRepository.findAll();
    }
}
