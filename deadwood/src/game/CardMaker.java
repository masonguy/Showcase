package game;

import board.Board;
import board.Card;
import board.Location;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CardMaker {
    private DeadwoodGUI gui;
    private static Board board;

    public CardMaker() {
        this.gui = new DeadwoodGUI();
        this.board = Board.getInstance();
    }

    public DeadwoodGUI getGui() {
        return gui;
    }

    public void setGui(DeadwoodGUI gui) {
        this.gui = gui;
    }

    /*
    public void createCards(ArrayList<Card> newCards){

        for(Card c : newCards){
            //System.out.println(c.getImageName());
            //ImageIcon boardImage = new ImageIcon("src/resources/cards/"+c.getImageName());
            ImageIcon cardBack = new ImageIcon("src/resources/cards/");
        }
    }*/
/*
    public void flipCard(Location loc) {

        ImageIcon cardImage = new ImageIcon("src/resources/cards/" + loc.getCard().getImageName());
        //ImageIcon cardImage = new ImageIcon("src/resources/dice/c3.png");
        int x = loc.getX();
        int y = loc.getY();
        int h = loc.getH();
        int w = loc.getW();
        System.out.println(cardImage.toString());
        System.out.println(loc.getCard().getImageName());

        gui.placeCard(x,y,h,w, cardImage);
    }*/
}