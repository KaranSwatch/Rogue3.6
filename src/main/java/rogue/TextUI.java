package rogue;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.TerminalPosition;

import javax.swing.JFrame;
import java.awt.Container;
import java.awt.GridBagConstraints;

import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.GridBagLayout;
import javax.swing.JList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.JScrollPane;

import java.io.Serializable;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class TextUI extends JFrame implements Serializable {
  private TerminalScreen screen;
  private final char startCol = 0;
  private final char msgRow = 1;
  private final char roomRow = 3;
  private static String configurationFileLocation;
  private static RogueParser parser;
  private static Rogue theGame;
  private static TextUI theGameUI;

  private SwingTerminal terminal;
  public static final int WIDTH = 875;
  public static final int HEIGHT = 515;
  // Screen buffer dimensions are different than terminal dimensions
  public static final int COLS = 80;
  public static final int ROWS = 24;
  private Container contentPane;
  private GridBagConstraints gc;

  private JPanel terminalPanel;

  private JPanel playerPanel;
  private JLabel playerLabel;

  private JPanel gameStatusPanel;
  private JLabel gameStatusLabel;

  private JPanel inventoryPanel;
  private JLabel inventoryLabel;

  private JPanel inventoryContentsPanel;
  private JList inventoryContentsList;
  private JScrollPane inventoryContentsPane;

  private JMenuBar menuBar;
  private JMenu gameOptions;
  private JMenuItem changeName;
  private JMenuItem loadJSON;
  private JMenuItem saveGame;
  private JMenuItem loadGame;
  private DefaultListModel modelOfList = new DefaultListModel();
  private final int colorOfGUI = 0x03DAC6;
  private final int colorOfGUI2 = 0x121212;
  private final int colorOfGUI3 = 0x6200EE;
  private final int borderSize = 4;

  /**
  Constructor for TextUI class.  Creates the screens, sets
  cursor to top left corner and does nothing else.
  **/
  public TextUI() {
    super("my awesome game");
    contentPane = getContentPane();
    setWindowDefaults(getContentPane());
    setUpPanels();
    setMenuBar();
    pack();
    setSize(WIDTH, HEIGHT);
    start();
  }

  private void setWindowDefaults(Container newContentPane) {
    setTitle("Rogue!");
    setSize(WIDTH, HEIGHT);
    setResizable(false);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    newContentPane.setLayout(new GridBagLayout());
    newContentPane.setBackground(new Color(colorOfGUI));
    gc = new GridBagConstraints();
  }

  private void setTerminal() {
    terminalPanel = new JPanel();
    terminal = new SwingTerminal();
    terminalPanel.add(terminal);
    terminalPanel.setBackground(new Color(colorOfGUI2));
    gc.gridx = 0;
    gc.gridy = 1;
    gc.weightx = 0;
    contentPane.add(terminalPanel, gc);
  }

  private void setUpPanels() {
    playerPanel = new JPanel(new FlowLayout());
    setUpPlayerPanel(playerPanel);
    gameStatusPanel = new JPanel();
    setUpGameStatusPanel(gameStatusPanel);
    inventoryPanel = new JPanel();
    setUpInventoryPanel(inventoryPanel);
    inventoryContentsPanel = new JPanel();
    setUpInventoryContentsPanel(inventoryContentsPanel);
    setTerminal();
  }

  private void setUpLabelPanel(JPanel thePanel) {
    Border prettyLine = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    thePanel.setBorder(prettyLine);
    JLabel exampleLabel = new JLabel("Tomorrow and tomorrow and tomorrow");
    thePanel.add(exampleLabel);
    final int dataEntryVal = 25;
    JTextField dataEntry = new JTextField("Enter text here", dataEntryVal);
    thePanel.add(dataEntry);
    JButton clickMe = new JButton("Click Me");
    thePanel.add(clickMe);
    contentPane.add(thePanel, BorderLayout.SOUTH);
  }

  private void setUpPlayerPanel(JPanel thePanel) {
    playerLabel = new JLabel("name of player");
    thePanel.add(playerLabel);
    thePanel.setBackground(new Color(colorOfGUI));
    thePanel.setBorder(new LineBorder(new Color(colorOfGUI2), borderSize));
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.gridx = 0;
    gc.gridy = 0;
    contentPane.add(thePanel, gc);
  }

  private void setUpGameStatusPanel(JPanel thePanel) {
    gameStatusLabel = new JLabel("Welcome To My Rogue Game");
    thePanel.add(gameStatusLabel);
    thePanel.setBackground(new Color(colorOfGUI));
    thePanel.setBorder(new LineBorder(new Color(colorOfGUI2), borderSize));
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.gridx = 0;
    gc.gridy = 2;
    contentPane.add(thePanel, gc);
  }

  private void setUpInventoryPanel(JPanel thePanel) {
    inventoryLabel = new JLabel("Inventory");
    thePanel.add(inventoryLabel);
    thePanel.setBackground(new Color(colorOfGUI));
    thePanel.setBorder(new LineBorder(new Color(colorOfGUI2), borderSize - 2));
    gc.fill = GridBagConstraints.BOTH;
    gc.gridx = 1;
    gc.gridy = 0;
    final double weight = 0.1;
    gc.weightx = weight;
    contentPane.add(thePanel, gc);
  }

  private void setUpInventoryContentsPanel(JPanel thePanel) {
    Object[] empty = new Object[1];
    inventoryContentsList = new JList(empty);
    inventoryContentsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    inventoryContentsList.setVisibleRowCount(-1);
    inventoryContentsList.setBackground(new Color(colorOfGUI));
    inventoryContentsList.setBorder(new LineBorder(new Color(colorOfGUI2), borderSize));
    gc.fill = GridBagConstraints.BOTH;
    gc.gridx = 1;
    gc.gridy = 1;
    contentPane.add(inventoryContentsList, gc);
  }

  private void updateInventory() {
    if (theGame.getInventory() != null) {
      contentPane.remove(inventoryContentsList);
      //inventoryContentsList = new JList(theGame.getInventory().toArray());
      inventoryContentsList = new JList(modelOfList);
      inventoryContentsList.setListData(theGame.getInventory().toArray());
      inventoryContentsList.setLayoutOrientation(JList.VERTICAL);
      inventoryContentsList.setVisibleRowCount(2);
      inventoryContentsList.setBackground(new Color(colorOfGUI));
      gc.fill = GridBagConstraints.NONE;
      gc.gridx = 1;
      gc.gridy = 1;
      contentPane.add(inventoryContentsList, gc);
    }
  }

  private void setMenuBar() {
    menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    gameOptions = new JMenu("Game Options");
    menuBar.add(gameOptions);
    changeName = new JMenuItem("Change Name");
    changeName.addActionListener(ev -> changeName());
    gameOptions.add(changeName);
    loadJSON = new JMenuItem("load JSON");
    loadJSON.addActionListener(ev -> loadJSON());
    gameOptions.add(loadJSON);
    saveGame = new JMenuItem("Save Game");
    saveGame.addActionListener(ev -> saveGame());
    gameOptions.add(saveGame);
    loadGame = new JMenuItem("Load Game");
    loadGame.addActionListener(ev -> loadGame());
    menuBar.setBackground(new Color(colorOfGUI));
    gameOptions.add(loadGame);
  }

  private void changeName() {
    String newName = null;
    newName = JOptionPane.showInputDialog(contentPane, "Enter a name");
    theGame.getPlayer().setName(newName);
    if (newName != null) {
      updatePlayerText("Player Name: " + newName);
    }
  }

  private void updatePlayerText(String name) {
    playerLabel.setText(name);
  }

  private void updateGameStatusText(String text) {
    gameStatusLabel.setText(text);
  }

  private void loadJSON() {
    JFileChooser chooser = new JFileChooser(".");
    int response = chooser.showOpenDialog(this);
    if (response == JFileChooser.APPROVE_OPTION) {
      try {
        resetGame(chooser.getSelectedFile().getName());
      } catch (Exception e) {
        JOptionPane.showMessageDialog(contentPane, "File could not be loaded");
        loadJSON();
      }
    }
  }

  private void resetGame(String filename) throws Exception {
    try {
      parser.parseNewRoomsFileOnly(filename);
      theGame = new Rogue(parser);
      contentPane.remove(inventoryContentsList);
      updateInventory();
      setUpInventoryContentsPanel(inventoryContentsPanel);
      theGameUI.draw("", theGame.getNextDisplay());
      inventoryContentsList.clearSelection();
      inventoryContentsList.removeAll();
      inventoryContentsList = null;
      setUpInventoryContentsPanel(inventoryContentsPanel);
      theGameUI.setVisible(true);
      theGameUI.updatePlayerText("Player Name: " + theGame.getPlayer().getName());
    } catch (Exception e) {
      throw e;
    }
  }

  private void saveGame() {
    JFileChooser chooser = new JFileChooser(".");
    int response = chooser.showSaveDialog(null);
    String newName = chooser.getSelectedFile().getName();
    String path = chooser.getSelectedFile().getAbsolutePath();
    if (response == JFileChooser.APPROVE_OPTION && newName != null) {
      saveFile(path);
    }
  }

  private void loadGame() {
    JFileChooser chooser = new JFileChooser(".");
    int response = chooser.showOpenDialog(this);
    if (response == JFileChooser.APPROVE_OPTION) {
      try {
        loadFile(chooser.getSelectedFile().getName());
      } catch (Exception e) {
        JOptionPane.showMessageDialog(contentPane, "File could not be loaded");
        loadGame();
      }
    }
  }

  private void saveFile(String filename) {
    try {
      FileOutputStream outputStream = new FileOutputStream(filename);
      ObjectOutputStream outputDest = new ObjectOutputStream(outputStream);
      outputDest.writeObject(theGame);
      outputDest.close();
      outputStream.close();
    } catch (IOException e) {
        System.out.println(e);
    }
  }

  private void loadFile(String filename) throws IOException, ClassNotFoundException {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename)); ) {
      theGame = (Rogue) in.readObject();
      theGameUI.draw("", theGame.getNextDisplay());
      contentPane.remove(inventoryContentsList);
      inventoryContentsList.clearSelection();
      inventoryContentsList.removeAll();
      inventoryContentsList = null;
      setUpInventoryContentsPanel(inventoryContentsPanel);
      updateInventory();
      //theGameUI.setVisible(true);
      theGameUI.updatePlayerText("Player Name: " + theGame.getPlayer().getName());
    } catch (IOException e) {
        throw e;
    } catch (ClassNotFoundException e) {
       throw e;
      }
  }

  private void start() {
    try {
      screen = new TerminalScreen(terminal);
      screen.setCursorPosition(TerminalPosition.TOP_LEFT_CORNER);
      screen.startScreen();
      screen.refresh();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  /**
  Prints a string to the screen starting at the indicated column and row.
  @param toDisplay the string to be printed
  @param column the column in which to start the display
  @param row the row in which to start the display
  **/
  public void putString(String toDisplay, int column, int row) {
    Terminal t = screen.getTerminal();
    try {
      t.setCursorPosition(column, row);
      for (char ch: toDisplay.toCharArray()) {
        t.putCharacter(ch);
      }
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  /**
  Changes the message at the top of the screen for the user.
  @param msg the message to be displayed
  **/
  public void setMessage(String msg) {
    putString("                                                                                               ", 1, 1);
    putString(msg, startCol, msgRow);
  }

  /**
  Redraws the whole screen including the room and the message.
  @param message the message to be displayed at the top of the room
  @param room the room map to be drawn
  **/
  public void draw(String message, String room) {
    try {
      screen.getTerminal().clearScreen();
      setMessage(message);
      putString(room, startCol, roomRow);
      screen.refresh();
    } catch (IOException e) {
    }
  }

  /**
  Obtains input from the user and returns it as a char.  Converts arrow
  keys to the equivalent movement keys in rogue.
  @return the ascii value of the key pressed by the user
  **/
  public char getInput() {
    KeyStroke keyStroke = null;
    char returnChar = ' ';
    while (keyStroke == null) {
      try {
          keyStroke = screen.pollInput();
      } catch (IOException e) {
          e.printStackTrace();
      }
    }
    returnChar = checkMovementKeys(keyStroke);
    if (returnChar == ' ') {
      returnChar = checkOtherKeys(keyStroke);
      if (returnChar == ' ') {
        returnChar = checkInventoryKeys(keyStroke);
      }
    }
    return returnChar;
  }

  private char checkMovementKeys(KeyStroke keyStroke) {
    char returnChar = ' ';
    if (keyStroke.getKeyType() == KeyType.ArrowDown) {
      returnChar = Rogue.DOWN;  //constant defined in rogue
    } else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
        returnChar = Rogue.UP;
    } else if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
        returnChar = Rogue.LEFT;
    } else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
        returnChar = Rogue.RIGHT;
    }
    return returnChar;
  }

  private char checkOtherKeys(KeyStroke keyStroke) {
    char returnChar = ' ';
    if (keyStroke.getCharacter() == 'r') {
      returnChar = Rogue.UP;
    } else if (keyStroke.getCharacter() == 'd') {
      returnChar = Rogue.LEFT;
    } else if (keyStroke.getCharacter() == 'f') {
      returnChar = Rogue.DOWN;
    } else if (keyStroke.getCharacter() == 'g') {
      returnChar = Rogue.RIGHT;
    }
    return returnChar;
  }

  private char checkInventoryKeys(KeyStroke keyStroke) {
    char returnChar = ' ';
    if (keyStroke.getCharacter() == 'e') {
      returnChar = Rogue.EAT;
    } else if (keyStroke.getCharacter() == 'w') {
        returnChar = Rogue.WEAR;
    } else if (keyStroke.getCharacter() == 't') {
        returnChar = Rogue.TOSS;
    } else {
      returnChar = keyStroke.getCharacter();
    }
    return returnChar;
  }

  private String getItemToUse(char input) {
    int itemToUse = -1;
    ArrayList<String> toDisplay = null;
    if (input == 'e') {
      toDisplay = theGame.getInventoryEdible();
    } else if (input == 'w') {
      toDisplay = theGame.getInventoryWearble();
    } else if (input == 't') {
      toDisplay = theGame.getInventoryTossable();
    }
    itemToUse = JOptionPane.showOptionDialog(null, "Select an item", null,
    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, toDisplay.toArray(), null);
    if (itemToUse != -1) {
      return toDisplay.get(itemToUse);
    }
    return "";
  }

  private static void setUpObjects() {
    configurationFileLocation = "fileLocations.json";
    parser = new RogueParser(configurationFileLocation);
    theGame = new Rogue(parser);
    theGameUI = new TextUI();
  }

  private static void updateGameUI(String message) {
    theGameUI.draw("", theGame.getNextDisplay());
    theGameUI.setVisible(true);
    theGameUI.updatePlayerText("Player Name: " + theGame.getPlayer().getName());
  }

  private static void nextMove(String message, char userInput) throws InvalidMoveException {
    try {
      message = theGame.makeMove(userInput);
      theGameUI.updateGameStatusText(message);
      theGameUI.draw("", theGame.getNextDisplay());
      theGameUI.updateInventory();
      if (userInput == 'e' || userInput == 't' || userInput == 'w') {
        String itemToUse = theGameUI.getItemToUse(userInput);
        if (itemToUse != "" && itemToUse != null) {
          message = theGame.useItem(itemToUse, userInput);
          theGameUI.clearInventory();
          theGameUI.updateGameStatusText(message);
        }
      }
    } catch (InvalidMoveException e) {
      throw e;
    }
  }

  private void clearInventory() {
    contentPane.remove(inventoryContentsList);
    updateInventory();
    setUpInventoryContentsPanel(inventoryContentsPanel);
    theGameUI.draw("", theGame.getNextDisplay());
    inventoryContentsList.clearSelection();
    inventoryContentsList.removeAll();
    inventoryContentsList = null;
    setUpInventoryContentsPanel(inventoryContentsPanel);
  }

  /**
  the main method.
  @param args command line arguments are unused at this point.
  **/
  public static void main(String[] args) {
    char userInput = 'h';
    String message = "Welcome to my Rogue game";
    setUpObjects();
    updateGameUI(message);
    while (userInput != 'q') {
      userInput = theGameUI.getInput();
      try {
        nextMove(message, userInput);
      } catch (InvalidMoveException badMove) {
        message = badMove.getMessage();
        if (message == null) {
          message = "I didn't understand what you meant, please enter a command";
        }
        theGameUI.updateGameStatusText(message);
        theGameUI.updateInventory();
        theGameUI.setMessage("");
      }
    }
  }
}
