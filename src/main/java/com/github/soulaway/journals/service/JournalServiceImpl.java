package com.github.soulaway.journals.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.soulaway.journals.controller.PublisherController;
import com.github.soulaway.journals.model.Category;
import com.github.soulaway.journals.model.Journal;
import com.github.soulaway.journals.model.Publisher;
import com.github.soulaway.journals.model.Subscription;
import com.github.soulaway.journals.model.User;
import com.github.soulaway.journals.repository.CategoryRepository;
import com.github.soulaway.journals.repository.JournalRepository;
import com.github.soulaway.journals.repository.UserRepository;

@Service
public class JournalServiceImpl implements JournalService {

	private final static Logger log = Logger.getLogger(JournalServiceImpl.class);

	@Autowired
	private JournalRepository journalRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired		
	private MailService mailService;
	
	@Transactional(readOnly = true)	
	@Override
	public List<Journal> listAll(User user) {
		User persistentUser = userRepository.findOne(user.getId());
		List<Subscription> subscriptions = persistentUser.getSubscriptions();
		if (subscriptions != null) {
			List<Long> ids = new ArrayList<>(subscriptions.size());
			subscriptions.stream().forEach(s -> ids.add(s.getCategory().getId()));
			return journalRepository.findByCategoryIdIn(ids);
		} else {
			return Collections.emptyList();
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<Journal> publisherList(Publisher publisher) {
		Iterable<Journal> journals = journalRepository.findByPublisher(publisher);
		return StreamSupport.stream(journals.spliterator(), false).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public Journal publish(Publisher publisher, Journal journal, Long categoryId) throws ServiceException {
		Category category = categoryRepository.findOne(categoryId);
		if(category == null) {
			throw new ServiceException("Category not found");
		}
		System.out.println(" CAT " + categoryId + " fuound " + category.getName());
		journal.setPublisher(publisher);
		journal.setCategory(category);
		try {
			Journal result = journalRepository.save(journal);
			mailService.notifySubscribers(categoryId, journal);		
			return result;
		} catch (DataIntegrityViolationException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public void unPublish(Publisher publisher, Long id) throws ServiceException {
		Journal journal = journalRepository.findOne(id);
		if (journal == null) {
			throw new ServiceException("Journal doesn't exist");
		}
		String filePath = PublisherController.getFileName(publisher.getId(), journal.getUuid());
		File file = new File(filePath);
		if (file.exists()) {
			boolean deleted = file.delete();
			if (!deleted) {
				log.error("File " + filePath + " cannot be deleted");
			}
		}
		if (!journal.getPublisher().getId().equals(publisher.getId())) {
			throw new ServiceException("Journal cannot be removed");
		}
		journalRepository.delete(journal);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Journal> getNewByCategory(Long categoryId) {
		LocalDateTime now = LocalDateTime.now();
		Date newPeriodEnd = toDate(now);
		Date newPeriodStart = toDate(now.minusDays(1L));
		List<Journal> result = journalRepository.findByCategoryIdAndPublishDateBetween(categoryId, newPeriodStart, newPeriodEnd);
		return result;
	}
	
	private Date toDate(LocalDateTime localDate) {
		return Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
	}
}
