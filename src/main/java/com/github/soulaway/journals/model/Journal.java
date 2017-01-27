package com.github.soulaway.journals.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

@Entity
public class Journal implements Serializable {

	private static final long serialVersionUID = Journal.class.getName().hashCode();

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Date publishDate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "publisher_id")
	private Publisher publisher;

	@Column(nullable = false)
	private String uuid; // external id

	@ManyToOne(optional = false)
	@JoinColumn(name = "category_id")
	private Category category;

	@PrePersist
	void onPersist() {
		this.publishDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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
		Journal j = (Journal) o;

		return true && Objects.equals(id, j.getId()) && Objects.equals(name, j.getName())
				&& Objects.equals(publishDate, j.getPublishDate()) && Objects.equals(publisher, j.getPublisher())
				&& Objects.equals(uuid, j.getUuid()) && Objects.equals(category, j.getCategory());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, publishDate, publisher, uuid, category);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Journal.class.getSimpleName().toLowerCase()).append(": {\n");
		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    publishDate: ").append(toIndentedString(publishDate)).append("\n");
		sb.append("    ").append(toIndentedString(publisher)).append("\n");
		sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
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
