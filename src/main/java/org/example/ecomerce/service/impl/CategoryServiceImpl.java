package org.example.ecomerce.service.impl;

import org.example.ecomerce.model.Category;
import org.example.ecomerce.repository.CategoryRepository;
import org.example.ecomerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
