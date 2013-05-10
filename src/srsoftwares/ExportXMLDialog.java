package srsoftwares;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Apr 26, 2011
 * Time: 1:16:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExportXMLDialog extends JDialog {
    private JPanel cardPanel;
    private List<String> tableNames;
    private JPanel firstPanel;
    private JPanel secondPanel;
    private JButton prevButton;
    private JButton nextButton;
    public static Map<DefaultMutableTreeNode, JCheckBox> treeMap;     /// LAST DONE
    private JTree tree;
    private static final String FIRST = "First";
    private static final String SECOND = "second";
    private int checkedTimes = 0;
    private JPanel buttonPanel;
    private CardLayout cardLayout;
    private JFileChooser jFileChooser;
    private JPanel mainPanel;
    private List<String> allSelectedTables;
    private String schemaName;

    public ExportXMLDialog(String schema) {
        schemaName = schema;
        setTitle("Export Tables for Schema :" + schemaName);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());

        cardPanel = new JPanel(new CardLayout());
        cardPanel.setBorder(BorderFactory.createEtchedBorder());

        createFirstPanel(schemaName);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.add(firstPanel);

        secondPanel = new JPanel(new BorderLayout());

        jFileChooser = new JFileChooser();
        jFileChooser.setControlButtonsAreShown(false);

        secondPanel.add(jFileChooser);
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);


        cardPanel.add(firstPanel, FIRST);
        cardPanel.add(secondPanel, SECOND);

        mainPanel.add(cardPanel, BorderLayout.CENTER);

        createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        int midHeight = screenHeight / 2;
        int midWidth = screenWidth / 2;
        this.setLocation(midWidth - 200, midHeight - 200);


        //this.setLocationRelativeTo(null);
        setSize(600, 400);
        setVisible(true);
    }

    public void createFirstPanel(String schemaName) {
        tableNames = DataBaseUtility.getTableNames(schemaName);

        firstPanel = new JPanel();
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // CREATION OF UPPER PANEL

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(schemaName);
        tree = new JTree(root);

        treeMap = new HashMap<DefaultMutableTreeNode, JCheckBox>();
        firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.Y_AXIS));
        for (String str : tableNames) {
            DefaultMutableTreeNode tableName = new DefaultMutableTreeNode(str);
            root.add(tableName);
            JCheckBox chkBox = new JCheckBox(str);
            treeMap.put(tableName, chkBox);

        }
        tree.expandRow(0);

        tree.setCellRenderer(new ExportXMLTreeRenderer());
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                activateCheckBox(e);
            }
        });


        JScrollPane scrollPane = new JScrollPane(tree);
        firstPanel.add(scrollPane);
        firstPanel.setVisible(true);
        // CREATION OF LOWER PANEL


    }

    private void activateCheckBox(MouseEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        int level = node.getLevel();
        if (level == 1) {
            JCheckBox chkBox = treeMap.get(node);

            if (chkBox.isSelected()) {
                chkBox.setSelected(false);
                checkedTimes--;

            } else {
                chkBox.setSelected(true);
                checkedTimes++;

            }
            if (checkedTimes > 0)
                nextButton.setEnabled(true);
            else
                nextButton.setEnabled(false);
            tree.updateUI();

        }

    }

    

    public void createButtonPanel() {

        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        nextButton.setEnabled(false);
        prevButton.setEnabled(false);
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // NAVIGATE TO NEXT CARD
                if (nextButton.getText().equals("Next")) {
                    cardLayout.next(cardPanel);
                    allSelectedTables=new ArrayList<String>();
                    prevButton.setEnabled(true);
                    ///////////////////////////////////////////////
                    
                    for (DefaultMutableTreeNode defaultMutableTreeNode : treeMap.keySet()) {
                     JCheckBox chkBox = treeMap.get(defaultMutableTreeNode);

                     if (chkBox.isSelected()) {
                        allSelectedTables.add(chkBox.getText());
                        }

                    }
                  nextButton.setText("Finish");
                }


                // SAVE THE FILE AS XML AT A GIVEN LOCATION

                else if (nextButton.getText().equals("Finish")){

                    JTextField txtName = getChooserComponents(jFileChooser);
                    String fileFullName = txtName.getText();


                    File dirName = jFileChooser.getCurrentDirectory();
                    File file = new File(dirName + File.separator + fileFullName);
                    GenerateXMLFile.createXMLFile(file,schemaName,allSelectedTables);

                    ExportXMLDialog.this.dispose();
                }

            }
        }

        );
        prevButton.addActionListener(new

                ActionListener() {
                    public void actionPerformed
                            (ActionEvent
                                    e) {
                        cardLayout.previous(cardPanel);
                        prevButton.setEnabled(false);
                        nextButton.setEnabled(true);
                        nextButton.setText("Next");
                    }
                }

        );

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);


    }

    private JTextField getChooserComponents(JComponent chooser) {
        Component[] components = chooser.getComponents();
        JTextField txtField = null;
        for (Component component : components) {
            if (component instanceof JTextField) {
                txtField = (JTextField) component;
                return txtField;
            } else if (component instanceof JComponent &&
                    ((JComponent) component).getComponents().length > 0) {
                txtField = getChooserComponents((JComponent) component);
                if (txtField != null) return txtField;
            }
        }
        return txtField;
    }


}

