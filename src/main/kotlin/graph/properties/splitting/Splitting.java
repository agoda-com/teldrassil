package graph.properties.splitting;

import java.util.ArrayList;
import java.util.List;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;
import graph.operations.GraphOperations;
import graph.properties.components.SplitComponent;
import graph.properties.components.SplitPair;

/**
 * A class containing methods regarding graph splitting, such as finding cut vertices
 * Some methods need to be rewritten due to being slow
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class Splitting<V extends Vertex, E extends Edge<V>> {

	/**
	 * Finds a list of graph's cut vertices
	 * A cut vertex is a vertex whose removal would disconnect the remaining graph
	 * @param graph The graph
	 * @return A list of cut vertices
	 */
	public List<V> findAllCutVertices(Graph<V,E> graph){
		return graph.listCutVertices();
	}


	/**
	 * Finds graph's split pairs
	 * A pair {u,v} of vertices is a split pair if it is an edge in the graph
	 * or if it is a separation pair (it increases the number of connected components)
	 * in the graph (graph is no longer connected if it is removed)
	 * @deprecated
	 * @param graph The graph
	 * @return A list of split pairs
	 */
	public List<SplitPair<V,E>> findAllSplitPairs(Graph<V,E> graph){

		List<SplitPair<V,E>> ret = new ArrayList<SplitPair<V,E>>();
		List<V> excluding = new ArrayList<V>();

		List<V> vertices =  graph.getVertices();

		for (int i = 0; i <vertices.size(); i++)
			for (int j = i+1; j < vertices.size(); j++){
				V v1 = vertices.get(i);
				V v2 = vertices.get(j);
				if (graph.hasEdge(v1, v2))
					ret.add(new SplitPair<V,E>(v1, v2));
				else{
					excluding.clear();
					excluding.add(v1);
					excluding.add(v2);
					if (!graph.isConnected(excluding)){
						ret.add(new SplitPair<V,E>(v1, v2));
					}
				}
			}

		return ret;
	}


	@SuppressWarnings("unchecked")
	/**
	 * Finds all split components of a split pair
	 * @param graph Graph
	 * @param splitPair Split pair
	 * @return List of all split components of a pair
	 */
	public List<SplitComponent<V, E>> findAllSplitComponents(Graph<V,E> graph, SplitPair<V, E> splitPair){

		
		List<E> coveredEdges = new ArrayList<E>();
		List<SplitComponent<V, E>> ret = new ArrayList<SplitComponent<V,E>>();
		V u = splitPair.getU();
		V v = splitPair.getV();
		//add edge

		List<E> edges =  graph.edgeesBetween(u, v);
		
		for (E e : edges){ 
			SplitComponent<V, E> component = new SplitComponent<>(splitPair, graph);
			component.addVertex(v);
			component.addVertex(u);
			component.addEdge(e);
			coveredEdges.add(e);
			ret.add(component);
		}

		for (E e : graph.allEdges(u)){
			if (coveredEdges.contains(e))
				continue;
			SplitComponent<V, E> component = new SplitComponent<>(splitPair, graph);
			
			coveredEdges.add(e);
			component.addVertex(u);
			component.addEdge(e);
			V other = e.getDestination() == u ? e.getOrigin() : e.getDestination();
			if (other == v) //just add split pair vertices and the edge{
				continue; 

			else{
				formSplitComponent(u, v, other, coveredEdges, new ArrayList<V>(), component, graph);
			}
			ret.add(component);
		}
		

		return ret;

	}

	@SuppressWarnings("unchecked")
	private void formSplitComponent(V u, V  v, V current, List<E> coveredEdges, List<V> coveredVertices,
			SplitComponent<V, E> component, Graph<V,E> graph){

		if (coveredVertices.contains(current))
			return;
		coveredVertices.add(current);

		component.addVertex(current);
		for (E e : graph.allEdges(current)){
			if (coveredEdges.contains(e))
				continue;

			coveredEdges.add(e);
			component.addEdge(e);
			V other = e.getDestination() == current ? e.getOrigin() : e.getDestination();
			if (other != u && other != v){
				formSplitComponent(u, v, other, coveredEdges, coveredVertices, component, graph);
			}
			else
				component.addVertex(other);
		}

	}

	/**
	 * All components should have two vertices in common: split pair vertices
	 * and no edges
	 * @param components Split components
	 * @param splitPair Split pair
	 * @return {@code true} if the test shows that everything is in order, {@code false} otherwise
	 */
	public boolean testSplitComponents(List<SplitComponent<V, E>> components, SplitPair<V,E> splitPair){
		GraphOperations<V, E> operations = new GraphOperations<>();
		for (int i = 0; i <components.size(); i++)
			for (int j = i+1; j < components.size(); j++){
				SplitComponent<V,E> com1 = components.get(i);
				SplitComponent<V, E> com2 = components.get(j);
				List<V> verticesInCommon = operations.verticesInCommon(com1, com2);
				if (!verticesInCommon.contains(splitPair.getU())  || 
						! verticesInCommon.contains(splitPair.getV()) || verticesInCommon.size() != 2)
					return false;
				List<E> edgesInCommon = operations.edgesInCommon(com1, com2);
				if (edgesInCommon.size() > 0)
					return false;

			}

		return true;
	}

	/**
	 * A split graph of a split pair with respect of some edge 
	 * is the union of all split components which don't contain that edge
	 * @param splitComponents All split components
	 * @param edge Edge
	 * @return Split graph
	 */
	public Graph<V,E> splitGraph(List<SplitComponent<V, E>> splitComponents, E edge){

		List<Graph<V, E>> allComponentsNotContainingEdge = new ArrayList<Graph<V,E>>();
		for (SplitComponent<V, E> component : splitComponents)
			if (!component.getEdges().contains(edge))
				allComponentsNotContainingEdge.add(component);


		GraphOperations<V, E> operations = new GraphOperations<>();
		return operations.union(allComponentsNotContainingEdge);

	}

	/** 
	 * Finds a split graph with respect to the given split pair and edge
	 * @param splitPair Split pair
	 * @param edge Edge
	 * @param graph Graph
	 * @return Split graph
	 */
	public Graph<V,E> splitGraph(SplitPair<V,E> splitPair, E edge, Graph<V,E> graph){
		return splitGraph(findAllSplitComponents(graph, splitPair), edge);
	}

	/**
	 * Checks if one split pair is dominated by another given an edge
	 * A split pair {u,v} is dominated by another split pair {x,y} if
	 * @param graph The graph
	 * @param dominanted Potentially dominated split pair
	 * @param dominant Potentially dominant split pair
	 * @param edge Edge
	 * @return {@code true} if {@code dominated} is dominated by @{code other}  
	 */
	public boolean splitPairIsDominantedBy(Graph<V,E> graph, SplitPair<V,E> dominanted, SplitPair<V, E> dominant, E edge){
		GraphOperations<V, E> operations = new GraphOperations<>();

		Graph<V,E> splitGraph1 = splitGraph(findAllSplitComponents(graph, dominanted), edge);
		Graph<V,E> splitGraph2 = splitGraph(findAllSplitComponents(graph, dominant), edge);
		
		return operations.isProperSubgraph(splitGraph2, splitGraph1);

	}

	/**
	 * Finds a list of maximal split pair with respect to some edge
	 * A maximal split pair with respect to some edge 
	 * is a split pair not dominated by any other split pair with respect to that edge
	 * There may several such pairs
	 * @param graph The graph
	 * @param edge The edge with respect to which the pairs are being examined
	 * @return A list of maximal split pairs
	 */
	public List<SplitPair<V, E>> maximalSplitPairs(Graph<V,E> graph, E edge){
		List<SplitPair<V,E>> ret = new ArrayList<SplitPair<V,E>>();

		List<SplitPair<V,E>> splitPairs =  findAllSplitPairs(graph);

		SplitPair<V, E> edgeSplit = new SplitPair<V,E>(edge.getOrigin(), edge.getDestination());
		for (SplitPair<V, E> splitPair1 : splitPairs){
			if (splitPair1.equals(edgeSplit))
				continue;
			boolean maximal = true;
			for (SplitPair<V, E> splitPair2 : splitPairs){
				if (splitPair2.equals(edgeSplit))
					continue;
				if (splitPair1 == splitPair2)
					continue;
				if (splitPairIsDominantedBy(graph, splitPair1, splitPair2, edge)){
					maximal = false;
					break;
				}
			}
			if (maximal)
				ret.add(splitPair1);
		}
		return ret;
	}

}
