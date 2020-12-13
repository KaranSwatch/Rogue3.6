package rogue;

public class Food extends Item implements Edible {
    /**
     *@return description to be printed once eaten
     */
    public String eat() {
        return super.getDescription();
    }

    /**
     *@return description to be printed once eaten
     */
    public String getDesc() {
        return super.getDescription();
    }
}
