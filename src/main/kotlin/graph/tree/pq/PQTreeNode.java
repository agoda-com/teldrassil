package graph.tree.pq;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import graph.elements.Vertex;

/**
 * Node of the PQ-tree
 * @author Renata
 */
public class PQTreeNode implements Vertex{

	/**
	 * Type of the node - either P or Q or a leaf
	 */
	private PQNodeType type;
	/**
	 * Content of the node. A cut vertex if the type is P or 
	 * a block if the type is Q. Virtual vertex if it is a leaf
	 */
	private Object content;

	/**
	 * Contains all of the children of a node which are 
	 * currently known to be full. Children are added to the
	 * list after they are matched to a template in the second pass
	 * No special order is necessary
	 */
	private List<PQTreeNode> fullChildren;
	
	/**
	 * Contains all of the children of a node which are currently known
	 * to be partial. It can have at most two elements, since otherwise
	 * the node would not match a legal template 
	 */
	private List<PQTreeNode> partialChildren;
	
	/**
	 * Contains all of the children of the node
	 * that are known to be empty
	 */
	private List<PQTreeNode> emptyChildren;

	/**
	 * All children of a node 
	 */
	private List<PQTreeNode> children;
	
	/**
	 * A count of the number of pertinent children currently possessed by the node
	 * The count is initially zero and is incremented by one each time
	 * a child of the node is processed during the bubbling up. During the 
	 * matching pass the count is decremented by one each time a child is matched
	 * The node is queued for matching when the pertinent child count reaches zero
	 * during the second pass 
	 */
	private int pertinendChildCount;
	
	/**
	 * A count of the number of pertinent leaves which are descendants of this node
	 * The field is built up during the second pass as each child of the node is matched
	 * It is the sum of the pertinent leaf counts for all of the pertinent children
	 */
	private int pertinentLeafCount;
	
	/**
	 * The node's label. Indicates if it's full, partial or empty
	 */
	private PQNodeLabel label;
	
	/**
	 * A designation used during the first pass. Every node is initially
	 * unmarked. It is marked queued when it is placed onto the queue when it 
	 * is placed onto queue during the bubbling up. It is marked either blocked or unblockedwhen it is processed
	 * Blocked nodes can become unblocked if their sibling become
	 * unblocked. 
	 */
	private PQNodeMark mark;
	
	
	private Object virtualEdge;
	
	/**
	 * The immediate ancestor of the node. The field os always valid for children
	 * of P-nodes and for endmost children of Q--nodes.
	 * It is only valid for interior children of Q-nodes if the child s marked  as unblocked
	 */
	private PQTreeNode parent;
	
	
	/**
	 * Construct a PQ-tree node of the given type.
	 * @param type Type of the node
	 */
	public PQTreeNode(PQNodeType type){
		super();
		this.type = type;
		mark = PQNodeMark.UNMARKED;
		
		//label leaves as empty
		if (type == PQNodeType.LEAF)
			label = PQNodeLabel.EMPTY;
		
		//initialize structures specific to certain nodes
		if (type != PQNodeType.LEAF){
			children = new ArrayList<PQTreeNode>();
			fullChildren = new ArrayList<PQTreeNode>();
			partialChildren = new ArrayList<PQTreeNode>();
			emptyChildren = new ArrayList<PQTreeNode>();
			
		}
	}
	
	/**
	 * Constructs a PQ-tree node of the given type with the specified content
	 * @param type Type of the node
	 * @param content Node's content
	 */
	public PQTreeNode(PQNodeType type, Object content) {
		this(type);
		this.content = content;
	}
	
	/**
	 * Only used for P-nodes
	 * @return The number of children currently possessed by the node
	 */
	public int childrenCount(){
		return children.size();
	}
	
	/**
	 * Only used for P-nodes
	 * @return The number of full children currently possessed by the node
	 */
	public int fullChildrenCount(){
		return fullChildren.size();
	}
	
	/**
	 * Only used for P-nodes
	 * @return The number of partial children currently possessed by the node
	 */
	public int partialChildrenCount(){
		return partialChildren.size();
	}
	
	/**
	 * Only used for P-nodes
	 * @return The number of empty children currently possessed by the node
	 */
	public int emptyChildrenCount(){
		return emptyChildren.size();
	}
	
	/**
	 * Increases pertinent children count
	 */
	public void incrementPertinentChildCount(){
		pertinendChildCount++;
	}
	
	/**
	 * Decrements pertinent children count
	 */
	public void decrementPertinentChildCount(){
		pertinendChildCount--;
	}

	/**
	 * Adds a child node to the current node 
	 * @param node Child node
	 */
	public void addChild(PQTreeNode node){
		children.add(node);
		if (node.getLabel() == PQNodeLabel.FULL)
			fullChildren.add(node);
		else if (node.getLabel() == PQNodeLabel.SINGLY_PARTIAL)
			partialChildren.add(node);
		else if (node.getLabel() == PQNodeLabel.EMPTY)
			emptyChildren.add(node);
		node.setParent(this);
	}
	
	/**
	 * Checks if the order of the children is empty, singly partial, full
	 * Some labels could be missing
	 * Reversal is possible
	 * @return Indicator if the order is valid
	 */
	public PQNodeOrderValid orderValidUpToOnePartial(){
		
		boolean hasEmpty = emptyChildrenCount() > 0;
		boolean hasPartial = partialChildrenCount() > 0;
		boolean hasFull = fullChildrenCount() > 0;
		boolean reversed = false;
		
		if (children.get(0).getLabel() == PQNodeLabel.FULL && (hasEmpty || hasPartial)){
			reversed = true;
			Collections.reverse(children);
		}
		else if (children.get(0).getLabel() == PQNodeLabel.SINGLY_PARTIAL && hasEmpty){
			reversed = true;
			Collections.reverse(children);
		}
		
		if (hasEmpty && children.get(0).getLabel() != PQNodeLabel.EMPTY)
			return PQNodeOrderValid.INVALID;
		else if (!hasEmpty && hasPartial && children.get(0).getLabel() != PQNodeLabel.SINGLY_PARTIAL)
			return PQNodeOrderValid.INVALID;
		
		
		boolean emptyFinished = false;
		boolean partialFinished = false;
		
		if (!hasEmpty)
			emptyFinished = true;
		if (!hasPartial)
			partialFinished = true;
		
		for (PQTreeNode child : children){
			PQNodeLabel label = child.getLabel();
			
			//first few should be empty
			//the first time it is not is detected
			//empty should be covered by singly partial
			//if there is such child
			if (!emptyFinished && hasEmpty && label != PQNodeLabel.EMPTY){
				if (hasPartial && label != PQNodeLabel.SINGLY_PARTIAL){
					return PQNodeOrderValid.INVALID;
				}
				emptyFinished = true;
			}
			//another empty encountered later, not valid
			else if (emptyFinished && label == PQNodeLabel.EMPTY){
				return PQNodeOrderValid.INVALID;
			}
			//partial after empty, the first time when the child is not partial
			else if (emptyFinished && !partialFinished && hasPartial && label != PQNodeLabel.SINGLY_PARTIAL){
				if (hasFull && label != PQNodeLabel.FULL){
					return PQNodeOrderValid.INVALID;
				}
				partialFinished = true;
			}
			else if (partialFinished && label == PQNodeLabel.SINGLY_PARTIAL){
				return PQNodeOrderValid.INVALID;
			}
			//the others should be full
			//the case when one of them is not should be detected by the previous ifs
		}
		
		if (!reversed)
			return PQNodeOrderValid.VALID;
		return PQNodeOrderValid.REVERSAL;
	}
	
	 /**Checks if the order of the children is empty, singly partial, full, empty partial, empty
	 * Some labels could be missing
	 * Reversal is possible
	 * @return Indicator if the order is valid
	 */
	public PQNodeOrderValid orderValidUpToTwoPartial(){
		
		boolean hasEmpty = emptyChildrenCount() > 0;
		
		if (children.get(0).getLabel() == PQNodeLabel.FULL)
			return PQNodeOrderValid.INVALID;
		
		int childrenNum = childrenCount();
		if (children.get(childrenNum - 1).getLabel() == PQNodeLabel.FULL)
			return PQNodeOrderValid.INVALID;
		
		//check outer first
		int firstEmpty = 0;
		int lastEmpty = childrenNum - 1;
		
		if (hasEmpty){
			int emptyNum = 0;
			if (children.get(0).getLabel() == PQNodeLabel.EMPTY)
				for (PQTreeNode child : children){
					if (child.getLabel() == PQNodeLabel.EMPTY)
						emptyNum++;
					else
						break;
				}
			firstEmpty = emptyNum;
			if (children.get(childrenNum - 1).getLabel() == PQNodeLabel.EMPTY){
				for (int i = childrenNum - 1; i >= 0; i--){
					PQTreeNode child = children.get(i);
					if (child.getLabel() == PQNodeLabel.EMPTY)
						emptyNum++;
					else{
						lastEmpty = i;
						break;
					}
				}
			}
				
			//check calculate empty number
			//if it is not equal to the number of all
			//empty children, there must be some in the middle
			//not valid
			if (emptyNum != emptyChildrenCount())
				return PQNodeOrderValid.INVALID;
		}
		
		//now check the ones after the empty nodes
		if (partialChildrenCount() == 2){
			//both ends should have partial nodes
			if (children.get(firstEmpty).getLabel() != PQNodeLabel.SINGLY_PARTIAL || 
					children.get(lastEmpty).getLabel() != PQNodeLabel.SINGLY_PARTIAL)
				return PQNodeOrderValid.INVALID;
		}
		
		else if (partialChildrenCount() == 1){
			if (children.get(firstEmpty).getLabel() != PQNodeLabel.SINGLY_PARTIAL &&
					children.get(lastEmpty).getLabel() != PQNodeLabel.SINGLY_PARTIAL)
				return PQNodeOrderValid.INVALID;
		}
		
		//the others should now be full
		//no need to check
		return PQNodeOrderValid.VALID;
		
		
	}
	
	/**
	 * Adds child node to the current node at the specified index
	 * @param node Child node
	 * @param index Index where the node should be added
	 */
	public void addChild(PQTreeNode node, int index){
		children.add(index, node);
		if (node.getLabel() == PQNodeLabel.FULL)
			fullChildren.add(node);
		else if (node.getLabel() == PQNodeLabel.SINGLY_PARTIAL)
			partialChildren.add(node);
		else if (node.getLabel() == PQNodeLabel.EMPTY)
			emptyChildren.add(node);
		node.setParent(this);
	}

	/**
	 * Removes a child node from the current one
	 * @param node Child node
	 */
	public void removeChild(PQTreeNode node){
		children.remove(node);
		if (node.getLabel() == PQNodeLabel.FULL)
			fullChildren.remove(node);
		else if (node.getLabel() == PQNodeLabel.SINGLY_PARTIAL)
			partialChildren.remove(node);
		else if (node.getLabel() == PQNodeLabel.EMPTY)
			emptyChildren.remove(node);
		node.setParent(null);
	}
	
	/**
	 * Changes the label to full
	 * while updating the lists of the 
	 * empty, partial and full children
	 */
	public void labelAsFull(){
		PQNodeLabel oldLabel = label;
		label = PQNodeLabel.FULL;
		if (parent != null){
			parent.getFullChildren().add(this);
			if (oldLabel == PQNodeLabel.SINGLY_PARTIAL)
				parent.getPartialChildren().remove(this);
			else if (oldLabel == PQNodeLabel.EMPTY)
				parent.getEmptyChildren().remove(this);
		}
	}
	
	/**
	 * Changes the label to empty
	 * while updating the lists of the 
	 * empty, partial and full children
	 */
	public void labelAsEmpty(){
		PQNodeLabel oldLabel = label;
		label = PQNodeLabel.EMPTY;
		if (parent != null){
			parent.getEmptyChildren().add(this);
			if (oldLabel == PQNodeLabel.SINGLY_PARTIAL)
				parent.getPartialChildren().remove(this);
			else if (oldLabel == PQNodeLabel.FULL)
				parent.getFullChildren().remove(this);
		}
	}
	
	/**
	 * Changes the label to partial
	 * while updating the lists of the 
	 * empty, partial and full children
	 * @param label Singly partial or doubly partial
	 */
	public void labelAsPartial(PQNodeLabel label){
		PQNodeLabel oldLabel = this.label;
		this.label = label;
		if (parent != null){
			if (oldLabel != PQNodeLabel.SINGLY_PARTIAL && oldLabel != PQNodeLabel.DOUBLY_PARTIAL)
				parent.getPartialChildren().add(this);
			if (oldLabel == PQNodeLabel.EMPTY)
				parent.getEmptyChildren().remove(this);
			else if (oldLabel == PQNodeLabel.FULL)
				parent.getFullChildren().remove(this);
		}
	}

	/**
	 * @return Node's type
	 */
	public PQNodeType getType() {
		return type;
	}

	/**
	 * @param type Node's type to set
	 */
	public void setType(PQNodeType type) {
		this.type = type;
	}

	/**
	 * @return The node's content
	 */
	public Object getContent() {
		return content;
	}

	/**
	 * @param content Node's content to set
	 */
	public void setContent(Object content) {
		this.content = content;
	}

	@Override
	public Dimension getSize() {
		return null;
	}

	@Override
	public void setSize(Dimension size) {
	}

	@Override
	public String toString() {
		
		if (type == PQNodeType.LEAF)
			return "LEAF " + virtualEdge;
		
		return type + " node =" + content;// + parent == null ? " root " : "parent = " + parent.getContent();  
		
	}

	/**
	 * @return Node's children
	 */
	public List<PQTreeNode> getChildren() {
		return children;
	}

	/**
	 * @param children Children nodes to set
	 */
	public void setChildren(List<PQTreeNode> children) {
		this.children = children;
	}

	/**
	 * @return The node's parent
	 */
	public PQTreeNode getParent() {
		return parent;
	}

	/**
	 * @param parent Parent to set
	 */
	public void setParent(PQTreeNode parent) {
		this.parent = parent;
	}

	/**
	 * @return A list of full children
	 */
	public List<PQTreeNode> getFullChildren() {
		return fullChildren;
	}

	/**
	 * @return Node's label
	 */
	public PQNodeLabel getLabel() {
		return label;
	}

	/**
	 * @param label Label to set
	 */
	public void setLabel(PQNodeLabel label) {
		this.label = label;
	}

	/**
	 * @return Node's mark
	 */
	public PQNodeMark getMark() {
		return mark;
	}

	/**
	 * @param mark Mark to set
	 */
	public void setMark(PQNodeMark mark) {
		this.mark = mark;
	}

	/**
	 * @return A list of partial children
	 */
	public List<PQTreeNode> getPartialChildren() {
		return partialChildren;
	}

	/**
	 * @return The number of pertinent children
	 */
	public int getPertinendChildCount() {
		return pertinendChildCount;
	}

	/**
	 * @param pertinendChildCount The number of pertinent children
	 */
	public void setPertinendChildCount(int pertinendChildCount) {
		this.pertinendChildCount = pertinendChildCount;
	}

	/**
	 * @return The number of pertinent leaves
	 */
	public int getPertinentLeafCount() {
		return pertinentLeafCount;
	}

	/**
	 * @param pertinentLeafCount The number of pertinent leaves to set
	 */
	public void setPertinentLeafCount(int pertinentLeafCount) {
		this.pertinentLeafCount = pertinentLeafCount;
	}

	/**
	 * @return the emptyChildren A list of empty children of the node
	 */
	public List<PQTreeNode> getEmptyChildren() {
		return emptyChildren;
	}

	/**
	 * @return The virtual edge
	 */
	public Object getVirtualEdge() {
		return virtualEdge;
	}

	/**
	 * @param virtualEdge The virtual edge to set
	 */
	public void setVirtualEdge(Object virtualEdge) {
		this.virtualEdge = virtualEdge;
	}

}
