package com.github.soulaway.journals.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.soulaway.journals.model.Category;
import com.github.soulaway.journals.model.Journal;
import com.github.soulaway.journals.model.Subscription;
import com.github.soulaway.journals.model.User;
import com.github.soulaway.journals.repository.JournalRepository;
import com.github.soulaway.journals.repository.UserRepository;
import com.github.soulaway.journals.service.CurrentUser;
import com.github.soulaway.journals.service.ServiceException;

@Controller
public class JournalController {

	@Autowired
	private JournalRepository journalRepository;

	@Autowired
	private UserRepository userRepository;

	@ResponseBody
	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET, produces = "application/pdf")
	public ResponseEntity<byte[]> renderDocument(@AuthenticationPrincipal Principal principal, @PathVariable("id") Long id) throws IOException{
		Journal journal = journalRepository.findOne(id);
		Category category = journal.getCategory();
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		User user = userRepository.findOne(activeUser.getUser().getId());
		List<Subscription> subscriptions = user.getSubscriptions();
		Optional<Subscription> subscription = subscriptions.stream()
				.filter(s -> s.getCategory().getId().equals(category.getId())).findFirst();
		if (subscription.isPresent() || journal.getPublisher().getId().equals(user.getId())) {
			File file = new File(PublisherController.getFileName(journal.getPublisher().getId(), journal.getUuid()));
			InputStream in = new FileInputStream(file);
			return ResponseEntity.ok(IOUtils.toByteArray(in));
		} else {
			throw new ServiceException("unsubscribed access, or user isn't a publisher");
		}

	}
}
