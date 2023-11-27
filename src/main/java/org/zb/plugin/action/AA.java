package org.zb.plugin.action;

import javax.swing.*;

/**
 * @author : ZhouBin
 */
public class AA {
    public static void main(String[] args) {


        SwingUtilities.invokeLater(()->{
            H2MPanel panel = new H2MPanel(null);
            panel.show();
        });
        System.out.println(222);
    }
}
