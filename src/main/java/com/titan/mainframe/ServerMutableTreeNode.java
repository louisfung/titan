package com.titan.mainframe;

import java.util.Vector;

import javax.swing.Icon;
import javax.swing.tree.MutableTreeNode;

public interface ServerMutableTreeNode extends MutableTreeNode {

	public boolean isVisible();

	public void setVisible(boolean b);

	public Vector<ServerMutableTreeNode> getChildren();

	public String toString();

	public Icon getIcon();
}
