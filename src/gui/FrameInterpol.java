/*
 * Creation : 26 juin 2018
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import cdf.Variable;
import utils.Interpolation;

public final class FrameInterpol extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JTable tableData;
    private static final String[] ENTETE = new String[] { "X", "Y", "Z" };

    public FrameInterpol() {

        this.setTitle("Interpolation valeur Z");
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());

        tableData = new JTable(new DefaultTableModel());
        ((DefaultTableModel) (tableData.getModel())).setDataVector(new String[100][3], ENTETE);
        tableData.setCellSelectionEnabled(true);

        ((DefaultTableModel) (tableData.getModel())).addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {
                // System.out.println("table changed");
                Variable var = PanelCDF.getSelVariable();
                if (var != null) {
                    Vector<?> table = (((DefaultTableModel) tableData.getModel()).getDataVector());
                    for (int row = 0; row < table.size(); row++) {
                        if (((Vector<?>) table.get(row)).get(0) != null && ((Vector<?>) table.get(row)).get(1) != null) {
                            double z = calcZ(var, ((Vector<?>) table.get(row)).get(0).toString(), ((Vector<?>) table.get(row)).get(1).toString());
                            ((Vector<String>) table.get(row)).set(2, String.valueOf(z));
                        } else {
                            break;
                        }

                    }
                    tableData.updateUI();
                }
            }
        });

        tableData.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == 10) {
                    ((DefaultTableModel) tableData.getModel()).fireTableDataChanged();
                }

                if (e.getKeyCode() == 127) {
                    int[] idxCol = tableData.getSelectedColumns();
                    int[] idxRow = tableData.getSelectedRows();
                    for (int c : idxCol) {
                        for (int r : idxRow) {
                            ((Vector<String>) ((DefaultTableModel) tableData.getModel()).getDataVector().get(r)).set(c, "");
                        }
                    }
                    ((DefaultTableModel) tableData.getModel()).fireTableDataChanged();
                }

                if ((e.getModifiers() == KeyEvent.CTRL_MASK) && (e.getKeyCode() == KeyEvent.VK_V)) {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable transferable = clipboard.getContents(null);

                    if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                        try {
                            String dataClipboard = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                            String[] splitLine = dataClipboard.split("\n");
                            String[] splitTab;

                            int intCol = tableData.getSelectedColumn();
                            int row = tableData.getSelectedRow();
                            int col = intCol;

                            for (String sLine : splitLine) {
                                splitTab = sLine.split("\t");
                                col = intCol;
                                for (String sTab : splitTab) {
                                    if (!sTab.isEmpty()) {
                                        tableData.getModel().setValueAt(sTab, row, col);

                                    }
                                    col++;
                                }
                                row++;
                            }
                        } catch (UnsupportedFlavorException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        this.add(new JScrollPane(tableData), BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);
    }

    public final double calcZ(Variable var, String x, String y) {
        if (x != null && y != null) {
            return Interpolation.interpLinear2D(var.getValues().toDouble2D(), Double.parseDouble(x), Double.parseDouble(y));
        }
        return Double.NaN;
    }

}
