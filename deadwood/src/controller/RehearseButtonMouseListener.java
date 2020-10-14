package controller;

import game.DeadwoodGUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class RehearseButtonMouseListener extends BaseDeadwoodMouseListener {
    String playerString = "You have selected rehearse\n";
    Controller cont = Controller.getInstance();

    public void mouseClicked(MouseEvent e) {
        cont.rehearseButtonClicked();
        System.out.println(playerString);
    }

}