package com.github.soulaway.journals.service;

import com.github.soulaway.journals.model.Journal;

public interface MailService {
	
	void notifySubscribers(Long categoryId, Journal journal);
	
	void scheduleDigestDelivery();
}
