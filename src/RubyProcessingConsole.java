/*
  A simple console for messing with Ruby-Processing sketches, live
  in the Processing IDE.
*/

package processing.app.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.jruby.*;
import org.jruby.demo.*;
import org.jruby.internal.runtime.*;

public class RubyProcessingConsole extends JFrame {
  
  public TextAreaReadline tar;
  
  public RubyProcessingConsole(String title) {
    super(title);

    getContentPane().setLayout(new BorderLayout());
    setSize(700, 600);

    JEditorPane text = new JTextPane();

    text.setMargin(new Insets(8,8,8,8));
    text.setCaretColor(new Color(0x00, 0x00, 0xa4));
    text.setBackground(new Color(0xf2, 0xf2, 0xf2));
    text.setForeground(new Color(0x00, 0x00, 0xa4));
    Font font = findFont("Monospaced", Font.PLAIN, 11, new String[] {"Monaco", "Andale Mono"});

    text.setFont(font);
    JScrollPane pane = new JScrollPane();
    pane.setViewportView(text);
    pane.setBorder(BorderFactory.createLineBorder(Color.darkGray));
    getContentPane().add(pane);
    validate();

    tar = new TextAreaReadline(text, " Ruby-Processing >> \n\n");
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        tar.shutdown();
      }
    });
  }
  
  public RubyInstanceConfig configureConsole(RubyInstanceConfig config) {
    config.setInput(tar.getInputStream());
    config.setOutput(new PrintStream(tar.getOutputStream()));
    config.setError(new PrintStream(tar.getOutputStream()));
    // config.setObjectSpaceEnabled(true); // useful for code completion inside the IRB
    return config;
  }
  
  public void startConsole(Ruby ruby) {  
    ruby.getGlobalVariables().defineReadonly("$$", new ValueAccessor(ruby.newFixnum(System.identityHashCode(ruby))));
    //runtime.getLoadService().init(new ArrayList());
    tar.hookIntoRuntime(ruby);
    
    final Ruby runtime = ruby;
    Thread thread = new Thread() {
      public void run() {
        setVisible(true);
        runtime.evalScriptlet("require 'irb'; require 'irb/completion'; IRB.start;");
      }
    };
    thread.start();
  }
  
  private Font findFont(String otherwise, int style, int size, String[] families) {
    String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    Arrays.sort(fonts);
    Font font = null;
    for (int i = 0; i < families.length; i++) {
      if (Arrays.binarySearch(fonts, families[i]) >= 0) {
        font = new Font(families[i], style, size);
        break;
      }
    }
    if (font == null)
      font = new Font(otherwise, style, size);
    return font;
  }
  
  /**
  *
  */
  private static final long serialVersionUID = 3746242973444417387L;
  
}












// package processing.app.tools;
// 
// import org.jruby.demo.*;
// 
// import java.awt.BorderLayout;
// import java.awt.Color;
// import java.awt.Font;
// import java.awt.GraphicsEnvironment;
// import java.awt.Insets;
// import java.awt.event.WindowAdapter;
// import java.awt.event.WindowEvent;
// import java.io.PrintStream;
// import java.util.ArrayList;
// import java.util.Arrays;
// 
// import javax.swing.BorderFactory;
// import javax.swing.JEditorPane;
// import javax.swing.JFrame;
// import javax.swing.JScrollPane;
// import javax.swing.JTextPane;
// 
// import org.jruby.Ruby;
// import org.jruby.RubyInstanceConfig;
// import org.jruby.internal.runtime.ValueAccessor;
// 
// public class RubyProcessingConsole extends JFrame {
//     public RubyProcessingConsole(String title) {
//         super(title);
//     }
// 
//     public static void main(final String[] args) {
//         final RubyProcessingConsole console = new RubyProcessingConsole("JRuby IRB Console");
// 
//         console.getContentPane().setLayout(new BorderLayout());
//         console.setSize(700, 600);
// 
//         JEditorPane text = new JTextPane();
// 
//         text.setMargin(new Insets(8,8,8,8));
//         text.setCaretColor(new Color(0xa4, 0x00, 0x00));
//         text.setBackground(new Color(0xf2, 0xf2, 0xf2));
//         text.setForeground(new Color(0xa4, 0x00, 0x00));
//         Font font = console.findFont("Monospaced", Font.PLAIN, 14,
//                 new String[] {"Monaco", "Andale Mono"});
// 
//         text.setFont(font);
//         JScrollPane pane = new JScrollPane();
//         pane.setViewportView(text);
//         pane.setBorder(BorderFactory.createLineBorder(Color.darkGray));
//         console.getContentPane().add(pane);
//         console.validate();
// 
//         final TextAreaReadline tar = new TextAreaReadline(text, " Welcome to the JRuby IRB Console \n\n");
//         console.addWindowListener(new WindowAdapter() {
//             public void windowClosing(WindowEvent e) {
//                 tar.shutdown();
//             }
//         });
// 
//         final RubyInstanceConfig config = new RubyInstanceConfig() {{
//             setInput(tar.getInputStream());
//             setLoader(this.getClass().getClassLoader());
//             setOutput(new PrintStream(tar.getOutputStream()));
//             setError(new PrintStream(tar.getOutputStream()));
//             setObjectSpaceEnabled(true); // useful for code completion inside the IRB
//             setArgv(args);
//         }};
//         final Ruby runtime = Ruby.newInstance(config);
// 
//         runtime.getGlobalVariables().defineReadonly("$$", new ValueAccessor(runtime.newFixnum(System.identityHashCode(runtime))));
//         runtime.getLoadService().init(new ArrayList());
// 
//         tar.hookIntoRuntime(runtime);
// 
//         Thread t2 = new Thread() {
//             public void run() {
//                 console.setVisible(true);
//                 runtime.evalScriptlet("require 'irb'; require 'irb/completion'; IRB.start");
//             }
//         };
//         t2.start();
// 
//         //try {
//         //    t2.join();
//         //} catch (InterruptedException ie) {
//             // ignore
//         //}
// 
//         //System.exit(0);
//     }
// 
//     private Font findFont(String otherwise, int style, int size, String[] families) {
//         String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
//         Arrays.sort(fonts);
//         Font font = null;
//         for (int i = 0; i < families.length; i++) {
//             if (Arrays.binarySearch(fonts, families[i]) >= 0) {
//                 font = new Font(families[i], style, size);
//                 break;
//             }
//         }
//         if (font == null)
//             font = new Font(otherwise, style, size);
//         return font;
//     }
// 
//     /**
//      *
//      */
//     private static final long serialVersionUID = 3746242973444417387L;
// 
// }
