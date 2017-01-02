package visu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import lab.Lab;
import lab.Variable;
import tools.Utilitaire;

public class Ihm extends JFrame {

	private static final long serialVersionUID = 1L;
	//Constante
	private static final String BT_COMPAR_LAB = "Comparer";
	private static final String BT_EXPORT = "Exporter";
	private static final String BT_ADD_LAB_REF = "Ajout lab(s) de référence";
	private static final String BT_ADD_LAB_WK = "Ajout lab(s) de travail";

	//GUI
	private static GridBagConstraints gbc = new GridBagConstraints();
	private JTabbedPane onglets;
	private JLabel labelLabRef;
	private JLabel labelLabWk;
	private JButton btExpRes;
	private JList<Variable> listRef;
	private JList<Variable> listWk;
	private JList<Variable> listPlus;
	private JList<Variable> listMoins;

	//Variable lab
	private String pathLabRef = null;
	private String nameLabRef = null;
	private String pathLabWk = null;
	private String nameLabWk = null;
	private Lab labRef = null;
	private Lab labWork = null;

	//Variable multi lab
	private JTable tabLabRef;
	private JTable tabLabWk;
	private static DefaultTableModel tabmodelRef = new DefaultTableModel(new Object[]{"Lab réf"}, 0);
	private static DefaultTableModel tabmodelWk = new DefaultTableModel(new Object[]{"Lab work"}, 0);
	private ArrayList<Lab> arrayLabRef = new ArrayList<Lab>();
	private ArrayList<Lab> arrayLabWk = new ArrayList<Lab>();

	public Ihm()
	{
		setTitle("SW Tools");
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		onglets = new JTabbedPane(JTabbedPane.TOP);

		// Onglet comparaison de Lab
		JPanel onglet2 = new JPanel(new GridLayout(1, 2));
		createPanLab(onglet2);
		createPanLabMutli(onglet2);
		onglets.addTab("Comparaison lab", onglet2);

		onglets.setOpaque(true);
		getContentPane().add(onglets,BorderLayout.CENTER);	

	}


	private void createPanLab(JPanel parentPan)
	{

		// Deuxième onglet
		//JPanel onglet2 = new JPanel(new GridLayout(1, 2));
		JPanel panLab = new JPanel();
		panLab.setBorder(new LineBorder(Color.BLACK));
		parentPan.add(panLab);
		panLab.setLayout(new GridBagLayout());

		setGbc(GridBagConstraints.HORIZONTAL, 0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		JLabel titrePan = new JLabel("Lab vs Lab");
		titrePan.setOpaque(true);
		titrePan.setBackground(Color.LIGHT_GRAY);
		titrePan.setFont(new Font(null, Font.BOLD, 30));
		titrePan.setBorder(new LineBorder(Color.BLACK, 2));
		titrePan.setHorizontalAlignment(SwingConstants.CENTER);
		panLab.add(titrePan,gbc);


		setGbc(GridBagConstraints.HORIZONTAL, 0, 1, 2, 1, 0.5, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		JButton btLabRef = new JButton("Sélection Lab de référence");
		btLabRef.addActionListener(new selLab());
		panLab.add(btLabRef,gbc);

		setGbc(GridBagConstraints.NONE, 0, 2, 1, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.PAGE_START);
		panLab.add(new JLabel("Lab de référence : "),gbc);

		setGbc(GridBagConstraints.NONE, 1, 2, 1, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.PAGE_START);
		labelLabRef = new JLabel("...");
		labelLabRef.setFont(new Font(null, Font.BOLD | Font.ITALIC, 12));
		panLab.add(labelLabRef,gbc);

		setGbc(GridBagConstraints.HORIZONTAL, 2, 1, 2, 1, 0.5, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		JButton btLabWk = new JButton("Sélection Lab de travail");
		btLabWk.addActionListener(new selLab());
		panLab.add(btLabWk,gbc);

		setGbc(GridBagConstraints.NONE, 2, 2, 1, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.PAGE_START);
		panLab.add(new JLabel("Lab de travail : "),gbc);

		setGbc(GridBagConstraints.NONE, 3, 2, 1, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.PAGE_START);
		labelLabWk = new JLabel("...");
		labelLabWk.setFont(new Font(null, Font.BOLD | Font.ITALIC, 12));
		panLab.add(labelLabWk,gbc);

		//Liste du lab réf
		setGbc(GridBagConstraints.BOTH, 0, 5, 2, 1, 1, 1, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		listRef = new JList<Variable>();
		listRef.setCellRenderer(new ListVarRenderer());
		JScrollPane scrollPaneRef = new JScrollPane(listRef);
		scrollPaneRef.setMinimumSize(new Dimension(200, 600));
		panLab.add(scrollPaneRef,gbc);

		//Liste du lab de travail
		setGbc(GridBagConstraints.BOTH, 2, 5, 2, 1, 1, 1, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		listWk = new JList<Variable>();
		listWk.setCellRenderer(new ListVarRenderer());
		JScrollPane scrollPaneWk = new JScrollPane(listWk);
		scrollPaneWk.setMinimumSize(new Dimension(200, 600));
		panLab.add(scrollPaneWk,gbc);

		setGbc(GridBagConstraints.HORIZONTAL, 0, 6, 2, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		JButton btCompar = new JButton(BT_COMPAR_LAB);
		btCompar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (labRef!=null & labWork!=null)
				{							
					listMoins.setListData(Lab.getTabVar(labRef.getDiffLab(labWork)));
					listPlus.setListData(Lab.getTabVar(labWork.getDiffLab(labRef)));

					btExpRes.setEnabled(true);
				}else{
					JOptionPane.showMessageDialog(Ihm.this, "Il faut charger les deux lab", "ERREUR", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panLab.add(btCompar,gbc);

		setGbc(GridBagConstraints.HORIZONTAL, 2, 6, 2, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		btExpRes = new JButton("Exporter résultats");
		btExpRes.setEnabled(false);
		btExpRes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Lab.ecrireRapport(labRef,labWork);
			}
		});
		panLab.add(btExpRes,gbc);

		setGbc(GridBagConstraints.NONE, 0, 7, 2, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		panLab.add(new JLabel("Label(s) disparu(s)"),gbc);

		setGbc(GridBagConstraints.BOTH, 0, 8, 2, 6, 1, 1, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		listMoins = new JList<Variable>();
		listMoins.setCellRenderer(new ListVarRenderer());
		JScrollPane scrollPaneMoins = new JScrollPane(listMoins);
		scrollPaneMoins.setMinimumSize(new Dimension(200, 600));
		panLab.add(scrollPaneMoins,gbc);

		setGbc(GridBagConstraints.NONE, 2, 7, 2, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		panLab.add(new JLabel("Label(s) supplémentaire(s)"),gbc);

		setGbc(GridBagConstraints.BOTH, 2, 8, 2, 6, 1, 1, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		listPlus = new JList<Variable>();
		listPlus.setCellRenderer(new ListVarRenderer());
		JScrollPane scrollPanePlus = new JScrollPane(listPlus);
		scrollPanePlus.setMinimumSize(new Dimension(200, 600));
		panLab.add(scrollPanePlus,gbc);
	}

	private void createPanLabMutli(JPanel parentPan)
	{
		JPanel panLabBis = new JPanel();
		panLabBis.setBorder(new LineBorder(Color.BLACK));
		parentPan.add(panLabBis);
		panLabBis.setLayout(new GridBagLayout());

		setGbc(GridBagConstraints.HORIZONTAL, 0, 0, GridBagConstraints.REMAINDER, 1, 1, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		JLabel titrePanBis = new JLabel("Comparaison Lab multiple");
		titrePanBis.setOpaque(true);
		titrePanBis.setBackground(Color.LIGHT_GRAY);
		titrePanBis.setFont(new Font(null, Font.BOLD, 30));
		titrePanBis.setBorder(new LineBorder(Color.BLACK, 2));
		titrePanBis.setHorizontalAlignment(SwingConstants.CENTER);
		panLabBis.add(titrePanBis,gbc);

		setGbc(GridBagConstraints.HORIZONTAL, 0, 1, 2, 1, 0.5, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		JButton btPaCo = new JButton(BT_ADD_LAB_REF);
		btPaCo.addActionListener(new addLab(tabmodelRef));
		panLabBis.add(btPaCo,gbc);

		setGbc(GridBagConstraints.HORIZONTAL, 2, 1, 2, 1, 0.5, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
		JButton btLab = new JButton(BT_ADD_LAB_WK);
		btLab.addActionListener(new addLab(tabmodelWk));
		panLabBis.add(btLab,gbc);

		tabLabRef = new JTable();
		tabLabRef.setFillsViewportHeight(true);
		tabLabRef.setModel(tabmodelRef);
		tabLabRef.setDefaultRenderer(tabLabRef.getColumnClass(0), new CellTabRenderer());
		setGbc(GridBagConstraints.BOTH, 0, 2, 2, 5, 1, 1, new Insets(0,0,0,0), GridBagConstraints.CENTER);
		panLabBis.add(new JScrollPane(tabLabRef), gbc);

		tabLabWk = new JTable();
		tabLabWk.setFillsViewportHeight(true);
		tabLabWk.setModel(tabmodelWk);
		setGbc(GridBagConstraints.BOTH, 2, 2, 2, 5, 1, 1, new Insets(0,0,0,0), GridBagConstraints.CENTER);
		panLabBis.add(new JScrollPane(tabLabWk), gbc);
		
		JButton btCompar = new JButton(BT_COMPAR_LAB);
		btCompar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(arrayLabRef.size()!=arrayLabWk.size())
				{
					final int reponse = JOptionPane.showConfirmDialog(
							null,
							"Nombre de fichier différent, comparer quand même?",
							"Question",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if(reponse==JOptionPane.OK_OPTION)
					{
						System.out.println("OK");
						Lab multiLabRef = Lab.compilLab(arrayLabRef);
						Lab multiLabWk = Lab.compilLab(arrayLabWk);
					}else{
						System.out.println("nOK");
					}
				}else{
					System.out.println("Nb file OK");
					Lab multiLabRef = Lab.compilLab(arrayLabRef);
					Lab multiLabWk = Lab.compilLab(arrayLabWk);
				}
			}
		});
		setGbc(GridBagConstraints.HORIZONTAL, 0, 7, 2, 1, 0, 0, new Insets(0,0,0,0), GridBagConstraints.CENTER);
		panLabBis.add(btCompar, gbc);
		
		JButton btExport = new JButton(BT_EXPORT);
		btExport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Lab multiLabRef = Lab.compilLab(arrayLabRef);
				Lab multiLabWk = Lab.compilLab(arrayLabWk);
				Lab.ecrireRapport(multiLabRef, multiLabWk);
			}
		});
		setGbc(GridBagConstraints.HORIZONTAL, 2, 7, 2, 1, 0, 0, new Insets(0,0,0,0), GridBagConstraints.CENTER);
		panLabBis.add(btExport, gbc);
	}

	private static void setGbc(int fill,int gridx,int gridy,int gridwidth,int gridheight,double weightx,double weighty,Insets insets,int anchor)
	{
		gbc.fill = fill;
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx=weightx;
		gbc.weighty=weighty;
		gbc.insets = insets;
		gbc.anchor = anchor;
	}

	public class selLab implements ActionListener
	{		
		@Override
		public void actionPerformed(ActionEvent e) {

			final JFileChooser jFileChooser = new JFileChooser("C:/Users/tramp/Desktop/Tmp");
			jFileChooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {return null;}

				@Override
				public boolean accept(File f) {

					if (f.isDirectory()) return true;

					String extension = Utilitaire.getExtension(f);
					if (extension.equals(Utilitaire.lab))
					{return true;}
					else{return false;}	
				}
			});


			final int reponse = jFileChooser.showOpenDialog(Ihm.this);
			if (reponse == JFileChooser.APPROVE_OPTION)
			{
				if (e.getActionCommand().equals("Sélection Lab de référence"))
				{
					pathLabRef = jFileChooser.getSelectedFile().getPath();
					nameLabRef = jFileChooser.getSelectedFile().getName();
					labelLabRef.setText(nameLabRef);
					labRef = new Lab(pathLabRef);
					listRef.setListData(Lab.getTabVar(labRef.getListVariable()));
				}else{
					pathLabWk = jFileChooser.getSelectedFile().getPath();
					nameLabWk = jFileChooser.getSelectedFile().getName();
					labelLabWk.setText(nameLabWk);
					labWork = new Lab(pathLabWk);
					listWk.setListData(Lab.getTabVar(labWork.getListVariable()));
				}

			}
		}

	}

	private class addLab implements ActionListener
	{	
		private DefaultTableModel tabmodel;

		public addLab(DefaultTableModel tabmodel)
		{
			this.tabmodel = tabmodel;
		}
		@Override
		public void actionPerformed(ActionEvent e)
		{
			final JFileChooser jFileChooser = new JFileChooser("C:/Users/tramp/Desktop/Tmp/Lab");
			jFileChooser.setMultiSelectionEnabled(true);
			jFileChooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {return null;}

				@Override
				public boolean accept(File f) {

					if (f.isDirectory()) return true;

					String extension = Utilitaire.getExtension(f);
					if (extension.equals(Utilitaire.lab))
					{return true;}
					else{return false;}	
				}
			});


			final int reponse = jFileChooser.showOpenDialog(Ihm.this);
			if (reponse == JFileChooser.APPROVE_OPTION)
			{

				for(File file : jFileChooser.getSelectedFiles())
				{
					if(e.getActionCommand().equals(BT_ADD_LAB_REF))
					{
						arrayLabRef.add(new Lab(file.getPath()));
						//this.tabmodel.addRow(new String[]{file.getName()});
						this.tabmodel.addRow(new Lab[]{new Lab(file.getPath())});
					}else{
						arrayLabWk.add(new Lab(file.getPath()));
						this.tabmodel.addRow(new String[]{file.getName()});
					}

				}
			}


		}

	}

}
