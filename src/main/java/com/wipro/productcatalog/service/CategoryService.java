package com.wipro.productcatalog.service;

import com.wipro.productcatalog.entity.Category;

/**
 * CategoryService Interface
 *
 * Service layer contract for category-related operations
 */
public interface CategoryService {

    /**
     * Get category by ID
     *
     * @param categoryId - Category ID
     * @return - Category entity or null if not found
     */
    Category getCategoryById(String categoryId);

}
