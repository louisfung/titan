package com.c2.pandora.mainframe;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import com.peterswing.CommonLib;

public class TextTreeNode implements ServerMutableTreeNode {
	public String name;
	Vector<ServerMutableTreeNode> children = new Vector<ServerMutableTreeNode>();
	public MutableTreeNode parent;
	boolean visible = true;
	Icon icon;

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public TextTreeNode(String name) {
		this.name = name;
	}

	@Override
	public TreeNode getChildAt(int childIndex) {
		int count = -1;
		for (ServerMutableTreeNode node : children) {
			if (node.isVisible()) {
				count++;
			}
			if (count == childIndex) {
				return node;
			}

		}
		return null;
	}

	@Override
	public int getChildCount() {
		int count = 0;
		for (ServerMutableTreeNode node : children) {
			if (node.isVisible()) {
				count++;
			}
		}
		return count;
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	@Override
	public int getIndex(TreeNode node) {
		for (int x = 0; x < children.size(); x++) {
			if (children.get(x) == node) {
				return x;
			}
		}
		return -1;
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public boolean isLeaf() {
		return children.size() == 0 || visible == false ? true : false;
	}

	@Override
	public Enumeration children() {
		return CommonLib.makeEnumeration(children.toArray());
	}

	@Override
	public void insert(MutableTreeNode child, int index) {
	}

	@Override
	public void remove(int indfileex) {
	}

	@Override
	public void remove(MutableTreeNode node) {
	}

	@Override
	public void setUserObject(Object object) {

	}

	@Override
	public void removeFromParent() {

	}

	@Override
	public void setParent(MutableTreeNode newParent) {

	}

	public String toString() {
		return name;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean b) {
		visible = b;
	}

	@Override
	public Vector<ServerMutableTreeNode> getChildren() {
		return children;
	}

}
