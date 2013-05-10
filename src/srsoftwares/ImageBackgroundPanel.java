package srsoftwares;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * Author: Sumit Roy
 * Date: 19/8/11
 * Time: 11:54 PM
 * To change this template use File | Settings | File Templates.
 */
 public class ImageBackgroundPanel extends JPanel {
        Image image;
        int x;
        int y;
        public ImageBackgroundPanel(Image image, int x, int y) {
            this.setLayout(new FlowLayout(FlowLayout.CENTER));
            this.image = image;
            this.x=x;
            this.y=y;

        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, x, y, this);
        }
    }

