package rogue;

public class Potion extends Magic implements Tossable, Edible {
    /**
     *@return description to be printed once eaten
     */
    public String eat() {
        String desc = super.getDesc();
        String[] arrDesc = desc.split(":");
        if (arrDesc.length != 1) {
            return arrDesc[0].trim();
        }
        return desc;
    }

    /**
     *@return description to be printed once tossed
     */
    public String toss() {
        String desc = super.getDesc();
        String[] arrDesc = desc.split(":");
        if (arrDesc.length != 1) {
            return arrDesc[1].trim();
        }
        return desc;
    }
}
