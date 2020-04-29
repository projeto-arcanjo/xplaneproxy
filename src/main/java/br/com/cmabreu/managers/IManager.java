package br.com.cmabreu.managers;

import hla.rti1516e.AttributeHandle;

public interface IManager {
	AttributeHandle getSpatialHandle();
	AttributeHandle getMarkingHandle();
	AttributeHandle getDamageStateHandle();
	AttributeHandle getEntityIdentifierHandle();
	AttributeHandle getEntityTypeHandle();
	AttributeHandle getForceIdentifierHandle();
	AttributeHandle getIsConcealedHandle();
}
