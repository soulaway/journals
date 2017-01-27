package com.github.soulaway.journals.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Publisher implements Serializable {

	private static final long serialVersionUID = Publisher.class.getName().hashCode();

	@Id
	@GeneratedValue
	private Long id;

	@OneToOne(optional = false)
	@JsonIgnore
	private User user;

	@Column(nullable = false)
	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Publisher p = (Publisher) o;

		return true && Objects.equals(id, p.getId()) && Objects.equals(user, p.getUser())
				&& Objects.equals(name, p.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, user, name);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Publisher.class.getSimpleName().toLowerCase()).append(": {\n");
		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    ").append(toIndentedString(user)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
