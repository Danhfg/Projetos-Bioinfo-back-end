package br.ufrn.imd.bioinfo.projetos.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.bioinfo.projetos.models.User;
import br.ufrn.imd.bioinfo.projetos.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
	
	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

    @PostMapping("/sign-in/")
	public String login(@Valid @RequestBody User user) {
		return userService.signin(user.getUsername(), user.getPassword());
	}

    @PostMapping(value="/sign-up/")
    public ResponseEntity<?> saveUsuarios(@Valid @RequestBody User user){
    	return userService.singup(user);
    }
}
