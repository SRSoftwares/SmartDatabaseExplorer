package srsoftwares;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * Author: Sumit Roy
 * Date: 7/16/11
 * Time: 4:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class AboutDialog extends JDialog {
    private JPanel mainPanel;
    private JPanel imgPanel;
    private String msgText;
    private String msgText2;
    private String msgText3;
    private String copyRt;
    private String trademark;
    private String msgText4;
    private JLabel aboutUsLabel;


    AboutDialog(LoginDialog parent) {
        super(parent);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setTitle("About , Smart Database Explorer "+LoginDialog.versionId+" - SR Softwares");
        Image image = null;
        try {
            image = ImageIO.read(this.getClass().getResource("images/about.png"));
        } catch (Exception e) {

        }
        this.setIconImage(image);

        this.setSize(376, 380);
        this.setLocationRelativeTo(parent);
        this.setLayout(new BorderLayout());
        this.createAboutDialog();
        this.setResizable(false);


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int w = e.getWindow().getSize().width;
                int h = e.getWindow().getSize().height;
                // System.out.println("Optimized View : Width = " + w + "\n\nHeight = " + h);
                AboutDialog.this.getParent().setEnabled(true);

            }
        });
        this.setVisible(true);
    }

    public void createAboutDialog() {
        Image image = null;
        try {
            image = ImageIO.read(AboutDialog.class.getResource("images/srsoftlogo.png"));

            imgPanel = new ImageBackgroundPanel(image, 65, 10);
            imgPanel.setBackground(Color.WHITE);
            this.add(imgPanel, BorderLayout.CENTER);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        aboutUsLabel = new JLabel();

        msgText = "About The Smart Database Explorer " + LoginDialog.versionId;
        msgText2 = "Coded Using Java Swing, JDK : v6.26";
        msgText3 = "Developed By : Sumit Roy <br> <h4 style = \"text-align : center;color : \t#800080;\"> Visit us at - <u>https://www.srsoftwares.co.nr</u>";
        copyRt = String.valueOf('\u00A9');
        trademark = String.valueOf('\u2122');
        msgText4 = "  Copy Rights " + copyRt + " 2011, SR Softwares" + trademark + ", All Rights Reserved ";

        aboutUsLabel.setText("<html>" + "<h3 style = \"text-align :center ; color : \t#8B0000 \">" + msgText + "<br>" + "<h4 style = \"text-align : center;color : \t#800080;\">" + msgText2 + "<h4 style = \"text-align : center;color : \t#800080;\">" + msgText3 + "<h4 style = \"text-align : center;color : #FF0000 ;\">" + msgText4 + "</html>");

        aboutUsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = (int) e.getPoint().getX();
                int y = (int) e.getPoint().getY();

                if (x >= 115 && x <= 310 && y >= 116 && y <= 130) {
                   openSite();
                }
            }

        });

        mainPanel.add(aboutUsLabel, JLabel.CENTER);

        this.add(mainPanel, BorderLayout.SOUTH);
    }

    private void openSite() {
        URI uri = null;
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

    /*public static void main(String[] args) {
         AboutDialog obj = new AboutDialog(571, 376);
    }*/

}
