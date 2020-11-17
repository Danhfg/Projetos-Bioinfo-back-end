package br.ufrn.imd.bioinfo.projetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufrn.imd.bioinfo.projetos.models.User;
import br.ufrn.imd.bioinfo.projetos.models.UserValidator;

public interface UserValidatorRepository extends JpaRepository<UserValidator, Long> {
	UserValidator findByCode(String uuid);
    
	UserValidator findByUser(User usuario);

}
