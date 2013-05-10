package srsoftwares;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Apr 26, 2011
 * Time: 5:25:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExportXMLTreeRenderer extends DefaultTreeCellRenderer {
    private Image image;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        if (node.getLevel() == 0) {

        try {
                image = ImageIO.read(this.getClass().getResource("images/schema_16.gif"));
            } catch (Exception e) {

            }
            setIcon(new ImageIcon(image));
        } else if (node.getLevel() == 1) {
            
            JCheckBox checkBox= ExportXMLDialog.treeMap.get(node);
          checkBox.setBackground(Color.WHITE);
            

            return(checkBox);

        } else
            return comp;
        return comp;
    }
}
