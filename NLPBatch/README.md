# NLPBatch  

The Natural Language Processing component is a batch process that is scheduled daily to look for new documents in the CMS platform providing an AtomPub CMIS API (the Alfresco open source CMS was chosen because it supports this kind of API) and perform the semantic processing.  
The processing involves a set of knowledge processing modules and in particular:  

* The TextRazor tool that parses, analyzes and semantically tags the document on the basis of WikiData and DBPedia ontologies.  
* The OpenLink Virtuoso Sponger, which creates RDF triples within the OpenLink Virtuoso Triple Store; these triple are enriched with TextRazor semantic triples.  

The semantic results of the NLP batch are used by the CMIS Browser web application and by an HTTP based endpoint to document SPARQL-based queries and retrieval.  