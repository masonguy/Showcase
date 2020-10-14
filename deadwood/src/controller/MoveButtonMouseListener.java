package controller;

import game.DeadwoodGUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MoveButtonMouseListener extends BaseDeadwoodMouseListener {
    String playerString = "You have selected to move\n";
    Controller cont = Controller.getInstance();

    public void mouseClicked(MouseEvent e) {
        System.out.println(playerString);
        cont.moveClicked();
    }

}