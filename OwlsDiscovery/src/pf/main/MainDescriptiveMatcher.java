/**
 * 
 */
package pf.main;

import java.io.IOException;

import javax.swing.JTextArea;

import pf.enigne.implementations.descriptive.DescriptiveEngine;
import pf.enigne.implementations.functional.FunctionalEngine;
import pf.vo.DescriptiveData;

/**
 * @author rodrigo.amorim
 *
 */
public class MainDescriptiveMatcher {

	/**
	 * Element that holds all informations from output.
	 */
	static JTextArea result;
	
	/**
	 * Method main of the Functional Matcher. (Algorithm 1)
	 * @param args - Array of arguments
	 * @throws IOException 
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		double basicCoefficient = 0.5;
		double structuralCoefficient = 0.5;
		double ancestorCoefficient = 0.25;
		double immediateChldCoefficient = 0.25;
		double leafCoefficient = 0.25;
		double siblingCoefficient = 0.25;
		double threshold = 0.2;
		String fileName = "dictionary.txt";
		DescriptiveEngine descriptiveEngine = new DescriptiveEngine(args, basicCoefficient, structuralCoefficient, ancestorCoefficient,
											immediateChldCoefficient, leafCoefficient, siblingCoefficient, threshold, fileName);

	}
	
	/**
	 * Method that invokes the Descriptive Matcher. (Algorithm 2)
	 * @param args - Array of arguments.
	 * @param DescriptiveData - Element with all required parameters to invoke the algorithm.
	 * @throws IOException 
	 */
	public static void invoke(String[] args, DescriptiveData data, JTextArea txtResult) throws IOException {
		result = txtResult;
		new DescriptiveEngine(args, data.getBasicCoefficient(), data.getStructuralCoefficient(),
				data.getAncestorCoefficient(), data.getImmediateChldCoefficient(), data.getLeafCoefficient(),
				data.getSiblingCoefficient(), data.getThreshold(), args[2]);
	}
	
	/**
	 * Method that invokes the Descriptive Matcher. (Algorithm 2)
	 * @param args - Array of arguments.
	 * @param filter - filter of results to be shown in the GUI.
	 * @throws IOException 
	 * @deprecated - A Better method was implemented.
	 */
	@SuppressWarnings("unused")
	public static void invoke(String[] args, double basicCoefficient, double structuralCoefficient, double ancestorCoefficient, 
			double immediateChldCoefficient, double leafCoefficient, double siblingCoefficient, double threshold,
			JTextArea txtResult) throws IOException {
		result = txtResult;
		DescriptiveEngine descriptiveEngine = new DescriptiveEngine(args, basicCoefficient, structuralCoefficient, ancestorCoefficient,
				immediateChldCoefficient, leafCoefficient, siblingCoefficient, threshold, args[2]);
	}
	
	/**
	 * Method that writes results in the output
	 * @param text - text that will be written.
	 */
	public static void writeOutput(String text) {
		result.setText(result.getText() + text);
	}
}
