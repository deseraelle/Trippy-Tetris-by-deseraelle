/***************************************************************************************/
// CLASS: 		  TetrisPanel
// AUTHOR:		  Dæyva Hjelmstad (dhjelmstad@gmail.com)
// DESCRIPTION:  The TetrisPiece class defines the possible tetris shapes by representing 
// 				  them as a 5x5 array. The methods necessary for rotation and movement 
// 				  around the board are also defined. Each piece is identified by an 
//					  integer between 0 and 6:
//							 0 - Square
//							 1 - Line
//							 2 - forwards 'L'
// 						 3 - backwards 'L'
//							 4 - backwards 'Z'
//							 5 - forwards 'Z'
//							 6 - 'T' 
// LAST UPDATED: 29 Nov 2012
/***************************************************************************************/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TetrisPiece
 {
	/**INSTANCE VARIABLES**/
	
	private int pieceType;
	private int row,column;
	private Color pieceColor;
	
	private int[][] pieceArray;
	
	 
	/**METHODS**/
	
	// Constructor takes the piece type and position. Color is determined by piece type
	public TetrisPiece(int pType, int pRow, int pColumn)
	 {
	 	row=pRow;
	 	column=pColumn;
	 	pieceType = pType;
	 	
	 	// SQUARE- yellow
	 	if (pieceType==0)
	 	 { 
	 	 	int[][] temp = {{0,0,0,0,0},{0,0,0,0,0},{0,0,1,1,0},{0,0,1,1,0},{0,0,0,0,0}};
	 	 	pieceArray = temp; 
	 	 	pieceColor = new Color(255,215,0);		
	 	 }
	 	
	 	// LINE- cyan
	 	else if(pieceType==1)
	 	 {
	 	 	int[][] temp = {{0,0,0,0,0},{0,0,0,0,0},{0,1,1,1,1},{0,0,0,0,0},{0,0,0,0,0}};
	 	 	pieceArray = temp; 
	 	 	pieceColor = Color.cyan;	
	 	 }
	 	
	 	// FORWARDS 'L'- orange
	 	else if(pieceType==2)
	 	 {
	 	 	int[][] temp = {{0,0,0,0,0},{0,0,1,0,0},{0,0,1,1,1},{0,0,0,0,0},{0,0,0,0,0}};
	 	 	pieceArray = temp;
	 	 	pieceColor = new Color(255,140,0);	 	
	 	 	
	 	 }
	 	
	 	// BACKWARDS 'L'- blue
	 	else if(pieceType==3)
	 	 {  
	 	 	int[][] temp = {{0,0,0,0,0},{0,0,0,0,0},{0,0,1,1,1},{0,0,1,0,0},{0,0,0,0,0}};
	 	 	pieceArray = temp; 
	 	 	pieceColor = Color.blue;
	 	 }			
	 	
	 	// BACKWARDS 'Z'- green
	 	else if(pieceType==4)
	 	 {
	 	 	int[][] temp = {{0,0,0,0,0},{0,0,0,0,0},{0,0,1,1,0},{0,1,1,0,0},{0,0,0,0,0}};
	 	 	pieceArray = temp; 	
	 	 	pieceColor = new Color(0,160,0);	
	 	 }
	 	
	 	// FORWARDS 'Z'- red
	 	else if (pieceType==5)
	 	 {
	 	   	int[][] temp = {{0,0,0,0,0},{0,0,0,0,0},{0,1,1,0,0},{0,0,1,1,0},{0,0,0,0,0}};
	 	   	pieceArray = temp; 	
	 	 	pieceColor = Color.red;	
	 	 }
	 	
	 	// 'T' SHAPE- purple
	 	else if (pieceType==6)
	 	 {
	 	 	int[][] temp = {{0,0,0,0,0},{0,0,1,0,0},{0,1,1,1,0},{0,0,0,0,0},{0,0,0,0,0}};
	 	 	pieceArray = temp;
	 	 	Color purple = new Color(138,43,226);	
	 	 	pieceColor = purple; 	
	 	 }
	 	
	 	// null shape
	 	else 
	 	 { 
	 	 	int[][] temp = new int[5][5];
	 	 	pieceArray = temp; 											
	 	 }
	 	 
	 } 
	 
	// Moves the piece down one row
	public void fall() 
	 {
	 	row += 1;
	 }
	 
	// Moves the piece one column to the left
	public void moveLeft()
	 {
	 	column -= 1;
	 } 
	 
	// Moves the piece one column to the right
	public void moveRight()
	 {
	 	column += 1;
	 } 
	 
	// Returns the 5x5 array representation of the piece
	public int[][] getPieceArray()
	 {
	 	return pieceArray;
	 }
	 
	// Returns the int that represents the piece's type
	public int getType()
	 {
	 	return pieceType;
	 }
	
	// Returns the color of the piece
	public Color getColor()
	 {
	 	return pieceColor;
	 }
	 
	// Returns the column of the piece
	public int getColumn()
	 {
	 	return column;
	 } 
	
	// Returns the row of the piece
	public int getRow()
	 {
	 	return row;
	 } 
	
	// Rotates the piece to the right by taking the transpose of the array then swapping
	// jth row with the (4-j)th row  
 	public void rotateRight()
 	 {
		if (pieceType != 0)  // The square does not need to be rotated
		 {	
			int[][] temp = new int[5][5];
			for(int i=0; i<5; i++)
			 {
				for(int j=0; j<5; j++)
				 {
					temp[i][j]=pieceArray[j][i];
				 }
			 } 
			
			for(int i=0; i<5; i++)
			 {
				for(int j=0; j<5; j++)
				 {
					pieceArray[i][j]=temp[i][4-j];
				 }
			 }
		 }	 
 	 	 
 	 }
 	
 	// Rotates the piece to the left by taking the transpose of the array then swapping
 	// the ith column with the (4-i)th column
 	public void rotateLeft()
 	 {
		if (pieceType != 0)  // The square does not need to be rotated
		 {	
			int[][] temp = new int[5][5];
			for(int i=0; i<5; i++)
			 {
				for(int j=0; j<5; j++)
				 {
					temp[i][j]=pieceArray[j][i];
				 }
			 } 
			
			for(int i=0; i<5; i++)
			 {
				for(int j=0; j<5; j++)
				 {
					pieceArray[i][j]=temp[4-i][j];
				 }
			 }
		 }	 
 	 } 
 }