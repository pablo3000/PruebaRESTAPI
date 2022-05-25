package ar.com.prueba.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ar.com.prueba.dao.ILocalidadesDAO;
import ar.com.prueba.entities.Localidades;

@Service
public class LocalidadesServices implements ILocalidadesServices {

	@Autowired
	private ILocalidadesDAO daoLocalidades;

	@Override
	public List<Localidades> get() {
		return daoLocalidades.findByOrderByNombreAsc();
	}

	@Override
	public Localidades get(Integer id) {
		return daoLocalidades.findById(id).orElse(null);
	}

	@Override
	public Localidades update(Localidades obj) {
		if (daoLocalidades.findById(obj.getId()).isPresent())
			return daoLocalidades.save(obj);
		else
			return null;
	}

	@Override
	public Localidades save(Localidades obj) {
		if (!daoLocalidades.findById(obj.getId()).isPresent())
			return daoLocalidades.save(obj);
		else
			return null;
	}

	@Override
	public void delete(Localidades obj) {
		daoLocalidades.delete(obj);
	}
}
