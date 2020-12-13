package rogue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;
/**
 * A room within the dungeon - contains monsters, treasure,
 * doors out, etc.
 */
public class Room implements Serializable {
  private int width;
  private int height;
  private int id;
  private ArrayList<Item> roomItems;
  private HashMap<String, Integer> doors;
  private Player player;
  private Boolean isPlayerInRoom;
  private HashMap<String, Character> symbols;
  private ArrayList<Map<String, String>> items;
  private Boolean startingRoom;

  /**
   *default constructor.
   */
  public Room() {
    roomItems = new ArrayList<>();
    doors = new HashMap<>();
    isPlayerInRoom = false;
    symbols = new HashMap<>();
    items = new ArrayList<>();
  }

  /**
   * @return width of room
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param newWidth width of room
   */
  public void setWidth(int newWidth) {
    this.width = newWidth;
  }

  /**
   * @return symbols of the game
   */
  public HashMap<String, Character> getSymbols() {
    return symbols;
  }

  /**
   * @param newSymbols symbols of the game
   */
  public void setSymbols(HashMap<String, Character> newSymbols) {
    this.symbols = newSymbols;
  }

  /**
   * @return height of room
   */
  public int getHeight() {
    return height;
  }

  /**
   * @param newStartingRoom is this the starting room
   */
  public void setStartingRoom(Boolean newStartingRoom) {
    this.startingRoom = newStartingRoom;
  }

  /**
   * @return is this the starting room
   */
  public Boolean getStartingRoom() {
    return startingRoom;
  }

  /**
   * @param newHeight height of room
   */
  public void setHeight(int newHeight) {
    this.height = newHeight;
  }

  /**
   * @return id of room
   */
  public int getId() {
    return id;
  }

  /**
   * @param newId id of room
   */
  public void setId(int newId) {
    this.id = newId;
  }

  /**
   * @return items in the room
   */
  public ArrayList<Item> getRoomItems() {
    return roomItems;
  }

  /**
   * @param newRoomItems items in the room
   */
  public void setRoomItems(ArrayList<Item> newRoomItems) {
    this.roomItems = newRoomItems;
  }

  /**
   * @return all items in the game
   */
  public ArrayList<Map<String, String>> getAllItems() {
    return items;
  }

  /**
   * @return player of the game
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * @param newPlayer player of the game
   */
  public void setPlayer(Player newPlayer) {
    this.player = newPlayer;
  }

  /**
   * @param direction one of N,S,E,W
   * @return location of door from given direction
   */
  public int getDoor(String direction) {
    if (doors.containsKey(direction)) {
      return doors.get(direction);
    } else {
      return -1;
    }
  }

  /**
   * @param allItems all items in the game
   */
  public void setAllItems(ArrayList<Map<String, String>> allItems) {
    this.items = allItems;
  }

  /*
  direction is one of NSEW
  location is a number between 0 and the length of the wall.
  */

  /**
   * @param direction one of N,S,E,W
   * @param location location of door
   */
  public void setDoor(String direction, int location) {
    doors.put(direction, location);
  }

  /**
   *
   * @return all doors in room
   */
  public HashMap<String, Integer> getAllDoors() {
    return doors;
  }

  /**
   * @return if player is in the room
   */
  public boolean isPlayerInRoom() {
    if (player == null) {
      return false;
    } else if (player.getCurrentRoom() == null) {
      return false;
    } else if (player.getCurrentRoom() == this) {
      return true;
    }
    return false;
  }


  /**
   * @param newIsPlayerInRoom if player is in the room
   */
  public void setIsPlayerInRoom(Boolean newIsPlayerInRoom) {
    this.isPlayerInRoom = newIsPlayerInRoom;
  }

  /**
   * remove item when player gets it.
   * @param toRemove item to be removed
   */
  public void removeItem(Item toRemove) {
    roomItems.remove(toRemove);
  }

  /**
   * @param toAdd item to be added to list of items in the room
   * @throws ImpossiblePositionException when item is on wall, door, on top of another element or outside boundaries
   * @throws NoSuchItemException when an item id is present in a room but does not exist in the list of items
   */
  public void addItem(Item toAdd) throws ImpossiblePositionException, NoSuchItemException {
    items = Rogue.getAllItems();
    try {
      checkIfItemExists(toAdd);
    } catch (NoSuchItemException e) {
      throw e;
    }
    try {
      checkIfItemOntopOtherItem(toAdd);
      checkIfPlayerOnItem(toAdd);
      checkIfItemImValidLocation(toAdd);
    } catch (ImpossiblePositionException e) {
      throw e;
    }
    toAdd.setCurrentRoom(this);
    roomItems.add(toAdd);
  }

  private void checkIfItemOntopOtherItem(Item toAdd) throws ImpossiblePositionException {
    for (Item currentItem : roomItems) {
      if (currentItem.getXyLocation().x == toAdd.getXyLocation().x
      && currentItem.getXyLocation().y == toAdd.getXyLocation().y) {
        throw new ImpossiblePositionException();
      }
    }
  }

  private void checkIfPlayerOnItem(Item toAdd) throws ImpossiblePositionException {
    if (player != null) {
      if (player.getXyLocation().x == toAdd.getXyLocation().x
      && player.getXyLocation().y == toAdd.getXyLocation().y) {
        throw new ImpossiblePositionException();
      }
    }
  }

  private void checkIfItemExists(Item toAdd) throws NoSuchItemException {
    items = Rogue.getAllItems();
    Boolean inAllItems = false;
    for (Map item : items) {
      if (toAdd.getId() == Integer.valueOf(item.get("id").toString())) {
        inAllItems = true;
        break;
      }
    }
    if (!inAllItems) {
      throw new NoSuchItemException();
    }
  }

  private void checkIfItemImValidLocation(Item toAdd) throws ImpossiblePositionException {
    if ((toAdd.getXyLocation().x <= 0 || toAdd.getXyLocation().y <= 0)
    || (toAdd.getXyLocation().x >= (width - 1) || toAdd.getXyLocation().y >= (height - 1))) {
      throw new ImpossiblePositionException();
    }
  }

  /**
   * @return true if the room is complete (items in valid places,
   * player in valid place if in room,
   * all doors have a connection), false otherwise
   * @throws NotEnoughDoorsException if room does not have atleast 1 door
   */
  public Boolean verifyRoom() throws NotEnoughDoorsException {
    if (doors.size() == 0) {
      throw new NotEnoughDoorsException();
    }
    return true;
  }

  private String buildRoomOutline() {
    String roomAscii = "";
    for (int i = 0; i < width; i++) {
      roomAscii += symbols.get("NS_WALL");
    }
    roomAscii += "\n";
    for (int i = 0; i < height - 2; i++) {
      roomAscii += symbols.get("EW_WALL");
      for (int j = 0; j < width - 2; j++) {
      roomAscii += symbols.get("FLOOR");
      }
      roomAscii += symbols.get("EW_WALL");
      roomAscii += "\n";
    }
    for (int i = 0; i < width; i++) {
      roomAscii += symbols.get("NS_WALL");
    }
    roomAscii += "\n";
    return roomAscii;
  }

  private StringBuilder setRoomDoors(String roomAscii) {
    StringBuilder roomAsciiBuilder = new StringBuilder(roomAscii);
    if (doors.containsKey("N")) {
      roomAsciiBuilder.setCharAt((int) doors.get("N"), symbols.get("DOOR"));
    }
    if (doors.containsKey("S")) {
      roomAsciiBuilder.setCharAt((width + 1) * (height - 1) + (int) doors.get("S"),
      symbols.get("DOOR"));
    }
    if (doors.containsKey("E")) {
      roomAsciiBuilder.setCharAt((width + 1) * ((int) doors.get("E") + 1) - 2,
      symbols.get("DOOR"));
    }
    if (doors.containsKey("W")) {
      roomAsciiBuilder.setCharAt((width + 1) * (int) doors.get("W"),
      symbols.get("DOOR"));
    }
    return roomAsciiBuilder;
  }
  /**
   * Produces a string that can be printed to produce an ascii rendering of the room and all of its contents.
   * @return (String) String representation of how the room looks
   */
  public String displayRoom() {
    String roomAscii = buildRoomOutline();
    StringBuilder roomAsciiBuilder = setRoomDoors(roomAscii);
    if (isPlayerInRoom) {
      int xLocationPlayer = player.getXyLocation().x;
      int yLocationPlayer = player.getXyLocation().y;
      roomAsciiBuilder.setCharAt((width + 1) * yLocationPlayer + xLocationPlayer,
      symbols.get("PLAYER"));
    }
    for (Item currentItem : roomItems) {
      int xLocationItem = currentItem.getXyLocation().x;
      int yLocationItem = currentItem.getXyLocation().y;
      roomAsciiBuilder.setCharAt((width + 1) * yLocationItem + xLocationItem,
      symbols.get(currentItem.getType().toUpperCase()));
    }
    return roomAsciiBuilder.toString();
  }
}
