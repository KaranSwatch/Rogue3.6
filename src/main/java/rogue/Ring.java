package rogue;

public class Ring extends Magic implements Wearable {
    /**
     *@return description to be printed once worn
     */
    public String wear() {
        return super.getDesc();
    }
}
