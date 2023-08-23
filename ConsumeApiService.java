package com.davi.reportesgenericos.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="api",url="localhost:8080/")
public interface ConsumeApiService {
	@RequestMapping(method = RequestMethod.GET,value="api/obtenerReporteXCodReporte/{COD_REPORTE}/{ID_PRODUCTO}/{ID_SISTEMA}")
//String getReporte(@PathVariable("PLANTILLA_ANEXO_A_CREDINEGOCIO_MIGC") String reporte, @PathVariable("a") String a, @PathVariable("b") String b);//repetri
	String getReporte(@PathVariable("COD_REPORTE") String reporte,@PathVariable("ID_SISTEMA") String sistema,@PathVariable("ID_PRODUCTO") String producto);
	
}
//http://localhost:8080/api/obtenerReporteXCodReporte/{COD_REPORTE}