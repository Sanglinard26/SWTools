/*
 * Creation : 6 avr. 2017
 */
package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import cdf.Cdf;
import cdf.CdfUtils;
import cdf.ListModelCdf;
import utils.Preference;

public final class ListCdf extends JList<Cdf> implements KeyListener {

    private static final long serialVersionUID = 1L;

    private static final String ICON_EXCEL = "/excel_icon_24.png";
    private static final String ICON_TEXT = "/text_icon_24.png";
    private static final String ICON_MATLAB = "/matlab_icon_24.png";
    private static final String ICON_TRASH = "/corbeille_icon_24.png";
    private static final String ICON_COMPARAISON = "/comparaison_icon_24.png";
    private static final String ICON_UP = "/up_icon_24.png";
    private static final String ICON_DOWN = "/down_icon_24.png";

    public ListCdf(ListModelCdf dataModel) {
        super(dataModel);
        setCellRenderer(new ListCdfRenderer());
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        addKeyListener(this);
        addMouseListener(new ListMouseListener());

        // Activatation DnD
        setDropMode(DropMode.INSERT);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        super.paintComponent(g);

        final DropLocation loc = getDropLocation();
        if (loc == null) {
            setBackground(Color.WHITE);
            setBorder(null);
            return;
        }

        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, Color.LIGHT_GRAY, Color.GRAY));

        Point2D center = new Point2D.Float(loc.getDropPoint().x, loc.getDropPoint().y);
        float radius = 72f;
        float[] dist = { 0.0f, 1f };
        Color[] colors = { Color.WHITE, UIManager.getLookAndFeel().getDefaults().getColor("textHighlight") };
        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        g2.setPaint(p);

        if (getModel().getSize() > 0) {
            g2.fillRect(0, (int) getCellBounds(0, getModel().getSize() - 1).getHeight(), getWidth(),
                    (int) (getHeight() - getCellBounds(0, getModel().getSize() - 1).getHeight()));
        } else {
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.dispose();
    }

    @Override
    public ListModelCdf getModel() {
        return (ListModelCdf) super.getModel();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 127 && this.getSelectedIndex() > -1) // touche suppr
        {
            for (@SuppressWarnings("unused")
            int idx : this.getSelectedIndices()) {
                this.getModel().removeCdf(this.getSelectedIndex());
            }

            this.clearSelection();
            PanelCDF.razUI();
        }

        final int moveMe = ListCdf.this.getSelectedIndex();

        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_UP) {

            if (moveMe != 0) {
                swap(moveMe, moveMe - 1);
                ListCdf.this.setSelectedIndex(moveMe - 1);
                ListCdf.this.ensureIndexIsVisible(moveMe - 1);
            }

        }

        if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_DOWN) {

            if (moveMe != getModel().getSize() - 1) {
                swap(moveMe, moveMe + 1);
                ListCdf.this.setSelectedIndex(moveMe + 1);
                ListCdf.this.ensureIndexIsVisible(moveMe + 1);
            }

        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private final class ListMouseListener extends MouseAdapter {
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger() && ListCdf.this.getModel().getSize() > 0 && ListCdf.this.getSelectedIndices().length <= 1) {
                final JPopupMenu menu = new JPopupMenu();
                final JMenu menuMove = new JMenu("Deplacer");
                final JMenu menuExport = new JMenu("Export");
                JMenuItem menuItem;
                if (ListCdf.this.locationToIndex(e.getPoint()) == ListCdf.this.getSelectedIndex()) {

                    menuItem = new JMenuItem("Supprimer ce fichier", new ImageIcon(getClass().getResource(ICON_TRASH)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ListCdf.this.getModel().removeCdf(ListCdf.this.getSelectedIndex());
                            ListCdf.this.clearSelection();
                            PanelCDF.razUI();
                        }
                    });
                    menu.add(menuItem);
                    menu.addSeparator();

                    menuItem = new JMenuItem("Monter", new ImageIcon(getClass().getResource(ICON_UP)));
                    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_MASK));
                    menuItem.addActionListener(new UpDownListener());
                    menuMove.add(menuItem);

                    menuMove.addSeparator();

                    menuItem = new JMenuItem("Descendre", new ImageIcon(getClass().getResource(ICON_DOWN)));
                    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_MASK));
                    menuItem.addActionListener(new UpDownListener());
                    menuMove.add(menuItem);

                    menuItem = new JMenuItem("Exporter le fichier en txt", new ImageIcon(getClass().getResource(ICON_TEXT)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_CDF));
                            fileChooser.setDialogTitle("Enregistement du fichier");
                            fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier texte (*.txt)", "txt"));
                            fileChooser.setSelectedFile(new File(ListCdf.this.getSelectedValue() + ".txt"));
                            final int rep = fileChooser.showSaveDialog(null);

                            if (rep == JFileChooser.APPROVE_OPTION) {

                                boolean result = false;

                                if (!fileChooser.getSelectedFile().exists()) {

                                    result = CdfUtils.toText(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());

                                } else {

                                    switch (JOptionPane.showConfirmDialog(null, "Le fichier existe deja, ecraser?", null,
                                            JOptionPane.INFORMATION_MESSAGE)) {
                                    case JOptionPane.OK_OPTION:
                                        result = CdfUtils.toText(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());
                                        break;
                                    case JOptionPane.NO_OPTION:
                                        this.actionPerformed(e);
                                        return;
                                    default:
                                        break;
                                    }
                                }

                                if (result) {

                                    final int reponse = JOptionPane.showConfirmDialog(null,
                                            "Export termine !\n" + fileChooser.getSelectedFile() + "\nVoulez-vous ouvrir le fichier?", null,
                                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                    switch (reponse) {
                                    case JOptionPane.OK_OPTION:
                                        try {
                                            if (Desktop.isDesktopSupported()) {
                                                Desktop.getDesktop().open(fileChooser.getSelectedFile());
                                            }
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        break;
                                    case JOptionPane.NO_OPTION:
                                        break;
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Export abandonne !");
                                }
                            }
                        }
                    });
                    menuExport.add(menuItem);

                    menuExport.addSeparator();
                    menuItem = new JMenuItem("Exporter le fichier en xls", new ImageIcon(getClass().getResource(ICON_EXCEL)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (true) {
                                final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_CDF));
                                fileChooser.setDialogTitle("Enregistement du fichier");
                                fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier Excel (*.xls)", "xls"));
                                fileChooser.setSelectedFile(new File(ListCdf.this.getSelectedValue() + ".xls"));
                                final int rep = fileChooser.showSaveDialog(null);

                                if (rep == JFileChooser.APPROVE_OPTION) {

                                    boolean result = false;

                                    if (!fileChooser.getSelectedFile().exists()) {

                                        result = CdfUtils.toExcel(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());

                                    } else {

                                        switch (JOptionPane.showConfirmDialog(null, "Le fichier existe deja, ecraser?", null,
                                                JOptionPane.INFORMATION_MESSAGE)) {
                                        case JOptionPane.OK_OPTION:
                                            result = CdfUtils.toExcel(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile());
                                            break;
                                        case JOptionPane.NO_OPTION:
                                            this.actionPerformed(e);
                                            return;
                                        default:
                                            break;
                                        }
                                    }

                                    if (result) {

                                        final int reponse = JOptionPane.showConfirmDialog(null,
                                                "Export termine !\n" + fileChooser.getSelectedFile() + "\nVoulez-vous ouvrir le fichier?", null,
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                        switch (reponse) {
                                        case JOptionPane.OK_OPTION:
                                            try {
                                                if (Desktop.isDesktopSupported()) {
                                                    Desktop.getDesktop().open(fileChooser.getSelectedFile());
                                                }
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            break;
                                        case JOptionPane.NO_OPTION:
                                            break;
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Export abandonne !");
                                    }
                                }
                            }
                        }
                    });
                    menuExport.add(menuItem);

                    menuExport.addSeparator();
                    menuItem = new JMenuItem("Exporter le fichier en m", new ImageIcon(getClass().getResource(ICON_MATLAB)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {

                            if (true) {
                                final JFileChooser fileChooser = new JFileChooser(Preference.getPreference(Preference.KEY_OPEN_CDF));
                                fileChooser.setDialogTitle("Enregistement du fichier");
                                fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier m (*.m)", "m"));
                                fileChooser.setSelectedFile(new File(ListCdf.this.getSelectedValue() + ".m"));
                                final int rep = fileChooser.showSaveDialog(null);

                                if (rep == JFileChooser.APPROVE_OPTION) {
                                    boolean transpose = false;
                                    if (JOptionPane.showConfirmDialog(null, "Transposer pour Matlab?", null,
                                            JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
                                        transpose = true;
                                    }

                                    boolean result = false;

                                    if (!fileChooser.getSelectedFile().exists()) {

                                        result = CdfUtils.toM(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile(), transpose);

                                    } else {

                                        switch (JOptionPane.showConfirmDialog(null, "Le fichier existe deja, ecraser?", null,
                                                JOptionPane.INFORMATION_MESSAGE)) {
                                        case JOptionPane.OK_OPTION:
                                            result = CdfUtils.toM(ListCdf.this.getSelectedValue(), fileChooser.getSelectedFile(), transpose);
                                            break;
                                        case JOptionPane.NO_OPTION:
                                            this.actionPerformed(e);
                                            return;
                                        default:
                                            break;
                                        }
                                    }

                                    if (result) {

                                        final int reponse = JOptionPane.showConfirmDialog(null,
                                                "Export termine !\n" + fileChooser.getSelectedFile() + "\nVoulez-vous ouvrir le fichier?", null,
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                                        switch (reponse) {
                                        case JOptionPane.OK_OPTION:
                                            try {
                                                if (Desktop.isDesktopSupported()) {
                                                    Desktop.getDesktop().open(fileChooser.getSelectedFile());
                                                }
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            break;
                                        case JOptionPane.NO_OPTION:
                                            break;
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Export abandonne !");
                                    }
                                }
                            }
                        }
                    });
                    menuExport.add(menuItem);

                    menu.add(menuMove);
                    menu.addSeparator();
                    menu.add(menuExport);
                    menu.addSeparator();

                    menuItem = new JMenuItem("Repartition des scores");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            new FrameScores(ListCdf.this.getSelectedValue());
                        }
                    });
                    menu.add(menuItem);

                } else {
                    menuItem = new JMenuItem("Supprimer tous les fichiers", new ImageIcon(getClass().getResource(ICON_TRASH)));
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ListCdf.this.getModel().clearList();
                            ListCdf.this.clearSelection();
                            PanelCDF.razUI();
                        }
                    });
                    menu.add(menuItem);
                }

                menu.show(e.getComponent(), e.getX(), e.getY());
            } else if (e.isPopupTrigger() && ListCdf.this.getModel().getSize() > 0 && ListCdf.this.getSelectedIndices().length == 2) {
                final JPopupMenu menu = new JPopupMenu();
                JMenuItem menuItem;

                menuItem = new JMenuItem("Comparer les deux fichiers", new ImageIcon(getClass().getResource(ICON_COMPARAISON)));
                menuItem.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {

                        final Thread thread = new Thread(new Runnable() {

                            @Override
                            public void run() {

                                Ihm.getProgressPanel().setText("Comparaison en cours...");
                                Ihm.getProgressPanel().setVisible(true);

                                Cdf cdfCompar = CdfUtils.comparCdf(ListCdf.this.getModel().getElementAt(ListCdf.this.getSelectedIndices()[0]),
                                        ListCdf.this.getModel().getElementAt(ListCdf.this.getSelectedIndices()[1]),
                                        PanelCDF.getRadiobtval().isSelected());

                                Ihm.getProgressPanel().setText(null);
                                Ihm.getProgressPanel().setVisible(false);

                                if (cdfCompar != null) {
                                    ListCdf.this.getModel().addCdf(cdfCompar);
                                    JOptionPane.showMessageDialog(null, "Comparaison terminee !", null, JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Pas de difference de valeur entre les deux fichiers", null,
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        });
                        thread.start();
                    }
                });
                menu.add(menuItem);

                menu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private final class UpDownListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            final int moveMe = ListCdf.this.getSelectedIndex();

            if (e.getActionCommand().equals("Monter")) {
                if (moveMe != 0) {
                    swap(moveMe, moveMe - 1);
                    ListCdf.this.setSelectedIndex(moveMe - 1);
                    ListCdf.this.ensureIndexIsVisible(moveMe - 1);
                }
            } else {
                if (moveMe != ListCdf.this.getModel().getSize() - 1) {
                    swap(moveMe, moveMe + 1);
                    ListCdf.this.setSelectedIndex(moveMe + 1);
                    ListCdf.this.ensureIndexIsVisible(moveMe + 1);
                }
            }
        }
    }

    private final void swap(int a, int b) {
        final Cdf aObject = getModel().getElementAt(a);
        final Cdf bObject = getModel().getElementAt(b);
        getModel().set(a, bObject);
        getModel().set(b, aObject);
    }

}
