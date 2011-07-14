/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package owlsautocomposer.graph;

import pf.vo.Service;

/**
 *
 * @author Administrador
 */
public class Node {
    private Service service;
    private String name;

    public Node(Service service, String name)
    {
        this.service = service;
        this.name = name;
    }

    public Service getService()
    {
        return service;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
