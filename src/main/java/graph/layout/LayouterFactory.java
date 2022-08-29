package graph.layout;

import graph.elements.Edge;
import graph.elements.Vertex;
import graph.layout.automatic.AutomaticPropertiesLayout;
import graph.layout.box.BoxLayouter;
import graph.layout.circle.CircleLayouter;
import graph.layout.circle.CircleWithCenterLayouter;
import graph.layout.force.directed.FruchtermanReingoldLayouter;
import graph.layout.force.directed.KamadaKawaiLayouter;
import graph.layout.force.directed.SpringLayouter;
import graph.layout.organic.JGraphFastorganicLayouter;
import graph.layout.organic.JGraphHierarchicalLayouter;
import graph.layout.organic.JGraphOrganicLayouter;
import graph.layout.organic.JungISOMLayouter;
import graph.layout.orthogonal.VisibilityRepresentationLayout;
import graph.layout.straight.line.ConvexLayouter;
import graph.layout.straight.line.TutteLayouter;
import graph.layout.symmetric.SymmetricCircleLayouter;
import graph.layout.tree.JGraphCompactTreeLayout;
import graph.layout.tree.JungTreeLayouter;
import graph.layout.tree.PrefuseBalloonLayouter;
import graph.layout.tree.PrefuseNodeLinkTreeLayouter;
import graph.layout.tree.RadialTreeLayouter;

/**
 * Factory class used to create an instance of the layouter class
 * @author Renata
 * @param <V> The vertex type
 * @param <E> The edge type 
 */
public class LayouterFactory<V extends Vertex, E extends Edge<V>> {
	
	/**
	 * Creates the appropriate layouter instance
	 * @param algorithm Desired algorithm represented by an enum value
	 * @return Layouter
	 */
	public AbstractLayouter<V,E> createLayouter(LayoutAlgorithms algorithm){

		AbstractLayouter<V,E> layouter = null;
		
		if (algorithm == LayoutAlgorithms.AUTOMATIC)
			layouter = new AutomaticPropertiesLayout<V,E>();
		else if (algorithm == LayoutAlgorithms.KAMADA_KAWAI)
			layouter = new KamadaKawaiLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.FRUCHTERMAN_REINGOLD)
			layouter= new FruchtermanReingoldLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.CIRCLE)
			layouter = new CircleLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.SPRING)
			layouter = new SpringLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.RADIAL_TREE)
			layouter = new RadialTreeLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.TREE)
			layouter = new JungTreeLayouter<>();
		else if (algorithm == LayoutAlgorithms.BOX)
			layouter = new BoxLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.TUTTE)
			layouter = new TutteLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.CONCENTRIC)
			layouter = new SymmetricCircleLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.ISOM)
			layouter = new JungISOMLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.COMPACT_TREE)
			layouter = new JGraphCompactTreeLayout<V,E>();
		else if (algorithm == LayoutAlgorithms.FAST_ORGANIC)
			layouter = new JGraphFastorganicLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.ORGANIC)
			layouter = new JGraphOrganicLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.HIERARCHICAL)
			layouter = new JGraphHierarchicalLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.BALLOON)
			layouter = new PrefuseBalloonLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.NODE_LINK_TREE)
			layouter = new PrefuseNodeLinkTreeLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.CONVEX)
			layouter = new ConvexLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.CIRCLE_CENTER)
			layouter = new CircleWithCenterLayouter<V,E>();
		else if (algorithm == LayoutAlgorithms.ORTHOGONAL)
			layouter = new VisibilityRepresentationLayout<V,E>();
			
		return layouter;

	}
	
	
	
	
}
