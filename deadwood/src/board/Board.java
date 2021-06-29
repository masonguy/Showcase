package board;

import data.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

//Singleton class
public class Board {
    private static Board instance;
    private ArrayList<Card> cardsOnBoard;
    private Data data;

    private Board(){ }

    //Use this to access instance of Board
    public static Board getInstance() {
        if(instance==null){
            instance = new Board();
        }
        return instance;
    }

    public ArrayList<Card> getCardsOnBoard() {
        return cardsOnBoard;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public ArrayList<Card> newCards() {
        ArrayList<Card> addedCards = new ArrayList<>();
        cardsOnBoard = data.getAllCards();
        Collections.shuffle(cardsOnBoard);
        for(Location l : data.getAllLocations()) {
            for(Role r : cardsOnBoard.get(0).getRoles()){
                r.setMovie(l);
            }
            addedCards.add(cardsOnBoard.get(0));
            l.setCard(cardsOnBoard.get(0));
            l.getCard().setFaceUp(false);
            cardsOnBoard.remove(0);
        }
        return addedCards;
    }

    public void newShotCounters(){

    }
}
