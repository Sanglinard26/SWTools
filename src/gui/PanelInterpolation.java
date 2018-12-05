package gui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import cdf.Curve;
import cdf.Map;
import cdf.Variable;
import utils.Interpolation;

public final class PanelInterpolation extends JPanel {

    private static final long serialVersionUID = 1L;

    private PanelCmd panelCmd;

    private Variable selectedVariable = null;

    private MyTableModel modelDatas;
    private JTable tableData;

    public PanelInterpolation() {

        this.setLayout(new BorderLayout());

        modelDatas = new MyTableModel();
        tableData = new JTable(modelDatas);
        tableData.setCellSelectionEnabled(true);

        modelDatas.addTableModelListener(new TableModelListener() {

            @Override
            public void tableChanged(TableModelEvent e) {

                if (modelDatas.isModeMap()) {
                    if (selectedVariable != null && e.getColumn() * e.getFirstRow() == 0) {
                        calcZvalue();
                    }
                } else {
                    if (selectedVariable != null && e.getColumn() < 2) {
                        calcZvalue();
                    }
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
                    modelDatas.fireTableCellUpdated(tableData.getSelectedRow(), tableData.getSelectedRow());
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

        panelCmd = new PanelCmd();
        this.add(panelCmd, BorderLayout.WEST);

    }

    public final void setVariable(Variable var) {

        panelCmd.switchMode.setEnabled(true);
        this.selectedVariable = var;
        if (panelCmd.switchMode.isSelected()) {
            setModel(this.selectedVariable);
        }

    }

    public final boolean isModeMap() {
        return modelDatas.isModeMap();
    }

    private final void setModel(Variable var) {
        modelDatas.setDatas(var.getValues().valuesToDouble2D());
        panelCmd.switchMode.setSelected(modelDatas.isModeMap());
    }

    public final void calcZvalue() {

        final Double[][] datas = modelDatas.getDatas();
        double result = Double.NaN;
        Double[][] results = this.selectedVariable.getValues().valuesToDouble2D().clone();

        for (int row = 0; row < tableData.getRowCount(); row++) {

            if (modelDatas.isModeMap()) {
                if (row > 0) {
                    for (int x = 1; x < this.selectedVariable.getValues().getDimX(); x++) {
                        results[0][x] = datas[0][x];
                        results[row][0] = datas[row][0];
                        results[row][x] = Interpolation.interpLinear2D(this.selectedVariable.getValues().toDouble2D(), datas[0][x], datas[row][0]);
                    }
                }
            } else {
                if (this.selectedVariable instanceof Map && datas[row][0] != null && datas[row][1] != null) {
                    result = Interpolation.interpLinear2D(this.selectedVariable.getValues().toDouble2D(), datas[row][0], datas[row][1]);
                    modelDatas.setValueAt(result, row, 2);
                } else if (this.selectedVariable instanceof Curve && datas[row][0] != null) {
                    result = Interpolation.interpLinear1D(this.selectedVariable.getValues().toDouble2D(), datas[row][0]);
                    modelDatas.setValueAt(result, row, 2);
                } else {
                    break;
                }
            }
        }

        if (modelDatas.isModeMap()) {
            modelDatas.setDatas(results);
        }

    }

    private final class PanelCmd extends JPanel {
        private final JToggleButton switchMode;

        public PanelCmd() {

            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            switchMode = new JToggleButton("Mode carto", modelDatas.isModeMap());
            switchMode.setEnabled(false);
            switchMode.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent paramActionEvent) {
                    if (selectedVariable != null) {
                        JToggleButton bt = (JToggleButton) paramActionEvent.getSource();
                        if (bt.isSelected()) {
                            bt.setText("Mode libre");
                            modelDatas.setDatas(PanelCDF.getSelVariable().getValues().valuesToDouble2D());
                        } else {
                            bt.setText("Mode carto");
                            modelDatas.setDatas();
                        }
                    }
                }
            });
            this.add(switchMode);
        }
    }

    private static final class MyTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private boolean modeMap = false;
        private static final String[] ENTETE = { "X", "Y", "Z" };
        private static final String EMPTY = "";
        private int nbCol = 0;
        private Double[][] datas;

        public MyTableModel() {
            this.datas = new Double[100][3];
            this.nbCol = this.datas[0].length;
            this.modeMap = false;
        }

        @Override
        public Class<?> getColumnClass(int paramInt) {
            return Double.class;
        }

        @Override
        public String getColumnName(int col) {
            if (modeMap) {
                return EMPTY;
            }
            return ENTETE[col];
        }

        @Override
        public int getColumnCount() {
            return nbCol;
        }

        @Override
        public int getRowCount() {
            return datas.length;
        }

        public final boolean isModeMap() {
            return this.modeMap;
        }

        @Override
        public Object getValueAt(int row, int col) {
            return datas[row][col];
        }

        public final void setDatas(Double[][] datas) {
            this.datas = datas;
            this.nbCol = this.datas[0].length;
            this.modeMap = true;
            fireTableStructureChanged();
        }

        public final void setDatas() {
            this.datas = new Double[100][3];
            this.nbCol = this.datas[0].length;
            this.modeMap = false;
            fireTableStructureChanged();
        }

        public final Double[][] getDatas() {
            return datas;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (modeMap) {
                return row * col == 0;
            }
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
