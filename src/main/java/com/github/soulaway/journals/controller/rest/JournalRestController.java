package com.github.soulaway.journals.controller.rest;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.soulaway.journals.dto.SubscriptionDTO;
import com.github.soulaway.journals.model.Category;
import com.github.soulaway.journals.model.Journal;
import com.github.soulaway.journals.model.Publisher;
import com.github.soulaway.journals.model.Subscription;
import com.github.soulaway.journals.model.User;
import com.github.soulaway.journals.repository.CategoryRepository;
import com.github.soulaway.journals.repository.PublisherRepository;
import com.github.soulaway.journals.service.CurrentUser;
import com.github.soulaway.journals.service.JournalService;
import com.github.soulaway.journals.service.UserService;

@RestController
@RequestMapping("/rest/sessions")
public class JournalRestController {

	@Autowired
	private PublisherRepository publisherRepository;

	@Autowired
	private JournalService journalService;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryRepository categoryRepository;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Object> browse(@AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		return ResponseEntity.ok(journalService.listAll(activeUser.getUser()));
	}

	@RequestMapping(value = "/active", method = RequestMethod.GET)
	public List<Journal> publishedList(@AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		Optional<Publisher> publisher = publisherRepository.findByUser(activeUser.getUser());
		return journalService.publisherList(publisher.get());
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public void unPublish(@PathVariable("id") Long id, @AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		Optional<Publisher> publisher = publisherRepository.findByUser(activeUser.getUser());
		journalService.unPublish(publisher.get(), id);
	}

	@RequestMapping(value = "/assignments", method = RequestMethod.GET)
	public List<SubscriptionDTO> getUserSubscriptions(@AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		User persistedUser = userService.findById(activeUser.getId());
		List<Subscription> subscriptions = persistedUser.getSubscriptions();
		List<Category> categories = categoryRepository.findAll();
		List<SubscriptionDTO> subscriptionDTOs = new ArrayList<>(categories.size());
		categories.stream().forEach(c -> {
			SubscriptionDTO subscr = new SubscriptionDTO(c);
			Optional<Subscription> subscription = subscriptions.stream().filter(s -> s.getCategory().getId().equals(c.getId())).findFirst();
			subscr.setActive(subscription.isPresent());
			subscriptionDTOs.add(subscr);
		});
		return subscriptionDTOs;
	}

	@RequestMapping(value = "/assign/{exchangeId}", method = RequestMethod.POST)
	public void subscribe(@PathVariable("exchangeId") Long categoryId, @AuthenticationPrincipal Principal principal) {
		CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
		User user = userService.findById(activeUser.getUser().getId());
		userService.subscribe(user, categoryId);
	}
}
