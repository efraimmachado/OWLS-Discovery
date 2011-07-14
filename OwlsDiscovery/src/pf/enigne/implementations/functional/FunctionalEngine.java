package pf.enigne.implementations.functional;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import owlsautocomposer.graph.Edge;
import owlsautocomposer.graph.Graph;
import owlsautocomposer.graph.Node;

import pf.enigne.implementations.descriptive.DescriptiveEngine;
import pf.enigne.implementations.pe.PEEngine;
import pf.enigne.interfaces.IEngine;
import pf.io.input.InputManager;
import pf.main.MainFunctionalMatcher;
import pf.matcher.implementations.functional.FunctionalMatcher;
import pf.matcher.implementations.functional.SimilarityDegree;
import pf.resource.MessageProperties;
import pf.vo.DescriptiveData;
import pf.vo.Query;
import pf.vo.Service;

/**
 * @author Rodrigo Almeida de Amorim
 * @version 0.1
 */

/**
 * Class that classify the result of the match, it specific how functional the request and the service are based
 * on the filters described in the Matcher.
 */
public class FunctionalEngine implements IEngine{

	/**
	 * Array of correspondences of the input parameters.
	 */
	ArrayList<SimilarityDegree> resultInputs = null;
	
	/**
	 * Array of correspondences of the effects parameters.
	 */
	ArrayList<SimilarityDegree> resultEffects = null;
	
	/**
	 * Array of correspondences of the preconditions parameters.
	 */
	ArrayList<SimilarityDegree> resultPreconditions = null;
	
	/**
	 * Array of correspondences of the output parameters.
	 */
	ArrayList<SimilarityDegree> resultOutputs = null;
	
	/**
	 * Element that contains the value to be shown in the GUI. 
	 */
	String[] filter;
	String[] filterPE;
	
	/**
	 * An InputManager object.
	 */
	InputManager inputManager = null;
	
	boolean hybridTreatment;
	
//	Lore
	boolean PETreatment;
	
	
	Long startTimeES = null;
	Long endTimeES = null;
	Long startTimePE = null;
	Long endTimePE = null;
	
	DescriptiveData dataHybrid;
	
	ArrayList<Service> servicesMatched;
	
	/**
	 * A FunctionalMatcher object.
	 */
	FunctionalMatcher functionalMatcher = null;
	
	/**
	 * Constructor default of the class.
	 * @param args - An array of arguments.
	 * @throws IOException 
	 */
	public FunctionalEngine(String[] args, String[] filter, String[] filterPE, DescriptiveData dataHybrid, boolean hybridTreatment) throws IOException {
		this.filter = filter;
		this.filterPE = filterPE;
		this.hybridTreatment = hybridTreatment;
		this.dataHybrid = dataHybrid;
		inputManager = new InputManager();
		functionalMatcher = new FunctionalMatcher();
		servicesMatched = new ArrayList<Service>();
                System.out.printf("efraim > lendo os inputs");
		inputManager.readDataEntry(args);
		functionalMatcher.matcher(inputManager.getServices(), inputManager.getRequest());
                
		
		/*
		 * Writing Service.
		 */
		MainFunctionalMatcher.writeOutput("REQUEST:\n" + inputManager.getRequest().getUri().toString() + "\n\nSERVICE:\n");
		
		for(int i=0; i <functionalMatcher.getResultInputs().size(); i++ ) {
			this.setResultInputs(functionalMatcher.getResultInputs().get(i));
			this.setResultOutputs(functionalMatcher.getResultOutputs().get(i));
			this.removeDualCorrespondences(this.getResultInputs(), FunctionalMatcher.INPUT);
			this.removeDualCorrespondences(this.getResultOutputs(), FunctionalMatcher.OUTPUT);
			servicesMatched.add(this.classifyResults(inputManager.getServices().get(i),inputManager.getRequest()));
		}
	}
	
//	Lore INICO
	public FunctionalEngine(String[] args, String[] filter, String[] filterPE, DescriptiveData dataHybrid, boolean hybridTreatment, boolean PETreatment, boolean isCompositionValid) throws IOException {

//		this.startTimeES = System.currentTimeMillis();


		inputManager = new InputManager();
		this.filter = filter;
		this.filterPE = filterPE;
		this.hybridTreatment = hybridTreatment;
		this.PETreatment = PETreatment;
		this.dataHybrid = dataHybrid;
		functionalMatcher = new FunctionalMatcher();
		servicesMatched = new ArrayList<Service>();
		inputManager.readDataEntry(args);

		long startTime = System.currentTimeMillis();
		if (!isCompositionValid)
		{
			functionalMatcher.matcher(inputManager.getServices(), inputManager.getRequest());
			/*
			 * Writing Service.
			 */
			MainFunctionalMatcher.writeOutput("REQUEST:\n" + inputManager.getRequest().getUri().toString() + "\n\nSERVICE:\n");

			for(int i=0; i <functionalMatcher.getResultInputs().size(); i++ ) {
				this.setResultInputs(functionalMatcher.getResultInputs().get(i));
				this.setResultOutputs(functionalMatcher.getResultOutputs().get(i));
				this.removeDualCorrespondences(this.getResultInputs(), FunctionalMatcher.INPUT);
				this.removeDualCorrespondences(this.getResultOutputs(), FunctionalMatcher.OUTPUT);
				servicesMatched.add(this.classifyResults(inputManager.getServices().get(i),inputManager.getRequest()));
			}
		}
		else
		{
			Graph graph = new Graph();
			//deve-se colocar o que o usuario quer na entrada, nao tem output
			Node finalNode = null;
                        try {
                            finalNode = new Node(new Service(inputManager.getRequest().getOutputList(), null, new URI("FINAL_STATE")), "FINAL_STATE");
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(FunctionalEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }
			//adding the final state of composition... this is a way to concentrate all request's output at one abstract service...
			//first, understand the algorithm and all will make sense... that algorithm works goal based, and the step are find a service will solve a dependecy
			//represented by a edge without origin node
			graph.addNode(finalNode);
			//adding the first pendencies
                        ArrayList<URI> NodeInputs = finalNode.getService().getInputList();
                        for (int k = 0; k < NodeInputs.size(); k++)
                        {
        			graph.addEdge(null, finalNode, NodeInputs.get(k),  true);//deve adicionar uma aresta para cada input dele, ou seja, para cada output da requisicao
                        }
			//the next line makes the request input be used in the composition if it is necessary
                        try {
                            inputManager.addService(new Service(null, inputManager.getRequest().getOutputList(), new URI("REQUEST_STATE")));
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(FunctionalEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }

			ArrayList<Edge> pendencyEdges = graph.getPendencyEdges();
			while(pendencyEdges.size() > 0)
			{
                                System.out.println("Existem "+pendencyEdges.size()+" pendências");
				//take the first pendency. it looks like fifo... i guess ;)
				Edge edge = pendencyEdges.get(0);
				Node newNode = null;
				Node annoyingNode = edge.getDestinyNode();//this node is annoying because it causes the pendency... like a woman :O
                                System.out.println("resolvendo pendência "+edge.getUri());
				//looking for compatible services avaliable
                                try {
                                    ArrayList<URI> uriList = new ArrayList<URI>();
                                    uriList.add(edge.getUri());
                                    functionalMatcher.compositionalMatcher(inputManager.getServices(), new Query(null, uriList, new URI("")));
                                } catch (URISyntaxException ex) {
                                    Logger.getLogger(FunctionalEngine.class.getName()).log(Level.SEVERE, null, ex);
                                }
				//falta setar o megazord... teoricamente ira funcionar... mas eu vou ajeitar ainda heuaheuaheuahe
                                ArrayList<ArrayList<SimilarityDegree>> results = functionalMatcher.getResultInputs();
                                System.out.println("resultado da busca:");
                                for (int w = 0; w < results.size(); w++)
                                {
                                    System.out.println("linha superior numero "+(w+1));
                                    if (w < inputManager.getServices().size())
                                        System.out.println("#SERVICE: "+inputManager.getServices().get(w).getUri());
                                    ArrayList<SimilarityDegree> resultSimilarity = results.get(w);
                                    for (int t = 0; t < resultSimilarity.size(); t++)
                                    {
                                        System.out.println("coluna numero "+(t+1));
                                        System.out.println("REQUEST: "+resultSimilarity.get(t).getRequestParameter().toString());
                                        System.out.println("SERVICE: "+resultSimilarity.get(t).getServiceParameter().toString());
                                        System.out.println("DEGREE:  "+resultSimilarity.get(t).getSimilarityDegree());

                                    }
                                   
                                }

				//choice the best service to solve the pendency
                                System.out.println("Escolhendo melhor no");
				while (newNode == null)
				{
//					newNode = new Node(new Service(results.get(0).get(0), null, new URI("semnocao")), "");//BestChoice(PEGADINHADOMALANDRO);
                                        //System.out.println("loop");
					if(graph.getForbiddenNodes().contains(newNode))
					{
//						PEGADINHADOMALANDRO.remove(newNode);
						newNode = null;
					}

				}
				//add the new node and the others compatible nodes (services) but set their edges to no fixed
				if (newNode != null)
				{
					graph.addNode(newNode);
					edge.setEdge(newNode, edge.getDestinyNode(),true);
//					for (int i = 0; i < PEGADINHADOMALANDRO.size() ; i++)
//					{
//						Node alternativeNode = PEGADINHADOMALANDRO.get(i);
//						graph.addNode(alternativeNode);
//						graph.createEdge(alternativeNode, edge.getDestinyNode(), false);
//					}
					//test if there is a cicle with the new service... i said it is a annoying node... ok ok, it isnt his fault, sry
					if (graph.thereIsAPath(edge.getDestinyNode(),annoyingNode))
					{
						graph.addForbiddenNode(annoyingNode);
						graph.removeNodeUntilNoFixedEdge(annoyingNode, null); //power power rangers verificar se eh null mesmo
					}
				}
				else //remove the new node because the pendencies cant be solved... i said it is a annoying node
				{
						graph.addForbiddenNode(annoyingNode);
						graph.removeNodeUntilNoFixedEdge(annoyingNode, null);
						if (!graph.getNodes().contains(finalNode))
						{
							//oh my god of pagode what can i do?
							//just cry little butterfly
						}
				}
				//take new pendencies...
				pendencyEdges = graph.getPendencyEdges();
			}

		}
		long endTime = System.currentTimeMillis();

		System.out.println("Tempo de execucao: " + (endTime - startTime));



	}
//	Lore FIM


	/**
	 * Method that returns if the filter has to be shown.
	 * @param filter - Filter that has to be searched in the vector.
	 * @return Return true if it's present and false it isn't present.
	 */
	private boolean isFiltered(String filter) {
		for(int i = 0; i < this.filter.length; i++) {
			if (this.filter[i] != null) {
				if (this.filter[i].equalsIgnoreCase(filter)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Method that returns if the filter has to be shown.
	 * @param filterPE - FilterPE that has to be searched in the vector.
	 * @return Return true if it's present and false it isn't present.
	 */
	private boolean isFilteredPE(String filterPE) {
		for(int i = 0; i < this.filterPE.length; i++) {
			if (this.filterPE[i] != null) {
				if (this.filterPE[i].equalsIgnoreCase(filterPE)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Method that classifies the obtained results.  
	 * @param services - A service
	 * @param request - A request 
	 */
	public Service classifyResults(Service service, Query request) {
		int levelMatching;
		Properties property = MessageProperties.getInstance();
		
		levelMatching = (int) returnLevelMatching(resultInputs, resultOutputs);
		
		//Writing the correspondences
		
		switch(levelMatching) {
			case (FunctionalMatcher.EXACT): {
				if (isFiltered(property.getProperty("label_exact")) && (this.PETreatment == true)) {
					PEEngine peEngine = new PEEngine();
					
					double level = peEngine.PEEngineResult(service, request);
					
					this.startTimePE = peEngine.getStartTimePE();
					this.endTimePE = peEngine.getEndTimePE();	
					
					String result = returnResult(level);
					if(isFilteredPE(result)){
						MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
					}
					
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_exact") + ") \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
					service.setDegreeMatch("EXACT");
				} else if (isFiltered(property.getProperty("label_exact"))) {
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_exact") + ") \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
					service.setDegreeMatch("EXACT");
				}
				break;
			}
			case (FunctionalMatcher.PLUGIN): {
				if (isFiltered(property.getProperty("label_plugin"))&& (this.PETreatment == true)) {
					PEEngine peEngine = new PEEngine();
					double level = peEngine.PEEngineResult(service, request);
					String result = returnResult(level);
					if(isFilteredPE(result)){
						MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
					}
					
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_plugin") + ") \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
					service.setDegreeMatch("PLUGIN");
					
				} else if (isFiltered(property.getProperty("label_plugin"))) {
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_plugin") + ") \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
					service.setDegreeMatch("PLUGIN");
				}
				break;
			}
			case (FunctionalMatcher.SUBSUMES): {
				if (isFiltered(property.getProperty("label_subsumes")) && (this.PETreatment == true)) {
					PEEngine peEngine = new PEEngine();
					double level = peEngine.PEEngineResult(service, request);
					String result = returnResult(level);
					if(isFilteredPE(result)){
						MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
					}
					
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_subsumes") + ") \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
					service.setDegreeMatch("SUBSUMES");
					
				} else if (isFiltered(property.getProperty("label_subsumes"))) {
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_subsumes") + ") \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
					service.setDegreeMatch("SUBSUMES");
				}	
				break;
			}
			case (FunctionalMatcher.SIBLING): {
				if (isFiltered(property.getProperty("label_sibling")) && (this.PETreatment == true)) {
					PEEngine peEngine = new PEEngine();
					double level = peEngine.PEEngineResult(service, request);
					String result = returnResult(level);
					if(isFilteredPE(result)){
						MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
					}
					
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_sibling") + ") \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
					service.setDegreeMatch("SIBLING");
					
				} else if (isFiltered(property.getProperty("label_sibling"))) {
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_sibling") + ") \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
					service.setDegreeMatch("SIBLING");
				}	
				break;
			}
			default: {
				
				if (isFiltered(property.getProperty("label_fail")) && (this.hybridTreatment == false)) {
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_fail") + ") \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
				} else if (isFiltered(property.getProperty("label_fail")) && (this.hybridTreatment == true)) {
					DescriptiveEngine descriptiveEngine = new DescriptiveEngine();
					double resultDescriptive = descriptiveEngine.descriptiveEngineHybrid(service, request, dataHybrid.getBasicCoefficient(),
							dataHybrid.getStructuralCoefficient(), dataHybrid.getAncestorCoefficient(),
							dataHybrid.getImmediateChldCoefficient(), dataHybrid.getLeafCoefficient(),
							dataHybrid.getSiblingCoefficient(), dataHybrid.getThreshold(), dataHybrid.getDicitonaryPath());
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_fail") + ") Hybrid \n");
					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " ( " + resultDescriptive + " ) Hybrid \n");
					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");				
				}
				service.setDegreeMatch("FAIL");
				break;
			}
		}
		return service;
	}
	
	/**
	 * Method that removes dual correspondences of parameters in the match. 
	 * @param result - An ArrayList of correspondences.
	 */
	public void removeDualCorrespondences(ArrayList<SimilarityDegree> result, char parameters) {  
		
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
		
	     /**
	      * verificar depois se vai funfar.
	      */
		 for(int i=0; i < result.size(); i++) {
			 if(result.get(i).getSimilarityDegree() == -1) {
				 result.remove(i);
				 i--;
			 }
		 }
	}
	
	
	/**
	 * Method that return the smallest degree of match between input and output parameters.
	 * @param resultInput - An ArrayList with correspondences of input parameters.
	 * @param resultOutput - An ArrayList with correspondences of output parameters.
	 * @return levelMatching - Smallest degree of match.
	 */
	private double returnLevelMatching(ArrayList<SimilarityDegree> resultInput, ArrayList<SimilarityDegree> resultOutput) {
		double levelMatching = FunctionalMatcher.NOTHING;
		
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
	 * Method that returns the String corresponding 
	 * to the filter
	 */
	private String returnResult(double levelMatching) {
		String result = "";
		
		if(levelMatching == 1.0)
			result = "SIBLING";
		else if(levelMatching == 2.0)
			result = "PLUGIN";
		else if(levelMatching == 3.0)
			result = "SUBSUMES";
		else if(levelMatching == 4.0)
			result = "EXACT";
		else
			result = "FAIL";
				
		return result;
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

	/**
	 * @return the servicesMatched
	 */
	public ArrayList<Service> getServicesMatched() {
		return servicesMatched;
	}

	/**
	 * @param servicesMatched the servicesMatched to set
	 */
	public void setServicesMatched(ArrayList<Service> servicesMatched) {
		this.servicesMatched = servicesMatched;
	}
	
	
	
}
