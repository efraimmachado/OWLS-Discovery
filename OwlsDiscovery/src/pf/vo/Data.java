/**
 * 
 */
package pf.vo;

/**
 * @author Rodrigo
 *
 */

/**
 * This class agregates all necessary parameters  to invoke the discovery algorithm. 
 */
public class Data {

	private String servicesPath;
	
	private String requestPath;

	/**
	 * @return the servicesPath
	 */
	public String getServicesPath() {
		return servicesPath;
	}

	/**
	 * @param servicesPath the servicesPath to set
	 */
	public void setServicesPath(String servicesPath) {
		this.servicesPath = servicesPath;
	}

	/**
	 * @return the requestPath
	 */
	public String getRequestPath() {
		return requestPath;
	}

	/**
	 * @param requestPath the requestPath to set
	 */
	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}
}
