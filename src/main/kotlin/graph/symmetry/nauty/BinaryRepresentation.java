package graph.symmetry.nauty;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.util.List;

/**
 * Binary representation of a graph, used in graph labeling algorithm 
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class BinaryRepresentation<V extends Vertex, E extends Edge<V>> {

	private Graph<V,E> graph;
	
	public BinaryRepresentation(Graph<V,E> graph){
		this.graph = graph;
	}

	/**
	 * Finds the binary representation of the graph given a list of its vertices
	 * @param verticeList A list of graph's vertices
	 * @return Binary representation
	 */
	public String binaryRepresenatation(List<V> verticeList){
		String representation = "";
		for (int i = 0; i < verticeList.size(); i++){
			V v1 = verticeList.get(i);
			for (int j = i + 1; j < verticeList.size(); j++){
				V v2 = verticeList.get(j);
				if (graph.edgeBetween(v1, v2) != null)
					representation += "1";
				else
					representation += "0";
			}
		}
		return representation;
	}
}
