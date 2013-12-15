package com.titan.storagepanel;

import java.awt.BorderLayout;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.apache.http.Header;

import com.peterswing.CommonLib;
import com.titan.TitanCommonLib;
import com.titan.communication.CommunicateLib;
import com.titan.mainframe.MainFrame;
import com.titanserver.Command;
import com.titanserver.HttpResult;
import com.titanserver.ReturnCommand;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ImageMetaDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTable table;
	String imageId;
	ImageMetaTableModel model = new ImageMetaTableModel();

	public ImageMetaDialog(MainFrame mainframe, String imageId, String imageName) {
		super(mainframe, imageName, true);
		this.imageId = imageId;
		setBounds(100, 100, 750, 500);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				table.setModel(model);
				scrollPane.setViewportView(table);
			}
		}
		init();
		CommonLib.centerDialog(this);
	}

	protected void showImageMetaDialog() {
		// TODO Auto-generated method stub

	}

	private void init() {
		Command command = new Command();
		command.command = "from titan: glance image-show";
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("$imageId", imageId);
		command.parameters.add(parameters);
		ReturnCommand r = CommunicateLib.send(TitanCommonLib.getCurrentServerIP(), command);
		HttpResult httpResult = (HttpResult) r.map.get("result");
		Header headers[] = httpResult.headers;
		model.names.clear();
		model.values.clear();
		for (Header header : headers) {
			model.names.add(header.getName().replaceAll("X-Image-Meta-", ""));
			model.values.add(header.getValue());
		}
		model.fireTableDataChanged();
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(550);
	}
}
