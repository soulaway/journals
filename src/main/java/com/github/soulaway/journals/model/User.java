package com.github.soulaway.journals.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "user")
public class User implements Serializable{

	private static final long serialVersionUID = User.class.getName().hashCode();

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;

	@Column(nullable = false)
	private String loginName;

	@Column(nullable = false)
	private String pwd;

	@Column(nullable = false)
	private Boolean enabled;

	@Column(name = "role", nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	@Cascade(CascadeType.ALL)
	private List<Subscription> subscriptions;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}
	
    @Override
    public boolean equals(java.lang.Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;

        return true && Objects.equals(id, user.getId()) &&
                Objects.equals(loginName, user.getLoginName());
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, loginName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
		sb.append(User.class.getSimpleName().toLowerCase()).append(": {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    loginName: ").append(toIndentedString(loginName)).append("\n");
        sb.append("}");
        return sb.toString();
    }
    
    private String toIndentedString(java.lang.Object o) {
        if(o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
