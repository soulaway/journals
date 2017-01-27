package com.github.soulaway.journals.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.soulaway.journals.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
