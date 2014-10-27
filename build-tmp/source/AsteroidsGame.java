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

SpaceShip sharkKnight;
Star [] galaxy;

interface Objects
{
  public void move();
  public void show();
}

public void setup() 
{
  size(600,600);
  background(0);
  sharkKnight = new SpaceShip();

  galaxy = new Star[250];
  for (int i = 0; i < galaxy.length; i++)
  {
    galaxy[i] = new Star();
  }
}

public void draw() 
{
  background(0);

  for(int i = 0; i < galaxy.length; i++)
  {
    galaxy[i].showStar();
  }

  sharkKnight.move();
  sharkKnight.show();
}

public void keyPressed()
{
  if(key == 'a')
  {
    sharkKnight.rotate(-5);
  }
  if(key == 'd')
  {
    sharkKnight.rotate(5);
  }

  if(key == 'w')
  {
    sharkKnight.accelerate(0.1f);
  }
  if(key == 's')
  {
    sharkKnight.accelerate(-0.1f);
  }

  if(key == 'q')
  {
    sharkKnight.setPointDirection((int)(Math.random()*360));

    sharkKnight.setDirectionX(0);
    sharkKnight.setDirectionY(0);

    sharkKnight.setX((int)(Math.random()*width));
    sharkKnight.setY((int)(Math.random()*height));
  }
}

class Star
{
  private int starX, starY;
  private int starSize;
  private int starColor;
  
  Star()
  {
    starX = (int)(Math.random()*width);
    starY = (int)(Math.random()*height);
    starSize = (int)(Math.random()*5);
    starColor = color(255,255,255);
  }

  public void showStar()
  {
    noStroke();
    fill(starColor);
    ellipse(starX,starY,starSize,starSize);
  }
} //

class SpaceShip extends Floater  
{   
    SpaceShip()
    {
        //Corners of the spaceship
        corners = 9;//4;

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
        // xCorners[0] = -12;
        // yCorners[0] = -8;

        // xCorners[1] = 14;
        // yCorners[1] = 0;

        // xCorners[2] = -12;
        // yCorners[2] = 8;

        // xCorners[3] = -2;
        // yCorners[3] = 0;

        //color of the spaceship
        myColor = color(78,106,240);

        //coordinates
        myCenterX = (int)width/2;
        myCenterY = (int)height/2;

        myDirectionX = 0;
        myDirectionY = 0;

        myPointDirection = 0;
    }



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
} //


abstract class Floater //Do NOT modify the Floater class! Make changes in the SpaceShip class 
{   
  protected int corners;  //the number of corners, a triangular floater has 3   
  protected int[] xCorners;
  protected int[] yCorners;
  protected int myColor;
  protected double myCenterX, myCenterY; //holds center coordinates   
  protected double myDirectionX, myDirectionY; //holds x and y coordinates of the vector for direction of travel   
  protected double myPointDirection; //holds current direction the ship is pointing in degrees    
  
  abstract public void setX(int x);
  abstract public int getX();
  abstract public void setY(int y);
  abstract public int getY();
  abstract public void setDirectionX(double x);
  abstract public double getDirectionX();
  abstract public void setDirectionY(double y);
  abstract public double getDirectionY();
  abstract public void setPointDirection(int degrees);
  abstract public double getPointDirection();

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
    fill(myColor);   
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
