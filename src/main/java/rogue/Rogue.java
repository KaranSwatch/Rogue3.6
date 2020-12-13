package rogue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.Point;
import java.io.Serializable;
public class Rogue implements Serializable {
  public static final char UP = '↑';
  public static final char DOWN = '↓';
  public static final char LEFT = '←';
  public static final char RIGHT = '→';
  public static final char EAT = 'e';
  public static final char WEAR = 'w';
  public static final char TOSS = 't';
  private String nextDisplay = "-----\n|.@..|\n|....|\n-----";
  private transient RogueParser rogueRarser;

  private Player player;
  private ArrayList<Room> rooms = new ArrayList<>();
  private HashMap<String, Character> symbols = new HashMap<>();
  private ArrayList<Item> itemsList = new ArrayList<>();
  private Point xyLocation;
  private Point xyLocationPlayer;
  private Room startingRoom;
  private Room playerRoom;
  private static ArrayList<Map<String, String>> allItems = new ArrayList<>();
  private ArrayList<Map<String, Integer>> doorLocations = new ArrayList<>();
  private ArrayList<Door> doorList = new ArrayList<>();
  private final int numDoor = 4;
  private boolean found = false;
  private ArrayList<Item> inventory = new ArrayList<>();

  /**
   * deafult constructor.
   */
  public Rogue() {
    initVals();
  }

  /**
   * set up RougeParser and add items.
   * @param theDungeonInfo RougeParser object
   */
  public Rogue(RogueParser theDungeonInfo) {
    initVals();
    rogueRarser = theDungeonInfo;
    this.symbols = rogueRarser.getAllSymbols();
    this.allItems = rogueRarser.getAllItems();
    Map roomInfo = rogueRarser.nextRoom();
    while (roomInfo != null) {
      addRoom(roomInfo);
      roomInfo = rogueRarser.nextRoom();
    }
    Map itemInfo = rogueRarser.nextItem();
    while (itemInfo != null) {
      addItem(itemInfo);
      itemInfo = rogueRarser.nextItem();
    }
    makeDoors();
    addPlayer();
  }

  private void initVals() {
    inventory.clear();
    xyLocation = null;
    xyLocationPlayer = null;
    startingRoom = null;
    playerRoom = null;
    rooms = new ArrayList<>();
    itemsList = new ArrayList<>();
    allItems = new ArrayList<>();
    doorLocations = new ArrayList<>();
    doorList = new ArrayList<>();
    inventory = new ArrayList<>();
  }

  private void addPlayer() {
    Player thePlayer = new Player("Karan Swatch");
    this.player = thePlayer;
    for (Room room : rooms) {
      room.setPlayer(thePlayer);
      if (room.getStartingRoom()) {
        startingRoom = room;
        playerRoom = room;
        xyLocationPlayer = new Point(1, 1);
        player.setXyLocation(xyLocationPlayer);
        player.setCurrentRoom(room);
        player.setDisplayCharacter(symbols.get("PLAYER"));
      }
    }
    nextDisplay = playerRoom.displayRoom();
  }

  private void makeDoors() {
    for (Map doorLocation : doorLocations) {
      Door door = new Door();
      for (Room room : rooms) {
        if (Integer.valueOf(doorLocation.get("id").toString())  == room.getId()
        || Integer.valueOf(doorLocation.get("con_room").toString())  == room.getId()) {
          door.connectRoom(room);
          if (doorLocation.containsKey("N")) {
            setDoorValues("N", door, doorLocation);
          } else if (doorLocation.containsKey("S")) {
            setDoorValues("S", door, doorLocation);
          } else if (doorLocation.containsKey("E")) {
            setDoorValues("E", door, doorLocation);
          } else if (doorLocation.containsKey("W")) {
            setDoorValues("W", door, doorLocation);
          }
        }
      }
      doorList.add(door);
    } }

  private void setDoorValues(String dir, Door door, Map doorLocation) {
    door.setDoorDirection(dir);
    door.setDoorVal(Integer.valueOf(doorLocation.get(dir).toString()));
  }

  private String switchRooms(Door door, int xLocation, int yLocation) {
    playerRoom.setIsPlayerInRoom(false);
    playerRoom = door.getOtherRoom(playerRoom);
    playerRoom.setIsPlayerInRoom(true);
    xyLocationPlayer.setLocation(xLocation, yLocation);
    player.setXyLocation(xyLocationPlayer);
    player.setCurrentRoom(playerRoom);
    playerRoom.setPlayer(player);
    return "Now in room " + playerRoom.getId();
  }

  private String updatePlayer() {
    String toReturn = "";
    if (xyLocationPlayer.x == 0) { //west door
      toReturn = updatePlayerWest();
    } else if (xyLocationPlayer.y == 0) { //north door
      toReturn = updatePlayerNorth();
    } else if (xyLocationPlayer.x == playerRoom.getWidth() - 1) { //east door
      toReturn = updatePlayerEast();
    } else if (xyLocationPlayer.y == playerRoom.getHeight() - 1) { //south door
      toReturn = updatePlayerSouth();
    }
    return toReturn;
  }

  private String updatePlayerWest() {
    String toReturn = "";
    for (Door door : doorList) {
      if (door.getDoorDirection().equals("W") && door.getDoorVal() == xyLocationPlayer.y
      && door.getOtherRoom(playerRoom) != null && door.getConnectedRooms().contains(playerRoom)) {
        if (door.getOtherRoom(playerRoom).getDoor("E") != -1) {
          toReturn = switchRooms(door, door.getOtherRoom(playerRoom).getWidth() - 2,
          door.getOtherRoom(playerRoom).getDoor("E"));
          return toReturn;
        } else {
          xyLocationPlayer.translate(1, 0);
        }
      }
    }
    return toReturn;
  }

  private String updatePlayerNorth() {
    String toReturn = "";
    for (Door door : doorList) {
      if (door.getDoorDirection().equals("N") && door.getDoorVal() == xyLocationPlayer.x
      && door.getOtherRoom(playerRoom) != null && door.getConnectedRooms().contains(playerRoom)) {
        if (door.getOtherRoom(playerRoom).getDoor("S") != -1) {
          toReturn = switchRooms(door, door.getOtherRoom(playerRoom).getDoor("S"),
          door.getOtherRoom(playerRoom).getHeight() - 2);
          return toReturn;
        } else {
          xyLocationPlayer.translate(0, 1);
        }
      }
    }
    return toReturn;
  }

  private String updatePlayerEast() {
    String toReturn = "";
    for (Door door : doorList) {
      if (door.getDoorDirection().equals("E") && door.getDoorVal() == xyLocationPlayer.y
      && door.getOtherRoom(playerRoom) != null && door.getConnectedRooms().contains(playerRoom)) {
        if (door.getOtherRoom(playerRoom).getDoor("W") != -1) {
          toReturn = switchRooms(door, 1, door.getOtherRoom(playerRoom).getDoor("W"));
          return toReturn;
        } else {
          xyLocationPlayer.translate(-1, 0);
        }
      }
    }
    return toReturn;
  }

  private String updatePlayerSouth() {
    String toReturn = "";
    for (Door door : doorList) {
      if (door.getDoorDirection().equals("S") && door.getDoorVal() == xyLocationPlayer.x
      && door.getOtherRoom(playerRoom) != null && door.getConnectedRooms().contains(playerRoom)) {
        if (door.getOtherRoom(playerRoom).getDoor("N") != -1) {
          toReturn = switchRooms(door, door.getOtherRoom(playerRoom).getDoor("N"), 1);
          return toReturn;
        } else {
          xyLocationPlayer.translate(0, -1);
        }
      }
    }
    return toReturn;
  }

  /**
   * calculates the next display, but does not return it.  It returns a message for the player.
   * getNextDisplay() must be called to get the display string.
   * @param input the move that was done
   * @return message to display of the move
   * @throws InvalidMoveException thrown when move is not valid
   */
  public String makeMove(char input) throws InvalidMoveException {
    String toReturn = "";
    if (input != UP && input != DOWN && input != LEFT && input != RIGHT
    && input != EAT && input != WEAR && input != TOSS) {
      throw new InvalidMoveException("Please use the arrow keys or RDFG to move");
    }
    try {
      validateMove(input);
      if (input == EAT || input == TOSS || input == WEAR) {
        toReturn = eatTossWearMoves(input, toReturn);
      }
    } catch (InvalidMoveException e) {
      throw e;
    }
    if (toReturn == "") {
      return updateItems(input);
    }
    updateItems(input);
    return toReturn;
  }

  private void validateMove(char input) throws InvalidMoveException {
    if (input == UP && (xyLocationPlayer.y > 1 || (playerRoom.getDoor("N") == xyLocationPlayer.x
    &&  xyLocationPlayer.y == 1))) {
      fixPlayerNorth();
    } else if (input == DOWN && (xyLocationPlayer.y < playerRoom.getHeight() - 2
    || (playerRoom.getDoor("S") == xyLocationPlayer.x &&  xyLocationPlayer.y == playerRoom.getHeight() - 2))) {
        fixPlayerSouth();
    } else if (input == LEFT && (xyLocationPlayer.x > 1
    || (playerRoom.getDoor("W") == xyLocationPlayer.y &&  xyLocationPlayer.x == 1))) {
        fixPlayerWest();
    } else if (input == RIGHT && (xyLocationPlayer.x < playerRoom.getWidth() - 2
    || (playerRoom.getDoor("E") == xyLocationPlayer.y &&  xyLocationPlayer.x == playerRoom.getWidth() - 2))) {
        fixPlayerEast();
    } else if (input != EAT && input != WEAR && input != TOSS) {
        throw new InvalidMoveException("Player can not travel through walls, try another move");
    }
  }

  private String eatTossWearMoves(char input, String toReturn) throws InvalidMoveException {
      try {
        if (input == EAT) {
        toReturn = eatMove(toReturn);
        } else if (input == WEAR) {
          toReturn = wearMove(toReturn);
        } else if (input == TOSS) {
          toReturn = tossMove(toReturn);
        }
      } catch (InvalidMoveException e) {
        throw e;
      }
    return toReturn;
  }

  private String eatMove(String toReturn) throws InvalidMoveException {
    Boolean hasEat = false;
    toReturn = "Select an item to eat: ";
    for (Item item : inventory) {
      if (item.getClass() ==  Food.class || item.getClass() ==  Potion.class || item.getClass() ==  SmallFood.class) {
        hasEat = true;
        if (toReturn.indexOf(item.getName() + ", ") == -1) {
          toReturn += item.getName() + ", ";
        }
      }
    }
    if (toReturn != "") {
      toReturn = toReturn.substring(0, toReturn.length() - 2);
    }
    if (!hasEat) {
      throw new InvalidMoveException("No items are edible, try another move");
    }
    return toReturn;
  }

  private String wearMove(String toReturn) throws InvalidMoveException {
    Boolean hasWear = false;
    toReturn = "Select an item to wear: ";
    for (Item item : inventory) {
      if (item.getClass() ==  Clothing.class || item.getClass() ==  Ring.class) {
        hasWear = true;
        if (toReturn.indexOf(item.getName() + ", ") == -1) {
          toReturn += item.getName() + ", ";
        }
      }
    }
    if (toReturn != "") {
      toReturn = toReturn.substring(0, toReturn.length() - 2);
    }
    if (!hasWear) {
      throw new InvalidMoveException("No items are wearable, try another move");
    }
    return toReturn;
  }

  private String tossMove(String toReturn) throws InvalidMoveException {
    Boolean hasToss = false;
    toReturn = "Select an item to toss: ";
    for (Item item : inventory) {
      if (item.getClass() ==  Potion.class || item.getClass() ==  SmallFood.class) {
        hasToss = true;
        if (toReturn.indexOf(item.getName() + ", ") == -1) {
          toReturn += item.getName() + ", ";
        }
      }
    }
    if (toReturn != "") {
      toReturn = toReturn.substring(0, toReturn.length() - 2);
    }
    if (!hasToss) {
      throw new InvalidMoveException("No items are tossable, try another move");
    }
    return toReturn;
  }


  private void fixPlayerNorth() {
    xyLocationPlayer.setLocation(xyLocationPlayer.x, xyLocationPlayer.y - 1);
    player.setXyLocation(xyLocationPlayer);
    playerRoom.setPlayer(player);
  }

  private void fixPlayerSouth() {
    xyLocationPlayer.setLocation(xyLocationPlayer.x, xyLocationPlayer.y + 1);
    player.setXyLocation(xyLocationPlayer);
    playerRoom.setPlayer(player);
  }

  private void fixPlayerEast() {
    xyLocationPlayer.translate(1, 0);
    player.setXyLocation(xyLocationPlayer);
    playerRoom.setPlayer(player);
  }

  private void fixPlayerWest() {
    xyLocationPlayer.translate(-1, 0);
    player.setXyLocation(xyLocationPlayer);
    playerRoom.setPlayer(player);
  }

  private String removeItem(char input) {
    String toReturn = "";
    Item toRemove = null;
    for (Item item : itemsList) {
      if (input != 'w') {
        if (item.getXyLocation().equals(xyLocationPlayer) && item.getCurrentRoom() == playerRoom) {
          toRemove = item;
        }
      }
    }
    if (toRemove != null) {
      toReturn = "You Picked up: " + toRemove.getName();
      inventory.add(toRemove);
      player.setInventory(inventory);
      playerRoom.removeItem(toRemove);
      itemsList.remove(toRemove);
    }
    return toReturn;
  }

  private String updateItems(char input) {
    String toReturn = "";
    toReturn = removeItem(input);
    if (toReturn == "") {
      toReturn = updatePlayer();
    } else {
      updatePlayer();
    }
    nextDisplay = playerRoom.displayRoom();
    if (toReturn == "") {
      return "That's a lovely move: " +  Character.toString(input);
    }
    return toReturn;
  }

  /**
   * @return returns the updated display string after a move has been made
   */
  public String getNextDisplay() {
    return nextDisplay;
  }

  private void addDoors(Map<String, String> toAdd, Room room) {
    if (toAdd.containsKey("S")) {
      addSouthDoor(toAdd, room);
    }
    if (toAdd.containsKey("N")) {
      addNorthDoor(toAdd, room);
    }
    if (toAdd.containsKey("E")) {
      addEastDoor(toAdd, room);
    }
    if (toAdd.containsKey("W")) {
      addWestDoor(toAdd, room);
    }
  }

  private void addSouthDoor(Map<String, String> toAdd, Room room) {
    HashMap<String, Integer> doorLocation = new HashMap<>();
    room.setDoor("S", Integer.valueOf(toAdd.get("S")));
    doorLocation.put("S", Integer.valueOf(toAdd.get("S")));
    doorLocation.put("con_room", Integer.valueOf(toAdd.get("s_con_room")));
    doorLocation.put("id", Integer.valueOf(toAdd.get("id")));
    doorLocations.add(doorLocation);
  }

  private void addNorthDoor(Map<String, String> toAdd, Room room) {
    HashMap<String, Integer> doorLocation = new HashMap<>();
    room.setDoor("N", Integer.valueOf(toAdd.get("N")));
    doorLocation.put("N", Integer.valueOf(toAdd.get("N")));
    doorLocation.put("con_room", Integer.valueOf(toAdd.get("n_con_room")));
    doorLocation.put("id", Integer.valueOf(toAdd.get("id")));
    doorLocations.add(doorLocation);
  }

  private void addEastDoor(Map<String, String> toAdd, Room room) {
    HashMap<String, Integer> doorLocation = new HashMap<>();
      room.setDoor("E", Integer.valueOf(toAdd.get("E")));
      doorLocation.put("E", Integer.valueOf(toAdd.get("E")));
      doorLocation.put("con_room", Integer.valueOf(toAdd.get("e_con_room")));
      doorLocation.put("id", Integer.valueOf(toAdd.get("id")));
      doorLocations.add(doorLocation);
  }

  private void addWestDoor(Map<String, String> toAdd, Room room) {
    HashMap<String, Integer> doorLocation = new HashMap<>();
      room.setDoor("W", Integer.valueOf(toAdd.get("W")));
      doorLocation.put("W", Integer.valueOf(toAdd.get("W")));
      doorLocation.put("con_room", Integer.valueOf(toAdd.get("w_con_room")));
      doorLocation.put("id", Integer.valueOf(toAdd.get("id")));
      doorLocations.add(doorLocation);
  }

  /**
   * for setting up the game.  Adds a room to the game using the map provided by RogueParser.
   * @param toAdd room to be added
   */
  public void addRoom(Map<String, String> toAdd) {
    Room room = new Room();
    int width = Integer.valueOf(toAdd.get("width"));
    room.setWidth(width);
    int height = Integer.valueOf(toAdd.get("height"));
    room.setHeight(height);
    int id = Integer.valueOf(toAdd.get("id"));
    room.setId(id);
    Boolean start = Boolean.valueOf(toAdd.get("start"));
    room.setIsPlayerInRoom(start);
    addDoors(toAdd, room);
    rooms.add(room);
    room.setSymbols(symbols);
    room.setAllItems(allItems);
    if (start) {
      room.setStartingRoom(true);
    } else {
      room.setStartingRoom(false);
    }
  }

  private Point movePlayerForException(Item item, Room room) {
    int xVal = xyLocation.x;
    int yVal = xyLocation.y;
    xyLocation = topCases(item, room);
    if (xVal == xyLocation.x && yVal == xyLocation.y) {
      xyLocation = bottomCases(item, room);
      if (xVal == xyLocation.x && yVal == xyLocation.y) {
        if (item.getXyLocation().x <= 0) { //somewhere else left
          xyLocation.setLocation(1, item.getXyLocation().y);
        } else if (item.getXyLocation().x >= (room.getWidth() - 1)) { //somewhere else right
          xyLocation.setLocation(1, item.getXyLocation().y + 1);
        } else {
          xyLocation.translate(1, 0);
        }
      }
    }
    return xyLocation;
  }

  private Point topCases(Item item, Room room) {
    if (item.getXyLocation().x <= 0 && item.getXyLocation().y <= 0) { //top left
      xyLocation.setLocation(1, 1);
    } else if (item.getXyLocation().x >= (room.getWidth() - 1) && item.getXyLocation().y <= 0) { //top right
      xyLocation.setLocation(1, 1);
    } else if (item.getXyLocation().y <= 0) { //somewhere else top
      xyLocation.setLocation(item.getXyLocation().x, 1);
    }
    return xyLocation;
  }

  private Point bottomCases(Item item, Room room) {
    if (item.getXyLocation().x <= 0 && item.getXyLocation().y >= (room.getHeight() - 1)) { //bottom left
      xyLocation.setLocation(1, room.getHeight() - 2);
    } else if (item.getXyLocation().x >= (room.getWidth() - 1) && item.getXyLocation().y >= (room.getHeight() - 1)) {
      xyLocation.setLocation(1, room.getHeight() - 2);
    } else if (item.getXyLocation().y >= (room.getHeight() - 1)) { //somewhere else bottom
      xyLocation.setLocation(item.getXyLocation().x, room.getHeight() - 2);
    }
    return xyLocation;
  }


  private void addDoorLocation(String location, int thisRoomId, int connectRoomId) {
    HashMap<String, Integer> doorLocation = new HashMap<>();
    doorLocation.put(location, 1);
    doorLocation.put("con_room", connectRoomId);
    doorLocation.put("id", thisRoomId);
    doorLocations.add(doorLocation);
  }

  private void addDoorForException(Room room) {
    for (Room allRoom : rooms) {
      if (allRoom.getAllDoors().size() < numDoor) {
        if (!allRoom.getAllDoors().containsKey("N")) {
          found = addExtraNorthDoor(room, allRoom);
          break;
        } else if (!allRoom.getAllDoors().containsKey("S")) {
          found = addExtraSouthDoor(room, allRoom);
          break;
        } else if (!allRoom.getAllDoors().containsKey("E")) {
          found = addExtraEastDoor(room, allRoom);
          break;
        } else if (!allRoom.getAllDoors().containsKey("W")) {
          found = addExtraWestDoor(room, allRoom);
          break;
        }
      }
      breakOutOfProgram();
    }
  }

  private Boolean addExtraNorthDoor(Room room, Room allRoom) {
    room.setDoor("S", 1);
    allRoom.setDoor("N", 1);
    addDoorLocation("S", room.getId(), allRoom.getId());
    addDoorLocation("N", allRoom.getId(), room.getId());
    return true;
  }

  private Boolean addExtraSouthDoor(Room room, Room allRoom) {
    room.setDoor("N", 1);
    allRoom.setDoor("S", 1);
    addDoorLocation("N", room.getId(), allRoom.getId());
    addDoorLocation("S", allRoom.getId(), room.getId());
    return true;
  }

  private Boolean addExtraWestDoor(Room room, Room allRoom) {
    room.setDoor("E", 1);
    allRoom.setDoor("W", 1);
    addDoorLocation("E", room.getId(), allRoom.getId());
    addDoorLocation("W", allRoom.getId(), room.getId());
    return true;
  }

  private Boolean addExtraEastDoor(Room room, Room allRoom) {
    room.setDoor("W", 1);
    allRoom.setDoor("E", 1);
    addDoorLocation("W", room.getId(), allRoom.getId());
    addDoorLocation("E", allRoom.getId(), room.getId());
    return true;
  }

  private void breakOutOfProgram() {
    if (!found) {
      System.err.println("The dungeon file cannot be used");
      System.exit(1);
    }
  }

  /**
   * for setting up the game.  Adds an item to the game using the map provided by RogueParser.
   * @param toAdd item to be added
   */
  public void addItem(Map<String, String> toAdd) {
    Item item = setUpItem(toAdd);
    if (toAdd.containsKey("room")) {
      xyLocation = new Point(Integer.valueOf(toAdd.get("x")), Integer.valueOf(toAdd.get("y")));
      item.setXyLocation(xyLocation);
      itemsList.add(item);
      for (Room room : rooms) {
        room.setAllItems(allItems);
        if (room.getId() == Integer.valueOf(toAdd.get("room"))) {
          item.setCurrentRoom(room);
          tryItem(room, item);
        }
        try {
          room.verifyRoom();
        } catch (NotEnoughDoorsException e) {
          addDoorForException(room);
        }
      }
    }
  }

  private void tryItem(Room room, Item item) {
    Boolean itemPassed = false;
    do {
      try {
        room.addItem(item);
        itemPassed = true;
      } catch (ImpossiblePositionException e) {
        xyLocation = movePlayerForException(item, room);
      } catch (NoSuchItemException e) {
        itemPassed = true;
      }
    } while (!itemPassed);
  }

  private Item setUpItem(Map<String, String> toAdd) {
    Item item = null;
    String type = toAdd.get("type");
    item = getItemType(type);
    int id = Integer.valueOf(toAdd.get("id"));
    item.setId(id);
    String name = toAdd.get("name");
    item.setName(name);
    item.setType(type);
    String description = toAdd.get("description");
    item.setDescription(description);
    item.setDisplayCharacter(symbols.get(type.toUpperCase()));
    return item;
  }

  private Item getItemType(String type) {
    Item item = null;
    if (type.equals("Magic")) {
      item = new Magic();
    } else if (type.equals("Clothing")) {
      item = new Clothing();
    } else if (type.equals("Potion")) {
      item = new Potion();
    } else if (type.equals("Food")) {
      item = new Food();
    } else if (type.equals("Ring")) {
      item = new Ring();
    } else if (type.equals("SmallFood")) {
      item = new SmallFood();
    } else {
      item = new Item();
    }
    return item;
  }

  /**
   * @param thePlayer player of the game
   */
  public void setPlayer(Player thePlayer) {
    this.player = thePlayer;
    for (Room room : rooms) {
      room.setPlayer(thePlayer);
    }
  }

  /**
   * @return list of rooms
   */
  public ArrayList<Room> getRooms() {
    return rooms;
  }

  /**
   * @return list of all item in the game
   */
  public ArrayList<Item> getItems() {
    return itemsList;
  }

  /**
   * @return player of the game
   */
  public Player getPlayer() {
    return player;
  }
  /**
   * @return map of items list
   */
  public static ArrayList<Map<String, String>> getAllItems() {
    return allItems;
  }

  /**
   * @return inventory of player
   */
  public ArrayList<String> getInventory() {
    ArrayList<String> names = new ArrayList<>();
    for (Item item : inventory) {
      names.add(item.getName());
    }
    return names;
  }

  /**
   * @return inventory with edible
   */
  public ArrayList<String> getInventoryEdible() {
    ArrayList<String> namesOfIventory = new ArrayList<>();
    for (Item item : inventory) {
      if (item.getClass() == Food.class || item.getClass() == Potion.class
      || item.getClass() == SmallFood.class) {
        namesOfIventory.add(item.getName());
      }
    }
    return namesOfIventory;
  }

  /**
   * @return inventory with wearable
   */
  public ArrayList<String> getInventoryWearble() {
    ArrayList<String> namesOfIventory = new ArrayList<>();
    for (Item item : inventory) {
      if (item.getClass() == Clothing.class || item.getClass() == Ring.class) {
        namesOfIventory.add(item.getName());
      }
    }
    return namesOfIventory;
  }

  /**
   * @return inventory with tossable
   */
  public ArrayList<String> getInventoryTossable() {
    ArrayList<String> namesOfIventory = new ArrayList<>();
    for (Item item : inventory) {
      if (item.getClass() == Potion.class || item.getClass() == SmallFood.class) {
        namesOfIventory.add(item.getName());
      }
    }
    return namesOfIventory;
  }

  /**
   * @param item item to be removed
   * @param input char entered by user
   */
  public void removeInventory(Item item, char input) {
    if (input == 't') {
      try {
        item.setXyLocation(new Point(player.getXyLocation().x + 1, player.getXyLocation().y));
        playerRoom.addItem(item);
      } catch (ImpossiblePositionException e) {
        xyLocation = movePlayerForException(item, playerRoom);
        Point temp = new Point(xyLocation.x, xyLocation.y);
        item.setXyLocation(temp);
        try {
          playerRoom.addItem(item);
        } catch (Exception ex) {
        }
      } catch (NoSuchItemException e) {
      }
    }
    itemsList.add(item);
    player.setInventory(inventory);
    inventory.remove(item);
  }

  /**
   * @param nameOfItem name of the item
   * @param input char entered by user
   * @return messsage to be displayed
   */
  public String useItem(String nameOfItem, char input) {
    String returnString = "";
    Boolean tOrE = false;
    Item toRemove = null;
    for (Item item : inventory) {
      if (item.getName().equals(nameOfItem)) {
        returnString = getItemDesc(item, input);
        if (input == 't' || input == 'e') {
            toRemove = item;
            tOrE = true;
        } else {
          toRemove = item;
        }
      }
    }
    adjustBasedOnInput(tOrE, toRemove, input);
    return returnString;
  }

  private void adjustBasedOnInput(Boolean tOrE, Item toRemove, char input) {
    if (tOrE) {
      removeInventory(toRemove, input);
    } else {
      updateForWorn(toRemove);
    }
  }

  private void updateForWorn(Item item) {
    if (item.getName().indexOf(" - W") == -1) {
      item.setName(item.getName() + " - W");
    }
  }

  private String getItemDesc(Item item, char input) {
    String[] splitReturn = item.getDescription().split(":");
    String returnString = item.getDescription();
    if (splitReturn.length != 1) {
      if (input == 't') {
        returnString = splitReturn[1].trim();
      } else if (input == 'e') {
        returnString = splitReturn[0].trim();
      } else {
        returnString = item.getDescription();
      }
    }
    return returnString;
  }

  /**
   * @return displaying the game
   */
  public String displayAll() {
    String allRooms = "";
    for (Room currentRoom: rooms) {
      allRooms += "<---- [Room " + currentRoom.getId() + "] ---->\n";
      if (currentRoom == startingRoom) {
        allRooms += "- Starting Room\n";
      }
      allRooms += currentRoom.displayRoom() + "\n\n";
    }
    return allRooms;
  }
}
