package com.github.soulaway.journals.service;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.github.soulaway.journals.model.Category;
import com.github.soulaway.journals.model.Journal;
import com.github.soulaway.journals.model.Subscription;
import com.github.soulaway.journals.repository.CategoryRepository;
import com.github.soulaway.journals.repository.SubscribtionRepository;

@Service
public class MailServiceImpl implements MailService{
	private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);
	
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;
    
    @Value("${spring.mail.username}")
    private String username;
    
    @Value("${spring.mail.password}")
    private String password;
    
    @Autowired
    private JavaMailSender sender;
    
    @Autowired
    private JournalService journalService;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private SubscribtionRepository subscribtionRepository;   
    
    @Bean
    public Session getSession() {
        log.info("getSession TLS enabled");
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.starttls.enable", "true");
        p.setProperty("mail.smtp.host", host);
        p.setProperty("mail.smtp.port", port);
        return Session.getInstance(p, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
	   
	protected void send(String message, String address) {
		SimpleMailMessage smm = new SimpleMailMessage();
		smm.setText(message);
		smm.setTo(address);
       try {
    	   sender.send(smm);
        } catch (MailException ex) {
            // silencing about the SMTP transport exceptions
        	log.debug("Error sending mail message: " + smm.toString(), ex);
        }
	}

	@Override
	public void notifySubscribers(Long categoryId, Journal journal) {
		log.info("notifySubscribers categoryId " + categoryId + " journal " + journal.getId());
		Category cat = categoryRepository.findOne(categoryId);
		List<Subscription> subs = subscribtionRepository.findUserDistinctByCategory(cat);
		subs.stream().forEach(s -> {
			doProcessEmail(s, Arrays.asList(journal));
		});
	}

	@Override
	@Scheduled(fixedDelay = 86400000, initialDelay = 10000)
	public void scheduleDigestDelivery() {
		log.info("scheduleDigestDelivery");
		List<Category> cats = categoryRepository.findAll();
		cats.stream().forEach(c -> {
			List<Journal> digest = journalService.getNewByCategory(c.getId());
			List<Subscription> subs = subscribtionRepository.findUserDistinctByCategory(c);
			subs.stream().forEach(s -> {
				doProcessEmail(s, digest);
			});
		});
	}
	
	private void doProcessEmail(Subscription sub, List<Journal> digest){
		StringBuilder message = new StringBuilder();
		digest.forEach(d -> {
			message.append(String.format("New Journal %s was published by %s at %s. \n", d.getName(), 
					d.getPublisher().getName(), d.getPublishDate().toString()));
		});
		if (!digest.isEmpty()){
			send(message.toString(), sub.getUser().getLoginName());
		} else {
			log.debug("doProcessEmail: nothing to email, seems no digest for today");
		}
	}
}
