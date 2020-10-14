package data;

import board.Card;
import board.Location;

import java.util.ArrayList;
//TODO: make singleton class

public class Data {
    private ArrayList<Card> allCards;
    private ArrayList<Location> allLocations;

    public Data() { }

    public ArrayList<Card> getAllCards() {
        return allCards;
    }

    public void setAllCards(ArrayList<Card> allCards) {
        this.allCards = allCards;
    }

    public ArrayList<Location> getAllLocations() {
        return allLocations;
    }

    public void setAllLocations(ArrayList<Location> allLocations) {
        this.allLocations = allLocations;
    }

}
