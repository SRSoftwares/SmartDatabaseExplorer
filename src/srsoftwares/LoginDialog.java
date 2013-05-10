package srsoftwares;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: Apr 11, 2011
 * Time: 2:18:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginDialog extends JDialog implements ItemListener, ActionListener {

    private JPanel componentPanel;
    private JPanel upperPanel;
    private JPanel userNamePanel;
    private JPanel passWordPanel;
    private JPanel hostPanel;
    private JPanel dataBasePanel;
    private JPanel portPanel;
    private JPanel lowerPanel;
    private JPanel animatedPanel;
    private JPanel rememberOptionPanel;
    private JPanel instancePanel;

    private JTextField userNameField;
    private JTextField portField;
    private JTextField instanceField;
    private JPasswordField passWordField;

    private JMenuBar loginMenuBar;
    private JMenu themesMenu;
    private JMenu helpMenu;
    private JMenuItem helpItem;

    private JComboBox dataComboBox;
    private JCheckBox rememberCheckBox;

    private JCheckBoxMenuItem[] menuItems;


    private JButton cancelButton;
    private JButton connectButton;

    private int uNameWidth;

    private Map<String, String> ipMap;

    private Properties loginProperties;
    private Properties hostCollections;
    private File loginFile = new File("login.Properties");
    private File hostCollectionFile = new File("hostCollection.Properties");

    private String user;
    private String pwd;
    private String host;
    private String port;
    private String instanceName;
    private String dataBaseName;
    private Map<String, String> loginDetails;
    private int clickCount;
    private JMenuItem aboutItem;


    private Dimension fieldDimension;
    private Dimension panelDimension;
    public static String versionId = " v1.2";
    private String[] items;
    private JLabel configLabel;
    private JComboBox hostBox;
    private String separator = "|";
    private boolean errorGenerated;
    private MainView mainView;
    private static LoginDialog loginDialog;
    private String hostIndex;
    private JCheckBox showPassword;
    private JLabel showLabel;
    private JLabel passWordLabel;
    private JTextField passwordTextField;
    private JDialog animatedDialog;
    private JCheckBox showPasswordCheckBox;


    LoginDialog(String str) {
        // MiscUtility.getClipboardContents();


        readFromHostCollectionPropertyFile();
        clickCount = 0;
        ipMap = new HashMap<String, String>();
        fieldDimension = new Dimension(200, 25);
        panelDimension = new Dimension(367, 27);
        items = new String[]{"Derby", "Oracle", "MySql", "Microsoft SQL Server"};
        loginDetails = new HashMap<String, String>();
        showPasswordCheckBox = new JCheckBox();
        this.setTitle(str);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        loginProperties = new Properties();
        hostCollections = new Properties();

        portField = new JTextField("");
        Image image = null;
        try {
            image = ImageIO.read(this.getClass().getResource("images/loginAccount.png"));
        } catch (Exception e) {

        }
        this.setIconImage(image);


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int w = e.getWindow().getSize().width;
                int h = e.getWindow().getSize().height;
                //  System.out.println("Optimized View : Width = " + w + " Height = " + h);
                //   System.out.println("userName Field = " + passWordPanel.getSize());

            }
        });

        animatedPanel = new JPanel();

        createMenuBar();

        // Creating Upper Panel & Adding Components
        createUpperPanel();

        // Creating Lower Panel & Adding Components
        createLowerPanel();

        // Creating Component Panel & Adding Upper Panel & Lower Panel
        this.setSize(366, 365);
        this.setModal(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;


        int midHeight = screenHeight / 2;
        int midWidth = screenWidth / 2;
        int compMidWidth = (int) (this.getSize().getWidth() / 2);
        int compMidHeight = (int) (this.getSize().getHeight() / 2);

        this.setLocation(midWidth - compMidWidth, midHeight - compMidHeight);

        createComponentPanel();
        readFromLoginPropertyFile();
        readFromHostCollectionPropertyFile();
        this.setJMenuBar(loginMenuBar);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);


    }

    private void createMenuBar() {
        loginMenuBar = new JMenuBar();
        loginMenuBar.setBackground(Color.WHITE);

        themesMenu = new JMenu("Themes");
        themesMenu.setBackground(Color.WHITE);

        helpMenu = new JMenu("Help");
        helpMenu.setBackground(Color.WHITE);

        helpItem = new JMenuItem("Help");
        helpItem.setBackground(Color.WHITE);
        helpItem.addActionListener(this);

        aboutItem = new JMenuItem("About");
        aboutItem.setBackground(Color.WHITE);
        aboutItem.addActionListener(this);

        setLookNFeelMenuItems();
        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);

        loginMenuBar.add(themesMenu);
        loginMenuBar.add(helpMenu);
    }

    public void createComponentPanel() {
        componentPanel = new JPanel();
        BoxLayout componentBoxLayout = new BoxLayout(componentPanel, BoxLayout.Y_AXIS);
        componentPanel.setLayout(componentBoxLayout);

        componentPanel.add(upperPanel);
        componentPanel.add(Box.createVerticalStrut(7));
        componentPanel.add(new JSeparator());
        componentPanel.add(Box.createVerticalStrut(5));
        componentPanel.add(lowerPanel);


        add(componentPanel, BorderLayout.CENTER);
        add(animatedPanel, BorderLayout.SOUTH);

        componentPanel.setBackground(Color.WHITE);
        animatedPanel.setBackground(Color.WHITE);
        this.setBackground(Color.WHITE);
        componentPanel.updateUI();

    }

    public void createUpperPanel() {
        upperPanel = new JPanel();
        upperPanel.setBackground(Color.WHITE);
        BoxLayout upperPanelBoxLayout = new BoxLayout(upperPanel, BoxLayout.Y_AXIS);
        upperPanel.setLayout(upperPanelBoxLayout);
        upperPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // CREATING USER NAME COMPONENTS
        createUserNamePanel();

        // CREATING PASSWORD COMPONENTS
        createPassWordPanel();

        // Create instance panel
        createInstancePanel();

        // CREATING HOST COMPONENTS
        createHostPanel();

        // CREATING DATA-BASE COMPONENTS
        createDataBasePanel();

        // CREATING PORT COMPONENTS
        createPortPanel();

        // create RememberOptionPanel
        createRememberOptionPanel();

        upperPanel.add(userNamePanel);
        upperPanel.add(Box.createVerticalStrut(6));
        upperPanel.add(passWordPanel);
        upperPanel.add(Box.createVerticalStrut(6));
        upperPanel.add(instancePanel);
        upperPanel.add(Box.createVerticalStrut(6));
        upperPanel.add(hostPanel);
        upperPanel.add(Box.createVerticalStrut(6));
        upperPanel.add(dataBasePanel);
        upperPanel.add(Box.createVerticalStrut(6));
        upperPanel.add(portPanel);
        upperPanel.add(Box.createVerticalStrut(6));
        upperPanel.add(rememberOptionPanel);


    }

    public void createUserNamePanel() {
        userNamePanel = new JPanel();
        userNamePanel.setPreferredSize(panelDimension);
        userNamePanel.setMaximumSize(panelDimension);
        userNamePanel.setMinimumSize(panelDimension);
        userNamePanel.setToolTipText("Please Enter Your Username");
        userNamePanel.setBackground(Color.WHITE);
        BoxLayout userNameBoxLayout = new BoxLayout(userNamePanel, BoxLayout.X_AXIS);
        userNamePanel.setLayout(userNameBoxLayout);
        JLabel userNameLabel = new JLabel("Username ");

        userNameField = new JTextField("");

        userNameField.setToolTipText("Please Enter Your Username");

        userNameField.setPreferredSize(fieldDimension);
        userNameField.setMaximumSize(fieldDimension);
        userNameField.setMinimumSize(fieldDimension);
        userNameField.updateUI();

        FontMetrics uNameMetrics = userNameLabel.getFontMetrics(userNameLabel.getFont());
        uNameWidth = uNameMetrics.stringWidth(userNameLabel.getText());

        userNamePanel.add(userNameLabel);
        userNamePanel.add(Box.createHorizontalStrut(10));
        userNamePanel.add(userNameField);


        userNamePanel.updateUI();
        userNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    connectButton.doClick();
                }
            }
        });


    }

    public void createPassWordPanel() {
        passWordPanel = new JPanel();
        passWordPanel.setToolTipText("Please Enter your Password");
        passWordPanel.setBackground(Color.WHITE);
        BoxLayout passWordBoxLayout = new BoxLayout(passWordPanel, BoxLayout.X_AXIS);
        passWordPanel.setLayout(passWordBoxLayout);
        passWordLabel = new JLabel("Password");
        showPassword = new JCheckBox("<html><h5 style = \"text-align :center\">Show<br>Password</html>");

        showPassword.setSelected(false);
        showPassword.setBackground(Color.WHITE);
       // showLabel = new JLabel("<html><h5 style = \"text-align :center\">Show<br>Password</html>");
       // showLabel.setBackground(Color.WHITE);
        passWordField = new JPasswordField("");
        passWordField.setToolTipText("Please Enter your Password");
        passWordField.setPreferredSize(fieldDimension);
        passWordField.setMaximumSize(fieldDimension);
        passWordField.setMinimumSize(fieldDimension);
        FontMetrics uPassWordMetrics = passWordLabel.getFontMetrics(passWordLabel.getFont());
        int uPassWordWidth = uPassWordMetrics.stringWidth(passWordLabel.getText());
        passWordPanel.setPreferredSize(panelDimension);
        passWordPanel.setMaximumSize(panelDimension);
        passWordPanel.setMinimumSize(panelDimension);
        passWordPanel.add(passWordLabel);

        passWordPanel.add(Box.createHorizontalStrut((uNameWidth - uPassWordWidth) + 10));
        passWordPanel.add(passWordField);
        passWordPanel.add(Box.createHorizontalStrut(4));
        passWordPanel.add(showPassword);
        passWordPanel.add(Box.createHorizontalStrut(2));
       // passWordPanel.add(showLabel);
        passWordPanel.updateUI();
        passWordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    connectButton.doClick();
                }
            }
        });

        showPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showPasswordCheckBox = (JCheckBox) e.getSource();
                showPasswordCheckBox.setText("<html><h5 style = \"text-align :center\">Show<br>Password</html>");

                String pass = String.valueOf(passWordField.getPassword()).trim();

                FontMetrics uPassWordMetrics = passWordLabel.getFontMetrics(passWordLabel.getFont());
                int uPassWordWidth = uPassWordMetrics.stringWidth(passWordLabel.getText());

                if (showPasswordCheckBox.isSelected()) {
                    passwordTextField = new JTextField(pass);
                    passWordPanel.remove(passWordField);
                    passWordPanel.remove(showPassword);
                   // passWordPanel.remove(showLabel);
                    showPassword.setSelected(true);
                    passwordTextField.setBackground(Color.WHITE);
                    passwordTextField.setPreferredSize(fieldDimension);
                    passwordTextField.setMaximumSize(fieldDimension);
                    passwordTextField.setMinimumSize(fieldDimension);
                    passWordPanel.add(Box.createHorizontalStrut((uNameWidth - uPassWordWidth) - 11));
                    passWordPanel.add(passwordTextField);
                    passWordPanel.add(Box.createHorizontalStrut(4));
                    passWordPanel.add(showPassword);
                    passWordPanel.add(Box.createHorizontalStrut(2));
                   // passWordPanel.add(showLabel);
                    passWordPanel.updateUI();
                    passwordTextField.requestFocus();

                } else {
                    passWordField.setText(passwordTextField.getText());
                    passWordPanel.removeAll();
                    passWordPanel.add(passWordLabel);
                    showPasswordCheckBox.setSelected(false);
                    passWordField.setBackground(Color.WHITE);
                    passWordField.setToolTipText("Please Enter your Password");
                    passWordField.setPreferredSize(fieldDimension);
                    passWordField.setMaximumSize(fieldDimension);
                    passWordField.setMinimumSize(fieldDimension);
                    passWordPanel.add(Box.createHorizontalStrut((uNameWidth - uPassWordWidth) + 10));
                    passWordPanel.add(passWordField);
                    passWordPanel.add(Box.createHorizontalStrut(4));
                    passWordPanel.add(showPassword);
                    passWordPanel.add(Box.createHorizontalStrut(2));
                   // passWordPanel.add(showLabel);
                    passWordField.requestFocus();
                    passWordPanel.updateUI();

                }
            }
        });

    }

    public void createInstancePanel() {
        instancePanel = new JPanel();
        instancePanel.setBackground(Color.WHITE);
        instancePanel.setToolTipText("Please Enter your instance Name, Instance name could be same as username, Oracle users enter XE as default instance name");
        BoxLayout instanceBoxLayout = new BoxLayout(instancePanel, BoxLayout.X_AXIS);
        instancePanel.setLayout(instanceBoxLayout);
        JLabel instanceLabel = new JLabel("Instance");
        instanceField = new JTextField("");
        instanceField.setToolTipText("Please Enter your instance Name, Instance name could be same as username, Oracle users enter XE as default instance name");
        instanceField.setPreferredSize(fieldDimension);
        instanceField.setMaximumSize(fieldDimension);
        instanceField.setMinimumSize(fieldDimension);
        instancePanel.setPreferredSize(panelDimension);
        instancePanel.setMaximumSize(panelDimension);
        instancePanel.setMinimumSize(panelDimension);
        FontMetrics instanceMetrics = instanceLabel.getFontMetrics(instanceLabel.getFont());
        int instanceWidth = instanceMetrics.stringWidth(instanceLabel.getText());

        instancePanel.add(instanceLabel);
        instancePanel.add(Box.createHorizontalStrut((uNameWidth - instanceWidth) + 10));
        instancePanel.add(instanceField);
        instanceField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    connectButton.doClick();
                }
            }
        });

    }

    public void createHostPanel() {
        hostPanel = new JPanel();
        hostPanel.setBackground(Color.WHITE);
        BoxLayout hostBoxLayout = new BoxLayout(hostPanel, BoxLayout.X_AXIS);
        hostPanel.setLayout(hostBoxLayout);
        hostPanel.setToolTipText("Please enter your host name or ip address, default host is 127.0.0.1 or localhost");
        JLabel hostLabel = new JLabel("Host");

        hostBox = new JComboBox();
        String ip = "127.0.0.1";  // by default we consider that default ip 127.0.0.1 is the ip address
        String sysName = "";
        sysName = new ScanNetworks().getSysIP(); // and system ip is the system name
        if (sysName.equals("127.0.0.1")) {
            hostBox.addItem(new String(ip + separator + "localhost"));
        } else {
            hostBox.addItem(new String(ip + separator + sysName));
        }


        hostBox.setToolTipText("Please choose your host name or ip address, default host is 127.0.0.1 or localhost");
        hostBox.setEditable(true);
        hostBox.setPreferredSize(fieldDimension);
        hostBox.setMaximumSize(fieldDimension);
        hostBox.setMinimumSize(fieldDimension);
        hostPanel.setPreferredSize(panelDimension);
        hostPanel.setMaximumSize(panelDimension);
        hostPanel.setMinimumSize(panelDimension);
        MiscUtility.addScrollAction(hostBox);


        FontMetrics hostMetrics = hostLabel.getFontMetrics(hostLabel.getFont());
        int hostWidth = hostMetrics.stringWidth(hostLabel.getText());

        hostPanel.add(hostLabel);
        hostPanel.add(Box.createHorizontalStrut((uNameWidth - hostWidth) + 10));
        hostPanel.add(hostBox);

        Image image = null;
        try {
            image = ImageIO.read(this.getClass().getResource("images/setSmall.png"));
        } catch (Exception e) {
        }
        configLabel = new JLabel();
        configLabel.setToolTipText("Click to Scan Networks for all available host names");
        configLabel.setIcon(new ImageIcon(image));
        hostPanel.add(Box.createHorizontalStrut(3));
        hostPanel.add(configLabel);
        final Image finalImage = image;
        configLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                LoginDialog obj = LoginDialog.this;
                ScanNetworks scn = new ScanNetworks(obj);
                System.out.println(ipMap);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);    //To change body of overridden methods use File | Settings | File Templates.
                Image imageBig = null;
                try {
                    imageBig = ImageIO.read(this.getClass().getResource("images/setBig.png"));
                } catch (Exception e1) {
                }
                configLabel.setIcon(new ImageIcon(imageBig));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);    //To change body of overridden methods use File | Settings | File Templates.
                configLabel.setIcon(new ImageIcon(finalImage));

            }
        });
        hostBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    connectButton.doClick();
                }
            }
        });
    }

    public void createDataBasePanel() {
        dataBasePanel = new JPanel();
        dataBasePanel.setBackground(Color.WHITE);
        dataBasePanel.setToolTipText("Select a Database");
        BoxLayout dataBaseBoxLayout = new BoxLayout(dataBasePanel, BoxLayout.X_AXIS);
        dataBasePanel.setLayout(dataBaseBoxLayout);

        JLabel dataBaseLabel = new JLabel("Database");
        dataComboBox = new JComboBox(items);
        dataComboBox.setToolTipText("Select a Database");
        dataComboBox.addItemListener(this);
        dataComboBox.setBackground(Color.WHITE);
        dataComboBox.setPreferredSize(fieldDimension);
        dataComboBox.setMaximumSize(fieldDimension);
        dataComboBox.setMinimumSize(fieldDimension);

        FontMetrics dataBaseMetrics = dataBaseLabel.getFontMetrics(dataBaseLabel.getFont());
        int dataBaseWidth = dataBaseMetrics.stringWidth(dataBaseLabel.getText());

        dataComboBox.setSelectedIndex(-1);

        dataBasePanel.add(dataBaseLabel);
        dataBasePanel.add(Box.createHorizontalStrut((uNameWidth - dataBaseWidth) + 10));
        dataBasePanel.add(dataComboBox);
        dataBasePanel.setPreferredSize(panelDimension);
        dataBasePanel.setMinimumSize(panelDimension);
        dataBasePanel.setMaximumSize(panelDimension);
        dataComboBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    connectButton.doClick();
                }
            }
        });
    }

    public void createPortPanel() {
        portPanel = new JPanel();
        portPanel.setBackground(Color.WHITE);
        portPanel.setToolTipText("If required enter your port instead of using default port");
        BoxLayout portBoxLayout = new BoxLayout(portPanel, BoxLayout.X_AXIS);
        portPanel.setLayout(portBoxLayout);

        JLabel portLabel = new JLabel("Port ");


        portField.setEditable(false);
        portField.setToolTipText("Press F2 or Double Click to edit Port Field, Press Enter to Save");
        portField.setPreferredSize(fieldDimension);
        portField.setMaximumSize(fieldDimension);
        portField.setMinimumSize(fieldDimension);

        portField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                clickCount++;
                if (clickCount == 2) {
                    portField.setEditable(true);
                    clickCount = 0;
                }

            }
        });

        portField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (portField.isEditable()) {
                    int a = (int) e.getKeyChar();
                    if (a == 10) {
                        portField.setEditable(false);
                    }
                }
            }

            public void keyPressed(KeyEvent e) {
                int a = e.getKeyCode();
                if (a == 113) {
                    if (!portField.isEditable()) {
                        portField.setEditable(true);
                    }
                }


            }
        });


        FontMetrics portMetrics = portLabel.getFontMetrics(portLabel.getFont());
        int portWidth = portMetrics.stringWidth(portLabel.getText());

        MiscUtility.addScrollAction(dataComboBox);

        portPanel.add(portLabel);
        portPanel.add(Box.createHorizontalStrut((uNameWidth - portWidth) + 10));
        portPanel.add(portField);
        portPanel.setPreferredSize(panelDimension);
        portPanel.setMaximumSize(panelDimension);
        portPanel.setMinimumSize(panelDimension);

        portField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    connectButton.doClick();
                }
            }
        });


    }


    public void createRememberOptionPanel() {
        rememberOptionPanel = new JPanel();
        rememberOptionPanel.setToolTipText("Check this option to save this login credentials");
        rememberOptionPanel.setBackground(Color.WHITE);
        BoxLayout remBoxLayout = new BoxLayout(rememberOptionPanel, BoxLayout.X_AXIS);
        rememberOptionPanel.setLayout(remBoxLayout);
        JLabel rememberLabel = new JLabel("Remember Login");
        rememberCheckBox = new JCheckBox();
        rememberCheckBox.setToolTipText("Check this option to save this login credentials");
        rememberCheckBox.setBackground(Color.WHITE);
        Dimension d = new Dimension(20, 20);
        rememberCheckBox.setPreferredSize(d);
        rememberCheckBox.setMaximumSize(d);
        rememberCheckBox.setMinimumSize(d);

        FontMetrics rememberMetrics = rememberLabel.getFontMetrics(rememberLabel.getFont());
        int rememberWidth = rememberMetrics.stringWidth(rememberLabel.getText());
        rememberOptionPanel.add(rememberLabel);
        rememberOptionPanel.add(Box.createHorizontalStrut((uNameWidth - rememberWidth) + 40));
        rememberOptionPanel.add(rememberCheckBox);
    }

    public void createLowerPanel() {
        lowerPanel = new JPanel();
        lowerPanel.setBackground(Color.WHITE);
        BoxLayout lowerPanelBoxLayout = new BoxLayout(lowerPanel, BoxLayout.X_AXIS);
        lowerPanel.setLayout(lowerPanelBoxLayout);

        // CREATING LOWER PANEL COMPONENTS

        connectButton = new JButton("Connect");

        connectButton.setToolTipText("Click Ok to connect to the database now");
        connectButton.addActionListener(this);
        cancelButton = new JButton("Cancel");
        cancelButton.setToolTipText("Click on cancel to edit the above information or to connect later");
        cancelButton.addActionListener(this);

        // ADDING COMPONENTS TO PANEL

        lowerPanel.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

        lowerPanel.add(connectButton);
        lowerPanel.add(Box.createHorizontalStrut(7));
        lowerPanel.add(cancelButton);
    }

    public void setScannedNet(Map<String, String> scanned) {
        this.ipMap = scanned;

    }

    public static void main(String[] args) {


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }


       loginDialog = new LoginDialog("User Login Window, Smart Database Explorer" + versionId);


    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == dataComboBox) {
            if (dataComboBox.getSelectedIndex() == 0) {
                portField.setText("1530");
            } else if (dataComboBox.getSelectedIndex() == 1) {
                portField.setText("1521");
            } else if (dataComboBox.getSelectedIndex() == 2) {
                portField.setText("3306");
            } else if (dataComboBox.getSelectedIndex() == 3) {
                portField.setText("1433");
            } else {
                portField.setText("");
            }

        }
        if (e.getSource() == hostBox) {

        }
    }

    public int selectedItemIndex() {
        String newIPAddress = hostBox.getEditor().getItem().toString();


        for (int i = 0; i < hostBox.getItemCount(); i++) {
            if (hostBox.getItemAt(i).toString().equals(newIPAddress)) {
                return i;
            }
        }
        if (MiscUtility.isValidIPAddress(newIPAddress)) {
            // Taking back up of hostBox
            List<String> backupHostNames = new ArrayList<String>();
            for (int i = 0; i < hostBox.getItemCount(); i++) {
                System.out.println("BEFORE HOST BOX CONTAINS = " + hostBox.getItemAt(i));
                backupHostNames.add(hostBox.getItemAt(i).toString());
            }
            hostBox.removeAllItems();
            hostBox.addItem(newIPAddress);
            for (int i = 0; i < backupHostNames.size(); i++) {
                hostBox.addItem(backupHostNames.get(i));
            }
            hostBox.setSelectedItem(newIPAddress);
            hostBox.setSelectedIndex(0);
            System.out.println("INSIDE where Selected Item is = " + hostBox.getSelectedItem() + " and Index is = " + hostBox.getSelectedIndex());
            for (int i = 0; i < hostBox.getItemCount(); i++) {
                System.out.println("AFTER HOST BOX CONTAINS = " + hostBox.getItemAt(i));
            }
            return 0;
        }
        return -1;

    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JMenuItem) {
            handleMenuBarEvent(e);
        }
        if (e.getSource() instanceof JButton) {
            if (e.getSource() == cancelButton) {
                this.dispose();
            }

            if (e.getSource() == connectButton) {
                Calendar calendar = new GregorianCalendar();
                int h = calendar.get(Calendar.HOUR);
                int m = calendar.get(Calendar.MINUTE);
                int s = calendar.get(Calendar.SECOND);
                String hour = String.valueOf(h);
                String minute = String.valueOf(m);
                String seconds = String.valueOf(s);
                System.out.println("Loading >> " + hour + ":" + minute + ":" + seconds);

                errorGenerated = false;
                if (isFormFilled()) {
                    user = userNameField.getText().toString().trim();

                    if (showPasswordCheckBox.isSelected()) {
                        pwd = passwordTextField.getText();
                    } else {
                        pwd = String.valueOf(passWordField.getPassword()).trim();
                    }
                    String hostIPName = hostBox.getSelectedItem().toString();

                    if (hostIPName.contains(separator)) {
                        host = MiscUtility.extractIPAddress(hostIPName, separator);
                    } else {
                        host = hostIPName;
                    }
                    port = portField.getText().toString().trim();
                    instanceName = instanceField.getText().toString().trim();
                    dataBaseName = dataComboBox.getSelectedItem().toString().trim();

                    System.out.println(user + "\n" + pwd + "\n" + host + "\n" + port + "\n" + instanceName + "\n" + dataBaseName);
                    loginDetails.put("User Name", user);
                    loginDetails.put("Database", dataBaseName);
                    loginDetails.put("Port", port);
                    loginDetails.put("System", host);
                    loginDetails.put("Instance", instanceName);
                    NetworkUtility netUtility = new NetworkUtility();
                    boolean hostReachable = netUtility.isHostReachable(host, 5);
                    boolean portConnected = netUtility.portScan(host, Integer.parseInt(port));
                    String errorDetails = "";
                    String errorHeading = "";
                    if (!hostReachable || !portConnected) {
                        if (!hostReachable) {
                            errorDetails = "Unable to Connect with the given database. Please check network cable connectivity";
                            errorHeading = "Host not Reachable";
                        } else {
                            errorDetails = "Database : " + dataBaseName + " is not running on the following port.";
                            errorHeading = "Invalid Port";
                        }
                        JOptionPane.showMessageDialog(null, errorDetails, errorHeading, JOptionPane.ERROR_MESSAGE);

                    } else {
                        if (dataComboBox.getSelectedIndex() == 3) {
                            Object result = DataBaseUtility.createMSSqlConnection(user, pwd, host, port, instanceName);
                            if (result instanceof Boolean) {
                                if (result.equals(true)) {
                                    if (rememberCheckBox.isSelected()) {
                                        writeToLoginPropertyFile();
                                    } else {
                                        hostCollections.clear();
                                        loginProperties.clear();
                                        try {
                                            loginProperties.store(new FileOutputStream(loginFile), "");
                                            hostCollections.store(new FileOutputStream(hostCollectionFile), "");
                                        } catch (IOException e1) {
                                        }
                                    }
                                    mainView = new MainView(this, "Microsoft Sql Server Connection", loginDetails);
                                }
                            } else {
                                errorDetails = result.toString();
                                errorHeading = "Microsoft SQL Server Connection";
                                errorGenerated = true;
                            }
                        }
                        if (dataComboBox.getSelectedIndex() == 2) {
                            Object result = DataBaseUtility.createMySQLConnection(user, pwd, host, port, instanceName);
                            if (result instanceof Boolean) {
                                if (result.equals(true)) {
                                    if (rememberCheckBox.isSelected()) {
                                        writeToLoginPropertyFile();
                                    } else {
                                        hostCollections.clear();
                                        loginProperties.clear();
                                        try {
                                            loginProperties.store(new FileOutputStream(loginFile), "");
                                            hostCollections.store(new FileOutputStream(hostCollectionFile), "");
                                        } catch (IOException e1) {
                                        }
                                    }
                                    mainView = new MainView(this, "MySQL Server Connection", loginDetails);
                                }
                            } else {
                                errorDetails = result.toString();
                                errorHeading = "MySQL Server Connection";
                                errorGenerated = true;
                            }
                        }
                        if (dataComboBox.getSelectedIndex() == 1) {
                            Object result = DataBaseUtility.createOracleConnection(user, pwd, host, port, instanceName);
                            if (result instanceof Boolean) {
                                if (result.equals(true)) {
                                    if (rememberCheckBox.isSelected()) {
                                        writeToLoginPropertyFile();
                                    } else {
                                        hostCollections.clear();
                                        loginProperties.clear();
                                        try {
                                            loginProperties.store(new FileOutputStream(loginFile), "");
                                            hostCollections.store(new FileOutputStream(hostCollectionFile), "");
                                        } catch (IOException e1) {
                                        }
                                    }
                                    //  DataBaseUtility.killConnection();
                                    mainView = new MainView(this, "Oracle Connection", loginDetails);
                                }
                            } else {
                                errorDetails = result.toString();
                                errorHeading = "Oracle Connection";
                                errorGenerated = true;
                            }
                        }
                        if (dataComboBox.getSelectedIndex() == 0) {
                            Object result = DataBaseUtility.createDerbyConnection(user, pwd, host, port, instanceName);
                            if (result instanceof Boolean) {
                                if (result.equals(true)) {
                                    if (rememberCheckBox.isSelected()) {
                                        writeToLoginPropertyFile();
                                    } else {
                                        hostCollections.clear();
                                        loginProperties.clear();

                                        try {
                                            loginProperties.store(new FileOutputStream(loginFile), "");
                                            hostCollections.store(new FileOutputStream(hostCollectionFile), "");
                                        } catch (IOException e1) {
                                        }
                                    }
                                    mainView = new MainView(this, "Derby Connection", loginDetails);
                                }
                            } else {
                                errorDetails = result.toString();
                                errorHeading = "Derby Connection";
                                errorGenerated = true;
                            }
                        }
                        if (errorGenerated) {

                            JOptionPane.showMessageDialog(null, errorDetails, errorHeading, JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Form not Properly Filled", "Incomplete Form Filling ", JOptionPane.ERROR_MESSAGE);

                }

            }
        }
    }

    //    private void showLoadingAnimation(){
//        animatedDialog=new JDialog();
//
//
//    }
//    private void stopLoadingAnimation(){
//
//    }
    private boolean isFormFilled() {

        String u = userNameField.getText();
        String p = String.valueOf(passWordField.getPassword());
        String h = hostBox.getSelectedItem().toString();
        String po = portField.getText();

        int da = dataComboBox.getSelectedIndex();

        if (u.equals("") || p.equals("") || h.equals("") || po.equals("") || da == -1) {
            return false;
        } else {
            return true;
        }

    }

    private void writeToLoginPropertyFile() {
        loginProperties.clear();
        loginProperties.setProperty("User", user);
        loginProperties.setProperty("Password", pwd);
        loginProperties.setProperty("HostIndex", String.valueOf(selectedItemIndex()));
        loginProperties.setProperty("Port", port);
        loginProperties.setProperty("Instance", instanceName);
        loginProperties.setProperty("Database", dataBaseName);
        loginProperties.setProperty("Check", String.valueOf(rememberCheckBox.isSelected()));

        writeToHostCollectionPropertyFile();

        try {
            loginProperties.store(new FileOutputStream(loginFile), "Login Details, SR Softwares , Smart Database Explorer. Please DO NOT MODIFY. Copyrights(c) SR Softwares.");
        } catch (IOException e) {

        }
    }


    public List<String> getHostItems() {
        List<String> allHostItems = new ArrayList<String>();
        for (int i = 0; i < hostBox.getItemCount(); i++) {
            allHostItems.add(hostBox.getItemAt(i).toString());
        }
        return allHostItems;
    }

    public void setHost(String ip) {

        //System.out.println("IP TO SET = "+ip);


        hostBox.addItem(ip);
        hostBox.setSelectedItem(ip);
        hostBox.updateUI();
        hostPanel.updateUI();

    }

    private void writeToHostCollectionPropertyFile() {
        hostCollections.clear();
        String hostAddress = "HostAddress";
        for (int i = 0; i < hostBox.getItemCount(); i++) {
            String hostPropertyKey = hostAddress + (i + 1);
            String hostPropertyValue = hostBox.getItemAt(i).toString();
            hostCollections.setProperty(hostPropertyKey, hostPropertyValue);

        }
        try {
            hostCollections.store(new FileOutputStream(hostCollectionFile), "Host Address & System Names, SR Softwares , Smart Database Explorer. Please DO NOT MODIFY. Copyrights(c) SR Softwares.");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private boolean isHostBoxContainsItem(String item) {
        for (int i = 0; i < hostBox.getItemCount(); i++) {
            if (hostBox.getItemAt(i).toString().equals(item)) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }
       // TODO     NOT ABLE TO PROPERLY WHAT IS THE VALUE OF HOST INDEX IN EVERY STARTUP
    private void readFromHostCollectionPropertyFile() {
        if (hostCollectionFile.exists()) {
            try {
                FileInputStream fin = new FileInputStream(hostCollectionFile);
                hostCollections.load(fin);
                String prefixKey = "HostAddress";
                if (hostCollections.size() > 0) {
                    for (int i = 1; i <= hostCollections.size(); i++) {
                        String retrieveKey = prefixKey + i;
                        String value = hostCollections.getProperty(retrieveKey);
                        if (!isHostBoxContainsItem(value))
                            hostBox.addItem(value);

                    }
                    System.out.println("INITIALLY HOST INDEX IS = "+hostIndex);
                    hostBox.setSelectedIndex(Integer.parseInt(hostIndex));
                }

            } catch (Exception e) {
            }
        }
    }

    private void readFromLoginPropertyFile() {
        if (loginFile.exists()) {
            try {

                FileInputStream fin = new FileInputStream(loginFile);
                loginProperties.load(fin);
                String usr = loginProperties.getProperty("User");
                String pass = loginProperties.getProperty("Password");
                hostIndex = loginProperties.getProperty("HostIndex");
                String port = loginProperties.getProperty("Port");
                String instance = loginProperties.getProperty("Instance");
                String database = loginProperties.getProperty("Database");
                String remember = loginProperties.getProperty("Check");

                boolean rememberStatus = remember.equalsIgnoreCase("True") ? true : false;

                userNameField.setText(usr);
                passWordField.setText(pass);
                instanceField.setText(instance);
                dataComboBox.setSelectedItem(database);
                portField.setText(port);


                rememberCheckBox.setSelected(rememberStatus);


            } catch (Exception e) {

            }
        }
    }


    public void setLookNFeelMenuItems() {
        UIManager.LookAndFeelInfo themes[] = UIManager.getInstalledLookAndFeels();
        menuItems = new JCheckBoxMenuItem[themes.length];
        ButtonGroup btnGrp = new ButtonGroup();
        for (int i = 0; i < themes.length; i++) {
            UIManager.LookAndFeelInfo theme = themes[i];
            String lafClassName = theme.getClassName();
            menuItems[i] = new JCheckBoxMenuItem(theme.getName());
            btnGrp.add(menuItems[i]);

            menuItems[i].setActionCommand(lafClassName);
            menuItems[i].addActionListener(this);
            themesMenu.add(menuItems[i]);
            if (lafClassName.equalsIgnoreCase(UIManager.getSystemLookAndFeelClassName())) {
                menuItems[i].setState(true);
            }


        }
    }

    public void handleMenuBarEvent(ActionEvent e) {
        JMenuItem item = (JMenuItem) e.getSource();

        if (item.getText().equals("Help")) {
            new LoginHelp(this);
        } else if (item.getText().equals("About")) {
            new AboutDialog(this);
        } else {
            String lafClassName = item.getActionCommand();
            try {
                UIManager.setLookAndFeel(lafClassName);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (UnsupportedLookAndFeelException e1) {
                e1.printStackTrace();
            }

            SwingUtilities.updateComponentTreeUI(LoginDialog.this);
            LoginDialog.this.validate();

        }


    }


    public boolean itemAlreadyAdded(String ip) {


        String ipToMatch = MiscUtility.extractIPAddress(ip, separator);

        boolean itemExist = false;
        for (int i = 0; i < hostBox.getItemCount(); i++) {
            String item = hostBox.getItemAt(i).toString();
            String hostAddress = MiscUtility.extractIPAddress(item, separator);


            if (ipToMatch.equals(hostAddress)) {
                itemExist = true;

                break;
            }
        }
        return itemExist;
    }


}
