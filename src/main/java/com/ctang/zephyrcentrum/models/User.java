package com.ctang.zephyrcentrum.models;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import com.ctang.zephyrcentrum.types.Roles;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Colby Tang
 */
@Entity
@Table(name = "users", schema = "system")
@Getter @Setter @NoArgsConstructor
public class User {
	@Id
	@Access(AccessType.FIELD)
	@Column(name="id", updatable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
    @Column(name="username")
    private String username;

	@NotNull
	@Email
    @Column(name="email")
    private String email;

	@Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Roles role = Roles.USER;

    @Column(name = "password_hash")
    private String passwordHash;

	@Transient // Not persisted to the database
    private String plainPassword; 

	@CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hash(email, passwordHash, id, username);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email) && Objects.equals(passwordHash, other.passwordHash)
				&& Objects.equals(id, other.id)
				&& Objects.equals(username, other.username);
	}

	/** 
	 * @return String
	 */
	@Override
	public String toString() {
		String retString = "Id=%d, Username=%s, Email=%s, PasswordHash=%s, CreatedDate=%s";
        return String.format(retString, getId(), getUsername(), getEmail(), getPasswordHash(), getCreatedDate());
	}
}
