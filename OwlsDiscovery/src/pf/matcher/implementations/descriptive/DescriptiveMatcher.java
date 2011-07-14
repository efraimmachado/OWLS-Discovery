/**
 * 
 */
package pf.matcher.implementations.descriptive;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import pf.io.input.DictionaryReader;
import pf.matcher.implementations.functional.SimilarityDegree;
import pf.matcher.interfaces.IMatcher;
import pf.vo.Query;
import pf.vo.Service;

/**
 * @author rodrigo.amorim
 *
 */
public class DescriptiveMatcher implements IMatcher {

	/**
	 * This constant represents ancestor classes.
	 */
	public static final char ANCESTOR = 'A';
	
	/**
	 * This constant represents sibling classes.
	 */
	public static final char SIBLING = 'S';
	
	/**
	 * This constant represents leaf classes.
	 */
	public static final char LEAF = 'L';
	
	/**
	 * This constant represents immediate child classes.
	 */
	public static final char IMMEDIATE_CHILD = 'I';
	
	/**
	 * This variable represents the threshold required by the user.
	 */
	private double threshold;
	
	/**
	 * This variable represents the coefficient that will be used to calculate the structural similarity
	 */
	private double immediateChildCoefficient;
	
	/**
	 * This variable represents the coefficient that will be used to calculate the structural similarity
	 */
	private double leafCoefficient;
	
	/**
	 * This variable represents the coefficient that will be used to calculate the structural similarity
	 */
	private double ancestorCoefficient;
	
	/**
	 * This variable represents the coefficient that will be used to calculate the structural similarity
	 */
	private double siblingCoefficient;
	
	/**
	 * This variable represents the coefficient that will be used to calculate the structural similarity
	 */
	private double basicCoefficient;
	
	/**
	 * This variable represents the coefficient that will be used to calculate the structural similarity
	 */
	private double structuralCoefficient;
	
	/**
	 * This variable will read the dictionary to calculate the descriptive similarity between the words
	 */
	private DictionaryReader dictionaryReader;
	
	/**
	 * An ontology.
	 */
	private String ontology = null;
	
	/**
	 * This constant represents an Input parameters.
	 */
	public static final char INPUT = 'I';

	/**
	 * This Constant represents an Output parameters.
	 */
	public static final char OUTPUT = 'O';
	
	/**
	 * An inferencing model.
	 */
	private OntModel model = null;
	
	/**
	 * An ontology model
	 */
	OntModelSpec ontModel = null;
	
	/**
	 * Attribute that holds all request parameters.
	 */
	private ArrayList<URI> requestParameters;
	
	/**
	 * An array that holds all correspondences between the inputs.
	 */
	private ArrayList<ArrayList<SimilarityDegree>> resultInputs = null;

	/**
	 * An array that holds all correspondences between the outputs.
	 */
	private ArrayList<ArrayList<SimilarityDegree>> resultOutputs = null;
	
	/**
	 * Default construct of of the class.
	 * @param basicCoefficient - coefficient of the basic similarity.
	 * @param structuralCoefficient - coefficient of the structural similarity.
	 * @param ancestorCoefficient - coefficient used in the structural similarity, it corresponds ancestors similarity
	 * @param immediateChldCoefficient - coefficient used in the structural similarity, it corresponds immediate children similarity
	 * @param leafCoefficient - coefficient used in the structural similarity, it corresponds leaves similarity
	 * @param siblingCoefficient - coefficient used in the structural similarity, it corresponds siblings similarity
	 * @param threshold - level of accuracy requested.
	 */
	public DescriptiveMatcher(double basicCoefficient, double structuralCoefficient, double ancestorCoefficient,
						   double immediateChldCoefficient, double leafCoefficient,
						   double siblingCoefficient, double threshold, String fileName) {
		
		this.threshold = threshold;
		this.basicCoefficient = basicCoefficient;
		this.structuralCoefficient = structuralCoefficient;
		this.ancestorCoefficient = ancestorCoefficient;
		this.immediateChildCoefficient = immediateChldCoefficient;
		this.leafCoefficient = leafCoefficient;
		this.siblingCoefficient = siblingCoefficient;
		
		dictionaryReader = new DictionaryReader(fileName);
		
		ontModel = PelletReasonerFactory.THE_SPEC;
		model = ModelFactory.createOntologyModel(ontModel);
	}
	
	/**
	 * Method that return the list of classes based on the property specified as parameter.
	 * @param parameter - URI that contains the class of the ontology
	 * @param resource - 
	 * @param owlProperty
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<URI> getListOfClasses(URI parameter, char owlProperty) {
		List<URI> classes = new ArrayList<URI>();
		URI uri = null;
		OntClass ontClass = null;
		Iterator iter = null;
		String[] vectorString;
		
		ontClass = model.getOntClass(parameter.toString());
		
		
		if (owlProperty == ANCESTOR) {
			iter = ontClass.listSuperClasses();
		} else if (owlProperty == SIBLING) {
			ontClass = ontClass.getSuperClass();
			iter = ontClass.listSubClasses(true);
		} else if (owlProperty == IMMEDIATE_CHILD) {
			iter = ontClass.listSubClasses(true);
		} else if (owlProperty == LEAF) {
			iter = ontClass.listSubClasses();
		}
		
		try {
			while(iter.hasNext()) {
				ontClass = (OntClass) iter.next();
				String next = ontClass.toString();
				vectorString = next.split("#");
				uri = new URI(next);
				
				/*
				 * Was the split correct!? this if verifies it! 
				 */
				if (vectorString.length > 1) {
					/*
					 * This 'if' test whether the loaded list is LEAF, if it's, only classes that don't have children
					 * have to be added in the final result. 
					 */
					if((owlProperty == LEAF) && (verifyIfitsLeaf(ontClass))) {
						classes.add(uri);
					} else if (owlProperty != LEAF && (!next.equals(parameter.toString()) && (!"Nothing".equalsIgnoreCase(vectorString[1])))) {
						classes.add(uri);
					}
				}	
			}	
		} catch (URISyntaxException e) {
			
		}
		return classes;
	}
	/**
	 * Method that verifies if the class is a leaf in the ontology
	 * @param ontClass - class that will be analyzed
	 * @return Return true if the class is a leaf and false otherwise.
	 */
	@SuppressWarnings("unchecked")
	private boolean verifyIfitsLeaf(OntClass ontClass) {
		Iterator<OntClass> iter = null;
		iter = ontClass.listSubClasses(true);
		String[] vectorString;
		
		while(iter.hasNext()) {
			ontClass = (OntClass) iter.next();
			String next = ontClass.toString();
			vectorString = next.split("#");
			
			if ("Nothing".equalsIgnoreCase(vectorString[1])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Method that calculates the descriptive similarity among the ancestors of the classes specified by the request and presented by the service
	 * @param requestParameter - A request parameter.
	 * @param responseParameter - A response parameter.
	 * @return A value that represents the descriptive similarity among the ancestors classes.
	 */
	@SuppressWarnings("unchecked")
	private double caculateStructuralSimilarity(URI requestParameter, URI responseParameter, char owlProperty) {
		List classesFromRequest = null;
		List classesFromResponse = null;
		ArrayList values = new ArrayList<Double>();
		boolean isOk = false;
		double arithmeticMean = 0;
		double standartDeviation = 0;
		
		classesFromRequest = this.getListOfClasses(requestParameter, owlProperty);
		classesFromResponse = this.getListOfClasses(responseParameter, owlProperty);
				
		/* If one of the list is empty, so it's impossible to calculate a syntcatic similarity
		 * so, we return 0 (Zero)
		 */
		if((classesFromRequest.size() == 0) || (classesFromResponse.size() == 0)) {
			return 0;
		}
		
		for(int i = 0; i < classesFromRequest.size(); i++) {
			for(int j = 0; j < classesFromResponse.size(); j++) {
				values.add(this.calculateDescriptiveSimilarity((URI)classesFromRequest.get(i), (URI)classesFromResponse.get(j)));
			}
		}
		
		/*
		 * The variable isOk is true if only if the (standard deviation / arithmetic mean) < threshold 
		 */
		
		while(isOk == false) {
			arithmeticMean = this.calculateArithmeticMean(values);
			standartDeviation = this.calculateStandardDeviation(values, arithmeticMean);
			
			if(this.threshold < (standartDeviation/arithmeticMean)) {
				for(int i = 0; i < values.size(); i++) {
					if((Double)values.get(i) < (arithmeticMean * (1 - this.threshold))) {
						values.remove(i);
						i--;
					}
				}
			} else {
				isOk = true; 
			}
		}
		return arithmeticMean;
	}
	
	/**
	 * Method that calculates the descriptive similarity.
	 * @param requestParameter - A request parameter.
	 * @param responseParameter - A response parameter.
	 * @return SimilarityDegree - An object who holds informations about the similarity.
	 */
	private double calculateDescriptiveSimilarity(URI requestParameter, URI serviceParameter) {
		
		String[] vectorRequestString = requestParameter.toString().split("#");
		String[] vectorServiceString = serviceParameter.toString().split("#");
		double result = dictionaryReader.calculateDescriptiveSimilarity(vectorRequestString[1], vectorServiceString[1]); 
		return(result);
	}
	
	/**
	 * Method that calculates the arithmetic mean of the elements from the matrix.
	 * @param values - ArrayList with the elements from the matrix.
	 * @return An arithmetic mean of the elements.
	 */
	private double calculateArithmeticMean(ArrayList<Double> values) {
		double sum = 0;
		
		for(int i = 0; i < values.size(); i++) {
				sum += values.get(i);
			}	
		return (sum/(values.size()));
	}
	
	/**
	 * Method that calculates the arithmetic mean of the elements from the matrix.
	 * @param values - ArrayList with the elements from the matrix.
	 * @param arithmeticMean - Element that holds the arithmetic mean calculated before.
	 * @return  The standard deviation from the values from the matrix.
	 */
	private double calculateStandardDeviation(ArrayList<Double> values, double arithmeticMean) {
		double sum = 0;
		
		for(int i = 0; i < values.size(); i++) {
				sum += Math.pow((values.get(i) - arithmeticMean), 2.0);
			}	
		return Math.sqrt((sum/values.size() - 1)) ;
	}
	
	/**
	 * Method that calculates the basic descriptive similarity between the words.
	 * @param requestParameter - A request parameter. 
	 * @param responseParameter - A response parameter.
	 * @return Return the basic descriptive similarity
	 */
	private double calculateBasicSimilarity(URI requestParameter, URI responseParameter) {
		return this.calculateDescriptiveSimilarity(requestParameter, responseParameter);
	}
	
	/**
	 * Method that calculates the aggregation of the structural similarity.
	 * (ancestors, immediate children, sibling and leaves).
	 * @param serviceParameter - A request parameter. 
	 * @param requestParameter - A response parameter.
	 * @return - Return the aggregation of the structural similarity.
	 */
	private double calculateAggStructuralSimilarity(URI serviceParameter, URI requestParameter) {
		double immediateChildSimilarity = 0.0;
		double siblingSimilarity = 0.0;
		double leafSimilarity = 0.0;
		double ancestorSimilarity = 0.0;
				
		ancestorSimilarity = this.caculateStructuralSimilarity(requestParameter, serviceParameter, ANCESTOR);
		siblingSimilarity = this.caculateStructuralSimilarity(requestParameter, serviceParameter, SIBLING);
		immediateChildSimilarity = this.caculateStructuralSimilarity(requestParameter, serviceParameter, IMMEDIATE_CHILD);
		leafSimilarity = this.caculateStructuralSimilarity(requestParameter, serviceParameter, LEAF);
		
		return ((ancestorSimilarity * ancestorCoefficient) + (siblingSimilarity * siblingCoefficient) +
			   (immediateChildSimilarity * immediateChildCoefficient) + (leafSimilarity * leafCoefficient));
	}
	
	/**
	 * Method that creates an object SimilarityDegree with the parameters matched.
	 * @param serviceParameter - A service parameter. 
	 * @param requestParameter - A request parameter.
	 * @param basicSimilarity - Basic similarity calculated.
	 * @param structuralSimilarity - Structural similarity calculated.
	 * @return - Return an SimilarityDegree object with the parameters previous matched.
	 */
	private SimilarityDegree calculateTotalSimilarity(URI serviceParameter, URI requestParameter,
													  double basicSimilarity, double structuralSimilarity) {
		double totalSimilarity = 0.0;
		
		totalSimilarity = ((basicSimilarity * basicCoefficient) + (structuralSimilarity * structuralCoefficient));
		return new SimilarityDegree(requestParameter, totalSimilarity, serviceParameter);
	}
	
	/**
	 * Method that performs the matching among the words (basic and structural).
	 * 
	 * @param serviceParameters -
	 *            An ArrayList with all service parameters.
	 * @param typeOfParameters -
	 *            Type of parameters, Input or Output.
	 * @return - return an ArrayList with all correspondences matched.
	 */
	private ArrayList<SimilarityDegree> calculateDescriptiveMatcherSimilarity (ArrayList<URI> serviceParameters, char typeOfParameters) {
		double basicSimilarity = 0.0;
		double structuralSimilarity = 0.0;
		ArrayList<SimilarityDegree> matchedParameters = new ArrayList<SimilarityDegree>();
		URI requestParameter = null;
		URI serviceParameter = null;
		
		/*
		 * verifying the quantity of parameters in input and output.
		 */
		if (typeOfParameters == DescriptiveMatcher.INPUT) {
			if (requestParameters.size() < serviceParameters.size()) {
				matchedParameters.add(new SimilarityDegree(requestParameter,
						0.0, serviceParameter));
				return matchedParameters;
			}
		} else if (typeOfParameters == DescriptiveMatcher.OUTPUT) {
			if (requestParameters.size() > serviceParameters.size()) {
				matchedParameters.add(new SimilarityDegree(requestParameter,
						0.0, serviceParameter));
				return matchedParameters;
			}
		}
		
		for (int i = 0; i < serviceParameters.size(); i++) {
			for (int j = 0; j < requestParameters.size(); j++) {
				if (hasSameOntology(serviceParameters.get(i), requestParameters.get(j))) {
					
					//Loading the ontology in the model
					loadOntology(requestParameters.get(j).getScheme() + ":"
							+ requestParameters.get(j).getSchemeSpecificPart() + "#");
					
					basicSimilarity = calculateBasicSimilarity(requestParameters.get(j), serviceParameters.get(i));
					structuralSimilarity = calculateAggStructuralSimilarity(serviceParameters.get(i), requestParameters.get(j));
					matchedParameters.add(calculateTotalSimilarity(serviceParameters.get(i), requestParameters.get(j), basicSimilarity, structuralSimilarity));
				} else {
					matchedParameters.add(calculateTotalSimilarity(serviceParameters.get(i), requestParameters.get(j), 0.0, 0.0));
				}
			}
		}
		return matchedParameters;
	}
	
	/**
	 * Method that has to be implemented due the interface. It manager all the
	 * methods in the class FunctionalMatcher.
	 * 
	 * @param services -
	 *            An ArrayList with all services loaded.
	 * @param request -
	 *            A Query with data from request loaded.
	 */
	public void matcher(ArrayList<Service> services, Query request) {
		resultInputs = new ArrayList<ArrayList<SimilarityDegree>>();
		resultOutputs = new ArrayList<ArrayList<SimilarityDegree>>();
		
		for (int i = 0; i < services.size(); i++) {
			requestParameters = request.getInputList();
			resultInputs.add(calculateDescriptiveMatcherSimilarity(services.get(i).getInputList(), DescriptiveMatcher.INPUT));
			requestParameters = request.getOutputList();
			resultOutputs.add(calculateDescriptiveMatcherSimilarity(services.get(i).getOutputList(), DescriptiveMatcher.OUTPUT));
		}	
	}
	
	/**
	 * Method that verify if the parameters have the same source/Ontology.
	 * 
	 * @param requestParameter -
	 *            A request parameter.
	 * @param serviceParameter -
	 *            A service parameter.
	 * @return true/false - return true if the sources are the same and false if
	 *         they are different.
	 */
	private boolean hasSameOntology(URI requestParameter, URI serviceParameter) {
		if (requestParameter.getSchemeSpecificPart().equals(
				serviceParameter.getSchemeSpecificPart())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Method that test if the new ontology is equal the one loaded before.
	 * 
	 * @param localOntology -
	 *            New ontology to be tested.
	 */
	private void loadOntology(String localOntology) {
		if (!(localOntology.equals(ontology))) {
			ontology = localOntology;
			model.read(ontology);
		}
	}
	
	/**
	 * @return the resultInputs
	 */
	public ArrayList<ArrayList<SimilarityDegree>> getResultInputs() {
		return resultInputs;
	}

	/**
	 * @return the resultOutputs
	 */
	public ArrayList<ArrayList<SimilarityDegree>> getResultOutputs() {
		return resultOutputs;
	}
}
