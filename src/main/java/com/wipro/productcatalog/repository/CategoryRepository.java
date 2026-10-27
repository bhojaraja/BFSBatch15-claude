package com.wipro.productcatalog.repository;

import com.wipro.productcatalog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CategoryRepository
 *
 * Data access layer for Category entity
 * Extends JpaRepository for CRUD operations
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

}
