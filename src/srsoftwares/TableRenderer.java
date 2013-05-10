package srsoftwares;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: Apr 18, 2011
 * Time: 11:39:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class TableRenderer extends DefaultTableCellRenderer {


    public Component getTableCellRendererComponent(JTable table, Object value, boolean sel, boolean hasFocus, int row, int col) {
        Component comp = super.getTableCellRendererComponent(table, value, sel, hasFocus, row, col);
        comp.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        Color backColor=new Color(188,210,238);
        comp.setForeground(new Color(0,0,0));

        if (row % 2 == 0) {
            comp.setBackground(backColor);

        } else {
            comp.setBackground(Color.white);
        }
        JLabel label = (JLabel) comp;


        if (value instanceof Integer) {
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            label.setHorizontalAlignment(SwingConstants.LEFT);

        }
        if (value instanceof Boolean) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setHorizontalAlignment(SwingConstants.CENTER);
            checkBox.setSelected((Boolean) value);

            if (row % 2 == 0) {
               checkBox.setBackground(backColor);
            } else {
               checkBox.setBackground(Color.white);
            }

            return checkBox;


        }
        return this;
    }
}
