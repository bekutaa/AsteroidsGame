import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class AsteroidsGame extends PApplet {

private SpaceShip sharkKnight;
private Star [] galaxy;
private ArrayList <Space> ichi;
private ArrayList <Bullet> ni;

public int score = 0;
public int bombs = 3;
public int numberOfAsteroids = 15;

public float timeSurvived = 0.0f;
public float finishedTime;

public boolean isShield = false;

public int textColor = color(255,0,0);

interface Space
{
  //All the mutators,retrievers, and move/show functions are already defined
  //in the abstract class Floater. All Floaters use them; therefore the
  //interface for all Floaters contains these functions.  
  public void setX(int x);
  public int getX();

  public void setY(int y);
  public int getY();

  public void setDirectionX(double x);
  public double getDirectionX();

  public void setDirectionY(double y);
  public double getDirectionY();

  public void setPointDirection(int degrees);
  public double getPointDirection();

  public void move();
  public void show();
}

public void setup() 
{
  size(600,600);
  background(0);

  //This is an ArrayList for all floaters. In it are asteroids and only one SpaceShip;
  //this spaceship is drawn last on top of everything else.
  //This is why its number is size-1.
  ichi = new ArrayList <Space>();

  sharkKnight = new SpaceShip();

  for(int s = 0; s < numberOfAsteroids; s++)
  {
    ichi.add(0,new Asteroid());
  }

  //Separate ArrayList for Bullets.
  ni = new ArrayList <Bullet>();

  //Create an array of background stars.
  galaxy = new Star[400];
  for (int i = 0; i < galaxy.length; i++)
  {
    galaxy[i] = new Star();
  }
}

public void draw() 
{
  background(0);

  //Draw the stars in the background.
  for(int i = 0; i < galaxy.length; i++)
  {
    galaxy[i].showStar();
  }

  if(ichi.size() == 0)
  {
    for(int i = 0; i < numberOfAsteroids; i++)
    {
      ichi.add(new Asteroid());
    }
  }
  else
  {
    //Move and show the asteroids.
    //Crash the ship if the asteroid is less than 25 units away from the ship.
    for(int i = 0; i < ichi.size(); i++)
    {
      if( i != ichi.size() )
      {
        ichi.get(i).move();
        ichi.get(i).show();

        if(!isShield)
        {
          if(dist(
          ichi.get(i).getX(),ichi.get(i).getY(),
          sharkKnight.getX(),sharkKnight.getY()
               ) < 25
            )
          {
            sharkKnight.setCrash(true);
          }
        }

        for(int a = 0; a < ni.size(); a++)
        {
          //Remove an asteroid if the bullet is close enough ("hits").
          //Remove the bullet as well.
          if( i != ichi.size() )
          {
            if(ichi.get(i) instanceof smallAsteroid)
            {
              if(dist(
              ichi.get(i).getX(),ichi.get(i).getY(),
              ni.get(a).getX(),ni.get(a).getY()
                     ) < 15
                )
              {
                score+=50;
                ichi.remove(i);
                ni.remove(a);
              }
            }
            else
            {
              if(dist(
              ichi.get(i).getX(),ichi.get(i).getY(),
              ni.get(a).getX(),ni.get(a).getY()
                     ) < 25
                )
              {
                score+=20;
                Asteroid exploded = (Asteroid)ichi.get(i);

                ichi.add(i, new smallAsteroid((Asteroid)exploded) );
                ichi.add(i+1, new smallAsteroid((Asteroid)exploded) );

                ichi.remove(i+2);
                ni.remove(a);
              }
            }
          }
        }
      }
    }
  }
  
  if(sharkKnight.getCrash() == false)
  {
    for(int a = 0; a < ni.size(); a++)
    {
      //Remove the bullet from the ArrayList if it goes offscreen.
      ni.get(a).move();
      if(
        (ni.get(a).getX() < 0 || ni.get(a).getX() > width) &&
        (ni.get(a).getY() < 0 || ni.get(a).getY() > height)
        )
      {
        ni.remove(a);
      }
      else
      {
       ni.get(a).show();
      }
    }
  }

  //Move and show the SpaceShip.
  sharkKnight.move();
  sharkKnight.show();
  
  //If the player has not crashed:
  //Display how long the player has survived in seconds, the score, and bombs left.
  if(sharkKnight.getCrash() == false)
  {
    fill(textColor);
    textSize(14);
    text("Time survived so far: " + ((int)((timeSurvived/60)*10))/10.0f + "s",400,590);
    text("Score: " + score,15,590);
    text("Bombs: ", 150,590);
    
    //Draw the number of bombs the player has left to use.
    noFill();
    strokeWeight(2);
    stroke(35,0,255);
    //fill(35,0,255);
    for(int b = 0; b < bombs; b++)
    {
      ellipse(185+25*(b+1), 585, 15,15);
    }

    timeSurvived+=1.0f;
  }
  //Once the "New Game?" screen turns up, display how long the player survived.
  else if(sharkKnight.getGame() == true)
  {
    finishedTime = ((int)((timeSurvived/60)*10))/10.0f;
    fill(textColor);
    textSize(20);
    text("You survived for " + finishedTime + " seconds", 155,235);
    text("You scored " + score + " points",195,210);
  }

  System.out.println(ichi.size());
}

public void keyPressed() //Spaceship movement
{
  if(key == 'v') { isShield = !isShield; } //invincibility for testing

  if(key == 'x' && bombs > 0)
  {
    int checkSize = ichi.size();
    {
      for(int i = 0; i < checkSize; i++)
      {
        ichi.remove(0);
      }
      bombs--;
    }
  }

  if(sharkKnight.getCrash() == false)
  {
    //Rotate left/right
    if(key == 'a' || (key == CODED && keyCode == LEFT)) { sharkKnight.rotate(-10); }
    if(key == 'd' || (key == CODED && keyCode == RIGHT)) { sharkKnight.rotate(10); }

    //Accelerate/decelerate
    if(key == 'w' || (key == CODED && keyCode == UP)) { sharkKnight.accelerate(0.1f); }
    if(key == 's' || (key == CODED && keyCode == DOWN)) { sharkKnight.accelerate(-0.1f); }

    //Hyperspace (no animation applicable)
    if(key == 'q')
    {
      sharkKnight.setPointDirection((int)(Math.random()*360));

      sharkKnight.setDirectionX(0);
      sharkKnight.setDirectionY(0);

      sharkKnight.setX((int)(Math.random()*width));
      sharkKnight.setY((int)(Math.random()*height));
    }

    if(key == ' ')
    {
      ni.add(new Bullet(sharkKnight));
    }
  }

  // if(key == 'f') { timeSurvived+=60; }
  // if(key == 'r') { score+=10; }
}

public void mousePressed() //ONLY for New Game
{
  //ONLY run this code if the player has crashed and the "New Game?" message has appeared.
  if(sharkKnight.getGame() == true)
  {
    if(mouseY > 325 && mouseY < 350)
    {
      //The player clicks on "Yes". Code is run to "reset" the game to its initial state.
      if(mouseX > 215 && mouseX < 270)
      {
        int checkSize = ichi.size();
        for(int i = 0; i < checkSize; i++)
        {
          ichi.remove(0);
        }

        for(int i = 0; i < numberOfAsteroids; i++)
        {
          ichi.add(new Asteroid());
        }
        
        checkSize = ni.size();
        for(int i = 0; i < checkSize; i++)
        {
          ni.remove(0);
        }

        for(Star temp : galaxy)
        {
          temp.reset();
        }

        sharkKnight.reset();
        timeSurvived = 0.0f;
        score = 0;
        bombs = 3;
      }

      //The player clicks on "No". The game "shuts down" with a black screen.
      if(mouseX > 340 && mouseX < 390)
      {
        background(0);
        sharkKnight.setGame(false);
        noLoop();
      }
    }
  }
}
//----------------------------------------------------------------------------------------
//Draws the stars in the background
class Star
{
  private int starX, starY;
  private int starSize;
  private int starColor;
  
  public Star()
  {
    starX = (int)(Math.random()*width);
    starY = (int)(Math.random()*height);
    starSize = (int)(Math.random()*3)+1;
    starColor = color(255,255,255);
  }

  public void showStar()
  {
    noStroke();
    fill(starColor);
    ellipse(starX,starY,starSize,starSize);
  }

  public void reset()
  {
    starX = (int)(Math.random()*width);
    starY = (int)(Math.random()*height);
    starSize = (int)(Math.random()*3)+1;
  }
} //

//----------------------------------------------------------------------------------------
//A spaceship: S.H. Ark Knight
class SpaceShip extends Floater implements Space
{   
    private boolean crashed;
    private boolean newGame;
    private float explodeRange;
    private int timeCounter;

    public SpaceShip()
    {
        //Corners of the spaceship
        corners = 9;

        xCorners = new int[corners];
        yCorners = new int[corners];

        xCorners[0] = 0;
        yCorners[0] = -8;

        xCorners[1] = -6;
        yCorners[1] = -6;

        xCorners[2] = -8;
        yCorners[2] = 0;

        xCorners[3] = -6;
        yCorners[3] = 6;

        xCorners[4] = 0;
        yCorners[4] = 8;

        xCorners[5] = 22;
        yCorners[5] = 6;

        xCorners[6] = 8;
        yCorners[6] = 2;

        xCorners[7] = 8;
        yCorners[7] = -2;

        xCorners[8] = 22;
        yCorners[8] = -6;

        //color of the spaceship
        myColor = color(78,106,240);

        //coordinates
        myCenterX = (int)width/2;
        myCenterY = (int)height/2;

        myDirectionX = 0;
        myDirectionY = 0;

        myPointDirection = 0;

        //Crashed?
        crashed = false;
        explodeRange = 0;

        //newgame stuff
        timeCounter = 0;
        newGame = false;
    }

    //Setters and getters for the crash "function".
    public void setCrash(boolean crash) { crashed = crash; }
    public boolean getCrash() { return crashed; }

    //Setters and getters for prepping for a new game.
    public void setGame(boolean game) { newGame = game; }
    public boolean getGame() { return newGame; }

    //Resets the spaceship to its initial state for a new game.
    public void reset()
    {
      myCenterX = (int)width/2;
      myCenterY = (int)height/2;

      myDirectionX = 0;
      myDirectionY = 0;

      myPointDirection = 0;

      crashed = false;
      explodeRange = 0;

      timeCounter = 0;
      newGame = false;
    }

    public void show()  //Draws the floater at the current position  
    {
      if(crashed)
      {
        //Stop the ship from moving once it's crashed.
        myDirectionX = 0;
        myDirectionY = 0;

        strokeWeight(2);
        noFill();
        stroke(255,0,0);

        //Explosion animation
        if(explodeRange < 50)
        {
          if(explodeRange > 10)
          {
            ellipse((float)myCenterX,(float)myCenterY,explodeRange-10,explodeRange-10);
          }
          if(explodeRange > 20)
          {
            ellipse((float)myCenterX,(float)myCenterY,explodeRange-20,explodeRange-20);
          }
          if(explodeRange > 30)
          {
            ellipse((float)myCenterX,(float)myCenterY,explodeRange-30,explodeRange-30);
          }
          if(explodeRange > 40)
          {
            ellipse((float)myCenterX,(float)myCenterY,explodeRange-40,explodeRange-40);
          }
          ellipse((float)myCenterX,(float)myCenterY,(int)explodeRange,(int)explodeRange);
          explodeRange+=1.5f;
        }
        else //Once done with exploding, display GAME OVER.
        {
          //After a while, change the message to "play again"
          if(timeCounter > 90)
          {
            newGame = true;
            fill(textColor);
            textSize(50);
            text("PLAY AGAIN?",150,300);

            textSize(28);
            text("YES",220,350);
            text("NO",345,350);
          }
          else
          {
            fill(textColor);
            textSize(50);
            text("GAME OVER",150,300);
            timeCounter++;
          }
        }
      }
      //If not collided, draw the ship instead.
      else
      {
        super.show();
      }
    }
} //

//----------------------------------------------------------------------------------------
//The Bullet class.
class Bullet extends Floater implements Space
{
  public Bullet(SpaceShip theShip)
  {
    myColor = color(35,0,255);

    myCenterX = theShip.getX();
    myCenterY = theShip.getY();

    myPointDirection = theShip.getPointDirection();

    double dRadians = myPointDirection*(Math.PI/180);

    myDirectionX = (5 * Math.cos(dRadians)) + theShip.getDirectionX();
    myDirectionY = (5 * Math.sin(dRadians)) + theShip.getDirectionY();
  }

  public void show()
  {
    // fill(myColor);
    // noStroke();
    noFill();
    stroke(myColor);

    ellipse((float)myCenterX,(float)myCenterY,5,5);
  }

  public void move()
  {
    myCenterX += myDirectionX; 
    myCenterY += myDirectionY;
  }
} //

//-----------------------------------------------------------------------------------------------------
//The Asteroid class.
class Asteroid extends Floater implements Space
{
  protected int rotSpeed;
  
  public Asteroid()
  {
    rotSpeed = (int)(Math.random()*7)-3;
    if(rotSpeed == 0)
    {
      if(Math.random() > 0.5f)
      {
        rotSpeed = 1;
      }
      else
      {
        rotSpeed = -1;
      }
    }

    //Initializes the asteroid's corners
    corners = 8;

    xCorners = new int[corners];
    yCorners = new int[corners];

    xCorners[0] = 4;
    yCorners[0] = -12;

    xCorners[1] = -6;
    yCorners[1] = -8;

    xCorners[2] = -10;
    yCorners[2] = -10;

    xCorners[3] = -14;
    yCorners[3] = 6;

    xCorners[4] = -8;
    yCorners[4] = 8;

    xCorners[5] = -2;
    yCorners[5] = 4;

    xCorners[6] = 8;
    yCorners[6] = 6;

    xCorners[7] = 10;
    yCorners[7] = -6;

    //Color
    myColor = color(210,210,210);

    //coordinates
    if(Math.random()>0.5f)
    {
      myCenterX = (int)(Math.random()*width);
      myCenterY = 0;
    }
    else
    {
      myCenterX = 0;
      myCenterY = (int)(Math.random()*height);
    }

    myDirectionX = (Math.random()*3)-1;
    myDirectionY = (Math.random()*3)-1;

    myPointDirection = (int)(Math.random()*360);
  }

  public void move()
  {
    rotate(rotSpeed);
    super.move();
  }
} //

class smallAsteroid extends Asteroid implements Space
{
  public smallAsteroid(Asteroid theAsteroid)
  {
    rotSpeed = (int)(Math.random()*7)-3;
    if(rotSpeed == 0)
    {
      if(Math.random() > 0.5f)
      {
        rotSpeed = 1;
      }
      else
      {
        rotSpeed = -1;
      }
    }

    corners = 7;

    xCorners[0] = 6;
    yCorners[0] = -6;

    xCorners[1] = 0;
    yCorners[1] = -8;

    xCorners[2] = -6;
    yCorners[2] = -6;

    xCorners[3] = -8;
    yCorners[3] = 0;

    xCorners[4] = -2;
    yCorners[4] = 4;

    xCorners[5] = 4;
    yCorners[5] = 2;

    xCorners[6] = 2;
    yCorners[6] = 2;

    myCenterX = theAsteroid.getX();
    myCenterY = theAsteroid.getY();

    myDirectionX = (Math.random()*5)-2;
    myDirectionY = (Math.random()*5)-2;

    myPointDirection = (int)(Math.random()*360);
  }
} //

//----------------------------------------------------------------------------------------
//An abstract class that contains variables and functions for other classes to inherit.

abstract class Floater //Do NOT modify the Floater class! Make changes in the SpaceShip class 
{   
  protected int corners;  //the number of corners, a triangular floater has 3   
  protected int[] xCorners;
  protected int[] yCorners;
  protected int myColor;
  protected double myCenterX, myCenterY; //holds center coordinates   
  protected double myDirectionX, myDirectionY; //holds x and y coordinates of the vector for direction of travel   
  protected double myPointDirection; //holds current direction the ship is pointing in degrees    
  
  //Edits were made to the Floater class functions with approval from Mr. Simon; namely,
  //making the setters and getters defined functions.
  public void setX(int x) { myCenterX = x; }
  public int getX() { return (int)myCenterX; }

  public void setY(int y) { myCenterY = y; }
  public int getY() { return (int)myCenterY; }

  public void setDirectionX(double x) { myDirectionX = x; }
  public double getDirectionX() { return myDirectionX; }

  public void setDirectionY(double y) { myDirectionY = y; }
  public double getDirectionY() { return myDirectionY; }

  public void setPointDirection(int degrees) { myPointDirection = degrees; }
  public double getPointDirection() { return myPointDirection; }

  //Accelerates the floater in the direction it is pointing (myPointDirection)
  public void accelerate (double dAmount)
  {
    //convert the current direction the floater is pointing to radians
    double dRadians =myPointDirection*(Math.PI/180);
    //change coordinates of direction of travel
    myDirectionX += ((dAmount) * Math.cos(dRadians));
    myDirectionY += ((dAmount) * Math.sin(dRadians));
  }
  public void rotate (int nDegreesOfRotation)
  {
    //rotates the floater by a given number of degrees
    myPointDirection+=nDegreesOfRotation;
  }
  public void move () //move the floater in the current direction of travel
  {
    //change the x and y coordinates by myDirectionX and myDirectionY       
    myCenterX += myDirectionX; 
    myCenterY += myDirectionY;

    //wrap around screen
    if(myCenterX >width)
    {     
      myCenterX = 0;    
    }    
    else if (myCenterX<0)
    {     
      myCenterX = width;    
    }    
    if(myCenterY >height)
    {    
      myCenterY = 0;    
    }   
    else if (myCenterY < 0)
    {     
      myCenterY = height;    
    }
  }   
  public void show ()  //Draws the floater at the current position  
  {    
    noFill();
    strokeWeight(1);
    stroke(myColor);

    //convert degrees to radians for sin and cos         
    double dRadians = myPointDirection*(Math.PI/180);                 
    int xRotatedTranslated, yRotatedTranslated;    
    beginShape();         
    
    for(int nI = 0; nI < corners; nI++)    
    {     
      //rotate and translate the coordinates of the floater using current direction 
      xRotatedTranslated = (int)((xCorners[nI]* Math.cos(dRadians)) - (yCorners[nI] * Math.sin(dRadians))+myCenterX);     
      yRotatedTranslated = (int)((xCorners[nI]* Math.sin(dRadians)) + (yCorners[nI] * Math.cos(dRadians))+myCenterY);      
      vertex(xRotatedTranslated,yRotatedTranslated);    
    }   
    endShape(CLOSE);
  }
} 
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "AsteroidsGame" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
