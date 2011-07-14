package pf.matcher.implementations.pe;

import java.util.ArrayList;

import pf.matcher.implementations.functional.SimilarityDegree;
import pf.matcher.interfaces.IMatcher;
import pf.vo.Query;
import pf.vo.Service;

import java.net.URI;
import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * This class implements a functional matcher based on Pallouci's works.
 * 
 * @author Rodrigo Almeida de Amorim
 * @version 0.1
 */
public class PEMatcher implements IMatcher {
	
	/*
	 * Constants
	 */

	/**
	 * This constant represents a lack of match.
	 */
	public static final int NOTHING = 5;

	/**
	 * This constant represents an exact match.
	 */
	public static final int EXACT = 4;

	/**
	 * This constant represents a plug-in match.
	 */
	public static final int PLUGIN = 3;

	/**
	 * This constant represents a subsumes match.
	 */
	public static final int SUBSUMES = 2;

	/**
	 * This constant represents a sibling match.
	 */
	public static final int SIBLING = 1;

	/**
	 * This constant represents a fail match.
	 */
	public static final int FAIL = 0;

	/**
	 * An ontology.
	 */
	private String ontology = null;

	/**
	 * An inferencing model.
	 */
	private OntModel model = null;

	/**
	 * An ontology model
	 */
	OntModelSpec ontModel = null;

	/**
	 * A Resource
	 */
	private Resource resource = null;

	/**
	 * This constant represents an PRECONDITION parameters.
	 */
	public static final char PRECONDITION = 'P';

	/**
	 * This Constant represents an EFFECT parameters.
	 */
	public static final char EFFECT = 'E';	
	
	/**
	 * An array that holds all correspondences between the PRECONDITIONs.
	 */
	private ArrayList<SimilarityDegree> resultPreconditions = null;

	/**
	 * An array that holds all correspondences between the EFFECTs.
	 */
	private ArrayList<SimilarityDegree> resultEffects = null;

	/**
	 * Constructor of the class, it expects an ArrayList of URI from request.
	 */
	public PEMatcher() {
		ontModel = PelletReasonerFactory.THE_SPEC;
		model = ModelFactory.createOntologyModel(ontModel);
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
		
		resultPreconditions = matcherPreConditions(services, request);
		resultEffects = matcherEffects(services, request);		
		
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
	 * Method that calculates if two elements of an ontology are exact.
	 * 
	 * @param requestParameter -
	 *            A request parameter.
	 * @param serviceParameter -
	 *            A service parameter.
	 * @param typoOfParameter -
	 *            It informs if the parameters are inputs or outputs.
	 * @return true/false - return true if they are the same and false if they
	 *         are different.
	 */
	@SuppressWarnings("unchecked")
	private boolean isExactMatching(URI requestParameter, URI serviceParameter,
			char typeOfParameter) {
		Iterator iterClass = null;
		Iterator iterProperty = null;
		NodeIterator iter = null;
		OntClass ontClass = null;
		OntProperty ontProperty = null;

		loadOntology(requestParameter.getScheme() + ":"
				+ requestParameter.getSchemeSpecificPart() + "#");

		if (typeOfParameter == PRECONDITION) {
			
			resource = model.getResource(ontology + requestParameter.getFragment());
			iter = model.listObjectsOfProperty(resource, OWL.equivalentClass);
			while (iter.hasNext()) {
				if (serviceParameter.toString().equals(iter.next().toString())) {
					return true;
				}
			}
			
			ontClass = model.getOntClass(requestParameter.toString());
			iterClass = ontClass.listSubClasses(true);
			while (iterClass.hasNext()) {
				if (serviceParameter.toString().equals(
						iterClass.next().toString())) {
					return true;
				}
			}
			
			if(ontClass.isProperty()) {
				iter = model.listObjectsOfProperty(resource, OWL.equivalentProperty);			
				while (iter.hasNext()) {
					if (serviceParameter.toString().equals(iter.next().toString())) {
						return true;
					}
				}
				
				ontProperty = model.getOntProperty(requestParameter.toString());
				iterProperty = ontProperty.listSubProperties(true);
				while (iterProperty.hasNext()) {
					if (serviceParameter.toString().equals(
							iterProperty.next().toString())) {
						return true;
					}
				}
				
			}
			
		} else if (typeOfParameter == EFFECT) {
			resource = model.getResource(ontology
					+ requestParameter.getFragment());
			iter = model.listObjectsOfProperty(resource, OWL.equivalentClass);
			while (iter.hasNext()) {
				if (serviceParameter.toString().equals(iter.next().toString())) {
					return true;
				}
			}

			ontClass = model.getOntClass(serviceParameter.toString());
			iterClass = ontClass.listSubClasses(true);
			while (iterClass.hasNext()) {
				if (requestParameter.toString().equals(
						iterClass.next().toString())) {
					return true;
				}
			}
			
			if(ontClass.isProperty()) {				
				iter = model.listObjectsOfProperty(resource, OWL.equivalentProperty);
				while (iter.hasNext()) {
					if (serviceParameter.toString().equals(iter.next().toString())) {
						return true;
					}
				}

				ontProperty = model.getOntProperty(serviceParameter.toString());
				iterProperty = ontProperty.listSubProperties(true);
				while (iterProperty.hasNext()) {
					if (requestParameter.toString().equals(
							iterProperty.next().toString())) {
						return true;
					}
				}
				
			}		
			
		}
		if (requestParameter.toString().equals(serviceParameter.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * Method that calculates if two elements of an ontology are plug-in.
	 * 
	 * @param requestParameter -
	 *            A request parameter.
	 * @param serviceParameter -
	 *            A service parameter.
	 * @param typoOfParameter -
	 *            It informs if the parameters are inputs or outputs.
	 * @return true/false - return true if they are the same and false if they
	 *         are different.
	 */
	@SuppressWarnings("unchecked")
	private boolean isPluginMatching(URI requestParameter,
			URI serviceParameter, char typeOfParameter) {
		Iterator iter = null;
		OntClass ontClass = null;
		OntProperty ontProperty = null;

		loadOntology(requestParameter.getScheme() + ":"
				+ requestParameter.getSchemeSpecificPart() + "#");

		if (typeOfParameter == PRECONDITION) {
			ontClass = model.getOntClass(requestParameter.toString());
			iter = ontClass.listSubClasses();
			while (iter.hasNext()) {
				String node = iter.next().toString();
				if (serviceParameter.toString().equals(node)) {
					return true;
				}
			}
			
			if(ontClass.isProperty()) {
				ontProperty = model.getOntProperty(requestParameter.toString());
				iter = ontProperty.listSubProperties();
				while (iter.hasNext()) {
					String node = iter.next().toString();
					if (serviceParameter.toString().equals(node)) {
						return true;
					}
				}
				
			}
		} else if (typeOfParameter == EFFECT) {
			ontClass = model.getOntClass(serviceParameter.toString());
			iter = ontClass.listSubClasses();
			while (iter.hasNext()) {
				if (requestParameter.toString().equals(iter.next().toString())) {
					return true;
				}
			}
			if(ontClass.isProperty()) {
				ontProperty = model.getOntProperty(serviceParameter.toString());
				iter = ontProperty.listSubProperties();
				while (iter.hasNext()) {
					if (requestParameter.toString().equals(iter.next().toString())) {
						return true;
					}
				}
				
			}
		}
		return false;
	}

	/**
	 * Method that calculates if two elements of an ontology are subsumes.
	 * 
	 * @param requestParameter -
	 *            A request parameter.
	 * @param serviceParameter -
	 *            A service parameter.
	 * @param typoOfParameter -
	 *            It informs if the parameters are inputs or outputs.
	 * @return true/false - return true if they are the same and false if they
	 *         are different.
	 */
	@SuppressWarnings("unchecked")
	private boolean isSubsumesMatching(URI requestParameter,
			URI serviceParameter, char typeOfParameter) {
		Iterator iter = null;
		OntClass ontClass = null;
		OntProperty ontProperty = null;

		loadOntology(requestParameter.getScheme() + ":"
				+ requestParameter.getSchemeSpecificPart() + "#");

		if (typeOfParameter == PRECONDITION) {
			ontClass = model.getOntClass(serviceParameter.toString());			
			iter = ontClass.listSubClasses();
			while (iter.hasNext()) {
				if (requestParameter.toString().equals(iter.next().toString())) {
					return true;
				}
			}
			if(ontClass.isProperty()) {
				ontProperty = model.getOntProperty(serviceParameter.toString());			
				iter = ontProperty.listSubProperties();
				while (iter.hasNext()) {
					if (requestParameter.toString().equals(iter.next().toString())) {
						return true;
					}
				}
				
			}
			
		} else if (typeOfParameter == EFFECT) {
			ontClass = model.getOntClass(requestParameter.toString());
			iter = ontClass.listSubClasses();
			while (iter.hasNext()) {
				if (serviceParameter.toString().equals(iter.next().toString())) {
					return true;
				}
			}
			
			if(ontClass.isProperty()) {
				ontProperty = model.getOntProperty(requestParameter.toString());
				iter = ontProperty.listSubProperties();
				while (iter.hasNext()) {
					if (serviceParameter.toString().equals(iter.next().toString())) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Method that calculates if two elements of an ontology are siblings.
	 * 
	 * @param requestParameter -
	 *            A request parameter.
	 * @param serviceParameter -
	 *            A service parameter.
	 * @param typoOfParameter -
	 *            It informs if the parameters are inputs or outputs.
	 * @return true/false - return true if they are the same and false if they
	 *         are different.
	 */
	@SuppressWarnings("unchecked")
	private boolean isSiblingMatching(URI requestParameter,
			URI serviceParameter, char typeOfParameter) {

		boolean gotSuperClass = false;
		boolean gotSuperProperty = false;
		OntClass ontClass = null;
		Iterator iter = null;
		OntProperty ontProperty = null;

		loadOntology(requestParameter.getScheme() + ":"
				+ requestParameter.getSchemeSpecificPart() + "#");

		ontClass = model.getOntClass(serviceParameter.toString());
		iter = ontClass.listSuperClasses(true);
		if (iter.hasNext()) {
			ontClass = (OntClass) iter.next();
			gotSuperClass = true;
		}
		if (gotSuperClass) {
			iter = ontClass.listSubClasses(true);
		}
		while (iter.hasNext()) {
			String next = iter.next().toString();
			if (requestParameter.toString().equals(next)) {
				return true;
			}
		}
		
		if(ontClass.isProperty()) {			
			ontProperty = model.getOntProperty(serviceParameter.toString());
			iter = ontProperty.listSuperProperties(true);
			if (iter.hasNext()) {
				ontProperty = (OntProperty) iter.next();
				gotSuperProperty = true;
			}
			if (gotSuperProperty) {
				iter = ontProperty.listSubProperties(true);
			}
			while (iter.hasNext()) {
				String next = iter.next().toString();
				if (requestParameter.toString().equals(next)) {
					return true;
				}
			}
			
		}
		
		return false;
		
		
	}
	
	/**
	 *Method to do the matching between conditions
	 * @param services -
	 *            An ArrayList with all services loaded.
	 * @param request -
	 *            A Query with data from request loaded.
	 */
	public ArrayList<SimilarityDegree> matcherPreConditions(ArrayList<Service> services, Query request) {				
		
		resultPreconditions = new ArrayList<SimilarityDegree>();
		
		//This variable is used to save the result of the matching of all the conditions
		ArrayList<ArrayList<SimilarityDegree>> matchResult = new ArrayList<ArrayList<SimilarityDegree>>();
		
		//if there are service preconditions
		if(!(services.get(0).getPreconditionList().isEmpty())){
			//loop to swap the services's conditions
			for (ArrayList<URI> serviceCondition : services.get(0).getPreconditionList()) {	

				//This variable is used to save the result of the matching of the role Condition (Predicates are used as parameters)
				ArrayList<ArrayList<SimilarityDegree>> conditionResult = new ArrayList<ArrayList<SimilarityDegree>>();
				
				//loop to swap the request's conditions		
				for (ArrayList<URI> requestCondition : request.getPreconditionList()) {
					
					//This variable is used to save the result of the matching between the Request's and Service's conditions' parameters 
					ArrayList<SimilarityDegree> predicateMatch = new ArrayList<SimilarityDegree>();
					ArrayList<SimilarityDegree> atributeMatch = new ArrayList<SimilarityDegree>();
					
					//This variable is used to save the result of the matching of the attributes
					ArrayList<SimilarityDegree> atributeResult =  new ArrayList<SimilarityDegree>();
					
					ArrayList<URI> requestPredicate = new ArrayList<URI>();
					ArrayList<URI> requestAtributes = new ArrayList<URI>();
					ArrayList<URI> servicePredicate = new ArrayList<URI>();
					ArrayList<URI> serviceAtributes = new ArrayList<URI>();
					
					requestPredicate.add(requestCondition.get(0));
					servicePredicate.add(serviceCondition.get(0));
					
					//Adding to the array the result of the predicate's matching
					predicateMatch = caculateDegreeConditions(servicePredicate,requestPredicate,PRECONDITION);
					
					requestAtributes.add(requestCondition.get(1));
					requestAtributes.add(requestCondition.get(2));
					serviceAtributes.add(serviceCondition.get(1));
					serviceAtributes.add(serviceCondition.get(2));
					
					//Adding to the array the result of the atributes's matching
					atributeMatch = caculateDegreeConditions(serviceAtributes,requestAtributes,PRECONDITION);	
					
//					for (SimilarityDegree param : predicateMatch) {
//							System.out.println("PREDICATE Request " + param.getRequestParameter());
//							System.out.println("PREDICATE Service " + param.getServiceParameter());
//							System.out.println("PREDICATE Degree " + param.getSimilarityDegree());
//							
//					}	
//					for (SimilarityDegree param : atributeMatch) {
//							System.out.println("ATRIBUTE Request " + param.getRequestParameter());
//							System.out.println("ATRIBUTE Service " + param.getServiceParameter());
//							System.out.println("ATRIBUTE Degree " + param.getSimilarityDegree());
//					}
					
					atributeResult =  bestAtributesMatch(atributeMatch);
				
							
//					for (SimilarityDegree reg : atributeResult) {
//						System.out.println("ATRIBUTE RESQUEST PARAMETER " + reg.getRequestParameter());
//						System.out.println("ATRIBUTE SERVICE PARAMETER " + reg.getServiceParameter());
//						System.out.println("ATRIBUTE SIMILARITY DEGREE " + reg.getSimilarityDegree());
//					}
					
					ArrayList<SimilarityDegree> condition = new ArrayList<SimilarityDegree>();
					
					for (SimilarityDegree param : predicateMatch) {
						condition.add(param);					
					}
					for (SimilarityDegree param : atributeResult) {
						condition.add(param);					
					}
					
					conditionResult.add(condition);
					
				} //endfor request
				
//				for (ArrayList<SimilarityDegree> param : conditionResult) {
//					for (SimilarityDegree reg : param) {
//						System.out.println("CONDITION RESQUEST " + reg.getRequestParameter());
//						System.out.println("CONDITION SERVICE " + reg.getServiceParameter());
//						System.out.println("CONDITION DEGREE " + reg.getSimilarityDegree());
//						
//					}
//				}
				
				matchResult.add(bestConditionsMatch(conditionResult));
				
			} //endfor service
		
//			
//			for (ArrayList<SimilarityDegree> param : matchResult) {
//				for (SimilarityDegree reg : param) {
//					System.out.println("RESULT RESQUEST PARAMETER " + reg.getRequestParameter());
//					System.out.println("RESULT SERVICE PARAMETER " + reg.getServiceParameter());
//					System.out.println("RESULT SIMILARITY DEGREE " + reg.getSimilarityDegree());	
//				}	
//			}
			
			
			resultPreconditions = calculateFinalDegree(matchResult);
			
//			for (SimilarityDegree reg : resultPreconditions) {
//					System.out.println("PC RESQUEST " + reg.getRequestParameter());
//					System.out.println("PC SERVICE " + reg.getServiceParameter());
//					System.out.println("PC DEGREE " + reg.getSimilarityDegree());	
//			}
			
		} else { // There are not services preconditions
			SimilarityDegree test = new SimilarityDegree(URI.create(""), 4.0, URI.create(""));
			resultPreconditions.add(test);
			
		}
		
		return resultPreconditions;	
	}
	
	public ArrayList<SimilarityDegree> matcherEffects(ArrayList<Service> services, Query request) {				
		
		resultEffects = new ArrayList<SimilarityDegree>();
		
		//This variable is used to save the result of the matching of all the conditions
		ArrayList<ArrayList<SimilarityDegree>> matchResult = new ArrayList<ArrayList<SimilarityDegree>>();
		
		//if there are service preconditions
		if(!(request.getEffectList().isEmpty())){
			
			//loop to swap the request's conditions		
			for (ArrayList<URI> requestCondition : request.getEffectList()) {					

				//This variable is used to save the result of the matching of the role Condition (Predicates are used as parameters)
				ArrayList<ArrayList<SimilarityDegree>> conditionResult = new ArrayList<ArrayList<SimilarityDegree>>();							

				//loop to swap the services's conditions
				for (ArrayList<URI> serviceCondition : services.get(0).getEffectList()) {				
					
					//This variable is used to save the result of the matching between the Request's and Service's conditions' parameters 
					ArrayList<SimilarityDegree> predicateMatch = new ArrayList<SimilarityDegree>();
					ArrayList<SimilarityDegree> atributeMatch = new ArrayList<SimilarityDegree>();
					
					//This variable is used to save the result of the matching of the attributes
					ArrayList<SimilarityDegree> atributeResult =  new ArrayList<SimilarityDegree>();
					
					ArrayList<URI> requestPredicate = new ArrayList<URI>();
					ArrayList<URI> requestAtributes = new ArrayList<URI>();
					ArrayList<URI> servicePredicate = new ArrayList<URI>();
					ArrayList<URI> serviceAtributes = new ArrayList<URI>();
					
					requestPredicate.add(requestCondition.get(0));
					servicePredicate.add(serviceCondition.get(0));
					
					//Adding to the array the result of the predicate's matching
					predicateMatch = caculateDegreeConditions(servicePredicate,requestPredicate,PRECONDITION);
					
					requestAtributes.add(requestCondition.get(1));
					requestAtributes.add(requestCondition.get(2));
					serviceAtributes.add(serviceCondition.get(1));
					serviceAtributes.add(serviceCondition.get(2));
					
					//Adding to the array the result of the atributes's matching
					atributeMatch = caculateDegreeConditions(serviceAtributes,requestAtributes,PRECONDITION);	
					
//					for (SimilarityDegree param : predicateMatch) {
//							System.out.println("PREDICATE Request " + param.getRequestParameter());
//							System.out.println("PREDICATE Service " + param.getServiceParameter());
//							System.out.println("PREDICATE Degree " + param.getSimilarityDegree());
//							
//					}	
//					for (SimilarityDegree param : atributeMatch) {
//							System.out.println("ATRIBUTE Request " + param.getRequestParameter());
//							System.out.println("ATRIBUTE Service " + param.getServiceParameter());
//							System.out.println("ATRIBUTE Degree " + param.getSimilarityDegree());
//					}
					
					atributeResult =  bestAtributesMatch(atributeMatch);
				
							
//					for (SimilarityDegree reg : atributeResult) {
//						System.out.println("ATRIBUTE RESQUEST PARAMETER " + reg.getRequestParameter());
//						System.out.println("ATRIBUTE SERVICE PARAMETER " + reg.getServiceParameter());
//						System.out.println("ATRIBUTE SIMILARITY DEGREE " + reg.getSimilarityDegree());
//					}
					
					ArrayList<SimilarityDegree> condition = new ArrayList<SimilarityDegree>();
					
					for (SimilarityDegree param : predicateMatch) {
						condition.add(param);					
					}
					for (SimilarityDegree param : atributeResult) {
						condition.add(param);					
					}
					
					conditionResult.add(condition);
					
				}
				
//				for (ArrayList<SimilarityDegree> param : conditionResult) {
//					for (SimilarityDegree reg : param) {
//						System.out.println("CONDITION RESQUEST " + reg.getRequestParameter());
//						System.out.println("CONDITION SERVICE " + reg.getServiceParameter());
//						System.out.println("CONDITION DEGREE " + reg.getSimilarityDegree());
//						
//					}
//				}
				
				matchResult.add(bestConditionsMatch(conditionResult));
				
			}	
		
//			
//			for (ArrayList<SimilarityDegree> param : matchResult) {
//				for (SimilarityDegree reg : param) {
//					System.out.println("RESULT RESQUEST PARAMETER " + reg.getRequestParameter());
//					System.out.println("RESULT SERVICE PARAMETER " + reg.getServiceParameter());
//					System.out.println("RESULT SIMILARITY DEGREE " + reg.getSimilarityDegree());	
//				}	
//			}
			
			
			resultEffects = calculateFinalDegree(matchResult);
			
//			for (SimilarityDegree reg : resultEffects) {
//					System.out.println("EFF RESQUEST " + reg.getRequestParameter());
//					System.out.println("EFF SERVICE " + reg.getServiceParameter());
//					System.out.println("EFF DEGREE " + reg.getSimilarityDegree());	
//			}
			
		} else { 
			SimilarityDegree test = new SimilarityDegree(URI.create(""), 4.0, URI.create(""));
			resultEffects.add(test);
			
		}
		
		
		
		
		return resultEffects;	
	}

		
	
	private ArrayList<SimilarityDegree> caculateDegreeConditions(ArrayList<URI> serviceParameters, ArrayList<URI> requestParameters, char typeOfParameters) {
		ArrayList<SimilarityDegree> matchedParameters = new ArrayList<SimilarityDegree>();
		URI requestParameter = null;
		URI serviceParameter = null;
		
		for (int i = 0; i < requestParameters.size(); i++) {
			requestParameter = (URI) requestParameters.get(i);
			for (int j = 0; j < serviceParameters.size(); j++) {
				serviceParameter = (URI) serviceParameters.get(j);
				/*
				 * Verifying if the parameters have the same ontology.
				 */
				if (hasSameOntology(requestParameter, serviceParameter)) {
					if (isExactMatching(requestParameter, serviceParameter,
							typeOfParameters)) {
						matchedParameters.add(new SimilarityDegree(
								requestParameter, EXACT,
								serviceParameter));
					} else if (isPluginMatching(requestParameter,
							serviceParameter, typeOfParameters)) {
						matchedParameters.add(new SimilarityDegree(
								requestParameter, PLUGIN,
								serviceParameter));
					} else if (isSubsumesMatching(requestParameter,
							serviceParameter, typeOfParameters)) {
						matchedParameters.add(new SimilarityDegree(
								requestParameter, SUBSUMES,
								serviceParameter));
					} else if (isSiblingMatching(requestParameter,
							serviceParameter, typeOfParameters)) {
						matchedParameters.add(new SimilarityDegree(
								requestParameter, SIBLING,
								serviceParameter));
					} else {
						matchedParameters.add(new SimilarityDegree(
								requestParameter, FAIL,
								serviceParameter));
					}
				} else if (typeOfParameters == PRECONDITION) {
					matchedParameters.add(new SimilarityDegree(
							requestParameter, FAIL,
							serviceParameter));
//					return matchedParameters;
				}
			}
		}
		return matchedParameters;
	}
	
	private ArrayList<SimilarityDegree> bestAtributesMatch(ArrayList<SimilarityDegree> list){
		SimilarityDegree argument1 = new SimilarityDegree();
		SimilarityDegree argument2 = new SimilarityDegree();
		ArrayList<SimilarityDegree> result = new ArrayList<SimilarityDegree>();
				
		if(list.get(0).getSimilarityDegree() > list.get(1).getSimilarityDegree()){
			argument1 = list.get(0); 
			
		}
		else
			argument1 = list.get(1);
		
		if(list.get(2).getSimilarityDegree() > list.get(3).getSimilarityDegree()){
			argument2 = list.get(2); 
			
		}
		else
			argument2 = list.get(3);
		
		result.add(argument1);
		result.add(argument2);		
		
		return result;
	}
	
/**
 * 
 * @param list
 * @return SimilarityDegree
 * This method gets a list of SimilarityDegree which is the list of matching of a certain request condition to all the services conditions
 * The method returns the best match of the list
 * That means that it selects the best match in the service for that request condition 
 */
	private ArrayList<SimilarityDegree> bestConditionsMatch(ArrayList<ArrayList<SimilarityDegree>> list){
		ArrayList<SimilarityDegree> best = new ArrayList<SimilarityDegree>();
		
		if(!(list.isEmpty())){
			//Get the first condition
			best = list.get(0);
			//swap the list of conditions
			for (ArrayList<SimilarityDegree> param : list) {
				if(!(param.isEmpty())) {
					//if the predicates's degree of param is bigger than the best chosen before, param is the new best
					if(param.get(0).getSimilarityDegree() > best.get(0).getSimilarityDegree())
						best = param;
				}
			}
		}
		return best;
		
	}
	
	/**
	 * 
	 * @param list
	 * @return ArrayList<SimilarityDegree>
	 * This method gets a list of SimilarityDegree which is the list of matching of a certain request condition to a certain service condition
	 * The method returns the smallest degree of Match between all the terms in the condition 
	 */

	private ArrayList<SimilarityDegree> calculateFinalDegree(ArrayList<ArrayList<SimilarityDegree>> list){
		ArrayList<SimilarityDegree> result = new ArrayList<SimilarityDegree>();
		
		if(!(list.isEmpty())){
			for (ArrayList<SimilarityDegree> param : list) {	
				if(!(param.isEmpty())) {
//					Get the smallest degree of math between the predicate and arguments
					double degree = Math.min(param.get(0).getSimilarityDegree(), 
							Math.min(param.get(1).getSimilarityDegree(), param.get(2).getSimilarityDegree()));
					//Use the predicate as parameter to save the degree of match between service and request condition
					SimilarityDegree aux = new SimilarityDegree(param.get(0).getRequestParameter(), degree, param.get(0).getServiceParameter());
					
					result.add(aux);
				}			
			}
		}

		return result;
	}
	
	
	public ArrayList<SimilarityDegree> getResultPreconditions() {
		return resultPreconditions;
	}


	public void setResultPreconditions(
			ArrayList<SimilarityDegree> resultPreconditions) {
		this.resultPreconditions = resultPreconditions;
	}


	public ArrayList<SimilarityDegree> getResultEffects() {
		return resultEffects;
	}


	public void setResultEffects(ArrayList<SimilarityDegree> resultEffects) {
		this.resultEffects = resultEffects;
	}
	
	
	

}
