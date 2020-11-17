package br.ufrn.imd.bioinfo.projetos.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.ufrn.imd.bioinfo.projetos.models.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long>{
	User findByUsername(String username);
}
