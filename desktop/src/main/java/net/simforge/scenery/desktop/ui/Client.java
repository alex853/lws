package net.simforge.scenery.desktop.ui;

import net.simforge.scenery.desktop.ui.Form1;

import javax.swing.*;

@Deprecated
public class Client {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Light-Weighted Scenery");
        frame.setContentPane(new Form1().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
