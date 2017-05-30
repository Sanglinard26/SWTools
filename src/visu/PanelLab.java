package visu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
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
import tools.Preference;
import tools.Utilitaire;

public final class PanelLab extends JPanel implements ListDataListener {

    private static final long serialVersionUID = 1L;
    // Constante
    private static final String BT_COMPAR_LAB = "Comparer";
    private static final String BT_EXPORT = "Exporter";
    private static final String BT_ADD_LAB_REF = "Ajout lab(s) de reference";
    private static final String BT_ADD_LAB_WK = "Ajout lab(s) de travail";
    private static final String TXT_FILTRAGE = "Entrer une partie du mot pour filtrer";

    // GUI
    private final JButton btAddLabRef, btAddLabWk, btCompar, btExport;
    private static final GridBagConstraints gbc = new GridBagConstraints();
    private final ListLab listLabRef;
    private final ListLab listLabWk;
    private final ListVar listVarRef;
    private final ListVar listVarWk, listVarPlus, listVarMoins;
    private final JTextField filterVarRef, filterVarWk;

    public PanelLab() {

        this.setLayout(new GridBagLayout());

        setGbc(GridBagConstraints.HORIZONTAL, 0, 0, 1, 1, 0, 0, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
        btAddLabRef = new JButton(BT_ADD_LAB_REF);
        btAddLabRef.setOpaque(false);
        btAddLabRef.addActionListener(new addLab());
        this.add(btAddLabRef, gbc);

        setGbc(GridBagConstraints.HORIZONTAL, 1, 0, 1, 1, 0, 0, new Insets(0, 5, 0, 20), GridBagConstraints.CENTER);
        btAddLabWk = new JButton(BT_ADD_LAB_WK);
        btAddLabWk.setOpaque(false);
        btAddLabWk.addActionListener(new addLab());
        this.add(btAddLabWk, gbc);

        btCompar = new JButton(BT_COMPAR_LAB);
        btCompar.setOpaque(false);
        btCompar.setEnabled(false);
        btCompar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (listLabRef.getModel().getSize() == listLabWk.getModel().getSize()) {

                    final Lab multiLabRef = Lab.compilLab(listLabRef.getModel().getList());
                    final Lab multiLabWk = Lab.compilLab(listLabWk.getModel().getList());

                    if (multiLabRef.getDiffLab(multiLabWk).size() != 0 | multiLabWk.getDiffLab(multiLabRef).size() != 0) {
                        listVarMoins.getModel().setList(new Lab(multiLabRef.getDiffLab(multiLabWk)));
                        listVarPlus.getModel().setList(new Lab(multiLabWk.getDiffLab(multiLabRef)));
                    } else {
                        JOptionPane.showMessageDialog(null, "Les fichiers Lab sont identiques !", "RESULTAT", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Le nombre de fichier a comparer est different !");
                }
            }
        });
        setGbc(GridBagConstraints.HORIZONTAL, 2, 0, 1, 1, 0, 0, new Insets(0, 20, 0, 0), GridBagConstraints.CENTER);
        this.add(btCompar, gbc);

        btExport = new JButton(BT_EXPORT);
        btExport.setOpaque(false);
        btExport.setEnabled(false);
        btExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lab.ecrireRapport(Lab.compilLab(listLabRef.getModel().getList()), Lab.compilLab(listLabWk.getModel().getList()));
            }
        });
        setGbc(GridBagConstraints.HORIZONTAL, 3, 0, 1, 1, 0, 0, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        this.add(btExport, gbc);

        // Liste des lab ref
        setGbc(GridBagConstraints.BOTH, 0, 1, 1, 2, 1, 0.3, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
        listLabRef = new ListLab(new ListModelLab());
        listLabRef.getModel().addListDataListener(this);
        listLabRef.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false & !listLabRef.isSelectionEmpty()) {
                    filterVarRef.setText("");
                    listVarRef.getModel().setList(listLabRef.getSelectedValue());
                }
            }
        });
        this.add(new JScrollPane(listLabRef), gbc);

        // Liste des lab travail
        setGbc(GridBagConstraints.BOTH, 1, 1, 1, 2, 1, 0.3, new Insets(0, 5, 0, 20), GridBagConstraints.CENTER);
        listLabWk = new ListLab(new ListModelLab());
        listLabWk.getModel().addListDataListener(this);
        listLabWk.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false & !listLabWk.isSelectionEmpty()) {
                    filterVarWk.setText("");
                    listVarWk.getModel().setList(listLabWk.getSelectedValue());
                }
            }
        });
        this.add(new JScrollPane(listLabWk), gbc);

        setGbc(GridBagConstraints.NONE, 2, 1, 1, 1, 0, 0, new Insets(10, 20, 0, 0), GridBagConstraints.CENTER);
        this.add(new JLabel("Label(s) disparu(s)"), gbc);

        setGbc(GridBagConstraints.NONE, 3, 1, 1, 1, 0, 0, new Insets(10, 0, 0, 0), GridBagConstraints.CENTER);
        this.add(new JLabel("Label(s) supplementaire(s)"), gbc);

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
        this.add(filterVarRef, gbc);

        filterVarWk = new JTextField(TXT_FILTRAGE, 20);
        filterVarWk.setEditable(false);
        filterVarWk.setFont(new Font(null, Font.ITALIC, 12));
        filterVarWk.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (((JTextField) e.getSource()).isEditable())
                    ((JTextField) e.getSource()).setText("");
            }
        });
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
        this.add(filterVarWk, gbc);

        // Liste du lab ref
        setGbc(GridBagConstraints.BOTH, 0, 4, 1, 1, 1, 1, new Insets(0, 0, 0, 5), GridBagConstraints.CENTER);
        listVarRef = new ListVar(new ListModelVar());
        this.add(new JScrollPane(listVarRef), gbc);

        // Liste du lab de travail
        setGbc(GridBagConstraints.BOTH, 1, 4, 1, 1, 1, 1, new Insets(0, 5, 0, 20), GridBagConstraints.CENTER);
        listVarWk = new ListVar(new ListModelVar());
        this.add(new JScrollPane(listVarWk), gbc);

        setGbc(GridBagConstraints.BOTH, 2, 2, 1, 3, 1, 1, new Insets(0, 20, 0, 0), GridBagConstraints.CENTER);
        listVarMoins = new ListVar(new ListModelVar());
        listVarMoins.getModel().addListDataListener(this);
        this.add(new JScrollPane(listVarMoins), gbc);

        setGbc(GridBagConstraints.BOTH, 3, 2, 1, 3, 1, 1, new Insets(0, 0, 0, 0), GridBagConstraints.CENTER);
        listVarPlus = new ListVar(new ListModelVar());
        listVarPlus.getModel().addListDataListener(this);
        this.add(new JScrollPane(listVarPlus), gbc);

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
                    return "Fichier *.lab";
                }

                @Override
                public boolean accept(File f) {

                    if (f.isDirectory())
                        return true;

                    String extension = Utilitaire.getExtension(f);
                    // Solutionner le NPE
                    if (extension.equals(Utilitaire.lab)) {
                        return true;
                    }
                    return false;
                }
            });

            final int reponse = jFileChooser.showOpenDialog(PanelLab.this);
            if (reponse == JFileChooser.APPROVE_OPTION) {
                for (File file : jFileChooser.getSelectedFiles()) {
                    if (e.getActionCommand().equals(BT_ADD_LAB_REF)) {
                        listLabRef.getModel().addLab(new Lab(file.getPath()));
                    } else {
                        listLabWk.getModel().addLab(new Lab(file.getPath()));
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
            filterVarRef.setText(TXT_FILTRAGE);

            // Condition pour autoriser le filtrage de label wk
            listVarWk.getModel().clearList();
            filterVarWk.setText(TXT_FILTRAGE);

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
                filterVarRef.setText(TXT_FILTRAGE);
                filterVarRef.setEditable(false);
            }

            // Condition pour autoriser le filtrage de label wk
            if (listLabWk.getModel().getSize() > 0) {
                filterVarWk.setEditable(true);
            } else {
                listVarWk.getModel().clearList();
                filterVarWk.setText(TXT_FILTRAGE);
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
        }
    }

}
