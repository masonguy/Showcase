package controller;

import game.DeadwoodGUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MoveLocationMouseListener extends BaseDeadwoodMouseListener {

    Controller cont = Controller.getInstance();
    private String str;
    String playerString = "";

    public MoveLocationMouseListener(String str) {
        this.str = str;
        playerString = "You have selected to move to a " + str;
    }

    public void mouseClicked(MouseEvent e) {
        System.out.println(playerString);
        cont.movePlayer(str);
    }

}