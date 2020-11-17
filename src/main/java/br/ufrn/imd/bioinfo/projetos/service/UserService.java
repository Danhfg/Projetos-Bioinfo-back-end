package br.ufrn.imd.bioinfo.projetos.service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.ufrn.imd.bioinfo.projetos.error.CustomException;
import br.ufrn.imd.bioinfo.projetos.error.DuplicatedEntryException;
import br.ufrn.imd.bioinfo.projetos.models.User;
import br.ufrn.imd.bioinfo.projetos.models.UserValidator;
import br.ufrn.imd.bioinfo.projetos.repository.UserRepository;
import br.ufrn.imd.bioinfo.projetos.repository.UserValidatorRepository;
import br.ufrn.imd.bioinfo.projetos.security.JwtTokenProvider;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final UserValidatorRepository userValidatorRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	public UserService(UserRepository userRepository, UserValidatorRepository userValidatorRepository,
			AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
		super();
		this.userRepository = userRepository;
		this.userValidatorRepository = userValidatorRepository;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}


	public String signin(String username, String password) {
		try{
			User user = userRepository.findByUsername(username);
			if(user == null) {
				throw new ResourceNotFoundException("User not found");
			}
			if(user.isValidated()) {
				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
				return "Bearer " + jwtTokenProvider.createToken(username);
			} else {
				UserValidator userValidator = userValidatorRepository.findByUser(user);
				//sendEmail(validadorUsuario, username);
				throw new CustomException("Usuário nao validado, foi reenviado o email de confirmação", HttpStatus.UNPROCESSABLE_ENTITY);
			}
		}catch (AuthenticationException e) {
			throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}


	public ResponseEntity<?> singup(User user) {
		User findUser = userRepository.findByUsername(user.getUsername());
		if (findUser == null) {
			user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
			//saveUser.setValidated(true);
			user.setValidated(false);
			User savedUser = userRepository.save(user);
			UserValidator userValidator = new UserValidator();
			userValidator.setUser(savedUser);
			userValidator.setCode(generateCode());
			userValidatorRepository.save(userValidator);
			//enviarEmail(validadorUsuario, pessoa);
		}else {
			throw new DuplicatedEntryException("Duplicated Email");
		}
		
		return null;
	}
	
	public String generateCode(){
		SecureRandom secureRandom = new SecureRandom(); //threadsafe
		Base64.Encoder base64Encoder = Base64.getUrlEncoder();
		byte[] randomBytes = new byte[24];
		secureRandom.nextBytes(randomBytes);
		return base64Encoder.encodeToString(randomBytes);
	}

}
