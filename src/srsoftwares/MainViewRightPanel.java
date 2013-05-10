package srsoftwares;

import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: Apr 14, 2011
 * Time: 2:13:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainViewRightPanel extends JPanel implements ChangeListener {
    private JTabbedPane tabbedPaneTableLevel;
    private JTabbedPane tabbedPaneSchemaLevel;
    private JTabbedPane tabbedPaneInstanceLevel;
    private JTabbedPane tabbedPaneSystemLevel;

    private JPanel dataTabPanel;

    private String schemaName;
    private String tableName;
    private Vector<String> columnNames;

    private TableRenderer tableRenderer;

    private TableHeaderRender headerRender;
    public static Map<String, Integer> tableColCount = new HashMap<String, Integer>();
    private MainViewLeftPanel leftPanel;
    private MainView mainView;
    private QueryEditorPanel queryEditorPanel;

    MainViewRightPanel(MainViewLeftPanel leftPanel, MainView mainView) {
        this.leftPanel = leftPanel;
        this.mainView = mainView;
        headerRender = new TableHeaderRender();
        tableRenderer = new TableRenderer();
    }

    public void showSystemInformation() {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("Connection Property");
        columnNames.add("Connection Information");
        tabbedPaneSystemLevel = new JTabbedPane();
        queryEditorPanel = new QueryEditorPanel(this, leftPanel, mainView);

        Map<String, String> databaseProperty = DataBaseUtility.getSystemInformation();
        Vector<Vector<Object>> datas = new Vector<Vector<Object>>();
        for (String eachPropertyKey : databaseProperty.keySet()) {
            Vector<Object> row = new Vector<Object>();
            row.add(eachPropertyKey);
            row.add(databaseProperty.get(eachPropertyKey));
            datas.add(row);
        }
        JTable systemInfoTable = new JTable();
        systemInfoTable.getTableHeader().setReorderingAllowed(false);
        DefaultTableModel model = new DefaultTableModel(datas, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        systemInfoTable.setModel(model);
        systemInfoTable.setAutoCreateRowSorter(true);
        MiscUtility.addCellHeaderRender(systemInfoTable.getColumnModel(), headerRender, tableRenderer);
        JScrollPane scrollPane = new JScrollPane(systemInfoTable);
        systemInfoTable.setFillsViewportHeight(true);
        tabbedPaneSystemLevel.add("System Information", scrollPane);
        tabbedPaneSystemLevel.add("Query Editor", queryEditorPanel.getQueryEditorPanel());
        this.setLayout(new BorderLayout());
        add(tabbedPaneSystemLevel, BorderLayout.CENTER);
    }

    public void addTableSchema() {
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("Schema Name");
        columnNames.add("Table Count");

        List<String> schemaNames = DataBaseUtility.getSchemaNames();

        if (schemaNames.size() > 0) {
            tabbedPaneInstanceLevel = new JTabbedPane();
            queryEditorPanel = new QueryEditorPanel(this, leftPanel, mainView);
            Vector<Vector<Object>> datas = new Vector<Vector<Object>>();
            for (String schemaName : schemaNames) {
                Vector<Object> row = new Vector<Object>();
                List<String> tableNames = DataBaseUtility.getTableNames(schemaName);
                int countTables = tableNames.size();
                row.add(schemaName);
                row.add(countTables);
                datas.add(row);
            }
            JTable instanceTable = new JTable();
            instanceTable.getTableHeader().setReorderingAllowed(false);
            DefaultTableModel model = new DefaultTableModel(datas, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 1) {
                        return Integer.class;
                    } else {
                        return super.getColumnClass(columnIndex);
                    }

                }
            };

            instanceTable.setModel(model);
            instanceTable.setAutoCreateRowSorter(true);
            MiscUtility.addCellHeaderRender(instanceTable.getColumnModel(), headerRender, tableRenderer);
            JScrollPane scrollPane = new JScrollPane(instanceTable);
            instanceTable.setFillsViewportHeight(true);
            tabbedPaneInstanceLevel.add("Instance Information", scrollPane);
            tabbedPaneInstanceLevel.add("Query Editor", queryEditorPanel.getQueryEditorPanel());

            this.setLayout(new BorderLayout());
            add(tabbedPaneInstanceLevel, BorderLayout.CENTER);

        } else {
            displayErrorMsg(DataBaseUtility.dbName, "Database", null);
        }
    }

    public void displayErrorMsg(String missingItemName, String missingItemType, JPanel errPanel) {
        JPanel errorPanel = null;
        if (errPanel == null) {
            errorPanel = this;
        } else {
            errorPanel = errPanel;
        }
        errorPanel.removeAll();

        JPanel mainErrorPanel = new JPanel();
        JPanel imagePanel = new JPanel();
        JPanel errorMsgPanel = new JPanel();

        mainErrorPanel.setLayout(new BoxLayout(mainErrorPanel, BoxLayout.Y_AXIS));
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.X_AXIS));
        errorMsgPanel.setLayout(new BoxLayout(errorMsgPanel, BoxLayout.X_AXIS));


        int midPanelHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2;
        int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int midPanelWidth = (screenWidth - MainView.splitAmount) / 2;


        // Setting Image Label & Panel
        ImageIcon imageIcon;
        JLabel imgLabel;
        imageIcon = new ImageIcon(MainViewRightPanel.class.getResource("images/noDBFound.png"));
        imgLabel = new JLabel();
        imgLabel.setIcon(imageIcon);

        imagePanel.add(imgLabel, JLabel.CENTER);

        // Setting Message to Display
        String msgToDisplay = "<html><h1 style = \" text align:center ; color : #FF0000 ;font-size:140% \" > <b> Sorry ! No Information Found for " + missingItemType + " : <br><h1 style = \"text-align : center; color : \t#800080;font-size:150%\">" + missingItemName + "</b></htm>";
        JLabel errorLabel = new JLabel(msgToDisplay, JLabel.CENTER);
//        errorMsgPanel.add(Box.createHorizontalStrut(midPanelWidth - 500));
        errorMsgPanel.add(errorLabel, JLabel.CENTER);

        // Adding Components
        imagePanel.setPreferredSize(new Dimension(175, 150));
        imagePanel.setMaximumSize(new Dimension(175, 150));
        imagePanel.setMinimumSize(new Dimension(175, 150));
        errorMsgPanel.setPreferredSize(new Dimension(500, 150));
        errorMsgPanel.setMaximumSize(new Dimension(500, 150));
        errorMsgPanel.setMinimumSize(new Dimension(500, 150));


        // imagePanel.setBorder(BorderFactory.createLineBorder(Color.RED));
        imagePanel.setBackground(Color.WHITE);
        errorMsgPanel.setBackground(Color.WHITE);
        mainErrorPanel.add(Box.createVerticalStrut(midPanelHeight - 200));
        mainErrorPanel.add(imagePanel);
        mainErrorPanel.add(Box.createVerticalStrut(15));
        // errorMsgPanel.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        mainErrorPanel.add(errorMsgPanel);
        mainErrorPanel.setBackground(Color.WHITE);

        errorPanel.add(mainErrorPanel, BorderLayout.CENTER);
        errorPanel.setBackground(Color.WHITE);
        mainErrorPanel.updateUI();
        errorPanel.updateUI();

    }


    public void addTableNames(String SchemaName) {

        Vector<String> columnNames = new Vector<String>();
        tableColCount.clear();
        columnNames.add("Table Name");
        columnNames.add("Table Column Count");
        columnNames.add("# No. Of Records");
        Vector<Vector<Object>> tablesMetadata = new Vector<Vector<Object>>();
        List<String> tableNames = DataBaseUtility.getTableNames(SchemaName);


        if (tableNames.size() > 0) {
            tabbedPaneSchemaLevel = new JTabbedPane();
            QueryEditorPanel queryEditorPanel = new QueryEditorPanel(this, leftPanel, mainView);

            for (String tableName : tableNames) {
                Vector<Object> row = new Vector<Object>();
                int countColumn = 0;
                int recordCount = 0;


                countColumn = DataBaseUtility.getColumnCount(SchemaName, tableName);
                recordCount = DataBaseUtility.getRowCount(SchemaName, tableName);


                row.add(tableName);
                row.add(countColumn);
                row.add(recordCount);
                tableColCount.put(tableName, recordCount);
                tablesMetadata.add(row);
            }


            JTable schemaTable = new JTable();

            schemaTable.getTableHeader().setReorderingAllowed(false);
            schemaTable.setAutoCreateRowSorter(true);
            DefaultTableModel model = new DefaultTableModel(tablesMetadata, columnNames) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

                public Class<?> getColumnClass(int columnIndex) {
                    if (columnIndex == 1 || columnIndex == 2) {
                        return Integer.class;
                    } else {
                        return super.getColumnClass(columnIndex);
                    }

                }
            };

            schemaTable.setModel(model);
            MiscUtility.addCellHeaderRender(schemaTable.getColumnModel(), headerRender, tableRenderer);

            JScrollPane scrollPane = new JScrollPane(schemaTable);
            schemaTable.setFillsViewportHeight(true);
            tabbedPaneSchemaLevel.add("Schema Information", scrollPane);
            tabbedPaneSchemaLevel.add("Query Editor", queryEditorPanel.getQueryEditorPanel());
            this.setLayout(new BorderLayout());
            add(tabbedPaneSchemaLevel, BorderLayout.CENTER);
        } else {
            displayErrorMsg(SchemaName, "Schema", null);
        }

    }


    public void addTableDetails(String TableName, String SchemaName) {

        schemaName = SchemaName;
        tableName = TableName;

        Vector<String> tableDataInfo = new Vector<String>();


        tabbedPaneTableLevel = new JTabbedPane();

        tableDataInfo.add("Column Name");
        tableDataInfo.add("Data Type");
        tableDataInfo.add("Size");
        tableDataInfo.add("Is Primary Key");
        tableDataInfo.add("Is Null");
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        try {
            columnNames = DataBaseUtility.getColumnNames(SchemaName, TableName);
            if (columnNames.size() > 0) {
                for (String columnName : columnNames) {
                    Vector<Object> row = new Vector<Object>();

                    String columnType = DataBaseUtility.getColumnType(SchemaName, TableName, columnName);
                    int columnSize = DataBaseUtility.getColumnSize(SchemaName, TableName, columnName);
                    boolean primary = DataBaseUtility.checkPrimary(SchemaName, TableName, columnName);
                    boolean nullable = DataBaseUtility.checkNullable(SchemaName, TableName, columnName);

                    row.add(columnName);
                    row.add(columnType);
                    row.add(columnSize);
                    row.add(primary);
                    row.add(nullable);
                    data.add(row);
                }
                JTable tableInfo = new JTable();
                tableInfo.setAutoCreateRowSorter(true);
                tableInfo.getTableHeader().setReorderingAllowed(false);
                DefaultTableModel model = new DefaultTableModel(data, tableDataInfo) {

                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }


                    public Class<?> getColumnClass(int columnIndex) {
                        if (columnIndex == 3 || columnIndex == 4) {
                            return Boolean.class;
                        } else if (columnIndex == 2) {
                            return Integer.class;
                        } else {
                            return super.getColumnClass(columnIndex);
                        }

                    }
                };

                tableInfo.setFillsViewportHeight(true);
                tableInfo.setModel(model);
                MiscUtility.addCellHeaderRender(tableInfo.getColumnModel(), headerRender, tableRenderer);

                JScrollPane scrollPane = new JScrollPane(tableInfo);
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


                // FILLING THE DATA TAB


                dataTabPanel = new JPanel(new BorderLayout());

                 // TODO : add icon to Meta Data and Data tab
                tabbedPaneTableLevel.addTab("Meta Data", scrollPane);
                tabbedPaneTableLevel.addTab("Data", dataTabPanel);
                queryEditorPanel = new QueryEditorPanel(this, leftPanel, mainView);

                tabbedPaneTableLevel.addTab("Query Editor", queryEditorPanel.getQueryEditorPanel());
                tabbedPaneTableLevel.addChangeListener(this);


                this.setLayout(new BorderLayout());
                add(tabbedPaneTableLevel, BorderLayout.CENTER);

            } else {

                displayErrorMsg(TableName, "Table", null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == tabbedPaneTableLevel) {
            int a = tabbedPaneTableLevel.getSelectedIndex();
            if (a == 1) {      // DATA TAB

                dataTabPanel.removeAll();
                JProgressBar progressBar = new JProgressBar();
                progressBar.setPreferredSize(new Dimension(600, 40));
                progressBar.setIndeterminate(true);
                progressBar.setBorderPainted(true);
                JPanel progressPanel = new JPanel();
                BoxLayout boxLayout = new BoxLayout(progressPanel, BoxLayout.X_AXIS);
                progressPanel.setLayout(boxLayout);
                progressPanel.add(Box.createGlue());
                progressPanel.add(progressBar);
                progressPanel.add(Box.createGlue());
                dataTabPanel.add(progressPanel);
                dataTabPanel.updateUI();

                new Thread(new Runnable() {

                    public void run() {
                        dataTabPanel.removeAll();


                        if (tableColCount.containsKey(tableName) && tableColCount.get(tableName) == 0) { // table having no records !
                            displayErrorMsg(tableName, "Table Details", dataTabPanel);
                        } else {
                            dataTabPanel.add(createDataTabPanel());
                            dataTabPanel.updateUI();
                        }

                    }
                }).start();
            }
            if (a == 2) {
                queryEditorPanel.updateUI();

            }
        }

    }

    public JPanel createDataTabPanel() {
        JTable fullRecordsTable = new JTable();
        JPanel dataTabPanel = new JPanel(new BorderLayout());


        Vector<Vector<Object>> rowData;

        rowData = DataBaseUtility.fillTable(schemaName, tableName);

        DefaultTableModel modelRow = new DefaultTableModel(rowData, columnNames) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Integer> columnsSize = MiscUtility.getMaxLengthsForColumns(rowData, columnNames);
        fullRecordsTable.setAutoCreateRowSorter(true);
        fullRecordsTable.setModel(modelRow);
        int sumDataColWidth = 0;

        fullRecordsTable.getTableHeader().setReorderingAllowed(false);
        // Changing the Column Model

        TableColumnModel colModel = fullRecordsTable.getColumnModel();
        int columnNo = 0;
        columnNo = DataBaseUtility.getColumnCount(schemaName, tableName);


        for (int i = 0; i < columnNo; i++) {
            TableColumn tabColumn = colModel.getColumn(i);
            tabColumn.setCellRenderer(tableRenderer);


            int v = MiscUtility.adjustTableColumnWidth(tableRenderer, columnsSize.get(i));
            sumDataColWidth += v;
            tabColumn.setMinWidth(v);
            fullRecordsTable.getTableHeader().setResizingAllowed(true);
        }
        double viewWidth = MainViewRightPanel.this.getSize().getWidth();
        if (sumDataColWidth > viewWidth) {
            fullRecordsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        JScrollPane allRowsScrollPane = new JScrollPane(fullRecordsTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        allRowsScrollPane.setAutoscrolls(true);

        dataTabPanel.add(allRowsScrollPane, BorderLayout.CENTER);
        return dataTabPanel;

    }


}


            


