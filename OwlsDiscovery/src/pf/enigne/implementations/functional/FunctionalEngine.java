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
                                //System.out.println("teste fundamental "+inputManager.getServices().get(i).getInputList()+"\n"+functionalMatcher.getResultInputs().get(i).toString());
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
                            finalNode = new Node(new Service(inputManager.getRequest().getOutputList(), new ArrayList<URI>(), new URI("FINAL_STATE")), "FINAL_STATE");
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
                            //ficar de olho nisso... acho que o output tem que ser o input...como est� ;)
                            inputManager.addService(new Service(new ArrayList<URI>(), inputManager.getRequest().getInputList(), new URI("REQUEST_STATE")));
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(FunctionalEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }

			ArrayList<Edge> pendencyEdges = graph.getPendencyEdges();
			while(pendencyEdges.size() > 0)
			{
                                System.out.println("Existem "+pendencyEdges.size()+" pend�ncias");
				//take the first pendency. it looks like fifo... i guess ;)
				Edge edge = pendencyEdges.get(0);
				Node newNode = null;
				Node annoyingNode = edge.getDestinyNode();//this node is annoying because it causes the pendency... like a woman :O
                                System.out.println("resolvendo pend�ncia "+edge.getUri());
				//looking for compatible services avaliable
                                try {
                                    ArrayList<URI> uriList = new ArrayList<URI>();
                                    uriList.add(edge.getUri());
                                    functionalMatcher.compositionalMatcher(inputManager.getServices(), new Query(null, uriList, new URI("")));
                                } catch (URISyntaxException ex) {
                                    Logger.getLogger(FunctionalEngine.class.getName()).log(Level.SEVERE, null, ex);
                                }
				//falta setar o megazord... teoricamente ira funcionar... mas eu vou ajeitar ainda heuaheuaheuahe
                                ArrayList<ArrayList<SimilarityDegree>> inputResults = functionalMatcher.getResultInputs();
//                                for(int i=0; i <inputResults.size(); i++ )
//                                {
//                                    this.setResultInputs(inputResults.get(i));
//                                    this.removeDualCorrespondences(this.getResultInputs(), FunctionalMatcher.INPUT);
//                                    System.out.println("Servico "+inputManager.getServices().get(i).getUri());
//                                    System.out.println("outputs "+inputManager.getServices().get(i).getOutputList());
//                                    servicesMatched.add(this.classifyCompositionalResults(inputManager.getServices().get(i),inputManager.getRequest()));
//            			}
                                filterResult(inputResults);
        			//choice the best service to solve the pendency

				while (newNode == null && !servicesMatched.isEmpty())
				{
                                        System.out.println("Escolhendo melhor no");
                                        newNode = BestChoice(servicesMatched);
                                        servicesMatched.remove(newNode);
                                        System.out.println("no escolhido "+newNode.getService().getUri().toString());
					if(graph.getForbiddenNodes().contains(newNode))
					{
                                            System.out.println("este no pertence a lista de nos proibidos");
                                            newNode = null;
                                        }

				}
				//add the new node and the others compatible nodes (services) but set their edges to no fixed
				if (newNode != null)
				{
                                        System.out.println("no definitivo "+newNode.getService().getUri().toString());
					graph.addNode(newNode);
					edge.setEdge(newNode, edge.getDestinyNode(),true);
					for (int i = 0; i < servicesMatched.size() ; i++)
					{
                                            if (servicesMatched.get(i).getDegreeMatch()!= null)
                                            {
                                                Node alternativeNode = new Node(servicesMatched.get(i), null);
                                                graph.addNode(alternativeNode);
                                                graph.addEdge(alternativeNode, edge.getDestinyNode(), servicesMatched.get(i).getUri(), false);
                                            }
					}
					//test if there is a cicle with the new service... i said it is a annoying node... ok ok, it isnt his fault, sry
                                        System.out.println("testando a exist�ncia de ciclo");
					if (graph.thereIsAPath(edge.getDestinyNode(),newNode))
					{
                                                System.out.printf("Existe um ciclo");
						graph.addForbiddenNode(annoyingNode);
						graph.removeNodeUntilNoFixedEdge(annoyingNode, null); //power power rangers verificar se eh null mesmo
					}
                                        System.out.println("no adicionado com sucesso");
				}
				else //remove the new node because the pendencies cant be solved... i said it is a annoying node
				{
                                                System.out.printf("a pendencia nao pode ser sanada, removendo o "+annoyingNode.getService().getUri());
						graph.addForbiddenNode(annoyingNode);
						graph.removeNodeUntilNoFixedEdge(annoyingNode, null);
						if (!graph.getNodes().contains(finalNode))
						{
                                                        System.out.println("SEM SOLUCAO");
							//oh my god of pagode what can i do?
							//just cry little butterfly
						}
				}
				//take new pendencies...
				pendencyEdges = graph.getPendencyEdges();
			}
                        graph.removeUnsed();
                        graph.print();

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
	 * @param service - A service
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
	 * Method that classifies the obtained results.
	 * @param service - A service
	 * @param request - A request
	 */
	public Service classifyCompositionalResults(Service service, Query request) {
		int levelMatching;
		Properties property = MessageProperties.getInstance();
                levelMatching = (int) returnCompositionalLevelMatching(resultInputs);

		//Writing the correspondences
		///efraim modificou para false
                this.PETreatment = false;

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
	private double returnCompositionalLevelMatching(ArrayList<SimilarityDegree> resultInput) {
		double levelMatching = FunctionalMatcher.NOTHING;
		
		for(int i=0; i < resultInput.size(); i++) {
			if(resultInput.get(i).getSimilarityDegree() < levelMatching) {
				levelMatching = resultInput.get(i).getSimilarityDegree(); 
			}
		}
		return levelMatching;
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

    private Node BestChoice(ArrayList<Service> servicesMatched) {

        int result = 0;
        int maxDegree = 0;
        int minInput  = Integer.MAX_VALUE;
        for (int i = 0; i < servicesMatched.size(); i++)
        {
            int valor = 0; //grau.equals("FAIL") = true, undestood?
            String grau = servicesMatched.get(i).getDegreeMatch();
            if (grau == null)
                continue;
            else if(grau.equals("EXACT"))
                valor = 4;
            else if(grau.equals("SUBSUMES"))
                valor = 3;
            else if (grau.equals("PLUGIN"))
                valor = 2;
            else if (grau.equals("SIBLING"))
                valor = 1;
//            else if (grau.equals("FAIL"))
//                valor = 0;
            //System.out.println("Antes maxdegree "+maxDegree+" mininput "+minInput);
            System.out.println("Servi�o "+servicesMatched.get(i).getUri());
            System.out.println("grau "+valor);
            System.out.println("numeroinput "+servicesMatched.get(i).getInputList().size());

            if (valor > maxDegree)
            {
                result = i;
                maxDegree = valor;
                minInput = servicesMatched.get(i).getInputList().size();
            }
            else if(valor == maxDegree)
            {
                if (servicesMatched.get(i).getInputList().size() < minInput)
                {
                    result = i;
                    minInput = servicesMatched.get(i).getInputList().size();
                }
            }
            System.out.println("Depois maxdegree "+maxDegree+" mininput "+minInput);
        }
        return new Node(servicesMatched.get(result), null);
    }

    private void filterResult(ArrayList<ArrayList<SimilarityDegree>> inputResults) {

        Properties property = MessageProperties.getInstance();
        for (int i = 0; i < inputResults.size(); i++)
        {
            ArrayList<SimilarityDegree> serviceInputResult = inputResults.get(i);
            System.out.println("inputs antes "+inputResults.get(i));
            for (int j = 0; j < serviceInputResult.size(); j++)
            {
    		switch((int)serviceInputResult.get(j).getSimilarityDegree())
                {
			case (FunctionalMatcher.EXACT): {
				if (isFiltered(property.getProperty("label_exact")) && (this.PETreatment == true))
                                {
                                    PEEngine peEngine = new PEEngine();
//					double level = peEngine.PEEngineResult(service, request);
					//if(isFilteredPE(result)){
//						MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
                                        //}
				}
                                else if (isFiltered(property.getProperty("label_exact")))
                                {

				}
                                else
                                {
                                    serviceInputResult.remove(j);
                                    j--;
                                }
				break;
                }
			case (FunctionalMatcher.PLUGIN):
                        {
				if (isFiltered(property.getProperty("label_plugin"))&& (this.PETreatment == true))
                                {
					PEEngine peEngine = new PEEngine();
					//double level = peEngine.PEEngineResult(service, request);
					//String result = returnResult(level);
				//	if(isFilteredPE(result)){
						//MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
				//	}
				}
                                else if (isFiltered(property.getProperty("label_plugin")))
                                {

                                }
                                else
                                {
                                    serviceInputResult.remove(j);
                                    j--;
                                }
				break;
			}
			case (FunctionalMatcher.SUBSUMES):
                        {
				if (isFiltered(property.getProperty("label_subsumes")) && (this.PETreatment == true)) {
					PEEngine peEngine = new PEEngine();
//					double level = peEngine.PEEngineResult(service, request);
//					String result = returnResult(level);
					//if(isFilteredPE(result)){
						//MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
					//}
				}
                                else if (isFiltered(property.getProperty("label_subsumes")))
                                {

                                }
                                 else
                                {
                                    serviceInputResult.remove(j);
                                    j--;
                                }
				break;
			}
			case (FunctionalMatcher.SIBLING):
                        {
				if (isFiltered(property.getProperty("label_sibling")) && (this.PETreatment == true)) {
					PEEngine peEngine = new PEEngine();
			//		double level = peEngine.PEEngineResult(service, request);
			//		String result = returnResult(level);
		//			if(isFilteredPE(result)){
		//				MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
		//			}
				}
                                else if (isFiltered(property.getProperty("label_sibling")))
                                {

                                }
                                else
                                {
                                    serviceInputResult.remove(j);
                                    j--;
                                }
				break;
			}
			default:
                        {
				if (isFiltered(property.getProperty("label_fail")) && (this.hybridTreatment == false))
                                {
					//MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_fail") + ") \n");
					//MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
				}
                                else if (isFiltered(property.getProperty("label_fail")) && (this.hybridTreatment == true))
                                {
					DescriptiveEngine descriptiveEngine = new DescriptiveEngine();
//					double resultDescriptive = descriptiveEngine.descriptiveEngineHybrid(service, request, dataHybrid.getBasicCoefficient(),
//							dataHybrid.getStructuralCoefficient(), dataHybrid.getAncestorCoefficient(),
//							dataHybrid.getImmediateChldCoefficient(), dataHybrid.getLeafCoefficient(),
//							dataHybrid.getSiblingCoefficient(), dataHybrid.getThreshold(), dataHybrid.getDicitonaryPath());
				}
                                else
                                {
                                    serviceInputResult.remove(j);
                                    j--;
                                }
				break;
			}
                }
            }
            System.out.println("inputs depois "+inputResults.get(i));
            if (inputResults.get(i).size() == 0)
            {
                i--;
            }
        }
        System.out.println("fim");
    }
	
	
	
}
