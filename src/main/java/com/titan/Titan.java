package com.titan;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.titan.communication.CommunicateLib;
import com.titan.mainframe.MainFrame;
import com.titanserver.Command;
import com.titanserver.ReturnCommand;

public class Titan extends JFrame {
	private JPanel contentPane;
	private JTextField textFieldIP;
	private JTextField textFieldUsername;
	private JPasswordField textFieldPassword;
	private JLabel lblErrorMessage = new JLabel("");
	MainFrame mainframe;
	static CommandLine cmd;
	public static boolean hideLogo;

	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				CommandLineParser parser = new PosixParser();
				Options options = new Options();
				try {
					//options.addOption(OptionBuilder.withDescription("specific config xml").hasArg().withArgName("file").create("f"));
					options.addOption("v", "version", false, "display version info");
					options.addOption("n", "nologo", false, "hide logo");
					cmd = parser.parse(options, args);
				} catch (ParseException e1) {
					e1.printStackTrace();
					System.exit(1);
				}

				if (cmd.hasOption("version") || cmd.hasOption("v")) {
					System.out.println("version : " + Global.version);
					HelpFormatter formatter = new HelpFormatter();
					formatter.printHelp("java -jar titan.jar [OPTION]", options);
					System.exit(1);
				}

				if (cmd.hasOption("nologo") || cmd.hasOption("n")) {
					hideLogo = true;
				}

				try {
					UIManager.setLookAndFeel("com.peterswing.white.PeterSwingWhiteLookAndFeel");
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					com.apple.eawt.Application macApp = com.apple.eawt.Application.getApplication();
					macApp.setDockIconImage(new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/titan_icon.png")).getImage());
				} catch (Exception e) {
				}

				Titan frame = new Titan();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	public Titan() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				TitanSetting.getInstance().save();
			}

			@Override
			public void windowOpened(WindowEvent e) {
				if (!textFieldUsername.getText().equals("") && !textFieldIP.getText().equals("")) {
					textFieldPassword.requestFocus();
				}
			}
		});
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 317, 319);
		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("com/titan/image/titan_icon.png")).getImage());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblIp = new JLabel("IP");
		lblIp.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI("http://www.titan-engine.net"));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		if (hideLogo) {
			lblNewLabel.setIcon(new ImageIcon(Titan.class.getResource("/com/titan/image/empty_logo.png")));
		} else {
			lblNewLabel.setIcon(new ImageIcon(Titan.class.getResource("/com/titan/image/titan_logo.png")));
		}

		textFieldIP = new JTextField();
		textFieldIP.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				lblErrorMessage.setText("");
				if (e.getKeyCode() == 10) {
					textFieldUsername.requestFocus();
				}
			}
		});
		textFieldIP.setColumns(10);
		textFieldIP.setText(TitanSetting.getInstance().lastIP);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);

		textFieldUsername = new JTextField();
		textFieldUsername.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				lblErrorMessage.setText("");
				if (e.getKeyCode() == 10) {
					textFieldPassword.requestFocus();
				}
			}
		});
		textFieldUsername.setColumns(10);
		textFieldUsername.setText(TitanSetting.getInstance().lastUsername);

		textFieldPassword = new JPasswordField();
		textFieldPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				lblErrorMessage.setText("");
				if (e.getKeyCode() == 10) {
					login();
				}
			}
		});

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		lblErrorMessage.setHorizontalAlignment(SwingConstants.CENTER);

		lblErrorMessage.setForeground(Color.RED);

		JLabel lblVersion = new JLabel(Global.version);
		lblVersion.setFont(new Font("Dialog", Font.PLAIN, 8));
		lblVersion.setForeground(Color.DARK_GRAY);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(10)
										.addGroup(
												gl_contentPane
														.createParallelGroup(Alignment.LEADING)
														.addGroup(gl_contentPane.createSequentialGroup().addComponent(lblNewLabel).addContainerGap(21, Short.MAX_VALUE))
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addGap(13)
																		.addGroup(
																				gl_contentPane
																						.createParallelGroup(Alignment.TRAILING, false)
																						.addComponent(lblPassword, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.addComponent(lblUsername, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE).addComponent(lblIp, GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE))
																		.addGap(27)
																		.addGroup(
																				gl_contentPane
																						.createParallelGroup(Alignment.LEADING)
																						.addComponent(textFieldIP, GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
																						.addComponent(textFieldUsername, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 157,
																								Short.MAX_VALUE)
																						.addGroup(
																								gl_contentPane
																										.createSequentialGroup()
																										.addComponent(textFieldPassword, GroupLayout.PREFERRED_SIZE, 157,
																												GroupLayout.PREFERRED_SIZE)
																										.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE,
																												Short.MAX_VALUE))).addGap(35))))
						.addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addComponent(lblErrorMessage, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE).addGap(17))
						.addGroup(
								gl_contentPane.createSequentialGroup().addGap(128).addComponent(btnLogin, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED, 84, Short.MAX_VALUE).addComponent(lblVersion).addContainerGap()));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
				gl_contentPane
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblNewLabel)
						.addGap(18)
						.addGroup(
								gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(textFieldIP, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblIp))
						.addGap(12)
						.addGroup(
								gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(textFieldUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblUsername))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(
								gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(textFieldPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblPassword)).addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE).addComponent(lblErrorMessage)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(btnLogin).addComponent(lblVersion)).addContainerGap()));
		contentPane.setLayout(gl_contentPane);
		pack();
	}

	protected void login() {
		TitanSetting.getInstance().lastIP = textFieldIP.getText();
		TitanSetting.getInstance().lastUsername = textFieldUsername.getText();

		Global.primaryServerIP = textFieldIP.getText();

		Command command = new Command();
		command.command = "login";
		command.parameters.add(textFieldUsername.getText());
		command.parameters.add(String.valueOf(textFieldPassword.getPassword()));
		ReturnCommand r = CommunicateLib.send(command);
		if (r == null) {
			lblErrorMessage.setText("cannot connect to server");
		} else if (!r.isError) {
			if (r.message.equals("yes")) {
				TitanSetting.getInstance().save();
				Titan.this.setVisible(false);
				if (mainframe == null) {
					mainframe = new MainFrame();
					mainframe.setSize(TitanSetting.getInstance().width, TitanSetting.getInstance().height);
					mainframe.setLocation(TitanSetting.getInstance().x, TitanSetting.getInstance().y);
					mainframe.setVisible(true);
				}
			} else {
				lblErrorMessage.setText("wrong password");
				pack();
			}
		}
	}
}
