/**
 * 
 */
package pf.vo;

/**
 * @author Rodrigo
 *
 */
/**
 * This class agregates all parameteres necessary to invoke the descriptive algorithm.
 */
public class DescriptiveData extends Data {

	private double basicCoefficient;
	
	private double structuralCoefficient;
	
	private double ancestorCoefficient;
	
	private double immediateChldCoefficient;
	
	private double leafCoefficient;
	
	private double siblingCoefficient;
	
	private double threshold;
	
	private String dicitonaryPath;

	/**
	 * @return the basicCoefficient
	 */
	public double getBasicCoefficient() {
		return basicCoefficient;
	}

	/**
	 * @param basicCoefficient the basicCoefficient to set
	 */
	public void setBasicCoefficient(double basicCoefficient) {
		this.basicCoefficient = basicCoefficient;
	}

	/**
	 * @return the structuralCoefficient
	 */
	public double getStructuralCoefficient() {
		return structuralCoefficient;
	}

	/**
	 * @param structuralCoefficient the structuralCoefficient to set
	 */
	public void setStructuralCoefficient(double structuralCoefficient) {
		this.structuralCoefficient = structuralCoefficient;
	}

	/**
	 * @return the ancestorCoefficient
	 */
	public double getAncestorCoefficient() {
		return ancestorCoefficient;
	}

	/**
	 * @param ancestorCoefficient the ancestorCoefficient to set
	 */
	public void setAncestorCoefficient(double ancestorCoefficient) {
		this.ancestorCoefficient = ancestorCoefficient;
	}

	/**
	 * @return the siblingCoefficient
	 */
	public double getSiblingCoefficient() {
		return siblingCoefficient;
	}

	/**
	 * @param siblingCoefficient the siblingCoefficient to set
	 */
	public void setSiblingCoefficient(double siblingCoefficient) {
		this.siblingCoefficient = siblingCoefficient;
	}

	/**
	 * @return the threshold
	 */
	public double getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	/**
	 * @return the dicitonaryPath
	 */
	public String getDicitonaryPath() {
		return dicitonaryPath;
	}

	/**
	 * @param dicitonaryPath the dicitonaryPath to set
	 */
	public void setDicitonaryPath(String dicitonaryPath) {
		this.dicitonaryPath = dicitonaryPath;
	}

	/**
	 * @return the immediateChldCoefficient
	 */
	public double getImmediateChldCoefficient() {
		return immediateChldCoefficient;
	}

	/**
	 * @param immediateChldCoefficient the immediateChldCoefficient to set
	 */
	public void setImmediateChldCoefficient(double immediateChldCoefficient) {
		this.immediateChldCoefficient = immediateChldCoefficient;
	}

	/**
	 * @return the leafCoefficient
	 */
	public double getLeafCoefficient() {
		return leafCoefficient;
	}

	/**
	 * @param leafCoefficient the leafCoefficient to set
	 */
	public void setLeafCoefficient(double leafCoefficient) {
		this.leafCoefficient = leafCoefficient;
	}
}
