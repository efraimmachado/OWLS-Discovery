package pf.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;

import pf.enigne.implementations.functional.FunctionalEngine;
import pf.vo.DescriptiveData;
import pf.vo.FunctionalData;
import pf.vo.Service;

/**
 * Class main of the Functional Matcher (Algorithm 1)
 * @author Rodrigo Almeida de Amorim
 * @version 0.1
 */
public class MainFunctionalMatcher {

	/**
	 * Element that holds all informations from output.
	 */
	static JTextArea result;
	
	/**
	 * Method that invokes the Functional Matcher. (Algorithm 1)
	 * @param args - Array of arguments.
	 * @param filter - filter of results to be shown in the GUI.
	 * @param filterPE - filterPE of results to be shown in the GUI.
	 * @throws IOException 
	 * @deprecated - Another better method was implemented.
	 */
	@SuppressWarnings("unused")
	public static void invoke(String[] args, String[] filter, String[] filterPE, JTextArea txtResult) throws IOException {
		result = txtResult;
		FunctionalEngine functionalEngine = new FunctionalEngine(args, filter, filterPE, null, false);
	}
	
	/**
	 * Method that invokes the Functional Matcher. (Algorithm 1)
	 * @param args - Array of arguments.
	 * @param FuncitonalData - Element with all required informations to invoke the algorithm.
	 * @throws IOException
         * @deprecated - Another better method was implemented.
	 */
	@SuppressWarnings("unused")
	public static void invoke(String[] args, FunctionalData data, DescriptiveData dataHybrid, JTextArea txtResult) throws IOException {
		result = txtResult;
//		Lore
//		FunctionalEngine functionalEngine = new FunctionalEngine(args, data.getFilters(), dataHybrid, data.isHybridTreatment());
//		Lore
//		FunctionalEngine functionalEngine = new FunctionalEngine
//		(args, data.getFilters(), data.getFiltersPE(),  dataHybrid, data.isHybridTreatment(), data.isPETreatment());
	}
///efraim
        @SuppressWarnings("unused")
	public static void invoke(String[] args, FunctionalData data, DescriptiveData dataHybrid, boolean isCompositionValid, JTextArea txtResult) throws IOException {
            result = txtResult;
//		Lore
//		FunctionalEngine functionalEngine = new FunctionalEngine(args, data.getFilters(), dataHybrid, data.isHybridTreatment());
//		Lore
            FunctionalEngine functionalEngine = new FunctionalEngine(args, data.getFilters(), data.getFiltersPE(),  dataHybrid, data.isHybridTreatment(), data.isPETreatment(), isCompositionValid);
     	}
///efraim-fim

	/**
	 * Method that invokes the Functional Matcher. (Algorithm 1)
	 * @param args - Array of arguments.
	 * @param FuncitonalData - Element with all required informations to invoke the algorithm.
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	public static ArrayList<Service> getMatchedResult(String[] args, FunctionalData data, DescriptiveData dataHybrid, JTextArea txtResult) throws IOException {
                System.out.printf("efraim 2\n");
		result = txtResult;
		FunctionalEngine functionalEngine = new FunctionalEngine(args, data.getFilters(), data.getFiltersPE(), dataHybrid, data.isHybridTreatment());
		return functionalEngine.getServicesMatched();
	}
	
	/**
	 * Method that make a Functional Discovery.
	 * @param request - URI that defines the request path. 
	 * @param serviceDirectory - Directory of Services.
	 * @return Return the discovered services.
	 * @throws IOException 
	 */
	public List<Service> discoverServices(String request, String serviceDirectory) throws IOException {
		String[] arg = new String[2];
		arg[0] = request;
		arg[1] = serviceDirectory;
		
		String[] filter = new String[5];
		filter[0] = "EXACT";
		filter[1] = "PLUG IN";
		filter[2] = "SUBSUMES";
		filter[3] = "SIBLING";
		filter[4] = "FAIL";
		
		FunctionalData functionalData = new FunctionalData();
		functionalData.setFilters(filter);
		
		FunctionalEngine functionalEngine = new FunctionalEngine(arg, functionalData.getFilters(), functionalData.getFiltersPE(), null, false);
		return functionalEngine.getServicesMatched();
	}
	

	public static void main(String[] args) throws IOException {
		List<Service> lista = null;
		MainFunctionalMatcher teste = new MainFunctionalMatcher();
		lista = teste.discoverServices("C:/xampp/htdocs/queries/1.1/car_price_service.owls",
				"C:/xampp/htdocs/services/transp");
		for(int i=0; i < lista.size(); i++) {
			Service servico = lista.get(i);
			System.out.println("SERVI?O: " + servico.getUri());
			System.out.println("SIMILARIDADE: " + servico.getDegreeMatch());
			System.out.println("---------------------------------------------");
		}
	}
	
	/**
	 * Method that writes results in the output
	 * @param text - text that will be written.
	 */
	public static void writeOutput(String text) {
		if (result != null) {
			result.setText(result.getText() + text);
		}
	}

}
