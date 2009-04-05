/*
  A simple tool to run Ruby-Processing Sketches from the Processing IDE.
*/

package processing.app.tools;

import processing.app.*;
import processing.app.syntax.*;
import processing.core.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class RubyProcessingPlugin extends JPanel implements Tool, MouseInputListener {
  
  static final String[] _commands       = {"run", "watch", "help"};
  static final String[] _statuses       = {"running", "watching"};
  static final int      BUTTON_COUNT    = _commands.length;
  static final int      BUTTON_WIDTH    = 27;
  static final int      BUTTON_HEIGHT   = 32;
  static final int      BUTTON_GAP      = 5;
  static final int      IMAGE_SIZE      = 33;
  static final int      OFFSET_OVER     = 95;
  static final int      OFFSET_DOWN     = 25;
  static final int      WIDTH           = 190;
  static final int      HEIGHT          = 67;
  static final int      BAR_HEIGHT      = 17;
  static final String   WIKI_URL        = "http://wiki.github.com/jashkenas/ruby-processing";
                        
  private Editor        _editor;
  private JFrame        _frame;
  private String        _title          = "Ruby-Processing";
  private Runtime       _runtime        = Runtime.getRuntime();
  private Image         _background;
  private Image         _buttonsImage;
  private int           _currentButton  = -1;
  private Point         _dragPoint;
  private boolean       _dragging       = false;
                        
  private JButton       _runButton;
  private JButton       _watchButton;
  private JButton       _liveButton;
  
  // The name of the beast.
  public String getMenuTitle() {
    return this._title;
  }
  
  // Create the controls behind the scenes. Don't show them yet.
  public void init(Editor editor) {
    _editor = editor;
    _background = new ImageIcon(getClass().getResource("images/control_panel.png")).getImage();
    _buttonsImage = new ImageIcon(getClass().getResource("images/buttons.png")).getImage();
    _createControls();
  }
  
  // Show the control panel, sesizing based on the height of the title bar.
  public void run() {
    _frame.setVisible(true);
    _frame.setSize(WIDTH, HEIGHT + _frame.getInsets().top);
    _frame.setLocationRelativeTo(_editor);
    // Set the syntax highlighting to Ruby, and force repainting.
    _editor.getTextArea().setTokenMarker(new RubyTokenMarker());
    _editor.handleSelectAll();
    _editor.setSelection(0, 0);
  }
  
  // For now, we just delegate all commands to an installed ruby-processing gem.
  public void runCommand(String command) {
    if (command == "help") {
      Base.openURL(WIKI_URL); 
    } else {
      try {
        SketchCode code = _editor.getSketch().getCode(0);
        String name = code.getPrettyName();
        String path = code.getFile().getAbsolutePath();
        _editor.statusNotice(_title + ": " + _statuses[_currentButton] + " \"" + name + "\"");
        _runtime.exec("rp5 " + command + " " + path);
      } catch (java.io.IOException ex) {
        System.err.println(ex);
        System.exit(1);
      }
    }
  }
  
  // Get everything all set up and freshly painted.
  private void _createControls() {
    _frame = new JFrame(_title);
    _frame.setContentPane(this);
    _frame.setUndecorated(true);
		_frame.setResizable(false);
		Base.setIcon(_frame);
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    
    addMouseListener(this);
    addMouseMotionListener(this);                            
  }
  
  public boolean isOpaque() {
    return true;
  }
      
  protected void paintComponent(Graphics g) {
    g.drawImage(_background, 0, 0, null);
    
    for (int i=0; i<BUTTON_COUNT; i++) {
      Image image = createImage(BUTTON_WIDTH, BUTTON_HEIGHT);
      Graphics brush = image.getGraphics();
      int mult = i == _currentButton ? -1 : -2;
      brush.drawImage(_buttonsImage, -(i*IMAGE_SIZE) - 3, mult*IMAGE_SIZE, null);
      g.drawImage(image, OFFSET_OVER + i * (BUTTON_WIDTH), OFFSET_DOWN, null);
    }        
  }
  
  // Show the light rollovers and set the current button.
  public void handleHover(int x, int y) {
    x = x - OFFSET_OVER;
    Boolean yMiss = y < OFFSET_DOWN || y > OFFSET_DOWN + BUTTON_HEIGHT;
    Boolean xMiss = x < 0 || x > BUTTON_COUNT * BUTTON_WIDTH;
    _currentButton = xMiss || yMiss ? -1 : x / BUTTON_WIDTH;
    repaint();
  }
  
  // A barrage of mouse handlers for this and that...
  
  public void mouseMoved(MouseEvent e) { 
    handleHover(e.getX(), e.getY()); 
  }
  public void mouseDragged(MouseEvent e) {
    Point p = e.getPoint();
    if (_dragging) {
      int xDiff = p.x - _dragPoint.x;
      int yDiff = p.y - _dragPoint.y;
      Point where = _frame.getLocation();
      _frame.setLocation(where.x + xDiff, where.y + yDiff);
    }
  }
  public void mousePressed(MouseEvent e) { 
    if (_currentButton >= 0) runCommand(_commands[_currentButton]);
    Point p = e.getPoint();
    if (p.y < BAR_HEIGHT) {
      _dragging = true;
      _dragPoint = p;
    }
  }
  public void mouseReleased(MouseEvent e) {
    _dragging = false;
  }
  public void mouseClicked(MouseEvent e) {
    Point p = e.getPoint();
    // Watch out for close button clicks.
    if (p.x > 6 && p.x < 18 && p.y > 2 && p.y < 14 ) {
      _frame.setVisible(false);
    }
  }
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {
    _editor.statusEmpty();
  }
  
}