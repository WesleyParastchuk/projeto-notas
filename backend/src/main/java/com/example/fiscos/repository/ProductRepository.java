package com.example.fiscos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fiscos.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
