package com.titan.serverpanel;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import com.titan.MainPanel;
import com.titan.openstackserver.OpenstackServerFrame;

public class MainServerPanel extends JPanel implements MainPanel {
	JFrame parentFrame;
	public ServerPanel serverPanel;

	public MainServerPanel(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		setBorder(new EmptyBorder(10, 10, 0, 10));
		setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane, BorderLayout.CENTER);

		serverPanel = new ServerPanel(parentFrame);
		ServerPanel.tableServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					OpenstackServerFrame f = new OpenstackServerFrame(MainServerPanel.this.parentFrame);
					f.setVisible(true);
				}
			}
		});
		tabbedPane.addTab("Server", null, serverPanel, null);

		ServerGraphPanel serverGraphPanel = new ServerGraphPanel();
		tabbedPane.addTab("Graph", null, serverGraphPanel, null);
	}

	public void refresh() {

	}

}
