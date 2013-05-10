package srsoftwares;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.imageio.ImageIO;
import javax.management.timer.TimerMBean;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: 15/3/12
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryHistoryViewDialog extends JDialog implements ActionListener, ListSelectionListener {
    private TableHeaderRender headerRender;
    private TableRenderer tableRenderer;
    private JTable queryHistoryTable;
    private JPanel tablePanel;
    private Map<Integer, Boolean> selectionStatusMap;
    private JPanel buttonPanel;
    private JButton addQueryButton;
    private QueryEditorPanel parentClass;
    private List<Integer> addedQueryNo;
    private MainView parentMainView;

    QueryHistoryViewDialog(MainView mainView,  QueryEditorPanel editorPanel) {
        super(mainView);
        parentMainView=mainView;

        parentClass = editorPanel;
        mainView.setEnabled(false);
        addedQueryNo = new ArrayList<Integer>();
        tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        selectionStatusMap = new HashMap<Integer, Boolean>();
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setTitle("Query History Log, Smart Database Explorer " + LoginDialog.versionId + " - SR Softwares");
        this.setSize(1200, 600);
        this.setLocationRelativeTo(mainView);
        this.setLayout(new BorderLayout());
        this.add(tablePanel, BorderLayout.CENTER);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        addQueryButton = new JButton("Add Selected Queries");
        addQueryButton.setEnabled(false);
        buttonPanel.add(addQueryButton);
        addQueryButton.addActionListener(this);
        this.add(buttonPanel, BorderLayout.SOUTH);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int w = e.getWindow().getSize().width;
                int h = e.getWindow().getSize().height;
                System.out.println("Optimized View : Width = " + w + "\n\n Height = " + h);
                parentMainView.setEnabled(true);

            }
        });

        this.setVisible(true);
    }

    public void showHistoryTable(Vector<Vector<Object>> datas) {
        tableRenderer = new TableRenderer();
        headerRender = new TableHeaderRender();
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("");                  // Column 0
        columnNames.add("Query No.");         // Column 1
        columnNames.add("Query");             // Column 2
        columnNames.add("Database");          // Column 3
        columnNames.add("Time Taken (Sec)");  // Column 4
        columnNames.add("Database Server IP");// Column 5
        columnNames.add("Time");              // Column 6
        columnNames.add("Date");              // Column 7
        queryHistoryTable = new JTable();
        queryHistoryTable.getTableHeader().setReorderingAllowed(false);

        // Setting Selection Status Map as FALSE by default for every row
        for (int i = 0; i < datas.size(); i++) {
            selectionStatusMap.put(i, false);
        }
        DefaultTableModel model = new DefaultTableModel(datas, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return true;
                }
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) { // Query No Column
                    return Integer.class;
                } else if (columnIndex == 0) {  // Check Box Column
                    return Boolean.class;
                } else if(columnIndex==7){   //   Date Column
                  return  Date.class;
                }else if(columnIndex==6){  // Time  Column
                    return Date.class;
                }else if(columnIndex==4){  // Time  Column
                    return Double.class;
                }
                else {
                    return super.getColumnClass(columnIndex);
                }

            }
        };
        queryHistoryTable.setModel(model);


        TableColumn tableColumn = queryHistoryTable.getColumnModel().getColumn(0);

        tableColumn.setHeaderRenderer(new CheckBoxHeader(new MyItemListener()));
        queryHistoryTable.setAutoCreateRowSorter(true);
        MiscUtility.addCellHeaderRender(queryHistoryTable.getColumnModel(), null, tableRenderer);

        JScrollPane tablePane = new JScrollPane(queryHistoryTable);
        queryHistoryTable.setFillsViewportHeight(true);
        setColumnWidthCustomized();

        queryHistoryTable.getTableHeader().setBackground(Color.WHITE);
        tablePanel.removeAll();
        tablePanel.add(tablePane, BorderLayout.CENTER);
        queryHistoryTable.setRowSelectionAllowed(true);
        queryHistoryTable.setColumnSelectionAllowed(false);
        queryHistoryTable.getSelectionModel().addListSelectionListener(this);
        queryHistoryTable.getColumnModel().getSelectionModel().addListSelectionListener(this);
        queryHistoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = queryHistoryTable.rowAtPoint(e.getPoint());
                int col = queryHistoryTable.columnAtPoint(e.getPoint());

                Boolean isSelected = (Boolean) queryHistoryTable.getValueAt(row, 0);
                if (col != 0) {
                    if (!isSelected) {
                        queryHistoryTable.setValueAt(true, row, 0);
                        selectionStatusMap.put(row, true);
                    } else {
                        queryHistoryTable.setValueAt(false, row, 0);
                       selectionStatusMap.put(row, false);

                    }
                    addQueryToEditor();
                } else {
                    if (!isSelected) {
                        selectionStatusMap.put(row, false);
                       addQueryToEditor();
                    }
                }


            }
        });
        tablePanel.updateUI();


    }

    public void setColumnWidthCustomized() {

        // Column 0 : Select All
        TableColumn tableColumn;
        int width0 = 75; // width0
        tableColumn = queryHistoryTable.getColumnModel().getColumn(0);
        tableColumn.setPreferredWidth(width0);
        tableColumn.setMaxWidth(width0);
        tableColumn.setMinWidth(width0);

        // Column 1: Query No.
        tableColumn = queryHistoryTable.getColumnModel().getColumn(1);
        int width1 = 60; // width1
        tableColumn.setPreferredWidth(width1);
        tableColumn.setMaxWidth(width1);
        tableColumn.setMinWidth(width1);

        // Column 3: Database Name
        tableColumn = queryHistoryTable.getColumnModel().getColumn(3);
        int width3 = 100; //width 3
        tableColumn.setPreferredWidth(width3);
        tableColumn.setMaxWidth(width3);
        tableColumn.setMinWidth(width3);

        //  Column 4: Duration
        tableColumn = queryHistoryTable.getColumnModel().getColumn(4);
        int width4 = 150; // width4
        tableColumn.setPreferredWidth(width4);
        tableColumn.setMaxWidth(width4);
        tableColumn.setMinWidth(width4);


        // Column 5: System IP
        tableColumn = queryHistoryTable.getColumnModel().getColumn(5);
        int width5 = 125; // width4
        tableColumn.setPreferredWidth(width4);
        tableColumn.setMaxWidth(width4);
        tableColumn.setMinWidth(width4);

        // Column 6: Time
        tableColumn = queryHistoryTable.getColumnModel().getColumn(6);
        int width6 = 125; // width5
        tableColumn.setPreferredWidth(width5);
        tableColumn.setMaxWidth(width5);
        tableColumn.setMinWidth(width5);

        // Column 7: Date
        tableColumn = queryHistoryTable.getColumnModel().getColumn(7);
        int width7 = 100; // width5
        tableColumn.setPreferredWidth(width6);
        tableColumn.setMaxWidth(width6);
        tableColumn.setMinWidth(width6);
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int selectedRow = queryHistoryTable.getSelectedRow();
            Boolean isSelected = (Boolean) queryHistoryTable.getValueAt(selectedRow, 0);
            selectionStatusMap.put(selectedRow, isSelected);
            addQueryToEditor();
        }
    }

    private void addQueryToEditor() {
        int rowSelected = 0;
        for (Integer rowIndex : selectionStatusMap.keySet()) {
            if (selectionStatusMap.get(rowIndex)) {
                rowSelected++;
                break;
            }

        }
        if (rowSelected > 0) {
            addQueryButton.setEnabled(true);
        } else {
            addQueryButton.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String qryToAdd = "";
        String notExecuted = "";
        for (Integer rowIndex : selectionStatusMap.keySet()) {
            if (selectionStatusMap.get(rowIndex)) {
                String qry = queryHistoryTable.getValueAt(rowIndex, 2) + ";\n";
                int qryNo = (Integer) queryHistoryTable.getValueAt(rowIndex, 1);
                if (!addedQueryNo.contains(qryNo)) {
                    addedQueryNo.add(qryNo);
                    qryToAdd = qryToAdd + qry;
                } else {

                    notExecuted = notExecuted + qryNo + ",";

                }
            }
        }
        if (notExecuted.length() > 0) {
            notExecuted = notExecuted.substring(0, notExecuted.length() - 1);

            JOptionPane optionPane = new JOptionPane();
            ImageIcon ico = new ImageIcon(ScanNetworks.class.getResource("images/exclaimIcon.png"));
            optionPane.setIcon(ico);
            optionPane.setMessage(JOptionPane.ERROR_MESSAGE);
            optionPane.setMessage("Query No : " + notExecuted + " are already added to the Query Editor.\n\t Choose some other Queries");
            JDialog dialog = optionPane.createDialog("Query(s) already Added !");
            Image image = null;
            try {
                image = ImageIO.read(this.getClass().getResource("images/exclaimIconTOP.PNG"));
            } catch (Exception e1) {

            }
            dialog.setIconImage(image);
            dialog.show();

        }
        if (qryToAdd.length() != 0) {
            parentClass.writeQueryFromHistory(qryToAdd);

            ImageIcon ico = new ImageIcon(ScanNetworks.class.getResource("images/okICON.png"));
            int option = JOptionPane.showOptionDialog(null, "Selected Query has been added Successfully ! \n        Close this window now ?", "Query Added to Editor", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ico, null, null);
            if (option == JOptionPane.YES_OPTION) {
                parentMainView.setEnabled(true);

                this.dispose();
            }
        }

    }


    class CheckBoxHeader extends JCheckBox implements TableCellRenderer, MouseListener {
        protected CheckBoxHeader rendererComponent;
        protected int column;
        protected boolean mousePressed = false;

        public CheckBoxHeader(ItemListener itemListener) {
            rendererComponent = this;
            rendererComponent.addItemListener(itemListener);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (table != null) {
                JTableHeader header = table.getTableHeader();
                if (header != null) {
                    rendererComponent.setForeground(header.getForeground());
                    rendererComponent.setBackground(header.getBackground());
                    rendererComponent.setFont(header.getFont());
                    header.addMouseListener(rendererComponent);
                }
            }
            setColumn(column);
            rendererComponent.setText("Select All");
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            return rendererComponent;
        }

        protected void setColumn(int column) {
            this.column = column;
        }

        public int getColumn() {
            return column;
        }

        protected void handleClickEvent(MouseEvent e) {
            if (mousePressed) {
                mousePressed = false;
                JTableHeader header = (JTableHeader) (e.getSource());
                JTable tableView = header.getTable();
                TableColumnModel columnModel = tableView.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int column = tableView.convertColumnIndexToModel(viewColumn);

                if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {
                    doClick();
                }
            }
        }

        public void mouseClicked(MouseEvent e) {
            handleClickEvent(e);
            ((JTableHeader) e.getSource()).repaint();
        }

        public void mousePressed(MouseEvent e) {
            mousePressed = true;
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }
    }

    class MyItemListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {

            Object source = e.getSource();
            if (source instanceof AbstractButton == false) return;
            boolean checked = e.getStateChange() == ItemEvent.SELECTED;
            for (int x = 0, y = queryHistoryTable.getRowCount(); x < y; x++) {
                queryHistoryTable.setValueAt(new Boolean(checked), x, 0);
                selectionStatusMap.put(x, checked);
            }
            addQueryButton.setEnabled(checked);

        }
    }

}
