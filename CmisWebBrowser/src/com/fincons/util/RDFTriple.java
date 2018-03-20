package com.fincons.util;

import com.hp.hpl.jena.rdf.model.RDFNode;

public class RDFTriple {
	
	RDFNode subject;
	RDFNode predicate;
	RDFNode object;
	
	public RDFTriple(RDFNode subject, RDFNode predicate, RDFNode object) {
		super();
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
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

}
