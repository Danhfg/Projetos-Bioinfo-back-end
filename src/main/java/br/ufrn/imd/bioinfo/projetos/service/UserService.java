package br.ufrn.imd.bioinfo.projetos.service;

import java.security.SecureRandom;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.ufrn.imd.bioinfo.projetos.error.CustomException;
import br.ufrn.imd.bioinfo.projetos.error.DuplicatedEntryException;
import br.ufrn.imd.bioinfo.projetos.models.Tipo_Usuario;
import br.ufrn.imd.bioinfo.projetos.models.User;
import br.ufrn.imd.bioinfo.projetos.models.UserDTO;
import br.ufrn.imd.bioinfo.projetos.models.UserValidator;
import br.ufrn.imd.bioinfo.projetos.repository.Tipo_UsuarioRepository;
import br.ufrn.imd.bioinfo.projetos.repository.UserRepository;
import br.ufrn.imd.bioinfo.projetos.repository.UserValidatorRepository;
import br.ufrn.imd.bioinfo.projetos.security.JwtTokenProvider;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final UserValidatorRepository userValidatorRepository;
	private final Tipo_UsuarioRepository tipoUsuarioRepository;
	private JavaMailSender javaMailSender;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	public UserService(UserRepository userRepository, UserValidatorRepository userValidatorRepository, Tipo_UsuarioRepository tipoUsuarioRepository,
			AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, JavaMailSender javaMailSender) {
		super();
		this.userRepository = userRepository;
		this.userValidatorRepository = userValidatorRepository;
		this.tipoUsuarioRepository = tipoUsuarioRepository;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
		this.javaMailSender = javaMailSender;
	}


	public String signin(String username, String password) {
		try{
			User user = userRepository.findByUsername(username);
			if(user == null) {
				throw new ResourceNotFoundException("User not found");
			}
			if(user.isValidated() && user.isActivaded()) {
				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
				return "Bearer " + jwtTokenProvider.createToken(username, userRepository.findByUsername(username).getTipo_usuario().getNome());
			} else if(!user.isValidated()) {
				UserValidator userValidator = userValidatorRepository.findByUser(user);
//				sendEmail(userValidator, username);
				throw new CustomException("User not validated!", HttpStatus.UNPROCESSABLE_ENTITY);
			}
			else {
				throw new CustomException("User not activated, contact our support!", HttpStatus.UNPROCESSABLE_ENTITY);
			}
		}catch (AuthenticationException e) {
			throw new CustomException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}


	public ResponseEntity<?> singup(UserDTO userdto) {
		User findUser = userRepository.findByUsername(userdto.getUsername());
		if (findUser == null) {
			User user = new User();
			user.setUsername(userdto.getUsername());
			user.setPassword(new BCryptPasswordEncoder().encode(userdto.getPassword()));
			user.setName(userdto.getName());
			//saveUser.setValidated(true);
			user.setValidated(true);
			user.setActivaded(true);
			Tipo_Usuario tipo_Usuario = tipoUsuarioRepository.findByNome("free");
			user.setTipo_usuario(tipo_Usuario);
			User savedUser = userRepository.save(user);
			
			System.out.println(savedUser.getName());
			
//			UserValidator userValidator = new UserValidator();
//			userValidator.setUser(savedUser);
//			userValidator.setCode(generateCode());
//			userValidatorRepository.save(userValidator);
//			sendEmail(userValidator, user.getUsername());
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
	
	public Page<User> list(Pageable pageable){
		return userRepository.findAll(pageable);
	}
	
	public User listuser(HttpServletRequest req){
		return userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
	}

	public void activate(Long id) {
		User user = userRepository.findById(id).isPresent() ? userRepository.findById(id).get() : null;
		if (user == null) {
			throw new ResourceNotFoundException("User not fouded");
		}
		else {
			user.setActivaded(true);
		}
	}

	public void sendEmail(UserValidator userValidator, String email){
		SimpleMailMessage smm = new SimpleMailMessage();
		smm.setTo(email);
		smm.setSubject("Email validation");
		smm.setText("Hello!\n"
				+ "To validate your account click on the following link: " + "https://bioinfo.imd.ufrn.br/daniel_backend/api/validation/?code=" + userValidator.getCode()
				+ "\nTo be able to use the system, you must contact our support at the following email: daniel.gomes.702@ufrn.edu.br ");
		try {
			javaMailSender.send(smm);
		} catch (Exception e) {
			throw new RuntimeException("Houve algum erro no envio do seu email!\n" + e);
		}
	}


	public void forgotPass(String email) {
		User usuario =  userRepository.findByUsername(email);
		if(usuario != null){
			UserValidator validadorUser = new UserValidator();
			validadorUser.setUser(usuario);
			validadorUser.setCode(generateCode());
			userValidatorRepository.save(validadorUser);
			sendEmail(validadorUser, email);
		}
		
	}

	/*public void sendEmail(UserValidator userValidator, String email){
		SimpleMailMessage smm = new SimpleMailMessage();
		smm.setTo(email);
		smm.setSubject("Password change request");
		smm.setText("Hello!\n"
				+ "To change your current password, open the following link: " + "https://bioinfo.imd.ufrn.br/dtree_app/?code=" + userValidator.getCode());
		try {
			javaMailSender.send(smm);
		} catch (Exception e) {
			throw new RuntimeException("Error!\n" + e);
		}
	}

	public void sendEmail(UserValidator userValidator, User user){
		SimpleMailMessage smm = new SimpleMailMessage();
		smm.setTo(user.getUsername());
		smm.setSubject("Email validation");
		smm.setText("Hello!\n"
				+ "To validate your account click on the following link: " + "https://bioinfo.imd.ufrn.br/daniel_back_end/?code=" + userValidator.getCode()
				+ "\nTo be able to use the system, you must contact our support at the following email: daniel.gomes.702@ufrn.edu.br ");
		try {
			javaMailSender.send(smm);
		} catch (Exception e) {
			throw new RuntimeException("Houve algum erro no envio do seu email!\n" + e);
		}
	}*/
}
