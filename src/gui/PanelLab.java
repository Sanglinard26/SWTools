package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import lab.Lab;
import lab.ListModelLab;
import lab.ListModelVar;
import lab.Variable;
import utils.Preference;
import utils.Utilitaire;

public final class PanelLab extends JComponent implements ListDataListener {

	private static final long serialVersionUID = 1L;
	// Constante
	private static final String BT_COMPAR_LAB = "Comparer";
	private static final String BT_EXPORT = "Exporter";
	private static final String BT_ADD_LAB_REF = "Ajout fichier(s) de reference";
	private static final String BT_ADD_LAB_WK = "Ajout fichier(s) de travail";

	// GUI
	private final JButton btCompar, btExport;
	private static final GridBagConstraints gbc = new GridBagConstraints();
	private final ListLab listLabCompil, listLabRef, listLabWk;
	private final ListVar listVarRef, listVarWk, listVarPlus, listVarMoins;
	private final JTextField filterVarRef, filterVarWk;

	public PanelLab() {

		this.setLayout(new BorderLayout(10, 0));

		final JPanel panelCompil = new JPanel(new GridBagLayout());
		panelCompil.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "<html><font size = 4><b>Compilation</b></font></html>"));

		setGbc(GridBagConstraints.HORIZONTAL, 0, 0, 1, 1, 0, 0, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
		final JButton btAddLab = new JButton("Ajouter fichiers Lab");
		btAddLab.addActionListener(new addLab());
		panelCompil.add(btAddLab, gbc);

		setGbc(GridBagConstraints.HORIZONTAL, 1, 0, 1, 1, 0, 0, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
		final JButton btCompil = new JButton("Compiler");
		btCompil.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {

				final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_RESULT_LAB));
				fileChooser.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "Fichier Lab (*.lab)";
					}

					@Override
					public boolean accept(File f) {
						return Utilitaire.getExtension(f).equals("lab");
					}
				});
				fileChooser.setSelectedFile(new File("CompilationLab.lab"));
				final int rep = fileChooser.showSaveDialog(null);

				if (rep == JFileChooser.APPROVE_OPTION) {
					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {
							final Lab compilLab = Lab.compilLab(listLabCompil.getModel().getList());
							compilLab.write(fileChooser.getSelectedFile());
							JOptionPane.showMessageDialog(null, "Compilation terminee !");
						}
					});
					thread.start();
				}
			}
		});
		panelCompil.add(btCompil, gbc);

		setGbc(GridBagConstraints.BOTH, 0, 1, 2, 1, 1, 1, new Insets(0, 0, 0, 5), GridBagConstraints.WEST);
		listLabCompil = new ListLab(new ListModelLab());
		panelCompil.add(new JScrollPane(listLabCompil), gbc);

		// ***************************

		final JPanel panelCompar = new JPanel(new GridBagLayout());
		panelCompar.setBorder(new TitledBorder(BorderFactory.createLineBorder(Color.BLACK), "<html><font size = 4><b>Comparaison</b></font></html>"));

		setGbc(GridBagConstraints.HORIZONTAL, 0, 0, 1, 1, 0, 0, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
		final JButton btAddLabRef = new JButton(BT_ADD_LAB_REF);
		btAddLabRef.addActionListener(new addLab());
		panelCompar.add(btAddLabRef, gbc);

		setGbc(GridBagConstraints.HORIZONTAL, 1, 0, 1, 1, 0, 0, new Insets(0, 5, 0, 20), GridBagConstraints.CENTER);
		final JButton btAddLabWk = new JButton(BT_ADD_LAB_WK);
		btAddLabWk.addActionListener(new addLab());
		panelCompar.add(btAddLabWk, gbc);

		btCompar = new JButton(BT_COMPAR_LAB);
		btCompar.setEnabled(false);
		btCompar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				final Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {

						final Lab multiLabRef = Lab.compilLab(listLabRef.getModel().getList());
						final Lab multiLabWk = Lab.compilLab(listLabWk.getModel().getList());

						final Set<Variable> listSup = new HashSet<Variable>(
								Math.max(multiLabRef.getListVariable().size(), multiLabWk.getListVariable().size()));
						final Set<Variable> listInf = new HashSet<Variable>(
								Math.max(multiLabRef.getListVariable().size(), multiLabWk.getListVariable().size()));

						listSup.addAll(multiLabWk.getDiffLab(multiLabRef));
						listInf.addAll(multiLabRef.getDiffLab(multiLabWk));

						if (listInf.size() != 0 | listSup.size() != 0) {
							listVarMoins.getModel().setList(new Lab(listInf));
							listVarPlus.getModel().setList(new Lab(listSup));
						} else {
							JOptionPane.showMessageDialog(null, "Les fichiers Lab sont identiques !", "RESULTAT", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				});

				thread.start();
			}
		});
		setGbc(GridBagConstraints.HORIZONTAL, 2, 0, 1, 1, 0, 0, new Insets(0, 20, 0, 0), GridBagConstraints.CENTER);
		panelCompar.add(btCompar, gbc);

		btExport = new JButton(BT_EXPORT);
		btExport.setEnabled(false);
		btExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Lab.rapportToHtml(Lab.compilLab(listLabRef.getModel().getList()), Lab.compilLab(listLabWk.getModel().getList()));
			}
		});
		setGbc(GridBagConstraints.HORIZONTAL, 3, 0, 1, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		panelCompar.add(btExport, gbc);

		// Liste des lab ref
		setGbc(GridBagConstraints.BOTH, 0, 1, 1, 2, 1, 0.3, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
		listLabRef = new ListLab(new ListModelLab());
		listLabRef.getModel().addListDataListener(this);
		listLabRef.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && !listLabRef.isSelectionEmpty()) {
					filterVarRef.setText("");
					listVarRef.getModel().setList(listLabRef.getSelectedValue());
				}
			}
		});
		panelCompar.add(new JScrollPane(listLabRef), gbc);

		// Liste des lab travail
		setGbc(GridBagConstraints.BOTH, 1, 1, 1, 2, 1, 0.3, new Insets(0, 5, 0, 20), GridBagConstraints.CENTER);
		listLabWk = new ListLab(new ListModelLab());
		listLabWk.getModel().addListDataListener(this);
		listLabWk.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting() && !listLabWk.isSelectionEmpty()) {
					filterVarWk.setText("");
					listVarWk.getModel().setList(listLabWk.getSelectedValue());
				}
			}
		});
		panelCompar.add(new JScrollPane(listLabWk), gbc);

		setGbc(GridBagConstraints.NONE, 2, 1, 1, 1, 0, 0, new Insets(10, 20, 0, 0), GridBagConstraints.CENTER);
		panelCompar.add(new JLabel("Label(s) disparu(s)"), gbc);

		setGbc(GridBagConstraints.NONE, 3, 1, 1, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		panelCompar.add(new JLabel("Label(s) supplementaire(s)"), gbc);

		filterVarRef = new JTextField(20);
		filterVarRef.setEditable(false);
		filterVarRef.setFont(new Font(null, Font.ITALIC, 12));
		filterVarRef.setBorder(new LineBorder(Color.BLACK, 1, false));
		filterVarRef.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				listVarRef.getModel().setFilter(filterVarRef.getText().toLowerCase());

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				listVarRef.getModel().setFilter(filterVarRef.getText().toLowerCase());

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Non utilisee
			}
		});
		setGbc(GridBagConstraints.HORIZONTAL, 0, 3, 1, 1, 0, 0, new Insets(10, 0, 0, 5), GridBagConstraints.CENTER);
		panelCompar.add(filterVarRef, gbc);

		filterVarWk = new JTextField(20);
		filterVarWk.setEditable(false);
		filterVarWk.setFont(new Font(null, Font.ITALIC, 12));
		filterVarWk.setBorder(new LineBorder(Color.BLACK, 1, false));
		filterVarWk.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				listVarWk.getModel().setFilter(filterVarWk.getText().toLowerCase());

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				listVarWk.getModel().setFilter(filterVarWk.getText().toLowerCase());

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Non utilisee
			}
		});
		setGbc(GridBagConstraints.HORIZONTAL, 1, 3, 1, 1, 0, 0, new Insets(10, 5, 0, 20), GridBagConstraints.CENTER);
		panelCompar.add(filterVarWk, gbc);

		// Liste du lab ref
		setGbc(GridBagConstraints.BOTH, 0, 4, 1, 1, 1, 1, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
		listVarRef = new ListVar(new ListModelVar());
		panelCompar.add(new JScrollPane(listVarRef), gbc);

		// Liste du lab de travail
		setGbc(GridBagConstraints.BOTH, 1, 4, 1, 1, 1, 1, new Insets(0, 5, 0, 20), GridBagConstraints.CENTER);
		listVarWk = new ListVar(new ListModelVar());
		panelCompar.add(new JScrollPane(listVarWk), gbc);

		setGbc(GridBagConstraints.BOTH, 2, 2, 1, 3, 1, 1, new Insets(0, 20, 0, 0), GridBagConstraints.CENTER);
		listVarMoins = new ListVar(new ListModelVar());
		listVarMoins.getModel().addListDataListener(this);
		panelCompar.add(new JScrollPane(listVarMoins), gbc);

		setGbc(GridBagConstraints.BOTH, 3, 2, 1, 3, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		listVarPlus = new ListVar(new ListModelVar());
		listVarPlus.getModel().addListDataListener(this);
		panelCompar.add(new JScrollPane(listVarPlus), gbc);

		this.add(panelCompil, BorderLayout.WEST);
		this.add(panelCompar, BorderLayout.CENTER);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// g.setColor(Color.LIGHT_GRAY);
		// g.fillRect((this.getWidth() / 2) + 5 - (8 / 2), this.getHeight() / 4, 8, this.getHeight() / 2);
	}

	private class addLab implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser jFileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_ADD_LAB));
			jFileChooser.setMultiSelectionEnabled(true);
			jFileChooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "Fichier (*.lab), (*.xml), (*.dcm)";
				}

				@Override
				public boolean accept(File f) {

					if (f.isDirectory())
						return true;

					String extension = Utilitaire.getExtension(f);
					if (extension.equals(Utilitaire.LAB) | extension.equals(Utilitaire.XML) | extension.equals(Utilitaire.DCM)) {
						return true;
					}
					return false;
				}
			});

			final int reponse = jFileChooser.showOpenDialog(PanelLab.this);
			if (reponse == JFileChooser.APPROVE_OPTION) {

				for (File file : jFileChooser.getSelectedFiles()) {
					if (e.getActionCommand().equals(BT_ADD_LAB_REF)) {
						listLabRef.getModel().addLab(new Lab(file));

					} else if (e.getActionCommand().equals(BT_ADD_LAB_WK)) {
						listLabWk.getModel().addLab(new Lab(file));
					} else {
						listLabCompil.getModel().addLab(new Lab(file));
					}
				}
			}
		}
	}

	private static void setGbc(int fill, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, Insets insets,
			int anchor) {
		gbc.fill = fill;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.insets = insets;
		gbc.anchor = anchor;
	}

	@Override
	public void intervalAdded(ListDataEvent e) {
		traitementEven(e);
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		traitementEven(e);
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		traitementEven(e);
	}

	private void traitementEven(ListDataEvent event) {
		switch (event.getType()) {
		case ListDataEvent.INTERVAL_ADDED:
			// Condition pour autoriser le filtrage de label réf
			filterVarRef.setEditable(true);

			// Condition pour autoriser le filtrage de label wk
			filterVarWk.setEditable(true);

			// Condition d'activation du bouton "Comparer"
			if (listLabRef.getModel().getSize() * listLabWk.getModel().getSize() != 0) {
				btCompar.setEnabled(true);
			}

			listVarMoins.getModel().clearList();
			listVarPlus.getModel().clearList();

			// Condition d'activation du bouton "Exporter"
			if (listVarMoins.getModel().getSize() + listVarPlus.getModel().getSize() != 0) {
				btExport.setEnabled(true);
			}

			break;
		case ListDataEvent.INTERVAL_REMOVED:
			// Condition pour autoriser le filtrage de label réf

			listVarRef.getModel().clearList();

			// Condition pour autoriser le filtrage de label wk
			listVarWk.getModel().clearList();

			// Condition d'activation du bouton "Comparer"
			if (listLabRef.getModel().getSize() * listLabWk.getModel().getSize() != 0) {
				btCompar.setEnabled(true);
			} else {
				btCompar.setEnabled(false);
			}

			listVarMoins.getModel().clearList();
			listVarPlus.getModel().clearList();

			// Condition d'activation du bouton "Exporter"
			if (listVarMoins.getModel().getSize() + listVarPlus.getModel().getSize() != 0) {
				btExport.setEnabled(true);
			} else {
				btExport.setEnabled(false);
			}

			break;
		case ListDataEvent.CONTENTS_CHANGED:
			// Condition pour autoriser le filtrage de label réf
			if (listLabRef.getModel().getSize() > 0) {
				filterVarRef.setEditable(true);
			} else {
				listVarRef.getModel().clearList();
				filterVarRef.setEditable(false);
			}

			// Condition pour autoriser le filtrage de label wk
			if (listLabWk.getModel().getSize() > 0) {
				filterVarWk.setEditable(true);
			} else {
				listVarWk.getModel().clearList();
				filterVarWk.setEditable(false);
			}

			// Condition d'activation du bouton "Comparer"
			if (listLabRef.getModel().getSize() * listLabWk.getModel().getSize() != 0) {
				btCompar.setEnabled(true);
			} else {
				btCompar.setEnabled(false);
			}

			// Condition d'activation du bouton "Exporter"
			if (listVarMoins.getModel().getSize() + listVarPlus.getModel().getSize() != 0) {
				btExport.setEnabled(true);
			} else {
				btExport.setEnabled(false);
			}

			break;
		default:
			break;
		}
	}

}
