package rogue;

public class Clothing extends Item implements Wearable {
    /**
     *@return description to be printed once worn
     */
    public String wear() {
        return super.getDescription();
    }
}
