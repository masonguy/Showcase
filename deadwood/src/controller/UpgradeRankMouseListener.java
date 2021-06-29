package controller;

import game.DeadwoodGUI;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class UpgradeRankMouseListener extends BaseDeadwoodMouseListener {
    private int rank = 0;
    String playerString = "";
    Controller cont = Controller.getInstance();


    public UpgradeRankMouseListener(int rank) {
        this.rank = rank;
        playerString = "You want to upgrade to a specfic rank " + rank;
    }

    public void mouseClicked(MouseEvent e) {
        System.out.println(playerString);
        cont.canUpgrade(rank);
    }

}