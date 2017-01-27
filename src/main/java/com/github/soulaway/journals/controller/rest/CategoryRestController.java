package com.github.soulaway.journals.controller.rest;

import com.github.soulaway.journals.model.Category;
import com.github.soulaway.journals.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/rest/category")
public class CategoryRestController {

    @Autowired
    private CategoryRepository repository;


    @RequestMapping(value = "")
    public List<Category> getCategories() {
        return repository.findAll();
    }

}
