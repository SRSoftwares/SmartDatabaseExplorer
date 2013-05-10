package srsoftwares;


import org.w3c.dom.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: 5/3/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryEditorPanel extends JPanel implements ActionListener, ClipboardOwner, UndoableEditListener, KeyListener {
    private JPanel queryEditorPanel;
    private JPanel tabbedPanel;
    private JTextArea queryTextArea;
    private JButton queryExecuteButton;
    private JButton queryResetButton;
    private JCheckBox autoCommitBox;
    private JLabel requestLabel;
    private JTabbedPane queryTabbedPane;
    private List<String> queryStatus;
    private TableRenderer tableRenderer;
    private MainViewRightPanel rightPanel;
    private MainViewLeftPanel leftPanel;
    private JPopupMenu popupMenu;
    private JMenuItem cutItem;
    private JMenuItem copyItem;
    private JMenuItem pasteItem;
    private JMenuItem selectAllItem;
    private JMenuItem deleteItem;
    private MainView mainView;

    private JButton showQueryHistoryButton;
    private File historyXML;
    private UndoManager undoManager;
    public static final int undoTimes = 1000;
    private volatile int caretStart = 0;
    private volatile int caretEnd = 0;
    private JMenuItem undoItem;
    private JMenuItem redoItem;
    private JButton cutButton;
    private JButton copyButton;
    private JButton pasteButton;
    private JButton deleteButton;
    private JButton redoButton;
    private JButton undoButton;
    private JLabel lineNoLabel;

    private volatile boolean enableCopyPaste;
    private boolean disableButton;
    private JPanel buttonPanel;
    static int splitterDistance = 275;
    private JPanel saveResultPanel;
    private JPanel recordPanel;
    private JLabel recordLabel;
    private JButton fontButton;
    private JButton openSqlButton;
    private JButton saveSqlButton;
    private int queryEditorPanelWidth;

    QueryEditorPanel(MainViewRightPanel rightPanel, MainViewLeftPanel leftPanel, MainView mainView) {
        this.rightPanel = rightPanel;
        this.leftPanel = leftPanel;
        this.mainView = mainView;
        historyXML = new File("QueryHistory.xml");
        disableButton = false;
        queryEditorPanel = new JPanel(new BorderLayout());
        queryEditorPanel.removeAll();
        queryEditorPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        queryEditorPanel.setLayout(new BoxLayout(queryEditorPanel, BoxLayout.Y_AXIS));
        tableRenderer = new TableRenderer();
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        buttonPanel = new JPanel(new FlowLayout());
        popupMenu = new JPopupMenu();
        ImageIcon cutImage = null;
        ImageIcon copyImage = null;
        ImageIcon pasteImage = null;
        ImageIcon selectAllImage = null;
        ImageIcon deleteImage = null;
        ImageIcon undoImage = null;
        ImageIcon redoImage = null;

        try {

            cutImage = new ImageIcon(ImageIO.read(this.getClass().getResource("images/cutIcon_Small.png")));
            copyImage = new ImageIcon(ImageIO.read(this.getClass().getResource("images/copyIcon_Small.png")));
            pasteImage = new ImageIcon(ImageIO.read(this.getClass().getResource("images/pasteIcon_Small.png")));
            selectAllImage = new ImageIcon(ImageIO.read(this.getClass().getResource("images/selectallIcon_Small.png")));
            deleteImage = new ImageIcon(ImageIO.read(this.getClass().getResource("images/deleteIcon_Small.png")));
            undoImage = new ImageIcon(ImageIO.read(this.getClass().getResource("images/undoIcon_Small.png")));
            redoImage = new ImageIcon(ImageIO.read(this.getClass().getResource("images/redoIcon_Small.png")));

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        cutItem = new JMenuItem("Cut");
        copyItem = new JMenuItem("Copy");
        pasteItem = new JMenuItem("Paste");
        selectAllItem = new JMenuItem("Select All");
        deleteItem = new JMenuItem("Delete");
        undoItem = new JMenuItem("Undo");
        redoItem = new JMenuItem("Redo");

        cutItem.setIcon(cutImage);
        copyItem.setIcon(copyImage);
        pasteItem.setIcon(pasteImage);
        selectAllItem.setIcon(selectAllImage);
        deleteItem.setIcon(deleteImage);
        undoItem.setIcon(undoImage);
        redoItem.setIcon(redoImage);

        popupMenu.add(new JSeparator(JSeparator.VERTICAL));
        popupMenu.add(cutItem);
        popupMenu.add(copyItem);
        popupMenu.add(pasteItem);
        popupMenu.add(new JSeparator(JSeparator.HORIZONTAL));
        popupMenu.add(selectAllItem);
        popupMenu.add(deleteItem);
        popupMenu.add(undoItem);
        popupMenu.add(redoItem);


        copyItem.addActionListener(this);
        cutItem.addActionListener(this);
        pasteItem.addActionListener(this);
        selectAllItem.addActionListener(this);
        deleteItem.addActionListener(this);
        undoItem.addActionListener(this);
        redoItem.addActionListener(this);

        tabbedPanel = new JPanel(new BorderLayout());
        undoManager = new UndoManager();
        undoManager.setLimit(undoTimes);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        queryTextArea = new JTextArea();
        setQueryTextAreaFont(new Font("Times New Roman", Font.PLAIN, 20));
        queryTextArea.setBorder(BorderFactory.createEtchedBorder());
        textAreaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(queryTextArea);

        scrollPane.setPreferredSize(new Dimension(700, 300));
        textAreaPanel.add(scrollPane, BorderLayout.CENTER);

        queryTextArea.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                caretStart = e.getDot();
                caretEnd = e.getMark();

                int caretPos = queryTextArea.getCaretPosition();
                int lineNo = 1;
                int columNo = 1;
                try {
                    lineNo = queryTextArea.getLineOfOffset(caretPos);
                    columNo = caretPos - queryTextArea.getLineStartOffset(lineNo);
                    lineNo++;
                    updateLineColumnNo(lineNo, columNo);

                } catch (Exception e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                if (e.getDot() == e.getMark()) {
                    if (disableButton) {

                        copyButton.setEnabled(false);
                        cutButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                    }

                } else {

                    copyButton.setEnabled(true);
                    cutButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    enableCopyPaste = true;
                }
                enableCopyPaste = false;
                if (queryTextArea.getText().length() > 0) {
                    queryExecuteButton.setEnabled(true);
                    queryResetButton.setEnabled(true);
                } else {
                    queryExecuteButton.setEnabled(false);
                    queryResetButton.setEnabled(false);
                }
            }
        });


        queryTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == e.BUTTON1) {
                    if (queryTextArea.getSelectedText() != null) {
                        copyButton.setEnabled(true);
                        cutButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                    } else {
                        copyButton.setEnabled(false);
                        cutButton.setEnabled(false);
                        deleteButton.setEnabled(false);

                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                showPopUp(e);
            }
        });


        queryTextArea.getDocument().addUndoableEditListener(this);
        queryTextArea.addKeyListener(this);
        queryExecuteButton = new JButton("Execute");
        queryResetButton = new JButton("Reset");
        try {
            queryExecuteButton = new JButton("Execute", new ImageIcon(ImageIO.read(this.getClass().getResource("images/runQueryBtn.png"))));
            queryResetButton = new JButton("Reset", new ImageIcon(ImageIO.read(this.getClass().getResource("images/resetBtn.png"))));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        queryExecuteButton.setFont(new Font("Times New Roman", Font.BOLD, 15));
        queryExecuteButton.setMnemonic(KeyEvent.VK_E);
        queryExecuteButton.setToolTipText("Execute Query");

        queryResetButton.setMnemonic(KeyEvent.VK_R);
        queryResetButton.setToolTipText("Reset Query Editor Text");
        queryResetButton.setFont(new Font("Times New Roman", Font.BOLD, 15));
        queryExecuteButton.setEnabled(false);
        queryResetButton.setEnabled(false);
        autoCommitBox = new JCheckBox("<html><h4><b>Auto Commit</b></html>", true);
        autoCommitBox.setBackground(Color.WHITE);
        autoCommitBox.addActionListener(this);
        queryExecuteButton.addActionListener(this);
        queryResetButton.addActionListener(this);

        buttonPanel.setBackground(Color.WHITE);
        lineNoLabel = new JLabel();
        Dimension dimensionLabel = new Dimension(120, 14);
        lineNoLabel.setPreferredSize(dimensionLabel);
        lineNoLabel.setMaximumSize(dimensionLabel);
        lineNoLabel.setMinimumSize(dimensionLabel);
        updateLineColumnNo(1, 0);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(lineNoLabel);
        buttonPanel.add(Box.createHorizontalStrut(610));
        buttonPanel.add(autoCommitBox);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(queryExecuteButton);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(queryResetButton);
        buttonPanel.add(Box.createHorizontalStrut(10));


        textAreaPanel.add(buttonPanel, BorderLayout.SOUTH);
        textAreaPanel.add(getStylePanel(), BorderLayout.NORTH);


        // Adding Right Click option to Text Editor


        tabbedPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        String tipText = "<html><h2 style=\"text-align : center ; color :BLACK \"> <b>Execute</b> - Execute a Query <br> <h2 style=\"text-align : center;color :BLACK \"> <b>Reset</b> -Clear Editor<br> <h2 style=\"text-align : center;color :BLACK \"> <b>Auto Commit</b> - Save changes to original Database <br> <h2 style=\"text-align : center;color :BLACK \"> <b>To Execute Multiple Query </b> - Add an <b>;</b> after every Query.</html>";
        requestLabel = new JLabel(tipText, JLabel.CENTER);

        requestLabel.setForeground(Color.DARK_GRAY);
        tabbedPanel.add(requestLabel, BorderLayout.CENTER);
        tabbedPanel.setBackground(Color.WHITE);
        textAreaPanel.setBackground(Color.WHITE);
        queryTabbedPane = new JTabbedPane();

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(splitterDistance);
        splitPane.setTopComponent(textAreaPanel);

        splitPane.setBottomComponent(tabbedPanel);

        queryEditorPanel.add(splitPane);


        this.updateUI();
        this.setVisible(true);

    }

    private void updateLineColumnNo(int lineNo, int columnNo) {

        String str = "Line: " + lineNo + " , Column: " + columnNo;
        lineNoLabel.setText(str);
        lineNoLabel.setHorizontalAlignment(JLabel.CENTER);
        lineNoLabel.updateUI();


    }

    private void showPopUp(MouseEvent e) {
        if (e.getButton() == e.BUTTON3) {

            int len = queryTextArea.getSelectionStart() == queryTextArea.getSelectionEnd() ? 0 : 1;
            if (len == 0) {

                cutItem.setEnabled(false);
                copyItem.setEnabled(false);
                deleteItem.setEnabled(false);

            } else {
                cutItem.setEnabled(true);
                copyItem.setEnabled(true);
                deleteItem.setEnabled(true);
            }
            if (getClipboardContents().length() == 0) {
                pasteItem.setEnabled(false);
            } else {
                pasteItem.setEnabled(true);
            }
            if (queryTextArea.getText().length() > 0) {
                selectAllItem.setEnabled(true);
            } else {
                selectAllItem.setEnabled(false);
            }
            if (undoManager.canUndo()) {
                undoItem.setEnabled(true);
            } else {
                undoItem.setEnabled(false);
            }
            if (undoManager.canRedo()) {
                redoItem.setEnabled(true);
            } else {
                redoItem.setEnabled(false);
            }
            popupMenu.show(queryTextArea, e.getX(), e.getY());

        }


    }

    private String getClipboardContents() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String clipboardText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                if (clipboardText == null) {
                    return "";
                }

                return clipboardText;
            } catch (Exception e) {

            }
        } else {
            return "";
        }
        return "";
    }

    private void setClipboardContents(String text) {
        StringSelection selection = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, this);
    }

    private void performCopy(JTextArea textArea) {
        int cursorPos = textArea.getCaretPosition();
        String selectedText = textArea.getSelectedText();
        setClipboardContents(selectedText);
        textArea.setCaretPosition(cursorPos);
        textArea.moveCaretPosition(cursorPos);
        textArea.updateUI();
    }

    private void performCut(JTextArea textArea) {
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        String selectedText = textArea.getSelectedText();
        String entireText = textArea.getText();
        String partA = entireText.substring(0, start);
        String partB = entireText.substring(end);


        textArea.setText(partA + partB);
        setClipboardContents(selectedText);
        textArea.updateUI();


    }

    private void performPaste(JTextArea textArea) {


        // when no text is selected
        if (textArea.getSelectedText() == null) {
            int cursorPos = textArea.getCaretPosition();

            String partA = cursorPos == 0 ? "" : textArea.getText().substring(0, cursorPos);
            String partB = cursorPos == textArea.getText().length() ? "" : textArea.getText().substring(cursorPos);

            textArea.setText(partA + getClipboardContents() + partB);
            textArea.moveCaretPosition(cursorPos);
            textArea.setCaretPosition(cursorPos);
            textArea.updateUI();
        } else {      // when some text is selected ... selected text get replaced by clip board contents
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();

            String partA = start == 0 ? "" : textArea.getText().substring(0, start);
            String partB = end == textArea.getText().length() ? "" : textArea.getText().substring(end);
            textArea.setText(partA + getClipboardContents() + partB);
            textArea.updateUI();
        }


    }

    private void performSelectAll(JTextArea textArea) {
        textArea.selectAll();
    }

    private void performDelete(JTextArea textArea) {
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();

        String entireText = textArea.getText();
        String partA = entireText.substring(0, start);
        String partB = entireText.substring(end);


        textArea.setText(partA + partB);
        textArea.updateUI();
    }

    public JPanel getQueryEditorPanel() {

        return queryEditorPanel;
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == fontButton) {
            FontStyleDialog fontStyleDialog = new FontStyleDialog(mainView, this);
        }

        if (e.getSource() == showQueryHistoryButton) {
            if (historyXML.exists()) {
                Vector<Vector<Object>> queryHistory = readXMLFile(historyXML);
                if (queryHistory != null) {
                    QueryHistoryViewDialog historyViewDialog = new QueryHistoryViewDialog(mainView, this);
                    historyViewDialog.showHistoryTable(queryHistory);
                } else {

                    JOptionPane.showMessageDialog(this, "Sorry, Query log file is seems to be corrupted !", "Query Log Corrupted", JOptionPane.ERROR_MESSAGE, null);

                }
            } else {
                JOptionPane.showMessageDialog(this, "Sorry, No Query log file exist", "Query Log missing", JOptionPane.ERROR_MESSAGE, null);
            }

        }

        if (e.getSource() == openSqlButton) { // Open an .sql or .text file
            JFileChooser openFileChooser = new JFileChooser(".");
            openFileChooser.addChoosableFileFilter(new SQLTextFileFilter("Text"));
            openFileChooser.setFileFilter(new SQLTextFileFilter("Sql"));
            openFileChooser.setAcceptAllFileFilterUsed(false);
            int returnValue = openFileChooser.showOpenDialog(QueryEditorPanel.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = openFileChooser.getSelectedFile();
                try {
                    FileInputStream fileInputStream = new FileInputStream(fileToOpen);
                    StringBuffer buffer = new StringBuffer("");
                    int ch;
                    while ((ch = fileInputStream.read()) != -1) {
                        buffer.append((char) ch);
                    }
                    fileInputStream.close();
                    queryTextArea.setText(buffer.toString());
                } catch (Exception e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }
        if (e.getSource() == saveSqlButton) {
           String sql=queryTextArea.getText();
            JFileChooser saveFileChooser=new JFileChooser();
            saveFileChooser.addChoosableFileFilter(new SQLTextFileFilter("Text"));
            saveFileChooser.setFileFilter(new SQLTextFileFilter("Sql"));
            saveFileChooser.setAcceptAllFileFilterUsed(false);
            int returnValue=saveFileChooser.showSaveDialog(QueryEditorPanel.this);

            if(returnValue==JFileChooser.APPROVE_OPTION){
                String fileName="";
                String fileExtension="";
                String fullFileNameWithDirectory="";
                File file=saveFileChooser.getSelectedFile();
                fileName=file.toString();
                String ext=MiscUtility.getFileExtension(file);
                if(saveFileChooser.getFileFilter().getDescription().equals("Text Files")){ // for text file...

                    if(ext==null ||( !ext.equals("txt")) ){ // e.g file name = sample, so we need to append .txt manually.
                      fileExtension=".txt";
                    }
                }
                else if(saveFileChooser.getFileFilter().getDescription().equals("SQL Files")){ // for text file...
                    if(ext==null||( !ext.equals("sql"))){ // e.g file name = sample, so we need to append .sql manually.
                      fileExtension=".sql";
                    }
                }
                fullFileNameWithDirectory=fileName+fileExtension;
                String msg=MiscUtility.saveQueryAsSQLText(fullFileNameWithDirectory,sql);
                if(msg.equals("Data Saved Successfully")){
                    ImageIcon ico = new ImageIcon(ScanNetworks.class.getResource("images/okICON.png"));
                    JOptionPane.showMessageDialog(null, "Query Output saved successfully to Excel file", "Saved to Excel file", JOptionPane.INFORMATION_MESSAGE, ico);

                }
               }
        }

        if (e.getSource() == queryExecuteButton) {
            tabbedPanel.removeAll();

            String str = queryTextArea.getText().trim();
            if (!DataBaseUtility.isValidSQLQuery(str)) {
                String errorText = "Invalid SQL Query. Please check your Query.";
                JLabel errorLabel = new JLabel(errorText, JLabel.CENTER);
                errorLabel.setFont(new Font("Times New Roman", Font.BOLD, 36));
                errorLabel.setForeground(new Color(220, 20, 60));
                tabbedPanel.add(errorLabel, BorderLayout.CENTER);
            } else {
                tabbedPanel.add(queryTabbedPane, BorderLayout.CENTER);
                queryTabbedPane.removeAll();
                List<String> multipleSqlQry = DataBaseUtility.parseSQL(str);
                queryStatus = new ArrayList<String>(multipleSqlQry.size());
                List<JPanel> tabPanel = getQueryPanels(multipleSqlQry);
                int i = 0;
                for (JPanel panel : tabPanel) {
                    String tabName = queryStatus.get(i);
                    queryTabbedPane.addTab(tabName, panel);
                    i++;
                }
                queryTabbedPane.updateUI();

            }


        }

        if (e.getSource() == queryResetButton) {
            tabbedPanel.removeAll();
            tabbedPanel.add(requestLabel, BorderLayout.CENTER);
            tabbedPanel.updateUI();
            queryTextArea.setText("");
        }
        if (e.getSource() == autoCommitBox) {
            if (autoCommitBox.isSelected()) {
                DataBaseUtility.autoCommit = true;
            } else {
                int userFeedback = JOptionPane.showConfirmDialog(null, "You are switching off Auto Commit Feature. Auto Commit enables you to save (commit) your changes. \n\t\t Really Disable Auto Commit ?", "Disable Auto Commit Feature", JOptionPane.OK_CANCEL_OPTION);
                if (userFeedback == JOptionPane.YES_OPTION) {
                    DataBaseUtility.autoCommit = false;
                } else {
                    autoCommitBox.setSelected(true);
                }
            }
        }
        if (e.getSource() == copyItem || e.getSource() == copyButton) {
            performCopy(queryTextArea);
        }
        if (e.getSource() == cutItem || e.getSource() == cutButton) {
            performCut(queryTextArea);
        }
        if (e.getSource() == pasteItem || e.getSource() == pasteButton) {
            performPaste(queryTextArea);
        }
        if (e.getSource() == selectAllItem) {
            performSelectAll(queryTextArea);
        }
        if (e.getSource() == deleteItem || e.getSource() == deleteButton) {
            performDelete(queryTextArea);
        }
        if (e.getSource() == undoItem || e.getSource() == undoButton) {
            performUndo();
        }
        if (e.getSource() == redoItem || e.getSource() == redoButton) {
            performRedo();
        }

    }

    public JPanel getStylePanel() {
        JPanel topStylePanel = new JPanel();
        topStylePanel.setLayout(new BoxLayout(topStylePanel, BoxLayout.X_AXIS));
        topStylePanel.setBackground(Color.WHITE);


        try {
            cutButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/cutBtn_Out.png"))));
            copyButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/copyBtn_Out.png"))));
            pasteButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/pasteBtn_Out.png"))));
            deleteButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/deleteBtn_Out.png"))));
            undoButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/undo_Out.png"))));
            redoButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/redo_Out.png"))));
            fontButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/fontChooserBtn_Out.png"))));
            showQueryHistoryButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/queryLog_Out.png"))));
            saveSqlButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/save_Out.png"))));
            openSqlButton = new JButton("", new ImageIcon(ImageIO.read(this.getClass().getResource("images/openSqlBtn_Out.png"))));

            MiscUtility.changeButtonLook(cutButton, "cutBtn_In.png", "cutBtn_Out.png", "Cut");
            MiscUtility.changeButtonLook(copyButton, "copyBtn_In.png", "copyBtn_Out.png", "Copy");
            MiscUtility.changeButtonLook(pasteButton, "pasteBtn_In.png", "pasteBtn_Out.png", "Paste");
            MiscUtility.changeButtonLook(deleteButton, "deleteBtn_In.png", "deleteBtn_Out.png", "Delete");
            MiscUtility.changeButtonLook(undoButton, "undo_In.png", "undo_Out.png", "Undo");
            MiscUtility.changeButtonLook(redoButton, "redo_In.png", "redo_Out.png", "Redo");
            MiscUtility.changeButtonLook(fontButton, "fontChooserBtn_In.png", "fontChooserBtn_Out.png", "Choose Font,Font Style & Size");
            MiscUtility.changeButtonLook(showQueryHistoryButton, "queryLog_In.png", "queryLog_Out.png", "Shows the Query Log");
            MiscUtility.changeButtonLook(openSqlButton, "openSqlBtn_In.png", "openSqlBtn_Out.png", "Open .SQL File");
            MiscUtility.changeButtonLook(saveSqlButton, "save_In.png", "save_Out.png", "Save to an .SQL File");

        } catch (IOException e) {
            e.printStackTrace();
        }
        cutButton.setEnabled(false);
        copyButton.setEnabled(false);
        deleteButton.setEnabled(false);

        cutButton.addActionListener(this);
        copyButton.addActionListener(this);
        pasteButton.addActionListener(this);
        deleteButton.addActionListener(this);
        undoButton.addActionListener(this);
        redoButton.addActionListener(this);
        showQueryHistoryButton.addActionListener(this);
        openSqlButton.addActionListener(this);
        saveSqlButton.addActionListener(this);

        fontButton.addActionListener(this);
        queryEditorPanelWidth = (int) (MiscUtility.getScreenSize().getWidth() - MainView.splitAmount) - 600; // 600 is deducted due to borders and layout
        int btnSpaces = queryEditorPanelWidth / 10;
        topStylePanel.add(cutButton);
        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));

        topStylePanel.add(copyButton);
        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));

        topStylePanel.add(pasteButton);
        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));

        topStylePanel.add(deleteButton);
        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));

        topStylePanel.add(undoButton);
        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));

        topStylePanel.add(redoButton);
        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));

        topStylePanel.add(fontButton);

        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));
        topStylePanel.add(showQueryHistoryButton);

        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));

        topStylePanel.add(openSqlButton);
        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));
        topStylePanel.add(saveSqlButton);
        topStylePanel.add(Box.createHorizontalStrut(btnSpaces));

        return topStylePanel;
    }


    public void writeQueryFromHistory(String text) {
        String oldText = queryTextArea.getText();
        queryTextArea.setText(oldText + text);
        queryTextArea.updateUI();
    }

    private List<JPanel> getQueryPanels(List<String> arr) {
        int queryNo = 0;
        List<JPanel> panels = new ArrayList<JPanel>();


        for (String sql : arr) {
            long startTime = Long.parseLong(MiscUtility.getCurrentSysDateTime().get("time"));
            queryNo++;
            JPanel queryPanel = new JPanel(new BorderLayout());
            JTable table = new JTable();
            table.setAutoCreateRowSorter(true);

            table.getTableHeader().setReorderingAllowed(false);
            DefaultTableModel model;
            try {
                if (DataBaseUtility.isValidSQLQuery(sql)) {
                    double time = 0.0;
                    sql = sql.trim();
                    if (DataBaseUtility.isReadQuery(sql)) {
                        // READ TYPE QUERY
                        model = DataBaseUtility.executeUserReadQuery(sql);
                        table.setModel(model);
                        int col = DataBaseUtility.getColumnNoForCustomizedQry();
                        final Vector<String> queryColumnNames = DataBaseUtility.getColumnNamesForCustomizedQry();
                        final Vector<Vector<Object>> rowData = DataBaseUtility.getDataVectorForCustomizedQry();
                        List<Integer> columnsSize = MiscUtility.getMaxLengthsForColumns(rowData, queryColumnNames);
                        TableColumnModel tableColumnModel = table.getColumnModel();
                        TableColumn tabColumn = new TableColumn();


                        int sumDataColWidth = 0;
                        for (int i = 0; i < col; i++) {
                            tabColumn = tableColumnModel.getColumn(i);
                            tabColumn.setCellRenderer(tableRenderer);
                            int v = MiscUtility.adjustTableColumnWidth(tableRenderer, columnsSize.get(i));
                            sumDataColWidth += v;
                            tabColumn.setMinWidth(v);
                            table.getTableHeader().setResizingAllowed(true);
                        }
                        double viewWidth = QueryEditorPanel.this.rightPanel.getSize().getWidth();
                        if (sumDataColWidth > viewWidth) {
                            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                        }

                        JScrollPane allRowsScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                        allRowsScrollPane.setAutoscrolls(true);
                        long endTime = Long.parseLong(MiscUtility.getCurrentSysDateTime().get("time"));

                        time = MiscUtility.getDifferenceTime(startTime, endTime);


                        saveResultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        saveResultPanel.setBackground(Color.WHITE);
                        final JButton saveResultButton = new JButton("<html><b>Save Result</b></html>", new ImageIcon(ScanNetworks.class.getResource("images/saveToExcel.png")));
                        saveResultButton.setToolTipText("Save Query output to an Excel file");
                        saveResultButton.setEnabled(false);
                        if (table.getRowCount() > 0) {
                            saveResultButton.setEnabled(true);
                        }
                        saveResultButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                             // TODO
                                String fileName = "";
                                String fileExtension = "";
                                JFileChooser saveResultFileChooser = new JFileChooser(".");
                                FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Sheet", "xls");
                                saveResultFileChooser.addChoosableFileFilter(filter);
                                saveResultFileChooser.setFileFilter(filter);
                                saveResultFileChooser.setAcceptAllFileFilterUsed(false);
                                saveResultFileChooser.setCurrentDirectory(new File("*.xls"));
                                int returnValue = saveResultFileChooser.showSaveDialog(QueryEditorPanel.this);
                                if (returnValue == JFileChooser.APPROVE_OPTION) {
                                    File file = saveResultFileChooser.getSelectedFile();
                                    fileName = file.toString();
                                    if (saveResultFileChooser.getFileFilter().getDescription().equals("Excel Sheet")) {
                                        if (fileName.endsWith(".xls")) {
                                            fileExtension = "";
                                        } else if (fileName.endsWith(".xlsx")) {
                                            fileName = fileName.substring(0, fileName.length() - 1);
                                            fileExtension = "";
                                        } else {
                                            fileExtension = ".xls";
                                        }

                                    } else {
                                        fileExtension = "";
                                    }

                                    String fullFileNameWithDirectory = fileName + fileExtension;
                                    String saveResult = MiscUtility.exportRecordToExcel(fullFileNameWithDirectory, "Exported Report Sheet", queryColumnNames, rowData);
                                    if (saveResult.equals("Data Saved Successfully")) {
                                        ImageIcon ico = new ImageIcon(ScanNetworks.class.getResource("images/okICON.png"));
                                        JOptionPane.showMessageDialog(null, "Query Output saved successfully to Excel file", "Saved to Excel file", JOptionPane.INFORMATION_MESSAGE, ico);
                                        saveResultButton.setText("<html><b>Result Saved</b></html>");
                                        saveResultButton.setIcon(new ImageIcon(ScanNetworks.class.getResource("images/saveToExcel.png")));
                                        saveResultButton.setEnabled(false);

                                    } else {
                                        ImageIcon ico = new ImageIcon(ScanNetworks.class.getResource("images/errorIcon.png"));
                                        JOptionPane.showMessageDialog(null, "Unable to Save Query output. Error Occurred \n " + saveResult, "Not Saved to Excel file", JOptionPane.ERROR_MESSAGE, ico);
                                    }


                                }
                            }
                        });
                        saveResultPanel.add(saveResultButton);

                        recordPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        recordLabel = new JLabel("No. of Records Fetched : " + String.valueOf(table.getRowCount()));
                        recordLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));

                        recordPanel.add(recordLabel);
                        queryPanel.add(saveResultPanel, BorderLayout.NORTH);
                        queryPanel.add(allRowsScrollPane, BorderLayout.CENTER);
                        queryPanel.add(recordPanel, BorderLayout.SOUTH);
                        queryStatus.add(new String("Query " + queryNo + " Executed Successfully (" + time + ")"));
                    } else {
                        // UPDATE TYPE QUERY
                        List<String> qryReturn = DataBaseUtility.executeUserUpdateQuery(sql);
                        System.out.println("STATUS OF UPDATE QUERY : ");
                        System.out.println(qryReturn);
                        String qryStats = qryReturn.get(0);
                        String rowsEffected = qryReturn.get(1);
                        String errorMsgs = qryReturn.get(2);
                        if (qryStats.equals("true")) {
                            long endTime = Long.parseLong(MiscUtility.getCurrentSysDateTime().get("time"));
                            time = MiscUtility.getDifferenceTime(startTime, endTime);
                            String result = "<html><b><h3 style = \"text-align :center ; \"> No. Of Rows Effected : " + rowsEffected + "</b><p><b><b><h1 style = \"text-align :center ; \">" + MiscUtility.getUpdateQueryMessageForQuery(sql) + "</b></html>";
                            JLabel updateQueryLabel = new JLabel(result, JLabel.CENTER);
                            updateQueryLabel.setForeground(Color.DARK_GRAY);

                            int width = MiscUtility.getScreenSize().width - MainView.splitAmount - 10;
                            int height = MiscUtility.getScreenSize().height - QueryEditorPanel.splitterDistance - 150;

                            Dimension size = new Dimension(width, height);
                            updateQueryLabel.setSize(size);
                            queryPanel.add(updateQueryLabel, BorderLayout.CENTER);
                            queryPanel.setBackground(Color.WHITE);
                            queryStatus.add("Query " + queryNo + " Executed Successfully (" + time + ")");
                            leftPanel.refreshTreeView();
                        }
                        if (qryStats.equals("false")) {
                        }
                        showQueryError(errorMsgs, queryPanel, queryNo);

                    }

                    saveQuery(sql, time);
                } else {
                    String errorText = "Invalid SQL Query. Please check your Query.";
                    JLabel errorLabel = new JLabel(errorText, JLabel.CENTER);
                    errorLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
                    errorLabel.setForeground(new Color(220, 20, 60));
                    queryPanel.add(errorLabel, BorderLayout.CENTER);
                    queryStatus.add(new String("Query " + queryNo + " Not Executed !"));

                }


            } catch (Exception e1) {
                showQueryError(e1.getMessage(), queryPanel, queryNo);
            }

            panels.add(queryPanel);
        }

        return panels;

    }

    private void showQueryError(String msg, JPanel queryPanel, int queryNo) {
        JLabel errorLabel = new JLabel(msg, JLabel.CENTER);
        errorLabel.setForeground(new Color(220, 20, 60));
        errorLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        queryPanel.add(errorLabel, BorderLayout.CENTER);
        queryStatus.add(new String("Query " + queryNo + " Not Executed !"));
    }

    private void saveQuery(String sql, double duration) {
        historyXML = new File("QueryHistory.xml");

        // If history xml file doest not exist
        if (!historyXML.exists()) {
            createNewXMLFile(historyXML, sql, duration);
        } else {  // when xml file does exist
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = null;
            try {
                docBuilder = dbfac.newDocumentBuilder();
                Document doc = docBuilder.parse(historyXML);
                Element root = doc.getDocumentElement();

                if (MiscUtility.isValidXML(root, "Database") && MiscUtility.isValidXML(root, "Date") && MiscUtility.isValidXML(root, "Time")) {
                    modifyXMLFile(historyXML, sql, duration);
                    readXMLFile(historyXML);
                } else {
                    JOptionPane.showMessageDialog(null, "Query History XML file Exist , but it is corrupted. \n Hence all previous query will be lost !", "Query History XML File Corrupted", JOptionPane.INFORMATION_MESSAGE);
                    historyXML.delete();

                    historyXML = new File("QueryHistory.xml");
                    createNewXMLFile(historyXML, sql, duration);
                }

            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }
    }

    public void createNewXMLFile(File historyXML, String sql, double duration) {
        try {
            PrintWriter printWriter = new PrintWriter(historyXML);
            printWriter.flush();
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("Query_History");
            root.setAttribute("Total_Query_Count", String.valueOf(1));
            String database = DataBaseUtility.loginDetails.get("database"); //gets the database name
            Element databaseNode = doc.createElement("Database");
            databaseNode.setAttribute("Name", database);
            databaseNode.setAttribute(database + "_Query_Count", String.valueOf(1));
            Element dateNode = doc.createElement("Date");
            dateNode.setAttribute("Value", MiscUtility.getCurrentSysDateTime().get("date").toString());
            Element timeQueryNode = doc.createElement("Time");
            timeQueryNode.setAttribute("Value", MiscUtility.getCurrentSysDateTime().get("timeFormatted").toString());
            timeQueryNode.setAttribute("Query", sql);
            String host = DataBaseUtility.loginDetails.get("host");
            timeQueryNode.setAttribute("Host", host);
            timeQueryNode.setAttribute("Time_Taken", String.valueOf(duration));
            root.appendChild(databaseNode);
            databaseNode.appendChild(dateNode);
            dateNode.appendChild(timeQueryNode);
            doc.appendChild(root);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            transformer.transform(source, new StreamResult(printWriter));
            printWriter.close();
        } catch (Exception e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void modifyXMLFile(File historyXML, String sql, double duration) {
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.parse(historyXML);
            Element root = doc.getDocumentElement();
            int queryCount;
            queryCount = root.getAttribute("Total_Query_Count").isEmpty() ? 0 : Integer.parseInt(root.getAttribute("Total_Query_Count"));
            queryCount++;
            root.setAttribute("Total_Query_Count", String.valueOf(queryCount));
            String database = DataBaseUtility.loginDetails.get("database");
            Node databaseNode = MiscUtility.getNodeFromXML(root, "Database", database, "Name");


            // searching for a existing database
            boolean databaseNodeFound = databaseNode == null ? false : true;

            // when no databaseNode found for a database
            if (!databaseNodeFound) {
                Element newDatabaseNode = doc.createElement("Database");
                newDatabaseNode.setAttribute("Name", database);
                newDatabaseNode.setAttribute(database + "_Query_Count", String.valueOf(1));
                Element dateNode = doc.createElement("Date");
                dateNode.setAttribute("Value", MiscUtility.getCurrentSysDateTime().get("date").toString());
                Element timeQueryNode = doc.createElement("Time");
                timeQueryNode.setAttribute("Value", MiscUtility.getCurrentSysDateTime().get("timeFormatted").toString());
                timeQueryNode.setAttribute("Query", sql);
                String host = DataBaseUtility.loginDetails.get("host");
                timeQueryNode.setAttribute("Host", host);
                timeQueryNode.setAttribute("Time_Taken", String.valueOf(duration));
                root.appendChild(newDatabaseNode);
                newDatabaseNode.appendChild(dateNode);
                dateNode.appendChild(timeQueryNode);
            } else {  // when database exist
                // Searching for today's date
                int dbQueryCount;
                dbQueryCount = ((Element) databaseNode).getAttribute(database + "_Query_Count").isEmpty() ? 0 : Integer.parseInt(((Element) databaseNode).getAttribute(database + "_Query_Count"));
                dbQueryCount++;
                ((Element) databaseNode).setAttribute(database + "_Query_Count", String.valueOf(dbQueryCount));
                String currDate = MiscUtility.getCurrentSysDateTime().get("date").toString();
                Node childDateNode = MiscUtility.getNodeFromXML(databaseNode, "Date", currDate, "Value");
                boolean dateFound = childDateNode == null ? false : true;
                // when no entry for today's date found
                if (!dateFound) {
                    Element dateNode = doc.createElement("Date");
                    dateNode.setAttribute("Value", MiscUtility.getCurrentSysDateTime().get("date").toString());
                    Element timeQueryNode = doc.createElement("Time");
                    timeQueryNode.setAttribute("Value", MiscUtility.getCurrentSysDateTime().get("timeFormatted").toString());
                    timeQueryNode.setAttribute("Query", sql);
                    String host = DataBaseUtility.loginDetails.get("host");
                    timeQueryNode.setAttribute("Host", host);
                    timeQueryNode.setAttribute("Time_Taken", String.valueOf(duration));
                    databaseNode.appendChild(dateNode);
                    dateNode.appendChild(timeQueryNode);

                } else {  // when entry found for today's date
                    Element timeQueryNode = doc.createElement("Time");
                    timeQueryNode.setAttribute("Value", MiscUtility.getCurrentSysDateTime().get("timeFormatted").toString());
                    timeQueryNode.setAttribute("Query", sql);
                    String host = DataBaseUtility.loginDetails.get("host");
                    timeQueryNode.setAttribute("Host", host);
                    timeQueryNode.setAttribute("Time_Taken", String.valueOf(duration));
                    childDateNode.appendChild(timeQueryNode);

                }

            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            PrintWriter printWriter = new PrintWriter(historyXML);
            printWriter.flush();
            transformer.transform(source, new StreamResult(printWriter));
            printWriter.close();

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public Vector<Vector<Object>> readXMLFile(File historyXML) {
        Vector<Vector<Object>> allRecords = null;
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.parse(historyXML);
            Element root = doc.getDocumentElement();
            allRecords = new Vector<Vector<Object>>();
            int queryNo = 0;
            if (MiscUtility.isValidXML(root, "Database") && MiscUtility.isValidXML(root, "Date") && MiscUtility.isValidXML(root, "Time")) {

                NodeList databaseNodes = root.getElementsByTagName("Database");

                for (int i = 0; i < databaseNodes.getLength(); i++) {
                    Node eachDatabase = databaseNodes.item(i);
                    String dbName = ((Element) eachDatabase).getAttribute("Name");
                    NodeList dateNodes = ((Element) eachDatabase).getElementsByTagName("Date");
                    for (int j = 0; j < dateNodes.getLength(); j++) {
                        Node eachDateNode = dateNodes.item(j);
                        String dateValue = MiscUtility.convertToStandardDateFormat(((Element) eachDateNode).getAttribute("Value"));

                        NodeList queryNodes = ((Element) eachDateNode).getElementsByTagName("Time");
                        for (int k = 0; k < queryNodes.getLength(); k++) {
                            Vector<Object> eachRow = new Vector<Object>();
                            Node eachQueryNode = queryNodes.item(k);
                            String time = MiscUtility.convertToStandardTimeFormat(((Element) eachQueryNode).getAttribute("Value"));
                            queryNo++;
                            String query = ((Element) eachQueryNode).hasAttribute("Query") ? ((Element) eachQueryNode).getAttribute("Query") : "N/A";
                            String hostIP = ((Element) eachQueryNode).hasAttribute("Host") ? ((Element) eachQueryNode).getAttribute("Host") : "N/A";
                            String duration = ((Element) eachQueryNode).hasAttribute("Time_Taken") ? ((Element) eachQueryNode).getAttribute("Time_Taken") : "N/A";
//                            System.out.print("\n\n" + queryNo + "       " + query + "       " + dbName + "     " + hostIP + "     " + time + "     " + dateValue);
                            eachRow.add(new Boolean(false));
                            eachRow.add(queryNo);
                            eachRow.add(query);
                            eachRow.add(dbName);
                            eachRow.add(duration);
                            eachRow.add(hostIP);
                            eachRow.add(time);
                            eachRow.add(dateValue);
                            allRecords.add(eachRow);
                        }

                    }

                }


            } else {
                return null;
            }

        } catch (Exception e) {

        }
        return allRecords;
    }


    private void performRedo() {
        if (undoManager.canRedo()) {
            undoManager.redo();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void performUndo() {
        if (undoManager.canUndo()) {
            undoManager.undo();
            queryTextArea.updateUI();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }


    public void setQueryTextAreaFont(Font font) {
        queryTextArea.setFont(font);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        // UNDO
        if ((e.getKeyCode() == KeyEvent.VK_Z) && (e.isControlDown())) {
            performUndo();

        }
        // REDO
        if ((e.getKeyCode() == KeyEvent.VK_Y) && (e.isControlDown())) {
            performRedo();
        }
        if ((e.getKeyCode() == KeyEvent.VK_A) && (e.isControlDown())) {
            cutButton.setEnabled(true);
            copyButton.setEnabled(true);
            deleteButton.setEnabled(true);


        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT) || (e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_KP_UP) || (e.getKeyCode() == KeyEvent.VK_KP_DOWN) || (e.getKeyCode() == KeyEvent.VK_KP_LEFT) || (e.getKeyCode() == KeyEvent.VK_KP_RIGHT)) {
            if (e.isShiftDown()) {

                // when start and end are different then some text is selected and enable the btns...but when start and end are eql then no text is selected...hence dis the btns
                if (enableCopyPaste) {

                    cutButton.setEnabled(true);
                    copyButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                    disableButton = false;

                } else {
                    cutButton.setEnabled(false);
                    copyButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    disableButton = true;
                }

            }

        }
    }

    public void keyReleased(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
