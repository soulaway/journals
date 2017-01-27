package com.github.soulaway.journals.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.github.soulaway.journals.model.Journal;
import com.github.soulaway.journals.model.Publisher;

public interface JournalRepository extends CrudRepository<Journal, Long> {

    Collection<Journal> findByPublisher(Publisher publisher);

    List<Journal> findByCategoryIdIn(List<Long> ids);
    
    List<Journal> findByCategoryIdAndPublishDateBetween(Long catId, Date endDate, Date startDate);
}
