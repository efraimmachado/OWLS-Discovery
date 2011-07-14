package pf.enigne.interfaces;

import pf.vo.Query;
import pf.vo.Service;

/**
 * @author Rodrigo Almeida de Amorim
 * @version 0.1
 */

/**
 * Interface that contains methods whose must be implemented by a engine implementation.
 */

public interface IEngine {

	Service classifyResults(Service services, Query query);
}
