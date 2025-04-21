package monopoly2;

import java.util.HashSet;
import java.util.Set;

public class Player {
    private String name;
    private int position;
    private int money;
    private Set<Property> owned;

    public Player(String name) {
        this.name = name;
        this.position = 0;
        this.money = 1500;
        this.owned = new HashSet<>();
    }

    public void move(int steps, int boardSize) {
        position = (position + steps) % boardSize;
    }

    public void adjustMoney(int amount) {
        money += amount;
    }

    public void buyProperty(Property p) {
        owned.add(p);
        p.setOwner(this);
    }

    public boolean owns(Property p) {
        return owned.contains(p);
    }

    public String getName() { return name; }
    public int getPosition() { return position; }
    public int getMoney() { return money; }

    @Override
    public String toString() {
        return name + " ($" + money + ")";
    }
}


