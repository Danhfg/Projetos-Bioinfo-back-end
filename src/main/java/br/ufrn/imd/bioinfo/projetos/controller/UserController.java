package br.ufrn.imd.bioinfo.projetos.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.bioinfo.projetos.models.UserDTO;
import br.ufrn.imd.bioinfo.projetos.service.UserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://danhfg.github.io/#/")
public class UserController {
	
	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

    @PostMapping("/sign-in/")
	public String login(@Valid @RequestBody UserDTO user) {
		return userService.signin(user.getUsername(), user.getPassword());
	}

    @PostMapping(value="/sign-up/")
    public ResponseEntity<?> saveUsuarios(@Valid @RequestBody UserDTO user){
    	return userService.singup(user);
    }

    @GetMapping("/user/list")
	@Secured({"ROLE_admin"})
	@ApiOperation(value = "Retorna todos os usuários do sistema.")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
	public ResponseEntity<?> listUsers(Pageable pageable) {
		return new ResponseEntity<>(userService.list(pageable), HttpStatus.OK);
	}

    @GetMapping("/user")
	@ApiOperation(value = "Retorna todos os usuários do sistema.")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
	public ResponseEntity<?> listUser(HttpServletRequest req) {
		return new ResponseEntity<>(userService.listuser(req), HttpStatus.OK);
	}


    @PostMapping("/user/activate/{id}")
	@Secured({"ROLE_admin"})
	@ApiOperation(value = "Ativa um usuário do sistema.")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
	public ResponseEntity<?> activate(@ApiParam(value = "Id do usuário") @PathVariable Long id) {
		userService.activate(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
