package com.davi.reportesgenericos.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.util.IOUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.davi.reportesgenericos.service.ConsumeApiService;
import com.davi.reportesgenericos.utils.Constantes;
import com.davi.reportesgenericos.utils.DocxHandling;
//import com.github.cliftonlabs.json_simple.JsonObject;
//import com.github.cliftonlabs.json_simple.Jsoner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@RestController
//@RequestMapping(value = "/getpdf", method = RequestMethod.POST)
@RequestMapping("/api")
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST })
public class ReporteGenericoController {

	@Autowired
	private ConsumeApiService consumeApiService;
	@Autowired
	private DocxHandling docxHService;

	private Map<String, String> textsToReplace;
	private String originalFilePath;

	private String temporalFileName;

	private XWPFDocument doc;

	@RequestMapping(value = "/getpdf", method = RequestMethod.POST)

	public ResponseEntity<byte[]> getPDF(@RequestBody String jsonRecibido) throws FileNotFoundException, IOException {// @RequestBody
																														// String
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String fechaComoCadena = sdf.format(new Date());
		System.out.println(fechaComoCadena);

//Extraer el valor de los campos deljsonImput
		Gson gson = new Gson();

		JsonObject jsonImpu = gson.fromJson(jsonRecibido, JsonObject.class);
		String reporte = jsonImpu.get("reporte").getAsString();
		String sistema = jsonImpu.get("sistema").getAsString();
		String producto = jsonImpu.get("producto").getAsString();
//		String fechaDia = jsonImpu.get("fechaDia").getAsString();
		System.out.println("Sistema: " + sistema);
		System.out.println("Producto: " + producto);

		String jsonRespuesta = consumeApiService.getReporte(reporte, sistema, producto);

		JsonObject jsonRecibid = gson.fromJson(jsonRespuesta, JsonObject.class);
		System.out.println("Respuesta Api Raul" + jsonRespuesta);
		// Convierte la cadena de texto JSON a un objeto JSON
		// JsonObject jsonObject = Jsoner.deserialize(jsonRespuesta, new JsonObject());
		// jsonImpu es el que se recibe de resposebody
		// es json de respuesta de la api de raul

		String ubicacion = "C:\\outPut\\";
		docxHService.setFilePathPdf(ubicacion + reporte + fechaComoCadena + "." + Constantes.EXTENSION_PDF);
		docxHService.main(jsonImpu, jsonRecibid);

		byte[] contents = IOUtils.toByteArray(new FileInputStream(docxHService.getFilePathPdf()));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		// Here you have to set the actual filename of your pdf
		String filename = "output.pdf";
		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
		return response;

	}

	@RequestMapping(value = "/getBase64", method = RequestMethod.POST)
	public ResponseEntity<String> getbase64(@RequestBody String jsonRecibido) throws Exception {// @RequestBody

		System.out.println("REQUEST DE Recibido" + jsonRecibido);
		String ubicacion = "C:\\outPut\\";
		docxHService.setFilePathPdf(ubicacion + UUID.randomUUID().toString() + "." + Constantes.EXTENSION_PDF);
//Extraer el valor de los campos deljsonImput
		Gson gson = new Gson();
		JsonObject jsonImpu = gson.fromJson(jsonRecibido, JsonObject.class);

		String reporte = jsonImpu.get("reporte").getAsString();
		String sistema = jsonImpu.get("sistema").getAsString();
		String producto = jsonImpu.get("producto").getAsString();
		System.out.println("Reporte: " + reporte);
		System.out.println("Sistema: " + sistema);
		System.out.println("Producto: " + producto);

		String jsonRespuesta = consumeApiService.getReporte(reporte, sistema, producto);

		JsonObject jsonRecibid = gson.fromJson(jsonRespuesta, JsonObject.class);
		System.out.println("Respuesta Api Raul" + jsonRespuesta);

		docxHService.main(jsonImpu, jsonRecibid);

//		File tmpFile=new File(ubicacion+UUID.randomUUID().toString()+ "." + Constantes.EXTENSION_PDF);

		File tmpFile = new File(docxHService.getFilePathPdf());

		String base64 = docxHService.fileToStrBase64(tmpFile);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<String> response = new ResponseEntity<>(base64, headers, HttpStatus.OK);
		System.out.println("RESPONSE DEL BASE64" + response);
		return response;

	}

}
