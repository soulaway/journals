package com.github.soulaway.journals.test.aservice;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.soulaway.journals.Application;
import com.github.soulaway.journals.model.Category;
import com.github.soulaway.journals.model.Journal;
import com.github.soulaway.journals.model.Publisher;
import com.github.soulaway.journals.model.User;
import com.github.soulaway.journals.repository.CategoryRepository;
import com.github.soulaway.journals.repository.PublisherRepository;
import com.github.soulaway.journals.service.JournalService;
import com.github.soulaway.journals.service.MailService;
import com.github.soulaway.journals.service.UserService;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
public class MailServiceTest {
	
	@Resource
	private MailService mailService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private JournalService journalService;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private PublisherRepository publisherRepository;

	private GreenMail greenMail;

    @Before
    public void startMailServer() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }
    
    @After
    public void stopMailServer() {
        greenMail.stop();
    }
    
	@Test
	public void sendNotifySubscribers() throws MessagingException, IOException {
		User user = getUser("publisher1");
		Optional<Publisher> p = publisherRepository.findByUser(user);
		List<Journal> digest = journalService.publisherList(p.get());
		if (!digest.isEmpty()){
			mailService.notifySubscribers(1L, digest.get(digest.size() - 1));
			boolean timeoutIsReached = greenMail.waitForIncomingEmail(5000, 1);
			if (!timeoutIsReached){
		        MimeMessage[] messages = greenMail.getReceivedMessages();
		        Assert.assertEquals(2, messages.length);
		        Assert.assertTrue(messages[0].getContent().toString().contains("New Journal")); 
			}
		}
	}

	protected User getUser(String name) {
		Optional<User> user = userService.getUserByLoginName(name);
		if (!user.isPresent()) {
			Assert.fail("user " + name + " doesn't exist");
		}
		return user.get();
	}
	
	protected Category getCat(Long catId){
		Category cat = categoryRepository.findOne(catId);
		if (cat == null) {
			Assert.fail("category " + catId + " doesn't exist");
		}
		return cat;
	}
}
