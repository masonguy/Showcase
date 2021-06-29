package data;

// Example Code for parsing XML file
// Dr. Moushumi Sharmin
// CSCI 345
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import board.Card;
import board.Location;
import board.Role;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Boolean.FALSE;

public class ParseXML{


    // building a document from the XML file
    // returns a Document object after loading the book.xml file.
    private ArrayList<Card> cardArrayList = new ArrayList<Card>();
    private ArrayList<Location> locationArrayList = new ArrayList<Location>();

    public ArrayList<Location> getLocationArrayList() {
        return locationArrayList;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getLine() {
        return line;
    }

    public String getDescription() {
        return description;
    }

    public String getLocationName() {
        return locationName;
    }

    private String roleName, line, description, locationName;
    public Document getDocFromFile(String filename) throws
            ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = null;
        try{
            doc = db.parse(filename);
        } catch (Exception ex){
            System.out.println("XML parse failure");
            ex.printStackTrace();
        }
        return doc;
    }
    // reads data from XML file and prints data
    public void readCardData(Document d){
        Element root = d.getDocumentElement();
        NodeList cards = root.getElementsByTagName("card");
        for (int i=0; i<cards.getLength();i++) {
            Node singleCard = cards.item(i);
            String name = singleCard.getAttributes().getNamedItem("name").getNodeValue();
            String imageName = singleCard.getAttributes().getNamedItem("img").getNodeValue();
            String toChange = singleCard.getAttributes().getNamedItem("budget").getNodeValue();

            NodeList children = singleCard.getChildNodes();
            ArrayList<Role> roleArrayList = new ArrayList<Role>();
            for(int j = 0; j < children.getLength(); j++) {
                Node sub = children.item(j);

                int roleDifficulty = 0;

                if("scene".equals(sub.getNodeName())) {
                    description = sub.getFirstChild().getNodeValue();
                }
                if("part".equals(sub.getNodeName())) {
                    roleName = sub.getAttributes().getNamedItem("name").getNodeValue();
                    roleDifficulty = Integer.parseInt(sub.getAttributes().getNamedItem("level").getNodeValue());
                }
                NodeList moreChild = sub.getChildNodes();
                for(int k = 0; k < moreChild.getLength(); k++) {
                    Node child = moreChild.item(k);
                    if("line".equals(child.getNodeName())) {
                        line = child.getFirstChild().getNodeValue();

                    }
                    if("area".equals(child.getNodeName())) {
                        int x,y,h,w = 0;
                        x = Integer.parseInt(child.getAttributes().getNamedItem("x").getNodeValue());
                        y = Integer.parseInt(child.getAttributes().getNamedItem("y").getNodeValue());
                        h = Integer.parseInt(child.getAttributes().getNamedItem("h").getNodeValue());
                        w = Integer.parseInt(child.getAttributes().getNamedItem("w").getNodeValue());
                        if(!roleName.equals("")) {
                            Role role = new Role(null, roleName, roleDifficulty, false, false, line, x, y, h, w, null);
                            //System.out.println(x + " " + y + " " + h + " " + w);
                            roleArrayList.add(role);
                        }
                    }

                }

            }
            Card card = new Card(name, FALSE, null, roleArrayList, Integer.parseInt(toChange), description, false, imageName);
            cardArrayList.add(card);
        }
        /*System.out.println("card " + cardArrayList.size());
        for (Card c : cardArrayList) {
            System.out.println(c.getName());
            System.out.println(c.getDescription());
            System.out.println(c.getBudget());
            for(Role r : c.getRoles()) {
                System.out.println(r.getRoleName());
                System.out.println(r.getDifficulty());
                System.out.println(r.getLine());
                System.out.println();
            }
            System.out.println();

        }*/
    }

    public void readBoardData(Document d){



        HashMap<String,Integer>  stationMap= new HashMap<String, Integer>();
        stationMap.put("Jail",0);
        stationMap.put("General Store", 1);
        stationMap.put("Casting Office", 2);
        ArrayList<Role> listTrainStation = new ArrayList<Role>();
        Location station = new Location(false, "Train Station", null, 3, null, null, stationMap, 21,69,115,205 ,null);
        listTrainStation.add(new Role(station, "Crusty Prospector", 1, false, false, "Aww, peaches!", 114, 227, 46, 46, null));
        listTrainStation.add(new Role(station, "Dragged by Train", 1,false,false,"Omgeezers", 51,268,46,46, null));
        listTrainStation.add(new Role(station, "Preacher with Bag", 2, false, false, "The Lord will provide",114,320,46,46, null));
        listTrainStation.add(new Role(station, "Cyrus the Gunfighter", 4, false, false, "Git to fightin' or git away",49,356,46,46, null));
        station.setRoles(listTrainStation);
        locationArrayList.add(station);

        HashMap<String,Integer>  hideoutMap= new HashMap<String, Integer>();
        hideoutMap.put("Church",0);
        hideoutMap.put("Ranch", 1);
        hideoutMap.put("Casting Office", 2);
        ArrayList<Role> listHideout = new ArrayList<Role>();
        Location hideout = new Location(false, "Secret Hideout", null, 3, null, null, hideoutMap,27,732,115,205 ,null);
        listHideout.add(new Role(station, "Clumsy Pit Fighter", 1, false, false, "Hit me!",435,719,46,46, null));
        listHideout.add(new Role(station, "Thug with Knife", 2,false,false,"Meet Suzy, my muderin' knife", 521,719,46,46, null));
        listHideout.add(new Role(station, "Dangerous Tom", 3, false, false, "There's two ways we can do this....", 435, 808, 46,46, null));
        listHideout.add(new Role(station, "Penny, who is lost", 4, false, false, "Oh, wow! for I am lost", 521, 808, 46, 46, null));
        hideout.setRoles(listHideout);
        locationArrayList.add(hideout);

        HashMap<String,Integer>  churchMap= new HashMap<String, Integer>();
        churchMap.put("Secret Hideout",0);
        churchMap.put("Bank", 1);
        churchMap.put("Hotel", 2);
        ArrayList<Role> listChurch = new ArrayList<Role>();
        Location church = new Location(false, "Church", null, 2, null, null, churchMap,623,734,115,205 ,null);
        listChurch.add(new Role(church, "Dead Man", 1, false, false, "....", 857, 730, 46, 46, null));
        listChurch.add(new Role(church, "Crying Woman", 2,false,false,"Oh, the humanity!", 858, 809,46,46, null));
        church.setRoles(listChurch);
        locationArrayList.add(church);

        HashMap<String,Integer>  hotelMap= new HashMap<String, Integer>();
        hotelMap.put("Church",0);
        hotelMap.put("Ranch", 1);
        hotelMap.put("Casting Office", 2);
        ArrayList<Role> listHotel = new ArrayList<Role>();
        Location hotel = new Location(false, "Hotel", null, 3, null, null, hotelMap, 969, 740, 115, 205 ,null);
        listHotel.add(new Role(hotel, "Sleeping Drunkard", 1, false, false, "Zzzzzzz...Whiskey!", 1111,469,46,46, null));
        listHotel.add(new Role(hotel, "Faro Player", 1,false,false,"Hit me!", 1044, 509 , 46,46, null));
        listHotel.add(new Role(hotel, "Falls from Balcony", 2, false, false, "Arrrgghh!", 1111,557,46,46, null));
        listHotel.add(new Role(hotel, "Australian Bartender", 3, false, false, "What'll it be, mate?",1046, 596 , 46,46, null));
        hotel.setRoles(listHotel);
        locationArrayList.add(hotel);

        HashMap<String,Integer>  streetMap= new HashMap<String, Integer>();
        streetMap.put("Trailer",0);
        streetMap.put("Jail", 1);
        streetMap.put("Saloon", 2);
        ArrayList<Role> listStreet = new ArrayList<Role>();
        Location street = new Location(false, "Main Street", null, 3, null, null, streetMap,969,28,115,205 ,null);
        listStreet.add(new Role(street, "Railroad Worker", 1, false, false, "Hit me!", 637,22,46,46, null));
        listStreet.add(new Role(street, "Falls of Roof", 2,false,false,"Aaaaiiiigggghh!",720, 22,46,46, null));
        listStreet.add(new Role(street, "Woman in Black Dress", 2, false, false, "Well, I'll be!",637,105,46,46, null));
        listStreet.add(new Role(street, "Mayor McGinty", 4, false, false, "People of Deadwood!",720,105 ,46,46, null));
        street.setRoles(listStreet);
        locationArrayList.add(street);


        HashMap<String,Integer>  jailMap= new HashMap<String, Integer>();
        jailMap.put("Main Street",0);
        jailMap.put("General Store", 1);
        jailMap.put("Train Station", 2);
        ArrayList<Role> listJail = new ArrayList<Role>();
        Location jail = new Location(false, "Jail", null, 1, null, null, jailMap, 281,27,115,205 ,null);
        listJail.add(new Role(jail, "Prisoner in Cell", 2, false, false, "Zzzzzzz...Whiskey!",519,25,46,46, null));
        listJail.add(new Role(jail, "Feller in irons", 3,false,false,"Ah kilt the wrong man!", 519,105,46,46, null));
        jail.setRoles(listJail);
        locationArrayList.add(jail);

        HashMap<String,Integer>  storeMap= new HashMap<String, Integer>();
        storeMap.put("Ranch",0);
        storeMap.put("Train Station", 1);
        storeMap.put("Jail", 2);
        storeMap.put("Saloon", 3);
        ArrayList<Role> listStore = new ArrayList<Role>();
        Location store = new Location(false, "General Store", null, 2, null, null, storeMap, 370,282,115,205,null);
        listStore.add(new Role(store, "Man in Overalls", 1, false, false, "Looks like a storm's comin' in.",236,276,46,46, null));
        listStore.add(new Role(store, "Mister Keach", 3,false,false,"Howdy, stranger",236,358 ,46,46, null));
        store.setRoles(listStore);
        locationArrayList.add(store);


        HashMap<String,Integer>  ranchMap= new HashMap<String, Integer>();
        ranchMap.put("Casting Office", 0);
        ranchMap.put("General Store", 1);
        ranchMap.put("Secret Hideout", 2);
        ranchMap.put("Bank", 3);
        ArrayList<Role> listRanch = new ArrayList<Role>();
        Location ranch = new Location(false, "Ranch", null, 2, null, null, ranchMap, 252,478,115,205,null);
        listRanch.add(new Role(ranch, "Shot in Leg", 1, false, false, "Ow! Me Leg!",412,608 ,46,46, null));
        listRanch.add(new Role(ranch, "Saucy Fred", 2,false,false,"That's what she said.", 488,608,46,46, null));
        listRanch.add(new Role(ranch, "Man Under Horse", 3, false, false, "A little help here!", 488,525,46,46, null));
        ranch.setRoles(listRanch);
        locationArrayList.add(ranch);

        HashMap<String,Integer>  bankMap= new HashMap<String, Integer>();
        bankMap.put("Saloon",0);
        bankMap.put("Church", 1);
        bankMap.put("Ranch", 2);
        bankMap.put("Hotel", 3);
        ArrayList<Role> listBank = new ArrayList<Role>();
        Location bank = new Location(false, "Bank", null, 1, null, null, bankMap, 623,475,115,205 ,null);
        listBank.add(new Role(bank, "Suspicious Gentleman", 2, false, false, "Can you be more specific?",911,554,46,46, null));
        listBank.add(new Role(bank, "Flustered Teller", 3,false,false,"Would  you like a large bill, sir?",911, 470,46,46, null));
        bank.setRoles(listBank);
        locationArrayList.add(bank);

        HashMap<String,Integer>  saloonMap= new HashMap<String, Integer>();
        saloonMap.put("Main Street",0);
        saloonMap.put("General Store", 1);
        saloonMap.put("Bank", 2);
        saloonMap.put("trailer", 3);
        ArrayList<Role> listSaloon = new ArrayList<Role>();
        Location saloon = new Location(false, "Saloon", null, 2, null, null, saloonMap, 632,280,115,205 ,null);
        listSaloon.add(new Role(saloon, "Reluctant Farmer", 1, false, false, "I ain't so sure about that!",877,352,46,46, null));
        listSaloon.add(new Role(saloon, "Woman in Red Dress", 2,false,false,"Come up and see me!", 877,276,46,46, null));
        saloon.setRoles(listSaloon);
        locationArrayList.add(saloon);


        HashMap<String,Integer>  trailerMap= new HashMap<String, Integer>();
        trailerMap.put("Main Street",0);
        trailerMap.put("Saloon", 1);
        trailerMap.put("Hotel", 2);
        Location trailer = new Location(false, "Trailer", null, 0, null, null, trailerMap, 991,248,194,201 ,null);
        locationArrayList.add(trailer);

        HashMap<String,Integer>  officeMap= new HashMap<String, Integer>();
        officeMap.put("Train Station",0);
        officeMap.put("Ranch", 1);
        officeMap.put("Secret Hideout", 2);
        Location office = new Location(false, "Casting Office", null,0, null, null, officeMap,9,459,208,209,null);
        locationArrayList.add(office);


        /*for(Location l : locationArrayList) {
            System.out.println(l.getName());
            System.out.println(l.getShotCounters());
        }*/
    }



    public ArrayList<Card> getCardArrayList() {
        return cardArrayList;
    }

}

//Element root = d.getDocumentElement();
//NodeList locations = root.getElementsByTagName("set");
        /*for (int i = 0; i < locations.getLength(); i ++) {
            HashMap<String,Integer>  neighborMap= new HashMap<String, Integer>();
            Node locChild = locations.item(i);
            String locationName = locChild.getAttributes().getNamedItem("name").getNodeValue();
            NodeList neighborList = root.getElementsByTagName("neighbors");
            System.out.println(neighborList.getLength());
            for (int j = 0; j < neighborList.getLength(); j++) {
                Node child = neighborList.item(j);
                String neighborName = child.getAttributes().getNamedItem("name").getNodeValue();
                System.out.println(neighborName);
            }
            ArrayList<Role> roleArrayList = new ArrayList<Role>();

        Location location = new Location(false, locationName, null, 0,null,null, neighborMap);
        locationArrayList.add(location);
        }*/