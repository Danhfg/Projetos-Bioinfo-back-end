package br.ufrn.imd.bioinfo.projetos.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ufrn.imd.bioinfo.projetos.models.NsSNV;
import br.ufrn.imd.bioinfo.projetos.repository.NsSNVRepository;

@Service
public class NsSNVService {
	
	private final NsSNVRepository nsSNVRepository;
	private final String infile;

	@Autowired
	public NsSNVService(NsSNVRepository nsSNVRepository) {
		super();
		this.nsSNVRepository = nsSNVRepository;
		this.infile = "C:\\Users\\Daniel\\Downloads\\dbNSFP4.0c (1)\\dbNSFP4.0c_variant.chr";
	}

	public String decisionTree(NsSNV nsSNV) {
		try {
			GZIPInputStream in = new GZIPInputStream(new FileInputStream(this.infile + nsSNV.getChr() + ".gz"));

			Reader decoder = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(decoder);

			String line = br.readLine();
			
			if(nsSNV.getAaalt() == null && nsSNV.getAaref() == null) {
				while ((line = br.readLine()) != null) {
					String[] collumns = line.split("\t");
					if(collumns[1].contentEquals(Integer.toString(nsSNV.getPos())) && collumns[2].contentEquals(nsSNV.getRef()) &&
						collumns[3].contentEquals(nsSNV.getAlt())) {
						return("Sift: " + collumns[38] + "\nSift4G: " + collumns[41] + "\nPROVEAN: " + collumns[59] + "\n");
					}	
				}
			}
			else if(!nsSNV.getAaalt().isBlank() && !nsSNV.getAaref().isBlank()) {
				
			}
			else {
				return ("Aaalt ou Aaref em branco");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		
		
		return "A";
	}
	
	

}
