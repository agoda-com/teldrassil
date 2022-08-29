package graph.tree.spqr;

import graph.elements.Edge;
import graph.elements.Graph;
import graph.elements.Vertex;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * Node of the SPQR tree
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class SPQRTreeNode<V extends Vertex, E extends Edge<V>> implements Vertex{


	/**
	 * Node type - S,P,Q,R
	 */
	private NodeType nodeType;

	/**
	 * Graph associated with the node
	 */
	private Skeleton<V,E> skeleton;

	/**
	 * Children of the node
	 */
	private List<SPQRTreeNode<V,E>> children;
	
	/**
	 * Construct an empty tree node
	 */
	public SPQRTreeNode() {
		super();
		children = new ArrayList<SPQRTreeNode<V,E>>();
	}

	/**
	 * Constructs a SPQR tree node of the given type
	 * @param nodeType Type of the node
	 */
	public SPQRTreeNode(NodeType nodeType) {
		super();
		this.nodeType = nodeType;
		children = new ArrayList<SPQRTreeNode<V,E>>();
	}


	/**
	 * Construct a SPQR tree node of the given type and sets its skeleton
	 * @param nodeType Type of the node
	 * @param skeleton Skeleton
	 */
	public SPQRTreeNode(NodeType nodeType, Skeleton<V, E> skeleton) {
		this(nodeType);
		this.skeleton = skeleton;
	}

	/**
	 * Construct a SPQR tree node of the given type and sets its skeleton
	 * @param nodeType Type of the node
	 * @param skeleton Graph object which should be associated with the node
	 */
	public SPQRTreeNode(NodeType nodeType, Graph<V, E> skeleton) {
		super();
		this.nodeType = nodeType;
		this.skeleton = new Skeleton<>(skeleton.getVertices(), skeleton.getEdges());
	}

	/**
	 * Adds a node to this node's list of children nodes
	 * @param node Child node to be added
	 */
	public void addChildNode(SPQRTreeNode<V,E> node){
		if (!children.contains(node))
			children.add(node);
	}

	@Override
	public Dimension getSize() {
		return null;
	}

	@Override
	public Object getContent() {
		return null;
	}

	/**
	 * @return The node's type
	 */
	public NodeType getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType Node's type to set
	 */
	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * @return Skeleton
	 */
	public Skeleton<V, E> getSkeleton() {
		return skeleton;
	}

	/**
	 * @param skeleton Skeleton to set
	 */
	public void setSkeleton(Skeleton<V, E> skeleton) {
		this.skeleton = skeleton;
	}

	/**
	 * @return A list of children
	 */
	public List<SPQRTreeNode<V, E>> getChildren() {
		return children;
	}

	/**
	 * @param children Children list to set
	 */
	public void setChildren(List<SPQRTreeNode<V, E>> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "TreeNode [Node type =" + nodeType + ",\n skeleton=" + skeleton +"]";
	}

	@Override
	public void setSize(Dimension size) {
	}

	@Override
	public void setContent(Object content) {
	}



}
