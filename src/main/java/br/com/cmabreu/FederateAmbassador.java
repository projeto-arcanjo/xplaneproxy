package br.com.cmabreu;

import hla.rti1516e.NullFederateAmbassador;

public class FederateAmbassador extends NullFederateAmbassador {
	private FederateManager federateManager;

	public FederateAmbassador( FederateManager federateManager ){
		this.federateManager = federateManager;
	}


}
