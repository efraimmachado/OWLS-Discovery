package pf.vo;

import java.net.URI;
import java.util.ArrayList;


/**
 * @author Rodrigo Almeida de Amorim
 * @version 0.1
 */

/**
 * Class that contains the attributes of a service.
 */

public class Service {
	
	/**
	 * Attribute that holds all input parameters.
	 */
	private ArrayList<URI> inputList = null;
	
	/**
	 * Attribute that holds all output parameters.
	 */
	private ArrayList<URI> outputList = null;
	
	/**
	 * Attribute that holds all precondition parameters.
	 */
	private ArrayList<ArrayList<URI>> preconditionList = null;
	
	/**
	 * Attribute that holds all effect parameters.
	 */
	private ArrayList<ArrayList<URI>> effectList = null;
	
	/**
	 * Attribute that holds the URI of the service.
	 */
	private URI uri = null;
	
	/**
	 * Degree of Matching
	 */
	private String degreeMatch = null;
	
	/**
	 * Constructor of the class.
	 * @param inputList - Attribute with all inputs of the service.
	 * @param outputList - Attribute with all outputs of the service.
	 * @param uri - Attribute with the URI of the service.
	 */
	public Service(ArrayList<URI> inputList, ArrayList<URI> outputList, URI uri) {
		this.inputList = inputList;
		this.outputList = outputList;
		this.uri = uri;
		this.preconditionList = null;
		this.effectList = null;
	}
	
	/**
	 * Constructor of the class.
	 * @param inputList - Attribute with all inputs of the service.
	 * @param outputList - Attribute with all outputs of the service.
	 * @param preconditionList - Attribute with all precondition of the service.
	 * @param effectList - Attribute with all effect of the service.
	 * @param uri - Attribute with the URI of the service.
	 */
	

	public Service(ArrayList<URI> inputList, ArrayList<URI> outputList,  ArrayList<ArrayList<URI>> preconditionList,
			ArrayList<ArrayList<URI>> effectList, URI uri) {
		super();
		this.inputList = inputList;
		this.outputList = outputList;
		this.preconditionList = preconditionList;
		this.effectList = effectList;
		this.uri = uri;
	}


	/**
	 * @return the inputList.
	 */
	public ArrayList<URI> getInputList() {
		return inputList;
	}


	/**
	 * @param inputList the inputList to set.
	 */
	public void setInputList(ArrayList<URI> inputList) {
		this.inputList = inputList;
	}

	/**
	 * @return the outputList.
	 */
	public ArrayList<URI> getOutputList() {
		return outputList;
	}

	/**
	 * @param outputList the outputList to set.
	 */
	public void setOutputList(ArrayList<URI> outputList) {
		this.outputList = outputList;				
	}

	public ArrayList<ArrayList<URI>> getPreconditionList() {
		return preconditionList;
	}

	public void setPreconditionList(ArrayList<ArrayList<URI>> preconditionList) {
		this.preconditionList = preconditionList;
	}

	public ArrayList<ArrayList<URI>> getEffectList() {
		return effectList;
	}

	public void setEffectList(ArrayList<ArrayList<URI>> effectList) {
		this.effectList = effectList;
	}

	/**
	 * @return the uri.
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set.
	 */
	public void setUri(URI uri) {
		this.uri = uri;
	}

	/**
	 * @return the degreeMatch
	 */
	public String getDegreeMatch() {
		return degreeMatch;
	}

	/**
	 * @param degreeMatch the degreeMatch to set
	 */
	public void setDegreeMatch(String degreeMatch) {
		this.degreeMatch = degreeMatch;
	}
}
