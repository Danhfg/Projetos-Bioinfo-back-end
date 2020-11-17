package br.ufrn.imd.bioinfo.projetos.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
    private Long idUser;
    
	@Column(columnDefinition = "VARCHAR(100)", unique = true)
	@NotEmpty(message = "{email.not.blank}")
	@Email(message = "{email.not.valid}")
    private String username;

	@Column
	@NotEmpty(message = "{senha.not.blank}")
    private String password;

	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	@JsonIgnore
	private boolean validated;

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	
}
