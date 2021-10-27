package br.ufrn.imd.bioinfo.projetos.models;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;

public class UserDTO {
    
	@Column(columnDefinition = "VARCHAR(100)", unique = true)
	@NotEmpty(message = "{email.not.blank}")
	@Email(message = "{email.not.valid}")
    private String username;

	@Column
	@NotEmpty(message = "{senha.not.blank}")
	@ApiModelProperty(
			  value = "Senha do usuário",
			  dataType = "String",
			  example = "senha123")
    private String password;
	
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
	
}
