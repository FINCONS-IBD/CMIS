package com.fincons.util;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class RDFQuadruple {

	private RDFNode subject;
	private RDFNode predicate;
	private RDFNode object;
	private RDFNode labelReadable;
	
	
	
	public RDFQuadruple(RDFNode subject, RDFNode predicate, RDFNode object, RDFNode labelReadable) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		this.labelReadable = labelReadable;
	}
	
	public RDFNode getSubject() {
		return subject;
	}
	public void setSubject(RDFNode subject) {
		this.subject = subject;
	}
	public RDFNode getPredicate() {
		return predicate;
	}
	public void setPredicate(RDFNode predicate) {
		this.predicate = predicate;
	}
	public RDFNode getObject() {
		return object;
	}
	public void setObject(RDFNode object) {
		this.object = object;
	}
	public RDFNode getLabelReadable() {
		return labelReadable;
	}
	public void setLabelReadable(RDFNode labelReadable) {
		this.labelReadable = labelReadable;
	}
	
	
	
}
