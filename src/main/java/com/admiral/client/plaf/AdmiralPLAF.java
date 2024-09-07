package com.admiral.client.plaf;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.logging.Logger;

public class AdmiralPLAF {
    private static final Logger log = Logger.getLogger(AdmiralPLAF.class.getName());

    public static JButton getOkButton(){
        JButton b = new JButton();
        //b.setIcon(new ImageIcon(AdmiralPLAF.class.getResource("icons/Ok24.gif")));
        //b.setIcon(new ImageIcon(Objects.requireNonNull(AdmiralPLAF.class.getResource("images/Ok24.gif"))));
        b.setMargin(new Insets(0, 10, 0, 10));
        b.setToolTipText("OK");
        return b;
    }

    public static JButton getCancelButton(){
        JButton b = new JButton();
        //b.setIcon(new ImageIcon(AdmiralPLAF.class.getResource("icons/Cancel24.gif")));
        //b.setIcon(new ImageIcon(Objects.requireNonNull(AdmiralPLAF.class.getResource("images/Cancel24.gif"))));
        b.setMargin(new Insets(0, 10, 0, 10));
        b.setToolTipText("OK");
        return b;
    }

    /**
     *  Center Window on Screen and show it
     *  @param window window
     */
    public static void showCenterScreen (Window window)
    {
        window.pack();
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wSize = window.getSize();
        window.setLocation(((sSize.width-wSize.width)/2), ((sSize.height-wSize.height)/2));
        window.toFront();
        window.setVisible(true);
    }	//	showCenterScreen
}
