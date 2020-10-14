package board;

import user.Player;

import java.util.ArrayList;

public class Card {
    private String name;
    private boolean isFaceUp;
    private Location location;
    private ArrayList<Role> roles;
    private int budget;
    private String description;
    private boolean isWrapped;
    private String imageName;

    public Card(String name, boolean isFaceUp, Location location, ArrayList<Role> roles, int budget, String description, boolean isWrapped, String imageName) {
        this.name = name;
        this.isFaceUp = isFaceUp;
        this.location = location;
        this.roles = roles;
        this.budget = budget;
        this.description = description;
        this.isWrapped = isWrapped;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public void setFaceUp(boolean faceUp) {
        isFaceUp = faceUp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<Role> roles) {
        this.roles = roles;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isWrapped() {
        return isWrapped;
    }

    public void setWrapped(boolean wrapped) {
        isWrapped = wrapped;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
