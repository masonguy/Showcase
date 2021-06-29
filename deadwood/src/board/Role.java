package board;

import user.Player;

public class Role {

    private Location movie;
    private String roleName;
    private int difficulty, height, width, xPosition, yPosition;
    private boolean isStarringRole;
    private boolean isTaken;
    private String line;
    private Player player;

    public Role(Location movie, String roleName, int difficulty, boolean isStarringRole, boolean isTaken, String line, int xPosition, int yPosition, int height, int width, Player player) {
        this.movie = movie;
        this.roleName = roleName;
        this.difficulty = difficulty;
        this.isStarringRole = isStarringRole;
        this.isTaken = isTaken;
        this.line = line;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.height = height;
        this.width = width;
        this.player = player;
    }

    public Location getMovie() {
        return movie;
    }

    public void setMovie(Location movie) {
        this.movie = movie;
    }

    public boolean isStarringRole() {
        return isStarringRole;
    }

    public void setStarringRole(boolean starringRole) {
        isStarringRole = starringRole;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void setTaken(boolean taken) {
        isTaken = taken;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
