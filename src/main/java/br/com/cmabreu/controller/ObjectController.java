package br.com.cmabreu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.cmabreu.services.ObjectService;
import br.com.cmabreu.udp.AircraftData;
import br.com.cmabreu.udp.ObjectData;


@RestController
@RequestMapping("/xplane")
public class ObjectController {
	
	@Autowired
	private ObjectService objectService;
	
	@RequestMapping(value = "/aircraft/data/{aircraftNumber}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public @ResponseBody AircraftData getAircraftData( @PathVariable("aircraftNumber") Integer aircraftNumber ) {
		try {
			return objectService.getAircraftData(aircraftNumber);
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}		
	}
	
	@RequestMapping(value = "/send", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public void sendText( @RequestParam(value = "message", required = true) String message ) {
		try {
			objectService.sendText( message );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
	@RequestMapping(value = "/object/spawn", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ObjectData spawn( 
			@RequestParam(value = "lat", required = true) Double lat,
			@RequestParam(value = "lon", required = true) Double lon,
			@RequestParam(value = "ele", required = true) Double ele,
			@RequestParam(value = "psi", required = true) Float psi,
			@RequestParam(value = "the", required = true) Float the,
			@RequestParam(value = "phi", required = true) Float phi,
			@RequestParam(value = "ground", required = true) Long ground,
			@RequestParam(value = "smoke", required = true) Float smoke,
			@RequestParam(value = "objectPath", required = true) String objectPath 	) {
		try {
			return objectService.spawn( lat, lon, ele, psi, the, phi, ground, smoke, objectPath );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "/object/heading/{index}/", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public void setHeading(
			@PathVariable("index") Integer index,
			@RequestParam(value = "psi", required = true) Float psi ) {
		objectService.setHeading(index, psi);
	}
	
	
	@RequestMapping(value = "/object/{index}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public ObjectData getObject( @PathVariable("index") Integer index ) {
		return objectService.getObject(index);
	}
	

}
