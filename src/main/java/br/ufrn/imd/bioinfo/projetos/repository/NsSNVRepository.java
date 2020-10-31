package br.ufrn.imd.bioinfo.projetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufrn.imd.bioinfo.projetos.models.NsSNV;

public interface NsSNVRepository extends JpaRepository<NsSNV, Long>{

}
