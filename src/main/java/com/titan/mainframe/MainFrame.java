package com.c2.pandora.mainframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.IOUtils;

import com.c2.pandora.AddServerDialog;
import com.c2.pandora.LicenseDialog;
import com.c2.pandora.MainPanel;
import com.c2.pandora.Pandora;
import com.c2.pandora.PandoraSetting;
import com.c2.pandora.WelcomePanel;
import com.c2.pandora.flavorpanel.FlavorPanel;
import com.c2.pandora.imagepanel.ImagePanel;
import com.c2.pandora.instancepanel.InstancePanel;
import com.c2.pandora.keystonepanel.KeystonePanel;
import com.c2.pandora.sdn.SDNPanel;
import com.c2.pandora.serverpanel.MainServerPanel;
import com.c2.pandora.settingpanel.SettingPanel;
import com.c2.pandora.thread.PandoraServerUpdateThread;
import com.c2.pandora.vdipanel.VDIPanel;
import com.peterswing.CommonLib;
import com.peterswing.FilterTreeModel;

public class MainFrame extends JFrame {
	private JPanel contentPane;
	JPanel mainContentPanel = new JPanel();
	JSplitPane splitPane = new JSplitPane();
	JLabel mainScreenLabel = new JLabel();
	private WelcomePanel welcomePanel;
	public static JTree serverTree;
	TextTreeNode serverRoot = new TextTreeNode("Servers");
	FilterTreeModel projectFilterTreeModel = new FilterTreeModel(new ServerTreeModel(serverRoot));

	public MainFrame() {
		setTitle("Pandora");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 700);
		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("com/c2/pandora/image/c2_icon.png")).getImage());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		splitPane.setDividerLocation(200);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(0, 0));

		JLabel logoLabel = new JLabel();
		logoLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(welcomePanel, BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		logoLabel.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/pandoraLogo.png")));
		//		logoLabel.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/openstack-logo.png")));
		panel.add(logoLabel, BorderLayout.NORTH);

		JPanel controlPanel = new JPanel();
		controlPanel.setBackground(new Color(239, 249, 255));
		controlPanel.setOpaque(true);
		panel.add(controlPanel, BorderLayout.CENTER);
		controlPanel.setLayout(new MigLayout("", "[1px,grow]", "[1px][][][grow][][][][][][][][][][][][][][]"));

		JLabel lblDashboard = new JLabel("");
		lblDashboard.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainmenu/dashboard.png")));
		lblDashboard.setForeground(Color.DARK_GRAY);
		lblDashboard.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblDashboard, "cell 0 1");

		JLabel lblSdn = new JLabel("");
		lblSdn.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainmenu/network.png")));
		lblSdn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(new SDNPanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});

		JLabel lblServer = new JLabel("");
		lblServer.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainmenu/server.png")));
		lblServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(new MainServerPanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblServer.setForeground(Color.DARK_GRAY);
		lblServer.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblServer, "flowx,cell 0 2");

		JScrollPane scrollPane = new JScrollPane();
		controlPanel.add(scrollPane, "cell 0 3 1 8,grow");

		serverTree = new JTree();
		serverTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				((MainPanel) mainContentPanel.getComponent(0)).refresh();
			}
		});
		serverTree.setModel(projectFilterTreeModel);
		serverTree.setCellRenderer(new ServerTreeRenderer());
		serverTree.setRootVisible(false);
		scrollPane.setViewportView(serverTree);
		updateServerTree();

		JLabel lblInstances = new JLabel("");
		lblInstances.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainmenu/instance.png")));
		lblInstances.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(new InstancePanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblInstances.setForeground(Color.DARK_GRAY);
		lblInstances.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblInstances, "cell 0 11,grow");

		JLabel lblKeystone = new JLabel("");
		lblKeystone.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainmenu/keystone.png")));
		lblKeystone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(new KeystonePanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblKeystone.setForeground(Color.DARK_GRAY);
		lblKeystone.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblKeystone, "cell 0 12,grow");

		JLabel lblFlavors = new JLabel("");
		lblFlavors.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainmenu/flavor.png")));
		lblFlavors.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(new FlavorPanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblFlavors.setForeground(Color.DARK_GRAY);
		lblFlavors.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblFlavors, "cell 0 13,grow");

		JLabel lblImages = new JLabel("");
		lblImages.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainmenu/image.png")));
		lblImages.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(new ImagePanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblImages.setForeground(Color.DARK_GRAY);
		lblImages.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblImages, "cell 0 14,grow");

		JLabel lblVdi = new JLabel("");
		lblVdi.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainmenu/vdi.png")));
		lblVdi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(new VDIPanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblVdi.setForeground(Color.DARK_GRAY);
		lblVdi.setFont(new Font("Dialog", Font.PLAIN, 16));
		controlPanel.add(lblVdi, "cell 0 15");
		lblSdn.setForeground(Color.DARK_GRAY);
		lblSdn.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblSdn, "cell 0 16,grow");

		JLabel lblSettings = new JLabel("");
		lblSettings.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainmenu/setting.png")));
		lblSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(new SettingPanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblSettings.setForeground(Color.DARK_GRAY);
		lblSettings.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblSettings, "cell 0 17,grow");

		JLabel AddServerLabel = new JLabel("");
		AddServerLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				AddServerDialog addServerDialog = new AddServerDialog(MainFrame.this);
				CommonLib.centerDialog(addServerDialog);
				addServerDialog.setVisible(true);

				updateServerTree();
			}
		});
		AddServerLabel.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/famfamfam/add.png")));
		controlPanel.add(AddServerLabel, "cell 0 2");
		splitPane.setLeftComponent(panel);

		JButton btnLogout = new JButton("Logout");
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logout();
			}
		});
		panel.add(btnLogout, BorderLayout.SOUTH);

		splitPane.setRightComponent(mainContentPanel);
		mainContentPanel.setLayout(new BorderLayout(0, 0));

		welcomePanel = new WelcomePanel();
		mainContentPanel.add(welcomePanel, BorderLayout.CENTER);
		welcomePanel.setLayout(new BorderLayout(0, 0));
		welcomePanel.add(mainScreenLabel, BorderLayout.CENTER);

		mainScreenLabel.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/mainscreen.png")));

		JPanel panel_1 = new JPanel();
		welcomePanel.add(panel_1, BorderLayout.SOUTH);

		JButton licenseButton = new JButton("License");
		licenseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputStream in = MainFrame.class.getResourceAsStream("/com/c2/pandora/license.txt");
				try {
					LicenseDialog dialog = new LicenseDialog(MainFrame.this, IOUtils.toString(in));
					CommonLib.centerDialog(dialog);
					dialog.setVisible(true);
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					IOUtils.closeQuietly(in);
				}
			}
		});
		panel_1.add(licenseButton);

		setLocationRelativeTo(null);

		System.out.println("Start update thread");
		new Thread(new PandoraServerUpdateThread()).start();
	}

	protected void updateServerTree() {
		serverRoot.children.removeAllElements();
		for (int x = 0; x < PandoraSetting.getInstance().pandoraServers.size(); x++) {
			ServerTreeNode server = new ServerTreeNode(PandoraSetting.getInstance().pandoraServers.get(x));
			server.setIcon(new ImageIcon(MainFrame.class.getResource("/com/c2/pandora/image/famfamfam/server.png")));
			serverRoot.children.add(server);
		}
		projectFilterTreeModel.reload();
		((FilterTreeModel) serverTree.getModel()).reload();
	}

	private void logout() {
		setVisible(false);
		Pandora frame = new Pandora();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
