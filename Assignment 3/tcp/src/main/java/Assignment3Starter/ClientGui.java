package Assignment3Starter;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.net.*;
import java.io.*;
import java.util.Base64;
import java.util.Map;

import java.nio.file.Paths;
import java.util.Scanner;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;

/**
 * The ClientGui class is a GUI frontend that displays an image grid, an input text box,
 * a button, and a text area for status. 
 * 
 * Methods of Interest
 * ----------------------
 * show(boolean modal) - Shows the GUI frame with the current state
 *     -> modal means that it opens the GUI and suspends background processes. Processing still happens 
 *        in the GUI. If it is desired to continue processing in the background, set modal to false.
 * newGame(int dimension) - Start a new game with a grid of dimension x dimension size
 * insertImage(String filename, int row, int col) - Inserts an image into the grid
 * appendOutput(String message) - Appends text to the output panel
 * submitClicked() - Button handler for the submit button in the output panel
 * 
 * Notes
 * -----------
 * > Does not show when created. show() must be called to show he GUI.
 * 
 */

public class ClientGui implements Assignment3Starter.OutputPanel.EventHandlers {
  JDialog frame;
  PicturePanel picturePanel;
  static OutputPanel outputPanel;

  //hasName boolean to check if we have the name already
  static boolean hasName = false;
  static String playerName = "";
  static int port = 8080;
  static Socket sock = null;
  static String host = "localhost";

  /**
   * Construct dialog
   */
  public ClientGui() {
    frame = new JDialog();
    frame.setLayout(new GridBagLayout());
    frame.setMinimumSize(new Dimension(500, 500));
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    // setup the top picture frame
    picturePanel = new PicturePanel();
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weighty = 0.25;
    frame.add(picturePanel, c);

    // setup the input, button, and output area
    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    c.weighty = 0.75;
    c.weightx = 1;
    c.fill = GridBagConstraints.BOTH;
    outputPanel = new OutputPanel();
    outputPanel.addEventHandlers(this);
    frame.add(outputPanel, c);


  }

  /**
   * Shows the current state in the GUI
   * @param makeModal - true to make a modal window, false disables modal behavior
   */
  public void show(boolean makeModal) {
    frame.pack();
    frame.setModal(makeModal);
    frame.setVisible(true);
  }

  /**
   * Creates a new game and set the size of the grid 
   * @param dimension - the size of the grid will be dimension x dimension
   */
  public void newGame(int dimension) {
    picturePanel.newGame(dimension);
    outputPanel.appendOutput("Started new game with a " + dimension + "x" + dimension + " board.");
  }

  /**
   * Insert an image into the grid at position (col, row)
   * 
   * @param filename - filename relative to the root directory
   * @param row - the row to insert into
   * @param col - the column to insert into
   * @return true if successful, false if an invalid coordinate was provided
   * @throws IOException An error occured with your image file
   */
  public boolean insertImage(String filename, int row, int col) throws IOException {
    String error = "";
    try {
      // insert the image
      if (picturePanel.insertImage(filename, row, col)) {
      // put status in output
        outputPanel.appendOutput("Inserting " + filename + " in position (" + row + ", " + col + ")");
        return true;
      }
      error = "File(\"" + filename + "\") not found.";
    } catch(PicturePanel.InvalidCoordinateException e) {
      // put error in output
      error = e.toString();
    }
    outputPanel.appendOutput(error);
    return false;
  }

  /**
   * Submit button handling
   * 
   * Change this to whatever you need
   */
  @Override
  public void submitClicked() {

    try {
      // open the connection
      sock = new Socket(host, port); // connect to host and socket on port 8888

      // get output channel
      OutputStream out = sock.getOutputStream();

      // create an object output writer (Java only)
      ObjectOutputStream os = new ObjectOutputStream(out);

      ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

      Scanner scanner = new Scanner(System.in);
      //String message = scanner.nextLine();

      //String str = "Please select a valid request.\ngame\nscoreboard\n<name of character>\nnext\nmore";
      //outputPanel.appendOutput(str);

      // Pulls the input box text
      String input = outputPanel.getInputText();

      //String json = "{'type': 'echo', 'data': 'hello'}";
      JSONObject json = new JSONObject();

      if(hasName == false){
        hasName = true;
        json.put("type", "name");
        json.put("data", input);
      }
//      if(checkInteger(input).equals("Please input a string"))
//        json.put("type", "error");
      else if(input.equals("game") || input.equals("Game") || input.equals("GAME"))
        json.put("type", "game");
      else if (input.equals("scoreboard") || input.equals("Scoreboard") || input.equals("SCOREBOARD"))
        json.put("type", "scoreboard");
      else if (input.equals("next") || input.equals("Next") || input.equals("NEXT"))
        json.put("type", "next");
      else if (input.equals("more") || input.equals("More") || input.equals("MORE"))
        json.put("type", "more");
      else if (input.equals("exit")){
        json.put("type", "exit");
      } else {
        json.put("type", "guess");
        json.put("data", input);
      }

      // An example how to update the points in the UI
      //outputPanel.setPoints(10);

      // if has input
      if (input.length() > 0) {
        // append input to the output panel
        outputPanel.appendOutput(input);
        // clear input text box
        outputPanel.setInputText("");
      }

      // write the whole message
      os.writeObject(json.toString());

      // make sure it wrote and doesn't get cached in a buffer
      os.flush();

      String i = (String) in.readObject();
      JSONObject req = new JSONObject(i);

      if (req.getString("type").equals("name")) {
        i = req.getString("data");
        outputPanel.appendOutput(i);
      }
//      else if(req.getString("type").equals("img")) {
//        //i = req.getString("data");
//        Base64.Decoder decoder = Base64.getDecoder();
//        byte[] bytes = decoder.decode(req.getString("data"));
//        ImageIcon icon = null;
//        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
//          BufferedImage image = ImageIO.read(bais);
//          icon = new ImageIcon(image);
//        }
//        if (icon != null) {
//          PicturePanel.setImg(icon);
//          JFrame frame = new JFrame();
//          JLabel label = new JLabel();
//          label.setIcon(icon);
//          frame.add(label);
//          frame.setSize(icon.getIconWidth(), icon.getIconHeight());
//          frame.show();
//        }

      else if (req.getString("type").equals("scoreboard")) {
        i = req.getString("data");
        String[] elements = i.split(",");
        for (int j = 0; j < elements.length; j++) {
          outputPanel.appendOutput(elements[j] + "\n");
        }
      } else if (req.getString("type").equals("game")) {
        i = req.getString("data");
        // add images to the grid
        insertImage(i, 0, 0);
        outputPanel.appendOutput("Make a guess such as 'Captin_Marvel'!");
      } else if (req.getString("type").equals("guess")) {
        i = req.getString("data");
        if (req.isNull("img")){
          outputPanel.appendOutput(i);
        } else {
          // add images to the grid
          String i2 = req.getString("img");
          insertImage(i2, 0, 0);
          outputPanel.appendOutput(i);
        }
      }
      //String i = (String) in.readObject();
      //outputPanel.appendOutput(i);
      //if(input == "exit")
        sock.close(); // close socked after sending
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public String checkInteger(String input) {
    try{
      Integer.parseInt(input);
      return input;
    } catch (NumberFormatException e){
      return "Please input a string";
    }
  }
  
  /**
   * Key listener for the input text box
   * 
   * Change the behavior to whatever you need
   */
  @Override
  public void inputUpdated(String input) {
    if (input.equals("surprise")) {
      outputPanel.appendOutput("You found me!");
    }
  }

  public static void main(String[] args) throws IOException {

    if (args.length >= 1){ // port, if provided
      port= Integer.parseInt(args[0]);
    }
    if (args.length >= 2){
      host = args[1];
    }

    // create the frame
    ClientGui main = new ClientGui();

    // setup the UI to display on image
    main.newGame(1);

    //initial packet
    try {
      // open the connection
      sock = new Socket(host, port); // connect to host and socket on port given

      // get output channel
      OutputStream out = sock.getOutputStream();
      // create an object output writer (Java only)
      ObjectOutputStream os = new ObjectOutputStream(out);
      ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

      JSONObject json = new JSONObject();
      json.put("type", "init");
      os.writeObject(json.toString());
      os.flush();
      String i = (String) in.readObject();
      JSONObject req = new JSONObject(i);
      i = req.getString("data");
      String i2 = req.getString("img");
      // add images to the grid
      main.insertImage(i2, 0, 0);
      outputPanel.appendOutput(i);
      sock.close(); // close socked after sending
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // show the GUI dialog as modal
    main.show(true); // you should not have your logic after this. You main logic should happen whenever "submit" is clicked
  }
}
