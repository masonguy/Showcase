package game;

import board.Role;
import user.Player;

public class CurrentTurn {
    private Player currPlayer;
    private Player[] players;
    private Role currRole;

    public CurrentTurn(Player currPlayer, Player[] players, Role currRole) {
        this.currPlayer = currPlayer;
        this.players = players;
        this.currRole = currRole;
    }

    public Player getCurrPlayer() {
        return currPlayer;
    }

    public void setCurrPlayer(Player currPlayer) {
        this.currPlayer = currPlayer;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public Role getCurrRole() {
        return currRole;
    }

    public void setCurrRole(Role currRole) {
        this.currRole = currRole;
    }

    public Player changePlayerTurn(Player newPlayer){

        currPlayer = newPlayer;
        return newPlayer;
    }
}
