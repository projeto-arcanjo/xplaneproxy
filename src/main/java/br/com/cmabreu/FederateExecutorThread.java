package br.com.cmabreu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FederateExecutorThread implements Runnable {
	private Logger logger = LoggerFactory.getLogger( FederateExecutorThread.class );
	private FederateService federateService;
	
	
	public FederateExecutorThread( FederateService federateService ) {
		this.federateService = federateService;
	}
	
	@Override
	public void run(  ) {
		
		try {
			federateService.kickOff();
		} catch( Exception e ) {
			logger.error( e.getCause() + " : " +  e.getMessage() );
		}		
		
	}

}
