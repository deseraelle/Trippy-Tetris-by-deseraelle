/***************************************************************************************/
// CLASS: 		  TetrisPanel
// AUTHOR: 		  dævyd hjelmstad (dhjelmstad@gmail.com)
// DESCRIPTION:  The TetrisPanel class sets up the GUI for the game and handles all
//					  of the game logic. 
// LAST UPDATED: 6 Nov 2020
/***************************************************************************************/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;
import javax.swing.Timer;
import java.util.*;
import java.lang.Math.*;
import javax.sound.sampled.*;
import java.io.*;
import java.lang.*;


public class TetrisPanel extends JPanel
 {
 	/**INSTANCE VARIABLES**/
 	private static final int GRID_SIZE = 30;
 	private static final int WIDTH = 12;
 	private static final int HEIGHT = 25;
 	private static final int LEFT_BUFFER = 2;
 	private static final int RIGHT_BUFFER = 10;
 	private static final int WINDOW_WIDTH = (WIDTH+LEFT_BUFFER+RIGHT_BUFFER)*GRID_SIZE; 
 	private static final int WINDOW_HEIGHT = HEIGHT*GRID_SIZE;
 	
 	private static final int SCORE_FONT = 20;
 	private static final int INSTRUCTION_FONT = 15;
 	
 	private boolean gameOver;
 	private boolean GAMEON;
 	private boolean paused;
 	private int score, numLines, delay;
 	
 	//Background image
 	private String BACKGROUND_PATH = "/resources/Mountains.JPG";
 	private ImageIcon BACKGROUNDicon = new ImageIcon(getClass().getResource(BACKGROUND_PATH));
 	private Image BACKGROUND = BACKGROUNDicon.getImage();
 	
 	//Music
 	private Timer musictimer;
 	private MusicListener musicTimerListener;
 	private AudioInputStream ais = null;
 	private Clip clip = null;
 	private int SONG_LENGTH = (5*60+36)*1000;
 	

 	// TetrisBoard contains data for the current piece, while TETRIS_BOARD holds the
 	// data for the 'permanent' blocks (hence the all-caps notation) which are at rest at 
 	// the bottom of the screen. The BlockColor array contains the data about the color
 	// of the blocks in both TetrisBoard and TETRIS_BOARD
 	private boolean[][] TetrisBoard, TETRIS_BOARD;
 	private Color[][] BlockColor;
 	
 	private TetrisPiece currentpiece, nextpiece;
 	
 	// Used to center the display of nextpiece in the side bar
 	private int nextpieceOFFSET_X, nextpieceOFFSET_Y;
 	
 	private Timer timer;
 	private TimerListener timerlistener;
 	
 	private JLabel scoreLabel, linesLabel, pausedLabel, instructionLabel, gameoverLabel, titleLabel;
 	private Random rnd;
 	
 	private JButton start;
 	private startListener starter;
 	

 	
 	/**METHODS**/
 	public TetrisPanel()
 	 {
 	 	// Set dimensions of the window and initialize instance variables
  	 	setPreferredSize(new Dimension (WINDOW_WIDTH,WINDOW_HEIGHT));

 	 	scoreLabel = new JLabel("Score: 0");
 	 	linesLabel = new JLabel("Number of Lines: 0"); 
 	 	pausedLabel = new JLabel("");
 	 	gameoverLabel = new JLabel("GAME OVER");
 	 	instructionLabel = new JLabel("<HTML>Use arrow keys to move side to side. 'Z' and 'X' rotate pieces, and Space Bar is hard drop. Press 'P' to pause, 'N' for a new game. <BR>1 Line- 100 pts<BR>2 Lines- 200pts<BR>3 Lines- 400 pts<BR>4 Lines-800 pts</HTML>");
 	 	titleLabel = new JLabel("<HTML>TRIPPY TETRIS<BR>by dhjelmstad</HTML>");
 	 	titleLabel.setFont(new Font("Sans-Serif", Font.ITALIC, 33));
 	 	titleLabel.setForeground(Color.WHITE);
 	 	titleLabel.setBounds((LEFT_BUFFER+WIDTH/2)*GRID_SIZE-4*GRID_SIZE+10, GRID_SIZE, 8*GRID_SIZE, 5*GRID_SIZE);
 	 	
 	 	// Create timer, add timerListener
 	 	timerlistener = new TimerListener();
 	 	timer = new Timer(getDelay(), timerlistener);
 	 	
 	 	// Add ControllerListener and give the program the keyboard focus
 	 	addKeyListener(new ControllerListener());
 	 	requestFocus();
 	 	
 	 	reset();
 	 		
 	 }
 	
 	
 	private void reset()
 	 {
 	 	add(titleLabel);
 	 	gameOver = false;
 	 	GAMEON = false;
 	 	paused = false;
 	 	score = 0;
 	 	numLines = 0;
 	 	TetrisBoard = new boolean[HEIGHT][WIDTH];
 	 	TETRIS_BOARD = new boolean[HEIGHT][WIDTH];
 	 	BlockColor = new Color[HEIGHT][WIDTH];
 	 	
 	 	// Initialize game board to be empty with a boundary of black blocks. These blocks
 	 	// will never change color or position
 	 	for(int i=0; i<HEIGHT; i++)
 	 	 {
 	 	 	for(int j=0; j<WIDTH; j++)
 	 	 	 {
 	 	 	 	if (j==0 || j==(WIDTH-1) || i==(HEIGHT-1))
 	 	 	 	 {
 	 	 	 		TETRIS_BOARD[i][j] = true;
 	 	 	 		BlockColor[i][j] = new Color(50,50,50);
 	 	 	 	 }	
 	 	 	 	else
 	 	 	 		TETRIS_BOARD[i][j] = false;
 	 	 	 	
 	 	 	 	TetrisBoard[i][j]=false;		
 	 	 	 }
 	 	 }
 	 	
 	 	// Initialize first two pieces:
 	 	rnd = new Random();
 	 	currentpiece = new TetrisPiece(rnd.nextInt(7),0,5);
 	 	nextpiece = new TetrisPiece(rnd.nextInt(7),0,5);
 	 	
 	 	repaint();
 	 	 
 	 	// Music timer
 	 	musicTimerListener = new MusicListener();
 	 	musictimer = new Timer(SONG_LENGTH, musicTimerListener);
 	 	musictimer.setInitialDelay(0);
 	 	
 	 	
 	 	// Start Button
 	 	setLayout(null);
 	 	start = new JButton("PUSH 2 START");
 	 	starter = new startListener();
 	 	start.addActionListener(starter);
 	 	start.setSize(new Dimension(4*GRID_SIZE, 2*GRID_SIZE));
		start.setBounds( (LEFT_BUFFER+WIDTH/2)*GRID_SIZE-start.getWidth()/2, (HEIGHT/4)*GRID_SIZE, 4*GRID_SIZE, 2*GRID_SIZE);
 	 	
 	 	timer.stop();
 	 	add(start);
 	 }
 	 
 	 private class startListener implements ActionListener
 	 {
 	 	public void actionPerformed(ActionEvent e)
 	 	 {
 	 	 	remove(titleLabel);
 	 	 	musictimer.start();
 	 	 	GAMEON = true;
 	 		timer.start();
 	 		remove(start);
 	 	 }
 	 }
 	
 	 
 	// The TimerListener class contains the main game loop
 	private class TimerListener implements ActionListener
 	 {
 	 	public void actionPerformed(ActionEvent e)
 	 	 {
			if(!gameOver)
			 {	
				if (currentpiece == null)
				 {
					currentpiece = nextpiece;
					nextpiece = new TetrisPiece(rnd.nextInt(7),0,5);
					timer.setDelay(getDelay());
				 }
				else
				 {
					lineDown();
				 }
				removeLines(); 
				refresh();	
			 }
			else
			 {
			 	clip.stop();
			 	timer.stop();
			 	repaint();
			 }
			 // try {
			 // 	Thread.sleep(100);
			 // } catch(InterruptedException exept)
			 // {}	
 	 	 }
 	 } 
 	   
 	// The legalMove method compares compares the position of currentpiece in TetrisBoard 
 	// with the 'permanent' blocks in TETRIS_BOARD. If there is an overlap, the method 
 	// returns false.
 	private boolean legalMove(int pRow, int pColumn)
 	 {
 	 	for(int i=0; i<5; i++)
 	 	 {
 	 	 	for(int j=0; j<5; j++)
 	 	 	 {
 	 			if(pRow+i>1 && pColumn+j>1)
 	 	 		 {
 	 				if ((currentpiece.getPieceArray())[i][j]==1 && TETRIS_BOARD[pRow+i-2][pColumn+j-2])
 	    			 {	
 	 	 				return false;
 	 	 			 }
 	 	 		 }	 
 	 	 	 }
 	 	 }
 	 	return true; 	
 	 }
 	
 	// The lineDown method checks if there is a block directly below currentpiece. If there
 	// isn't, currentpiece is moved down one row. If there is, the blocks of currentpiece
 	// are added to TETRIS_BOARD and cleared from TetrisBoard, and nextpiece becomes  
 	// currentpiece. If the new piece has no room to fall, gameOver is set to true. If 
 	// there is room to fall, the game speed is set and nextpiece is set to a random piece.
 	private void lineDown()
 	 {
 	 	if(legalMove(currentpiece.getRow()+1,currentpiece.getColumn()))
 	 	 {
 	 	 	prepareToMove();
 	 	 	currentpiece.fall();
 	 	 }
 	 	else
 	 	 {
 	 	 	for(int i=0; i<5; i++)
 	 	 	 {
 	 	 		for(int j=0; j<5; j++)
 	 	 	 	 {
 	 				if(currentpiece.getRow()+i>1 && currentpiece.getColumn()+j>1)
 	 	 			 {
 	 					if ((currentpiece.getPieceArray())[i][j]==1)
 	    				 {	
 	 	 					TETRIS_BOARD[currentpiece.getRow()+i-2][currentpiece.getColumn()+j-2]=true;
 	 	 			 	 }
 	 	 		 	 }	 
 	 	 		 }
 	 		 }
 	 		prepareToMove(); // Clears the piece from the TetrisBoard array 
 	 	 	currentpiece = nextpiece;
 	 	 	if (!legalMove(currentpiece.getRow()+1,currentpiece.getColumn()))
						gameOver = true;
 	 	 	nextpiece = new TetrisPiece(rnd.nextInt(7),0,5);
 	 	 	timer.setDelay(getDelay());
 	 	 } 
 	 }
 	
 	// prepareToMove clears the blocks of currentpiece from the TetrisBoard array. 
 	private void prepareToMove()
 	 {
 	 	for(int i=0; i<5; i++)
 	 		 {
 	 	 		for(int j=0; j<5; j++)
 	 	 		 {
 	 				if(currentpiece.getRow()+i>1 && currentpiece.getColumn()+j>1)
 	 	 		 	 {
 	 					if ((currentpiece.getPieceArray())[i][j]==1)
 	    			 	 {	
 	 	 						TetrisBoard[currentpiece.getRow()+i-2][currentpiece.getColumn()+j-2]=false;
 	 	 			 	 }
 	 	 			 }	 
 	 	 		 }
 	 	 	 }	 	 
 	 }
 	
	// The removeLines method checks for completed lines. If there are any, they are 
	// removed and all blocks above are shifted down, and the score and numLines are incremented 
	// accordingly. (1 line = 100pts, 2 lines = 200pts, 3 lines = 400pts, 4 lines = 800pts)
	private void removeLines()
	 {
	 	int sum = 0;
	 	int linesthisturn = 0;
	 	for (int i=1; i<HEIGHT-1; i++)
	 	 {
	 	 	for (int j=1; j<WIDTH-1; j++)
	 	 	 {
	 	 	 	sum += TETRIS_BOARD[i][j]?1:0;
	 	 	 }
	 	 	if (sum == WIDTH-2)
	 	 	 {
	 	 	 	for (int n=i;  n>=0; n--)
	 	 	 	 {
					for (int j=1; j<WIDTH-1; j++)
					 {
						if (n>0)
						 {	
							TETRIS_BOARD[n][j]=TETRIS_BOARD[n-1][j];
							BlockColor[n][j] = BlockColor[n-1][j];
						 }	
						else
							TETRIS_BOARD[n][j]=false;	
					 }
				 
	 	 	 	 }
	 	 	 	refresh();

	 	 	 	numLines++;
	 	 	 	linesthisturn++;
	 	 	 } 
	 	 	sum = 0; 
	 	 }
	 	if(linesthisturn!=0) score += 100*Math.pow(2,linesthisturn-1);
	 }

 	// The refresh method handles updates TetrisBoard and BlockColor to the current 
 	// position/color of currentpiece
 	private void refresh()
 	 {
 	 	 	for(int i=0; i<5; i++)
 	 		 {
 	 	 		for(int j=0; j<5; j++)
 	 	 		 {
 	 	 		  if(currentpiece.getRow()+i>1 && currentpiece.getColumn()+j>1)
 	 	 		 	{
 	 	 			 	if ((currentpiece.getPieceArray())[i][j]==1)
 	 	 	 		  	 {	
 	 	 	 		 		TetrisBoard[currentpiece.getRow()+i-2][currentpiece.getColumn()+j-2]=true;
 	 	 	  	 			BlockColor[currentpiece.getRow()+i-2][currentpiece.getColumn()+j-2]=currentpiece.getColor();
 	 	 	  	 	  	 }		
 	 	 	  	 	} 
 	 	 		 }	
 	 	 	} 
 	 	 	repaint();  
 	 }
 	

 	 private void loadScores()
 	 {
 	 	BufferedReader fileReader = null;
 	 	scoreData = new Object[HIGH_SCORE_LENGTH][2];
 	 	Object columns[] = {"name", "score"};
 	 	score_count = 0;
 	 	String tmp = "";
 	 	try{
 	 		String line = "";
 	 		fileReader = new BufferedReader(new FileReader(file));
 	 		fileReader.readLine();
 	 		while ((line = fileReader.readLine()) != null){
				String[] tokens = line.split(CSV_DELIMITER);
 	 			scoreData[score_count] = tokens;
 	 			score_count++;
 	 		}
 	 		fileReader.close();
 	 	} catch (IOException ioe){
 	 		ioe.printStackTrace();

 	 	}
 	 	Object [][] data = new Object[score_count][2];
 	 	for(int i=0; i<score_count; i++){
 	 		data[i][0] = scoreData[i][0];
 	 		tmp = "" + scoreData[i][1];
 	 		data[i][1] = Integer.parseInt(tmp);
 	 	}

 	 	dt_model = new DefaultTableModel(data, new String[] {
 	 			"name", "score" }) {
 	 		Class[] types = {String.class, Integer.class};
 	 		boolean[] canEdit = {true, true};

 	 		@Override
 	 		public Class getColumnClass(int columnIndex) {
 	 			return this.types[columnIndex];
 	 		}

 	 		public boolean isCellEditable(int columnIndex) {
 	 			return this.canEdit[columnIndex];
 	 		}
 	 	};

 	 	scoreRecord = new JTable(dt_model);
 	 	scoreRecord.setFillsViewportHeight(true);

 	 	TableRowSorter<TableModel> sorter = new TableRowSorter<>(scoreRecord.getModel());
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		int columnIndexToSort = 1;
		sortKeys.add(new RowSorter.SortKey(columnIndexToSort, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		scoreRecord.setRowSorter(sorter);
 	 	scrollPane = new JScrollPane(scoreRecord);

 	 }

 	 // Write score data to score_record.csv
	private boolean exportToCSV(JTable tableToExport, String pathToExportTo) {

	    try {

	        file.setWritable(true);
	        FileWriter csv = new FileWriter(file);


	        for (int i = 0; i < dt_model.getColumnCount(); i++) {
	            csv.write(dt_model.getColumnName(i) + ",");
	        }

	        csv.write("\n");

	        for (int i = 0; i < score_count; i++) {
	            for (int j = 0; j < dt_model.getColumnCount(); j++) {
	                csv.write(dt_model.getValueAt(i, j).toString() + ",");
	            }
	            csv.write("\n");
	        }
	        csv.flush();
	        csv.close();
	        return true;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return false;
	}    



 	// ControllerListener handles all of the controls for the game
 	private class ControllerListener implements KeyListener
	 {
      // Holding down the DOWN arrow increases the speed of a falling piece, and when
      // it is released the piece resumes its original speed
      public void keyReleased(KeyEvent e) 
       {
      	switch(e.getKeyCode())
      	 {
      	 	case KeyEvent.VK_DOWN:
      	 		timer.setDelay(getDelay());
      	 		break;
      	 }		
      
       }

      public void keyTyped(KeyEvent e) {}

      public void keyPressed(KeyEvent e)
        {
      	switch(e.getKeyCode())
          {
         	// LEFT ARROW: Moves currentpiece one column to the left, if it is a legal move
         	case KeyEvent.VK_LEFT:
					if (legalMove(currentpiece.getRow(),currentpiece.getColumn()-1) && !paused && !gameOver)
					 {	
						prepareToMove();
						currentpiece.moveLeft();
						refresh();
						repaint();
					 }	
					break;
         	
         	// RIGHT ARROW: Moves currentpiece one column to the right, if it is a legal move
         	case KeyEvent.VK_RIGHT:
					if (legalMove(currentpiece.getRow(),currentpiece.getColumn()+1) && !paused && !gameOver)
					 {	
						prepareToMove();
						currentpiece.moveRight();
						refresh();
						repaint();
         		 }
         		break;
         		
         	// DOWN ARROW: Speeds up the falling of currentpiece while held down
         	case KeyEvent.VK_DOWN:
         		if (!paused && !gameOver)	
         			timer.setDelay(25);
         		break;
         			
         	// Z: Rotates currentpiece to the left if it is legal
         	case KeyEvent.VK_Z:
         		if (!paused && !gameOver)
         		 {
						prepareToMove();
						currentpiece.rotateRight();
						
						// If the rotation is illegal, rotate immediately back to previous position
						for(int i=0; i<5; i++)
						 {
							for(int j=0; j<5; j++)
							 {
								if(currentpiece.getRow()+i>1 && currentpiece.getColumn()+j>1)
								 {
									if ((currentpiece.getPieceArray())[i][j]==1 && TETRIS_BOARD[currentpiece.getRow()+i-2][currentpiece.getColumn()+j-2])
									 {	
										currentpiece.rotateLeft();
									 }
								 }	 
							 }
						 }
						refresh();
						repaint();
					 }	
         		break;
         		
         	// X: Rotates currentpiece to the right if it is legal	
         	case KeyEvent.VK_X:
         		if (!paused && !gameOver)
         		 {
						prepareToMove();
						currentpiece.rotateLeft();
						
						// If the rotation is illegal, rotate immediately back to previous position
						for(int i=0; i<5; i++)
						 {
							for(int j=0; j<5; j++)
							 {
								if(currentpiece.getRow()+i>1 && currentpiece.getColumn()+j>1)
								 {
									if ((currentpiece.getPieceArray())[i][j]==1 && TETRIS_BOARD[currentpiece.getRow()+i-2][currentpiece.getColumn()+j-2])
									 {	
										currentpiece.rotateRight();
									 }
								 }	 
							 }
						 }
						refresh();
						repaint();
					 }	
         		break;
         		
         	// SPACE BAR: 'Instantly' drops currentpiece to bottom of the board
         	case KeyEvent.VK_SPACE:
         		if(!paused && !gameOver)	
         			timer.setDelay(0);
         		break;
         		
         	// P: pauses the game
         	case KeyEvent.VK_P:
         		if (!gameOver && GAMEON)
         		 {
						paused = !paused;
						if (paused)
							timer.stop();
						else
							timer.start();
						repaint();
					 }		
         		break;
         	case KeyEvent.VK_N:
         		clip.stop();
         		remove(gameoverLabel);
         		JFrame frame = new JFrame ("Tetris");
 	 			frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
 	 	
 	 			// Creates new instance of TetrisPanel()
 	 			frame.getContentPane().add(new TetrisPanel());
 	 	
 	 			frame.pack();
 	 			frame.setVisible(true);
         		JComponent comp = (JComponent) e.getSource();
  				Window win = SwingUtilities.getWindowAncestor(comp);
  				win.dispose();
         		break;					
          }	
        }
    }
 	
 	// The getDelay method calculates the game speed based on the number of lines the
 	// player has already cleared. The calculation is done so that the game speeds up
 	// fast enough, but doesn't become negative. If the game speed were purely linear,
 	// the value of delay would become negative after a certain number of lines. The
 	// logarithm term, which takes effect after game speed has been doubled, prevents this.  
 	private int getDelay()
 	 {
 	 	if (numLines<100)
 	 		return 600-3*numLines;
 	 	else
 	 		return 300-42*(((int) Math.log(.5*numLines+2))-2);	
 	 }
 	 
 	// Overrides JPanel's paintComponent method. All positions with a 'True' value in 
 	// TetrisBoard and TETRIS_BOARD are drawn on the board with the corresponding color in
 	// BlockColor. The next piece is displayed in a box on the right hand side bar. 
 	// The score, number of lines, and instructions are printed on the right hand side bar
 	// as well. If the game is paused, a message is displayed that says so. If the game
 	// is over, GAME OVER is displayed. 
 	public void paintComponent (Graphics page)
 	 {
 	 	super.paintComponent(page);
 	 	
 	 	//JLabel backgroundLabel = new JLabel(BACKGROUND);
 	 	//backgroundLabel.setBounds(LEFT_BUFFER*GRID_SIZE, 0, WIDTH*GRID_SIZE, HEIGHT*GRID_SIZE);
 	 	//add(backgroundLabel);
 	 	page.drawImage(BACKGROUND, (LEFT_BUFFER)*GRID_SIZE, 0, (WIDTH)*GRID_SIZE, HEIGHT*GRID_SIZE, null);
 	 	
 	 	for(int i=0; i<HEIGHT; i++)
 	 	 {
 	 	 	for(int j=0; j<WIDTH; j++)
 	 	 	 {
 	 	 	 	if (TetrisBoard[i][j] || TETRIS_BOARD[i][j])
 	 	 	 	{
 	 	 	 		page.setColor(BlockColor[i][j]);
 	 	 	 		page.fillRect((LEFT_BUFFER+j)*GRID_SIZE+1,i*GRID_SIZE+1,GRID_SIZE-1,GRID_SIZE-1);
 	 	 	 	}
 	 	 	 }
 	 	 }
 	 	scoreLabel.setText("Score: "+score);
 	 	linesLabel.setText("Number of Lines: "+numLines);
 	 	if (paused)
 	 	 	pausedLabel.setText("PAUSED");
 	 	else
 	 		pausedLabel.setText("");
 	 	scoreLabel.setBounds((LEFT_BUFFER+WIDTH+1)*GRID_SIZE, 7*GRID_SIZE, 10*GRID_SIZE, 2*GRID_SIZE);
 	 	scoreLabel.setFont(new Font("Serif", Font.BOLD, SCORE_FONT));
 	 	linesLabel.setBounds((LEFT_BUFFER+WIDTH+1)*GRID_SIZE, 8*GRID_SIZE, 10*GRID_SIZE, 2*GRID_SIZE);
 	 	linesLabel.setFont(new Font("Serif", Font.PLAIN, SCORE_FONT));
 	 	pausedLabel.setBounds((LEFT_BUFFER+WIDTH/2)*GRID_SIZE-pausedLabel.getWidth()/2, 3*GRID_SIZE, 5*GRID_SIZE, 2*GRID_SIZE);
 	 	pausedLabel.setFont(new Font("Serif", Font.BOLD, 35));
 	 	pausedLabel.setForeground(Color.WHITE);
 	 	instructionLabel.setBounds((LEFT_BUFFER+WIDTH)*GRID_SIZE+10, (HEIGHT-6)*GRID_SIZE, 9*GRID_SIZE, 7*GRID_SIZE);
 	 	instructionLabel.setFont(new Font("Serif",Font.ITALIC, INSTRUCTION_FONT));
 	 	add(scoreLabel);
 	 	add(linesLabel);
 	 	add(instructionLabel);
 	 	add(pausedLabel);
 	 	if (gameOver)
 	 	 {	
 	 	 	gameoverLabel.setBounds((LEFT_BUFFER+WIDTH/2)*GRID_SIZE-gameoverLabel.getWidth()/2+5, (HEIGHT/4)*GRID_SIZE, 10*GRID_SIZE, 10*GRID_SIZE);
 	 		gameoverLabel.setFont(new Font("Serif", Font.BOLD, 45));
 	 		gameoverLabel.setForeground(Color.WHITE);
 	 	 	add(gameoverLabel);
 	 	 }	
 	 	
 	 	// Display nextpiece
 	 	page.drawRect((LEFT_BUFFER+WIDTH+1)*GRID_SIZE, GRID_SIZE, 7*GRID_SIZE, 4*GRID_SIZE);
 	 	if (nextpiece.getType()==2 || nextpiece.getType()==6) 
 	 		nextpieceOFFSET_Y = 1;
 	 	else 
 	 		nextpieceOFFSET_Y = 0;
 	 	if (nextpiece.getType()==6 || nextpiece.getType()==4 || nextpiece.getType()==5)
 	 	 	nextpieceOFFSET_X = 1;
 	 	else
 	 		nextpieceOFFSET_X = 0; 	
 	 	for(int i=0; i<5; i++)
 	 	 {
 	 	 	for(int j=0; j<5; j++)
 	 	 	 {
 	 	 	 	if (nextpiece.getPieceArray()[i][j]==1)
 	 	 	 	 {
 	 	 	 	 	page.setColor(nextpiece.getColor());
 	 	 	 	 	page.fillRect((LEFT_BUFFER+WIDTH+1+nextpieceOFFSET_X+j)*GRID_SIZE+1,(i+nextpieceOFFSET_Y)*GRID_SIZE+1,GRID_SIZE-1,GRID_SIZE-1);
 	 	 	 	 }
 	 	 	 }
 	 	 }
 	 }

private class MusicListener implements ActionListener
 	 {
 	 	public void actionPerformed(ActionEvent e)
 	 	 {
			try {
				 ais = AudioSystem.getAudioInputStream(TetrisPanel.class.getResourceAsStream("/resources/oldirty.wav"));
				 clip = AudioSystem.getClip();
				clip.open(ais);
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			 } catch (Exception ex) {};
			
		}
 	 }
 	  	 
 	// Gives focus to keyboard
 	public boolean isFocusable() {
        return true;
      }  
 }