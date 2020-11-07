/***************************************************************************************/
// CLASS: 		  Tetris
// AUTHOR:		  Dæyva Hjelmstad (dhjelmstad@gmail.com)
// DESCRIPTION:  The Tetris class contains the main() method for the game, which 
//					  creates a frame and adds an instance of TetrisPanel to it. 
// LAST UPDATED: 3 Nov 2012
//
//Note: To create .jar: jar cfm Tetris.jar Manifest.txt *

//9/1 jar not playing music, new game button not working
/***************************************************************************************/
import javax.swing.JFrame;

public class Tetris
 {
 	public static void main(String[] args)
 	 {
 	 	JFrame frame = new JFrame ("Tetris");
 	 	frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
 	 	
 	 	// Creates new instance of TetrisPanel()
 	 	frame.getContentPane().add(new TetrisPanel());
 	 	
 	 	frame.pack();
 	 	frame.setVisible(true);
 	 }
 }