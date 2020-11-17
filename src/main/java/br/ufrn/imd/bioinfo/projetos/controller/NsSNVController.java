package br.ufrn.imd.bioinfo.projetos.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.bioinfo.projetos.error.CustomException;
import br.ufrn.imd.bioinfo.projetos.models.NsSNV;
import br.ufrn.imd.bioinfo.projetos.service.NsSNVService;

@RestController
@RequestMapping(value = "/api")
public class NsSNVController {
	
	private final NsSNVService nsSNVService;
	
	@Autowired
	public NsSNVController(NsSNVService nsSNVService) {
		this.nsSNVService = nsSNVService;
	}
/*
	@GetMapping(value = "/predict/decisionTree")
	public ResponseEntity<?> decisionTree(@Valid @RequestBody NsSNV nsSNV) {
		String result = nsSNVService.decisionTree(nsSNV);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
*/
	@PostMapping(value = "/predict/decisionTree")
	@Secured({"ROLE_Free"})
	public ResponseEntity<?> decisionTreePost(HttpServletRequest req, @Valid @RequestBody NsSNV nsSNV) {
		try {
			nsSNVService.decisionTree(req, nsSNV);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	@GetMapping(value = "/predict/result")
	@Secured({"ROLE_Free"})
	public ResponseEntity<?> result(HttpServletRequest req) {
		try {
			List<NsSNV> list = nsSNVService.getAllResult(req);
			return new ResponseEntity<>(list, HttpStatus.OK);
		}
		catch (Exception e) {
			throw e;
		}
	}
	

	@GetMapping(value = "/predict/allPretictiors")
	public ResponseEntity<?> allPretictiors(@Valid @RequestBody NsSNV nsSNV) {
		String result = nsSNVService.allPretictiors(nsSNV);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}


}
