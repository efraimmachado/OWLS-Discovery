package pf.matcher.implementations.functional;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import pf.matcher.interfaces.IMatcher;
import pf.vo.Query;
import pf.vo.Service;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;

/**
 * This class implements a functional matcher based on Pallouci's works.
 * 
 * @author Rodrigo Almeida de Amorim
 * @version 0.1
 */
public class FunctionalMatcher implements IMatcher {

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
     * This constant represents an Input parameters.
     */
    public static final char INPUT = 'I';
    /**
     * This Constant represents an Output parameters.
     */
    public static final char OUTPUT = 'O';
    /**
     * Attribute that holds all request parameters.
     */
    private ArrayList<URI> requestParameters;
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
     * An array that holds all correspondences between the inputs.
     */
    private ArrayList<ArrayList<SimilarityDegree>> resultInputs = null;
    /**
     * An array that holds all correspondences between the outputs.
     */
    private ArrayList<ArrayList<SimilarityDegree>> resultOutputs = null;

    /**
     * Constructor of the class, it expects an ArrayList of URI from request.
     *
     * @param requestParameters -
     *            ArrayList with request parameters.
     */
    public FunctionalMatcher() {
        ontModel = PelletReasonerFactory.THE_SPEC;
        model = ModelFactory.createOntologyModel(ontModel);
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
        NodeIterator iter = null;
        OntClass ontClass = null;

        loadOntology(requestParameter.getScheme() + ":"
                + requestParameter.getSchemeSpecificPart() + "#");

        if (typeOfParameter == FunctionalMatcher.INPUT) {
            resource = model.getResource(ontology
                    + requestParameter.getFragment());
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
        } else if (typeOfParameter == FunctionalMatcher.OUTPUT) {
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

        loadOntology(requestParameter.getScheme() + ":"
                + requestParameter.getSchemeSpecificPart() + "#");

        if (typeOfParameter == FunctionalMatcher.INPUT) {
            ontClass = model.getOntClass(requestParameter.toString());
            iter = ontClass.listSubClasses();
            while (iter.hasNext()) {
                String node = iter.next().toString();
                if (serviceParameter.toString().equals(node)) {
                    return true;
                }
            }
        } else if (typeOfParameter == FunctionalMatcher.OUTPUT) {
            ontClass = model.getOntClass(serviceParameter.toString());
            iter = ontClass.listSubClasses();
            while (iter.hasNext()) {
                if (requestParameter.toString().equals(iter.next().toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method that s if two elements of an ontology are subsumes.
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

        loadOntology(requestParameter.getScheme() + ":"
                + requestParameter.getSchemeSpecificPart() + "#");

        if (typeOfParameter == FunctionalMatcher.INPUT) {
            ontClass = model.getOntClass(serviceParameter.toString());
            iter = ontClass.listSubClasses();
            while (iter.hasNext()) {
                if (requestParameter.toString().equals(iter.next().toString())) {
                    return true;
                }
            }
        } else if (typeOfParameter == FunctionalMatcher.OUTPUT) {
            ontClass = model.getOntClass(requestParameter.toString());
            iter = ontClass.listSubClasses();
            while (iter.hasNext()) {
                if (serviceParameter.toString().equals(iter.next().toString())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Method that s if two elements of an ontology are siblings.
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
        OntClass ontClass = null;
        Iterator iter = null;

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
        return false;
    }

    /**
     * Method that performs the matching switching among available filters.
     *
     * @param serviceParameters -
     *            An ArrayList with all service parameters.
     * @param typeOfParameters -
     *            Type of parameters, Input or Output.
     * @return - return an ArrayList with all correspondences matched.
     */
    private ArrayList<SimilarityDegree> caculateDegree(ArrayList<URI> serviceParameters, char typeOfParameters) {
        ArrayList<SimilarityDegree> matchedParameters = new ArrayList<SimilarityDegree>();
        URI requestParameter = null;
        URI serviceParameter = null;
        /*
         * verifying the quantity of parameters in input and output.
         */
        if (typeOfParameters == FunctionalMatcher.INPUT) {
            if (requestParameters.size() < serviceParameters.size()) {
                matchedParameters.add(new SimilarityDegree(requestParameter,
                        FunctionalMatcher.FAIL, serviceParameter));
                return matchedParameters;
            }
        } else if (typeOfParameters == FunctionalMatcher.OUTPUT) {
            if (requestParameters.size() > serviceParameters.size()) {
                matchedParameters.add(new SimilarityDegree(requestParameter,
                        FunctionalMatcher.FAIL, serviceParameter));
                return matchedParameters;
            }
        }

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
                                requestParameter, FunctionalMatcher.EXACT,
                                serviceParameter));
                    } else if (isPluginMatching(requestParameter,
                            serviceParameter, typeOfParameters)) {
                        matchedParameters.add(new SimilarityDegree(
                                requestParameter, FunctionalMatcher.PLUGIN,
                                serviceParameter));
                    } else if (isSubsumesMatching(requestParameter,
                            serviceParameter, typeOfParameters)) {
                        matchedParameters.add(new SimilarityDegree(
                                requestParameter, FunctionalMatcher.SUBSUMES,
                                serviceParameter));
                    } else if (isSiblingMatching(requestParameter,
                            serviceParameter, typeOfParameters)) {
                        matchedParameters.add(new SimilarityDegree(
                                requestParameter, FunctionalMatcher.SIBLING,
                                serviceParameter));
                    }
                } else if (typeOfParameters == FunctionalMatcher.INPUT) {
                    matchedParameters.add(new SimilarityDegree(
                            requestParameter, FunctionalMatcher.FAIL,
                            serviceParameter));
                    return matchedParameters;
                }

            }
        }
        /*
         * If no matches were performed, return a fail match.
         */
        if (matchedParameters.size() == 0) {
            matchedParameters.add(new SimilarityDegree(requestParameter,
                    FunctionalMatcher.FAIL, serviceParameter));
        }
        return matchedParameters;
    }

    /**
     * Method that has to be implemented due the interface. It managers all the
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
            resultInputs.add(caculateDegree(services.get(i).getInputList(),
                    INPUT));

//			for (ArrayList<SimilarityDegree> param : resultInputs) {
//				for (SimilarityDegree reg : param) {
//					System.out.println("R I Param " + reg.getRequestParameter());
//					System.out.println("S I Param " + reg.getServiceParameter());
//					System.out.println("R I Simil " + reg.getSimilarityDegree());
//					
//				}				
//			}


            requestParameters = request.getOutputList();
            resultOutputs.add(caculateDegree(services.get(i).getOutputList(),
                    OUTPUT));

//			for (ArrayList<SimilarityDegree> param : resultOutputs) {
//				for (SimilarityDegree reg : param) {
//					System.out.println("R O Param " + reg.getRequestParameter());
//					System.out.println("S O Param " + reg.getServiceParameter());
//					System.out.println("R O Simil " + reg.getSimilarityDegree());
//					
//				}	
//			}

        }



    }

    private ArrayList<SimilarityDegree> calculateCompositionalDegree(ArrayList<URI> serviceParameters, ArrayList<URI> requestParameters) {
        ArrayList<SimilarityDegree> matchedParameters = new ArrayList<SimilarityDegree>();
        URI requestParameter = null;
        URI serviceParameter = null;

        System.out.println("INCIANDO COMPOSITIONALDEGREE COM:");
        System.out.println(serviceParameters);
        System.out.println(requestParameters);
        for (int i = 0; i < requestParameters.size(); i++) {
            requestParameter = (URI) requestParameters.get(i);
            for (int j = 0; j < serviceParameters.size(); j++) {
                serviceParameter = (URI) serviceParameters.get(j);
                /*
                 * Verifying if the parameters have the same ontology.
                 */
                System.out.print("comparando "+serviceParameter+" com "+ requestParameter);
                if (hasSameOntology(requestParameter, serviceParameter)) {
                    if (isExactMatching(requestParameter, serviceParameter, FunctionalMatcher.OUTPUT))
                    {
                        System.out.println(" >> exact");
                        matchedParameters.add(new SimilarityDegree(
                                requestParameter, FunctionalMatcher.EXACT,
                                serviceParameter));
                    } 
                    else if (isPluginMatching(requestParameter, serviceParameter, FunctionalMatcher.OUTPUT))
                    {
                        System.out.println(" >> plugin");
                        matchedParameters.add(new SimilarityDegree(
                                requestParameter, FunctionalMatcher.PLUGIN,
                                serviceParameter));
                    }
                    else if (isSubsumesMatching(requestParameter,
                            serviceParameter, FunctionalMatcher.OUTPUT))
                    {
                       System.out.println(" >> subsumes");
                        matchedParameters.add(new SimilarityDegree(
                                requestParameter, FunctionalMatcher.SUBSUMES,
                                serviceParameter));
                    }
                    else if (isSiblingMatching(requestParameter,
                            serviceParameter, FunctionalMatcher.OUTPUT))
                    {
                        System.out.println(" >> sibling");
                        matchedParameters.add(new SimilarityDegree(
                                requestParameter, FunctionalMatcher.SIBLING,
                                serviceParameter));
                    }
                    //If no matches were performed, return a fail match.
                    else 
                    {
                        System.out.println(" >> fail");
                        matchedParameters.add(new SimilarityDegree(requestParameter,
                            FunctionalMatcher.FAIL, serviceParameter));
                    }
                }
                //If no matches were performed, return a fail match.
                else 
                {
                    System.out.println(" >> fail");
                    matchedParameters.add(new SimilarityDegree(requestParameter,
                            FunctionalMatcher.FAIL, serviceParameter));
                }
            }
        }
        for (int i = 0; i < matchedParameters.size();i++)
            System.out.println(matchedParameters.get(i).getRequestParameter()+" , "+matchedParameters.get(i).getServiceParameter()+" , "+matchedParameters.get(i).getSimilarityDegree());
        return matchedParameters;
    }

// this matcher differs from the other because it just look the compability between the service output and request output... well remember the request output is
// one depenency, ie, one service input (that service is part of composition)
    public void compositionalMatcher(ArrayList<Service> services, Query request) {
        //verificar se é resultinput mesmo
        resultInputs = new ArrayList<ArrayList<SimilarityDegree>>();
        requestParameters = request.getOutputList();
        for (int i = 0; i < services.size(); i++) {
            resultInputs.add(calculateCompositionalDegree(services.get(i).getOutputList(), request.getOutputList()));
            //			for (ArrayList<SimilarityDegree> param : resultInputs) {
            //				for (SimilarityDegree reg : param) {
            //					System.out.println("R I Param " + reg.getRequestParameter());
            //					System.out.println("S I Param " + reg.getServiceParameter());
            //					System.out.println("R I Simil " + reg.getSimilarityDegree());
            //
            //				}
            //			}
        }
    }
}
