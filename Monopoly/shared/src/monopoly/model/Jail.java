package monopoly.model;



public class Jail extends BoardSpace {
    public Jail() {
        super("Jail / Just Visiting");
    }

    @Override
    public String getType() {
        return "Jail";
    }

}
