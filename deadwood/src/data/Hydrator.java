package data;

import board.Board;
import board.Location;
import org.w3c.dom.Document;

//TODO: hydrator: instantiate data object with parsexml
public class Hydrator {

    public Hydrator() { }

    public void boardSetup(Data data){
        //TODO: setup neighbors
        Board board = Board.getInstance();
        board.setData(data);
    }

    public void setupData(){
        ParseXML parser = new ParseXML();
        Data data = new Data();
        Document doc = null;

        try{
            doc = parser.getDocFromFile("src/resources/cards.xml");
            parser.readCardData(doc);
            doc = parser.getDocFromFile("src/resources/board.xml");
            parser.readBoardData(doc);
        }catch (Exception e){
            System.out.println("Error = "+e);
        }
        data.setAllCards(parser.getCardArrayList());
        data.setAllLocations(parser.getLocationArrayList());
        boardSetup(data);
    }

}