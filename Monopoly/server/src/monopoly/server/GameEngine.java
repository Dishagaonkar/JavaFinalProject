package monopoly.server;

import monopoly.model.*;

import java.util.*;

public class GameEngine {

    /* ───────── immutable board & RNG ───────── */
    private final MonopolyBoard board   = new MonopolyBoard();
    private final List<Player>  players = new ArrayList<>();
    private final Random        rnd     = new Random();

    /* ───────── mutable game state ───────── */
    private int    turn = 0;                 // 0-based index into players
    private String last = "Server ready.";   // last log/event line

    /* -------- add a new player -------- */
    public synchronized Player addPlayer(String name) {
        Player p = new Player(name);
        players.add(p);
        return p;
    }

    /* -------- roll dice (only if p is current) -------- */
    public synchronized void rollDice(Player p) {
        if (!p.equals(players.get(turn))) return;      // ignore out-of-turn rolls

        int r = rnd.nextInt(6) + 1;
        p.move(r, board.getBoard().size());

        BoardSpace sq = board.getBoard().get(p.getPosition());
        last = handleEvent(p, sq, r);

        turn = (turn + 1) % players.size();
    }

    /* -------- buy property request -------- */
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

    /* -------- resolve landing event -------- */
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
        return pl.getName() + " rolled " + roll +
               " and landed on " + sq.getName();
    }

    /* -------- accessors used by ClientHandler -------- */
    public synchronized MonopolyBoard board()      { return board; }
    public synchronized List<Player>  players()    { return players; }
    public synchronized String        lastEvent()  { return last; }
    public synchronized int           currentTurn(){ return turn; }
}
