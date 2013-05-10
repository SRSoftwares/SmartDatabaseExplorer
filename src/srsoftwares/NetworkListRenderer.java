package srsoftwares;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: Sumit Roy
 * Date: 11/15/11
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.                                                                                                                      0
 */
public class NetworkListRenderer extends DefaultListCellRenderer {


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        boolean addedSystems=false;
         ScanNetworks.hostBoxItems=ScanNetworks.parent.getHostItems();

        if(ScanNetworks.listModelMap.containsKey(value)){
        String ip=ScanNetworks.listModelMap.get(value);
         if(ScanNetworks.hostBoxItems.contains(ip)){
             addedSystems=true;
            System.out.println("\n ######  IP ADDRESS IN RENDERER IS = "+value+" and is it already added to hostBox = ?? "+addedSystems);
         }
        }


        Image image = null;
        try {

            image = ImageIO.read(this.getClass().getResource("images/networkPC.png"));

            if(addedSystems){
                System.out.println(" INSIDE IF BLOCK  and icon to set is = OK ICON for IP = "+value);
                image = ImageIO.read(this.getClass().getResource("images/addedSystem.png"));
            }


            this.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return comp;   //To change body of overridden methods use File | Settings | File Templates.
    }
}
