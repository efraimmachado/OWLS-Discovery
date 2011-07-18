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
import pf.matcher.implementations.functional.CompositionalServiceInputMatch;
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
                        boolean noSolution = false;
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
        			graph.addEdge(null, finalNode, NodeInputs.get(k),null,  true);//deve adicionar uma aresta para cada input dele, ou seja, para cada output da requisicao
                        }
			//the next line makes the request input be used in the composition if it is necessary
                        try {
                            //ficar de olho nisso... acho que o output tem que ser o input...como está ;)
                            inputManager.addService(new Service(new ArrayList<URI>(), inputManager.getRequest().getInputList(), new URI("REQUEST_STATE")));
                        } catch (URISyntaxException ex) {
                            Logger.getLogger(FunctionalEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }

			ArrayList<Edge> pendencyEdges = graph.getPendencyEdges();
			while(pendencyEdges.size() > 0 && noSolution == false)
			{
                                System.out.println("Pendências:");
                                for (int i = 0; i< pendencyEdges.size(); i++)
                                    System.out.println(pendencyEdges.get(i).getUri());
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
                                ArrayList<ArrayList<SimilarityDegree>> inputResults = functionalMatcher.getResultInputs();
//                                for(int i=0; i <inputResults.size(); i++ )
//                                {
//                                    this.setResultInputs(inputResults.get(i));
//                                    this.removeDualCorrespondences(this.getResultInputs(), FunctionalMatcher.INPUT);
//                                    System.out.println("Servico "+inputManager.getServices().get(i).getUri());
//                                    System.out.println("outputs "+inputManager.getServices().get(i).getOutputList());
//                                    servicesMatched.add(this.classifyCompositionalResults(inputManager.getServices().get(i),inputManager.getRequest()));
//            			}
                                //removing services catched by user filter (exact, subsumes....) the service is passed to take the PE
                                filterResult(inputResults, annoyingNode.getService());
                                //organise the matchs with its services
                                ArrayList<CompositionalServiceInputMatch> compServicesInputMatch = CreateInputMatch(inputManager.getServices(),inputResults);
//                                System.out.println("testando a lista criada");
//                                for (int i = 0; i < compServicesInputMatch.size(); i++)
//                                {
//                                    System.out.println("Servico "+compServicesInputMatch.get(i).getService().getUri());
//                                    for (int j=0;j<compServicesInputMatch.get(i).getService().getOutputList().size();j++)
//                                        System.out.println("inputList (servico) "+compServicesInputMatch.get(i).getService().getOutputList().get(j));
//                                    for (int j=0;j<compServicesInputMatch.get(i).getInputsSimilarity().size();j++)
//                                    System.out.println("inputList (estrutura) "+compServicesInputMatch.get(i).getInputsSimilarity().get(j).getServiceParameter());
//
//                                }
                               //choice the best service to solve the pendency
                                if (true)
                                    
				while (newNode == null && !compServicesInputMatch.isEmpty())
				{
                                        System.out.println("Escolhendo melhor servico");
                                        CompositionalServiceInputMatch compServInp = BestChoice(compServicesInputMatch, graph);
                                        newNode = new Node(compServInp.getService(),null);
                                        compServicesInputMatch.remove(compServInp);
                                        System.out.println("servico escolhido "+newNode.getService().getUri());
					if(graph.isForbiddenNode(newNode))
					{
                                            System.out.println("este no pertence a lista de nos proibidos");
                                            newNode = null;
                                        }

				}
				//add the new node and the others compatible nodes (services) but set their edges to no fixed
				if (newNode != null)
				{
                                        System.out.println("servico definitivo "+newNode.getService().getUri());
					graph.addNode(newNode);
					edge.setEdge(newNode, edge.getDestinyNode(),true);
//					for (int i = 0; i < compServicesInputMatch.size() ; i++)
//					{
//                                            System.out.println("adicionando nos alternativos "+compServicesInputMatch.get(i).getService().getUri());
//                                            Node alternativeNode = new Node(compServicesInputMatch.get(i).getService(), null);
//                                            graph.addNode(alternativeNode);
//                                            graph.addEdge(alternativeNode, edge.getDestinyNode(), edge.getUri(),edge, false);
//					}
					//test if there is a cicle with the new service... i said it is a annoying node... ok ok, it isnt his fault, sry
                                        System.out.println("testando a existência de ciclo");
					if (graph.thereIsAPath(edge.getDestinyNode(),newNode))
					{
                                                System.out.printf("Existe um ciclo");
						graph.addForbiddenNode(annoyingNode);
						graph.removeNodeUntilNoFixedEdge(annoyingNode, null); //power power rangers verificar se eh null mesmo
					}
                                        System.out.println("no adicionado com sucesso");
                                        //adding more new pendencies (the new node ^^)
                                        NodeInputs = newNode.getService().getInputList();
                                        for (int k = 0; k < NodeInputs.size(); k++)
                                        {
                                                graph.addEdge(null, newNode, NodeInputs.get(k),null,  true);//deve adicionar uma aresta para cada input dele, ou seja, para cada output da requisicao
                                        }
				}
				else //remove the annoying node because the pendencies cant be solved... i said it is a annoying node
				{
                                                System.out.println("a pendencia nao pode ser sanada, removendo o "+annoyingNode.getService().getUri());
						graph.addForbiddenNode(annoyingNode);
                                                //graph.print();
                                                //System.out.println("removendo "+newNode.getService().getUri());
                                                graph.getNodes().remove(annoyingNode);
                                                for (int q = 0; q < graph.getEdges().size(); q++)
                                                {
                                                    if (graph.getEdges().get(q).getOriginNode()!= null)
                                                    {
                                                        if (graph.getEdges().get(q).getOriginNode() == annoyingNode)
                                                        {
                                                            graph.getEdges().get(q).setEdge(null,graph.getEdges().get(q).getDestinyNode() , true);
                                                        }
                                                    }
                                                    else if(graph.getEdges().get(q).getDestinyNode() == annoyingNode)
                                                    {
                                                            System.out.println("removendo "+graph.getEdges().get(q));
                                                            graph.getEdges().remove(graph.getEdges().get(q));
                                                    }
                                                }
						//graph.removeNodeUntilNoFixedEdge(annoyingNode, null);
						if (!graph.getNodes().contains(finalNode))
						{
							noSolution = true;
						}
				}
				//take new pendencies...
				pendencyEdges = graph.getPendencyEdges();
			}
                        if (noSolution)
                        {
                            System.out.println("\nSOLUCAO IMPOSSIVEL");
                        }
                        else
                        {
                            System.out.println("\nSOLUCAO ENCONTRADA");
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
//	public Service classifyCompositionalResults(Service service, Query request) {
//		int levelMatching;
//		Properties property = MessageProperties.getInstance();
//                levelMatching = (int) returnCompositionalLevelMatching(resultInputs);
//
//		//Writing the correspondences
//		///efraim modificou para false
//                this.PETreatment = false;
//
//		switch(levelMatching) {
//			case (FunctionalMatcher.EXACT): {
//				if (isFiltered(property.getProperty("label_exact")) && (this.PETreatment == true)) {
//					PEEngine peEngine = new PEEngine();
//
//					double level = peEngine.PEEngineResult(service, request);
//
//					this.startTimePE = peEngine.getStartTimePE();
//					this.endTimePE = peEngine.getEndTimePE();
//
//					String result = returnResult(level);
//					if(isFilteredPE(result)){
//						MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
//					}
//
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_exact") + ") \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//					service.setDegreeMatch("EXACT");
//				} else if (isFiltered(property.getProperty("label_exact"))) {
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_exact") + ") \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//					service.setDegreeMatch("EXACT");
//				}
//				break;
//			}
//			case (FunctionalMatcher.PLUGIN): {
//				if (isFiltered(property.getProperty("label_plugin"))&& (this.PETreatment == true)) {
//					PEEngine peEngine = new PEEngine();
//					double level = peEngine.PEEngineResult(service, request);
//					String result = returnResult(level);
//					if(isFilteredPE(result)){
//						MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
//					}
//
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_plugin") + ") \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//					service.setDegreeMatch("PLUGIN");
//
//				} else if (isFiltered(property.getProperty("label_plugin"))) {
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_plugin") + ") \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//					service.setDegreeMatch("PLUGIN");
//				}
//				break;
//			}
//			case (FunctionalMatcher.SUBSUMES): {
//				if (isFiltered(property.getProperty("label_subsumes")) && (this.PETreatment == true)) {
//					PEEngine peEngine = new PEEngine();
//					double level = peEngine.PEEngineResult(service, request);
//					String result = returnResult(level);
//					if(isFilteredPE(result)){
//						MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
//					}
//
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_subsumes") + ") \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//					service.setDegreeMatch("SUBSUMES");
//
//				} else if (isFiltered(property.getProperty("label_subsumes"))) {
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_subsumes") + ") \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//					service.setDegreeMatch("SUBSUMES");
//				}
//				break;
//			}
//			case (FunctionalMatcher.SIBLING): {
//				if (isFiltered(property.getProperty("label_sibling")) && (this.PETreatment == true)) {
//					PEEngine peEngine = new PEEngine();
//					double level = peEngine.PEEngineResult(service, request);
//					String result = returnResult(level);
//					if(isFilteredPE(result)){
//						MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + result + ") Preconditions/Effects \n");
//					}
//
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_sibling") + ") \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//					service.setDegreeMatch("SIBLING");
//
//				} else if (isFiltered(property.getProperty("label_sibling"))) {
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_sibling") + ") \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//					service.setDegreeMatch("SIBLING");
//				}
//				break;
//			}
//			default: {
//
//				if (isFiltered(property.getProperty("label_fail")) && (this.hybridTreatment == false)) {
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_fail") + ") \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//				} else if (isFiltered(property.getProperty("label_fail")) && (this.hybridTreatment == true)) {
//					DescriptiveEngine descriptiveEngine = new DescriptiveEngine();
//					double resultDescriptive = descriptiveEngine.descriptiveEngineHybrid(service, request, dataHybrid.getBasicCoefficient(),
//							dataHybrid.getStructuralCoefficient(), dataHybrid.getAncestorCoefficient(),
//							dataHybrid.getImmediateChldCoefficient(), dataHybrid.getLeafCoefficient(),
//							dataHybrid.getSiblingCoefficient(), dataHybrid.getThreshold(), dataHybrid.getDicitonaryPath());
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " (" + property.getProperty("label_fail") + ") Hybrid \n");
//					MainFunctionalMatcher.writeOutput(service.getUri().toString() + " ( " + resultDescriptive + " ) Hybrid \n");
//					MainFunctionalMatcher.writeOutput("---------------------------------------------------------------------------------------------------------------------\n");
//				}
//				service.setDegreeMatch("FAIL");
//				break;
//			}
//		}
//		return service;
//	}


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

    private CompositionalServiceInputMatch BestChoice(ArrayList<CompositionalServiceInputMatch> servicesInputMatch, Graph graph) {

        int serviceChoosenIndex = 0;
        //int inputChoosenIndex = 0;
        double maxDegree = 0.0;
        int minInput  = Integer.MAX_VALUE;

        for (int i = 0; i < servicesInputMatch.size(); i++)
        {
            CompositionalServiceInputMatch serviceInputMatch = servicesInputMatch.get(i);
            for (int j = 0; j < serviceInputMatch.getInputsSimilarity().size(); j++)
            {
                double degreeValue = serviceInputMatch.getInputsSimilarity().get(j).getSimilarityDegree();
                if (degreeValue > maxDegree)
                {
                    serviceChoosenIndex = i;
                    //inputChoosenIndex   = j;
                    maxDegree = degreeValue;
                    minInput = serviceInputMatch.getService().getInputList().size();
                    //System.out.println("minInput "+minInput+" get "+serviceInputMatch.getService().getInputList().size());
                }
                else if(degreeValue == maxDegree)
                {
                    System.out.println("comparando "+servicesInputMatch.get(serviceChoosenIndex).getService().getUri()+"\n com "+serviceInputMatch.getService().getUri());
//                    if(!graph.serviceUsed(servicesInputMatch.get(serviceChoosenIndex).getService()) &&
//                            graph.serviceUsed(serviceInputMatch.getService()) &&
                    if(
                            serviceInputMatch.getService().getInputList().size() < minInput)
                    {
                        serviceChoosenIndex = i;
                        //inputChoosenIndex   = j;
                        minInput = serviceInputMatch.getService().getInputList().size();
                        //System.out.println("minInput "+minInput+" get "+serviceInputMatch.getService().getInputList().size());
                    }
                    System.out.println("vencedor "+servicesInputMatch.get(serviceChoosenIndex).getService().getUri());
                }
            }
        }
        //System.out.println("grau "+maxDegree+" | "+"numero de inputs "+minInput);
        //for (int i = 0; i < servicesInputMatch.get(serviceChoosenIndex).getService().getInputList().size();i++)
           //System.out.println(">>"+servicesInputMatch.get(serviceChoosenIndex).getService().getInputList().get(i));
        return servicesInputMatch.get(serviceChoosenIndex);
    }

    private void filterResult(ArrayList<ArrayList<SimilarityDegree>> inputResults, Service service) {

        Properties property = MessageProperties.getInstance();
        for (int i = 0; i < inputResults.size(); i++)
        {
            ArrayList<SimilarityDegree> serviceInputResult = inputResults.get(i);
           // System.out.println("servico "+inputManager.getServices().get(i).getUri());
            //System.out.println("inputs antes "+inputResults.get(i));
            //itering in service input match degree...
            for (int j = 0; j < serviceInputResult.size(); j++)
            {
                //System.out.println(serviceInputResult.get(j).getServiceParameter());
    		switch((int)serviceInputResult.get(j).getSimilarityDegree())
                {
			case (FunctionalMatcher.EXACT): {
				if (isFiltered(property.getProperty("label_exact")) && (this.PETreatment == true))
                                {
                                    PEEngine peEngine = new PEEngine();
                                    Query request =  new Query(service.getInputList(), service.getOutputList(), service.getPreconditionList(), service.getEffectList(), service.getUri());
                                    double level = peEngine.PECompositionalEngineResult(inputManager.getServices().get(i), request);
                                    String result = returnResult(level);
                                    if(isFilteredPE(result))
                                    {
                                        System.out.println("filtro PE ok");
                                    }
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
                                    Query request =  new Query(service.getInputList(), service.getOutputList(), service.getPreconditionList(), service.getEffectList(), service.getUri());
                                    double level = peEngine.PECompositionalEngineResult(inputManager.getServices().get(i), request);
                                    String result = returnResult(level);
                                    if(isFilteredPE(result))
                                    {
                                        System.out.println("filtro PE ok");
                                    }
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
				if (isFiltered(property.getProperty("label_subsumes")) && (this.PETreatment == true))
                                {
                                    PEEngine peEngine = new PEEngine();
                                    Query request =  new Query(service.getInputList(), service.getOutputList(), service.getPreconditionList(), service.getEffectList(), service.getUri());
                                    double level = peEngine.PECompositionalEngineResult(inputManager.getServices().get(i), request);
                                    String result = returnResult(level);
                                    if(isFilteredPE(result))
                                    {
                                        System.out.println("filtro PE ok");
                                    }
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
				if (isFiltered(property.getProperty("label_sibling")) && (this.PETreatment == true))
                                {
                                    PEEngine peEngine = new PEEngine();
                                    Query request =  new Query(service.getInputList(), service.getOutputList(), service.getPreconditionList(), service.getEffectList(), service.getUri());
                                    double level = peEngine.PECompositionalEngineResult(inputManager.getServices().get(i), request);
                                    String result = returnResult(level);
                                    if(isFilteredPE(result))
                                    {
                                        System.out.println("filtro PE ok");
                                    }
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
                                else
                                {
                                    serviceInputResult.remove(j);
                                    j--;
                                }
				break;
			}
                }
            }
         //   System.out.println("inputs depois "+inputResults.get(i));
//            if (inputResults.get(i).size() == 0)
//            {
//                i--;
//            }
        }
    }

    private ArrayList<CompositionalServiceInputMatch> CreateInputMatch(ArrayList<Service> services, ArrayList<ArrayList<SimilarityDegree>> inputResults)
    {
        System.out.println("crindo lista de matching");
        ArrayList<CompositionalServiceInputMatch> result = new ArrayList<CompositionalServiceInputMatch>();
        for (int i = 0; i < inputResults.size(); i++)
        {
            if (!inputResults.get(i).isEmpty())
            {
                result.add(new CompositionalServiceInputMatch(services.get(i),inputResults.get(i)));
            }
        }
        return result;
    }
}
