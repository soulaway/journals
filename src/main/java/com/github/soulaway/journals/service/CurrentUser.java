package com.github.soulaway.journals.service;

import org.springframework.security.core.authority.AuthorityUtils;

import com.github.soulaway.journals.model.Role;
import com.github.soulaway.journals.model.User;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = CurrentUser.class.getName().hashCode();
	private User user;

	public CurrentUser(User user) {
		super(user.getLoginName(), user.getPwd(), AuthorityUtils.createAuthorityList(user.getRole().toString()));
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public Long getId() {
		return user.getId();
	}

	public Role getRole() {
		return user.getRole();
	}

}