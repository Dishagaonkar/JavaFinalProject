package monopoly2;

import java.util.Random;

import java.util.*;

import javax.swing.JOptionPane;

public class GameController {
    private MonopolyBoard board;
    private List<Player> players;
    private int currentPlayerIndex;
    private Random rand;

    public GameController() {
        board = new MonopolyBoard();
        players = new ArrayList<>();
        players.add(new Player("Player 1"));
        players.add(new Player("Player 2"));
        players.add(new Player("Player 3"));
        players.add(new Player("Player 4"));
        currentPlayerIndex = 0;
        rand = new Random();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public MonopolyBoard getBoard() {
        return board;
    }

    public String takeTurn() {
        Player player = getCurrentPlayer();
        int roll = rand.nextInt(6) + 1;
        player.move(roll, board.getBoard().size());

        BoardSpace space = board.getBoard().get(player.getPosition());
        String event = handleEvent(player, space);

        String status = player.getName() + " rolled a " + roll + " and landed on " + space.getName() + ".\n" + event;

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

        return status;
    }

    private String handleEvent(Player player, BoardSpace space) {
        if (space instanceof Property prop) {
            if (!prop.isOwned()) {
                if (player.getMoney() >= prop.getRent()) {
                    int choice = JOptionPane.showConfirmDialog(null,
                            player.getName() + ": Buy " + prop.getName() + " for $" + prop.getRent() + "?",
                            "Buy Property", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        player.adjustMoney(-prop.getRent());
                        player.buyProperty(prop);
                        return player.getName() + " bought " + prop.getName() + " for $" + prop.getRent();
                    } else {
                        return player.getName() + " declined to buy " + prop.getName();
                    }
                } else {
                    return player.getName() + " cannot afford " + prop.getName();
                }
            } else if (prop.getOwner() != player) {
                player.adjustMoney(-prop.getRent());
                prop.getOwner().adjustMoney(prop.getRent());
                return player.getName() + " paid $" + prop.getRent() + " rent to " + prop.getOwner().getName();
            } else {
                return "Landed on your own property.";
            }
        } else if (space instanceof Chance) {
            int change = (rand.nextBoolean() ? 1 : -1) * (rand.nextInt(100) + 1);
            player.adjustMoney(change);
            return "Chance card: " + (change >= 0 ? "Gained $" : "Lost $") + Math.abs(change);
        } else if (space instanceof CommunityChest) {
            int bonus = rand.nextInt(201) - 100;
            player.adjustMoney(bonus);
            return "Community Chest: " + (bonus >= 0 ? "Gained $" : "Lost $") + Math.abs(bonus);
        } else if (space instanceof Go) {
            player.adjustMoney(200);
            return "Collected $200 for landing on GO!";
        } else if (space instanceof Tax tax) {
            player.adjustMoney(-tax.getAmount());
            return "Paid tax: $" + tax.getAmount();
        } else if (space instanceof GoToJail) {
            player.move(10 - player.getPosition(), board.getBoard().size());
            return "Go To Jail! Moving to Jail.";
        }

        return "Nothing happened.";
    }
}
