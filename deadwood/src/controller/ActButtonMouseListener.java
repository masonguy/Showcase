package controller;

import game.DeadwoodGUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ActButtonMouseListener extends BaseDeadwoodMouseListener {
    String playerString = "You have selected act\n";
    Controller cont = Controller.getInstance();

    public void mouseClicked(MouseEvent e) {
        cont.actClicked();
        System.out.println(playerString);
    }


}