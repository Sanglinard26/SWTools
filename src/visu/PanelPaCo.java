package visu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import lab.ListModelVar;
import paco.ListModelLabel;
import paco.PaCo;
import paco.TableModelHistory;
import tools.Preference;
import tools.Utilitaire;

public final class PanelPaCo extends JPanel {
	
	private static final GridBagConstraints gbc = new GridBagConstraints();
	
	// GUI
	private final JButton btOpen;
	private final JLabel labelNomPaCo;
	private final JLabel labelNbLabel;
	private final JTextField txtFiltre;
	private final ListLabel listLabel;
	private JPanel panVisu;
	private final TableHistory tableHistory;
	
	// PaCo
	private PaCo paco;
	
	public PanelPaCo() {
		
		setLayout(new GridBagLayout());
		
		gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 5, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
		btOpen = new JButton(new AbstractAction("Ouvrir") {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser jFileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_PACO));
				jFileChooser.setFileFilter(new FileFilter() {
					
					@Override
					public String getDescription() {
						return "PaCo *.xml";
					}
					
					@Override
					public boolean accept(File f) {
						
						if (f.isDirectory()) return true;
						
						String extension = Utilitaire.getExtension(f);
						if (extension.equals(Utilitaire.xml))
						{
							return true;
						}
						return false;
					}
				});
				
				int reponse = jFileChooser.showOpenDialog(PanelPaCo.this);
				if (reponse == JFileChooser.APPROVE_OPTION)
				{
					paco = new PaCo(jFileChooser.getSelectedFile().getPath());
					labelNomPaCo.setText("Nom : " + paco.getName());
					labelNbLabel.setText("Nombre de label : " + paco.getNbLabel());
					listLabel.getModel().setList(paco.getListLabel());
					listLabel.clearSelection();
					tableHistory.getModel().setData(new String[0][0]);
				}
			}
		});
		add(btOpen, gbc);
		
		gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
		labelNomPaCo = new JLabel("Nom : ");
		add(labelNomPaCo, gbc);
		
		gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.LINE_START;
		labelNbLabel = new JLabel("Nombre de label : ");
		add(labelNbLabel, gbc);
		
		gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 5, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        txtFiltre = new JTextField(20);
        txtFiltre.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				listLabel.getModel().setFilter(txtFiltre.getText().toLowerCase());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				listLabel.getModel().setFilter(txtFiltre.getText().toLowerCase());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// Non utilise
				
			}
		});
        add(txtFiltre, gbc);
		
		gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.gridheight = 3;
        gbc.weightx = 0.2;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 5, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
		listLabel = new ListLabel(new ListModelLabel());
		listLabel.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting() & !listLabel.isSelectionEmpty())
				{
					tableHistory.getModel().setData(listLabel.getSelectedValue().getSwCsHistory());
					panVisu.removeAll();
					panVisu.repaint();
					panVisu.add(listLabel.getSelectedValue().showView());
					panVisu.validate();
				}
			}
		});
		add(new JScrollPane(listLabel), gbc);
		
        panVisu = new JPanel();
        //panVisu.add(new JLabel(String.valueOf(Math.random())));
        panVisu.setBackground(Color.WHITE);

        tableHistory = new TableHistory(new TableModelHistory());
        tableHistory.setFillsViewportHeight(false);
        
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 4;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        JSplitPane splitPane = new JSplitPane(
        		JSplitPane.VERTICAL_SPLIT, 
        		new JScrollPane(panVisu), 
        		new JScrollPane(tableHistory));
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(400);
        add(splitPane, gbc);
	}

}
