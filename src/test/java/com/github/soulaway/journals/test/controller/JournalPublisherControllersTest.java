package com.github.soulaway.journals.test.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.soulaway.journals.Application;
import com.github.soulaway.journals.repository.JournalRepository;
import com.github.soulaway.journals.repository.UserRepository;
import com.github.soulaway.journals.service.CurrentUser;
import com.github.soulaway.journals.test.security.ApplicationAuthorisationTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
public class JournalPublisherControllersTest {
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Resource
	private JournalRepository journalRepository;	
	
	@Autowired
	private UserRepository userRepository;
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}
	@Test
	public void postPublishTest() throws Exception {
		InputStream is = ApplicationAuthorisationTest.class.getClassLoader().getResourceAsStream("default-testpage.pdf");		
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[32768];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}
		buffer.flush();
		byte[] content = buffer.toByteArray();
		MockMultipartFile fstmp = new MockMultipartFile("file", "default-testpage.pdf", "multipart/form-data", content);
		long journalsCount = journalRepository.count();
		mockMvc.perform(MockMvcRequestBuilders.fileUpload("/publisher/publish").file(fstmp)
				.param("name", "New Journal")
				.param("category", "4")
				.with(SecurityMockMvcRequestPostProcessors.user(createPubliserUserDetails("publisher2"))))
				/*.andDo(MockMvcResultHandlers.print())*/
				.andExpect(MockMvcResultMatchers.status().is3xxRedirection())
				.andExpect(MockMvcResultMatchers.redirectedUrl("/publisher/browse"));
		Assert.assertEquals(journalsCount + 1, journalRepository.count());
	}
	
	//TODO 
	/*@Test
	public void getViewJournal() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/view/3")
				.with(SecurityMockMvcRequestPostProcessors.user(createPubliserUserDetails("publisher2"))))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk());
	}*/
	
	protected CurrentUser createPubliserUserDetails(String userName) {
		CurrentUser cu = new CurrentUser(userRepository.findByLoginName(userName));
		return cu;
	}
}
