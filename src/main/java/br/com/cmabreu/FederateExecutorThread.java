package br.com.cmabreu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.cmabreu.services.FederateService;

public class FederateExecutorThread implements Runnable {
	private Logger logger = LoggerFactory.getLogger( FederateExecutorThread.class );
	private FederateService federateService;
	
	
	public FederateExecutorThread( FederateService federateService ) {
		this.federateService = federateService;
	}
	
	@Override
	public void run(  ) {
		logger.info("Executando processo principal");
		
		while(true) {
			//federateService.evokeCallBacks();	
		}
				
	}

}
