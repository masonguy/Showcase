package controller;

import board.*;
import game.CardMaker;
import game.Deadwood;
import game.DeadwoodGUI;
import game.Game;
import game.handlers.ActHandler;
import user.Player;

import javax.swing.*;
import java.util.ArrayList;


public class Controller {
    private  static Controller instance;
    private DeadwoodGUI gui;
    private Deadwood deadwood;
    private static Board board;

    private static int counter;


    private Player player;


    private Player[] players;

    private Game game;

    private Controller() {

    }

    public static Controller getInstance() {
        if(instance == null) {
            instance = new Controller();
            board = Board.getInstance();
        }
        return instance;
    }



    public void numberOfPlayersController() {
        int numPlayers = 0;
        gui.setUpVisible();
        JComboBox combo = gui.getNumPlayerBox();
        String playerOptions[] = { "1","2","3"};
        numPlayers = Integer.parseInt(playerOptions[combo.getSelectedIndex()]);
        deadwood.setNumPlayers(numPlayers);
        game = deadwood.initializeGame();

    }

    public void inputNames() {
        String[] names = gui.getPlayerNames();
        game.initPlayers(names);
        gui.initPlayerDisplay();
        game.startGame();
        game.startPlayerTurn(0);
        gui.initTurnOptions();
        players = game.getPlayers();
    }

    public void setToTrailer() {
        gui.setToTrailer();
    }

    public void postWinner(Player winner) {
        gui.postWinner(winner);
    }

    public void updatePlayerInfoList(Player[] players) {
        gui.updatePlayerInfoList(players);
    }

    public void moveClicked() {
        Player player = game.getActivePlayer();

        if(!player.isActing()) {
            gui.closeTurnOptions();
            gui.displayMoveOptions(game.getActivePlayer().getLocation().getNeighbors());
        } else {
            gui.displayMoveInvalid();
        }
        game.startPlayerTurn(game.getPlayerIndex()+1);
        player = game.getActivePlayer();
    }

    public void actClicked() {
        Player[] playerArr = game.getPlayers();
        ArrayList<Player> allPlayers = new ArrayList<>();

        for(Player pl : playerArr){
            allPlayers.add(pl);
        }

        ActHandler handler = new ActHandler();
        Player activePlayer = game.getActivePlayer();
        if(activePlayer.getLocation().getName().equalsIgnoreCase("Casting Office") || activePlayer.getLocation().getName().equalsIgnoreCase("trailer") || activePlayer.getLocation().getCard().isWrapped()) {
            gui.cantActHere();

        } else {
            //gui.closeTurnOptions();
            Role curRole = game.getActivePlayer().getRole();
            if (!activePlayer.isActing() && !activePlayer.getLocation().getCard().isWrapped()) { //player has not selected role
                curRole = gui.displayActOptions(game.getActivePlayer());
            }

            if (activePlayer.getLocation().getShotCounters() > 0) {
                String input = gui.displayActOrRehearse(activePlayer, curRole);
                if (input.equals("Act")) {
                    if (handler.act(activePlayer, curRole)) { //payout
                        ArrayList<Role> allRoles = activePlayer.getLocation().getRoles();
                        ArrayList<Role> cardRoles = activePlayer.getLocation().getCard().getRoles();
                        allRoles.addAll(cardRoles);
                        ArrayList<Player> playersWorkingMovie = new ArrayList<>();

                        for (Role r : allRoles) {
                            if (r.isTaken()) {
                                playersWorkingMovie.add(r.getPlayer());
                            }
                        }
                        handler.payout(playersWorkingMovie);
                        activePlayer.getLocation().getCard().setWrapped(true);

                        //todo set stuff: make sure players have no roles, card is wrapped, etc
                    }
                    //updateShotCounters
                    gui.updateShotCounters(curRole.getMovie());


                } else if (input.equals("Rehearse")) {
                    handler.rehearse(activePlayer);
                } else {
                    //none picked, end turn
                }
            }
            //play role, remember to set isActing to false in payout method if haven't already
            if (curRole.getMovie().getCard().isWrapped()) {
                gui.payoutPopup(allPlayers);
            } else {
                gui.updatePlayerInfo(allPlayers, activePlayer.getLocation());
            }
        }
        gui.removePlayerInfo();
        gui.updatePlayerInfoList(getPlayers());
        endClicked();
    }

    public void displayActivePlayer(Player player) {
        gui.displayActivePlayer(player);
    }

    public void notifyNewDay(int day) {
        gui.notifyNewDay(day);
    }

    public void removeActivePlayer(){
        gui.removeActivePlayer();
    }

    public void endClicked() {
        game.endClicked();
        game.removeActivePlayer();
        gui.closeTurnOptions();
        gui.initTurnOptions();
        gui.removePlayerInfo();
        game.startPlayerTurn(game.getPlayerIndex()+1);
        player = game.getActivePlayer();


    }

    public void rehearseButtonClicked(){
        if(game.getActivePlayer().isActing()){
            ActHandler handler = new ActHandler();
            handler.rehearse(game.getActivePlayer());
            gui.rehearsePopup(game.getActivePlayer(),true);
        }else{
            gui.rehearsePopup(game.getActivePlayer(),false);
        }
        game.startPlayerTurn(game.getPlayerIndex()+1);
    }

    public void canUpgrade(int rank) {
        gui.removeUpgradeButtons();
        UpgradeTable table = new UpgradeTable();
        int[] desired = table.getTable().get(rank);
        if(game.getActivePlayer().getCredits() < desired[1] && game.getActivePlayer().getDollars() < desired[0]) {
            gui.cantUpgradeFunds();
        } else {
            gui.chooseUpgradeOption(rank);
        }
    }

    public void upgradePlayer(String type, int rank) {
        gui.hideUpgradeOptions();
        UpgradeTable table = new UpgradeTable();
        int[] desired = table.getTable().get(rank);
        if(type.equalsIgnoreCase("credits")) {
            Player cur = game.getActivePlayer();
            if(cur.getCredits() >= desired[1] ) {
                cur.setCredits(cur.getCredits()-desired[1]);
                cur.setRank(rank);
                gui.updateRankImage(rank, cur.getDie().getColor());
            } else {
                gui.rankSelectionError();
                gui.hideUpgradeOptions();
            }
        } else if(type.equalsIgnoreCase("dollars")) {
            Player cur = game.getActivePlayer();
            if(cur.getDollars() >= desired[0]) {
                cur.setDollars(cur.getDollars()-desired[0]);
                cur.setRank(rank);
                gui.updateRankImage(rank, cur.getDie().getColor());
            } else {
                gui.rankSelectionError();

            }
        }
    }

    public void upgrade() {
        if(game.getActivePlayer().getLocation().getName().equalsIgnoreCase("casting office")) {
            gui.initUpgradePane(game.getActivePlayer().getRank());
        } else {
            gui.cantUpgrade();
        }
    }

    public void movePlayer(String str) {
        Location loc = null;
        for(Location l : board.getData().getAllLocations()) {
            if(l.getName().equalsIgnoreCase(str)) {
                loc = l;
            }
        }

        game.getActivePlayer().setLocation(loc);
        gui.movePlayerImage(loc.getX(), loc.getY(), loc.getH(), loc.getW(), game.getActivePlayer());
        gui.initTurnOptions();
        gui.removeMoveOptions();
        if(!loc.isVisited() && game.getActivePlayer().getLocation().getName().toLowerCase()!="trailers" && game.getActivePlayer().getLocation().getName().toLowerCase()!="casting office"){
            gui.placeCard(game.getActivePlayer().getLocation());
            loc.getCard().setFaceUp(true);
            loc.setVisited(true);
        }
    }

    public void distributeCards(){
        CardMaker cardMaker = new CardMaker();
        ArrayList<Card> newCards = board.newCards();
        ArrayList<Location> locations = board.getData().getAllLocations();
        gui.distributeNewCards(locations);
    }

    public void setGui(DeadwoodGUI gui) {
        this.gui = gui;
    }

    public Player[] getPlayers() {
        return players;
    }
    public Player getPlayer() {
        return player;
    }
    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Controller.counter = counter;
    }

    public void distributeShotCounters(){
        gui.distributeShotCounters(game.getBoard().getData().getAllLocations());
    }
}
