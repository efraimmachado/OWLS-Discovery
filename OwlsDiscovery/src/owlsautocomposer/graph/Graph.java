/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package owlsautocomposer.graph;


import java.net.URI;
import java.util.ArrayList;
import pf.vo.Service;

/**
 *
 * @author Administrador
 */
public class Graph {
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    ArrayList<Node> forbiddenNodes;

    public Graph()
    {
        nodes = new ArrayList<Node>();
        forbiddenNodes = new ArrayList<Node>();
        edges = new ArrayList<Edge>();
    }
//    public void addNode(Service newService, Node node, boolean fixedEdge)
//    {
//        Node newNode = new Node(newService, "");
//        Edge newEdge = new Edge(newNode, node, fixedEdge, null);
//        edges.add(newEdge);
//        nodes.add(newNode);
//    }

//    public void addNode(Node newNode, Node node, boolean fixedEdge)
//    {
//        System.out.printf("adicionando no");
//        Edge newEdge = new Edge(newNode, node, fixedEdge, null);
//        edges.add(newEdge);
//        nodes.add(newNode);
//    }

    private boolean thereIsNode(Node node)
    {
        return nodes.contains(node);
    }
    
    public boolean addNode(Node newNode)
    {
        if (thereIsNode(newNode))
        {
            return false;
        }
        else
        {
            nodes.add(newNode);
            return true;
        }
    }

    //dependency edges are the edges without origin-destiny
    public ArrayList<Edge> getPendencyEdges()
    {
        ArrayList<Edge> pendencyEdges = new ArrayList<Edge>();
        for (int i = 0; i < edges.size();  i++)
        {
            if (edges.get(i).getOriginNode() == null)
            {
                pendencyEdges.add(edges.get(i));
            }
        }
        return pendencyEdges;
}

//that function can be the target of softwares eng.s because it makes the cyclomatic complexity grows up and it looks like (a little) a megazord  because it removes and add things ;)
public void removeNodeUntilNoFixedEdge(Node node, Edge fromThatEdge)
{
    //maybe i have to verify if the node is null because it is recursive in a graph, ie, two differents calls of a same function can try to delete the same node...
    //well, it is not make concurrently but i dont know if i can pass a null object to a arraylist (see the if condition above)

    if (nodes.contains(node))
    {
        System.out.println("removendo "+node.getService().getUri());
        boolean equivalentEdgeFound = false;
        ArrayList<Edge> edgesToNode = getAllEdgesTo(node);
        if (fromThatEdge == null)
        {
            System.out.println("no folha");
            System.out.println("removendo arestas para o no "+node.getService().getUri());
            edges.remove(edgesToNode);
        }
        else
        {
            System.out.println("existem arestas para o no");
            for (int i = 0; i < edgesToNode.size(); i++)
            {
                System.out.println("testando se existe aresta equivalente a "+fromThatEdge);
                if (fromThatEdge == edgesToNode.get(i).getEquivalentEdge())
                {
                    //É NECESSARIO COLOCAR AS OUTRAS ARESTAS EQUIVALENTES A ESTA NOVA ARESTA
                    System.out.println("aresta equivalente encontrada "+edgesToNode.get(i));
                    System.out.println("setando aresta para aresta fixa");
                    equivalentEdgeFound = true;
                    edgesToNode.get(i).setFixedEdge(true);
                    edgesToNode.get(i).setEquivalentEdge(null);

                    System.out.println("setando todas as arestas que vao para o no como arestas fixas:");
                    ArrayList<Edge> newFixedEdges = getAllEdgesTo(edgesToNode.get(i).getOriginNode());
                    for (int j = 0; j < newFixedEdges.size(); j++)
                    {
                        newFixedEdges.get(i).setFixedEdge(true);
                        newFixedEdges.get(i).setEquivalentEdge(edgesToNode.get(i));
                        System.out.println(newFixedEdges.get(i));
                    }
                }
            }
            //fromThatEdge.destroy();
        }
        if (equivalentEdgeFound == false)
        {
            System.out.println("aresta equivalente nao encontrada");
            for (int i = 0; i < edgesToNode.size(); i++)
            {
                System.out.println("removendo no "+edgesToNode.get(i).getOriginNode());
                removeNodeUntilNoFixedEdge(edgesToNode.get(i).getOriginNode(), null);
            }
            ArrayList<Edge> edgesFromNode = getAllEdgesFrom(node);
            System.out.println("removendo o no "+node.getService().getUri());
            nodes.remove(node);
            //node.destroy();
            System.out.println("para todas as arestas que saem do no acima, remova-as");
            for (int i = 0; i < edgesFromNode.size(); i++)
            {
                Edge edge = edgesFromNode.get(i);
                if (edge.getDestinyNode() != null)
                {
                    System.out.println("removendo o no "+edge.getDestinyNode().getService().getUri());
                    removeNodeUntilNoFixedEdge(edge.getDestinyNode(), edge);
                }
                else
                {
                    this.edges.remove(edge);
                    //edge.destroy();
                }
                this.edges.remove(edge);
            }
        }
    }
}

    public void addEdge(Node originNode, Node destinyNode, URI uri, Edge equivalentEdge, boolean fixedEdge)
    {
        Edge newEdge = new Edge(originNode, destinyNode,uri, fixedEdge, equivalentEdge);
        edges.add(newEdge);
    }

    public ArrayList<Edge> getEdges()
    {
        return edges;
    }

    public boolean thereIsAPath(Node startNode, Node finishNode)
    {
         ArrayList<Node> visitedNodes = new ArrayList<Node>();
        return thereIsEdge(startNode, finishNode, visitedNodes);
    }
    public boolean thereIsEdge(Node startNode, Node finishNode, ArrayList<Node> visitedNodes)
    {
        boolean result = false;
        System.out.printf("buscando caminho de "+startNode+" ate "+finishNode+"\n");
        if (visitedNodes.contains(startNode))
        {
            System.out.printf("no ja visitado\n");
            return false;

        }
        else if(startNode.equals(finishNode))
        {
            System.out.printf("no encontrado\n");
            return true;
        }
        else
            System.out.printf(startNode+" nao eh igual a "+finishNode+"\n");
        visitedNodes.add(startNode);
        ArrayList<Edge> edgesFromNode = getAllEdgesFrom(startNode);
        if (edgesFromNode.isEmpty())
        {
            System.out.printf("sem caminho para seguir\n");
            return false;
        }
        System.out.printf("Existem "+edgesFromNode.size() +" caminhos para seguir\n");
        for (int i = 0; i <edgesFromNode.size(); i++)
        {
            System.out.printf("Seguindo o caminho de "+edgesFromNode.get(i).getDestinyNode()+"\n");
            result = thereIsEdge(edgesFromNode.get(i).getDestinyNode(), finishNode, visitedNodes);
            if (result)
            {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Edge> getAllEdgesFrom(Node originNode)
    {
        ArrayList<Edge> result = new ArrayList<Edge>();
        for (int i = 0; i < edges.size(); i++)
        {
            Edge edge = edges.get(i);
            Node node = edge.getOriginNode();
            if (node != null && node.equals(originNode))
            {
                result.add(edge);
            }
        }
        return result;
    }

    public ArrayList<Edge> getAllEdgesTo(Node DestinyNode)
    {
        ArrayList<Edge> result = new ArrayList<Edge>();
        for (int i = 0; i < edges.size(); i++)
        {
            Edge edge = edges.get(i);
            Node node = edge.getDestinyNode();
            if (node.equals(DestinyNode))
            {
                result.add(edge);
            }
        }
        return result;
    }


    public ArrayList<Node> getNodes()
    {
        return nodes;
    }

    public ArrayList<Node> getForbiddenNodes()
    {
        return forbiddenNodes;
    }

    public void addForbiddenNode(Node node)
    {
        forbiddenNodes.add(node);
    }

    public void print()
    {
        System.out.println("\nPRINTING GRAPH...\n");
        System.out.println("NODES = "+nodes);
        System.out.println("EDGES = "+edges);
        for (int i = 0; i < edges.size(); i++)
        {
            if (edges.get(i).getFixedEdge())
            {
                System.out.print("(");
                if (edges.get(i).getOriginNode() != null)
                {
                    System.out.print(edges.get(i).getOriginNode().getService().getUri()+" , ");
                }
                else
                {
                   System.out.print("NULL , ");
                }
                if (edges.get(i).getDestinyNode() != null)
                {
                    System.out.print(edges.get(i).getDestinyNode().getService().getUri());
                }
                else
                {
                   System.out.print("NULL");
                }
                System.out.println(")");

            }

        }
        
    }

    public void removeUnsed()
    {
        for (int i = 0; i < edges.size(); i++)
        {
            if (!edges.get(i).getFixedEdge())
            {
                System.out.println("removendo "+edges.get(i).getOriginNode().getService().getUri());
                nodes.remove(edges.get(i).getOriginNode());
                edges.remove(i);
            }
        }
    }

    public boolean isForbiddenNode(Node node)
    {
        for (int i = 0; i < forbiddenNodes.size(); i++)
        {
            if (node.getService().getUri().toString().equals(forbiddenNodes.get(i).getService().getUri().toString()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean serviceUsed(Service service)
    {
        for (int i = 0; i < nodes.size(); i++)
        {
            if (nodes.get(i).getService().getUri().toString().equals(service.getUri().toString()))
            {
                return true;
            }
        }
        return false;
    }
}
