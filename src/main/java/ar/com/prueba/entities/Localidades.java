package ar.com.prueba.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "localidades")
public class Localidades implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;
	@Basic(optional = false)
	@Column(name = "nombre")
	private String nombre;

	@JoinColumn(name = "provincia", referencedColumnName = "id")
	@ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Provincias provincia;

	public Localidades() {
	}

	public Localidades(Integer id) {
		this.id = id;
	}

	public Localidades(Integer id, String nombre) {
		this.id = id;
		this.nombre = nombre;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Provincias getProvincia() {
		return provincia;
	}

	public void setEstado(Provincias provincia) {
		this.provincia = provincia;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Localidades)) {
			return false;
		}
		Localidades other = (Localidades) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "si.Localidades[ id=" + id + " ]";
	}

}
