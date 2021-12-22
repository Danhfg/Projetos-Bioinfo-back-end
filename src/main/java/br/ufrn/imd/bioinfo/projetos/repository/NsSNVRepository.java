package br.ufrn.imd.bioinfo.projetos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.ufrn.imd.bioinfo.projetos.models.NsSNV;
import br.ufrn.imd.bioinfo.projetos.models.User;

public interface NsSNVRepository extends JpaRepository<NsSNV, Long>{
	
	@Query(value="SELECT * FROM nssnv ns WHERE ns.pos = ?1 and ns.alt = ?2", nativeQuery = true)
	NsSNV findByPosAndAlt(Integer pos, String Alt);
	
	Page<NsSNV> findByUser(User user, Pageable pageable);

}
