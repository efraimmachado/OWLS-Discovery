/**
 * 
 */
package pf.vo;

/**
 * @author Rodrigo
 *
 */
/**
 * This class agregates all parameteres necessary to invoke the functional algorithm.
 */
public class FunctionalData extends Data{

	private String[] filters;
	
	private String[] filtersPE;
	
	private boolean hybridTreatment;
	
//	Lore
	private boolean PETreatment;

	/**
	 * @return the filters
	 */
	public String[] getFilters() {
		return filters;
	}

	/**
	 * @param filters the filters to set
	 */
	public void setFilters(String[] filters) {
		this.filters = filters;
	}

	/**
	 * @return the hybridTreatment
	 */
	public boolean isHybridTreatment() {
		return hybridTreatment;
	}

	/**
	 * @param hybridTreatment the hybridTreatment to set
	 */
	public void setHybridTreatment(boolean hybridTreatment) {
		this.hybridTreatment = hybridTreatment;
	}

//Lore INicio	
	public boolean isPETreatment() {
		return PETreatment;
	}

	public void setPETreatment(boolean pETreatment) {
		PETreatment = pETreatment;
	}

	public String[] getFiltersPE() {
		return filtersPE;
	}

	public void setFiltersPE(String[] filtersPE) {
		this.filtersPE = filtersPE;
	}
//Lore Fim
	
	
	
}
