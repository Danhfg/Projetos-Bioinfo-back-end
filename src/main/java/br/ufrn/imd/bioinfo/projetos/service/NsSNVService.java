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
		this.infile = "C:\\Users\\Daniel\\Downloads\\dbNSFP4.1a\\dbNSFP4.1a_variant.chr";
	}
	
	private int nDamageCount(String[] collumns) {
		int nDamage = 0;
		if(collumns[50].contains("D") || collumns[50].contains("U")) nDamage++;
		if(collumns[54].contains("A") || collumns[54].contains("D")) nDamage++;
		if(collumns[59].contains("H") || collumns[59].contains("M")) nDamage++;
		if(collumns[62].contains("D")) nDamage++;
		if(collumns[38].contains("D")) nDamage++;
		if(collumns[44].contains("D") || collumns[44].contains("P")) nDamage++;
		if(collumns[47].contains("D") || collumns[47].contains("P")) nDamage++;
		if(!collumns[65].contains("N")) nDamage++;
		
		
		return nDamage;
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

						int nDamage = nDamageCount(collumns);
						return("Sift: " + collumns[38] + "\nSift4G: " + collumns[41] + "\nPROVEAN: " + collumns[59] +
								"\nPolyphen2_HDIV: " + collumns[44] + "\nPolyphen2_HVAR: " + collumns[47] +
								"\nExac: " + collumns[243] + "\nNDAMAGE: " + nDamage + "\n");
					}	
				}
			}
			else if(!nsSNV.getAaalt().isBlank() && !nsSNV.getAaref().isBlank()) {
				while ((line = br.readLine()) != null) {
					String[] collumns = line.split("\t");
					if(collumns[1].contentEquals(Integer.toString(nsSNV.getPos())) && collumns[2].contentEquals(nsSNV.getRef()) &&
						collumns[3].contentEquals(nsSNV.getAlt()) && collumns[4].contentEquals(nsSNV.getAaref()) &&
						collumns[5].contentEquals(nsSNV.getAaalt()) ) {
						return("Sift: " + collumns[38] + "\nSift4G: " + collumns[41] + "\nPROVEAN: " + collumns[59] +
								"\nPolyphen2_HDIV: " + collumns[44] + "\nPolyphen2_HVAR: " + collumns[47]);
					}	
				}
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
	
	public String allPretictiors(NsSNV nsSNV) {
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
						return("SIFT_pred: "  + collumns[38] + "\nSIFT4G_pred: "  + collumns[41] + "\nPolyphen2_HDIV_pred: "  + 
								collumns[44] + "\nPolyphen2_HVAR_pred: "  + collumns[47] + "\nLRT_pred: "  + collumns[50] + 
								"\nMutationTaster_pred: "  + collumns[54] + "\nMutationAssessor_pred: "  + collumns[59] + 
								"\nFATHMM_pred: "  + collumns[62] + "\nPROVEAN_pred: "  + collumns[65] + "\nMetaSVM_pred: "  +
								collumns[70] + "\nMetaLR_pred: "  + collumns[73] + "\nM-CAP_pred: "  + collumns[77] + 
								"\nMutpred: _score: "  + collumns[81] + "\nPrimateAI_pred: "  + collumns[91] + "\nDEOGEN2_pred: "  +
								collumns[94] + "\nBayesDel_addAF_pred: "  + collumns[97] + "\nBayesDel_noAF_pred: "  + collumns[100] +
								"\nClinpred: _pred: "  + collumns[103] + "\nLIST-S2_pred: "  + collumns[106] + "\nAloft_pred: "  +
								collumns[111] + "\nfathmm-MKL_coding_pred: "  + collumns[123] + "\nfathmm-XF_coding_pred: " + collumns[127]);
						/*return("Sift: " + collumns[38] + "\nSift4G: " + collumns[41] + "\nPROVEAN: " + collumns[59] +
								"\nPolyphen2_HDIV: " + collumns[44] + "\nPolyphen2_HVAR: " + collumns[47]);*/
					}
				}
			}
			else if(!nsSNV.getAaalt().isBlank() && !nsSNV.getAaref().isBlank()) {
				while ((line = br.readLine()) != null) {
					String[] collumns = line.split("\t");
					if(collumns[1].contentEquals(Integer.toString(nsSNV.getPos())) && collumns[2].contentEquals(nsSNV.getRef()) &&
						collumns[3].contentEquals(nsSNV.getAlt()) && collumns[4].contentEquals(nsSNV.getAaref()) &&
						collumns[5].contentEquals(nsSNV.getAaalt()) ) {
						return("SIFT_pred: "  + collumns[38] + "\nSIFT4G_pred: "  + collumns[41] + "\nPolyphen2_HDIV_pred: "  + 
								collumns[44] + "\nPolyphen2_HVAR_pred: "  + collumns[47] + "\nLRT_pred: "  + collumns[50] + 
								"\nMutationTaster_pred: "  + collumns[54] + "\nMutationAssessor_pred: "  + collumns[59] + 
								"\nFATHMM_pred: "  + collumns[62] + "\nPROVEAN_pred: "  + collumns[65] + "\nMetaSVM_pred: "  +
								collumns[70] + "\nMetaLR_pred: "  + collumns[73] + "\nM-CAP_pred: "  + collumns[77] + 
								"\nMutpred: _score: "  + collumns[81] + "\nPrimateAI_pred: "  + collumns[91] + "\nDEOGEN2_pred: "  +
								collumns[94] + "\nBayesDel_addAF_pred: "  + collumns[97] + "\nBayesDel_noAF_pred: "  + collumns[100] +
								"\nClinpred: _pred: "  + collumns[103] + "\nLIST-S2_pred: "  + collumns[106] + "\nAloft_pred: "  +
								collumns[111] + "\nfathmm-MKL_coding_pred: "  + collumns[123] + "\nfathmm-XF_coding_pred: " + collumns[127]);
						/*return("Sift: " + collumns[38] + "\nSift4G: " + collumns[41] + "\nPROVEAN: " + collumns[59] +
								"\nPolyphen2_HDIV: " + collumns[44] + "\nPolyphen2_HVAR: " + collumns[47]);*/
					}	
				}
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
