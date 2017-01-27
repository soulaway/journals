package com.github.soulaway.journals.test.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.soulaway.journals.Application;
import com.github.soulaway.journals.repository.UserRepository;
import com.github.soulaway.journals.service.CurrentUser;

// TODO improove expectations

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
public class JournalRestControllerTest {

	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Test
	public void getBrowse() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/rest/journals").with(SecurityMockMvcRequestPostProcessors.user(createPubliserUserDetails())))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void getPublishedList() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/rest/journals/published")
			.with(SecurityMockMvcRequestPostProcessors.user(createPubliserUserDetails())))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Test
	public void deleteUnPublishTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/rest/journals/unPublish/1")
			.with(SecurityMockMvcRequestPostProcessors.user(createPubliserUserDetails())))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Test
	public void getUserSubscriptionsTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/rest/journals/subscriptions")
			.with(SecurityMockMvcRequestPostProcessors.user(createPubliserUserDetails())))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	@Test
	public void postSubscribeTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/rest/journals/subscribe/1")
			.with(SecurityMockMvcRequestPostProcessors.user(createPubliserUserDetails())))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	protected CurrentUser createPubliserUserDetails() {
		CurrentUser cu = new CurrentUser(userRepository.findByLoginName("publisher1"));
		return cu;
	}
}
