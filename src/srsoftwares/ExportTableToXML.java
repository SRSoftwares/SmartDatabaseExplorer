package srsoftwares;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: May 2, 2011
 * Time: 3:30:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExportTableToXML extends JDialog implements ActionListener {
   private String schemaName;
   private String tableName;
   private JButton finishButton;
   private JFileChooser fileChooser;
    public ExportTableToXML(String schemaName,String tableName) {
        this.schemaName=schemaName;
        this.tableName=tableName;
        setTitle("Export "+tableName+ " As XML");
        JPanel containerPanel=new JPanel(new BorderLayout());
        fileChooser=new JFileChooser();
        fileChooser.setControlButtonsAreShown(false);
        containerPanel.add(fileChooser,BorderLayout.CENTER);

        JPanel lowerPanel=new JPanel(new FlowLayout()) ;
        finishButton=new JButton("Finish");
        finishButton.addActionListener(this);
        lowerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        lowerPanel.add(finishButton);
        containerPanel.add(lowerPanel,BorderLayout.SOUTH);

        add(containerPanel);
        
        pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

//    public static void main(String[] args) {
//        sumit.demoproject.ExportTableToXML obj=new sumit.demoproject.ExportTableToXML("TEST","TEST");
//    }

    public void actionPerformed(ActionEvent e) {
        JTextField txtName = getChooserComponents(fileChooser);
        String fileFullName = txtName.getText();
        File dirName = fileChooser.getCurrentDirectory();
        File file = new File(dirName + File.separator + fileFullName);
        GenerateXMLTableDetailsFile.createXMLTableDetails(file, schemaName, tableName);
        ExportTableToXML.this.dispose();

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
