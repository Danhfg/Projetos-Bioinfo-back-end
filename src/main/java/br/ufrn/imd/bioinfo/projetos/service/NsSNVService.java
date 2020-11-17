package br.ufrn.imd.bioinfo.projetos.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.ufrn.imd.bioinfo.projetos.error.CustomException;
import br.ufrn.imd.bioinfo.projetos.error.DuplicatedEntryException;
import br.ufrn.imd.bioinfo.projetos.models.NsSNV;
import br.ufrn.imd.bioinfo.projetos.models.User;
import br.ufrn.imd.bioinfo.projetos.repository.NsSNVRepository;
import br.ufrn.imd.bioinfo.projetos.repository.UserRepository;
import br.ufrn.imd.bioinfo.projetos.security.JwtTokenProvider;

@Service
public class NsSNVService {
	
	private final NsSNVRepository nsSNVRepository;
	private final UserRepository userRepository;
	private final String infile;
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired	
	public NsSNVService(NsSNVRepository nsSNVRepository, UserRepository userRepository,
			JwtTokenProvider jwtTokenProvider) {
		this.nsSNVRepository = nsSNVRepository;
		this.userRepository = userRepository;
		this.infile = "A";
		this.jwtTokenProvider = jwtTokenProvider;
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

	public void decisionTree(HttpServletRequest  req, NsSNV nsSNV) {
		NsSNV nsSNVResult = nsSNVRepository.findByPosAndAlt(nsSNV.getPos(), nsSNV.getAlt());
		if(nsSNVResult != null) {
			throw new DuplicatedEntryException("Position and Alt duplicated");
		}
		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		nsSNV.setUser(user);
		try {
			FileWriter fileWriter = new FileWriter("try.vcf");
			fileWriter.write("##fileformat=VCFv4.0\r\n" + 
					"##fileDate="+ LocalDate.now() + "\r\n" + 
					"##source=dbSNP\r\n" + 
					"##dbSNP_BUILD_ID=132\r\n" + 
					"##reference=GRCh38\r\n" + 
					"##phasing=partial\r\n" + 
					"##variationPropertyDocumentationUrl=ftp://ftp.ncbi.nlm.nih.gov/snp/specs/dbSNP_BitField_latest.pdf\r\n" + 
					"##INFO=<ID=RV,Number=0,Type=Flag,Description=\"RS orientation is reversed\">\r\n" + 
					"##INFO=<ID=NS,Number=1,Type=Integer,Description=\"Number of Samples With Data\">\r\n" + 
					"##INFO=<ID=AF,Number=.,Type=Float,Description=\"Allele Frequency\">\r\n" + 
					"##INFO=<ID=VP,Number=1,Type=String,Description=\"Variation Property\">\r\n" + 
					"##INFO=<ID=dbSNPBuildID,Number=1,Type=Integer,Description=\"First SNP Build for RS\">\r\n" + 
					"##INFO=<ID=WGT,Number=1,Type=Integer,Description=\"Weight, 00 - unmapped, 1 - weight 1, 2 - weight 2, 3 - weight 3 or more\">\r\n" + 
					"##INFO=<ID=VC,Number=1,Type=String,Description=\"Variation Class\">\r\n" + 
					"##INFO=<ID=CLN,Number=0,Type=Flag,Description=\"SNP is Clinical(LSDB,OMIM,TPA,Diagnostic)\">\r\n" + 
					"##INFO=<ID=PM,Number=0,Type=Flag,Description=\"SNP is Precious(Clinical,Pubmed Cited)\">\r\n" + 
					"##INFO=<ID=TPA,Number=0,Type=Flag,Description=\"Provisional Third Party Annotation(TPA) (currently rs from PHARMGKB who will give phenotype data)\">\r\n" + 
					"##INFO=<ID=PMC,Number=0,Type=Flag,Description=\"Links exist to PubMed Central article\">\r\n" + 
					"##INFO=<ID=S3D,Number=0,Type=Flag,Description=\"Has 3D structure - SNP3D table\">\r\n" + 
					"##INFO=<ID=SLO,Number=0,Type=Flag,Description=\"Has SubmitterLinkOut - From SNP->SubSNP->Batch.link_out\">\r\n" + 
					"##INFO=<ID=NSF,Number=0,Type=Flag,Description=\"Has non-synonymous frameshift A coding region variation where one allele in the set changes all downstream amino acids. FxnClass = 44\">\r\n" + 
					"##INFO=<ID=NSM,Number=0,Type=Flag,Description=\"Has non-synonymous missense A coding region variation where one allele in the set changes protein peptide. FxnClass = 42\">\r\n" + 
					"##INFO=<ID=NSN,Number=0,Type=Flag,Description=\"Has non-synonymous nonsense A coding region variation where one allele in the set changes to STOP codon (TER). FxnClass = 41\">\r\n" + 
					"##INFO=<ID=REF,Number=0,Type=Flag,Description=\"Has reference A coding region variation where one allele in the set is identical to the reference sequence. FxnCode = 8\">\r\n" + 
					"##INFO=<ID=SYN,Number=0,Type=Flag,Description=\"Has synonymous A coding region variation where one allele in the set does not change the encoded amino acid. FxnCode = 3\">\r\n" + 
					"##INFO=<ID=U3,Number=0,Type=Flag,Description=\"In 3' UTR Location is in an untranslated region (UTR). FxnCode = 53\">\r\n" + 
					"##INFO=<ID=U5,Number=0,Type=Flag,Description=\"In 5' UTR Location is in an untranslated region (UTR). FxnCode = 55\">\r\n" + 
					"##INFO=<ID=ASS,Number=0,Type=Flag,Description=\"In acceptor splice site FxnCode = 73\">\r\n" + 
					"##INFO=<ID=DSS,Number=0,Type=Flag,Description=\"In donor splice-site FxnCode = 75\">\r\n" + 
					"##INFO=<ID=INT,Number=0,Type=Flag,Description=\"In Intron FxnCode = 6\">\r\n" + 
					"##INFO=<ID=R3,Number=0,Type=Flag,Description=\"In 3' gene region FxnCode = 13\">\r\n" + 
					"##INFO=<ID=R5,Number=0,Type=Flag,Description=\"In 5' gene region FxnCode = 15\">\r\n" + 
					"##INFO=<ID=OTH,Number=0,Type=Flag,Description=\"Has other snp with exactly the same set of mapped positions on NCBI refernce assembly.\">\r\n" + 
					"##INFO=<ID=CFL,Number=0,Type=Flag,Description=\"Has Assembly conflict. This is for weight 1 and 2 snp that maps to different chromosomes on different assemblies.\">\r\n" + 
					"##INFO=<ID=ASP,Number=0,Type=Flag,Description=\"Is Assembly specific. This is set if the snp only maps to one assembly\">\r\n" + 
					"##INFO=<ID=MUT,Number=0,Type=Flag,Description=\"Is mutation (journal citation, explicit fact): a low frequency variation that is cited in journal and other reputable sources\">\r\n" + 
					"##INFO=<ID=VLD,Number=0,Type=Flag,Description=\"Is Validated.  This bit is set if the snp has 2+ minor allele count based on frequency or genotype data.\">\r\n" + 
					"##INFO=<ID=G5A,Number=0,Type=Flag,Description=\">5% minor allele frequency in each and all populations\">\r\n" + 
					"##INFO=<ID=G5,Number=0,Type=Flag,Description=\">5% minor allele frequency in 1+ populations\">\r\n" + 
					"##INFO=<ID=HD,Number=0,Type=Flag,Description=\"Marker is on high density genotyping kit (50K density or greater).  The snp may have phenotype associations present in dbGaP.\">\r\n" + 
					"##INFO=<ID=GNO,Number=0,Type=Flag,Description=\"Genotypes available. The snp has individual genotype (in SubInd table).\">\r\n" + 
					"##INFO=<ID=KGPilot1,Number=0,Type=Flag,Description=\"1000 Genome discovery(pilot1) 2009\">\r\n" + 
					"##INFO=<ID=KGPilot123,Number=0,Type=Flag,Description=\"1000 Genome discovery all pilots 2010(1,2,3)\">\r\n" + 
					"##INFO=<ID=KGVAL,Number=0,Type=Flag,Description=\"1000 Genome validated by second method\">\r\n" + 
					"##INFO=<ID=KGPROD,Number=0,Type=Flag,Description=\"1000 Genome production phase\">\r\n" + 
					"##INFO=<ID=PH1,Number=0,Type=Flag,Description=\"Phase 1 genotyped: filtered, non-redundant\">\r\n" + 
					"##INFO=<ID=PH2,Number=0,Type=Flag,Description=\"Phase 2 genotyped: filtered, non-redundant\">\r\n" + 
					"##INFO=<ID=PH3,Number=0,Type=Flag,Description=\"Phase 3 genotyped: filtered, non-redundant\">\r\n" + 
					"##INFO=<ID=CDA,Number=0,Type=Flag,Description=\"Variation is interrogated in a clinical diagnostic assay\">\r\n" + 
					"##INFO=<ID=LSD,Number=0,Type=Flag,Description=\"Submitted from a locus-specific database\">\r\n" + 
					"##INFO=<ID=MTP,Number=0,Type=Flag,Description=\"Microattribution/third-party annotation(TPA:GWAS,PAGE)\">\r\n" + 
					"##INFO=<ID=OM,Number=0,Type=Flag,Description=\"Has OMIM/OMIA\">\r\n" + 
					"##INFO=<ID=NOC,Number=0,Type=Flag,Description=\"Contig allele not present in SNP allele list. The reference sequence allele at the mapped position is not present in the SNP allele list, adjusted for orientation.\">\r\n" + 
					"##INFO=<ID=WTD,Number=0,Type=Flag,Description=\"Is Withdrawn by submitter If one member ss is withdrawn by submitter, then this bit is set.  If all member ss' are withdrawn, then the rs is deleted to SNPHistory\">\r\n" + 
					"##INFO=<ID=NOV,Number=0,Type=Flag,Description=\"Rs cluster has non-overlapping allele sets. True when rs set has more than 2 alleles from different submissions and these sets share no alleles in common.\">\r\n" + 
					"##INFO=<ID=GCF,Number=0,Type=Flag,Description=\"Has Genotype Conflict Same (rs, ind), different genotype.  N/N is not included.\">\r\n" +
					"#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO\r\n" + 
					nsSNV.getChr()+"	"+nsSNV.getPos()+"	.	"+nsSNV.getRef()+"	"+nsSNV.getAlt()+
					"	.	.	.");
			fileWriter.close();

			ProcessBuilder pb = new ProcessBuilder("java","-jar","C:\\Users\\Daniel\\Downloads\\snpEff_latest_core\\snpEff\\SnpSift.jar",
					"dbnsfp","-v","-db","C:\\Users\\Daniel\\Downloads\\dbNSFP4.1a.txt.gz","try.vcf");
			pb.redirectOutput(new File("data\\",user.getIdUser().toString()+ 
					nsSNV.getPos().toString()+ nsSNV.getAlt()+"out.vcf"));
			pb.redirectError(new File("data\\",user.getIdUser().toString()+ 
					nsSNV.getPos().toString()+ nsSNV.getAlt()+"out.log"));
			Process p = pb.start();
			
			nsSNV.setPid(p.pid());
			nsSNV.setAlive(true);
			nsSNVRepository.save(nsSNV);
			
			CompletableFuture<Process> cfp = p.onExit();
			//cfp.get();
			cfp.thenAccept(
					ph_ -> 
						{
							nsSNV.setAlive(false);
							ReversedLinesFileReader object = null;
							try {
								object = new ReversedLinesFileReader(new File("data\\",user.getIdUser().toString()+ 
											nsSNV.getPos().toString()+ nsSNV.getAlt()+"out.vcf"));
								String result = object.readLine();
								nsSNV.setResult(result);
								System.out.println("Line - " + result);
							} catch (IOException e) {
								e.printStackTrace();
						 	}finally{
								nsSNVRepository.save(nsSNV);
									try {
										object.close();
									} catch (IOException e) {
										e.printStackTrace();
								  }
						 	}
						});
			//System.out.println(p.waitFor());
			
			//Process proc = Runtime.getRuntime().exec("java -jar C:\\Users\\Daniel\\Downloads\\snpEff_latest_core\\snpEff\\SnpSift.jar dbnsfp -v -db C:\\Users\\Daniel\\Downloads\\dbNSFP4.1a.txt.gz try.vcf > out.vcf");
			//proc.waitFor();
			
			/*ProcessBuilder pb =
					   new ProcessBuilder("java", "-jar", "C:\\Users\\Daniel\\Downloads\\snpEff_latest_core\\snpEff\\SnpSift.jar", "dbnsfp -v db", "C:\\Users\\Daniel\\Downloads\\dbNSFP4.1a.txt.gz");
					 Map<String, String> env = pb.environment();
					 File log = new File("log");
					 pb.redirectErrorStream(true);
					 pb.redirectOutput(Redirect.appendTo(log));
					 Process p = pb.start();*/
/*
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
								"\nExac: " + collumns[193] + " "+ collumns[243] + "\nNDAMAGE: " + nDamage + "\n");
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
			}*/
		} catch (IOException /*| InterruptedException | ExecutionException*/ e) {
			// TODO Auto-generated catch block
			throw new CustomException("Erro interno, entre em contato com nosso suporte", HttpStatus.INTERNAL_SERVER_ERROR);
		} /*
		
		
		return "A";*/
		System.out.println("TESTE");
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

	public List<NsSNV> getAllResult(HttpServletRequest req) {
		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		List<NsSNV> nsSNVList = nsSNVRepository.findByUser(user);
		
		if(nsSNVList == null || nsSNVList.isEmpty()) {
			throw new ResourceNotFoundException("User doesn't have requests."); 
		}
		
		return nsSNVList;
		
	}

}
