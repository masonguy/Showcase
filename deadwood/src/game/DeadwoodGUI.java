package game;

import board.Board;
import board.Card;
import board.Location;
import board.Role;
import controller.*;
import data.JTextFieldLimit;
import game.handlers.ActHandler;
import user.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.swing.*;
import javax.swing.ImageIcon;
import javax.swing.border.Border;

public class DeadwoodGUI extends JFrame {
    private JLabel gameBoard;
    private JLabel enterPlayersText;
    private JLabel choosePlayerText;
    private JLabel redLabel;
    private JLabel bluelabel;
    private JLabel purplelabel;
    private JLabel activePlayerLabel;
    private JLabel activePlayerImage;
    private JLabel playerOneLabel;
    private JLabel playerTwoLabel;
    private JLabel playerThreeLabel;
    private JLabel playerOneInfo;
    private JLabel playerTwoInfo;
    private JLabel playerThreeInfo;

    private ArrayList<JLabel> cardbacks;

    private JLabel currCard;
    private ImageIcon cardImage;
    private JTextArea console;


    private JTextField player1NameField;
    private JTextField player2NameField;
    private JTextField player3NameField;

    private JTextField[] playerTextFields = {player1NameField, player2NameField, player3NameField};

    private JButton enterPlayers;
    private JButton enterPlayersNames;
    private JButton moveButton;
    private JButton actButton;
    private JButton rehearseButton;
    private JButton upgradeButton;
    private JButton endTurnButton;
    private JButton moveButtonOption1;
    private JButton moveButtonOption2;
    private JButton moveButtonOption3;
    private JButton moveButtonOption4;
    private JButton rank2Button;
    private JButton rank3Button;
    private JButton rank4Button;
    private JButton rank5Button;
    private JButton rank6Button;
    private JButton creditButton;
    private JButton dollarButton;

    private JLayeredPane deadwoodPane;

    private Controller cont;

    private JOptionPane cantUpgradePane;

    private ImageIcon boardImage;
    private ImageIcon redDice;
    private ImageIcon blueDice;
    private ImageIcon purpleDice;
    private ImageIcon activeImage;

    private ImageIcon shotCounter;

    private JComboBox numPlayerBox;

    private static final String DEADWOOD_TITLE = "Deadwood";

    private int numPlayers;

    private String playerOptions[] = {"1", "2", "3"};

    public DeadwoodGUI() {
        super(DEADWOOD_TITLE);
        cont = Controller.getInstance();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initializeLabels();
        initializeDeadwoodPane();
        initializeConsole();
    }

    private void initializeLabels() {
        setUpGameBoardLabel();
        setUpEnterInitialsLabels();
    }

    private void initializeConsole() { //This is totally optional, just something we could maybe work with
        JTextArea jt = new JTextArea("10, 10");
        JPanel p = new JPanel();
        p.add(jt);
        console = jt;
        console.setEditable(false);
        console.setBounds(4, 800, 500, 100);
        Border border = BorderFactory.createTitledBorder("MESSAGES");
        console.setBorder(border);
        deadwoodPane.add(jt, 1);
    }

    public void notifyNewDay(int day) {
        cantUpgradePane = new JOptionPane();
        cantUpgradePane.showMessageDialog(this,"A new day has started! Day: " + day);
    }

    public void postWinner(Player winner) {
        cantUpgradePane = new JOptionPane();
        int points = winner.getCredits() + winner.getDollars() + (winner.getRank()*5);
        cantUpgradePane.showMessageDialog(this,"Congrats to " + winner.getName() + " they win with " + points + " points!");
        gameBoard.setVisible(false);
        closeTurnOptions();
        endTurnButton.setVisible(false);
    }

    public void setToTrailer() {
        if (numPlayers == 1) {
            playerOneLabel.setLocation(991, 248);
            playerOneLabel.setVisible(true);
        } else if (numPlayers == 2) {
            playerOneLabel.setLocation(991, 248);
            playerTwoLabel.setLocation(991, 278);
            playerOneLabel.setVisible(true);
            playerTwoLabel.setVisible(true);
        } else {
            playerOneLabel.setLocation(991, 248);
            playerTwoLabel.setLocation(991, 278);
            playerThreeLabel.setLocation(991, 308);
        }
    }

    public void displayActivePlayer(Player player) {
        if (player.getDie().getColor().equals("blue")) {
            activePlayerImage = new JLabel();
            activePlayerImage.setIcon(blueDice);
            activePlayerImage.setBounds(boardImage.getIconWidth() + 10, 10 + blueDice.getIconHeight(), blueDice.getIconWidth(), blueDice.getIconHeight());

            activePlayerLabel = new JLabel(player.getName());
            activePlayerLabel.setBounds(boardImage.getIconWidth() + 50, 10 + blueDice.getIconHeight(), blueDice.getIconWidth() + 70, blueDice.getIconHeight());

            deadwoodPane.add(activePlayerImage, 1);
            deadwoodPane.add(activePlayerLabel, 1);
        } else if (player.getDie().getColor().equals("red")) {
            activePlayerImage = new JLabel();
            activePlayerImage.setIcon(redDice);
            activePlayerLabel = new JLabel(player.getName());

            activePlayerImage.setBounds(boardImage.getIconWidth() + 10, 10 + blueDice.getIconHeight(), blueDice.getIconWidth(), blueDice.getIconHeight());
            activePlayerLabel = new JLabel(player.getName());
            activePlayerLabel.setBounds(boardImage.getIconWidth() + 50, 10 + blueDice.getIconHeight(), blueDice.getIconWidth() + 70, blueDice.getIconHeight());
            deadwoodPane.add(activePlayerImage, 1);
            deadwoodPane.add(activePlayerLabel, 1);
        } else if (player.getDie().getColor().equals("purple")) {
            activePlayerImage = new JLabel();
            activePlayerImage.setIcon(purpleDice);
            activePlayerLabel = new JLabel(player.getName());

            activePlayerImage.setBounds(boardImage.getIconWidth() + 10, 10 + blueDice.getIconHeight(), blueDice.getIconWidth(), blueDice.getIconHeight());
            activePlayerLabel = new JLabel(player.getName());
            activePlayerLabel.setBounds(boardImage.getIconWidth() + 50, 10 + blueDice.getIconHeight(), blueDice.getIconWidth() + 70, blueDice.getIconHeight());

            deadwoodPane.add(activePlayerImage, 1);
            deadwoodPane.add(activePlayerLabel, 1);
        }
    }


    public void removeActivePlayer() {
        activePlayerImage.setVisible(false);
        activePlayerLabel.setVisible(false);
    }

    private void setUpGameBoardLabel() {
        gameBoard = new JLabel();
        boardImage = new ImageIcon("src/resources/board.jpg");
        gameBoard.setIcon(boardImage);
        gameBoard.setBounds(0, 0, boardImage.getIconWidth(), boardImage.getIconHeight());
        setSize(boardImage.getIconWidth() + 340, boardImage.getIconHeight() + 50);

    }

    private void setUpEnterInitialsLabels() {
        enterPlayersText = new JLabel("Choose number of players");
        enterPlayersText.setBounds(boardImage.getIconWidth() + 50, 100, 200, 100);


        numPlayerBox = new JComboBox(playerOptions);
        numPlayerBox.setBounds(boardImage.getIconWidth() + 110, 160, 50, 20);

        enterPlayers = new JButton("Proceed");
        enterPlayers.setBounds(boardImage.getIconWidth() + 90, 190, 100, 30);
        enterPlayers.addMouseListener(new StartMouseListener());

        choosePlayerText = new JLabel("Enter Player Name(s)");
        choosePlayerText.setBounds(boardImage.getIconWidth() + 70, 100, 200, 100);

        player1NameField = new JTextField(10);
        player1NameField.setDocument(new JTextFieldLimit(9));
        player1NameField.setBounds(boardImage.getIconWidth() + 90, 190, 100, 30);

        player2NameField = new JTextField(10);
        player2NameField.setDocument(new JTextFieldLimit(9));
        player2NameField.setBounds(boardImage.getIconWidth() + 90, 230, 100, 30);

        player3NameField = new JTextField(10);
        player3NameField.setDocument(new JTextFieldLimit(9));
        player3NameField.setBounds(boardImage.getIconWidth() + 90, 270, 100, 30);

        enterPlayersNames = new JButton("Proceed");
        enterPlayersNames.setBounds(boardImage.getIconWidth() + 90, 330, 100, 30);
        enterPlayersNames.addMouseListener(new EnterNameMouseListener());
    }

    public void setUpVisible() {
        numPlayers = Integer.parseInt(playerOptions[numPlayerBox.getSelectedIndex()]);
        enterPlayers.setVisible(false);
        numPlayerBox.setVisible(false);
        enterPlayersText.setVisible(false);

        deadwoodPane.add(choosePlayerText, 0);
        if (numPlayers == 1) {
            deadwoodPane.add(player1NameField, 0);
        } else if (numPlayers == 2) {
            deadwoodPane.add(player1NameField, 0);
            deadwoodPane.add(player2NameField, 0);
        } else if (numPlayers == 3) {
            deadwoodPane.add(player1NameField, 0);
            deadwoodPane.add(player2NameField, 0);
            deadwoodPane.add(player3NameField, 0);
        }
        deadwoodPane.add(enterPlayersNames, 0);
    }

    public void initTurnOptions() {
        moveButton = new JButton("Move");
        moveButton.setBounds(boardImage.getIconWidth() + 90, 100, 100, 30);
        moveButton.addMouseListener(new MoveButtonMouseListener());

        actButton = new JButton("Act");
        actButton.setBounds(boardImage.getIconWidth() + 90, 140, 100, 30);
        actButton.addMouseListener(new ActButtonMouseListener());

        rehearseButton = new JButton("Rehearse");
        rehearseButton.setBounds(boardImage.getIconWidth() + 90, 180, 100, 30);
        rehearseButton.addMouseListener(new RehearseButtonMouseListener());

        upgradeButton = new JButton("Upgrade");
        upgradeButton.setBounds(boardImage.getIconWidth() + 90, 220, 100, 30);
        upgradeButton.addMouseListener(new UpgradeButtonMouseListener());

        endTurnButton = new JButton("End Turn");
        endTurnButton.setBounds(boardImage.getIconWidth() + 90, 300, 100, 30);
        endTurnButton.addMouseListener(new EndTurnMouseListener());

        deadwoodPane.add(endTurnButton, 1);
        deadwoodPane.add(moveButton, 1);
        deadwoodPane.add(actButton, 1);
        deadwoodPane.add(rehearseButton, 1);
        deadwoodPane.add(upgradeButton, 1);
    }

    public void removePlayerInfo() {
        playerOneInfo.setVisible(false);
        if(numPlayers == 3) {
            playerTwoInfo.setVisible(false);
            playerThreeInfo.setVisible(false);
        } else if (numPlayers == 2) {
            playerTwoInfo.setVisible(false);
        }
    }

    public void updateRankImage(int rank, String color) {
        String input = "src/resources/dice/" + color.charAt(0) + rank + ".png";
        if(color.equalsIgnoreCase("red")) {
            redDice = new ImageIcon(input);
            playerTwoLabel.setIcon(redDice);
            redLabel.setIcon(redDice);
        } else if (color.equalsIgnoreCase("blue")) {
            blueDice = new ImageIcon(input);
            playerOneLabel.setIcon(blueDice);
            bluelabel.setIcon(blueDice);
        } else if (color.equalsIgnoreCase("purple")) {
            input = "src/resources/dice/v" + rank + ".png";
            purpleDice = new ImageIcon(input);
            playerThreeLabel.setIcon(purpleDice);
            purplelabel.setIcon(purpleDice);
        }
    }

    public void cantUpgradeFunds() {
        actButton.setVisible(false);
        cantUpgradePane = new JOptionPane();
        cantUpgradePane.showMessageDialog(this,"You don't have enough dollars or credits!");

    }

    public void cantUpgrade() {
        cantUpgradePane = new JOptionPane();
        cantUpgradePane.showMessageDialog(this,"You cant upgrade here");
    }

    public void initUpgradePane(int rank) {
        closeTurnOptions();
        actButton.setVisible(false);
        rank2Button = new JButton("Rank 2");
        rank2Button.setBounds(boardImage.getIconWidth() + 90, 100, 100, 30);
        rank2Button.addMouseListener(new UpgradeRankMouseListener(2));

        rank3Button = new JButton("Rank 3");
        rank3Button.setBounds(boardImage.getIconWidth() + 90, 140, 100, 30);
        rank3Button.addMouseListener(new UpgradeRankMouseListener(3));

        rank4Button = new JButton("Rank 4");
        rank4Button.setBounds(boardImage.getIconWidth() + 90, 180, 100, 30);
        rank4Button.addMouseListener(new UpgradeRankMouseListener(4));

        rank5Button = new JButton("Rank 5");
        rank5Button.setBounds(boardImage.getIconWidth() + 90, 220, 100, 30);
        rank5Button.addMouseListener(new UpgradeRankMouseListener(5));

        rank6Button = new JButton("Rank 6");
        rank6Button.setBounds(boardImage.getIconWidth() + 90, 260, 100, 30);
        rank6Button.addMouseListener(new UpgradeRankMouseListener(6));

        if(rank == 6) {

        } else if (rank == 5) {
            deadwoodPane.add(rank6Button, 3);
        } else if (rank == 4) {
            deadwoodPane.add(rank6Button, 3);
            deadwoodPane.add(rank5Button, 3);
        } else if (rank == 3) {
            deadwoodPane.add(rank6Button, 3);
            deadwoodPane.add(rank5Button, 3);
            deadwoodPane.add(rank4Button, 3);
        } else if (rank == 2) {
            deadwoodPane.add(rank6Button, 3);
            deadwoodPane.add(rank5Button, 3);
            deadwoodPane.add(rank4Button, 3);
            deadwoodPane.add(rank3Button, 3);
        } else {
            deadwoodPane.add(rank6Button, 3);
            deadwoodPane.add(rank5Button, 3);
            deadwoodPane.add(rank4Button, 3);
            deadwoodPane.add(rank3Button, 3);
            deadwoodPane.add(rank2Button, 3);

        }

    }

    public void removeUpgradeButtons() {
        rank2Button.setVisible(false);
        rank3Button.setVisible(false);
        rank4Button.setVisible(false);
        rank5Button.setVisible(false);
        rank6Button.setVisible(false);
        actButton.setVisible(false);

    }

    public void closeTurnOptions() {
        moveButton.setVisible(false);
        actButton.setVisible(false);
        rehearseButton.setVisible(false);
        upgradeButton.setVisible(false);
    }

    private void initializeDeadwoodPane() {
        deadwoodPane = getLayeredPane();
        deadwoodPane.add(gameBoard, 0);
        deadwoodPane.add(enterPlayersText, 0);
        deadwoodPane.add(numPlayerBox, 0);
        deadwoodPane.add(enterPlayers, 0);

    }

    public void initPlayerDisplay() {
        player1NameField.setVisible(false);
        player2NameField.setVisible(false);
        player3NameField.setVisible(false);
        enterPlayersNames.setVisible(false);
        choosePlayerText.setVisible(false);

        if(blueDice == null) {
            blueDice = new ImageIcon("src/resources/dice/b1.png");
        }
        bluelabel = new JLabel();
        bluelabel.setIcon(blueDice);
        bluelabel.setBounds(boardImage.getIconWidth() + 10, 700, blueDice.getIconWidth(), blueDice.getIconHeight());
        deadwoodPane.add(bluelabel, 1);

        if(redDice == null) {
            redDice = new ImageIcon("src/resources/dice/r1.png");
        }
        redLabel = new JLabel();
        redLabel.setIcon(redDice);
        redLabel.setBounds(boardImage.getIconWidth() + 10, 710 + blueDice.getIconHeight(), redDice.getIconWidth(), redDice.getIconHeight());
        if (numPlayers >= 2) {
            deadwoodPane.add(redLabel, 1);
        }

        if(purpleDice == null) {
            purpleDice = new ImageIcon("src/resources/dice/v1.png");
        }
        purplelabel = new JLabel();
        purplelabel.setIcon(purpleDice);
        purplelabel.setBounds(boardImage.getIconWidth() + 10, 720 + purpleDice.getIconHeight() * 2, purpleDice.getIconWidth(), purpleDice.getIconHeight());
        if (numPlayers == 3) {
            deadwoodPane.add(purplelabel, 1);
        }

        initPlayerLabels();
        if (numPlayers == 1) {
            playerOneLabel.setLocation(991, 248);
            playerOneLabel.setVisible(true);
        } else if (numPlayers == 2) {
            playerOneLabel.setLocation(991, 248);
            playerTwoLabel.setLocation(991, 278);
            playerOneLabel.setVisible(true);
            playerTwoLabel.setVisible(true);
        } else {
            playerOneLabel.setLocation(991, 248);
            playerTwoLabel.setLocation(991, 278);
            playerThreeLabel.setLocation(991, 308);
            playerOneLabel.setVisible(true);
            playerTwoLabel.setVisible(true);
            playerThreeLabel.setVisible(true);
        }
    }

    public void playerDisplay() {

    }

    public JComboBox getNumPlayerBox() {
        return numPlayerBox;
    }

    public String[] getPlayerNames() {
        String[] arr = new String[numPlayers];
        if (numPlayers == 1) {
            arr[0] = player1NameField.getText();
        } else if (numPlayers == 2) {
            arr[0] = player1NameField.getText();
            arr[1] = player2NameField.getText();
        } else if (numPlayers == 3) {
            arr[0] = player1NameField.getText();
            arr[1] = player2NameField.getText();
            arr[2] = player3NameField.getText();
        }
        return arr;
    }
    public void removeMoveOptions() {
        moveButtonOption1.setVisible(false);
        moveButtonOption2.setVisible(false);
        moveButtonOption3.setVisible(false);
        if(moveButtonOption4 != null) {
            moveButtonOption4.setVisible(false);
        }
        moveButton.setVisible(false);
        rehearseButton.setVisible(false);
        upgradeButton.setVisible(false);
    }

    public void displayMoveOptions(HashMap<String, Integer> map) {
        closeTurnOptions();
        Set<String> s = map.keySet();
        int n = s.size();
        String arr[] = new String[n];
        int i = 0;
        for (String str : s) {
            arr[i++] = str;
        }
        if (map.size() == 3) {
            moveButtonOption1 = new JButton(arr[0]);
            moveButtonOption2 = new JButton(arr[1]);
            moveButtonOption3 = new JButton(arr[2]);
        } else {
            moveButtonOption1 = new JButton(arr[0]);
            moveButtonOption2 = new JButton(arr[1]);
            moveButtonOption3 = new JButton(arr[2]);
            moveButtonOption4 = new JButton(arr[3]);
        }

        moveButtonOption1.setBounds(boardImage.getIconWidth() + 65, 100, 150, 30);
        moveButtonOption1.addMouseListener(new MoveLocationMouseListener(arr[0]));

        moveButtonOption2.setBounds(boardImage.getIconWidth() + 65, 140, 150, 30);
        moveButtonOption2.addMouseListener(new MoveLocationMouseListener(arr[1]));

        moveButtonOption3.setBounds(boardImage.getIconWidth() + 65, 180, 150, 30);
        moveButtonOption3.addMouseListener(new MoveLocationMouseListener(arr[2]));
        if (map.size() == 4) {
            moveButtonOption4.setBounds(boardImage.getIconWidth() + 65, 220, 150, 30);
            moveButtonOption4.addMouseListener(new MoveLocationMouseListener(arr[3]));
        }

        if (map.size() == 3) {
            deadwoodPane.add(moveButtonOption1, 1);
            deadwoodPane.add(moveButtonOption2, 1);
            deadwoodPane.add(moveButtonOption3, 1);
        } else {
            deadwoodPane.add(moveButtonOption1, 1);
            deadwoodPane.add(moveButtonOption2, 1);
            deadwoodPane.add(moveButtonOption3, 1);
            deadwoodPane.add(moveButtonOption4, 1);
        }
    }

    public void initPlayerLabels() {
        playerOneLabel = new JLabel();
        playerOneLabel.setIcon(blueDice);
        playerOneLabel.setSize(blueDice.getIconWidth(), blueDice.getIconHeight());
        deadwoodPane.add(playerOneLabel,  JLayeredPane.POPUP_LAYER); //TODO: make sure this is good
        playerOneLabel.setVisible(false);

        playerTwoLabel = new JLabel();
        playerTwoLabel.setIcon(redDice);
        playerTwoLabel.setSize(redDice.getIconWidth(), redDice.getIconHeight());
        deadwoodPane.add(playerTwoLabel,  JLayeredPane.POPUP_LAYER);
        playerTwoLabel.setVisible(false);

        playerThreeLabel = new JLabel();
        playerThreeLabel.setIcon(purpleDice);
        playerThreeLabel.setSize(purpleDice.getIconWidth(), purpleDice.getIconHeight());
        deadwoodPane.add(playerThreeLabel,  JLayeredPane.POPUP_LAYER);
        playerThreeLabel.setVisible(false);
    }

    public void movePlayerImage(int x, int y, int h, int w, Player player) {
        if (player.getDie().getColor().equals("blue")) {
            playerOneLabel.setLocation(x, y);
        } else if (player.getDie().getColor().equals("red")) {
            playerTwoLabel.setLocation(x+20, y);
        } else if (player.getDie().getColor().equals("purple")) {
            playerThreeLabel.setLocation(x+40, y);

        }
    }

    public void cantActHere() {
        actButton.setVisible(false);
        cantUpgradePane = new JOptionPane();
        cantUpgradePane.showMessageDialog(this, "You cant act here!");
    }

    public void chooseUpgradeOption(int rank) {
        moveButton.setVisible(false);
        actButton.setVisible(false);
        rehearseButton.setVisible(false);
        closeTurnOptions();
        removeMoveOptions();
        removeUpgradeButtons();

        creditButton = new JButton("Credits");
        creditButton.setBounds(boardImage.getIconWidth() + 90, 100, 100, 30);
        creditButton.addMouseListener(new UpgradeTypeMouseListener("Credits", rank));

        dollarButton = new JButton("Dollars");
        dollarButton.setBounds(boardImage.getIconWidth() + 90, 140, 100, 30);
        dollarButton.addMouseListener(new UpgradeTypeMouseListener("Dollars", rank));

        deadwoodPane.add(creditButton, 4);
        deadwoodPane.add(dollarButton, 4);

    }

    public void rankSelectionError() {
        actButton.setVisible(false);
        cantUpgradePane = new JOptionPane();
        cantUpgradePane.showMessageDialog(this, "Insufficient Funds!");
    }

    public void hideUpgradeOptions() {
        creditButton.setVisible(false);
        dollarButton.setVisible(false);
    }

    public Role displayActOptions(Player player) {
        Location location = player.getLocation();
        ArrayList<Role> availRoles = location.getRoles();
        for(Role r : location.getCard().getRoles()){
            if(!r.isTaken()) {
                availRoles.add(r);
            }
        }
        String[] roles = new String[availRoles.size() + 1];

        //create array of strings from arraylist
        for (int i = 0; i < availRoles.size(); i++) {
            roles[i] = availRoles.get(i).getRoleName()+", Difficulty: "+availRoles.get(i).getDifficulty();
        }
        roles[availRoles.size()] = "None";

        Role selectedRole = null;
        String input = new String();
        while (input != "None" && (selectedRole == null || selectedRole.getDifficulty() > player.getRank())) {

            input = (String) JOptionPane.showInputDialog(null, "Choose now...",
                    "Available Roles", JOptionPane.QUESTION_MESSAGE, null,
                    roles, // Array of choices
                    roles[0]); // Initial choice
            System.out.println(input);

            for (int i = 0; i < availRoles.size(); i++) {
                if (input.equals( availRoles.get(i).getRoleName()+", Difficulty: "+availRoles.get(i).getDifficulty())) {
                    selectedRole = availRoles.get(i);
                }
            }
        }
        if (input == "None") {
            selectedRole = null;
        }
        player.setActing(true);
        player.setRole(selectedRole);
        selectedRole.setPlayer(player);
        selectedRole.setTaken(true);

        return selectedRole;
    }
    //pass array of objects, with tostring method it will return the object

    public String displayActOrRehearse(Player player, Role role) {
        String[] options = {"Act", "Rehearse", "Nothing"};
        String input = (String) JOptionPane.showInputDialog(null, "Would you like to act or rehearse?",
                "The Choice of a Lifetime", JOptionPane.QUESTION_MESSAGE, null,
                options, // Array of choices
                options[0]); // Initial choice

        if (input == "Act") {
            //handler.act(player,role);
            return "Act";
        } else if (input == "Rehearse") {
            return "Rehearse";
            //handler.rehearse(player);
        } else {
            //TODO: end / change turns here
            return "None";
        }
    }


    public void updatePlayerInfoList(Player[] players) {

        Player bluePlayer = null;
        Player redPlayer = null;
        Player purplePlayer = null;
        for(int i = 0; i < players.length; i++) {
            if(players[i].getDie().getColor().equalsIgnoreCase("red")) {
                redPlayer = players[i];
            } else if (players[i].getDie().getColor().equalsIgnoreCase("blue")) {
                bluePlayer = players[i];
            } else if (players[i].getDie().getColor().equalsIgnoreCase("purple")) {
                purplePlayer = players[i];
            }
        }
        if(playerOneInfo !=null)
            playerOneInfo.removeAll();
        playerOneInfo = new JLabel(bluePlayer.getName() + " | Credits: " + bluePlayer.getCredits() + " Dollars: " + bluePlayer.getDollars());
        playerOneInfo.setBounds(boardImage.getIconWidth() + 55, 700, 300, 40);
        deadwoodPane.add(playerOneInfo,5);
        if(numPlayers == 3) {
            playerTwoInfo = new JLabel(redPlayer.getName() + " | Credits: " + redPlayer.getCredits() + " Dollars: " + redPlayer.getDollars());
            playerTwoInfo.setBounds(boardImage.getIconWidth() + 55, 710 + blueDice.getIconHeight(), 300, 40);
            deadwoodPane.add(playerTwoInfo, 5);

            playerThreeInfo = new JLabel(purplePlayer.getName() + " | Credits: " + purplePlayer.getCredits() + " Dollars: " + purplePlayer.getDollars());
            playerThreeInfo.setBounds(boardImage.getIconWidth() + 55, 720 + blueDice.getIconHeight()*2, 300, 40);
            deadwoodPane.add(playerThreeInfo, 5);
        }
        else if(numPlayers == 2) {
            playerTwoInfo = new JLabel(redPlayer.getName() + " | Credits: " + redPlayer.getCredits() + " Dollars: " + redPlayer.getDollars());
            playerTwoInfo.setBounds(boardImage.getIconWidth() + 55, 710 + blueDice.getIconHeight(), 300, 40);
            deadwoodPane.add(playerTwoInfo, 5);
        }

    }

    public void updatePlayerInfo(ArrayList<Player> players, Location activeLoc) {
        String stats = "";
        stats += "Shot counters left at "+activeLoc.getName()+": "+activeLoc.getShotCounters()+"\n";
        for(Player e : players){
            stats += e.getName()+": ";
            stats += "Rank: "+e.getRank()+", ";
            stats += "Dollars: "+e.getDollars()+", ";
            stats += "Credits: "+e.getCredits()+"\n";

        }

        JFrame frame = new JFrame("Player Stats"); //temporary jframe popup for player info
        frame.pack();
        JOptionPane.showMessageDialog(frame,stats,"Player Stats",JOptionPane.INFORMATION_MESSAGE);

    }

    public void displayMoveInvalid(){
        JFrame frame = new JFrame("Player Stats"); //temporary jframe popup for player info
        frame.pack();
        //frame.setVisible(true);111
        JOptionPane.showMessageDialog(frame,"You can't move while you are acting!","Player Stats",JOptionPane.INFORMATION_MESSAGE);

    }

    public void payoutPopup(ArrayList<Player> players){
        String stats = "SCENE WRAPPED!\n\nPayout!\n";
        for(Player e : players){
            stats += e.getName()+": ";
            stats += "Rank: "+e.getRank()+", ";
            stats += "Dollars: "+e.getDollars()+", ";
            stats += "Credits: "+e.getCredits()+"\n";

        }

        JFrame frame = new JFrame("Payout"); //temporary jframe popup for player info
        frame.pack();
        JOptionPane.showMessageDialog(frame,stats,"Player Stats",JOptionPane.INFORMATION_MESSAGE);

    }

    public void updateShotCounters(Location location){
        int shotCounters = location.getShotCounters();

    }

    public void distributeNewCards(ArrayList<Location> locations) {
        ImageIcon cardback = new ImageIcon("src/resources/cards/back.png");
        cardbacks = new ArrayList<>(locations.size());
        int i = 0;
        for(Location loc : locations){
            Card curCard = loc.getCard();
            int x = loc.getX();
            int y = loc.getY();
            JLabel newCardback = new JLabel(cardback);

            if (cardback != null) {
                newCardback.setBounds(x, y,
                        cardback.getIconWidth(),
                        cardback.getIconHeight());
            } else {
            newCardback.setBounds(x, y, 50, 50);
            newCardback.setOpaque(true);
            newCardback.setBackground(Color.BLACK);
            }
            cardbacks.add(newCardback);

        }

        for(int j = 0; j < cardbacks.size()-2; j++){
            deadwoodPane.add(cardbacks.get(j), 1);
        }
    }


        public void placeCard(Location location) {
            ImageIcon cardImage = new ImageIcon("src/resources/cards/" + location.getCard().getImageName());
            int x = location.getX();
            int y = location.getY();
            //Index for corresponding location in cardbacks AList
            //0: train station
            //1: secret hideout
            //2: church
            //3: hotel
            //4: main street
            //5: jail
            //6: general store
            //7: ranch
            //8: bank
            //9: saloon
            //10: trailer
            //11: casting office

            JLabel faceup = new JLabel(cardImage);
            if(location.getName().toLowerCase().equals("train station")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(0,faceup);
                deadwoodPane.add(cardbacks.get(0), 1);

            }else if(location.getName().toLowerCase().equals("secret hideout")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(1,faceup);
                deadwoodPane.add(cardbacks.get(1), 1);

            }else if(location.getName().toLowerCase().equals("church")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(2,faceup);
                deadwoodPane.add(cardbacks.get(2), 1);

            }else if(location.getName().toLowerCase().equals("hotel")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(3,faceup);
                deadwoodPane.add(cardbacks.get(3), 1);

            }else if(location.getName().toLowerCase().equals("main street")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(4,faceup);
                deadwoodPane.add(cardbacks.get(4), 1);

            }else if(location.getName().toLowerCase().equals("jail")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(5,faceup);
                deadwoodPane.add(cardbacks.get(5), 1);

            }else if(location.getName().toLowerCase().equals("general store")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(6,faceup);
                deadwoodPane.add(cardbacks.get(6), 1);

            }else if(location.getName().toLowerCase().equals("ranch")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(7,faceup);
                deadwoodPane.add(cardbacks.get(7), 1);

            }else if(location.getName().toLowerCase().equals("bank")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(8,faceup);
                deadwoodPane.add(cardbacks.get(8), 1);

            }else if(location.getName().toLowerCase().equals("saloon")){
                if (cardImage != null) {
                    faceup.setBounds(x, y,
                            cardImage.getIconWidth(),
                            cardImage.getIconHeight());
                } else {
                    faceup.setBounds(x, y, 50, 50);
                    faceup.setOpaque(true);
                    faceup.setBackground(Color.BLACK);
                }
                cardbacks.set(9,faceup);
                deadwoodPane.add(cardbacks.get(9), 1);

            }
    }

    public void distributeShotCounters(ArrayList<Location> locations){
        createShotCounterIcon();

        for(Location l : locations){
            ArrayList<ImageIcon> shotImages = new ArrayList<>();
            int maxShots = l.getShotCounters();
            for(int i = 0; i < maxShots; i++){
                shotImages.add(shotCounter);
            }
            l.setShotCounterList(shotImages);
            shotImages.clear();
        }
    }

    public void addShotImage(int x, int y){
        JLabel shotLabel = new JLabel(shotCounter);
        shotLabel.setSize(shotCounter.getIconWidth(),shotCounter.getIconHeight()); //todo: change width and height
        shotLabel.setBounds(x,y,shotCounter.getIconWidth(),shotCounter.getIconHeight());
        shotLabel.setVisible(true);
        deadwoodPane.add(shotLabel,3);
        deadwoodPane.setVisible(true);
    }

    public void createShotCounterIcon(){
        shotCounter = new ImageIcon("src/resources/shot.png");
        if(shotCounter == null){
            System.out.println("null shotcounter");
        }
    }

    public Controller getCont() {
        return cont;
    }

    public void setCont(Controller cont) {
        this.cont = cont;
    }

    public void rehearsePopup(Player player, boolean success){
        String stats = "";
        if(success) {
            stats = "Player: " + player.getName() + " has successfully gained one rehearse credit, turn over.";
        }else{
            stats = "You can't rehearse right now.";
        }
        JFrame frame = new JFrame("Rehearse");
        frame.pack();
        JOptionPane.showMessageDialog(frame, stats, "Rehearse", JOptionPane.INFORMATION_MESSAGE);
    }


}