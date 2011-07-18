package pf.enigne.implementations.pe;

import java.util.ArrayList;

import pf.enigne.interfaces.IEngine;
import pf.matcher.implementations.functional.FunctionalMatcher;
import pf.matcher.implementations.functional.SimilarityDegree;
import pf.matcher.implementations.pe.PEMatcher;
import pf.vo.Query;
import pf.vo.Service;

public class PEEngine implements IEngine{
	
	
	/**
	 * Array of correspondences of the input parameters.
	 */
	private ArrayList<SimilarityDegree> resultPrecondition = null;
	
	/**
	 * Array of correspondences of the output parameters.
	 */
	private ArrayList<SimilarityDegree> resultEffect = null;
	
	
	PEMatcher peMatcher = null;
	
	Long startTimePE = null;
	Long endTimePE = null;
	
	
	/**
	 * Método que vai chamar o matcher. Esse método eh acionado somente quando o match de I/O nao resulta em fail
	 * @param service
	 * @param request
	 */
	public double PEEngineResult(Service service, Query request){
		
		ArrayList<Service> services = new ArrayList<Service>();
		services.add(service);
		ArrayList<Query> queries = new ArrayList<Query>();
		queries.add(request);
		peMatcher = new PEMatcher();
		
		this.startTimePE = System.currentTimeMillis();
		
		peMatcher.matcher(services, request);
		
		this.endTimePE = System.currentTimeMillis();	
		
		
		this.setResultPrecondition(peMatcher.getResultPreconditions());
		this.setResultEffect(peMatcher.getResultEffects());
		
		return this.getClassifyResults();
		
	}
	
	public double PECompositionalEngineResult(Service service, Query request){

		ArrayList<Service> services = new ArrayList<Service>();
		services.add(service);
		ArrayList<Query> queries = new ArrayList<Query>();
		queries.add(request);
		peMatcher = new PEMatcher();

		peMatcher.compositionalMatcher(services, request);

		this.setResultPrecondition(peMatcher.getResultPreconditions());
		//this.setResultEffect(peMatcher.getResultEffects());

		return this.getCompositionalClassifyResults();

	}


	/**
	 * Method that classifies the obtained results. Different from the upper method, this method return the levelMatching.  
	 * @param services - A service
	 * @param request - A request 
	 */
	public double getClassifyResults() {
		//Properties property = MessageProperties.getInstance();
		
		return returnLevelMatching(resultPrecondition, resultEffect);
	}

	public double getCompositionalClassifyResults() {
		//Properties property = MessageProperties.getInstance();

		return returnCompositionalLevelMatching(resultPrecondition);
	}


	/**
	 * Method that return the smallest degree of match between input and output parameters.
	 * @param resultInput - An ArrayList with correspondences of input parameters.
	 * @param resultOutput - An ArrayList with correspondences of output parameters.
	 * @return levelMatching - Smallest degree of match.
	 */
	
	private double returnLevelMatching(ArrayList<SimilarityDegree> resultPrecondition, ArrayList<SimilarityDegree> resultEffect) {
		double levelMatching = FunctionalMatcher.NOTHING;
		
		
		for (SimilarityDegree param : resultEffect) {
			System.out.println("ResultEffects Request " + param.getRequestParameter());
			System.out.println("ResultEffects Service " + param.getServiceParameter());
			System.out.println("ResultEffects Degree " + param.getSimilarityDegree());
			
		}
		
		for (SimilarityDegree param : resultPrecondition) {
			System.out.println("resultPrecondition Request " + param.getRequestParameter());
			System.out.println("resultPrecondition Service " + param.getServiceParameter());
			System.out.println("resultPrecondition Degree " + param.getSimilarityDegree());
			
		}
		
		if(!(resultPrecondition.isEmpty() || resultEffect.isEmpty())){
			for(int i=0; i < resultPrecondition.size(); i++) {
				if(resultPrecondition.get(i).getSimilarityDegree() < levelMatching) {
					levelMatching = resultPrecondition.get(i).getSimilarityDegree(); 
				}
			}
			for(int i=0; i < resultEffect.size(); i++) {
				if(resultEffect.get(i).getSimilarityDegree() < levelMatching) {
					levelMatching = resultEffect.get(i).getSimilarityDegree();
				}
			} 
		}
		return levelMatching;
	}
	
	private double returnCompositionalLevelMatching(ArrayList<SimilarityDegree> resultPrecondition) {
		double levelMatching = FunctionalMatcher.NOTHING;

		for (SimilarityDegree param : resultPrecondition) {
			System.out.println("resultPrecondition Request " + param.getRequestParameter());
			System.out.println("resultPrecondition Service " + param.getServiceParameter());
			System.out.println("resultPrecondition Degree " + param.getSimilarityDegree());

		}

		if(!resultPrecondition.isEmpty())
                {
			for(int i=0; i < resultPrecondition.size(); i++) {
				if(resultPrecondition.get(i).getSimilarityDegree() < levelMatching) {
					levelMatching = resultPrecondition.get(i).getSimilarityDegree();
				}
			}
		}
		return levelMatching;
	}


	public ArrayList<SimilarityDegree> getResultPrecondition() {
		return resultPrecondition;
	}

	public void setResultPrecondition(ArrayList<SimilarityDegree> resultPrecondition) {
		this.resultPrecondition = resultPrecondition;
	}

	public ArrayList<SimilarityDegree> getResultEffect() {
		return resultEffect;
	}

	public void setResultEffect(ArrayList<SimilarityDegree> resultEffect) {
		this.resultEffect = resultEffect;
	}
	
	

	
	
	
	public Long getStartTimePE() {
		return startTimePE;
	}


	public void setStartTimePE(Long startTimePE) {
		this.startTimePE = startTimePE;
	}


	public Long getEndTimePE() {
		return endTimePE;
	}


	public void setEndTimePE(Long endTimePE) {
		this.endTimePE = endTimePE;
	}


	@Override
	public Service classifyResults(Service services, Query query) {
		
		return null;
	}
	
	

		

}
