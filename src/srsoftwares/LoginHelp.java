package srsoftwares;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * Author: Sumit Roy
 * Date: 9/12/11
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class LoginHelp extends JDialog {
    private JPanel mainPanel;
    private JPanel editingPanel;
    private JEditorPane aboutPane;
    private File file;
    private JButton saveButton;
    private JButton editButton;
    private JButton hideButton;
    private int countMouseClick;
    private JScrollPane scrollPane;

    LoginHelp(LoginDialog parent) {
        super(parent);
        LoginHelp.this.getParent().setEnabled(false);
        countMouseClick = 0;
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        this.setTitle("Login Help, Smart Database Explorer");

        Image image = null;
        try {
            image = ImageIO.read(this.getClass().getResource("images/about.png"));
        } catch (Exception e) {

        }
        this.createNShowGUI();
        this.setIconImage(image);
        this.setSize(665, 613);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.add(mainPanel);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                 int w = e.getWindow().getSize().width;
                 int h = e.getWindow().getSize().height;
                 //System.out.println("Optimized View : Width = " + w + "\n\nHeight = " + h);
                LoginHelp.this.getParent().setEnabled(true);


            }
        });

        aboutPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int a = aboutPane.viewToModel(e.getPoint());
                if (a >= 330 && a <= 355) {
                    openSite();
                }
                countMouseClick++;
                if (countMouseClick == 3) {
                    JOptionPane.showMessageDialog(null, "Welcome to Development Mode. Don't change any information unless required ! Changed information can't be restore to default.Change at your own risk!", "Development Mode", JOptionPane.INFORMATION_MESSAGE);
                    createEditingPanel();
                    mainPanel.add(editingPanel, BorderLayout.SOUTH);
                    mainPanel.updateUI();
                    countMouseClick = 0;
                }
            }


        });
        this.setVisible(true);
    }

    private void openSite() {
        URI uri= null;
        try {
            uri = new URI("http://www.srsoftwares.co.nr");
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
          JOptionPane.showMessageDialog(null, "Sorry ! Your system doesn't allowing this application to open our Website", "Desktop not supported", JOptionPane.ERROR_MESSAGE);
        }
    }



    public void createNShowGUI() {
        InputStream is = this.getClass().getResourceAsStream("loginhelp.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sb = new StringBuffer();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            isr.close();
            is.close();
            aboutPane = new JEditorPane();
            aboutPane.setText(sb.toString());
            aboutPane.setEditable(false);
            aboutPane.setBackground(Color.WHITE);
            aboutPane.setFont(new Font("Serifs", Font.BOLD, 15));
            aboutPane.setForeground(new Color(4, 79, 170));
            scrollPane = new JScrollPane(aboutPane);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    scrollPane.getVerticalScrollBar().setValue(0);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void createEditingPanel() {
        editingPanel = new JPanel();
        editingPanel.setLayout(new BoxLayout(editingPanel, BoxLayout.X_AXIS));
        editButton = new JButton("Edit Information");
        saveButton = new JButton("Save Information");
        hideButton = new JButton("Hide Me");
        editingPanel.add(Box.createHorizontalStrut(150));
        editingPanel.add(editButton);
        editingPanel.add(Box.createHorizontalStrut(20));
        editingPanel.add(saveButton);
        editingPanel.add(Box.createHorizontalStrut(20));
        editingPanel.add(hideButton);

        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aboutPane.setEditable(true);
            }
        });

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String textFormat = aboutPane.getText();
                try {
                    file = new File("out\\production\\Smart Database Explorer\\srsoftwares\\loginhelp.txt");
                    OutputStream os = new FileOutputStream((file));
                    OutputStreamWriter osr = new OutputStreamWriter(os);
                    BufferedWriter br = new BufferedWriter(osr);
                    br.write(textFormat);
                    br.close();
                    osr.close();
                    os.close();

                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(null, "Sorry ! The specified folder or directory is not Found. Please follow the codes", "Dev Mode : File/Folder Not Found", JOptionPane.ERROR_MESSAGE);
                }
                aboutPane.setEditable(false);
            }
        });

        hideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mainPanel.remove(editingPanel);
                mainPanel.updateUI();
            }
        });

    }

    /* public static void main(String[] args) {
        LoginHelp loginHelp = new LoginHelp();
    }*/
}
