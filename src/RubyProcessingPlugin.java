/*
  A simple tool to run Ruby-Processing Sketches from the Processing IDE.
*/

package processing.app.tools;

import processing.app.*;
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
  static final int      OFFSET_DOWN     = 8;
  static final int      WIDTH           = 190;
  static final int      HEIGHT          = 50;
  static final String   WIKI_URL        = "http://wiki.github.com/jashkenas/ruby-processing";
                        
  private Editor        _editor;
  private JFrame        _frame;
  private String        _title          = "Ruby-Processing";
  private Runtime       _runtime        = Runtime.getRuntime();
  private Image         _background;
  private Image         _buttonsImage;
  private int           _currentButton  = -1;
                        
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
    _frame.setLocation(screen.width / 2, screen.height / 2);
    
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
  
  public void mouseMoved(MouseEvent e)    { handleHover(e.getX(), e.getY()); }
  public void mouseDragged(MouseEvent e)  {}
  public void mousePressed(MouseEvent e)  { 
    if (_currentButton >= 0) runCommand(_commands[_currentButton]); 
  }
  public void mouseReleased(MouseEvent e) {}
  public void mouseClicked(MouseEvent e)  {}
  public void mouseEntered(MouseEvent e)  {}
  public void mouseExited(MouseEvent e)   {
    _editor.statusEmpty();
  }
  
}


    // _runButton = new JButton("Run");
    // _runButton.setActionCommand("run");
    // _runButton.addActionListener(this);
    // _runButton.setToolTipText("Just run the sketch.");
    // 
    // _watchButton = new JButton("Watch");
    // _watchButton.setActionCommand("watch");
    // _watchButton.addActionListener(this);
    // _watchButton.setToolTipText("Run the sketch, reloading every time you save.");
    // 
    // surface.add("Center", _runButton);
    // surface.add("Center", _watchButton);