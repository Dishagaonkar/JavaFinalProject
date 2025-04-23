package monopoly.model;

public class Tax extends BoardSpace {
    private int amount;
    public Tax(String name, int amount) {
        super(name);
        this.amount = amount;
    }
    public int getAmount() { return amount; }
    @Override public String getType() { return "Tax"; }
}
