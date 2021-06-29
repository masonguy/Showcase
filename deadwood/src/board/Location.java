package board;
import user.Player;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * make this superclass for different locations as subtypes of location?
 */
public class Location {

    private boolean isVisited;
    private String name;
    private Player[] players;
    private int shotCounters;
    private ArrayList<Role> roles;
    private Card card;
    private HashMap<String, Integer> neighbors;
    private int x;
    private int y;
    private int w;
    private int h;
    private ArrayList<ImageIcon> shotCounterList;

    public Location(boolean isVisited, String name, Player[] players, int shotCounters, ArrayList<Role> roles, Card card, HashMap<String, Integer> neighbors, int x, int y, int w, int h, ArrayList<ImageIcon> shotCounterList) {
        this.isVisited = isVisited;
        this.name = name;
        this.players = players;
        this.shotCounters = shotCounters;
        this.roles = roles;
        this.card = card;
        this.neighbors = neighbors;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.shotCounterList = shotCounterList;
    }

    public ArrayList<ImageIcon> getShotCounterList() {
        return shotCounterList;
    }

    public void setShotCounterList(ArrayList<ImageIcon> shotCounterList) {
        this.shotCounterList = shotCounterList;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int getShotCounters() {
        return shotCounters;
    }

    public void setShotCounters(int shotCounters) {
        this.shotCounters = shotCounters;
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    public Card getCard() {
        return card;
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public HashMap<String, Integer> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(HashMap<String, Integer> neighbors) {
        this.neighbors = neighbors;
    }

    public void decrementShotCounters(){
        --shotCounters;
    }

    public ArrayList<Role> getAvailableRoles(){
        //TODO: add roles on card to this
        ArrayList<Role> list = new ArrayList<Role>();

        for(Role curRole: roles){

            if(!curRole.isTaken()){
                list.add(curRole);
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return "Location{" +
                "isVisited=" + isVisited +
                ", name='" + name + '\'' +
                ", players=" + Arrays.toString(players) +
                ", shotCounters=" + shotCounters +
                ", roles=" + roles +
                ", card=" + card +
                ", neighbors=" + neighbors +
                '}';
    }
}
