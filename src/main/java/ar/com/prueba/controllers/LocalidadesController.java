package ar.com.prueba.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import ar.com.prueba.entities.*;
import ar.com.prueba.entities.InternalError;
import ar.com.prueba.services.ILocalidadesServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.File;

@RestController
@RequestMapping("prueba/api/v1/localidades")
@CrossOrigin(origins = { "http://localhost:4200" })
@Tag(name = "Localidades")
public class LocalidadesController {

	private final String TERROR = "Error";
	private final String EGENERAL = "Ocurrió un error inesperado en el BackEnd. Consulte al administrador del sistema";
	private final String NOTFOUND = "No existen registros";
	private final String TGENERAL = "mensaje";
	private final String OK = "Todo bien";

	private final String REPORT_PATH = "resources/reporte.jrxml";

	@Autowired
	ILocalidadesServices service;

	@GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Retorna todas las Localidades", responses = {
			@ApiResponse(description = OK, responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Localidades.class))),
			@ApiResponse(description = NOTFOUND, responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeneralError.class))),
			@ApiResponse(description = EGENERAL, responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InternalError.class))) })
	public ResponseEntity<?> getAll() {
		List<Localidades> out = null;
		Map<String, Object> response = new HashMap<>();

		try {
			out = service.get();
		} catch (DataAccessException e) {
			response.put(TERROR, EGENERAL);
			response.put(TERROR, e.getMessage().concat("::").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (null == out || out.size() < 1) {
			response.put(TGENERAL, NOTFOUND);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}
		return new ResponseEntity<List<Localidades>>(out, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Retorna una Localidad", responses = {
			@ApiResponse(description = OK, responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Localidades.class))),
			@ApiResponse(description = NOTFOUND, responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeneralError.class))),
			@ApiResponse(description = EGENERAL, responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InternalError.class))) })
	public ResponseEntity<?> get(@PathVariable Integer id) {
		Localidades out = null;
		Map<String, Object> response = new HashMap<>();

		try {
			out = service.get(id);
		} catch (DataAccessException e) {
			response.put(TERROR, EGENERAL);
			response.put(TERROR, e.getMessage().concat("::").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (null == out) {
			response.put(TGENERAL, NOTFOUND);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}

		return new ResponseEntity<Localidades>(out, HttpStatus.OK);
	}

	@GetMapping("/report/")
	@Operation(summary = "Genera un reporte en PDF", responses = {
			@ApiResponse(description = OK, responseCode = "200", content = @Content(mediaType = "application/force-download", schema = @Schema(implementation = Byte.class))),
			@ApiResponse(description = NOTFOUND), @ApiResponse(description = EGENERAL) })
	public ResponseEntity<Resource> print() {
		try {
			List<Localidades> outCuenta = service.get();
			if (null != outCuenta) {
				JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource(outCuenta, false);
				JasperReport report = JasperCompileManager.compileReport(new File(REPORT_PATH).getAbsolutePath());
				JasperPrint jPrint = JasperFillManager.fillReport(report, null, beanCollectionDataSource);

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setExporterInput(new SimpleExporterInput(jPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
				SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();

				exporter.setConfiguration(configuration);
				exporter.exportReport();

				byte[] bytePDF = outputStream.toByteArray();

				return ResponseEntity.ok()
						.header("Content-Disposition", String.format("attachment; filename=\"salida.pdf\""))
						.contentType(MediaType.parseMediaType("application/force-download"))
						.body(new ByteArrayResource(bytePDF));
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).build();
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}

	@PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Guarda una Localidad", responses = {
			@ApiResponse(description = OK, responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Localidades.class))),
			@ApiResponse(description = EGENERAL, responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InternalError.class))) })
	public ResponseEntity<?> create(@RequestBody Localidades obj) {
		Localidades out = null;
		Map<String, Object> response = new HashMap<>();

		try {
			obj.setId(null);
			out = service.save(obj);
			return new ResponseEntity<Localidades>(out, HttpStatus.OK);
		} catch (DataAccessException e) {
			response.put(TERROR, EGENERAL);
			response.put(TERROR, e.getMessage().concat("::").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/")
	@Operation(summary = "Edita una Localidad", responses = {
			@ApiResponse(description = OK, responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Localidades.class))),
			@ApiResponse(description = NOTFOUND, responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeneralError.class))),
			@ApiResponse(description = EGENERAL, responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InternalError.class))) })
	public ResponseEntity<?> update(@RequestBody Localidades obj) {
		Localidades out = null;
		Map<String, Object> response = new HashMap<>();

		try {
			out = service.update(obj);
		} catch (DataAccessException e) {
			response.put(TERROR, EGENERAL);
			response.put(TERROR, e.getMessage().concat("::").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (null == out) {
			response.put(TGENERAL, NOTFOUND);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}

		return new ResponseEntity<Localidades>(out, HttpStatus.OK);
	}

	@DeleteMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Elimina una Localidad", responses = {
			@ApiResponse(description = OK, responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Localidades.class))),
			@ApiResponse(description = NOTFOUND, responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GeneralError.class))),
			@ApiResponse(description = EGENERAL, responseCode = "500", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InternalError.class))) })
	public ResponseEntity<?> delete(@RequestBody Localidades obj) {
		Localidades out = null;
		Map<String, Object> response = new HashMap<>();

		try {
			out = service.get(obj.getId());
			if (null != out)
				service.delete(obj);

		} catch (DataAccessException e) {
			response.put(TERROR, EGENERAL);
			response.put(TERROR, e.getMessage().concat("::").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (null == out) {
			response.put(TGENERAL, NOTFOUND);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		} else
			return new ResponseEntity<>(null, HttpStatus.OK);

	}
}
