package com.titan.mainframe;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.tree.TreeCellRenderer;

public class ServerTreeRenderer extends JLabel implements TreeCellRenderer {
	LineBorder border = new LineBorder(new Color(100, 188, 255));

	public ServerTreeRenderer() {
		this.setOpaque(true);
	}

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

//		this.setPreferredSize(new Dimension(100, 20));
		return this;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		FontMetrics fm = getFontMetrics(getFont());
		char[] chars = getText().toCharArray();

		int w = getIconTextGap() + 40;
		for (char ch : chars) {
			w += fm.charWidth(ch);
		}
		w += getText().length();
		dim.width = w;
		return dim;
	}

}
