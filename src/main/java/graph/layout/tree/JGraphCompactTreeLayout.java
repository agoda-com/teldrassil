package graph.layout.tree;

import com.mxgraph.layout.mxCompactTreeLayout;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.AbstractJGraphXLayouter;
import graph.layout.GraphLayoutProperties;
import graph.layout.PropertyEnums.CompactTreeProperties;

/**
 * A layouter which creates a drawing of a graph using JGraphX's compact tree algorithm
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class JGraphCompactTreeLayout<V extends Vertex, E extends Edge<V>> extends AbstractJGraphXLayouter<V, E> {

	public JGraphCompactTreeLayout(){
		oneGraph = true;
	}
	
	@Override
	protected void initLayouter(GraphLayoutProperties layoutProperties) {
		
		Object horizontal = layoutProperties.getProperty(CompactTreeProperties.HORIZONTAL);
		Object invert = layoutProperties.getProperty(CompactTreeProperties.INVERT);
		Object levelDistance = layoutProperties.getProperty(CompactTreeProperties.LEVEL_DISTANCE);
		Object resizeParents = layoutProperties.getProperty(CompactTreeProperties.RESIZE_PARENTS);
		Object nodeDistance = layoutProperties.getProperty(CompactTreeProperties.NODE_DISTANCE);
		
		mxCompactTreeLayout treeLayout = new mxCompactTreeLayout(jGraphXGraph);
		if (horizontal != null)
			treeLayout.setHorizontal((boolean)horizontal);
		else
			treeLayout.setHorizontal(false);
		if (invert != null)
			treeLayout.setInvert((boolean) invert);
		else
			treeLayout.setInvert(false);
		if (levelDistance != null)
			treeLayout.setLevelDistance((int)levelDistance);
		if (resizeParents != null)
			treeLayout.setResizeParent((boolean)resizeParents);
		if (nodeDistance != null)
			treeLayout.setNodeDistance((int)nodeDistance);
		
		layouter = treeLayout;
		
	}
	

}
