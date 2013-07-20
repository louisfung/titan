package com.titan.sdn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import processing.core.PApplet;

import com.titan.MainPanel;
import com.titan.mainframe.MainFrame;

public class SDNPanel extends JPanel implements MainPanel {
	MainFrame mainframe;
	ProcessingTarget target;
	GraphModel graphModel;

	public SDNPanel(MainFrame mainframe) {
		this.mainframe = mainframe;

		setLayout(new BorderLayout(0, 0));
		//Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		//Append imported data to GraphAPI
		//		importController.process(container, new DefaultProcessor(), workspace);

		//Get a graph model - it exists because we have a workspace
		graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

		//Create three nodes
		Node n0 = graphModel.factory().newNode("n0");
		n0.getNodeData().setLabel("Node 0");
		Node n1 = graphModel.factory().newNode("n1");
		n1.getNodeData().setLabel("Node 1");
		Node n2 = graphModel.factory().newNode("n2");
		n2.getNodeData().setLabel("Node 2");
		Node n3 = graphModel.factory().newNode("n3");
		n3.getNodeData().setLabel("Node 3");
		Node n4 = graphModel.factory().newNode("n4");
		n4.getNodeData().setLabel("Node 4");
		Node n5 = graphModel.factory().newNode("n5");
		n5.getNodeData().setLabel("Node 5");

		//Create three edges
		Edge e1 = graphModel.factory().newEdge(n0, n1, 1f, true);
		Edge e2 = graphModel.factory().newEdge(n1, n2, 1f, true);
		Edge e3 = graphModel.factory().newEdge(n2, n3, 1f, true);
		Edge e4 = graphModel.factory().newEdge(n3, n4, 1f, true);
		Edge e5 = graphModel.factory().newEdge(n4, n5, 1f, true);
		Edge e6 = graphModel.factory().newEdge(n5, n0, 1f, true);

		//Append as a Directed Graph
		DirectedGraph directedGraph = graphModel.getDirectedGraph();
		directedGraph.addNode(n0);
		directedGraph.addNode(n1);
		directedGraph.addNode(n2);
		directedGraph.addNode(n3);
		directedGraph.addNode(n4);
		directedGraph.addNode(n5);
		directedGraph.addEdge(e1);
		directedGraph.addEdge(e2);
		directedGraph.addEdge(e3);
		directedGraph.addEdge(e4);
		directedGraph.addEdge(e5);
		directedGraph.addEdge(e6);

		/*
		//Rank color by Degree
		RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
		Ranking degreeRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
		AbstractColorTransformer colorTransformer = (AbstractColorTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_COLOR);
		colorTransformer.setColors(new Color[] { new Color(0xFEF0D9), new Color(0xB30000) });
		rankingController.transform(degreeRanking, colorTransformer);

		//Get Centrality
		AttributeModel attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
		GraphDistance distance = new GraphDistance();
		distance.setDirected(true);
		distance.execute(graphModel, attributeModel);

		//Rank size by centrality
		AttributeColumn centralityColumn = attributeModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
		Ranking centralityRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, centralityColumn.getId());
		AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.RENDERABLE_SIZE);
		sizeTransformer.setMinSize(3);
		sizeTransformer.setMaxSize(20);
		rankingController.transform(centralityRanking, sizeTransformer);

		//Rank label size - set a multiplier size
		Ranking centralityRanking2 = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, centralityColumn.getId());
		AbstractSizeTransformer labelSizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT, Transformer.LABEL_SIZE);
		labelSizeTransformer.setMinSize(1);
		labelSizeTransformer.setMaxSize(3);
		rankingController.transform(centralityRanking2, labelSizeTransformer);
		*/

		//Preview configuration
		PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
		PreviewModel previewModel = previewController.getModel();
		previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
		previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.BLACK));
		previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 10f);
		previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
		previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.TRUE);
		previewModel.getEnabledMouseListeners();
		previewController.refreshPreview();

		//New Processing target, get the PApplet
		target = (ProcessingTarget) previewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
		PApplet applet = target.getApplet();
		applet.init();

		//Refresh the preview and reset the zoom
		previewController.render(target);

		//Add the applet to a JFrame and display
		add(applet, BorderLayout.CENTER);

		JToolBar toolBar = new JToolBar();
		add(toolBar, BorderLayout.NORTH);

		JButton btnResetZoom = new JButton("Reset zoom");
		btnResetZoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AutoLayout autoLayout = new AutoLayout(2, TimeUnit.SECONDS);
				autoLayout.setGraphModel(graphModel);
				YifanHuLayout firstLayout = new YifanHuLayout(null, new StepDisplacement(1f));
				ForceAtlasLayout secondLayout = new ForceAtlasLayout(null);
				AutoLayout.DynamicProperty adjustBySizeProperty = AutoLayout.createDynamicProperty("forceAtlas.adjustSizes.name", Boolean.TRUE, 0.1f);//True after 10% of layout time
				AutoLayout.DynamicProperty repulsionProperty = AutoLayout.createDynamicProperty("forceAtlas.repulsionStrength.name", new Double(500.), 0f);//500 for the complete period
				autoLayout.addLayout(firstLayout, 0.5f);
				autoLayout.addLayout(secondLayout, 0.5f, new AutoLayout.DynamicProperty[] { adjustBySizeProperty, repulsionProperty });
				autoLayout.execute();

				target.refresh();
				target.resetZoom();
			}
		});
		btnResetZoom.setIcon(new ImageIcon(SDNPanel.class.getResource("/com/titan/image/famfamfam/arrow_inout.png")));
		toolBar.add(btnResetZoom);

		target.refresh();
		target.resetZoom();
	}

	public void refresh() {

	}
}
