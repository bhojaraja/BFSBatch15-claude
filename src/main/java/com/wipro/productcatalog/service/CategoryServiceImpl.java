package com.wipro.productcatalog.service;

import com.wipro.productcatalog.entity.Category;
import com.wipro.productcatalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CategoryServiceImpl
 *
 * Implementation of CategoryService interface
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(String categoryId) {
        log.debug("Fetching category by ID: {}", categoryId);
        return categoryRepository.findById(categoryId).orElse(null);
    }

}
