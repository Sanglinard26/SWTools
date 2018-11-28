package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import cdf.Curve;
import cdf.Map;
import cdf.Variable;
import utils.Interpolation;

public final class PanelInterpolation extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MyLinearTableModel modelDatas;
    private static JTable tableData;

    public PanelInterpolation() {

        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        modelDatas = new MyLinearTableModel();
        tableData = new JTable(modelDatas);
        tableData.setCellSelectionEnabled(true);

        modelDatas.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {

                final Variable var = PanelCDF.getSelVariable();

                if (var != null && e.getColumn() < 2) {

                    calcZvalue(var);
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
                    modelDatas.fireTableDataChanged();
                }

                if (e.getKeyCode() == 127) {
                    int[] idxCol = tableData.getSelectedColumns();
                    int[] idxRow = tableData.getSelectedRows();
                    for (int c : idxCol) {
                        for (int r : idxRow) {
                            modelDatas.setValueAt(null, r, c);
                        }
                    }
                    modelDatas.fireTableDataChanged();
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
                                        modelDatas.setValueAt(Double.parseDouble(sTab), row, col);

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

    }
    
    public static final void setModel(Variable var)
    {
    	tableData.setModel(new MyMapTableModel(var.getValues().valuesToDouble2D()));
    }

    public final void calcZvalue(Variable var) {

        final Double[][] datas = modelDatas.getDatas();
        double result = Double.NaN;

        for (int row = 0; row < tableData.getRowCount(); row++) {

            if (var instanceof Map && datas[row][0] != null && datas[row][1] != null) {
                result = Interpolation.interpLinear2D(var.getValues().toDouble2D(), datas[row][0], datas[row][1]);
            } else if (var instanceof Curve && datas[row][0] != null) {
                result = Interpolation.interpLinear1D(var.getValues().toDouble2D(), datas[row][0]);
            } else {
                break;
            }

            modelDatas.setValueAt(result, row, 2);

        }

    }

    private static final class MyLinearTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private final String[] ENTETE = { "X", "Y", "Z" };
        private final Double[][] datas;

        public MyLinearTableModel() {
            datas = new Double[100][3];
        }

        @Override
        public Class<?> getColumnClass(int paramInt) {
            return Double.class;
        }

        @Override
        public String getColumnName(int col) {
            return ENTETE[col];
        }

        @Override
        public int getColumnCount() {
            return ENTETE.length;
        }

        @Override
        public int getRowCount() {
            return datas.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            return datas[row][col];
        }

        public final Double[][] getDatas() {
            return datas;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col < 2;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            if (value != null) {
                datas[row][col] = (double) value;
            } else {
                datas[row][col] = null;
            }

            fireTableCellUpdated(row, col);
        }

    }
    
    private static final class MyMapTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private static final String EMPTY = "";
        private int nbCol = 0;
        private final Double[][] datas;

        public MyMapTableModel(Double[][] datas) {
            this.datas = datas;
            nbCol = this.datas[0].length;
        }

        @Override
        public Class<?> getColumnClass(int paramInt) {
            return Double.class;
        }

        @Override
        public String getColumnName(int col) {
        	return EMPTY;
        }

        @Override
        public int getColumnCount() {
            return nbCol;
        }

        @Override
        public int getRowCount() {
            return datas.length;
        }

        @Override
        public Object getValueAt(int row, int col) {
            return datas[row][col];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col < 2;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            if (value != null) {
                datas[row][col] = (double) value;
            } else {
                datas[row][col] = null;
            }

            fireTableCellUpdated(row, col);
        }

    }

}
