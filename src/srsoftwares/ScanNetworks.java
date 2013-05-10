package srsoftwares;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Owner : SR Softwares
 * Author: Sumit Roy
 * Date: 9/14/11
 * Time: 1:17 PM
 * Visit us at http://www.srsoftwares.co.nr
 * To change this template use File | Settings | File Templates.
 */
public class ScanNetworks extends JDialog implements ActionListener {
    private JPanel progressPanel;
    private JPanel lowerPanel;
    private JPanel mainPanel;
    private JPanel userIPPanel;
    private JPanel subNetPanel;
    private JLabel userInfoLabel;
    private JLabel subNetFromLabel;
    private JLabel subNetToLabel;
    private JLabel scanningLabel1;
    private JLabel scanningLabel2;


    private JSpinner toSubnetSpinner;
    private JSpinner fromSubnetSpinner;
    private Integer[] subNetVal;
    private SpinnerListModel fromSubnetModel;


    private JButton scanBtn;
    private JButton stopScanBtn;
    private JProgressBar scanPrgBar;

    private String ip;
    private String subnet;
    private Map<String, String> ipMap;
    public static Map<String, String> listModelMap;
    private SpinnerListModel toSubnetModel;

    private volatile boolean stopScan;
    private String sysName;
    private JLabel timeOutLabel;
    private Integer[] timeOutVal;
    private SpinnerListModel timeOutModel;
    private JSpinner timeOutSpinner;
    private JPanel outputPanel;
    private JList outputList;
    private DefaultListModel outputModel;
    private JScrollPane listPane;
    private JPanel okBtnPanel;
    private JButton okButton;

    public static LoginDialog parent;
    private Thread scanningThread;

    public static Map<Integer, Boolean> selectedItemIndex = new HashMap<Integer, Boolean>();
    private NetworkListRenderer networkListRenderer;
    public static List<String> hostBoxItems;
    private String separator;

    ScanNetworks() {
        this.getSystemIP();

    }

    ScanNetworks(final LoginDialog parent) {
        super(parent);
        this.parent = parent;
        parent.setEnabled(false);
        stopScan = false;
        separator = "|";
        this.getSystemIP();
        hostBoxItems = parent.getHostItems();
        System.out.println("Host Box Items are = ");

        for (int i = 0; i < hostBoxItems.size(); i++) {
            System.out.println("ITEM IS  >> = " + hostBoxItems.get(i));
        }

        this.createUserIPPanel();
        this.createSubnetPanel();
        this.createProgressPanel();
        this.createLowerPanel();
        this.createOutputPanel();
        this.createOkBtnPanel();
        this.createMainPanel();

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.setTitle("Scan Networks, Smart Database Explorer " + LoginDialog.versionId);
        Image image = null;
        try {
            image = ImageIO.read(this.getClass().getResource("images/Network Scan.png"));
        } catch (Exception e) {

        }
        scanBtn.addActionListener(this);

        this.add(mainPanel, BorderLayout.CENTER);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //  int w = e.getWindow().getSize().width;
                // int h = e.getWindow().getSize().height;
                parent.setEnabled(true);
                //  System.out.println("Optimized View : Width = " + w + "\n\nHeight = " + h);

                stopScan = true;
                parent.setScannedNet(ipMap);

            }
        });


        this.setIconImage(image);
        this.setSize(600, 460);
        this.setResizable(false);

        int childHeight = this.getHeight();
        int childWidth = this.getWidth();
        int parHeight = parent.getSize().height;
        int parWidth = parent.getSize().width;
        int diffHt = (childHeight - parHeight) / 2;
        int diffWd = (childWidth - parWidth) / 2;

        int parLocX = parent.getLocationOnScreen().x;
        int parLocY = parent.getLocationOnScreen().y;

        int childX = parLocX - diffWd;
        int childY = parLocY - diffHt;
        this.setLocation(childX, childY);
        this.setVisible(true);

    }


    private void createUserIPPanel() {
        userIPPanel = new JPanel();
        userIPPanel.setLayout(new BoxLayout(userIPPanel, BoxLayout.X_AXIS));
        userIPPanel.setBackground(Color.WHITE);

        String ipMsg = "<html>" + "<h2 style = \"text-align :center ; color : \t#6600FF \">Your IP address is : " + ip;
        userInfoLabel = new JLabel(ipMsg, JLabel.CENTER);
        userIPPanel.add(userInfoLabel);

        userIPPanel.setMaximumSize(new Dimension(492, 45));
        userIPPanel.setMinimumSize(new Dimension(492, 45));
        userIPPanel.setPreferredSize(new Dimension(492, 45));

    }

    private void createSubnetPanel() {
        subNetPanel = new JPanel();
        subNetPanel.setLayout(new BoxLayout(subNetPanel, BoxLayout.X_AXIS));
        subNetPanel.setBackground(Color.WHITE);
        subNetPanel.setMaximumSize(new Dimension(550, 50));
        subNetPanel.setMinimumSize(new Dimension(550, 50));
        subNetPanel.setPreferredSize(new Dimension(550, 50));
        String subNetFrom = "Run Network Scan Between : " + subnet + ".";
        String subNetTo = " and : " + subnet + ".";
        String timeOut = " Max No. of Try";

        subNetFromLabel = new JLabel(subNetFrom, JLabel.CENTER);
        subNetToLabel = new JLabel(subNetTo, JLabel.CENTER);
        timeOutLabel = new JLabel(timeOut, JLabel.CENTER);

        subNetVal = new Integer[255];
        timeOutVal = new Integer[10];


        for (int i = 0; i < 255; i++) {
            subNetVal[i] = i + 1;
            if (i < 10) {
                timeOutVal[i] = i + 1;
            }
        }


        fromSubnetModel = new SpinnerListModel(subNetVal);
        toSubnetModel = new SpinnerListModel(subNetVal);
        timeOutModel = new SpinnerListModel(timeOutVal);

        fromSubnetSpinner = new JSpinner(fromSubnetModel);
        fromSubnetSpinner.setMaximumSize(new Dimension(40, 25));
        fromSubnetSpinner.setMinimumSize(new Dimension(40, 25));
        fromSubnetSpinner.setPreferredSize(new Dimension(40, 25));

        ((JSpinner.DefaultEditor) fromSubnetSpinner.getEditor()).getTextField().setBackground(Color.WHITE);
        ((JSpinner.DefaultEditor) fromSubnetSpinner.getEditor()).getTextField().setEditable(false);
        fromSubnetSpinner.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                handleMouseScrollEvent(e);
            }
        });

        toSubnetSpinner = new JSpinner(toSubnetModel);

        toSubnetSpinner.setValue(255);
        toSubnetSpinner.setMaximumSize(new Dimension(40, 25));
        toSubnetSpinner.setMinimumSize(new Dimension(40, 25));
        toSubnetSpinner.setPreferredSize(new Dimension(40, 25));

        timeOutSpinner = new JSpinner(timeOutModel);
        timeOutSpinner.setValue(2);
        timeOutSpinner.setMaximumSize(new Dimension(40, 25));
        timeOutSpinner.setMinimumSize(new Dimension(40, 25));
        timeOutSpinner.setPreferredSize(new Dimension(40, 25));

        ((JSpinner.DefaultEditor) toSubnetSpinner.getEditor()).getTextField().setBackground(Color.WHITE);
        ((JSpinner.DefaultEditor) toSubnetSpinner.getEditor()).getTextField().setEditable(false);

        ((JSpinner.DefaultEditor) timeOutSpinner.getEditor()).getTextField().setBackground(Color.WHITE);
        ((JSpinner.DefaultEditor) timeOutSpinner.getEditor()).getTextField().setEditable(false);

        toSubnetSpinner.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                handleMouseScrollEvent(e);
            }
        });

        timeOutSpinner.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                handleMouseScrollEvent(e);
            }
        });

        subNetPanel.add(Box.createHorizontalStrut(10));

        subNetPanel.add(subNetFromLabel);
        subNetPanel.add(Box.createHorizontalStrut(2));
        subNetPanel.add(fromSubnetSpinner);
        subNetPanel.add(Box.createHorizontalStrut(15));
        subNetPanel.add(subNetToLabel);
        subNetPanel.add(Box.createHorizontalStrut(2));
        subNetPanel.add(toSubnetSpinner);
        subNetPanel.add(Box.createHorizontalStrut(15));
        subNetPanel.add(timeOutLabel);
        subNetPanel.add(Box.createHorizontalStrut(2));
        subNetPanel.add(timeOutSpinner);
        subNetPanel.add(Box.createHorizontalStrut(10));


    }

    private void createProgressPanel() {
        progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.X_AXIS));
        progressPanel.setBackground(Color.WHITE);
        progressPanel.setMaximumSize(new Dimension(490, 55));
        progressPanel.setMinimumSize(new Dimension(490, 55));
        progressPanel.setPreferredSize(new Dimension(490, 55));

        scanBtn = new JButton("Scan Networks");
        stopScanBtn = new JButton("Stop Scan");
        stopScanBtn.setVisible(false);
        scanPrgBar = new JProgressBar(0, 100);

        scanPrgBar.setMaximumSize(new Dimension(255, 25));
        scanPrgBar.setMinimumSize(new Dimension(255, 25));
        scanPrgBar.setPreferredSize(new Dimension(255, 25));
        scanPrgBar.setBackground(Color.WHITE);
        scanPrgBar.setStringPainted(true);

        progressPanel.add(Box.createHorizontalStrut(10));
        progressPanel.add(scanBtn);
        progressPanel.add(Box.createHorizontalStrut(20));
        progressPanel.add(scanPrgBar);
        progressPanel.add(Box.createHorizontalStrut(10));
        progressPanel.add(stopScanBtn);
        progressPanel.add(Box.createHorizontalStrut(10));

        stopScanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopScan = true;
                showStatusMessages(3);
            }
        });

    }

    private void createLowerPanel() {
        lowerPanel = new JPanel();
        lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.X_AXIS));
        lowerPanel.setBackground(Color.WHITE);
        showStatusMessages(0);

        lowerPanel.setMaximumSize(new Dimension(270, 45));
        lowerPanel.setMinimumSize(new Dimension(270, 45));
        lowerPanel.setPreferredSize(new Dimension(270, 45));
        lowerPanel.setVisible(true);
    }

    /**
     * SR Softwares - Smart Database Explorer
     * <p/>
     * shows a status message while scanning is being performed.
     *
     * @param statusType status type variable sets what message will be displayed
     *                   <p/>            Status Type - Shows Message
     *                   <p/>             1          : Shows Scanning Network Message
     *                   <p/>             2          : Shows Scan Finished Messages
     *                   <p/>             3 : Shows Scan Cancelled Messages
     *                   <p/>             0 : Shows Start your Scan Messages
     */
    private void showStatusMessages(int statusType) {
        ImageIcon imgIcon;
        ImageIcon imgIcon2;
        switch (statusType) {
            case 0:
                lowerPanel.removeAll();
                scanningLabel1 = new JLabel("");
                imgIcon = new ImageIcon(ScanNetworks.class.getResource("images/networkDiscoveryMSG.png"));
                scanningLabel1.setIcon(imgIcon);
                scanningLabel1.setBackground(Color.WHITE);
                imgIcon2 = new ImageIcon(ScanNetworks.class.getResource("images/networkDiscovery.png"));
                scanningLabel2 = new JLabel();
                scanningLabel2.setIcon(imgIcon2);
                scanningLabel2.setBackground(Color.WHITE);
                lowerPanel.add(Box.createHorizontalStrut(5));
                lowerPanel.add(scanningLabel1);
                lowerPanel.add(Box.createHorizontalStrut(2));
                lowerPanel.add(scanningLabel2);
                lowerPanel.updateUI();
                break;


            case 1:
                lowerPanel.removeAll();
                scanningLabel1 = new JLabel("");
                imgIcon = new ImageIcon(ScanNetworks.class.getResource("images/Scanning.png"));
                scanningLabel1.setIcon(imgIcon);
                scanningLabel1.setBackground(Color.WHITE);
                imgIcon2 = new ImageIcon(ScanNetworks.class.getResource("images/Animated Signal Bars.gif"));
                scanningLabel2 = new JLabel();
                scanningLabel2.setIcon(imgIcon2);
                scanningLabel2.setBackground(Color.WHITE);
                lowerPanel.add(Box.createHorizontalStrut(60));
                lowerPanel.add(scanningLabel1);
                lowerPanel.add(Box.createHorizontalStrut(5));
                lowerPanel.add(scanningLabel2);
                lowerPanel.updateUI();
                break;


            case 2:
                lowerPanel.removeAll();
                scanningLabel1 = new JLabel("");
                imgIcon = new ImageIcon(ScanNetworks.class.getResource("images/scanningCompletedMSG.png"));
                scanningLabel1.setIcon(imgIcon);
                scanningLabel1.setBackground(Color.WHITE);
                imgIcon2 = new ImageIcon(ScanNetworks.class.getResource("images/scanningCompleted.png"));
                scanningLabel2 = new JLabel();
                scanningLabel2.setIcon(imgIcon2);
                scanningLabel2.setBackground(Color.WHITE);
                lowerPanel.add(Box.createHorizontalStrut(5));
                lowerPanel.add(scanningLabel1);
                lowerPanel.add(Box.createHorizontalStrut(2));
                lowerPanel.add(scanningLabel2);
                lowerPanel.updateUI();
                break;


            case 3:
                lowerPanel.removeAll();
                scanningLabel1 = new JLabel("");
                imgIcon = new ImageIcon(ScanNetworks.class.getResource("images/scanningCancelledMSG.png"));
                scanningLabel1.setIcon(imgIcon);
                scanningLabel1.setBackground(Color.WHITE);
                imgIcon2 = new ImageIcon(ScanNetworks.class.getResource("images/scanningCancelled.png"));
                scanningLabel2 = new JLabel();
                scanningLabel2.setIcon(imgIcon2);
                scanningLabel2.setBackground(Color.WHITE);
                lowerPanel.add(Box.createHorizontalStrut(5));
                lowerPanel.add(scanningLabel1);
                lowerPanel.add(Box.createHorizontalStrut(2));
                lowerPanel.add(scanningLabel2);
                lowerPanel.updateUI();
                break;
        }

    }

    private void createOutputPanel() {
        outputPanel = new JPanel();
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
        outputPanel.setBackground(Color.WHITE);
        Dimension outputPanelDimension=new Dimension(575, 175);
        outputPanel.setMaximumSize(outputPanelDimension);
        outputPanel.setMinimumSize(outputPanelDimension);
        outputPanel.setPreferredSize(outputPanelDimension);
        outputModel = new DefaultListModel();
        outputList = new JList(outputModel);


        outputList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int selectedIndexes[] = outputList.getSelectedIndices();

                if (selectedIndexes.length > 0) {
                    okButton.setEnabled(true);
                    if (selectedIndexes.length == 1) {
                        okButton.setText("Add this System  ");
                    } else {
                        okButton.setText("Add these Systems");
                    }
                    okButton.updateUI();

                } else {
                    okButton.setText("Please Select a System");
                    okButton.setEnabled(false);

                }


            }
        });
        networkListRenderer = new NetworkListRenderer();
        outputList.setCellRenderer(networkListRenderer);
        outputList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        outputList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        outputList.setVisibleRowCount(-1);
        listPane = new JScrollPane(outputList);
        listPane.setBorder(null);
        outputPanel.add(listPane);
        listPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        TitledBorder title = BorderFactory.createTitledBorder("No System Found");
        title.setTitleJustification(TitledBorder.LEFT);
        outputPanel.setBorder(title);
        outputPanel.setVisible(true);

    }

    private void updateTitleBorderText(int v) {
        if (v == 1) {
            TitledBorder title = BorderFactory.createTitledBorder("System Found (" + v + ")");
            title.setTitleJustification(TitledBorder.LEFT);
            outputPanel.setBorder(title);
            outputPanel.setVisible(true);

        }
        if (v > 1) {
            TitledBorder title = BorderFactory.createTitledBorder("System Founds (" + v + ")");
            title.setTitleJustification(TitledBorder.LEFT);
            outputPanel.setBorder(title);
            outputPanel.setVisible(true);

        }
        if (v == 0) {
            TitledBorder title = BorderFactory.createTitledBorder("No System Found");
            title.setTitleJustification(TitledBorder.LEFT);
            outputPanel.setBorder(title);
            outputPanel.setVisible(true);
        }


    }

    private void createOkBtnPanel() {
        okBtnPanel = new JPanel();
        okBtnPanel.setLayout(new BorderLayout());
        okButton = new JButton("No System is selected");
        okButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
        okButton.setMaximumSize(new Dimension(250, 20));
        okButton.setMinimumSize(new Dimension(250, 20));
        okButton.setPreferredSize(new Dimension(250, 20));
        okBtnPanel.add(okButton, BorderLayout.EAST);
        okBtnPanel.setMaximumSize(new Dimension(535, 30));
        okBtnPanel.setMinimumSize(new Dimension(535,30));
        okBtnPanel.setPreferredSize(new Dimension(535, 30));
        okButton.setEnabled(false);
        okBtnPanel.setBackground(Color.WHITE);
        okBtnPanel.setVisible(true);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^*^");
                int selectedIndexes[] = outputList.getSelectedIndices();
                for (int i = 0; i < selectedIndexes.length; i++) {
                    String ipToSend = outputModel.get(selectedIndexes[i]).toString();
                    String ipFormatted = listModelMap.get(ipToSend);

                    if (!parent.itemAlreadyAdded(ipFormatted)) {
                        parent.setHost(ipFormatted);
                        JOptionPane optionPane = new JOptionPane();
                        ImageIcon ico = new ImageIcon(ScanNetworks.class.getResource("images/okICON.png"));
                        optionPane.setIcon(ico);
                        optionPane.setMessage(JOptionPane.INFORMATION_MESSAGE);
                        optionPane.setMessage("Selected System has been added Successfully ! \nPlease close this window now.");
                        JDialog dialog = optionPane.createDialog("System Added Successfully");
                        selectedItemIndex.put(selectedIndexes[i], true);
                        Image image = null;
                        try {
                            image = ImageIO.read(this.getClass().getResource("images/roundedOkBtn.PNG"));
                        } catch (Exception e1) {

                        }
                        dialog.setIconImage(image);
                        dialog.show();
                    } else {
                        JOptionPane optionPane = new JOptionPane();
                        ImageIcon ico = new ImageIcon(ScanNetworks.class.getResource("images/exclaimIcon.png"));
                        optionPane.setIcon(ico);
                        optionPane.setMessage(JOptionPane.ERROR_MESSAGE);
                        String hostName = MiscUtility.extractHostName(ipFormatted, separator);
                        optionPane.setMessage("Selected System : " + hostName + " is already added !  \nPlease chose some other System(s).");
                        JDialog dialog = optionPane.createDialog("System Not Added !");
                        Image image = null;
                        try {
                            image = ImageIO.read(this.getClass().getResource("images/exclaimIconTOP.PNG"));
                        } catch (Exception e1) {

                        }
                        dialog.setIconImage(image);
                        dialog.show();
                    }

                }
                outputList.setCellRenderer(new NetworkListRenderer());

            }
        });
    }


    private void createMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(userIPPanel);
        mainPanel.add(Box.createVerticalStrut(0));
        mainPanel.add(subNetPanel);
        mainPanel.add(Box.createVerticalStrut(0));
        mainPanel.add(progressPanel);
        mainPanel.add(Box.createVerticalStrut(0));
        mainPanel.add(lowerPanel);
        mainPanel.add(Box.createVerticalStrut(0));
        mainPanel.add(outputPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(okBtnPanel);
    }

    private void handleMouseScrollEvent(MouseWheelEvent e) {
        if (e.getSource() instanceof JSpinner) {
            JSpinner spinner = (JSpinner) e.getSource();
            int upVal = 0;
            if (spinner.getModel().equals(timeOutModel)) {
                upVal = 10;
            } else {
                upVal = 255;
            }
            int val = Integer.parseInt(spinner.getValue().toString());
            if (e.getWheelRotation() == 1) {
                if (val > 1) {
                    spinner.setValue(spinner.getPreviousValue());
                } else {
                    spinner.setValue(upVal);
                }
            } else {
                if (val < upVal) {
                    spinner.setValue(spinner.getNextValue());
                } else {
                    spinner.setValue(1);
                }
            }


        }


    }

    //  public static void main(String[] args) {
    //      ScanNetworks scn = new ScanNetworks();
    //   }

    public String convertToHTML(String upperLine, String lowerLine) {

        String htmlString = "<html><b>System Name : " + upperLine + "</b><br> IP Address : " + lowerLine + "</html>";
        return htmlString;
    }

    public void actionPerformed(ActionEvent e) {

        final int startVal = Integer.parseInt(fromSubnetSpinner.getValue().toString());
        final int endVal = Integer.parseInt(toSubnetSpinner.getValue().toString());
        final int timeOutVal = Integer.parseInt(timeOutSpinner.getValue().toString());
        if (startVal < endVal) {
            System.out.println("******** RE LOADING HOST NAMES ");
            hostBoxItems = parent.getHostItems();
            System.out.println("Host Box Items are = ");

            for (int i = 0; i < hostBoxItems.size(); i++) {
                System.out.println("ITEM IS  >> = " + hostBoxItems.get(i));
            }

            System.out.println("\n\n ******** PRINTING SELECTED ITEM INDEX MAP "+selectedItemIndex);
            System.out.println("***************** END**********************************");
            scanBtn.setEnabled(false);
            fromSubnetSpinner.setEnabled(false);
            toSubnetSpinner.setEnabled(false);
            timeOutSpinner.setEnabled(false);
            showStatusMessages(1);
            outputPanel.setVisible(true);
            stopScanBtn.setVisible(true);
            outputList.clearSelection();
            updateTitleBorderText(0);
            outputList.removeAll();
            outputModel.removeAllElements();
            okButton.setEnabled(false);
            okBtnPanel.updateUI();
            outputList.updateUI();
            final int maxTry = 1000;
            ipMap = new HashMap<String, String>();
            listModelMap = new HashMap<String, String>();
            scanPrgBar.setValue(0);
            ipMap.put(ip, sysName);
            Runnable runnable = new Runnable() {
                public void run() {
                    int t = 1;
                    outputPanel.updateUI();
                    int i = 0;
                    int sysFoundCount = 1;
                    for (i = startVal; i <= endVal && (!stopScan); i++) {

                        String host = subnet + "." + i;
                        try {
                            int k = 0;
                            boolean reachableStatus = false;
                            InetAddress ipAddress = null;
                            while (k < timeOutVal && (!stopScan)) {
                                if (host.equals(getSysIP())) {
                                    String output = "127.0.0.1" + separator + getSysIP();

                                    String elementToDisplay = convertToHTML(getSysIP(), "127.0.0.1");
                                    outputModel.addElement(elementToDisplay);
                                    System.out.println(output);
                                    listModelMap.put(elementToDisplay, output);
                                    sysFoundCount++;
                                    updateTitleBorderText(sysFoundCount);
                                    outputPanel.updateUI();
                                    break;
                                } else {
                                    reachableStatus = InetAddress.getByName(host).isReachable(maxTry);
                                    if (reachableStatus) {
                                        ipAddress = InetAddress.getByName(host);
                                        ipMap.put(host, ipAddress.getHostName());
                                        String output = host + "|" + ipAddress.getHostName();
                                        String elementToDisplay = convertToHTML(ipAddress.getHostName(), host);
                                        outputModel.addElement(elementToDisplay);

                                        listModelMap.put(elementToDisplay, output);
                                        System.out.println(">> "+"\n"+output);
                                        updateTitleBorderText(sysFoundCount);
                                        sysFoundCount++;

                                        outputPanel.updateUI();
                                        break;
                                    }
                                }


                                k++;
                            }


                            // System.out.println("IP STATUS : IP = "+host+ " Host Connected =  "+reachableStatus+" TryCount = "+k);
                            t = ((i - startVal) * 100) / (endVal - startVal);
                            scanPrgBar.setValue(t);


                            scanningLabel2.updateUI();

                        } catch (Exception e1) {
                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }

                    }
                    if (i > endVal) {
                        showStatusMessages(2);
                    }
                    stopScanBtn.setVisible(false);
                    stopScan = false;

                    scanBtn.setEnabled(true);
                    fromSubnetSpinner.setEnabled(true);
                    toSubnetSpinner.setEnabled(true);
                    timeOutSpinner.setEnabled(true);

                }
            };
            scanningThread = new Thread(runnable);
            scanningThread.start();
        } else {
            JOptionPane.showMessageDialog(null, "Starting Subnet Range Must be less than Ending Subnet Range", "Wrong Subnet Range", JOptionPane.ERROR_MESSAGE);
        }


    }

    public void getSystemIP() {
        InetAddress ownIP = null;
        try {
            ownIP = InetAddress.getLocalHost();
            ip = ownIP.getHostAddress();
            sysName = InetAddress.getByName(ip).getHostName();
        } catch (UnknownHostException e1) {

        }


        subnet = ip.substring(0, ip.lastIndexOf("."));
    }

    public String getSysIP() {
        return ip;
    }

    public String getSysName() {
        return sysName;

    }
}



