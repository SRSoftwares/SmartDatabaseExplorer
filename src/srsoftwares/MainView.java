package srsoftwares;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: Apr 13, 2011
 * Time: 3:54:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainView extends JFrame implements TreeSelectionListener, ActionListener {
    public JPanel rightPanel;
    private JTree tree;
    private String schemaNameDialog;
    private String tableNameDialog;
    private JPanel statusPanel;
    private JLabel userNameLabel;
    private JLabel databaseLabel;
    private JLabel systemNameLabel;
    private JLabel portLabel;
    private JLabel loggedTimeLabel;
    static int splitAmount = 250;
    private int maxTryCountForDBClose = 10;
    private Map<String,String> loginDetails;
    private MainViewLeftPanel leftObj;


    MainView(LoginDialog ld, String type, Map<String,String> loginDetails) {
        this.loginDetails = loginDetails;
        Calendar calendar = new GregorianCalendar();

        int h = calendar.get(Calendar.HOUR);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        String hour = String.valueOf(h);
        String minute = String.valueOf(m);
        String seconds = String.valueOf(s);
        System.out.println("Load >> " + hour + ":" + minute + ":" + seconds);
        String DATE_FORMAT_NOW = "dd/MM/yyyy hh:mm aa";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        loginDetails.put("Login Time", sdf.format(cal.getTime()));

        ld.dispose();

        setTitle("Central View, Smart Database Explorer " + LoginDialog.versionId+" SR Softwares");
          Image image = null;
        try {
            image = ImageIO.read(this.getClass().getResource("images/mainViewIcon.png"));
        } catch (Exception e) {

        }
        this.setIconImage(image);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel componentPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftObj = new MainViewLeftPanel(loginDetails.get("Instance"),loginDetails.get("Database"));
        leftPanel.add(leftObj, BorderLayout.CENTER);
        tree = leftObj.getTree();
        int width=splitAmount;
        int height=MiscUtility.getScreenSize().height-100;
        Dimension size=new Dimension(width,height);
        leftPanel.setPreferredSize(size);
        leftPanel.setMaximumSize(size);
        leftPanel.setMinimumSize(size);
        tree.expandRow(0);
        tree.addTreeSelectionListener(this);
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showPopUp(e);
            }
        });

        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setLayout(new BorderLayout());
        MainViewRightPanel panel=new MainViewRightPanel(leftObj,this);
        panel.showSystemInformation();

        rightPanel.add(panel, BorderLayout.CENTER);
        rightPanel.setBackground(Color.WHITE);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(splitAmount);
        splitPane.setEnabled(false);
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        componentPanel.add(splitPane, BorderLayout.CENTER);
        statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        prepareStatusPanel();

        componentPanel.add(statusPanel, BorderLayout.SOUTH);


        add(componentPanel);
        setSize(Toolkit.getDefaultToolkit().getScreenSize());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            //  System.out.println("Width of Right Panel = "+rightPanel.getSize().getWidth());
            }
        });
        //this.setResizable(false);
        this.setBackground(Color.WHITE);
        JOptionPane.showMessageDialog(null, "Connection Successful, Click Ok to Continue", type, JOptionPane.INFORMATION_MESSAGE);
        setVisible(true);
         h = calendar.get(Calendar.HOUR);
         m = calendar.get(Calendar.MINUTE);
         s = calendar.get(Calendar.SECOND);
         hour = String.valueOf(h);
         minute = String.valueOf(m);
         seconds = String.valueOf(s);
        System.out.println("Loaded >> " + hour + ":" + minute + ":" + seconds);
    }

    private void prepareStatusPanel() {

        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

        userNameLabel = new JLabel("  User: " + loginDetails.get("User Name"), JLabel.CENTER);
        userNameLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        databaseLabel = new JLabel("  Database: " + loginDetails.get("Database"), JLabel.CENTER);
        databaseLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        portLabel = new JLabel("  Port: " + loginDetails.get("Port"), JLabel.CENTER);
        portLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        systemNameLabel = new JLabel("  System : " + loginDetails.get("System"), JLabel.CENTER);
        systemNameLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        loggedTimeLabel = new JLabel("  Logged in : " + loginDetails.get("Login Time"), JLabel.CENTER);
        loggedTimeLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        statusPanel.add(Box.createHorizontalStrut(40));
        statusPanel.add(userNameLabel);

        statusPanel.add(Box.createHorizontalStrut(100));
        statusPanel.add(databaseLabel);

        statusPanel.add(Box.createHorizontalStrut(100));
        statusPanel.add(portLabel);

        statusPanel.add(Box.createHorizontalStrut(100));
        statusPanel.add(systemNameLabel);

        statusPanel.add(Box.createHorizontalStrut(100));
        statusPanel.add(loggedTimeLabel);

        statusPanel.add(Box.createHorizontalStrut(40));


    }


    private void showPopUp(MouseEvent e) {

        tree.setSelectionPath(tree.getClosestPathForLocation(e.getX(), e.getY()));

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        int level = node.getLevel();
        if (level == 2 && (e.getButton() == e.BUTTON3)) {
            schemaNameDialog = String.valueOf(node.getUserObject());
            JPopupMenu popName = new JPopupMenu();
            JMenuItem item = new JMenuItem("Export Schema");
            popName.add(item);
            popName.show(tree, e.getX(), e.getY());
            item.addActionListener(this);
        }


        if (level == 3 && (e.getButton() == e.BUTTON3)) {
            tableNameDialog = String.valueOf(node.getUserObject());
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
            JPopupMenu popTable = new JPopupMenu();
            JMenuItem item = new JMenuItem("Export Table");
            popTable.add(item);
            popTable.show(tree, e.getX(), e.getY());
            final String selectedSchemaName = String.valueOf(parentNode.getUserObject());
            final String selectedTableName = String.valueOf(node.getUserObject());
            item.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {

                    ExportTableToXML obj = new ExportTableToXML(selectedSchemaName, selectedTableName);
                }
            });

        }
    }

    public void showSystemInformation(){
      rightPanel.removeAll();
      MainViewRightPanel panel=new MainViewRightPanel(leftObj,this);
      panel.showSystemInformation();
      rightPanel.add(panel,BorderLayout.CENTER);
      rightPanel.updateUI();
    }

    public void showSystemSchemaInformation() {
        rightPanel.removeAll();
        MainViewRightPanel panel = new MainViewRightPanel(leftObj,this);
        panel.addTableSchema();
        rightPanel.add(panel, BorderLayout.CENTER);
        rightPanel.updateUI();

    }

    public void addTable(String str) {
        rightPanel.removeAll();
        MainViewRightPanel panel = new MainViewRightPanel(leftObj,this);

        panel.addTableNames(str);

        rightPanel.add(panel, BorderLayout.CENTER);
        rightPanel.updateUI();


    }

    public void addTableDetails(String str, String parentName) {
        rightPanel.removeAll();
        MainViewRightPanel panel = new MainViewRightPanel(leftObj,this);
        rightPanel.add(panel, BorderLayout.CENTER);
        rightPanel.updateUI();
        panel.addTableDetails(str, parentName);
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        String str = String.valueOf(node.getUserObject());
        int level = node.getLevel();
        if(level ==0){           // WHEN USER CLICKS ON THE SYSTEM
             showSystemInformation();
        }
        else if (level == 1) {  // WHEN USER CLICKS ON THE INSTANCE
            showSystemSchemaInformation();
        } else if (level == 2) {
            addTable(str);      // WHEN USER CLICKS ON A SCHEMA


        } else if (level == 3) {  // WHEN USER CLICKS ON A TABLE
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            String parentName = String.valueOf(parent.getUserObject());
            addTableDetails(str, parentName);
        }


    }

    public void actionPerformed(ActionEvent e) {
        ExportXMLDialog dialogWindow = new ExportXMLDialog(schemaNameDialog);
    }
}
