package srsoftwares;

import javax.imageio.ImageIO;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: Apr 14, 2011
 * Time: 12:08:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeRenderer extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        Component comp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node == null) return comp;
        Image image = null;
        if (node.getLevel() == 0) {
            try {
                image = ImageIO.read(this.getClass().getResource("images/oracleLogo.jpg"));
            } catch (Exception e) {

            }
            setIcon(new ImageIcon(image));
            this.setFont(new Font("Serifs", Font.BOLD, 18));
        }
        else if (node.getLevel() == 1) {
            try {
                image = ImageIO.read(this.getClass().getResource("images/instance_16.gif"));
            } catch (Exception e) {

            }
            setIcon(new ImageIcon(image));
            this.setFont(new Font("Serifs", Font.BOLD, 16));
        } else if (node.getLevel() == 2) {
            try {
                image = ImageIO.read(this.getClass().getResource("images/schema_16.gif"));
            } catch (Exception e) {

            }
            setIcon(new ImageIcon(image));
            this.setFont(new Font("Arial", Font.BOLD, 15));
        } else if (node.getLevel() == 3) {
             try {
                image = ImageIO.read(this.getClass().getResource("images/table_16.gif"));
            } catch (Exception e) {

            }
            setIcon(new ImageIcon(image));
            this.setFont(new Font("Arial", Font.PLAIN, 12));
        }
        return comp;
    }

}
