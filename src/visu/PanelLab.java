package visu;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import lab.Lab;
import lab.Variable;
import tools.Utilitaire;


public final class PanelLab extends JPanel {


	private static final long serialVersionUID = 1L;
	//Constante
	private static final String BT_COMPAR_LAB = "Comparer";
	private static final String BT_EXPORT = "Exporter";
	private static final String BT_ADD_LAB_REF = "Ajout lab(s) de référence";
	private static final String BT_ADD_LAB_WK = "Ajout lab(s) de travail";

	//GUI
	private static final GridBagConstraints gbc = new GridBagConstraints();
	private JList<Variable> listRef;
	private JList<Variable> listWk;
	private JList<Variable> listPlus;
	private JList<Variable> listMoins;

	//Variable multi lab
	private JTable tabLabRef;
	private JTable tabLabWk;
	private DefaultTableModel tabmodelRef = new DefaultTableModel(new Object[]{"Lab réf"}, 0);
	private DefaultTableModel tabmodelWk = new DefaultTableModel(new Object[]{"Lab work"}, 0);
	private ArrayList<Lab> arrayLabRef = new ArrayList<Lab>();
	private ArrayList<Lab> arrayLabWk = new ArrayList<Lab>();

	public PanelLab()
	{
		this.setLayout(new GridBagLayout());

		setGbc(GridBagConstraints.HORIZONTAL, 0, 0, 1, 1, 0.5, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		JButton btPaCo = new JButton(BT_ADD_LAB_REF);
		btPaCo.addActionListener(new addLab(tabmodelRef));
		this.add(btPaCo,gbc);

		setGbc(GridBagConstraints.HORIZONTAL, 1, 0, 1, 1, 0.5, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		JButton btLab = new JButton(BT_ADD_LAB_WK);
		btLab.addActionListener(new addLab(tabmodelWk));
		this.add(btLab,gbc);

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
						Lab multiLabRef = Lab.compilLab(arrayLabRef);
						Lab multiLabWk = Lab.compilLab(arrayLabWk);

						listMoins.setListData(Lab.getTabVar(multiLabRef.getDiffLab(multiLabWk)));
						listPlus.setListData(Lab.getTabVar(multiLabWk.getDiffLab(multiLabRef)));
					}
				}else{
					Lab multiLabRef = Lab.compilLab(arrayLabRef);
					Lab multiLabWk = Lab.compilLab(arrayLabWk);

					listMoins.setListData(Lab.getTabVar(multiLabRef.getDiffLab(multiLabWk)));
					listPlus.setListData(Lab.getTabVar(multiLabWk.getDiffLab(multiLabRef)));
				}
			}
		});
		setGbc(GridBagConstraints.HORIZONTAL, 2, 0, 2, 1, 0, 0, new Insets(0,0,0,0), GridBagConstraints.CENTER);
		this.add(btCompar, gbc);

		tabLabRef = new JTable();
		tabLabRef.setFillsViewportHeight(true);
		tabLabRef.setMinimumSize(new Dimension(200, 400));
		tabLabRef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabLabRef.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2)
				{
					tabmodelRef.removeRow(tabLabRef.getSelectedRow());
					//listRef.setListData(new Variable[0]);
					DefaultListModel<Variable> listModel = (DefaultListModel<Variable>) listRef.getModel();
					listModel.clear();
				}else{
					listRef.setListData(Lab.getTabVar(arrayLabRef.get(tabLabRef.getSelectedRow()).getListVariable()));
				}
			}
		});
		tabLabRef.setModel(tabmodelRef);
		setGbc(GridBagConstraints.BOTH, 0, 1, 1, 3, 1, 1, new Insets(0,0,0,0), GridBagConstraints.CENTER);
		this.add(new JScrollPane(tabLabRef), gbc);

		tabLabWk = new JTable();
		tabLabWk.setFillsViewportHeight(true);
		tabLabWk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabLabWk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				listWk.setListData(Lab.getTabVar(arrayLabWk.get(tabLabWk.getSelectedRow()).getListVariable()));
			}
		});
		tabLabWk.setModel(tabmodelWk);
		setGbc(GridBagConstraints.BOTH, 1, 1, 1, 3, 1, 1, new Insets(0,0,0,0), GridBagConstraints.CENTER);
		this.add(new JScrollPane(tabLabWk), gbc);

		setGbc(GridBagConstraints.NONE, 2, 1, 1, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		this.add(new JLabel("Label(s) disparu(s)"),gbc);

		setGbc(GridBagConstraints.NONE, 3, 1, 1, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		this.add(new JLabel("Label(s) supplémentaire(s)"),gbc);

		//Liste du lab réf
		setGbc(GridBagConstraints.BOTH, 0, 4, 1, 3, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		listRef = new JList<Variable>();
		listRef.setCellRenderer(new ListVarRenderer());
		JScrollPane scrollPaneRef = new JScrollPane(listRef);
		scrollPaneRef.setMinimumSize(new Dimension(200, 600));
		this.add(scrollPaneRef,gbc);

		//Liste du lab de travail
		setGbc(GridBagConstraints.BOTH, 1, 4, 1, 3, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		listWk = new JList<Variable>();
		listWk.setCellRenderer(new ListVarRenderer());
		JScrollPane scrollPaneWk = new JScrollPane(listWk);
		scrollPaneWk.setMinimumSize(new Dimension(200, 600));
		this.add(scrollPaneWk,gbc);

		setGbc(GridBagConstraints.BOTH, 2, 2, 1, 4, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		listMoins = new JList<Variable>();
		listMoins.setCellRenderer(new ListVarRenderer());
		JScrollPane scrollPaneMoins = new JScrollPane(listMoins);
		scrollPaneMoins.setMinimumSize(new Dimension(200, 600));
		this.add(scrollPaneMoins,gbc);

		setGbc(GridBagConstraints.BOTH, 3, 2, 1, 4, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
		listPlus = new JList<Variable>();
		listPlus.setCellRenderer(new ListVarRenderer());
		JScrollPane scrollPanePlus = new JScrollPane(listPlus);
		scrollPanePlus.setMinimumSize(new Dimension(200, 600));
		this.add(scrollPanePlus,gbc);



		JButton btExport = new JButton(BT_EXPORT);
		btExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Lab multiLabRef = Lab.compilLab(arrayLabRef);
				Lab multiLabWk = Lab.compilLab(arrayLabWk);
				Lab.ecrireRapport(multiLabRef, multiLabWk);
			}
		});
		setGbc(GridBagConstraints.HORIZONTAL, 2, 6, 2, 1, 0, 0, new Insets(0,0,0,0), GridBagConstraints.CENTER);
		this.add(btExport, gbc);

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


			final int reponse = jFileChooser.showOpenDialog(PanelLab.this);
			if (reponse == JFileChooser.APPROVE_OPTION)
			{
				for(File file : jFileChooser.getSelectedFiles())
				{
					Lab newLab = new Lab(file.getPath());
					if(e.getActionCommand().equals(BT_ADD_LAB_REF))
					{
						if(!(arrayLabRef.contains(newLab)))
							{
								arrayLabRef.add(new Lab(file.getPath()));
								this.tabmodel.addRow(new Lab[]{new Lab(file.getPath())});
							}
					}else{
						if(!(arrayLabWk.contains(newLab)))
						{
							arrayLabWk.add(new Lab(file.getPath()));
							this.tabmodel.addRow(new String[]{file.getName()});
						}
					}
				}
			}
		}
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

}
