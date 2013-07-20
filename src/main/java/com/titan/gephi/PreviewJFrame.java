package com.titan.gephi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.JFrame;

import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
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

public class PreviewJFrame {
	public static void main(String[] args) {
		new PreviewJFrame();
	}

	public PreviewJFrame() {
		//Init a project - and therefore a workspace
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		//Import file
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);
		Container container;
		try {
			File file = new File(getClass().getResource("/com/titan/gephi/Java.gexf").toURI());
			container = importController.importFile(file);
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}

		//Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);

		//Preview configuration
		PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
		PreviewModel previewModel = previewController.getModel();
		previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
		previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
		previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
		previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 10f);
		previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLACK);
		previewModel.getEnabledMouseListeners();
		previewController.refreshPreview();

		//New Processing target, get the PApplet
		ProcessingTarget target = (ProcessingTarget) previewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
		PApplet applet = target.getApplet();
		applet.init();

		//Refresh the preview and reset the zoom
		previewController.render(target);
		target.refresh();
		target.resetZoom();

		//Add the applet to a JFrame and display
		JFrame frame = new JFrame("Test Preview");
		frame.setLayout(new BorderLayout());

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(applet, BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}
}
