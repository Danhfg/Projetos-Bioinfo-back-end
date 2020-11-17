package br.ufrn.imd.bioinfo.projetos.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class UserValidator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUserValidator;

    @ManyToOne
    //@NotEmpty
    @JoinColumn(name = "id_user")
    private User user;

    @Column(columnDefinition = "TEXT", unique = true)
    private String code;

	public Long getIdUserValidator() {
		return idUserValidator;
	}

	public void setIdUserValidator(Long idUserValidator) {
		this.idUserValidator = idUserValidator;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
