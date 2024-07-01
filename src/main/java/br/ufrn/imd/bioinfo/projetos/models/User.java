package br.ufrn.imd.bioinfo.projetos.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
    private Long idUser;
    
	@Column(columnDefinition = "VARCHAR(100)", unique = true)
	@NotEmpty(message = "{email.not.blank}")
	//@Email(message = "{email.not.valid}")
    private String username;

	@Column
	@NotEmpty(message = "{senha.not.blank}")
	@JsonIgnore
	@ApiModelProperty(
			  value = "Senha do usuário",
			  dataType = "String",
			  example = "senha123")
    private String password;

	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	@JsonIgnore
	private boolean validated;

	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	@JsonIgnore
	private boolean activaded;

	@ManyToOne
	//@NotEmpty
	@JsonIgnore
	@JoinColumn(name = "id_tipo_usuario")
	private Tipo_Usuario tipo_usuario;

	@Column(columnDefinition = "VARCHAR(100)")
	//@NotEmpty(message = "{nome.not.blank}")
	@ApiModelProperty(
	  value = "Nome da pesoa",
	  dataType = "String",
	  example = "Daniel Henrique Ferreira Gomes")
	private String name;

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

	public boolean isActivaded() {
		return activaded;
	}

	public void setActivaded(boolean activaded) {
		this.activaded = activaded;
	}

	public Tipo_Usuario getTipo_usuario() {
		return tipo_usuario;
	}

	public void setTipo_usuario(Tipo_Usuario tipo_usuario) {
		this.tipo_usuario = tipo_usuario;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name= name;
	}
	
}
