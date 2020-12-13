package rogue;
import java.awt.Point;
import java.io.Serializable;
/**
 * A basic Item class; basic functionality for both consumables and equipment.
 */
public class Item implements Serializable {
  private int id;
  private String name;
  private String type;
  private Point xyLocation;
  private Character displayedCharacter;
  private String description;
  private Room currentRoom;

  /**
   *default constructor.
   */
  public Item() {
    id = 0;
    name = "";
    type = "";
    description = "";
  }

 /**
  * @return id of item
  */
  public int getId() {
    return id;
  }

  /**
   * @param newId id of item
   */
  public void setId(int newId) {
    this.id = newId;
  }

  /**
   * @return name of item
   */
  public String getName() {
    return name;
  }

  /**
   * @param newName name of item
   */
  public void setName(String newName) {
    this.name = newName;
  }

  /**
   * @return type of item
   */
  public String getType() {
    return type;
  }

  /**
   * @param newType type of item
   */
  public void setType(String newType) {
    this.type = newType;
  }

  /**
   * @return ascii character of the item
   */
  public Character getDisplayCharacter() {
    return displayedCharacter;
  }

  /**
   * @param newDisplayCharacter ascii character of the item
   */
  public void setDisplayCharacter(Character newDisplayCharacter) {
    this.displayedCharacter = newDisplayCharacter;
  }

  /**
   * @return description of item
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param newDescription description of item
   */
  public void setDescription(String newDescription) {
    this.description = newDescription;
  }

  /**
   * @return coordinate of item
   */
  public Point getXyLocation() {
    return xyLocation;
  }

  /**
   * @param newXyLocation coordinate of item
   */
  public void setXyLocation(Point newXyLocation) {
    this.xyLocation = newXyLocation;
  }

  /**
   * @return room of where item is
   */
  public Room getCurrentRoom() {
    return currentRoom;
  }

  /**
   * @param newCurrentRoom room of where item is
   */
  public void setCurrentRoom(Room newCurrentRoom) {
    this.currentRoom = newCurrentRoom;
  }
}
