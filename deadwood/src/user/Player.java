package user;

import board.Location;
import board.Role;

public class Player implements Comparable<Player>{
    private Dice die;
    private Role role;
    private Location location;
    private String name;
    private int rank;
    private int credits;
    private int dollars;
    private boolean isActing;

    public Player(Dice die, Role role, Location location, String name, int rank, int credits, int dollars, boolean isActing) {
        this.die = die;
        this.role = role;
        this.location = location;
        this.name = name;
        this.rank = rank;
        this.credits = credits;
        this.dollars = dollars;
        this.isActing = isActing;
    }

    public Dice getDie() {
        return die;
    }

    public void setDie(Dice die) {
        this.die = die;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRank() { return rank; }

    public void setRank(int rank) { this.rank = rank; }

    public void upgrade(int rank){
        this.rank++;
    }

    public void changeCredits(int changeVal){
        this.credits+=changeVal;
    }

    public void changeDollars(int changeVal){
        this.dollars+=changeVal;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getDollars() {
        return dollars;
    }

    public void setDollars(int dollars) {
        this.dollars = dollars;
    }

    public boolean isActing() {
        return isActing;
    }

    public void setActing(boolean acting) {
        isActing = acting;
    }

    //results in a sort in reverse order
    @Override
    public int compareTo(Player player){
        if(this.role.getDifficulty()==player.getRole().getDifficulty()){
            return 0;
        }else if(this.role.getDifficulty()>player.getRole().getDifficulty()){
            return -1;
        }else {
            return 1;
        }
    }
}