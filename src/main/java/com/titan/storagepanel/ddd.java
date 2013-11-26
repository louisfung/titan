package com.titan.storagepanel;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class ddd extends JFrame {

	private JPanel contentPane;
	private JButton button1;
	private JButton button2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.peterswing.white.PeterSwingWhiteLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ddd frame = new ddd();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ddd() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		button1 = new JButton("New button");

		button2 = new JButton("New button");
		button2.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				System.out.println("m2");
				//				for (MouseMotionListener ml : button1.getMouseMotionListeners()) {
				//					System.out.println("f");
				//					ml.mouseMoved(cloneMouseEvent(e));
				//					button1.repaint();
				//				}
				button1.getModel().setPressed(true);
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
				gl_contentPane
						.createSequentialGroup()
						.addGroup(
								gl_contentPane.createParallelGroup(Alignment.LEADING).addComponent(button1)
										.addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addComponent(button2))).addContainerGap(317, Short.MAX_VALUE)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(
				gl_contentPane.createSequentialGroup().addGap(53).addComponent(button1).addGap(38).addComponent(button2).addContainerGap(119, Short.MAX_VALUE)));
		contentPane.setLayout(gl_contentPane);
	}

	private MouseEvent cloneMouseEvent(MouseEvent e) {
		return new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(),
				e.isPopupTrigger(), e.getButton());
	}
}
