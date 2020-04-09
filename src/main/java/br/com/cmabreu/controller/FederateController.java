package br.com.cmabreu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.cmabreu.models.XPlaneAircraft;
import br.com.cmabreu.services.FederateService;

@RestController
public class FederateController {
	
    @Autowired
    private FederateService federateService;	
	

    // In original code the Federation was destroyed after the main loop.
    // Now we must destroy it by calling this endpoint
    @RequestMapping(value = "/quit", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public @ResponseBody String quit() {
    	federateService.quit();
    	return "ok";
	}
	
	
	@RequestMapping(value = "/deleteobjectinstance", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public @ResponseBody String deleteObjectInstance( @RequestParam(value = "handle", required = true) Integer objectInstanceHandle ) {
		try {
			federateService.deleteObjectInstance( objectInstanceHandle );
		} catch ( Exception e ) {
			//
		}
		return "ok";
	}
	
	
	@RequestMapping(value = "/spawn", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public @ResponseBody XPlaneAircraft spawn( @RequestParam(value = "identificador", required = true) String identificador ) {
		try {
			XPlaneAircraft aircraft = federateService.spawn( identificador );
			return aircraft;
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
	}


	@RequestMapping(value = "/update", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public @ResponseBody XPlaneAircraft update( @RequestParam(value = "identificador", required = true) String identificador,
			@RequestParam(value = "lat", required = true) float lat,
			@RequestParam(value = "lon", required = true) float lon, @RequestParam(value = "alt", required = true) float alt ) {
		try {
			XPlaneAircraft aircraft = federateService.update( identificador, lat, lon, alt );
			return aircraft;
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}

