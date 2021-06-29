package game;


import board.Board;
import board.Location;
import controller.Controller;
import data.Hydrator;
import game.handlers.ActHandler;
import user.Dice;
import user.Player;

import java.util.ArrayList;
import java.util.Scanner;

public class Game {
    private Board board = Board.getInstance();
    private int numPlayers;
    private int dayCount = 1;
    private Player[] players;



    private static int counter;



    private Player activePlayer;
    private Scanner scan = new Scanner(System.in);
    private Location defaultLocation;
    private Controller cont;

    private static int playerIndex;

    public Game (int numPlayers) {
        this.numPlayers = numPlayers;

        startGame();
    }

    public void startGame() {
        counter = 0;
        Hydrator hydr = new Hydrator();
        cont = Controller.getInstance();
        hydr.setupData();
        cont.distributeCards();
        //cont.distributeShotCounters();
        for(Location l : board.getData().getAllLocations()) {
            if(l.getName().equalsIgnoreCase("Trailer")) {
                defaultLocation = l;
            }
        }
    }

    public void initPlayers(String[] arr) {
        String[] diceArr = {"blue", "red", "purple"};
        players = new Player[numPlayers];
        for(int i = 0; i < numPlayers; i++) {
            String name = arr[i];
            System.out.println(name);
            Dice die = new Dice(diceArr[i]);
            Player player = new Player(die, null , defaultLocation, name, 1 , 0,0, false);

            players[i] = player;

        }
    }

    //TODO: make sure 2 people can't take a role (use isTaken field)
    public void startPlayerTurn(int i) {
        if(checkEnd() && dayCount == 4) {
            //todo: post winner
            Player winner = calcWinner();
            cont.postWinner(winner);
        } else if (checkEnd() ) {
            dayCount++;
            cont.notifyNewDay(dayCount);
            //board.newCards(); //todo: make sure this is removing used cards
            cont.setToTrailer();
            cont.distributeCards();
        }
        cont.updatePlayerInfoList(players);
        counter++;
        cont.setCounter(counter);
        playerIndex = i;
        if(playerIndex >= numPlayers) {
            playerIndex = 0;
        }
        ActHandler acter = new ActHandler();

        activePlayer = players[playerIndex];
        displayActivePlayer();

    }

   public Player calcWinner() {
       Player winner = null;
       int score = 0;
       for(Player p : players) {
           int cur = p.getDollars() + p.getCredits() + (p.getRank()*5);
           if(cur > score) {
               score = cur;
               winner = p;
           }
       }

       return winner;
   }

   public boolean checkEnd() {
       boolean over = false;
       int count = 0;
       ArrayList<Location> locations = board.getData().getAllLocations();
       for (Location l : locations) {
           if(l.getShotCounters() == 0) {
               count++;
           }
       }
       if(count == 11) {
           over = true;
       }
       return over;
   }

   public boolean endClicked() {
       return false;
   }

    public void displayActivePlayer() {
        System.out.println("Active Player: " + activePlayer.getName()); //needs further implementation
        cont.displayActivePlayer(activePlayer);
    }

    public void removeActivePlayer() {
       cont.removeActivePlayer();
    }

    public static int getPlayerIndex() {
        return playerIndex;
    }

    public static void setPlayerIndex(int playerIndex) {
        Game.playerIndex = playerIndex;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    public void displayPlayerLocation() {
        for(int i = 0; i < numPlayers; i++) {
            System.out.println("Player: " + players[i].getName() + "Location: " + players[i].getLocation());
        }
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getdayCount() {
        return dayCount;
    }

    public void setdayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public Scanner getScan() {
        return scan;
    }

    public void setScan(Scanner scan) {
        this.scan = scan;
    }

    public Location getDefaultLocation() {
        return defaultLocation;
    }

    public void setDefaultLocation(Location defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

    public Controller getCont() {
        return cont;
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Game.counter = counter;
    }
}