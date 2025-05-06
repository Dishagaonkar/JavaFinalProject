package monopoly.model;

import java.io.Serializable; import java.util.*;

import java.util.HashSet;
import java.util.Set;

// public class Player implements Serializable {
//     private static final long serialVersionUID = 1L;
//     private final String name;  private int pos=0;  private int money=1500;
//     private final Set<Property> owned = new HashSet<>();

//     public Player(String name){ this.name=name; }
//     public void move(int steps,int boardSize){ pos=(pos+steps)%boardSize; }
//     public void adjustMoney(int amt){ money+=amt; }
//     public void buyProperty(Property p){ owned.add(p); p.setOwner(this);}    
//     public String getName(){ return name; }
//     public int getPosition(){ return pos; }
//     public int getMoney(){ return money; }
//     @Override public String toString(){ return name+" ($"+money+")"; }

    
// }

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private int pos = 0;
    private int money = 1500;
    private final Set<Property> owned = new HashSet<>();

    // 🚔 Jail state
    private boolean inJail = false;
    private int jailTurns = 0;

    // ❌ Bankruptcy
    private boolean bankrupt = false;

    public Player(String name) {
        this.name = name;
    }

    // === Position ===
    public void move(int steps, int boardSize) {
        pos = (pos + steps) % boardSize;
    }

    public void setPosition(int position) {
        this.pos = position;
    }

    public int getPosition() {
        return pos;
    }

    // === Money ===
    public void adjustMoney(int amt) {
        money += amt;
        if (money < 0) bankrupt = true;
    }

    public int getMoney() {
        return money;
    }

    public boolean isBankrupt() {
        return bankrupt;
    }

    // === Jail ===
    public boolean isInJail() {
        return inJail;
    }

    public void sendToJail() {
        inJail = true;
        jailTurns = 0;
        setPosition(10); // jail index
    }

    public void releaseFromJail() {
        inJail = false;
        jailTurns = 0;
    }

    public void incrementJailTurns() {
        jailTurns++;
    }

    public int getJailTurns() {
        return jailTurns;
    }

    // === Properties ===
    public void buyProperty(Property p) {
        owned.add(p);
        p.setOwner(this);
    }

    public Set<Property> getOwnedProperties() {
        return owned;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " ($" + money + ")" + (inJail ? " [in Jail]" : "");
    }
}

