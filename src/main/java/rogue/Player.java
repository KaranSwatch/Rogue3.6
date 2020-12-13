package rogue;
import java.awt.Point;
import java.util.ArrayList;
import java.io.Serializable;
/**
 * The player character.
 */
public class Player implements Serializable {
  private String name;
  private Point xyLocation;
  private Room currentRoom;
  private Character displayedCharacter;
  private ArrayList<Item> inventory;

  /**
   *default constructor.
   */
  public Player() {
    name = "";
  }

  /**
   * @param newName name of player
   */
  public Player(String newName) {
    this.name = newName;
  }
  /**
   * @return name of player
   */
  public String getName() {
    return name;
  }

  /**
   * @param newName name of player
   */
  public void setName(String newName) {
    this.name = newName;
  }

  /**
   * @return coordinate of player
   */
  public Point getXyLocation() {
    return xyLocation;
  }

  /**
   * @param newXyLocation coordinate of player
   */
  public void setXyLocation(Point newXyLocation) {
    this.xyLocation = newXyLocation;
  }

  /**
   * @return room where player is
   */
  public Room getCurrentRoom() {
    return currentRoom;
  }

  /**
   * @param newRoom room where player is
   */
  public void setCurrentRoom(Room newRoom) {
    this.currentRoom = newRoom;
  }

  /**
   * @return ascii character of player
   */
  public Character getDisplayCharacter() {
    return displayedCharacter;
  }

  /**
   * @param newDisplayCharacter ascii character of player
   */
  public void setDisplayCharacter(Character newDisplayCharacter) {
    this.displayedCharacter = newDisplayCharacter;
  }

  /**
   * @param newInventory inventory of player
   */
  public void setInventory(ArrayList<Item> newInventory) {
    inventory = newInventory;
  }

  /**
   * @return inventory of player
   */
  public ArrayList<Item> getInventory() {
    return inventory;
  }
}
