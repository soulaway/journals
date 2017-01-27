package com.github.soulaway.journals.test.security;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.github.soulaway.journals.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class })
public class ApplicationAuthorisationTest {
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private static GrantedAuthority pubAuth = new GrantedAuthority() {
		private static final long serialVersionUID = 5561846412184995657L;

		@Override
		public String getAuthority() {
			return "PUBLISHER";
		}
	};

	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity()).build();
	}

	@Test
	public void getLoginAsPublic() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("/login")));
	}

	@Test
	public void getLoginAsSubscriber() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/")
				.with(SecurityMockMvcRequestPostProcessors.user("user1").password("user1")))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("/journals")));
	}

	@Test
	public void getLoginAsPublisher() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/")
				.with(SecurityMockMvcRequestPostProcessors.user("publisher1").password("publisher1")
						.authorities(pubAuth)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("/publisher/browse")));
	}

	/**
	 * Tries to login with nonexistent user and got 302 redirection to URL
	 * /login?error
	 * 
	 * @throws Exception
	 */

	@Test
	public void getLoginUnauth() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/login")
				.with(SecurityMockMvcRequestPostProcessors.user("unauthorized").password("NA")))
				.andExpect(MockMvcResultMatchers.status().isFound())
				.andExpect(MockMvcResultMatchers.header().string("Location", Matchers.containsString("/login?error")));
	}

	@Test
	public void logout() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login?logout")).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(
						MockMvcResultMatchers.content().string(Matchers.containsString("You have been logged out.")));
	}

}
