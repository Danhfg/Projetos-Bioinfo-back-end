package br.ufrn.imd.bioinfo.projetos.service;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.ufrn.imd.bioinfo.projetos.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

	private final UserRepository userRepository;
	
	
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		br.ufrn.imd.bioinfo.projetos.models.User user =  userRepository.findByUsername(username);
		List<GrantedAuthority> listuser = AuthorityUtils.createAuthorityList("ROLE_Free");
		return new User(user.getUsername(), user.getPassword(), listuser);
	}

}
