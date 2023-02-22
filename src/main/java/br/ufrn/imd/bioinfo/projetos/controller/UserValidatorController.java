package br.ufrn.imd.bioinfo.projetos.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import br.ufrn.imd.bioinfo.projetos.service.UserValidatorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value="/api")
@CrossOrigin(origins = "https://danhfg.github.io/", maxAge = 3600)
public class UserValidatorController {

    private UserValidatorService userValidatorServiceService;

	public UserValidatorController(UserValidatorService userValidatorServiceService) {
		this.userValidatorServiceService = userValidatorServiceService;
	}

    @GetMapping(value = "/validation/")
    @ApiOperation(value = "Método que valida a conta de um usuário.")
    @CrossOrigin
    public RedirectView validador(@ApiParam(value = "Código a ser validádo") @RequestParam String code){
        userValidatorServiceService.validate(code);
        RedirectView rv = new RedirectView();
        rv.setUrl("http://bioinfo.imd.ufrn.br/dtree_webapp/");
        return rv;
    }

}
