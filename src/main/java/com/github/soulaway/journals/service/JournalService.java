package com.github.soulaway.journals.service;

import java.util.List;

import com.github.soulaway.journals.model.Journal;
import com.github.soulaway.journals.model.Publisher;
import com.github.soulaway.journals.model.User;

public interface JournalService {

	List<Journal> listAll(User user);

	List<Journal> publisherList(Publisher publisher);

	Journal publish(Publisher publisher, Journal journal, Long categoryId);

	void unPublish(Publisher publisher, Long journalId);
	
	List<Journal> getNewByCategory(Long categoryId);
}
