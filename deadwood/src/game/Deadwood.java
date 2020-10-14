package game;

import javax.swing.*;
import javax.swing.SwingUtilities;

import controller.Controller;
import data.Data;
import data.ParseXML;
import org.w3c.dom.Document;

import java.awt.*;
import java.util.*;

public class Deadwood {

    private static int numPlayers;
    //private Game game;


    public static void main(String args[]) {
        // main class
        DeadwoodGUI gui = new DeadwoodGUI();
        Controller cont = Controller.getInstance();
        cont.setGui(gui);
        gui.setVisible(true);


        Document doc = null;
        ParseXML parsing = new ParseXML();
        try{
            doc = parsing.getDocFromFile("src/resources/cards.xml");
            parsing.readCardData(doc);
            doc = parsing.getDocFromFile("src/resources/board.xml");
           // parsing.readBoardData(doc);
        }catch (Exception e){
            System.out.println("Error = "+e);
        }
        //Scanner scan = new Scanner(System.in);
        //System.out.println("How many players? ");
        //numPlayers = scan.nextInt();


    }

    public static Game initializeGame() {
        return new Game(numPlayers);
    }

    public static void setNumPlayers(int numPlayers) {
        Deadwood.numPlayers = numPlayers;
    }


}