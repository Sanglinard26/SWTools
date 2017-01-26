package visu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import lab.Lab;
import lab.ListModelLab;
import lab.ListModelVar;
import lab.Variable;
import observer.Observateur;
import tools.Preference;
import tools.Utilitaire;

public final class PanelLab extends JPanel {

    private static final long serialVersionUID = 1L;
    // Constante
    private static final String BT_COMPAR_LAB = "Comparer";
    private static final String BT_EXPORT = "Exporter";
    private static final String BT_ADD_LAB_REF = "Ajout lab(s) de référence";
    private static final String BT_ADD_LAB_WK = "Ajout lab(s) de travail";
    private static final String TXT_FILTRAGE = "Entrer une partie du mot pour filtrer";

    // GUI
    private final JButton btCompar, btExport;
    private static final GridBagConstraints gbc = new GridBagConstraints();
    private JList<Lab> listLabRef, listLabWk;
    private JList<Variable> listVarRef;
    private JList<Variable> listVarWk, listVarPlus, listVarMoins;
    private JTextField filterVarRef, filterVarWk;

    public PanelLab() {

        this.setLayout(new GridBagLayout());

        setGbc(GridBagConstraints.HORIZONTAL, 0, 0, 1, 1, 0, 0, new Insets(0, 0, 5, 5), GridBagConstraints.CENTER);
        JButton btAddLabRef = new JButton(BT_ADD_LAB_REF);
        btAddLabRef.addActionListener(new addLab());
        this.add(btAddLabRef, gbc);

        setGbc(GridBagConstraints.HORIZONTAL, 1, 0, 1, 1, 0, 0, new Insets(0, 5, 5, 20), GridBagConstraints.CENTER);
        JButton btAddLabWk = new JButton(BT_ADD_LAB_WK);
        btAddLabWk.addActionListener(new addLab());
        this.add(btAddLabWk, gbc);

        btCompar = new JButton(BT_COMPAR_LAB);
        btCompar.setEnabled(false);
        btCompar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (listLabRef.getModel().getSize() != listLabWk.getModel().getSize()) {
                    final int reponse = JOptionPane.showConfirmDialog(null, "Nombre de fichier différent, comparer quand même?", "Question",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reponse == JOptionPane.OK_OPTION) {
                        Lab multiLabRef = Lab.compilLab(((ListModelLab) listLabRef.getModel()).getList());
                        Lab multiLabWk = Lab.compilLab(((ListModelLab) listLabWk.getModel()).getList());

                        ((ListModelVar) listVarMoins.getModel()).setList(new Lab(multiLabRef.getDiffLab(multiLabWk)));
                        ((ListModelVar) listVarPlus.getModel()).setList(new Lab(multiLabWk.getDiffLab(multiLabRef)));
                    }
                } else {
                    Lab multiLabRef = Lab.compilLab(((ListModelLab) listLabRef.getModel()).getList());
                    Lab multiLabWk = Lab.compilLab(((ListModelLab) listLabWk.getModel()).getList());

                    ((ListModelVar) listVarMoins.getModel()).setList(new Lab(multiLabRef.getDiffLab(multiLabWk)));
                    ((ListModelVar) listVarPlus.getModel()).setList(new Lab(multiLabWk.getDiffLab(multiLabRef)));

                    btExport.setEnabled(true);
                }
            }
        });
        setGbc(GridBagConstraints.HORIZONTAL, 2, 0, 1, 1, 0, 0, new Insets(0, 20, 10, 0), GridBagConstraints.CENTER);
        this.add(btCompar, gbc);

        btExport = new JButton(BT_EXPORT);
        btExport.setEnabled(true);
        btExport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Lab multiLabRef = Lab.compilLab(((ListModelLab) listLabRef.getModel()).getList());
                Lab multiLabWk = Lab.compilLab(((ListModelLab) listLabWk.getModel()).getList());
                Lab.ecrireRapport(multiLabRef, multiLabWk);
            }
        });
        setGbc(GridBagConstraints.HORIZONTAL, 3, 0, 1, 1, 0, 0, new Insets(0, 0, 10, 0), GridBagConstraints.CENTER);
        this.add(btExport, gbc);

        // Liste des lab r�f
        setGbc(GridBagConstraints.BOTH, 0, 1, 1, 2, 1, 0.3, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
        listLabRef = new JList<Lab>();
        listLabRef.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // non utilisee
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // non utilisee
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 127 & listLabRef.getSelectedIndex() > -1) // touche suppr
                {
                    ((ListModelLab) listLabRef.getModel()).removeLab(listLabRef.getSelectedIndex());
                    listLabRef.clearSelection();
                    ((ListModelVar) listVarRef.getModel()).clearList();
                    listVarRef.clearSelection();
                }

            }
        });
        listLabRef.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listLabRef.setCellRenderer(new ListLabRenderer());
        listLabRef.setModel(new ListModelLab());
        ((ListModelLab) listLabRef.getModel()).addObservateur(new Observateur() {

            @Override
            public void update(ArrayList<Lab> listLab, String typeAction) {
                System.out.println("Udapte : " + typeAction + " Nb Lab : " + listLab.size());
                if (!listLab.isEmpty()) {
                    btCompar.setEnabled(true);
                    filterVarRef.setEditable(true);
                } else {
                    btCompar.setEnabled(false);
                    filterVarRef.setText(TXT_FILTRAGE);
                    filterVarRef.setEditable(false);
                }

            }
        });
        listLabRef.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false & !listLabRef.isSelectionEmpty()) {
                    filterVarRef.setText("");
                    ((ListModelVar) listVarRef.getModel()).setList(listLabRef.getSelectedValue());
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
                                // filterVarRef.setText("");
                                ((ListModelVar) listVarRef.getModel()).clearList();
                                listVarRef.clearSelection();
                            }
                        });

                    } else {
                        menuItem = new JMenuItem("Tout supprimer");
                        menuItem.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ((ListModelLab) listLabRef.getModel()).clearList();
                                // filterVarRef.setText("");
                                listLabRef.clearSelection();
                                ((ListModelVar) listVarRef.getModel()).clearList();
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
        setGbc(GridBagConstraints.BOTH, 1, 1, 1, 2, 1, 0.3, new Insets(0, 5, 0, 20), GridBagConstraints.CENTER);
        listLabWk = new JList<Lab>();
        listLabWk.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // non utilisee
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // non utilisee
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 127 & listLabWk.getSelectedIndex() > -1) // touche suppr
                {
                    ((ListModelLab) listLabWk.getModel()).removeLab(listLabWk.getSelectedIndex());
                    listLabWk.clearSelection();
                    ((ListModelVar) listVarWk.getModel()).clearList();
                    listVarWk.clearSelection();
                }

            }
        });
        listLabWk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listLabWk.setCellRenderer(new ListLabRenderer());
        listLabWk.setModel(new ListModelLab());
        listLabWk.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false & !listLabWk.isSelectionEmpty()) {
                    filterVarWk.setText("");
                    ((ListModelVar) listVarWk.getModel()).setList(listLabWk.getSelectedValue());
                }
            }
        });
        listLabWk.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() & listLabWk.getModel().getSize() > 0) {
                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem menuItem;
                    if (listLabWk.locationToIndex(e.getPoint()) == listLabWk.getSelectedIndex()) {

                        menuItem = new JMenuItem("Supprimer");
                        menuItem.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ((ListModelLab) listLabWk.getModel()).removeLab(listLabWk.getSelectedIndex());
                                listLabWk.clearSelection();
                                filterVarWk.setText("");
                                ((ListModelVar) listVarWk.getModel()).clearList();
                                listVarWk.clearSelection();
                            }
                        });

                    } else {
                        menuItem = new JMenuItem("Tout supprimer");
                        menuItem.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                ((ListModelLab) listLabWk.getModel()).clearList();
                                filterVarWk.setText("");
                                listLabWk.clearSelection();
                                ((ListModelVar) listVarWk.getModel()).clearList();
                            }
                        });

                    }
                    menu.add(menuItem);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        JScrollPane scrollListLabWk = new JScrollPane(listLabWk);
        scrollListLabWk.setBorder(new LineBorder(Color.DARK_GRAY, 1, false));
        this.add(scrollListLabWk, gbc);

        setGbc(GridBagConstraints.NONE, 2, 1, 1, 1, 0, 0, new Insets(0, 20, 0, 0), GridBagConstraints.CENTER);
        this.add(new JLabel("Label(s) disparu(s)"), gbc);

        setGbc(GridBagConstraints.NONE, 3, 1, 1, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        this.add(new JLabel("Label(s) suppl�mentaire(s)"), gbc);

        filterVarRef = new JTextField(TXT_FILTRAGE, 20);
        filterVarRef.setEditable(false);
        filterVarRef.setFont(new Font(null, Font.ITALIC, 12));
        filterVarRef.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (((JTextField) e.getSource()).isEditable())
                    ((JTextField) e.getSource()).setText("");
            }
        });
        filterVarRef.setBorder(new LineBorder(Color.BLACK, 1, false));
        filterVarRef.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                ((ListModelVar) listVarRef.getModel()).setFilter(filterVarRef.getText().toLowerCase());

            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                ((ListModelVar) listVarRef.getModel()).setFilter(filterVarRef.getText().toLowerCase());

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Non utilisee
            }
        });
        setGbc(GridBagConstraints.HORIZONTAL, 0, 3, 1, 1, 0, 0, new Insets(10, 0, 0, 5), GridBagConstraints.CENTER);
        this.add(filterVarRef, gbc);

        filterVarWk = new JTextField(TXT_FILTRAGE, 20);
        filterVarWk.setFont(new Font(null, Font.ITALIC, 12));
        filterVarWk.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((JTextField) e.getSource()).setText("");
            }
        });
        filterVarWk.setBorder(new LineBorder(Color.BLACK, 1, false));
        filterVarWk.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                ((ListModelVar) listVarWk.getModel()).setFilter(filterVarWk.getText().toLowerCase());

            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                ((ListModelVar) listVarWk.getModel()).setFilter(filterVarWk.getText().toLowerCase());

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Non utilisee
            }
        });
        setGbc(GridBagConstraints.HORIZONTAL, 1, 3, 1, 1, 0, 0, new Insets(10, 5, 0, 20), GridBagConstraints.CENTER);
        this.add(filterVarWk, gbc);

        // Liste du lab r�f
        setGbc(GridBagConstraints.BOTH, 0, 4, 1, 1, 1, 1, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
        listVarRef = new JList<Variable>();
        listVarRef.setModel(new ListModelVar());
        listVarRef.setCellRenderer(new ListVarRenderer());
        JScrollPane scrollPaneRef = new JScrollPane(listVarRef);
        scrollPaneRef.setMinimumSize(new Dimension(200, 600));
        this.add(scrollPaneRef, gbc);

        // Liste du lab de travail
        setGbc(GridBagConstraints.BOTH, 1, 4, 1, 1, 1, 1, new Insets(0, 5, 0, 20), GridBagConstraints.CENTER);
        listVarWk = new JList<Variable>();
        listVarWk.setModel(new ListModelVar());
        listVarWk.setCellRenderer(new ListVarRenderer());
        JScrollPane scrollPaneWk = new JScrollPane(listVarWk);
        scrollPaneWk.setMinimumSize(new Dimension(200, 600));
        this.add(scrollPaneWk, gbc);

        setGbc(GridBagConstraints.BOTH, 2, 2, 1, 3, 1, 1, new Insets(0, 20, 0, 0), GridBagConstraints.CENTER);
        listVarMoins = new JList<Variable>();
        listVarMoins.setModel(new ListModelVar());
        listVarMoins.setCellRenderer(new ListVarRenderer());
        JScrollPane scrollPaneMoins = new JScrollPane(listVarMoins);
        scrollPaneMoins.setMinimumSize(new Dimension(200, 600));
        this.add(scrollPaneMoins, gbc);

        setGbc(GridBagConstraints.BOTH, 3, 2, 1, 3, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        listVarPlus = new JList<Variable>();
        listVarPlus.setModel(new ListModelVar());
        listVarPlus.setCellRenderer(new ListVarRenderer());
        JScrollPane scrollPanePlus = new JScrollPane(listVarPlus);
        scrollPanePlus.setMinimumSize(new Dimension(200, 600));
        this.add(scrollPanePlus, gbc);

    }

    private class addLab implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser jFileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_ADD_LAB));
            jFileChooser.setMultiSelectionEnabled(true);
            jFileChooser.setFileFilter(new FileFilter() {

                @Override
                public String getDescription() {
                    return "Fichier *.lab";
                }

                @Override
                public boolean accept(File f) {

                    if (f.isDirectory())
                        return true;

                    String extension = Utilitaire.getExtension(f);
                    if (extension.equals(Utilitaire.lab)) {
                        return true;
                    }
                    return false;
                }
            });

            final int reponse = jFileChooser.showOpenDialog(PanelLab.this);
            if (reponse == JFileChooser.APPROVE_OPTION) {
                for (File file : jFileChooser.getSelectedFiles()) {
                    Lab newLab = new Lab(file.getPath());
                    if (e.getActionCommand().equals(BT_ADD_LAB_REF)) {
                        ((ListModelLab) listLabRef.getModel()).addLab(newLab);
                    } else {
                        ((ListModelLab) listLabWk.getModel()).addLab(newLab);
                    }
                }
                // btCompar.setEnabled(true);
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
