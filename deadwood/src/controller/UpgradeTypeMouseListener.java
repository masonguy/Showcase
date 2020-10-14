package controller;

import game.DeadwoodGUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class UpgradeTypeMouseListener extends BaseDeadwoodMouseListener {
    private String type = "";
    private int rank;
    String playerString = "";
    Controller cont = Controller.getInstance();


    public UpgradeTypeMouseListener(String type, int rank) {
        this.type = type;
        this.rank = rank;
        playerString = "You have chosen type: " + type;
    }

    public void mouseClicked(MouseEvent e) {
        System.out.println(playerString);
        cont.upgradePlayer(type, rank);
    }

}