package srsoftwares;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: 5/18/12
 * Time: 3:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class FontStyleDialog extends JDialog implements ItemListener {
    private JComboBox allFontSizeBox;
    private JComboBox allFontsBox;
    private JComboBox allFontStyleBox;
    private JButton okButton;
    private JLabel sampleLabel;
    private JPanel mainPanel;
    private JPanel sampleDisplayPanel;

    public FontStyleDialog(final MainView mainView, final QueryEditorPanel parent) {
        super(mainView);
        mainView.setEnabled(false);
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);

        sampleDisplayPanel = new JPanel(new BorderLayout());
        sampleDisplayPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        okButton = new JButton("Use Font");
        try {
            okButton.setIcon(new ImageIcon(ImageIO.read(this.getClass().getResource("images/okMouseOut.png"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int fontSize = Integer.parseInt(allFontSizeBox.getSelectedItem().toString());
                int fontStyle = MiscUtility.fontStylesMap.get(allFontStyleBox.getSelectedItem().toString());
                String fontType = allFontsBox.getSelectedItem().toString();
                Font f=new Font(fontType,fontStyle,fontSize);
                parent.setQueryTextAreaFont(f);
                mainView.setEnabled(true);
                FontStyleDialog.this.dispose();
            }
        });

        MiscUtility.changeButtonLook(okButton, "okMouseIn.png", "okMouseOut.png", "Apply this Font style");

        JLabel fontLabel = new JLabel("Font");
        allFontsBox = new JComboBox(MiscUtility.getAllSystemFonts());
        allFontsBox.setSelectedItem("Times New Roman");
        Dimension dimension = new Dimension(150, 25);
        allFontsBox.setMaximumSize(dimension);
        allFontsBox.setMinimumSize(dimension);
        allFontsBox.setPreferredSize(dimension);
        MiscUtility.addScrollAction(allFontsBox);
        allFontsBox.addItemListener(this);


        JLabel fontStyle = new JLabel("Font Style");
        allFontStyleBox = new JComboBox(MiscUtility.getAllFontStyles());
        allFontStyleBox.setMaximumSize(new Dimension(100, 25));
        allFontStyleBox.setMinimumSize(new Dimension(100, 25));
        allFontStyleBox.setPreferredSize(new Dimension(100, 25));
        allFontStyleBox.setSelectedItem("Normal");
        MiscUtility.addScrollAction(allFontStyleBox);
        allFontStyleBox.addItemListener(this);

        JLabel fontSize = new JLabel("Size");
        allFontSizeBox = new JComboBox(MiscUtility.getAllSystemFontSizes());
        allFontSizeBox.setSelectedItem(20);
        allFontSizeBox.setMaximumSize(new Dimension(40, 25));
        allFontSizeBox.setMinimumSize(new Dimension(40, 25));
        allFontSizeBox.setPreferredSize(new Dimension(40, 25));
        MiscUtility.addScrollAction(allFontSizeBox);
        allFontSizeBox.addItemListener(this);

        mainPanel.add(Box.createHorizontalStrut(10));

        mainPanel.add(fontLabel);
        mainPanel.add(Box.createHorizontalStrut(3));
        mainPanel.add(allFontsBox);

        mainPanel.add(Box.createHorizontalStrut(10));

        mainPanel.add(fontStyle);
        mainPanel.add(Box.createHorizontalStrut(3));
        mainPanel.add(allFontStyleBox);

        mainPanel.add(Box.createHorizontalStrut(10));

        mainPanel.add(fontSize);
        mainPanel.add(Box.createHorizontalStrut(3));
        mainPanel.add(allFontSizeBox);

        mainPanel.add(Box.createHorizontalStrut(7));

        mainPanel.add(okButton);
        mainPanel.add(Box.createHorizontalStrut(10));
        this.add(mainPanel, BorderLayout.NORTH);
        mainPanel.updateUI();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int w = e.getWindow().getSize().width;
                int h = e.getWindow().getSize().height;
              //  System.out.println("Optimized View : Width = " + w + "\n\nHeight = " + h);
                mainView.setEnabled(true);

            }
        });

        // SETTING BORDER FOR SAMPLE DISPLAY PANEL

        TitledBorder samplePanelTitle = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED, Color.GRAY, Color.WHITE), "Sample Output");
        samplePanelTitle.setTitleJustification(TitledBorder.LEFT);
        sampleDisplayPanel.setBorder(samplePanelTitle);


        sampleLabel = new JLabel("Sample Text", JLabel.CENTER);
        sampleLabel.setBackground(Color.WHITE);
        updateDisplayLevel();
        sampleDisplayPanel.add(sampleLabel, BorderLayout.CENTER);
        this.add(sampleDisplayPanel, BorderLayout.CENTER);

        this.setSize(563, 200);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(mainView);
        this.setTitle("Select Font,Styles,Size");
        this.setVisible(true);

    }

    public void updateDisplayLevel() {
        int fontSize = Integer.parseInt(allFontSizeBox.getSelectedItem().toString());
        int fontStyle = MiscUtility.fontStylesMap.get(allFontStyleBox.getSelectedItem().toString());
        String fontType = allFontsBox.getSelectedItem().toString();
        sampleLabel.setFont(new Font(fontType, fontStyle, fontSize));
        sampleLabel.updateUI();

    }

//    public static void main(String[] args) {
//        FontStyleDialog obj = new FontStyleDialog();
//    }


    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == allFontsBox) {
            updateDisplayLevel();
        } else if (e.getSource() == allFontStyleBox) {
            updateDisplayLevel();
        } else {
            updateDisplayLevel();
        }
    }
}


