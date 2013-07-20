package com.titan.serverpanel;

import java.awt.Font;

import javax.swing.CellRendererPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxCellState;
import com.titan.AutoFitLabel;

public class PeterSwingCanvas extends mxInteractiveCanvas {
	protected CellRendererPane rendererPane = new CellRendererPane();
	AutoFitLabel iconLabel = new AutoFitLabel();
	JLabel textLabel = new JLabel();
	mxGraphComponent graphComponent;
	ImageIcon firewall = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/servericons/firewall.png"));
	ImageIcon internet = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/servericons/internet.png"));
	ImageIcon nas = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/servericons/nas.png"));
	ImageIcon router = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/servericons/router.png"));
	ImageIcon server = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/servericons/server.png"));
	ImageIcon switchImg = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/servericons/switch.png"));
	ImageIcon titan = new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/servericons/titan.png"));

	//	Color borderColor = new Color(0, 0, 255);
	//	Color backgroundcolor = new Color(0, 0, 255);

	public PeterSwingCanvas(mxGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
		//		jLabel.setBorder(BorderFactory.createLineBorder(borderColor));
		iconLabel.setHorizontalAlignment(JLabel.CENTER);
		//		jLabel.setBackground(backgroundcolor);
		iconLabel.setOpaque(false);
		textLabel.setFont(new Font("arial", Font.PLAIN, 10));
		textLabel.setHorizontalAlignment(JLabel.CENTER);
	}

	public void drawVertex(mxCellState state, String label) {
		if (label.startsWith("internet:")) {
			iconLabel.setIcon(internet);
		} else if (label.startsWith("switch:")) {
			iconLabel.setIcon(switchImg);
		} else if (label.startsWith("server:")) {
			iconLabel.setIcon(server);
		} else if (label.startsWith("nas:")) {
			iconLabel.setIcon(nas);
		} else if (label.startsWith("titan:")) {
			iconLabel.setIcon(titan);
		} else if (label.startsWith("firewall:")) {
			iconLabel.setIcon(firewall);
		} else if (label.startsWith("router:")) {
			iconLabel.setIcon(router);
		} else {
			iconLabel.setIcon(null);
		}
		textLabel.setText(label);
		rendererPane.paintComponent(g, iconLabel, graphComponent, (int) state.getX() + translate.x, (int) state.getY() + translate.y, (int) state.getWidth(),
				(int) state.getHeight() - 40, true);

		rendererPane.paintComponent(g, textLabel, graphComponent, (int) state.getX() + translate.x, (int) state.getY() + translate.y + (int) state.getHeight() - 40,
				(int) state.getWidth(), 40, true);
	}

}