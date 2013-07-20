package com.c2.pandora.serverpanel;

import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class ServerGraph extends mxGraph {

	public void drawState(mxICanvas canvas, mxCellState state, boolean drawLabel) {
		String label = (drawLabel) ? state.getLabel() : "";
		if (getModel().isVertex(state.getCell()) && canvas instanceof PeterSwingCanvas) {
			PeterSwingCanvas c = (PeterSwingCanvas) canvas;
			c.drawVertex(state, label);
		} else {
			super.drawState(canvas, state, true);
		}
	}

	//	public boolean isPort(Object cell) {
	//		mxGeometry geo = getCellGeometry(cell);
	//
	//		return (geo != null) ? geo.isRelative() : false;
	//	}

	//	public String getToolTipForCell(Object cell) {
	//		//		if (model.isEdge(cell)) {
	//		//			return convertValueToString(model.getTerminal(cell, true)) + " -> " + convertValueToString(model.getTerminal(cell, false));
	//		//		}
	//		//
	//		//		return super.getToolTipForCell(cell);
	//		return "hey";
	//	}

	//	public boolean isCellFoldable(Object cell, boolean collapse) {
	//		return false;
	//	}
}
