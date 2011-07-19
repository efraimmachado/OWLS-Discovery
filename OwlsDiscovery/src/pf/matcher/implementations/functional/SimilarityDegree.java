package pf.matcher.implementations.functional;

import java.net.URI;

/**
 * This class holds a request parameter and a possible similarity degree with a service parameter.
 * @author Rodrigo Almeida de Amorim
 *
 */
public class SimilarityDegree {

	/**
	 * A request parameter.
	 */	
	private URI requestParameter;
	
	/**
	 * A similarity degree between the parameters.
	 */
	private double similarityDegree;
	
	/**
	 * URI serviceParameter.
	 */
	private URI serviceParameter;
	
	/**
	 * Constructor of the class
	 */
	
	public SimilarityDegree() {
	}
	
	public SimilarityDegree(URI requestParameter, double similarityDegree, URI serviceParameter) {
		this.requestParameter = requestParameter;
		this.similarityDegree = similarityDegree;
		this.serviceParameter = serviceParameter;
	}
	

	/**
	 * @return the requestParameter.
	 */
	public URI getRequestParameter() {
		return requestParameter;
	}

	/**
	 * @param requestParameter the requestParameter to set.
	 */
	public void setRequestParameter(URI requestParameter) {
		this.requestParameter = requestParameter;
	}

	/**
	 * @return the similarityDegree.
	 */
	public double getSimilarityDegree() {
		return similarityDegree;
	}

	/**
	 * @param similarityDegree the similarityDegree to set.
	 */
	public void setSimilarityDegree(double similarityDegree) {
		this.similarityDegree = similarityDegree;
	}

	/**
	 * @return the serviceParameter.
	 */
	public URI getServiceParameter() {
		return serviceParameter;
	}

	/**
	 * @param serviceParametre the serviceParameter to set.
	 */
	public void setServiceParameter(URI serviceParametre) {
		this.serviceParameter = serviceParametre;
	}

        public String toString()
        {
            return serviceParameter.toString();
        }
}
