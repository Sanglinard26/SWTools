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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import lab.Lab;
import lab.ListModelLab;
import lab.Variable;
import tools.Preference;
import tools.Utilitaire;

public final class PanelLab extends JPanel {

    private static final long serialVersionUID = 1L;
    // Constante
    private static final String BT_COMPAR_LAB = "Comparer";
    private static final String BT_EXPORT = "Exporter";
    private static final String BT_ADD_LAB_REF = "Ajout lab(s) de référence";
    private static final String BT_SUP_LAB_REF = "Vider la liste";
    private static final String BT_ADD_LAB_WK = "Ajout lab(s) de travail";

    // GUI
    private final JButton btCompar, btExport;
    private static final GridBagConstraints gbc = new GridBagConstraints();
    private JList<Lab> listLabRef;
    private JList<Lab> listLabWk;
    private ListModelLab modelLabRef = new ListModelLab();
    private ListModelLab modelLabWk = new ListModelLab();
    private JList<Variable> listRef;
    private JList<Variable> listWk;
    private JList<Variable> listPlus;
    private JList<Variable> listMoins;

    public PanelLab() {
        this.setLayout(new GridBagLayout());

        setGbc(GridBagConstraints.HORIZONTAL, 0, 0, 1, 1, 0.5, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        JButton btPaCo = new JButton(BT_ADD_LAB_REF);
        btPaCo.addActionListener(new addLab());
        this.add(btPaCo, gbc);

        setGbc(GridBagConstraints.HORIZONTAL, 2, 0, 1, 1, 0.5, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        JButton btLab = new JButton(BT_ADD_LAB_WK);
        btLab.addActionListener(new addLab());
        this.add(btLab, gbc);

        btCompar = new JButton(BT_COMPAR_LAB);
        btCompar.setEnabled(false);
        btCompar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (modelLabRef.getSize() != modelLabWk.getSize()) {
                    final int reponse = JOptionPane.showConfirmDialog(null, "Nombre de fichier différent, comparer quand même?", "Question",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reponse == JOptionPane.OK_OPTION) {
                        Lab multiLabRef = Lab.compilLab(modelLabRef.getList());
                        Lab multiLabWk = Lab.compilLab(modelLabWk.getList());

                        listMoins.setListData(Lab.getTabVar(multiLabRef.getDiffLab(multiLabWk)));
                        listPlus.setListData(Lab.getTabVar(multiLabWk.getDiffLab(multiLabRef)));
                    }
                } else {
                    Lab multiLabRef = Lab.compilLab(modelLabRef.getList());
                    Lab multiLabWk = Lab.compilLab(modelLabWk.getList());

                    listMoins.setListData(Lab.getTabVar(multiLabRef.getDiffLab(multiLabWk)));
                    listPlus.setListData(Lab.getTabVar(multiLabWk.getDiffLab(multiLabRef)));

                    btExport.setEnabled(true);
                }
            }
        });
        setGbc(GridBagConstraints.HORIZONTAL, 4, 0, 2, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        this.add(btCompar, gbc);

        // Liste des lab r�f
        setGbc(GridBagConstraints.BOTH, 0, 1, 2, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        listLabRef = new JList<Lab>();
        listLabRef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listLabRef.setCellRenderer(new ListLabRenderer());
        listLabRef.setModel(modelLabRef);
        listLabRef.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false & !listLabRef.isSelectionEmpty()) {
                    listRef.setListData(Lab.getTabVar(listLabRef.getSelectedValue().getListVariable()));
                }
            }
        });
        listLabRef.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() & listLabRef.getModel().getSize() > 0) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem menuItem;
                    if (listLabRef.locationToIndex(e.getPoint()) == listLabRef.getSelectedIndex()) {

                        menuItem = new JMenuItem("Supprimer");
                        menuItem.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ((ListModelLab) listLabRef.getModel()).removeLab(listLabRef.getSelectedIndex());
                                listLabRef.clearSelection();
                                listRef.setListData(new Variable[0]);
                            }
                        });

                    } else {
                        menuItem = new JMenuItem("Tout supprimer");
                        menuItem.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ((ListModelLab) listLabRef.getModel()).clearList();
                                listLabRef.clearSelection();
                                listRef.setListData(new Variable[0]);
                            }
                        });

                    }
                    menu.add(menuItem);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        this.add(new JScrollPane(listLabRef), gbc);

        // Liste des lab travail
        setGbc(GridBagConstraints.BOTH, 2, 1, 2, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        listLabWk = new JList<Lab>();
        listLabWk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listLabWk.setCellRenderer(new ListLabRenderer());
        listLabWk.setModel(modelLabWk);
        listLabWk.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    listWk.setListData(Lab.getTabVar(listLabWk.getSelectedValue().getListVariable()));
                }
            }
        });
        this.add(new JScrollPane(listLabWk), gbc);

        setGbc(GridBagConstraints.NONE, 4, 1, 1, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        this.add(new JLabel("Label(s) disparu(s)"), gbc);

        setGbc(GridBagConstraints.NONE, 5, 1, 1, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        this.add(new JLabel("Label(s) suppl�mentaire(s)"), gbc);

        // Liste du lab r�f
        setGbc(GridBagConstraints.BOTH, 0, 2, 2, 3, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        listRef = new JList<Variable>();
        listRef.setCellRenderer(new ListVarRenderer());
        JScrollPane scrollPaneRef = new JScrollPane(listRef);
        scrollPaneRef.setMinimumSize(new Dimension(200, 600));
        this.add(scrollPaneRef, gbc);

        // Liste du lab de travail
        setGbc(GridBagConstraints.BOTH, 2, 2, 2, 3, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        listWk = new JList<Variable>();
        listWk.setCellRenderer(new ListVarRenderer());
        JScrollPane scrollPaneWk = new JScrollPane(listWk);
        scrollPaneWk.setMinimumSize(new Dimension(200, 600));
        this.add(scrollPaneWk, gbc);

        setGbc(GridBagConstraints.BOTH, 4, 2, 1, 4, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        listMoins = new JList<Variable>();
        listMoins.setCellRenderer(new ListVarRenderer());
        JScrollPane scrollPaneMoins = new JScrollPane(listMoins);
        scrollPaneMoins.setMinimumSize(new Dimension(200, 600));
        this.add(scrollPaneMoins, gbc);

        setGbc(GridBagConstraints.BOTH, 5, 2, 1, 4, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        listPlus = new JList<Variable>();
        listPlus.setCellRenderer(new ListVarRenderer());
        JScrollPane scrollPanePlus = new JScrollPane(listPlus);
        scrollPanePlus.setMinimumSize(new Dimension(200, 600));
        this.add(scrollPanePlus, gbc);

        btExport = new JButton(BT_EXPORT);
        btExport.setEnabled(true);
        btExport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Lab multiLabRef = Lab.compilLab(modelLabRef.getList());
                Lab multiLabWk = Lab.compilLab(modelLabWk.getList());
                Lab.ecrireRapport(multiLabRef, multiLabWk);
            }
        });
        setGbc(GridBagConstraints.HORIZONTAL, 4, 6, 2, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        this.add(btExport, gbc);

    }

    private class addLab implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser jFileChooser = new JFileChooser(Preference.getPreference("pathLab"));
            jFileChooser.setMultiSelectionEnabled(true);
            jFileChooser.setFileFilter(new FileFilter() {

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public boolean accept(File f) {

                    if (f.isDirectory())
                        return true;

                    String extension = Utilitaire.getExtension(f);
                    if (extension.equals(Utilitaire.lab)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            final int reponse = jFileChooser.showOpenDialog(PanelLab.this);
            if (reponse == JFileChooser.APPROVE_OPTION) {
                for (File file : jFileChooser.getSelectedFiles()) {
                    Lab newLab = new Lab(file.getPath());
                    if (e.getActionCommand().equals(BT_ADD_LAB_REF)) {
                        modelLabRef.addLab(newLab);
                    } else {
                        modelLabWk.addLab(newLab);
                    }
                }
                btCompar.setEnabled(true);
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

}
