/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pf.matcher.implementations.functional;

import java.util.ArrayList;
import pf.vo.Service;

/**
 *
 * @author Administrador
 */
public class CompositionalServiceInputMatch {
    Service service;
    ArrayList<SimilarityDegree> inputsSimilarity;

    public CompositionalServiceInputMatch(Service service, ArrayList<SimilarityDegree> inputsSimilarity)
    {
          this.service = service;
          this.inputsSimilarity = inputsSimilarity;
    }

    public Service getService()
    {
        return service;
    }

    public ArrayList<SimilarityDegree> getInputsSimilarity()
    {
        return inputsSimilarity;
    }

}
