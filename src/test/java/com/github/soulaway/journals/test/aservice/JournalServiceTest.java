package com.github.soulaway.journals.test.aservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.soulaway.journals.Application;
import com.github.soulaway.journals.model.Category;
import com.github.soulaway.journals.model.Journal;
import com.github.soulaway.journals.model.Publisher;
import com.github.soulaway.journals.model.User;
import com.github.soulaway.journals.repository.CategoryRepository;
import com.github.soulaway.journals.repository.JournalRepository;
import com.github.soulaway.journals.repository.PublisherRepository;
import com.github.soulaway.journals.service.JournalService;
import com.github.soulaway.journals.service.ServiceException;
import com.github.soulaway.journals.service.UserService;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JournalServiceTest {

	private final static String NEW_JOURNAL_NAME = "New Journal";

	@Resource
	private JournalService journalService;

	@Resource
	private UserService userService;

	@Autowired
	private PublisherRepository publisherRepository;
	
	@Resource
	private JournalRepository journalRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;	
	
	@Test
	public void browseSubscribedUser() {
		List<Journal> journals = journalService.listAll(getUser("user1"));
		assertNotNull(journals);
		assertEquals(1, journals.size());

		assertEquals(new Long(1), journals.get(0).getId());
		assertEquals("Medicine", journals.get(0).getName());
		assertEquals(new Long(1), journals.get(0).getPublisher().getId());
		assertNotNull(journals.get(0).getPublishDate());
	}

	@Test
	public void browseUnSubscribedUser() {
		List<Journal> journals = journalService.listAll(getUser("user2"));
		assertEquals(0, journals.size());
	}

	@Test
	public void listPublisher() {
		User user = getUser("publisher1");
		Optional<Publisher> p = publisherRepository.findByUser(user);
		List<Journal> journals = journalService.publisherList(p.get());
		assertEquals(2, journals.size());

		assertEquals(new Long(1), journals.get(0).getId());
		assertEquals(new Long(2), journals.get(1).getId());

		assertEquals("Medicine", journals.get(0).getName());
		assertEquals("Test Journal", journals.get(1).getName());
		journals.stream().forEach(j -> assertNotNull(j.getPublishDate()));
		journals.stream().forEach(j -> assertEquals(new Long(1), j.getPublisher().getId()));

	}

	@Test(expected = ServiceException.class)
	public void publishFail() throws ServiceException {
		User user = getUser("publisher2");
		Optional<Publisher> p = publisherRepository.findByUser(user);

		Journal journal = new Journal();
		journal.setName("New Journal");

		journalService.publish(p.get(), journal, 1L);
	}

	@Test(expected = ServiceException.class)
	public void publishFail2() throws ServiceException {
		User user = getUser("publisher2");
		Optional<Publisher> p = publisherRepository.findByUser(user);

		Journal journal = new Journal();
		journal.setName("New Journal");

		journalService.publish(p.get(), journal, 150L);
	}
	
	private GreenMail greenMail;
	
	@Test()
	public void publishSuccess() {
		//prepare test SMPT server assuming that publish action will execute Email delivery 
		greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
        
		User user = getUser("publisher2");
		Optional<Publisher> p = publisherRepository.findByUser(user);

		Journal journal = new Journal();
		journal.setName(NEW_JOURNAL_NAME);
		journal.setUuid("SOME_EXTERNAL_ID");
		try {
			journalService.publish(p.get(), journal, 3L);
		} catch (ServiceException e) {
			fail(e.getMessage());
		}
		greenMail.stop();
		
		List<Journal> journals = journalService.listAll(getUser("user1"));
		assertEquals(2, journals.size());

		journals = journalService.publisherList(p.get());
		assertEquals(2, journals.size());
		assertEquals(new Long(3), journals.get(0).getId());
		assertEquals(new Long(4), journals.get(1).getId());
		assertEquals("Health", journals.get(0).getName());
		assertEquals(NEW_JOURNAL_NAME, journals.get(1).getName());
		journals.stream().forEach(j -> assertNotNull(j.getPublishDate()));
		journals.stream().forEach(j -> assertEquals(new Long(2), j.getPublisher().getId()));
	}

	@Test(expected = ServiceException.class)
	public void unPublishFail() {
		User user = getUser("publisher1");
		Optional<Publisher> p = publisherRepository.findByUser(user);
		journalService.unPublish(p.get(), 4L);
	}

	@Test(expected = ServiceException.class)
	public void unPublishFail2() {
		User user = getUser("publisher1");
		Optional<Publisher> p = publisherRepository.findByUser(user);
		journalService.unPublish(p.get(), 100L);
	}

	@Test
	public void unPublishSuccess() {
		User user = getUser("publisher2");
		Optional<Publisher> p = publisherRepository.findByUser(user);
		journalService.unPublish(p.get(), 4L);

		List<Journal> journals = journalService.publisherList(p.get());
		assertEquals(1, journals.size());
		journals = journalService.listAll(getUser("user1"));
		assertEquals(1, journals.size());
	}
	
	@Test
	public void zgetNewByCategory() {
		User user = getUser("publisher1");
		Optional<Publisher> p = publisherRepository.findByUser(user);
		Category cat = categoryRepository.findOne(2L);
		journalRepository.save(createNewJournal(p.get(), cat));
		assertEquals(1, journalService.getNewByCategory(cat.getId()).size());
	}
	
	private Journal createNewJournal(Publisher p, Category c){
		Journal journal = new Journal();
		journal.setPublisher(p);
		journal.setCategory(c);
		journal.setUuid("test-external-id " + c.getId());
		journal.setName("test journal " + c.getName());
		return journal;
	}
	
	protected User getUser(String name) {
		Optional<User> user = userService.getUserByLoginName(name);
		if (!user.isPresent()) {
			fail("user1 doesn't exist");
		}
		return user.get();
	}

}
