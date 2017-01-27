package com.github.soulaway.journals.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

@Entity(name = "subscription")
public class Subscription implements Serializable {

	private static final long serialVersionUID = Subscription.class.getName().hashCode();

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(optional = false)
	private User user;

	@Column(nullable = false)
	private Date date;

	@ManyToOne(optional = false)
	private Category category;

	@PrePersist
	private void onPersist() {
		date = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Subscription s = (Subscription) o;

		return true && Objects.equals(id, s.getId()) && Objects.equals(user, s.getUser())
				&& Objects.equals(date, s.getDate()) && Objects.equals(category, s.getCategory());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, user, date, category);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Subscription.class.getSimpleName().toLowerCase()).append(": {\n");
		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    ").append(toIndentedString(user)).append("\n");
		sb.append("    date: ").append(toIndentedString(date)).append("\n");
		sb.append("    ").append(toIndentedString(category)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
