package com.titan.serverpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxOrganicLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxPoint;
import com.peterswing.GenericTableModel;

public class ServerGraphPanel extends JPanel {
	ServerGraph graph = new ServerGraph();
	ServerGraphUIComponent serverUIComponent = new ServerGraphUIComponent(graph);
	int scale = 100;
	private JTable propertyTable;
	GenericTableModel peopertyTableModel = new GenericTableModel();
	private JSplitPane splitPane;

	/**
	 * Create the panel.
	 */
	public ServerGraphPanel() {
		setLayout(new BorderLayout());

		Object parent = graph.getDefaultParent();

		graph.getModel().beginUpdate();
		try {
			int x = 100;
			mxCell internetNode = (mxCell) graph.insertVertex(parent, null, "internet:", x, 100, 100, 100);
			mxCell ports1[] = addPort(internetNode);
			mxCell firewall = (mxCell) graph.insertVertex(parent, null, "firewall:", x += 100, 100, 100, 100);
			mxCell ports2[] = addPort(firewall);
			mxCell nas = (mxCell) graph.insertVertex(parent, null, "nas:", x += 100, 100, 100, 100);
			mxCell ports3[] = addPort(nas);
			mxCell titan = (mxCell) graph.insertVertex(parent, null, "titan:", x += 100, 100, 100, 100);
			mxCell ports4[] = addPort(titan);
			//			mxCell router = (mxCell) graph.insertVertex(parent, null, "router:", x += 100, 100, 100,100);
			//			mxCell ports5[] = addPort(router);
			mxCell server = (mxCell) graph.insertVertex(parent, null, "server:", x += 100, 100, 100, 100);
			mxCell ports6[] = addPort(server);
			mxCell switchNode = (mxCell) graph.insertVertex(parent, null, "switch:", x += 100, 100, 150, 150);
			mxCell ports7[] = addPort(switchNode);

			//graph.insertEdge(parent, null, "10GB", ports1[1], ports2[0], "edgeStyle=entityRelationEdgeStyle;");
			graph.insertEdge(parent, null, "10GB", ports1[1], ports2[0], "edgeStyle=elbowEdgeStyle;");
			graph.insertEdge(parent, null, "", ports2[1], ports7[0], "edgeStyle=elbowEdgeStyle;");
			graph.insertEdge(parent, null, "", ports7[1], ports4[0], "edgeStyle=elbowEdgeStyle;");
			graph.insertEdge(parent, null, "", ports4[1], ports6[0], "edgeStyle=elbowEdgeStyle;");
			graph.insertEdge(parent, null, "", ports6[1], ports3[0], "edgeStyle=elbowEdgeStyle;");

		} finally {
			graph.getModel().endUpdate();
		}

		graph.setCellsDisconnectable(false);

		graph.setCellsResizable(false);
		graph.setCellsMovable(true);
		graph.setCellsEditable(false);
		graph.foldCells(false);
		graph.setGridSize(10);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, BorderLayout.SOUTH);

		JButton btnZoomIn = new JButton("Zoom in");
		btnZoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scale += 20;
				serverUIComponent.zoomTo((double) scale / 100, true);
			}
		});
		panel.add(btnZoomIn);

		JButton btnZoomOut = new JButton("Zoom out");
		btnZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scale -= 20;
				serverUIComponent.zoomTo((double) scale / 100, true);
			}
		});
		panel.add(btnZoomOut);
		peopertyTableModel.columnNames.add("Key");
		peopertyTableModel.columnNames.add("Value");
		Vector<Object> keys = new Vector<Object>();
		Vector<Object> values = new Vector<Object>();
		keys.add("preferIPv4Stack");
		values.add("10.0.0.21/23");
		keys.add("IPv6Addresses");
		values.add("345AASD213SKLJDHIU24908AS");
		keys.add("networkaddress.cache.ttl ");
		values.add("10 secs");
		keys.add("networkaddress.cache.negative.ttl");
		values.add("2 secs");
		keys.add("proxyHost");
		values.add("192.168.0.1");
		keys.add("proxyPort");
		values.add("80");
		keys.add("nonProxyHosts");
		values.add("8.8.8.8");
		peopertyTableModel.values.add(keys);
		peopertyTableModel.values.add(values);
		peopertyTableModel.fireTableStructureChanged();
		peopertyTableModel.fireTableDataChanged();

		JPanel panel_2 = new JPanel();
		add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		splitPane = new JSplitPane();
		//		splitPane.setDividerLocation(getWidth() - 200);
		panel_2.add(splitPane, BorderLayout.CENTER);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setPreferredSize(new Dimension(200, 10));
		splitPane.setRightComponent(tabbedPane);

		JPanel propertyPanel = new JPanel();
		tabbedPane.addTab("Network", null, propertyPanel, null);
		propertyPanel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		propertyPanel.add(scrollPane, BorderLayout.CENTER);

		propertyTable = new JTable();
		propertyTable.getTableHeader().setReorderingAllowed(false);
		propertyTable.setModel(peopertyTableModel);
		scrollPane.setViewportView(propertyTable);
		splitPane.setLeftComponent(serverUIComponent);

		serverUIComponent.setGridVisible(true);
		serverUIComponent.setGridColor(Color.lightGray);
		serverUIComponent.setBackground(Color.white);
		serverUIComponent.getViewport().setOpaque(false);
		serverUIComponent.setBackground(Color.WHITE);
		serverUIComponent.setConnectable(false);
		serverUIComponent.getGraphControl().addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				Object cell = serverUIComponent.getCellAt(e.getX(), e.getY());

				if (cell != null) {
					String label = graph.getLabel(cell);
					System.out.println(label);
					if (label.contains("->")) {
						//						cellClientEvent(label);
					}
				}
			}
		});

		JToolBar toolBar = new JToolBar();
		serverUIComponent.setColumnHeaderView(toolBar);

		JButton btnCL = new JButton("C. L");
		btnCL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mxCircleLayout layout = new mxCircleLayout(graph);
					Object cell = serverUIComponent.getGraph().getDefaultParent();
					layout.execute(cell);
				} catch (Exception ex) {
				}
			}
		});
		toolBar.add(btnCL);

		JButton btnOrganic = new JButton("Organic");
		btnOrganic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mxOrganicLayout layout = new mxOrganicLayout(graph);
					Object cell = serverUIComponent.getGraph().getDefaultParent();
					layout.execute(cell);
				} catch (Exception ex) {
				}
			}
		});
		toolBar.add(btnOrganic);

		JButton btnHierarchical = new JButton("Hierarchical");
		btnHierarchical.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Object cell = graph.getSelectionCell();

					if (cell == null || graph.getModel().getChildCount(cell) == 0) {
						cell = graph.getDefaultParent();
					}
					graph.getModel().beginUpdate();

					mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
					layout.execute(cell);

					mxMorphing morph = new mxMorphing(serverUIComponent, 20, 1.2, 20);
					morph.addListener(mxEvent.DONE, new mxIEventListener() {
						public void invoke(Object sender, mxEventObject evt) {
							graph.getModel().endUpdate();
						}
					});

					morph.startAnimation();
				} catch (Exception ex) {
				}
			}
		});
		toolBar.add(btnHierarchical);
	}

	private mxCell[] addPort(mxCell node) {
		final int PORT_DIAMETER = 0;
		final int PORT_RADIUS = PORT_DIAMETER / 2;

		mxGeometry geo1 = new mxGeometry(0, 0.5, PORT_DIAMETER, PORT_DIAMETER);
		geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
		geo1.setRelative(true);

		mxCell port1 = new mxCell(null, geo1, "shape=ellipse;perimter=ellipsePerimeter");
		port1.setVertex(true);
		graph.addCell(port1, node);

		mxGeometry geo2 = new mxGeometry(1, 0.5, PORT_DIAMETER, PORT_DIAMETER);
		geo2.setOffset(new mxPoint(-PORT_RADIUS, +PORT_RADIUS));
		geo2.setRelative(true);

		mxCell port2 = new mxCell(null, geo2, "shape=ellipse;perimter=ellipsePerimeter");
		port2.setVertex(true);
		graph.addCell(port2, node);
		return new mxCell[] { port1, port2 };
	}
}
