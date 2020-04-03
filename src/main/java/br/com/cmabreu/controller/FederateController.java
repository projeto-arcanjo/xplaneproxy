package br.com.cmabreu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.cmabreu.FederateExecutorThread;
import br.com.cmabreu.FederateService;

@RestController
public class FederateController {
	
    @Autowired
    private FederateService federateService;	
	

    // In original code the Federation was destroyed after the main loop.
    // Now we must destroy it by calling this endpoint
    @RequestMapping(value = "/destroyfederation", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public @ResponseBody String destroyFederation() {
    	federateService.destroyFederation();
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

    	
    	
    // This will replace the user input to start the simulation
	@RequestMapping(value = "/start", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_UTF8_VALUE )
	public @ResponseBody String start () {

		// Do not block the web browser interface!
		Runnable runnable = new FederateExecutorThread( federateService );
		Thread thread = new Thread(runnable);
		thread.start();
		
		return "OK";
	}	
	
	
}

