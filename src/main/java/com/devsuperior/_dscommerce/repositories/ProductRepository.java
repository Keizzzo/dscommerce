package com.devsuperior._dscommerce.repositories;

import com.devsuperior._dscommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
