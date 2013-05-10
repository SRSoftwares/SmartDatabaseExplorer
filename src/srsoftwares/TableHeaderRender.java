package srsoftwares;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * Author: Sumit Roy
 * Date: 1/10/12
 * Time: 3:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class TableHeaderRender extends JLabel implements TableCellRenderer{

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label=new JLabel((String) value,JLabel.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Times New Roman",Font.BOLD,18));
        label.setForeground(Color.BLACK);
        return label;
    }
}
