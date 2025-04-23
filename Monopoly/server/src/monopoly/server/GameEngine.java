package monopoly.server;

import monopoly.model.*; import java.util.*;

public class GameEngine {
    private final MonopolyBoard board = new MonopolyBoard();
    private final List<Player> players = new ArrayList<>();
    private int turn=0; private final Random rnd=new Random();
    private String last="Server ready.";

    public synchronized Player addPlayer(String name){ Player p=new Player(name); players.add(p); return p; }

    public synchronized void rollDice(Player p){ if(!p.equals(players.get(turn))) return;
        int r=rnd.nextInt(6)+1; p.move(r,board.getBoard().size());
        BoardSpace s=board.getBoard().get(p.getPosition()); last=handle(p,s,r); turn=(turn+1)%players.size(); }

    private String handle(Player pl,BoardSpace sp,int roll){ // trimmed for brevity
        if(sp instanceof Property prop){ if(!prop.isOwned()){ if(pl.getMoney()>=prop.getRent()){ pl.adjustMoney(-prop.getRent()); pl.buyProperty(prop); return pl.getName()+" bought "+prop.getName(); } }
            else if(prop.getOwner()!=pl){ pl.adjustMoney(-prop.getRent()); prop.getOwner().adjustMoney(prop.getRent()); return pl.getName()+" paid rent to "+prop.getOwner().getName(); }}
        return pl.getName()+" rolled "+roll+" landed on "+sp.getName(); }

    public synchronized MonopolyBoard board(){ return board; }
    public synchronized List<Player> players(){return players;}
    public synchronized String last(){return last;}
}