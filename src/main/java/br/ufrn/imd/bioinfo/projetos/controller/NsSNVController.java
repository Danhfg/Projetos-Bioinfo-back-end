package br.ufrn.imd.bioinfo.projetos.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.bioinfo.projetos.models.NsSNV;
import br.ufrn.imd.bioinfo.projetos.service.NsSNVService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/api")
public class NsSNVController {
	
	private final NsSNVService nsSNVService;
	
	@Autowired
	public NsSNVController(NsSNVService nsSNVService) {
		this.nsSNVService = nsSNVService;
	}

	@GetMapping(value = "/predict/decisionTree/{id}")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
	@Secured({"ROLE_free", "ROLE_admin"})
	public ResponseEntity<?> decisionTree(@ApiParam(value = "Titulo da atividade a ser solicitada") @PathVariable Long id, HttpServletRequest req) {
		String result = nsSNVService.decisionTree(req, id);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping(value = "/predict/processPrediction")
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
	@Secured({"ROLE_free", "ROLE_admin"})
	public ResponseEntity<?> processPredictionPost(HttpServletRequest req, @Valid @RequestBody NsSNV nsSNV) {
		try {
			nsSNVService.processPrediction(req, nsSNV);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (Exception e) {
			throw e;
		}
	}

	@GetMapping(value = "/predict/results")
	@Secured({"ROLE_free", "ROLE_admin"})
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
	public ResponseEntity<?> results(HttpServletRequest req) {
		try {
			List<NsSNV> list = nsSNVService.getAllResult(req);
			return new ResponseEntity<>(list, HttpStatus.OK);
		}
		catch (Exception e) {
			throw e;
		}
	}

	@GetMapping(value = "/predict/allPretictiors/{id}")
	@Secured({"ROLE_free", "ROLE_admin"})
	@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, paramType = "header", example = "Bearer access_token")
	public ResponseEntity<?> allPretictiors(HttpServletRequest req, @PathVariable Long id) {
		String result = nsSNVService.allPretictiors(req, id);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}


}
