package br.ufrn.imd.bioinfo.projetos.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.ufrn.imd.bioinfo.projetos.error.CustomException;
import br.ufrn.imd.bioinfo.projetos.error.ResourceNotFoundException;
import br.ufrn.imd.bioinfo.projetos.models.User;
import br.ufrn.imd.bioinfo.projetos.models.UserValidator;
import br.ufrn.imd.bioinfo.projetos.repository.UserRepository;
import br.ufrn.imd.bioinfo.projetos.repository.UserValidatorRepository;

@Service
public class UserValidatorService {
    private final UserValidatorRepository userValidatorRepository;
    private final UserRepository userRepository;
    
	public UserValidatorService(UserValidatorRepository userValidatorRepository, UserRepository userRepository) {
		this.userValidatorRepository = userValidatorRepository;
		this.userRepository = userRepository;
	}

    public void validate(String code){
        UserValidator userValidator = userValidatorRepository.findByCode(code);
        if(userValidator!= null){
            User user = userRepository.findById(userValidator.getUser().getIdUser()).get();
            if(user != null){
                user.setValidated(true);
                userRepository.save(user);
                userValidatorRepository.delete(userValidator);
            } else {
                throw new ResourceNotFoundException("Usuario nao encontrado!");
            }
        } else {
            throw new CustomException("Código inválido ou inexistente", HttpStatus.BAD_REQUEST);
        }
    }
    

}
