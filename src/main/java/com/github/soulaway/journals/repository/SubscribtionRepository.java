package com.github.soulaway.journals.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.soulaway.journals.model.Category;
import com.github.soulaway.journals.model.Subscription;

public interface SubscribtionRepository extends JpaRepository<Subscription, Long> {
	List<Subscription> findUserDistinctByCategory(Category cat);
}
