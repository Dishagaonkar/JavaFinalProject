package monopoly.model;

import java.io.Serializable; import java.util.*;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;  private int pos=0;  private int money=1500;
    private final Set<Property> owned = new HashSet<>();

    public Player(String name){ this.name=name; }
    public void move(int steps,int boardSize){ pos=(pos+steps)%boardSize; }
    public void adjustMoney(int amt){ money+=amt; }
    public void buyProperty(Property p){ owned.add(p); p.setOwner(this);}    
    public String getName(){ return name; }
    public int getPosition(){ return pos; }
    public int getMoney(){ return money; }
    @Override public String toString(){ return name+" ($"+money+")"; }
}