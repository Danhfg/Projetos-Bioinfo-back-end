package br.ufrn.imd.bioinfo.projetos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufrn.imd.bioinfo.projetos.models.Tipo_Usuario;

public interface Tipo_UsuarioRepository extends JpaRepository<Tipo_Usuario, Long>{
	Tipo_Usuario findByNome(String nome);
}
