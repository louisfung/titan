package com.c2.pandora.mainframe;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.tree.TreeCellRenderer;

public class ServerTreeRenderer extends JLabel implements TreeCellRenderer {
	LineBorder border = new LineBorder(new Color(100, 188, 255));

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		ServerMutableTreeNode node = (ServerMutableTreeNode) value;
		this.setIcon(node.getIcon());
		this.setText(node.toString());
		if (sel) {
			this.setBorder(border);
			this.setBackground(UIManager.getColor("Tree.selectionBackground"));
		} else {
			this.setBorder(null);
			this.setBackground(UIManager.getColor("Tree.background"));
		}
		this.setOpaque(true);
		return this;
	}

}
