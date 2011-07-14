package pf.enigne.implementations.descriptive;

import java.io.IOException;
import java.util.ArrayList;

import pf.enigne.interfaces.IEngine;
import pf.io.input.InputManager;
import pf.main.MainDescriptiveMatcher;
import pf.main.MainFunctionalMatcher;
import pf.matcher.implementations.functional.FunctionalMatcher;
import pf.matcher.implementations.functional.SimilarityDegree;
import pf.matcher.implementations.descriptive.DescriptiveMatcher;
import pf.vo.Query;
import pf.vo.Service;

/**
 * @author Rodrigo.Amorim
 */
public class DescriptiveEngine implements IEngine {

	/**
	 * This variable represents the threshold required by the user.
	 */
	private double threshold;
	
	/**
	 * Array of correspondences of the input parameters.
	 */
	private ArrayList<SimilarityDegree> resultInputs = null;
	
	/**
	 * Array of correspondences of the output parameters.
	 */
	private ArrayList<SimilarityDegree> resultOutputs = null;
	
	/**
	 * An InputManager object.
	 */
	private InputManager inputManager = null;
	
	/**
	 * A FunctionalMatcher object.
	 */
	private DescriptiveMatcher descriptiveMatcher = null;

	/**
	 * Default Constructor
	 */
	public DescriptiveEngine() {
	}
	
	/**
	 * Constructor default of the class.
	 * @param args - An array of arguments.
	 */
	/**
	 * LORE
	 * Se for só o descritivo usa ESSE construtor. Senao usa o método descriptiveEngineHybrid
	 * @throws IOException 
	 */
	public DescriptiveEngine(String[] args, double basicCoefficient, double structuralCoefficient, double ancestorCoefficient,
						   double immediateChldCoefficient, double leafCoefficient,
						   double siblingCoefficient, double threshold, String fileName) throws IOException {
		
		inputManager = new InputManager();
		this.threshold = threshold;
		
		descriptiveMatcher = new DescriptiveMatcher(basicCoefficient, structuralCoefficient, ancestorCoefficient,
												immediateChldCoefficient, leafCoefficient, siblingCoefficient, threshold, fileName);
		inputManager.readDataEntry(args);
		descriptiveMatcher.matcher(inputManager.getServices(), inputManager.getRequest());
		
		/*
		 * Writing Service.
		 */
		MainDescriptiveMatcher.writeOutput("REQUEST:\n" + inputManager.getRequest().getUri().toString() + "\n\nSERVICE:\n");
		
		for(int i=0; i <descriptiveMatcher.getResultInputs().size(); i++ ) {
			this.setResultInputs(descriptiveMatcher.getResultInputs().get(i));
			this.setResultOutputs(descriptiveMatcher.getResultOutputs().get(i));
			this.removeDualCorrespondences(this.getResultInputs(), DescriptiveMatcher.INPUT);
			this.removeDualCorrespondences(this.getResultOutputs(), DescriptiveMatcher.OUTPUT);
			this.classifyResults(inputManager.getServices().get(i),inputManager.getRequest());
		}
	}
	
	/**
	 * @param args - An array of arguments.
	 */
	/**
	 * LORE
	 * Usa esse método caso o matching seja hibrido. Caso seja SOMENTE descritivo, usa o contrutor
	 */
	public double descriptiveEngineHybrid(Service service, Query request, double basicCoefficient, double structuralCoefficient, double ancestorCoefficient,
						   double immediateChldCoefficient, double leafCoefficient,
						   double siblingCoefficient, double threshold, String fileName) {
		
		this.threshold = threshold;
		
		descriptiveMatcher = new DescriptiveMatcher(basicCoefficient, structuralCoefficient, ancestorCoefficient,
												immediateChldCoefficient, leafCoefficient, siblingCoefficient, threshold, fileName);
		ArrayList<Service> services = new ArrayList<Service>();
		services.add(service);
		ArrayList<Query> queries = new ArrayList<Query>();
		queries.add(request);
		descriptiveMatcher.matcher(services, request);
		
		this.setResultInputs(descriptiveMatcher.getResultInputs().get(0));
		this.setResultOutputs(descriptiveMatcher.getResultOutputs().get(0));
		this.removeDualCorrespondences(this.getResultInputs(), DescriptiveMatcher.INPUT);
		this.removeDualCorrespondences(this.getResultOutputs(), DescriptiveMatcher.OUTPUT);
		return this.getClassifyResults();
	}
	
	/**
	 * Method that removes dual correspondences of parameters in the match. 
	 * @param result - An ArrayList of correspondences.
	 */
	@SuppressWarnings("unchecked")
	private void removeDualCorrespondences(ArrayList<SimilarityDegree> result, char parameters) {  
		
	     /*
	      * Remove based on services parameters if is an Input.
	      */
	     if(parameters == FunctionalMatcher.INPUT) {
			 for(int i=0; i < result.size(); i++) {
				 for (int j=0; j < result.size(); j++) {
					 if( (i != j) && (result.get(i).getServiceParameter().toString().
							 equals(result.get(j).getServiceParameter().toString()))) {
						 if(result.get(i).getSimilarityDegree() > result.get(j).getSimilarityDegree()) {
							 result.get(j).setSimilarityDegree(-1);
						 } else {
							 result.get(i).setSimilarityDegree(-1);
						 }
					 }
				 }
			 }
			 /*
		      * Remove based on request parameters if is an Output.
		      */	 
	     } else if (parameters == FunctionalMatcher.OUTPUT) {
	    	 for(int i=0; i < result.size(); i++) {
				 for (int j=0; j < result.size(); j++) {
					 if( (i != j) && (result.get(i).getRequestParameter().toString().
							 equals(result.get(j).getRequestParameter().toString()))) {
						 if(result.get(i).getSimilarityDegree() > result.get(j).getSimilarityDegree()) {
							 result.get(j).setSimilarityDegree(-1);
						 } else {
							 result.get(i).setSimilarityDegree(-1);
						 }
					 }
				 }
			 }
	     }
		 for(int i=0; i < result.size(); i++) {
			 if(result.get(i).getSimilarityDegree() == -1) {
				 result.remove(i);
				 i--;
			 }
		 }
	}
	
	/**
	 * Method that classifies the obtained results.  
	 * @param services - A service
	 * @param request - A request 
	 */
	public Service classifyResults(Service service, Query request) {
		double levelMatching;
		//Properties property = MessageProperties.getInstance();
		
		levelMatching = returnLevelMatching(resultInputs, resultOutputs);
		
		//Writing the correspondences
		if (levelMatching > this.threshold) {
			MainDescriptiveMatcher.writeOutput(service.getUri().toString() + " matched with " + levelMatching + " of similarity.\n");
			MainDescriptiveMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
			service.setDegreeMatch(String.valueOf(levelMatching));
		}
		
		return service;
	}
	
	/**
	 * Method that classifies the obtained results. Different from the upper method, this method return the levelMatching.  
	 * @param services - A service
	 * @param request - A request 
	 */
	public double getClassifyResults() {
		//Properties property = MessageProperties.getInstance();
		
		return returnLevelMatching(resultInputs, resultOutputs);
	}
	
	/**
	 * Method that return the smallest degree of match between input and output parameters.
	 * @param resultInput - An ArrayList with correspondences of input parameters.
	 * @param resultOutput - An ArrayList with correspondences of output parameters.
	 * @return levelMatching - Smallest degree of match.
	 */
	
	private double returnLevelMatching(ArrayList<SimilarityDegree> resultInput, ArrayList<SimilarityDegree> resultOutput) {
		double levelMatching = 1.1;
		
		for(int i=0; i < resultInput.size(); i++) {
			if(resultInput.get(i).getSimilarityDegree() < levelMatching) {
				levelMatching = resultInput.get(i).getSimilarityDegree(); 
			}
		}
		for(int i=0; i < resultOutput.size(); i++) {
			if(resultOutput.get(i).getSimilarityDegree() < levelMatching) {
				levelMatching = resultOutput.get(i).getSimilarityDegree();
			}
		}
		return levelMatching;
	}
	
	/**
	 * @return the resultInputs.
	 */
	public ArrayList<SimilarityDegree> getResultInputs() {
		return resultInputs;
	}

	/**
	 * @param resultInputs the resultInputs to set.
	 */
	public void setResultInputs(ArrayList<SimilarityDegree> resultInputs) {
		this.resultInputs = resultInputs;
	}

	/**
	 * @return the resultOutputs.
	 */
	public ArrayList<SimilarityDegree> getResultOutputs() {
		return resultOutputs;
	}

	/**
	 * @param resultOutputs the resultOutputs to set.
	 */
	public void setResultOutputs(ArrayList<SimilarityDegree> resultOutputs) {
		this.resultOutputs = resultOutputs;
	}

}
