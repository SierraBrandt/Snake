//main game
package snake;


import java.io.*; 
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import javax.swing.*;

public class Snake extends JFrame implements Runnable {

    static final int numRows = 21;
    static final int numColumns = 21;
    static final int XBORDER = 20;
    static final int YBORDER = 20;
    static final int YTITLE = 30;
    static final int WINDOW_BORDER = 8;
    static final int WINDOW_WIDTH = 2*(WINDOW_BORDER + XBORDER) + numColumns*30;
    static final int WINDOW_HEIGHT = YTITLE + WINDOW_BORDER + 2 * YBORDER + numRows*30;
    
   
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    final int EMPTY = 0;
    final int SNAKE = 1;
    final int BAD_BOX = 2;
     
    int timeCount;
            
    int board[][];

    int currentRow;
    int currentColumn;
    int columnDir;
    int rowDir;
    
 
    
    boolean playGame;
    boolean endGame;
    
    int score;
    int highScore;
    static Snake frame;
    public static void main(String[] args) {
        frame = new Snake();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Snake() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                    playGame = true;
                    columnDir = 0;
                    rowDir = -1;
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    playGame = true;
                    columnDir = 0;
                    rowDir = 1;
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    playGame = true;
                    columnDir = -1;
                    rowDir = 0;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    playGame = true;
                    columnDir = 1;
                    rowDir = 0;
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.white);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        
        g.setColor(Color.red);
//horizontal lines
        for (int zi=1;zi<numRows;zi++)
        {
            g.drawLine(getX(0) ,getY(0)+zi*getHeight2()/numRows ,
            getX(getWidth2()) ,getY(0)+zi*getHeight2()/numRows );
        }
//vertical lines
        for (int zi=1;zi<numColumns;zi++)
        {
            g.drawLine(getX(0)+zi*getWidth2()/numColumns ,getY(0) ,
            getX(0)+zi*getWidth2()/numColumns,getY(getHeight2())  );
        }
        
//Display the objects of the board
        for (int zrow=0;zrow<numRows;zrow++)
        {
            for (int zcolumn=0;zcolumn<numColumns;zcolumn++)
            {
//If the location on the board is snake, then draw the box gray.   
                if (board[zrow][zcolumn] == BAD_BOX)
                {
                    g.setColor(Color.red);
                    g.fillRect(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
                    
                }
                if (board[zrow][zcolumn] == SNAKE)
                {
                    g.setColor(Color.gray);
                    g.fillRect(getX(0)+zcolumn*getWidth2()/numColumns,
                    getY(0)+zrow*getHeight2()/numRows,
                    getWidth2()/numColumns,
                    getHeight2()/numRows);
                    
                }
                
               

            }
        }
        if(endGame){
        g.setColor(Color.BLACK);
        g.setFont(new Font("Andy",Font.PLAIN,70));
        g.drawString("GAME OVER",40,300);
        }
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Andy",Font.PLAIN,15));
        g.drawString("Score: " + score,20, 42);
        g.drawString("High Score: " + highScore,400,42);
        gOld.drawImage(image, 0, 0, null);
    }

////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            
            
            double seconds = .1;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//Allocate memory for the 2D array that represents the board.
        board = new int[numRows][numColumns];
//Initialize the board to be empty.
        for (int zrow = 0;zrow < numRows;zrow++)
        {
            for (int zcolumn = 0;zcolumn < numColumns;zcolumn++)
                board[zrow][zcolumn] = EMPTY;
        }

       currentRow = numRows/2;
       currentColumn = numColumns/2;
       board[currentRow][currentColumn]=SNAKE;
        
       playGame = false;
       endGame = false;
       
       score = 0;
       timeCount = 0;
    }
/////////////////////////////////////////////////////////////////////////
      public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            reset();
        }
        if (endGame)
            return;
        
//return if the game is not playing.        
        if (!playGame)
            return;
//Set the next value in the 2D array so that the snake body adds to the right.        

        currentRow += rowDir;
        currentColumn += columnDir;
//The game is over if the head of the snake goes off the board.        
        if (currentRow < 0)
        {
            currentRow = numRows;
        }
        else if (currentRow >= numRows)
        {
            currentRow = -1;
        }
        else if (currentColumn < 0)
        {
            currentColumn = numColumns;
        }
        else if (currentColumn >= numColumns )
        {
            currentColumn = -1;
            
        }else if (board[currentRow][currentColumn]== SNAKE)
        {
            endGame = true;

        } 
        else if (board[currentRow][currentColumn]== BAD_BOX)
        {
            endGame = true;

        } 
        else
        {
            board[currentRow][currentColumn] = SNAKE;
            score ++;
                    if (score>=highScore)
                        highScore = score;
        }
        
        if (timeCount % 20 == 19) //Should be true every 2 seconds.
        {
        //add a bad box to a random location on the board.
        int randomRow = (int)(Math.random()*currentRow);
        int randomColumn = (int)(Math.random()*currentColumn);
        board[randomRow][randomColumn] = BAD_BOX;
        }   
        timeCount++;
    }


////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }


/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER + WINDOW_BORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE );
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    public int getWidth2() {
        return (xsize - 2 * (XBORDER + WINDOW_BORDER));
    }

    public int getHeight2() {
        return (ysize - 2 * YBORDER - WINDOW_BORDER - YTITLE);
    }
}