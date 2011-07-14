package pf.matcher.interfaces;

import java.util.ArrayList;

import pf.vo.Query;
import pf.vo.Service;

/**
 * @author Rodrigo Almeida de Amorim
 * @version 0.1
 */

/**
 * Interface that contains methods whose must be implemented by a matcher implementation.
 */

public interface IMatcher {
	
	public void matcher(ArrayList<Service> services, Query request);
}
