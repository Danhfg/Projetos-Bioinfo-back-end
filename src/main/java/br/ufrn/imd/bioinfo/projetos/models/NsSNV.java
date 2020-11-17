package br.ufrn.imd.bioinfo.projetos.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class NsSNV {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idNsSNV;
	
	@Column(columnDefinition = "VARCHAR(2)")
	private String chr;

	@Column(columnDefinition = "INT")
	private Integer pos;

	@Column(columnDefinition = "VARCHAR(1)")
	private String ref;

	@Column(columnDefinition = "VARCHAR(1)")
	private String alt;

	@Column(columnDefinition = "VARCHAR(1)")
	private String aaref;

	@Column(columnDefinition = "VARCHAR(1)")
	private String aaalt;
	
	@OneToOne
	//@JsonIgnore
	//@NotEmpty
	@JoinColumn(name = "id_user")
	@JsonIgnore
	private User user;

	@Column(columnDefinition = "INT")
	@JsonIgnore
	private Long pid;

	@Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
	@JsonIgnore
	private boolean isAlive;

    @Column(columnDefinition = "TEXT")
	private String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public long getIdNsSNV() {
		return idNsSNV;
	}

	public void setIdNsSNV(long idNsSNV) {
		this.idNsSNV = idNsSNV;
	}

	public String getChr() {
		return chr;
	}

	public void setChr(String chr) {
		this.chr = chr;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getAaref() {
		return aaref;
	}

	public void setAaref(String aaref) {
		this.aaref = aaref;
	}

	public String getAaalt() {
		return aaalt;
	}

	public void setAaalt(String aaalt) {
		this.aaalt = aaalt;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

}
