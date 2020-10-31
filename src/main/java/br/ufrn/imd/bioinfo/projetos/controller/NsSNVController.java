package br.ufrn.imd.bioinfo.projetos.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping(value = "/predict/decisionTree")
	public ResponseEntity<?> decisionTree(@Valid @RequestBody NsSNV nsSNV) {
		String result = nsSNVService.decisionTree(nsSNV);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

}
