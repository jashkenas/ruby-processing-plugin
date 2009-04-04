package processing.app.tools;

import processing.app.*;
import processing.core.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class RubyProcessingPlugin implements Tool, ActionListener {
  
  private Editor _editor;
  private JFrame _frame; 
  private String _title = "Ruby-Processing";
  private Runtime _runtime = Runtime.getRuntime();
  
  private JButton _runButton;
  private JButton _watchButton;
  private JButton _liveButton;
  
  // What this Tool is called.
  public String getMenuTitle() {
    return this._title;
  }
  
  public void init(Editor editor) {
    _editor = editor;
    _createControls();
  }
    
  public void run() {
    _frame.setVisible(true);
  }
  
  public void actionPerformed(ActionEvent e) {
    try {
      SketchCode code = _editor.getSketch().getCode(0);
      String path = code.getFile().getAbsolutePath();
      String command = "rp5 " + e.getActionCommand() + " " + path;
      _runtime.exec(command);
    } catch (java.io.IOException ex) {
      System.err.println(ex);
      System.exit(1);
    }
  }
  
  private void _createControls() {
    _frame = new JFrame(this._title);
    _frame.setSize(200, 400);
    Container content = _frame.getContentPane();
    content.setLayout(new BorderLayout());
    
    URL ashurstURL = getClass().getResource("images/ashurst.png");
    final ImageIcon ashurst = new ImageIcon(ashurstURL);
    
    class RubySurface extends JPanel {
      public boolean isOpaque() {
        return true;
      }
      
      protected void paintComponent(Graphics g) {
        g.drawImage(ashurst.getImage(), 0, 0, ashurst.getImageObserver());
      }
    }
    
    RubySurface surface = new RubySurface();
    
    _runButton = new JButton("Run");
    _runButton.setActionCommand("run");
    _runButton.addActionListener(this);
    _runButton.setToolTipText("Just run the sketch.");
    
    _watchButton = new JButton("Watch");
    _watchButton.setActionCommand("watch");
    _watchButton.addActionListener(this);
    _watchButton.setToolTipText("Run the sketch, reloading every time you save.");
    
    surface.add("Center", _runButton);
    surface.add("Center", _watchButton);
    content.add(surface);
         
    Dimension size = _frame.getSize();
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    _frame.setLocation((screen.width - size.width) / 2,
                       (screen.height - size.height) / 2);
                            
    Base.setIcon(_frame);
  }
  
}