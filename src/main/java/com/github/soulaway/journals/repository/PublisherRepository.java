package com.github.soulaway.journals.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.soulaway.journals.model.Publisher;
import com.github.soulaway.journals.model.User;

import java.util.Optional;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    Optional<Publisher> findByUser(User user);

}
