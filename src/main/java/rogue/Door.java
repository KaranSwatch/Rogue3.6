package rogue;

import java.util.ArrayList;
import java.io.Serializable;
public class Door implements Serializable {
  private ArrayList<Room> connectedRooms;
  private String doorDirection;
  private int doorVal;

  /**
   * deafult constructor.
   */
  public Door() {
    connectedRooms = new ArrayList<>();
  }

  /**
   * @param newDoorDirection direction of door (N,S,E,W)
   */
  public void setDoorDirection(String newDoorDirection) {
    this.doorDirection = newDoorDirection;
  }

  /**
   * @param newDoorVal value of door
   */
  public void setDoorVal(int newDoorVal) {
    this.doorVal = newDoorVal;
  }

  /**
   * @return direction of door (N,S,E,W)
   */
  public String getDoorDirection() {
    return doorDirection;
  }

  /**
   * @return value of door
   */
  public int getDoorVal() {
    return doorVal;
  }

  /**
   *
   * @param r specify one of the two rooms that can be attached to a door
   */
  public void connectRoom(Room r) {
    if (connectedRooms.size() < 2) {
      connectedRooms.add(r);
    }
  }

  /**
   *
   * @return get an Arraylist that contains both rooms connected by this door
   */
  public ArrayList<Room> getConnectedRooms() {
    return connectedRooms;
  }

  /**
   *
   * @param currentRoom get the connected room by passing in the current room
   * @return the room connected to currentRoom
   */
  public Room getOtherRoom(Room currentRoom) {
    for (Room thisRoom : connectedRooms) {
      if (thisRoom != currentRoom) {
        return thisRoom;
      }
    }
    return null;
  }
}
