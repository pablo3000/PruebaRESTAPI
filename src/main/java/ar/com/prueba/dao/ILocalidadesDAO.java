package ar.com.prueba.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


import ar.com.prueba.entities.Localidades;

public interface ILocalidadesDAO extends CrudRepository<Localidades, Integer>{
	
	List<Localidades> findByOrderByNombreAsc();		
}
