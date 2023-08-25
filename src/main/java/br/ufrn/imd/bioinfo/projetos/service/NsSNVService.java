package br.ufrn.imd.bioinfo.projetos.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import br.ufrn.imd.bioinfo.projetos.error.CustomException;
import br.ufrn.imd.bioinfo.projetos.models.NsSNV;
import br.ufrn.imd.bioinfo.projetos.models.User;
import br.ufrn.imd.bioinfo.projetos.repository.NsSNVRepository;
import br.ufrn.imd.bioinfo.projetos.repository.UserRepository;
import br.ufrn.imd.bioinfo.projetos.security.JwtTokenProvider;

@Service
public class NsSNVService {
	
	private final NsSNVRepository nsSNVRepository;
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PythonAPI pythonAPI;

	@Autowired	
	public NsSNVService(NsSNVRepository nsSNVRepository, UserRepository userRepository,
			JwtTokenProvider jwtTokenProvider, PythonAPI pythonAPI) {
		this.nsSNVRepository = nsSNVRepository;
		this.userRepository = userRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.pythonAPI = pythonAPI;
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
	
	/*public String decisionTree(HttpServletRequest  req, Long id) {
		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		Optional<NsSNV> opt_ns = nsSNVRepository.findById(id);
		NsSNV nsSNV = opt_ns.isPresent() ? opt_ns.get() : null;
		if(nsSNV == null )
		{
			throw new ResourceNotFoundException("Not Found");
		}
		else if(nsSNV.getUser().getIdUser() != user.getIdUser())
		{
			throw new CustomException("Permision denied", HttpStatus.FORBIDDEN);
		}
		else {
			String[] ns_list = nsSNV.getResult().split("	");
			String sift = ns_list[38];
			String provean = ns_list[65];
			String polyphen = ns_list[47];
			
			if(!sift.contains("D") && !provean.contains("N") && (!polyphen.contains("D") && !polyphen.contains("P")) ) {
				return "Neutral";
			}
			else {
				Double exac = ns_list[190].compareTo(".") == 0 ? 0 : Double.parseDouble(ns_list[190]); 
				if (exac < 0.0001) {
					return "Pathogenic";
				}
				else {
					if(nDamageCount(ns_list) <= 6)
					{
						return "Neutral";
					}
					else{
						Double common = ns_list[171].compareTo(".") == 0 ? 0 : Double.parseDouble(ns_list[171]);
						if(common > 0)
						{
							return "Neutral";
						}
						else return "Pathogenic";
					}
				}
			}
		}
	}*/

	public void processPrediction(HttpServletRequest  req, NsSNV nsSNV) {
		/*NsSNV nsSNVResult = nsSNVRepository.findByPosAndAlt(nsSNV.getPos(), nsSNV.getAlt());
		if(nsSNVResult != null) {
			throw new DuplicatedEntryException("Position and Alt duplicated");
		}*/
		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		nsSNV.setUser(user);
		try {
			/*FileWriter fileWriter = new FileWriter("try.vcf");
			fileWriter.write("##fileformat=VCFv4.0\r\n" + 
					"##fileDate="+ LocalDate.now() + "\r\n" +
					"#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO\r\n" + 
					nsSNV.getChr()+"	"+nsSNV.getPos()+"	.	"+nsSNV.getRef()+"	"+nsSNV.getAlt()+
					"	.	.	.");
			fileWriter.close();*/

			/*ProcessBuilder pb = new ProcessBuilder("java","-jar","C:\\Db\\snpEff_latest_core\\snpEff\\SnpSift.jar",
					"dbnsfp","-v","-db","C:\\Db\\dbNSFP4.1a.txt.gz","try.vcf");*/
			ProcessBuilder pb;
			if(SystemUtils.IS_OS_LINUX) {
				/*pb = new ProcessBuilder("tabix","/mnt/c/Db/dbNSFP4.1a.txt.gz", nsSNV.getChr(),":"+nsSNV.getPos(),"-",
						"dbnsfp","-v","-db","C:\\Db\\dbNSFP4.1a.txt.gz","try.vcf");*/
				pb = new ProcessBuilder("./tabix", "/db/dbNSFP4.1a.txt.gz", nsSNV.getChr()+":"+nsSNV.getPos().toString()+"-"+
						nsSNV.getPos().toString(),"-p", "vcf", "| awk '($3==\""+nsSNV.getRef()+"\" && $4==\""+nsSNV.getAlt()+"\")'");
			}
			else{
				pb = new ProcessBuilder("wsl", "tabix","/mnt/c/Db/dbNSFP4.1a.txt.gz",nsSNV.getChr()+":"+nsSNV.getPos().toString()+"-"+
						nsSNV.getPos().toString(), "-p", "vcf");
			}
			File out = new File("./",user.getIdUser().toString()+ 
					nsSNV.getPos().toString()+ nsSNV.getAlt()+"out.vcf");
			pb.redirectOutput(out);
			File outLog = new File("./",user.getIdUser().toString()+ 
					nsSNV.getPos().toString()+ nsSNV.getAlt()+"out.log");
			pb.redirectError(outLog);
			Process p = pb.start();

			nsSNV.setPid(p.pid());
			nsSNV.setAlive(true);
			nsSNVRepository.save(nsSNV);
			System.out.println("SALVO");
			
			CompletableFuture<Process> cfp = p.onExit();
			//cfp.get();
			cfp.thenAccept(
					ph_ -> 
						{
							nsSNV.setAlive(false);
							Scanner object = null;
							try {
								object = new Scanner(new File("./",user.getIdUser().toString()+ 
											nsSNV.getPos().toString()+ nsSNV.getAlt()+"out.vcf"));
								String result = "";// = object.nextLine();
//								if(result.split("	")[3].compareTo(nsSNV.getAlt()) != 0) 
								while (object.hasNextLine()) {
									result = object.nextLine();
									if(result.split("	")[3].equalsIgnoreCase(nsSNV.getAlt())) {
										break;
									}
									result = "";
								}
								String resultMl = processResultML(result);
								System.out.println(resultMl);
								nsSNV.setResultML(getMlResults(resultMl));
								result = processResult(result);
								nsSNV.setResult(result);
								nsSNVRepository.save(nsSNV);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
						 	}finally{
						 		//out.delete();
						 		outLog.delete();
						 		NsSNV nsSNV_new = nsSNVRepository.save(nsSNV);
								//processClinvar(req, nsSNV_new);
								try {
									ProcessBuilder pb2;
									if(SystemUtils.IS_OS_LINUX) {
										pb2 = new ProcessBuilder("./tabix", "/db/clinvar.vcf.gz", nsSNV.getChr()+":"+nsSNV.getPos().toString()+"-"+
												nsSNV.getPos().toString(),"-p", "vcf", "| awk '($4==\""+nsSNV.getRef()+"\" && $5==\""+nsSNV.getAlt()+"\")'");
									}
									else{
										pb2 = new ProcessBuilder("wsl", "tabix","/mnt/c/Db/clinvar.vcf.gz",nsSNV.getChr()+":"+nsSNV.getPos().toString()+"-"+
												nsSNV.getPos().toString(), "-p", "vcf");
									}
									File out2 = new File("./",user.getIdUser().toString()+ 
											nsSNV.getPos().toString()+ nsSNV.getAlt()+".clivar.result.vcf");
									pb2.redirectOutput(out2);
									File outLog2 = new File("./",user.getIdUser().toString()+ 
											nsSNV.getPos().toString()+ nsSNV.getAlt()+".clinvar.log");
									pb2.redirectError(outLog2);
									Process p2 = pb2.start();

									CompletableFuture<Process> cfp2 = p2.onExit();

									cfp2.thenAccept(
											ph_2 -> 
												{
													Scanner object2 = null;
													try {
														object2 = new Scanner(new File("./",user.getIdUser().toString()+ 
																	nsSNV.getPos().toString()+ nsSNV.getAlt()+".clivar.result.vcf"));
														//String result2 = object2.nextLine();
														String result2 = "";// = object.nextLine();
//														if(result.split("	")[3].compareTo(nsSNV.getAlt()) != 0) 
														
														while(object2.hasNextLine()) {
															result2 = object2.nextLine();
															if(result2.split("	")[4].equalsIgnoreCase(nsSNV.getAlt())) {
																break;
															}
															result2 = "";
														}
														result2 = processClinvarResult(result2);
														nsSNV_new.setResultClinvar(result2);
														nsSNVRepository.save(nsSNV_new);
														object2.close();
													} catch (FileNotFoundException e) {
														e.printStackTrace();
												 	}finally{
												 		//out.delete();
												 		//outLog2.delete();
														nsSNVRepository.save(nsSNV_new);
												 	}
													nsSNVRepository.save(nsSNV_new);							
												});
									
								} catch (IOException /*| InterruptedException | ExecutionException*/ e) {
									// TODO Auto-generated catch block
									//throw new CustomException("Erro interno, entre em contato com nosso suporte", HttpStatus.INTERNAL_SERVER_ERROR);
									System.out.println(e);
								} 
						 	}
							//nsSNVRepository.save(nsSNV);							
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
			//throw new CustomException("Erro interno, entre em contato com nosso suporte", HttpStatus.INTERNAL_SERVER_ERROR);
			System.out.println(e);
		} /*
		
		
		return "A";*/
	}
	
	private String processResult(String result) {
		String[] collumns = result.split("	");
		return("SIFT_pred:"  + collumns[38] + "\nSIFT4G_pred:"  + collumns[41] + "\nPolyphen2_HDIV_pred:"  + 
				collumns[44] + "\nPolyphen2_HVAR_pred:"  + collumns[47] + "\nLRT_pred:"  + collumns[50] + 
				"\nMutationTaster_pred:"  + collumns[54] + "\nMutationAssessor_pred:"  + collumns[59] + 
				"\nFATHMM_pred:"  + collumns[62] + "\nPROVEAN_pred:"  + collumns[65] + "\nMetaSVM_pred:"  +
				collumns[70] + "\nMetaLR_pred:"  + collumns[73] + "\nM-CAP_pred:"  + collumns[77] + 
				"\nMutpred:_score:"  + collumns[81] + "\nPrimateAI_pred:"  + collumns[91] + "\nDEOGEN2_pred:"  +
				collumns[94] + "\nBayesDel_addAF_pred:"  + collumns[97] + "\nBayesDel_noAF_pred:"  + collumns[100] +
				"\nClinpred_pred:"  + collumns[103] + "\nLIST-S2_pred:"  + collumns[106] + "\nAloft_pred:"  +
				collumns[111] + "\nfathmm-MKL_coding_pred:"  + collumns[123] + "\nfathmm-XF_coding_pred:" + collumns[127] +
				"\nExAC_AF:" + collumns[193] + "\n1000Gp3_AF:" + collumns[171]);
	}
	private String processResultML(String result) {
		if(result.equalsIgnoreCase("")) return "";
		String[] collumns = result.split("	");
		String r = "";
		
		if(collumns[38].contains("D"))
			r += "1,";
		else r+= "0,";

		if((collumns[44].contains("D") || collumns[44].contains("P")))
			r+="1,";
		else r+= "0,";

		if(!collumns[65].contains("N"))
			r+="1,";
		else r+= "0,";

		if(collumns[193].equals("."))
		{
			r+= "1,";
		}
		else
		{
			if (Double.parseDouble(collumns[193]) < 0.0001)
				r+= "1,";
			else r+= "0,";
		}

		if(nDamageCount(collumns) <= 6)
			r+= "1,";
		else r+= "0,";

		if(collumns[171].equals("."))
		{
			r+= "1";
		}
		else
		{
			if (Double.parseDouble(collumns[171]) < 0.0001)
				r+= "1";
			else r+= "0";
		}

		return(r);
	}

	public String allPretictiors(HttpServletRequest req, Long id) {
		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		Optional<NsSNV> opt_ns = nsSNVRepository.findById(id);
		NsSNV nsSNV = opt_ns.isPresent() ? opt_ns.get() : null;
		if(nsSNV == null )
		{
			throw new ResourceNotFoundException("Not Found");
		}
		else if(nsSNV.getUser().getIdUser() != user.getIdUser())
		{
			throw new CustomException("Permision denied", HttpStatus.FORBIDDEN);
		}
		else {
			return nsSNV.getResult();
			/*String[] collumns = nsSNV.getResult().split("	");
			return("SIFT_pred:"  + collumns[38] + "\nSIFT4G_pred:"  + collumns[41] + "\nPolyphen2_HDIV_pred:"  + 
					collumns[44] + "\nPolyphen2_HVAR_pred:"  + collumns[47] + "\nLRT_pred:"  + collumns[50] + 
					"\nMutationTaster_pred:"  + collumns[54] + "\nMutationAssessor_pred:"  + collumns[59] + 
					"\nFATHMM_pred:"  + collumns[62] + "\nPROVEAN_pred:"  + collumns[65] + "\nMetaSVM_pred:"  +
					collumns[70] + "\nMetaLR_pred:"  + collumns[73] + "\nM-CAP_pred:"  + collumns[77] + 
					"\nMutpred:_score:"  + collumns[81] + "\nPrimateAI_pred:"  + collumns[91] + "\nDEOGEN2_pred:"  +
					collumns[94] + "\nBayesDel_addAF_pred:"  + collumns[97] + "\nBayesDel_noAF_pred:"  + collumns[100] +
					"\nClinpred_pred:"  + collumns[103] + "\nLIST-S2_pred:"  + collumns[106] + "\nAloft_pred:"  +
					collumns[111] + "\nfathmm-MKL_coding_pred:"  + collumns[123] + "\nfathmm-XF_coding_pred:" + collumns[127] +
					"\nExAC_AF:" + collumns[193] + "\n1000Gp3_AF:" + collumns[171]);*/
			/*return("Sift: " + collumns[38] + "\nSift4G: " + collumns[41] + "\nPROVEAN: " + collumns[59] +
					"\nPolyphen2_HDIV: " + collumns[44] + "\nPolyphen2_HVAR: " + collumns[47]);*/
		}
	}

	public Page<NsSNV> getAllResult(HttpServletRequest req, Pageable pageable) {
		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		Page<NsSNV> nsSNVList = nsSNVRepository.findByUser(user, pageable);
		
		if(nsSNVList == null || nsSNVList.isEmpty()) {
			throw new ResourceNotFoundException("User doesn't have requests."); 
		}
		
		return nsSNVList;
		
	}

	public List<NsSNV> getAllResult(HttpServletRequest req) {
		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		List<NsSNV> nsSNVList = nsSNVRepository.findByUser(user);

		if(nsSNVList == null || nsSNVList.isEmpty()) {
			throw new ResourceNotFoundException("User doesn't have requests."); 
		}

		return nsSNVList;
		
	}


	public void deletePrediction(HttpServletRequest req, Long id) {
		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
		NsSNV nssnv = nsSNVRepository.findById(id).isPresent() ? nsSNVRepository.findById(id).get() : null;
		if (nssnv == null) 
			throw new ResourceNotFoundException("Prediction not found");
		if (nssnv.getUser().getIdUser() != user.getIdUser())
			throw new CustomException("Permission denied", HttpStatus.FORBIDDEN);
		nsSNVRepository.deleteById(nssnv.getIdNsSNV());
	}
	
	public String getMlResults(String result) {
		String response = pythonAPI.getMlResults(result);
		return response;
	}
	

	@FeignClient(url= "http://10.7.43.13:5000" , name = "pythonapi")
	private interface PythonAPI{
		
	    @PostMapping("/results")
	    public String getMlResults(@RequestBody String values);
	}
	
	public String processClinvarResult(String vcf) {
		String[] collumns = vcf.split("	");
		
		return collumns[7];
		
	}

	public ResponseEntity<?> processNPrediction(HttpServletRequest req, NsSNV nsSNV) {
		
		try (BufferedReader br = new BufferedReader(new StringReader(nsSNV.getVcf()))) {
			String line;

            // Percorre cada linha no arquivo
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                	// #CHROM POS      ID         REF   ALT    QUAL  FILTER   INFO FORMAT       NA00001
                	String[] row = line.split("\t");
                	NsSNV new_nsSNV = new NsSNV();
                	new_nsSNV.setChr(row[0]);
                	new_nsSNV.setPos(Integer.parseInt(row[1]));
                	new_nsSNV.setRef(row[3]);
                	new_nsSNV.setAlt(row[4]);
                	
                	processPrediction(req, new_nsSNV);
                }
            }

			return new ResponseEntity<>(HttpStatus.OK);
		}catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
		
	}
	
//	public void processClinvar(HttpServletRequest  req, NsSNV nsSNV) {
//		User user = userRepository.findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
//		nsSNV.setUser(user);
//		System.out.println("ENTROU NO CLINVAR");
//		try {
//			ProcessBuilder pb;
//			if(SystemUtils.IS_OS_LINUX) {
//				pb = new ProcessBuilder("./tabix", "/db/clinvar.vcf.gz", nsSNV.getChr()+":"+nsSNV.getPos().toString()+"-"+
//						nsSNV.getPos().toString(),"-p", "vcf", "| awk '($4==\""+nsSNV.getRef()+"\" && $5==\""+nsSNV.getAlt()+"\")'");
//			}
//			else{
//				pb = new ProcessBuilder("wsl", "tabix","/mnt/c/Db/clinvar.vcf.gz",nsSNV.getChr()+":"+nsSNV.getPos().toString()+"-"+
//						nsSNV.getPos().toString(), "-p", "vcf");
//			}
//			File out = new File("./",user.getIdUser().toString()+ 
//					nsSNV.getPos().toString()+ nsSNV.getAlt()+".clivar.result.vcf");
//			pb.redirectOutput(out);
//			File outLog = new File("./",user.getIdUser().toString()+ 
//					nsSNV.getPos().toString()+ nsSNV.getAlt()+".clinvar.log");
//			pb.redirectError(outLog);
//			Process p = pb.start();
//
//			CompletableFuture<Process> cfp = p.onExit();
//			System.out.println(String.join(" ",pb.command().toArray(new String[0])));
//
//			cfp.thenAccept(
//					ph_ -> 
//						{
//							Scanner object = null;
//							try {
//								object = new Scanner(new File("./",user.getIdUser().toString()+ 
//											nsSNV.getPos().toString()+ nsSNV.getAlt()+".clivar.result.vcf"));
//								String result = object.nextLine();
//								System.out.println(result);
//								result = processClinvarResult(result);
//								nsSNV.setResultClinvar(result);
//								nsSNVRepository.save(nsSNV);
//								object.close();
//							} catch (FileNotFoundException e) {
//								e.printStackTrace();
//						 	}finally{
//						 		//out.delete();
//						 		outLog.delete();
//								nsSNVRepository.save(nsSNV);
//						 	}
//							nsSNVRepository.save(nsSNV);							
//						});
//			
//		} catch (IOException /*| InterruptedException | ExecutionException*/ e) {
//			// TODO Auto-generated catch block
//			//throw new CustomException("Erro interno, entre em contato com nosso suporte", HttpStatus.INTERNAL_SERVER_ERROR);
//			System.out.println(e);
//		} 
//	}
	

}
