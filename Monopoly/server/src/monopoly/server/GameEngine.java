package monopoly.server;

import monopoly.model.*;

import java.util.*;

public class GameEngine {

   
    private final MonopolyBoard board   = new MonopolyBoard();
    private final List<Player>  players = new ArrayList<>();
    private final Random        rnd     = new Random();

    
    private int    turn = 0;                 
    private String last = "Server ready.";  

   
    public synchronized Player addPlayer(String name) {
        Player p = new Player(name);
        players.add(p);
        return p;
    }

    
    public synchronized void rollDice(Player p) {
        if (!p.equals(players.get(turn))) return;      
    
        int r = rnd.nextInt(6) + 1;
        p.move(r, board.getBoard().size());
    
        BoardSpace sq = board.getBoard().get(p.getPosition());
        last = handleEvent(p, sq, r);
    

    }
    
   
    public synchronized void advanceTurn() {
        turn = (turn + 1) % players.size();
    }

   
    public synchronized boolean buyProperty(Player p, int square) {
        BoardSpace sq = board.getBoard().get(square);
        if (!(sq instanceof Property prop))                 return false;
        if (prop.isOwned() || p.getMoney() < prop.getRent()) return false;

        p.adjustMoney(-prop.getRent());
        p.buyProperty(prop);
        last = p.getName() + " bought " + prop.getName() +
               " for $" + prop.getRent();
        return true;
    }

    
        private String handleEvent(Player pl, BoardSpace sq, int roll) {
        if (sq instanceof Property prop) {
            if (!prop.isOwned()) {
                return pl.getName() + " may buy " + prop.getName();
            }
            if (prop.getOwner() != pl) {
                pl.adjustMoney(-prop.getRent());
                prop.getOwner().adjustMoney(prop.getRent());
                return pl.getName() + " paid $" + prop.getRent() +
                       " rent to " + prop.getOwner().getName();
            }
        }

        if (sq instanceof Chance || sq instanceof CommunityChest) {
            String[] cards = {
                "Bank error in your favor. Collect $200.",
                "Doctor's fees. Pay $50.",
                "You have won second prize in a beauty contest. Collect $10.",
                "Pay hospital fees of $100."
            };

            int index = rnd.nextInt(cards.length);
            String card = cards[index];

            switch (index) {
                case 0 -> pl.adjustMoney(200);
                case 1 -> pl.adjustMoney(-50);
                case 2 -> pl.adjustMoney(10);
                case 3 -> pl.adjustMoney(-100);
            }

            last = pl.getName() + " drew a card on " + sq.getName() + ": " + card;
            return last;
        }

        return pl.getName() + " rolled " + roll +
               " and landed on " + sq.getName();
    }

    public synchronized MonopolyBoard board()      { return board; }
    public synchronized List<Player>  players()    { return players; }
    public synchronized String        lastEvent()  { return last; }
    public synchronized int           currentTurn(){ return turn; }
}
