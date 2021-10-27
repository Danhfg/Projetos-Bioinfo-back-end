package br.ufrn.imd.bioinfo.projetos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import br.ufrn.imd.bioinfo.projetos.repository.UserRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepository;
	
	
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		br.ufrn.imd.bioinfo.projetos.models.User user = Optional.ofNullable(userRepository.findByUsername(username))
				.orElseThrow(() -> new UsernameNotFoundException("usuario não encontrado")); 
		List<GrantedAuthority> listuser = AuthorityUtils.createAuthorityList("ROLE_" + user.getTipo_usuario().getNome());
		return new User(user.getUsername(), user.getPassword(), listuser);
	}

}
