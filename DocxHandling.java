package com.davi.reportesgenericos.service;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;

import com.davi.reportesgenericos.utils.Constantes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cliftonlabs.json_simple.JsonObject;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;



public class DocxHandling {
	@Autowired
	private ConsumeApiService consumeApiService;
	

	private Map<String, String> textsToReplace;
	private String originalFilePath;

	private String temporalFileName;

	private XWPFDocument doc;

	public DocxHandling() {
	}

	public DocxHandling(Map<String, String> textsToReplace, String originalFilePath, String destinationPath) {
		this.textsToReplace = textsToReplace;
		this.originalFilePath = originalFilePath;
	}

	public InputStream replaceTexts() throws Exception {
	String codigo="CPDMIGC";
//		String campos=	consumeApiService.getReporte(codigo);
//		System.out.println("***************consumiento Feig"+campos);
	
		System.out.println("se inicia replaceText");
		// Validamos que hayamos recibido el path del documento original
		if (originalFilePath == null)
			throw new NullPointerException("Debe proporcionar el path del archivo original");

		// Validamos que hayamos recibido los textos a reemplazar
		if (textsToReplace == null || textsToReplace.isEmpty())
			throw new Exception("Debe proporcionar los textos a reemplazar");

		// Generamos un nombre para nuestro archivo temporal
		temporalFileName = UUID.randomUUID().toString();

		// Abrimos nuestro documento
		doc = new XWPFDocument(new FileInputStream(originalFilePath));
		// System.out.println("estamos en el metodo que reemplaza");
		// comenzamos con la iteracion de los textos a reemplazar, se modifica para
		// utilizar con un arraylist o json
		// Desde base de datos se debe obtener esta informacion y hacerlo dinamico
		// cuando busque valores
		System.out.println("se debe tomar la desicion de que pdf se va a generar");
		Integer tipoReporte = 1;
		// variables globales
		JsonObject json = new JsonObject();
		String[] variablesWord = null;

		
		if (tipoReporte == 4) {
			
			json.put("nombre","Luis Mario Lemus");
			json.put("dui","548789524");
			json.put("montoAceptado","3000");
			json.put("destino"," destino ");
			
			
			
			
			String nameWord = (String) json.get("nombre");
			// System.out.println("tamaño del json "+json.size());

			// variables se van a llenar desde base de datos
			// crear captura de variables que no hagan match, bitacora
			variablesWord = new String[] { "noPoliza", "fecNacimiento", "telefono", "edadMeses", "dui", "giroNegocio",
					"direccionCli", "montoCredito", "plazo", "tipoCredito", "kg", "lbs",
					"mts", "diaDesde", "mesDesde", "anioDesde", "diasHasta", "mesHasta",
					"anioHasta", "ciudad", "dias", "mes", "anio" };
		}

		// doc = replaceText(doc, "<<nombre>>", "luis mario lemus");

		for (int i = 0; i < json.size(); i++) {

			String replace = (String) json.get(variablesWord[i]);
			 System.out.println("vars:"+variablesWord[i]+" json "+replace);
			doc = replaceText(doc, "{" + variablesWord[i] + "}", replace);
			if (variablesWord[i].equalsIgnoreCase("nombre")) {
				
				System.out.println("estamos llegando");
			//XWPFDocument doc = new XWPFDocument();
			  XWPFTable table = doc.createTable(1,2);
			  table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
			  table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2000));
			 
			  table.getRow(0).getCell(0).setText("1A");
			  table.getRow(0).getCell(1).setText("1B");
			  XWPFTableRow newrow = table.createRow();
			  newrow.getCell(0).setText("2A");
			  newrow.getCell(1).setText("2B");
			  
			}
		}
		for (int i = 0; i < variablesWord.length; i++) {
			String replace = (String) json.get(variablesWord[i]);
			// System.out.println("llegamos aqui");

		}
		// metodo que utiliza collection, pero se descarta por falta de costumbre
		Iterator it = textsToReplace.keySet().iterator();
		while (it.hasNext()) {
			String item = (String) it.next();
			// Enviamos a reemplazar el texto y guardamos sustituimos el documento en la
			// misma variable
			// doc = replaceText(doc, item, textsToReplace.get(item));
		}
		System.out.println("archivoTemporal " + temporalFileName);
		// Una vez se haya terminado de reemplazar, guardamos el documento en un archivo
		// temporal
		// Para ello general el archivo temporal que pasaremos a nuestro metodo que se
		// encarga de realizar la escritura
		String tmpDestinationPath = Files.createTempFile("temporalFileName", "." + Constantes.EXTENSION_DOCX)
				.toAbsolutePath().toString();
		System.out.println("tmpDestinationPath " + tmpDestinationPath);
		// Guardamos el documento en el archivo
		saveWord(tmpDestinationPath, doc);

		// Retornamos un ImputStream por si el usuario va a trabajar con el
		return new FileInputStream(tmpDestinationPath);
	}

	public InputStream replaceTextsAndConvertToPDF() throws Exception {
		try {
			System.out.println("llamamos el metodo");
			InputStream in = replaceTexts();
//			DocxToPdfConverter cwoWord = new DocxToPdfConverter();
			System.out.println("que es in " + in);
			return convert(in);
		} catch (Exception e) {
			throw e;
		}
	}

	// En el se carga el proceso de sustituir las variables de word y convertir a
	// pdf
	public void main()  {
		System.out.println(UUID.randomUUID().toString());
		System.out.println("1");

		// Archivo que indicamos la ruta a cargar para convertir a pdf
		Integer tipoReporte = 1;
		String filePath="";
		if (tipoReporte==1) {
			 filePath = "C:\\Users\\61888\\Desktop\\reports\\importarPyme.docx";
			// String filePathDestino = "C\\Users\\567\\Desktop\\pruebaConvertir.pdf";	
		}
		if (tipoReporte==2) {
			 filePath = "C:\\Users\\61888\\Desktop\\reports\\CARTAMOVILPYME.docx";
			// String filePathDestino = "C\\Users\\567\\Desktop\\pruebaConvertir.pdf";
			 
			  		}
		

		try {
			DocxHandling handling = new DocxHandling();
			// (Collections.singletonMap("{nombre}", "Rolando Mota Del Campo"), filePath,
			// filePathDestino);
			handling.setOriginalFilePath(filePath);
			System.out.println("iniciamos ");

			handling.setTextsToReplace(Collections.singletonMap("<<nombre>>", "Luis Lemus"));
			InputStream in = handling.replaceTextsAndConvertToPDF();
			// File destino = new File(filePathDestino);
			// System.out.println("file destino "+destino);
			// Files.copy(in, destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static XWPFDocument replaceText(XWPFDocument doc, String findText, String replaceText) {
		// Realizamos el recorrido de todos los parrafos
		//System.out.println("replaceText " + findText + "  " + replaceText + "  " + doc.getParagraphs().size());
		

		for (int i = 0; i < doc.getParagraphs().size(); ++i) {
			// Asignamos el parrafo a una variable para trabajar con ella
			XWPFParagraph s = doc.getParagraphs().get(i);

			// De ese parrafo recorremos todos los Runs
			for (int x = 0; x < s.getRuns().size(); x++) {
				// Asignamos el run en turno a una varibale
				XWPFRun run = s.getRuns().get(x);
				// Obtenemos el texto
				String text = run.text();
				// Validamos si el texto contiene el key a sustituir
				
				if (text.contains(findText)) {
					// System.out.println("replaceText "+findText + " "+replaceText);
					// Si lo contiene lo reemplazamos y guardamos en una variable
					String replaced = run.getText(run.getTextPosition()).replace(findText, replaceText);
					// Pasamos el texto nuevo al run
					run.setText(replaced, 0);
					 
				}
				
			}
		}
				// Retornamos el documento con los textos ya reemplazados
		return doc;
	}

	private static void saveWord(String filePath, XWPFDocument doc) throws FileNotFoundException, IOException {
		FileOutputStream out = null;
		try {
			
//			  //XWPFDocument doc = new XWPFDocument();
//			  XWPFTable table = doc.createTable(1,2);
//			  table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6000));
//			  table.getCTTbl().getTblGrid().addNewGridCol().setW(BigInteger.valueOf(2000));
//			  table.getRow(0).getCell(0).setText("1A");
//			  table.getRow(0).getCell(1).setText("1B");
//			  XWPFTableRow newrow = table.createRow();
//			  newrow.getCell(0).setText("2A");
//			  newrow.getCell(1).setText("2B");
			
			out = new FileOutputStream(filePath);
			doc.write(out);
		} finally {
			out.close();
		}
	}

	public void setTextsToReplace(Map<String, String> textsToReplace) {
		this.textsToReplace = textsToReplace;
	}

	public void setOriginalFilePath(String originalFilePath) {
		this.originalFilePath = originalFilePath;
	}
	
	public InputStream convert(InputStream in) {
		try {
			System.out.println("esto es convert ");
			File tmpFile = Files.createTempFile(UUID.randomUUID().toString(), "." + Constantes.EXTENSION_PDF).toFile();
			XWPFDocument document = new XWPFDocument(in);
			PdfOptions options = PdfOptions.create();
			System.out.println("esto es tmpFile "+tmpFile);
			OutputStream out = new FileOutputStream(tmpFile);
			PdfConverter.getInstance().convert(document, out, options);
			System.out.println("Aqui convertimose a pdf");
			return new FileInputStream(tmpFile);
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}
	public <T> T unmarshal(ObjectMapper objectMapper, String json, Class<T> clazz) throws IOException {
		T objectData = objectMapper.readValue(json, clazz);

		return objectData;
	}

}
