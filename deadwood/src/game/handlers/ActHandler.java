package game.handlers;

import board.Board;
import board.Location;
import board.Role;
import user.Player;

import java.util.*;

public class ActHandler {

    private Board board = Board.getInstance();
    private Scanner scanner = new Scanner(System.in);

    public ActHandler() {

    }

    public void startRole(Location location, Player curPlayer) {
        //TODO: assign role to player
        //TODO: remove from available roles, add as player's role, some more

        System.out.println("Have you already taken a role?");
        String yesNo = scanner.nextLine();
        ArrayList<Role> allRoles = location.getRoles();
        ArrayList<Role> cardRoles = location.getCard().getRoles();
        allRoles.addAll(cardRoles);

        if(yesNo.toLowerCase().equals("no")) {
            System.out.println("Pick a role:");


            //print all available roles
            for (Role curRole : allRoles) {
                if (!curRole.isTaken()) {
                    System.out.println(curRole.getRoleName() + ",\tDifficulty: " + curRole.getDifficulty());
                }
            }
            System.out.println();

            String rolePick = "";
            //iterate through roles, checking if a role matches input

            boolean pickMatch = false;
            boolean lowRank = false;
            Role newRole = null;
            while(!pickMatch) {
                lowRank = false;
                rolePick = scanner.nextLine();
                for (Role curRole : allRoles) {
                    if (rolePick.equalsIgnoreCase(curRole.getRoleName()) && curPlayer.getRank() >= curRole.getDifficulty()) {
                        newRole = curRole;
                        curPlayer.setRole(newRole);
                        newRole.setPlayer(curPlayer);
                        System.out.println("You have successfully taken '" + curRole.getRoleName() + "'.");
                        pickMatch = true;
                        break;
                    } else if (rolePick.equalsIgnoreCase(curRole.getRoleName()) && curPlayer.getRank() < curRole.getDifficulty()) {
                        System.out.println("Rank too low. Try again.");
                        lowRank = true;
                        break;
                    }
                } //TODO: redesign so we have all roles in one place, populated
                if(!pickMatch && !lowRank){System.out.println("Role not found. Please try again.");}
            }
            actOrRehearse(location, curPlayer);
        }
        else{
            actOrRehearse(location, curPlayer);
        }
    }

    public void actOrRehearse(Location location, Player curPlayer) {
        curPlayer.setActing(true);
        System.out.println("Would you like to act or rehearse?");
        String actRehearseChoice = scanner.nextLine();
        if (actRehearseChoice.toLowerCase().equals("rehearse")) {
            rehearse(curPlayer);
        } else if (actRehearseChoice.toLowerCase().equals("act")) {
            act(curPlayer,curPlayer.getRole());
        } else {
            System.out.println("Invalid input. Please enter 'act' or 'rehearse'.");
            actOrRehearse(location, curPlayer);
        }
    }

    public void rehearse(Player curPlayer) {
        curPlayer.getDie().incrementRehearseCredits();
        System.out.println("Rehearse success. You have "+curPlayer.getDie().getRehearseCredits()+" rehearse credits.");
    }

    //returns TRUE if shot counters are used up, FALSE if there are more shots to do
    public boolean act(Player curPlayer, Role curRole) {

        boolean rollSuccess = curPlayer.getDie().sceneRoll(curPlayer.getLocation().getCard().getBudget());
        System.out.println("Acting "+curRole.getRoleName());

        if (rollSuccess) {
            curPlayer.getLocation().decrementShotCounters();
            if (curRole.isStarringRole()) {
                System.out.println("Success! You get $2.");
                curPlayer.changeCredits(2);
                System.out.println("Shot counters left: "+curRole.getMovie().getShotCounters());
            } else {
                System.out.println("Success! You get $1 and 1 credit.");
                curPlayer.changeCredits(1);
                curPlayer.changeDollars(1);
                System.out.println("Shot counters left: "+curRole.getMovie().getShotCounters());
            }
        } else {
            if (!(curRole.isStarringRole())) {
                System.out.println("Failure! You get $1.");
                curPlayer.changeDollars(1);
                System.out.println("Shot counters left: "+curRole.getMovie().getShotCounters());
            } else {
                System.out.println("Failure! You get $0 and 0 credits.");
                System.out.println("Shot counters left: "+curRole.getMovie().getShotCounters());
            }
        }

        if(curRole.getMovie().getShotCounters() == 0){ //TODO: could go straight to payout instead
            ArrayList<Role> allRoles = curPlayer.getLocation().getRoles();
            ArrayList<Role> cardRoles = curPlayer.getLocation().getCard().getRoles();
            allRoles.addAll(cardRoles);
            ArrayList<Player> playersWorkingMovie = new ArrayList<>();

            //print all available roles
            for (Role r : allRoles) {
                if (r.isTaken()) {
                    playersWorkingMovie.add(r.getPlayer());
                    //System.out.println(r.getRoleName() + ",\tDifficulty: " + r.getDifficulty());
                }
            }
            //payout(playersWorkingMovie);
            return true;
        }else{
            return false;
        }
    }

    //TODO: fix payout
    public void payout(ArrayList<Player> players) {

        if(players.isEmpty()){
            System.out.println("No players to pay");
            return;
        }
        //separate players into 2 arrays based on if on or off the card for the movie
        ArrayList<Player> playersOnCard = new ArrayList<Player>();
        ArrayList<Player> playersOffCard = new ArrayList<Player>();

        for (Player p : players) {
            p.setActing(false);
            if (p.getRole().isStarringRole()) {
                playersOnCard.add(p);
            } else {
                playersOffCard.add(p);
            }
        }

        int budget = players.get(0).getLocation().getCard().getBudget();
        Integer[] rolls = new Integer[budget];

        //creates array of die rolls and sorts from greatest to least
        //Integer[] rolls = new Integer[curRole.getMovie().getCard().getBudget()]; //TODO: <-- fix (above error)
        for (int i = 0; i < budget; i++) {
            rolls[i] = players.get(0).getDie().roll();
        }

        //sort our list of die rolls and our list of players
        Arrays.sort(rolls, Collections.reverseOrder());
        Queue<Integer> rollq = new PriorityQueue<>();

        for(int i = 0; i < rolls.length; i++){
            rollq.add(rolls[i]);
        }

        Collections.sort(playersOnCard);
        //Collections.reverseOrder(); // might need this


        if(!playersOnCard.isEmpty()) {
            while(!rollq.isEmpty()) {

                int i = 0;
                for (Player p : playersOnCard) {
                    p.changeDollars(rollq.poll());
                    i++;
                }
            }
        }

        if(!playersOffCard.isEmpty() && !playersOnCard.isEmpty()){

            for(Player p : playersOffCard){
                p.changeDollars(p.getRole().getDifficulty());
            }
        }
        for (Player p : players){
            System.out.println("Player "+p.getName()+" now has "+p.getCredits()+" credits and "+p.getDollars()+" dollars.");
        }
        players.get(0).getLocation().getCard().setWrapped(true);
    }

    public void isRankHighEnough(Player player, int difficulty){
        //TODO: implement
    }
}