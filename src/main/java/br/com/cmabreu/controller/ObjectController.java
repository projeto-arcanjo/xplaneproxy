package br.com.cmabreu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.cmabreu.models.XPlaneObjectDTO;
import br.com.cmabreu.services.ObjectService;
import br.com.cmabreu.udp.AircraftData;


@RestController
@RequestMapping("/xplane")
public class ObjectController {
	
	@Autowired
	private ObjectService objectService;
	
	@RequestMapping(value = "/autoupdate", method = RequestMethod.GET )
	public void setAutoUpdate( @RequestParam(value = "autoupdate", required = true) Boolean autoUpdate ) {
		try {
			objectService.setAutoUpdate( autoUpdate );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	
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
	public XPlaneObjectDTO spawn( 
			@RequestParam(value = "lat", required = true) Double lat,
			@RequestParam(value = "lon", required = true) Double lon,
			@RequestParam(value = "ele", required = true) Double ele,
			@RequestParam(value = "psi", required = true) Float psi,
			@RequestParam(value = "the", required = true) Float the,
			@RequestParam(value = "phi", required = true) Float phi,
			@RequestParam(value = "ground", required = true) Integer ground,
			@RequestParam(value = "smoke", required = true) Float smoke,
			@RequestParam(value = "objectPath", required = true) String objectPath,
			@RequestParam(value = "objectName", required = true) String objectName) {
		try {
			return objectService.spawn( lat, lon, ele, psi, the, phi, ground, smoke, objectPath, objectName );
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
	

	@RequestMapping(value = "/object/smoke/{index}/", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public void setSmoke(
			@PathVariable("index") Integer index,
			@RequestParam(value = "amount", required = true) Float amount ) {
		objectService.setSmoke(index, amount);
	}
	
	
	@RequestMapping(value = "/object/{index}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public XPlaneObjectDTO getObject( @PathVariable("index") Integer index ) {
		return objectService.getObjectDTO(index);
	}
	

}
