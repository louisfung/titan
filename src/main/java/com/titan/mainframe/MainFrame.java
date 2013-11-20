package com.titan.mainframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.IOUtils;

import com.peterswing.CommonLib;
import com.peterswing.FilterTreeModel;
import com.titan.Global;
import com.titan.LicenseDialog;
import com.titan.MainPanel;
import com.titan.Titan;
import com.titan.TitanCommonLib;
import com.titan.TitanSetting;
import com.titan.WelcomePanel;
import com.titan.communication.CommunicateLib;
import com.titan.dashboard.DashboardPanel;
import com.titan.flavorpanel.FlavorPanel;
import com.titan.instancepanel.InstancePanel;
import com.titan.keystonepanel.KeystonePanel;
import com.titan.sdn.SDNPanel;
import com.titan.serverpanel.MainServerPanel;
import com.titan.settingpanel.SettingPanel;
import com.titan.storagepanel.StoragePanel;
import com.titan.thread.TitanServerUpdateThread;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;
import com.titanserver.structure.TitanServerDefinition;

public class MainFrame extends JFrame {
	private JPanel contentPane;
	JPanel mainContentPanel = new JPanel();
	JSplitPane splitPane = new JSplitPane();
	JLabel mainScreenLabel = new JLabel();
	private WelcomePanel welcomePanel;
	public static JTree serverTree;
	TextTreeNode serverRoot = new TextTreeNode("Servers");
	public FilterTreeModel projectFilterTreeModel = new FilterTreeModel(new ServerTreeModel(serverRoot));
	MainServerPanel mainServerPanel;
	private JLabel lblServer;
	private JLabel lblDashboard;
	private JLabel lblInstances;
	private JLabel lblKeystone;
	private JLabel lblFlavors;
	private JLabel lblStorages;
	private JLabel lblSdn;
	private JLabel lblSettings;
	Color selectedBorderColor = new Color(0, 30, 255);

	public MainFrame() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				TitanSetting.getInstance().x = MainFrame.this.getX();
				TitanSetting.getInstance().y = MainFrame.this.getY();
				TitanSetting.getInstance().width = MainFrame.this.getWidth();
				TitanSetting.getInstance().height = MainFrame.this.getHeight();
				TitanSetting.getInstance().save();
			}
		});
		setTitle("Titan " + Global.version);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		if (TitanSetting.getInstance().width == 0 || TitanSetting.getInstance().height == 0) {
			setBounds(TitanSetting.getInstance().x, TitanSetting.getInstance().y, 1200, 700);
		} else {
			setBounds(TitanSetting.getInstance().x, TitanSetting.getInstance().y, TitanSetting.getInstance().width, TitanSetting.getInstance().height);
		}

		if (!Titan.hideLogo) {//$hide$
			setIconImage(new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/titan_icon.png")).getImage());
		}//$hide$

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

		if (!Titan.hideLogo) {//$hide$
			logoLabel.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/titanLogo.png")));
		}//$hide$

		//		logoLabel.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/openstack-logo.png")));
		panel.add(logoLabel, BorderLayout.NORTH);

		JPanel controlPanel = new JPanel();
		controlPanel.setBackground(new Color(239, 249, 255));
		controlPanel.setOpaque(true);
		panel.add(controlPanel, BorderLayout.CENTER);
		controlPanel.setLayout(new MigLayout("", "[1px,grow]", "[1px][][][grow][][][][][][]"));

		lblDashboard = new JLabel("");
		lblDashboard.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainContentPanel.removeAll();
				mainContentPanel.add(new DashboardPanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblDashboard.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/mainmenu/dashboard.png")));
		lblDashboard.setForeground(Color.DARK_GRAY);
		lblDashboard.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblDashboard, "cell 0 1,growx");

		lblServer = new JLabel("");
		lblServer.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/mainmenu/server.png")));
		lblServer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (mainServerPanel == null || !mainServerPanel.serverPanel.jprogressBarDialog.isActive()) {
					clearSelectedColor();
					lblServer.setBorder(new LineBorder(selectedBorderColor, 1));
					mainContentPanel.removeAll();
					mainServerPanel = new MainServerPanel(MainFrame.this);
					mainContentPanel.add(mainServerPanel, BorderLayout.CENTER);
					mainContentPanel.updateUI();
				}
			}
		});
		lblServer.setForeground(Color.DARK_GRAY);
		lblServer.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblServer, "flowx,cell 0 2,growx");

		JScrollPane scrollPane = new JScrollPane();
		controlPanel.add(scrollPane, "cell 0 3 1 1,grow");

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

		lblInstances = new JLabel("");
		lblInstances.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/mainmenu/instance.png")));
		lblInstances.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearSelectedColor();
				lblInstances.setBorder(new LineBorder(selectedBorderColor, 1));
				mainContentPanel.removeAll();
				mainContentPanel.add(new InstancePanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblInstances.setForeground(Color.DARK_GRAY);
		lblInstances.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblInstances, "cell 0 4,grow");

		lblKeystone = new JLabel("");
		lblKeystone.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/mainmenu/keystone.png")));
		lblKeystone.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearSelectedColor();
				lblKeystone.setBorder(new LineBorder(selectedBorderColor, 1));
				mainContentPanel.removeAll();
				mainContentPanel.add(new KeystonePanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblKeystone.setForeground(Color.DARK_GRAY);
		lblKeystone.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblKeystone, "cell 0 5,grow");

		lblFlavors = new JLabel("");
		lblFlavors.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/mainmenu/flavor.png")));
		lblFlavors.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearSelectedColor();
				lblFlavors.setBorder(new LineBorder(selectedBorderColor, 1));
				mainContentPanel.removeAll();
				mainContentPanel.add(new FlavorPanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblFlavors.setForeground(Color.DARK_GRAY);
		lblFlavors.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblFlavors, "cell 0 6,grow");

		lblStorages = new JLabel("");
		lblStorages.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/mainmenu/storage.png")));
		lblStorages.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearSelectedColor();
				lblStorages.setBorder(new LineBorder(selectedBorderColor, 1));
				mainContentPanel.removeAll();
				mainContentPanel.add(new StoragePanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblStorages.setForeground(Color.DARK_GRAY);
		lblStorages.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblStorages, "cell 0 7,grow");

		lblSdn = new JLabel("");
		lblSdn.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/mainmenu/network.png")));
		lblSdn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearSelectedColor();
				lblSdn.setBorder(new LineBorder(selectedBorderColor, 1));
				mainContentPanel.removeAll();
				mainContentPanel.add(new SDNPanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblSdn.setForeground(Color.DARK_GRAY);
		lblSdn.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblSdn, "cell 0 8,grow");

		lblSettings = new JLabel("");
		lblSettings.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/mainmenu/setting.png")));
		lblSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clearSelectedColor();
				lblSettings.setBorder(new LineBorder(selectedBorderColor, 1));
				mainContentPanel.removeAll();
				mainContentPanel.add(new SettingPanel(MainFrame.this), BorderLayout.CENTER);
				mainContentPanel.updateUI();
			}
		});
		lblSettings.setForeground(Color.DARK_GRAY);
		lblSettings.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		controlPanel.add(lblSettings, "cell 0 9,grow");
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
		if (!Titan.hideLogo) {
			mainScreenLabel.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/mainscreen.png")));
		}

		JPanel panel_1 = new JPanel();
		welcomePanel.add(panel_1, BorderLayout.SOUTH);

		JButton licenseButton = new JButton("License");
		licenseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputStream in = MainFrame.class.getResourceAsStream("/com/titan/license.txt");
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
		new Thread(new TitanServerUpdateThread()).start();
	}

	protected void clearSelectedColor() {
		lblServer.setBorder(null);
		lblInstances.setBorder(null);
		lblKeystone.setBorder(null);
		lblFlavors.setBorder(null);
		lblStorages.setBorder(null);
		lblSdn.setBorder(null);
		lblSettings.setBorder(null);
	}

	public void updateServerTree() {
		serverRoot.children.removeAllElements();
		TitanServerDefinition serverDefinition = new TitanServerDefinition();
		serverDefinition.id = "";
		serverDefinition.ip = Global.primaryServerIP;

		Command command = new Command();
		command.command = "getID";
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		if (r != null) {
			serverDefinition.id = r.message;
		}

		ServerTreeNode server = new ServerTreeNode(serverDefinition);
		server.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/famfamfam/server.png")));
		serverRoot.children.add(server);

		for (int x = 0; x < TitanSetting.getInstance().titanServers.size(); x++) {
			server = new ServerTreeNode(TitanSetting.getInstance().titanServers.get(x));
			server.setIcon(new ImageIcon(MainFrame.class.getResource("/com/titan/image/famfamfam/server.png")));
			serverRoot.children.add(server);
		}
		projectFilterTreeModel.reload();
		((FilterTreeModel) serverTree.getModel()).reload();
	}

	private void logout() {
		setVisible(false);
		Titan frame = new Titan();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
