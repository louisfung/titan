package com.titan.storagepanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.NodeList;

import com.peterswing.CommonLib;
import com.peterswing.GenericTableModel;
import com.peterswing.advancedswing.downloadfiledialog.DownloadFileDialog;
import com.peterswing.advancedswing.jprogressbardialog.JProgressBarDialog;
import com.peterswing.advancedswing.searchtextfield.JSearchTextField;
import com.titan.TitanCommonLib;

public class DownloadImageDialog extends JDialog {
	private final JPanel contentPanel = new JPanel();
	private JComboBox typeComboBox;
	private JSearchTextField searchTextField;
	private JComboBox sizeComboBox;
	GenericTableModel tableModel = new GenericTableModel();
	private JTable table;
	final JProgressBarDialog d;

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel("com.peterswing.white.PeterSwingWhiteLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		new DownloadImageDialog(null).setVisible(true);
	}

	public DownloadImageDialog(final Frame frame) {
		super(frame, true);
		setTitle("Openstack vm os image");
		setBounds(100, 100, 947, 706);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			panel.setLayout(new MigLayout("", "[41px][107.00px][27px][95.00px][24px][95.00px][]", "[27px]"));
			{
				JLabel lblSearch = new JLabel("Search");
				panel.add(lblSearch, "cell 0 0,alignx left,aligny center");
			}
			{
				searchTextField = new JSearchTextField();
				searchTextField.setPreferredSize(new Dimension(150, 40));
				panel.add(searchTextField, "cell 1 0,growx,aligny center");
			}
			{
				JLabel lblType = new JLabel("Type");
				panel.add(lblType, "cell 2 0,alignx left,aligny center");
			}
			{
				typeComboBox = new JComboBox(new String[] { "All", "Ubuntu", "Fedora", "Redhat", "CentOS", "Gentoo", "Windows" });
				panel.add(typeComboBox, "cell 3 0,growx,aligny top");
			}
			{
				JLabel lblSize = new JLabel("Size");
				panel.add(lblSize, "cell 4 0,alignx left,aligny center");
			}
			{
				sizeComboBox = new JComboBox(new String[] { "0-700MB", ">700MB" });
				panel.add(sizeComboBox, "cell 5 0,growx,aligny top");
			}
			{
				JButton btnSearch = new JButton("Search");
				btnSearch.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						d.setVisible(true);
					}
				});
				panel.add(btnSearch, "cell 6 0");
			}
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setModel(tableModel);
				table.addMouseListener(new JTableButtonMouseListener(table));
				table.addMouseMotionListener(new JTableButtonMouseListener(table));
				scrollPane.setViewportView(table);
			}
		}
		{
			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			{
				JButton downloadButton = new JButton("Download");
				downloadButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (table.getSelectedRowCount() == 1) {
							JFileChooser jf = new JFileChooser();
							int x = jf.showSaveDialog(frame);
							if (x == JFileChooser.APPROVE_OPTION) {
								DownloadImage p = (DownloadImage) table.getValueAt(table.getSelectedRow(), 0);
								DownloadFileDialog d = new DownloadFileDialog(frame, "Downloading image", true, p.file, jf.getSelectedFile());
								CommonLib.centerDialog(d);
								d.setVisible(true);
							}
						}
					}
				});
				panel.add(downloadButton);
			}
		}

		d = new JProgressBarDialog(frame, true);
		d.jProgressBar.setIndeterminate(true);
		d.jProgressBar.setStringPainted(true);
		d.thread = new Thread() {
			public void run() {
				InputStream in = null;
				tableModel.columnNames.clear();
				tableModel.columnNames.add("image");
				tableModel.editables.clear();
				tableModel.editables.put(0, true);
				Vector<Object> col1 = new Vector<Object>();
				try {
					d.jProgressBar.setString("connecting to titan image site");
					in = new URL("http://titan-image.kingofcoders.com/titan-image.xml").openStream();
					String xml = IOUtils.toString(in);
					NodeList list = TitanCommonLib.getXPathNodeList(xml, "/images/image");
					for (int x = 0; x < list.getLength(); x++) {
						DownloadImage downloadImage = new DownloadImage();
						downloadImage.author = TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/author/text()");
						downloadImage.authorEmail = TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/authorEmail/text()");
						downloadImage.License = TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/License/text()");
						downloadImage.description = TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/description/text()");
						downloadImage.file = TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/file/text()");
						downloadImage.os = TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/os/text()");
						downloadImage.osType = TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/osType/text()");
						downloadImage.uploadDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1)
								+ "]/uploadDate/text()"));
						downloadImage.size = TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/size/text()");
						downloadImage.architecture = TitanCommonLib.getXPath(xml, "/images/image[" + (x + 1) + "]/architecture/text()");
						col1.add(downloadImage);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Unable to connect titan image site !", "Error", JOptionPane.ERROR_MESSAGE);
				} finally {
					IOUtils.closeQuietly(in);
				}
				tableModel.values.clear();
				tableModel.values.add(col1);
				tableModel.fireTableStructureChanged();
				table.getColumnModel().getColumn(0).setCellRenderer(new DownloadImageTableCellRenderer());
				//				table.getColumnModel().getColumn(0).setCellEditor(new DownloadImageTableCellEditor());
				for (int x = 0; x < table.getRowCount(); x++) {
					table.setRowHeight(x, 150);
				}
			}
		};
		d.setVisible(true);
	}

	class JTableButtonMouseListener extends MouseAdapter {
		private final JTable table;

		public JTableButtonMouseListener(JTable table) {
			this.table = table;
		}

		public void mouseClicked(MouseEvent e) {

			//			int column = table.getColumnModel().getColumnIndexAtX(e.getX());
			//			int row = e.getY() / table.getRowHeight();
			//			DownloadImageTableCellRenderer downloadImageTableCellRenderer = (DownloadImageTableCellRenderer) table.getCellRenderer(row, column);
			//			DownloadImagePanel downloadImagePanel = (DownloadImagePanel) downloadImageTableCellRenderer.getTableCellRendererComponent(table, table.getValueAt(row, column),
			//					table.isRowSelected(row), table.hasFocus(), row, column);
			//			System.out.println(e.getX() + "," + downloadImagePanel.downloadButton.getX());
			//			downloadImagePanel.downloadButton.doClick();
			/*			int column = table.getColumnModel().getColumnIndexAtX(e.getX());
						int row    = e.getY()/table.getRowHeight(); 

						if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
							
						    System.out.println(value);
						    if (value instanceof DownloadImagePanel) {
						    	((DownloadImagePanel)value).doClick();
						    }
						}*/
		}

		private MouseEvent cloneMouseEvent(MouseEvent e) {
			return new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiers(), e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(), e.getClickCount(),
					e.isPopupTrigger(), e.getButton());
		}

		public void mouseMoved(MouseEvent e) {
			//			int column = table.columnAtPoint(e.getPoint());
			//			int row = table.rowAtPoint(e.getPoint());
			//			DownloadImageTableCellRenderer downloadImageTableCellRenderer = (DownloadImageTableCellRenderer) table.getCellRenderer(row, column);
			//			DownloadImagePanel downloadImagePanel = (DownloadImagePanel) downloadImageTableCellRenderer.getTableCellRendererComponent(table, table.getValueAt(row, column),
			//					table.isRowSelected(row), table.hasFocus(), row, column);
			//
			//			System.out.println(e.getPoint());
			//			System.out.println(downloadImagePanel.downloadButton.getLocation());
			//
			//			for (MouseMotionListener ml : downloadImagePanel.downloadButton.getMouseMotionListeners()) {
			//				System.out.println("f");
			//				ml.mouseMoved(cloneMouseEvent(e));
			//				downloadImagePanel.downloadButton.setIcon(null);
			//				downloadImagePanel.downloadButton.repaint();
			//			}
			//			table.repaint();
			//			ActionEvent e = new ActionEvent(downloadImagePanel.downloadButton, 1234, "CommandToPeform");
			//			myListener.actionPerformed(e);

			//			System.out.println(downloadImagePanel.getComponentAt(e.getPoint()));
		}
	}
}
