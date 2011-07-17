/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package owlsautocomposer.graph;

import java.net.URI;


/**
 *
 * @author Administrador
 */
public class Edge {
    Node originNode, destinyNode;
    boolean fixedEdge;
    Edge equivalentEdge;
    URI uri;

    public Edge(Node originNode, Node destinyNode, URI uri, boolean fixedEdge, Edge equivalentEdge)
    {
        this.originNode = originNode;
        this.destinyNode = destinyNode;
        this.fixedEdge = fixedEdge;
        this.equivalentEdge = equivalentEdge;
        this.uri = uri;
    }

    public void setFixedEdge(boolean fixedEdge)
    {
            this.fixedEdge = fixedEdge;
    }

    public Edge getEquivalentEdge()
    {
        return equivalentEdge;
    }

    public Node getOriginNode() {
        return originNode;
    }

    public Node getDestinyNode() {
		return destinyNode;
    }

    public void setEdge(Node originNode, Node destinyNode, boolean fixedEdge)
    {
        this.originNode = originNode;
        this.destinyNode = destinyNode;
        this.fixedEdge = fixedEdge;
    }

    public URI getUri()
    {
        return uri;
    }

    public boolean getFixedEdge()
    {
        return fixedEdge;
    }

    public void setEquivalentEdge(Edge edge)
    {
        equivalentEdge = edge;
    }

    @Override
    public String toString()
    {
        String result = "(";
        if (getOriginNode() != null)
        {
            result += getOriginNode().getService().getUri()+" , ";
        }
        else
        {
           result += "NULL , ";
        }
        if (getDestinyNode() != null)
        {
            result += getDestinyNode().getService().getUri();
        }
        else
        {
           result += "NULL";
        }
        result += ")";
        return result;
    }
}
