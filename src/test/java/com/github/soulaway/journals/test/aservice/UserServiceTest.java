package com.github.soulaway.journals.test.aservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.soulaway.journals.Application;
import com.github.soulaway.journals.model.Category;
import com.github.soulaway.journals.model.User;
import com.github.soulaway.journals.repository.CategoryRepository;
import com.github.soulaway.journals.service.ServiceException;
import com.github.soulaway.journals.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
public class UserServiceTest {

	@Resource
	private UserService userService;

	@Autowired
	private CategoryRepository categoryRepository;
	
	private final String TEST_USER_NAME = "dgsoloviev.pub@gmail.com";
	
	@Test
	public void testUserSubscribe(){
		User user = getUser(TEST_USER_NAME);
		Category cat = getCat(2L);
		int subscribtionsCouont = user.getSubscriptions().size();
		userService.subscribe(user, cat.getId());
		User sameUser = getUser(TEST_USER_NAME);
		assertEquals(subscribtionsCouont + 1, sameUser.getSubscriptions().size());

	}
	
	@Test(expected = ServiceException.class)
	public void testUserSubscribeWrongCategory(){
		User user = getUser(TEST_USER_NAME);
		userService.subscribe(user, 0L);
	}
	
	@Test 
	public void testFindUserById() {
		User sameUser = getUser(TEST_USER_NAME);
		User user = userService.findById(sameUser.getId());
		assertEquals(user, sameUser);
	}
	
	protected User getUser(String name) {
		Optional<User> user = userService.getUserByLoginName(name);
		if (!user.isPresent()) {
			fail("user " + name + " doesn't exist");
		}
		return user.get();
	}
	
	protected Category getCat(Long catId){
		Category cat = categoryRepository.findOne(catId);
		if (cat == null) {
			fail("category " + catId + " doesn't exist");
		}
		return cat;
	}
}
