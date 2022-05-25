package ar.com.prueba.services;

import java.util.List;
import ar.com.prueba.entities.Localidades;

public interface ILocalidadesServices {
	public List<Localidades> get();

	public Localidades get(Integer id);

	public Localidades update(Localidades obj);

	public Localidades save(Localidades obj);

	public void delete(Localidades obj);

}
