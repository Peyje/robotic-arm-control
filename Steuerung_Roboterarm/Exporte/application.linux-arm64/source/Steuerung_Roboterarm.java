import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import org.gamecontrolplus.gui.*; 
import org.gamecontrolplus.*; 
import net.java.games.input.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Steuerung_Roboterarm extends PApplet {

/*
* Roboterarm Steuerung
 *
 * Steuere den MeArm (o.\u00c4.) \u00fcber die GUI oder den XBOX Controller
 *
 * In der Gui kann der Sollwert der Servos im Textfeld eingegeben werden
 * Dazu gibt es Tasten, die die Claw \u00f6ffnen, schlie\u00dfen und alle Servos resetten 
 * Ebenso kann mit der Connect Taste der Controller verbunden werden
 * Werden die Pfeil Buttons gehalten, bewegt sich der jeweilige Servo bis die Maus wieder losgelassen wird
 * Selbiges gilt f\u00fcr die Sticks des Cotrollers
 * 
 * Kommuniziert wird \u00fcber den serial Port, mit folgendem Protokoll:
 * 'SSS' als Startbit
 * 'C' oder 'H' oder 'V' oder 'F' f\u00fcr den Servo, dann '0' bis '180' f\u00fcr den Winkel
 *  oder '777' f\u00fcr kontinuierlich links, '888' f\u00fcr Stopp, '999' f\u00fcr kontinuierlich rechts
 * 
 *
 *
 */




//F\u00fcr Controllerverwendung




//F\u00fcr GUI


Serial myPort;

//F\u00fcr Controllerverwendung
ControlIO control;
Configuration config;
ControlDevice gpad;

//F\u00fcr GUI
ControlP5 cp5;




public void setup() {
  //Zeige alle Serial Ports, um den passenden zu finden
  //printArray(Serial.list());

  //Setze passenden Port mit einer Rate von 9600
  myPort = new Serial(this, Serial.list()[1], 9600);
  println("Bereit f\u00fcr Eingabe");

  //Interface erstellen und Farben festlegen
  

  circleColor = color(50);
  rectColor = color(50, 55, 100);
  triangleColor = color(50);

  circleHighlight = color(204);
  rectHighlight = color(204);
  triangleHighlight = color(204);

  chosenColor = color(255, 204, 0);
  resetColor = color(255, 0, 0);


  //Y-Lage der Kreise 
  circleCY = height/5;
  circleVY = height/5;
  circleHY = height/5;
  circleFY = height/5;

  //X-Lage der Kreise
  circleCX = width/5;
  circleVX = width/5 * 2;
  circleHX = width/5 * 3;
  circleFX = width/5 * 4;

  //Y-Lage Rechtecke
  rectOY = height/9 * 6;
  rectCY = height/9 * 7;
  rectRY = height/9 * 8;
  rectConY = height/9 * 6;

  //X-Lage Rechtecke
  rectOX = width/6;
  rectCX = width/6;
  rectRX = width/6;
  rectConX = width/8 * 7;

  //Zeichne Ellipsen ausgehend von der Mitte
  ellipseMode(CENTER);

  //Neue Instanz fuer GUI Extras
  cp5 = new ControlP5(this);

  cp5.addTextfield("clawInput")
    .setPosition(108, 170)
    .setAutoClear(true)
    .setSize(40, 20)
    ;

  cp5.addTextfield("verticalInput")
    .setPosition(236, 170)
    .setAutoClear(true)
    .setSize(40, 20)
    ;

  cp5.addTextfield("horizontalInput")
    .setPosition(364, 170)
    .setAutoClear(true)
    .setSize(40, 20)
    ;

  cp5.addTextfield("forwardInput")
    .setPosition(492, 170)
    .setAutoClear(true)
    .setSize(40, 20)
    ;
}











public void draw() {
  //Mausposition bestimmen und Hintergrund zeichnen
  updateMouseLocation();
  background(167);

  //Zeichne Kreise f\u00fcr Claw, Vertical, Horizontal und Forward  
  circleDraw((stateC1 || stateC2 || stateOpen || stateClose || stateReset), circleCX, circleCY);
  circleDraw((stateV1 || stateV2 || stateReset), circleVX, circleVY);
  circleDraw((stateH1 || stateH2 || stateReset), circleHX, circleHY);
  circleDraw((stateF1 || stateF2 || stateReset), circleFX, circleFY);

  //Zeichne Rechtecke f\u00fcr Buttons
  rectDraw(stateOpen, rectOverO, rectOX, rectOY);
  rectDraw(stateClose, rectOverC, rectCX, rectCY);
  rectDraw(stateReset, rectOverR, rectRX, rectRY);
  rectDraw(stateConnect, rectOverCon, rectConX, rectConY);
  
  //Zeichne Rechtecke f\u00fcr Positionsbuttons
  rectDraw(statePos1, rectOverPos1, 260, rectOY);
  rectDraw(statePos2, rectOverPos2, 220, rectCY);
  rectDraw(statePos3, rectOverPos3, 260, rectRY);
  rectDraw(stateSetPos, rectOverSetPos, 300, rectCY);

  //Controller Buttons zeichnen  
  buttonDraw(APressed, color(60, 245, 44), chosenColor, rectCX, rectCY);
  buttonDraw(BPressed, color(245, 44, 85), chosenColor, rectOX, rectOY);
  buttonDraw(YPressed, chosenColor, color(245, 44, 85), rectRX, rectRY);

  //Zeichne Dreiecke f\u00fcr Steuerung
  triangleDraw(stateV1, switchOverV1, circleVX, circleVY + 30, circleVX - 13, circleVY + 50, circleVX + 13, circleVY + 50);
  triangleDraw(stateV2, switchOverV2, circleVX, circleVY + 80, circleVX - 13, circleVY + 60, circleVX + 13, circleVY + 60);
  triangleDraw(stateF1, switchOverF1, circleFX, circleFY + 30, circleFX - 13, circleFY + 50, circleFX + 13, circleFY + 50);
  triangleDraw(stateF2, switchOverF2, circleFX, circleFY + 80, circleFX - 13, circleFY + 60, circleFX + 13, circleFY + 60);
  triangleDraw(stateH1, switchOverH1, circleHX - 25, circleHY + 55, circleHX - 5, circleHY + 42, circleHX - 5, circleHY + 68);
  triangleDraw(stateH2, switchOverH2, circleHX + 25, circleHY + 55, circleHX + 5, circleHY + 42, circleHX + 5, circleHY + 68);
  triangleDraw(stateC1, switchOverC1, circleCX, circleCY + 40, circleCX - 10, circleCY + 29, circleCX - 10, circleCY + 51);
  triangleDraw(stateC1, switchOverC1, circleCX, circleCY + 40, circleCX + 10, circleCY + 29, circleCX + 10, circleCY + 51);
  triangleDraw(stateC2, switchOverC2, circleCX - 12, circleCY + 70, circleCX - 2, circleCY + 59, circleCX - 2, circleCY + 81);
  triangleDraw(stateC2, switchOverC2, circleCX + 12, circleCY + 70, circleCX + 2, circleCY + 59, circleCX + 2, circleCY + 81);

  //Zeichne alle Labels
  label();


  //Geht manuelle Texteingaben durch und setzt moegliche neue Werte
  manualInput();
  
  //Zeige Warte-Box, bis erste Werte vom Arduino reinkommen
  if(angleC == -1) {
    stroke(255);
    fill(255, 0,0);
    rect(10, 230, 620, 120);
    fill(255);
    text("WAIT", 300, 300);
  }

  //Wenn SetupButton gedrueckt wird starte das Controller Setup
  if (stateConnect) {
    setupController();
    stateConnect = false;
    println("stateConnect terminated");
    println("");
    controllerConnected = true;
    println("Controller successfully connected");
    println("");
  }

  //Nehme Controllerwerte an, wenn er verbunden ist
  if (controllerConnected) {
    controllerInput();
  }

  if (myPort.available() > 0) {
    incomingByte = myPort.readStringUntil('\n');
    print("IncomingByte: ");
    print(incomingByte);

    if (incomingByte != null) {
      servoSent = incomingByte.charAt(0);
      print("Servo: ");
      print(servoSent);
      angleSent = 0;
      int charAt = 1;
      for (int i = 0; i < incomingByte.length(); i++) {
        if (incomingByte.charAt(i) == 'X') {
          charAt++;
        }
      }
      angleSent = Integer.parseInt(incomingByte.substring(charAt, 4));
      print(", Angle: ");
      println(angleSent);
      println("___________");
    }

    if (servoSent == 'C') {
      angleC = angleSent;
    }
    if (servoSent == 'V') {
      angleV = angleSent;
    }
    if (servoSent == 'H') {
      angleH = angleSent;
    }
    if (servoSent == 'F') {
      angleF = angleSent;
    }
  }
  
}
public void setupController() {
  // Initialise die ControlIO
  control = ControlIO.getInstance(this);
  // Finde Ger\u00e4t, dass zur Beschreibung passt
  gpad = control.getMatchedDevice("XBOX Controller");
  if (gpad == null) {
    println("No suitable device configured");
    System.exit(-1);
  }
}



public void controllerInput() {
  //Zeigen, dass Controller verbunden ist
  fill(255, 204, 0);
  text("Connected", 562, 280);

  //Controller Daten speichern
  conXPos = gpad.getSlider("X").getValue();
  conYPos = gpad.getSlider("Y").getValue();
  conZPos = gpad.getSlider("Z").getValue();
  conTriPos = gpad.getSlider("Trigger").getValue();
  APressed = gpad.getButton("A Button").pressed();
  BPressed = gpad.getButton("B Button").pressed();
  YPressed = gpad.getButton("Y Button").pressed();
  hatUp = gpad.getHat("Positions").up();
  hatDown = gpad.getHat("Positions").down();
  hatLeft = gpad.getHat("Positions").left();
  hatRight = gpad.getHat("Positions").right();

  if (BPressed && !controllerBActive) {
    println("stateOpen initiated (Controller)");
    stateOpen = true;
    controllerBActive = true;
    sendToArduino('C', 35);
    println("stateOpen terminated");
    println("");
  }
  if (controllerBActive && !BPressed) {
    stateOpen = false;
    controllerBActive = false;
  }

  if (APressed && !controllerAActive) {
    println("stateClose initiated (Controller)");
    stateClose = true;
    controllerAActive = true;
    sendToArduino('C', 165);
    println("stateClose terminated");
    println("");
  }
  if (controllerAActive && !APressed) {
    stateClose = false;
    controllerAActive = false;
  }

  if (YPressed && !controllerYActive) {
    println("stateReset initiated (Controller)");
    stateReset = true;
    controllerYActive = true;
    sendToArduino('C', 35);
    sendToArduino('H', 90);
    sendToArduino('V', 90);
    sendToArduino('F', 90);
    println("stateReset terminated");
    println("");
  }
  if (controllerYActive && !YPressed) {
    stateReset = false;
    controllerYActive = false;
  }

  if (conXPos < -0.2f && !controllerX1Active) {
    stateH1 = true;
    controllerX1Active = true;
    println("stateH1 initiated (Controller)");
    sendToArduino('H', 999);
    println("SERVO MOVEMENT!");
  }
  if (conXPos > -0.2f && controllerX1Active) {
    stateH1 = false;
    controllerX1Active = false;
    sendToArduino('H', 888);
    println("stateH1 terminated");
    println("");
  }
  if (conXPos > 0.2f && !controllerX2Active) {
    stateH2 = true;
    controllerX2Active = true;
    println("stateH2 initiated (Controller)");
    sendToArduino('H', 777);
    println("SERVO MOVEMENT!");
  }
  if (conXPos < 0.2f && controllerX2Active) {
    stateH2 = false;
    controllerX2Active = false;
    sendToArduino('H', 888);
    println("stateH2 terminated");
    println("");
  }

  if (conYPos < -0.2f && !controllerY1Active) {
    stateF1 = true;
    controllerY1Active = true;
    println("stateF1 initiated (Controller)");
    sendToArduino('F', 999);
    println("SERVO MOVEMENT!");
  }
  if (conYPos > -0.2f && controllerY1Active) {
    stateF1 = false;
    controllerY1Active = false;
    sendToArduino('F', 888);
    println("stateF1 terminated");
    println("");
  }
  if (conYPos > 0.2f && !controllerY2Active) {
    stateF2 = true;
    controllerY2Active = true;
    println("stateF2 initiated (Controller)");
    sendToArduino('F', 777);
    println("SERVO MOVEMENT!");
  }
  if (conYPos < 0.2f && controllerY2Active) {
    stateF2 = false;
    controllerY2Active = false;
    sendToArduino('F', 888);
    println("stateF2 terminated");
    println("");
  }

  if (conZPos < -0.2f && !controllerZ1Active) {
    stateV1 = true;
    controllerZ1Active = true;
    println("stateV1 initiated (Controller)");
    sendToArduino('V', 999);
    println("SERVO MOVEMENT!");
  }
  if (conZPos > -0.2f && controllerZ1Active) {
    stateV1 = false;
    controllerZ1Active = false;
    sendToArduino('V', 888);
    println("stateV1 terminated");
    println("");
  }
  if (conZPos > 0.2f && !controllerZ2Active) {
    stateV2 = true;
    controllerZ2Active = true;
    println("stateV2 initiated (Controller)");
    sendToArduino('V', 777);
    println("SERVO MOVEMENT!");
  }
  if (conZPos < 0.2f && controllerZ2Active) {
    stateV2 = false;
    controllerZ2Active = false;
    sendToArduino('V', 888);
    println("stateV2 terminated");
    println("");
  }

  if (conTriPos < -0.2f && !controllerC1Active) {
    stateC1 = true;
    controllerC1Active = true;
    println("stateC1 initiated (Controller)");
    sendToArduino('C', 999);
    println("SERVO MOVEMENT!");
  }
  if (conTriPos > -0.2f && controllerC1Active) {
    stateC1 = false;
    controllerC1Active = false;
    sendToArduino('C', 888);
    println("stateC1 terminated");
    println("");
  }
  if (conTriPos > 0.2f && !controllerC2Active) {
    stateC2 = true;
    controllerC2Active = true;
    println("stateC2 initiated (Controller)");
    sendToArduino('C', 777);
    println("SERVO MOVEMENT!");
  }
  if (conTriPos < 0.2f && controllerC2Active) {
    stateC2 = false;
    controllerC2Active = false;
    sendToArduino('C', 888);
    println("stateC2 terminated");
    println("");
  }

  if (hatUp && !controllerHatUpActive) {
    statePos1 = true;
    controllerHatUpActive = true;

    if (stateSetPos) {
      pos1C = angleC;
      pos1H = angleH;
      pos1V = angleV;
      pos1F = angleF;
      println("Pos1 set");
    } else {
      sendToArduino('C', pos1C);
      sendToArduino('V', pos1V);
      sendToArduino('H', pos1H);
      sendToArduino('F', pos1F);
    }
  }
  if (controllerHatUpActive && !hatUp) {
    statePos1 = false;
    controllerHatUpActive = false;
  }
  
  if (hatLeft && !controllerHatLeftActive) {
    statePos2 = true;
    controllerHatLeftActive = true;

    if (stateSetPos) {
      pos2C = angleC;
      pos2H = angleH;
      pos2V = angleV;
      pos2F = angleF;
      println("Pos2 set");
    } else {
      sendToArduino('C', pos2C);
      sendToArduino('V', pos2V);
      sendToArduino('H', pos2H);
      sendToArduino('F', pos2F);
    }
  }
  if (controllerHatLeftActive && !hatLeft) {
    statePos2 = false;
    controllerHatLeftActive = false;
  }
  
  if (hatDown && !controllerHatDownActive) {
    statePos3 = true;
    controllerHatDownActive = true;

    if (stateSetPos) {
      pos3C = angleC;
      pos3H = angleH;
      pos3V = angleV;
      pos3F = angleF;
      println("Pos3 set");
    } else {
      sendToArduino('C', pos3C);
      sendToArduino('V', pos3V);
      sendToArduino('H', pos3H);
      sendToArduino('F', pos3F);
    }
  }
  if (controllerHatDownActive && !hatDown) {
    statePos3 = false;
    controllerHatDownActive = false;
  }
  
  if (hatRight && !controllerHatRightActive) {
    controllerHatRightActive = true;
    stateSetPos = !stateSetPos;
    println("setState toggled");
  }
  if (controllerHatRightActive && !hatRight) {
    controllerHatRightActive = false;
  }
}
//Die verschiedenen States des Programmes
boolean stateOpen, stateClose, stateReset, stateConnect;
boolean statePos1, statePos2, statePos3, stateSetPos;
boolean stateC1, stateC2, stateV1, stateV2, stateH1, stateH2, stateF1, stateF2;

int pos1C = 35; 
int pos2C = 35;
int pos3C = 35;
int pos1H = 90;
int pos2H = 90;
int pos3H = 90;
int pos1V = 90;
int pos2V = 90;
int pos3V = 90;
int pos1F = 90;
int pos2F = 90;
int pos3F = 90;

//Fuers manuelle Einstellen
int servoValueC, servoValueV, servoValueH, servoValueF;

boolean controllerConnected = false;

boolean controllerX1Active = false;
boolean controllerY1Active = false;
boolean controllerZ1Active = false;
boolean controllerC1Active = false;
boolean controllerX2Active = false;
boolean controllerY2Active = false;
boolean controllerZ2Active = false;
boolean controllerC2Active = false;
boolean controllerBActive = false;
boolean controllerAActive = false;
boolean controllerYActive = false;
boolean controllerHatUpActive = false;
boolean controllerHatDownActive = false;
boolean controllerHatLeftActive = false;
boolean controllerHatRightActive = false;

//Position der Buttons
int circleCX, circleCY;
int circleVX, circleVY;
int circleHX, circleHY;
int circleFX, circleFY;
int rectOX, rectOY;
int rectCX, rectCY;
int rectRX, rectRY;
int rectConX, rectConY;

//Datenfelder f\u00fcr Buttons
int circleSize = 20;
int rectSizeX = 60;
int rectSizeY = 25;
int circleColor, rectColor, triangleColor;
int circleHighlight, rectHighlight, triangleHighlight;
int chosenColor, resetColor;

boolean rectOverO = false;
boolean rectOverC = false;
boolean rectOverR = false;
boolean rectOverCon = false;
boolean rectOverPos1 = false;
boolean rectOverPos2 = false;
boolean rectOverPos3 = false;
boolean rectOverSetPos = false;

boolean switchOverC1 = false;
boolean switchOverC2 = false;
boolean switchOverV1 = false;
boolean switchOverV2 = false;
boolean switchOverH1 = false;
boolean switchOverH2 = false;
boolean switchOverF1 = false;
boolean switchOverF2 = false;


//Anzahl Digits des servoValues
int lengthOfServoValue;

//Entg\u00fcltige Daten, die an den Arduino gesendet werden
String command, prevCommand;


//Controller Data
float conXPos, conYPos, conZPos, conTriPos;
boolean APressed, BPressed, YPressed;
boolean hatLeft, hatRight, hatUp, hatDown;

//Send to Arduino
boolean stateConR, stateConL, stateStop;

//GUI
String prevClawInput, prevVerticalInput, prevHorizontalInput, prevForwardInput;
String clawInput = "";
String verticalInput = "";
String horizontalInput = "";
String forwardInput = "";

//Servobegrenzung
int clawMax = 165;
int clawMin = 35;
int verticalMax = 135;
int verticalMin = 0;
int horizontalMax = 180;
int horizontalMin = 0;
int forwardMax = 180;
int forwardMin = 55;

//F\u00fcr serielle Kommunikation
String incomingByte;
boolean start = true;
char servoSent;
int angleSent;
int angleC = -1;
int angleV = -1;
int angleH = -1;
int angleF = -1;
//Zeichne Kreise f\u00fcr Claw, Vertical, Horizontal und Forward State
public void circleDraw(boolean state, int x, int y) {
  if (state) {
    fill(chosenColor);
  }
  else {
    fill(circleColor);
  }
  stroke(255);
  ellipse(x, y, circleSize, circleSize);
}




//Zeichne Rechtecke f\u00fcr Buttons Open, Close und Reset
public void rectDraw(boolean state, boolean over, int x, int y) {
  if (state) {
    fill(chosenColor);
  }
  else if (over) {
    fill(rectHighlight);
  } 
  else {
    fill(rectColor);
  }
  stroke(255);
  rect(x, y, rectSizeX, rectSizeY);
}




//Zeichne Controller Buttons
public void buttonDraw(boolean pressed, int colorFill, int colorChosen, int x, int y) {
  if (pressed) {
    fill(colorChosen);
  }
  else {
    fill(colorFill);
  }
  stroke(255);
  ellipse(x + 90, y + 11, circleSize, circleSize);
}


//Zeichne Dreiecke zum Steuern
public void triangleDraw(boolean pressed, boolean over, int x1, int y1, int x2, int y2, int x3, int y3) {
  if(pressed) {
    fill(chosenColor);
  }
  else if(over) {
    fill(triangleHighlight);
  }
  else {
    fill(triangleColor);
  }
  stroke(255);
  triangle(x1, y1, x2, y2, x3, y3);
}







  
  
public void label() {
  //---TEXT------------------------------------------------------------------
  //Label f\u00fcr Klaue
  fill(0, 102, 153);
  text("Claw", 115, 50);
  
  //Label f\u00fcr Vertical
  fill(0, 102, 153);
  text("Vertical", 236, 50);
  
  //Label f\u00fcr Horizontal
  fill(0, 102, 153);
  text("Horizontal", 356, 50);
  
  //Label f\u00fcr Forward
  fill(0, 102, 153);
  text("Forward", 490, 50);
  
  //Label f\u00fcr Controller Button A
  fill(255);
  text("B", rectOX + 87, rectOY + 15);
  
  //Label f\u00fcr Controller Button B
  fill(255);
  text("A", rectCX + 87, rectCY + 15);
  
  //Label f\u00fcr Controller Button Y
  fill(255);
  text("Y", rectRX + 87, rectRY + 15);
  
  //Label f\u00fcr Open
  fill(255);
  text("OPEN", 119, 258);
  
  //Label f\u00fcr Close
  fill(255);
  text("CLOSE", 116, 298);
  
  //Label f\u00fcr Reset
  fill(255);
  text("RESET", 116, 338);
  
  //Label f\u00fcr Connect
  fill(255);
  text("CONNECT", 562, 258);
  
  //Label f\u00fcr Pos1
  fill(255);
  text("POS 1", 274, 258);
  
  //Label f\u00fcr Pos2
  fill(255);
  text("POS 2", 235, 298);
  
  //Label f\u00fcr Pos3
  fill(255);
  text("POS 3", 274, 338);
  
  //Label f\u00fcr SetPos
  fill(255);
  text("SET", 321, 298);
  
  //Label f\u00fcr Name
  fill(0, 102, 153);
  text("Roboterarm Steuerung von Christopher Katins", 360, 352);
  
  
 
  
  
  //------CONTROLLER--------------------------------------------------------
  //Controller Labels
  fill(0, 102, 153);
  text("Controller Data:", 385, 250);
  text("X Position:", 385, 270);
  text("Y Position:", 385, 285);
  text("Z Position:", 385, 300);
  text("Trigger Position:", 385, 315);
  
  //Controller Data
  fill(0);
  text(conXPos, 500, 270);
  text(conYPos, 500, 285);
  text(conZPos, 500, 300);
  text(conTriPos, 500, 315);
  
  //Controller Pending
  if(!controllerConnected) {
    fill(245, 44, 85);
    text("Pending", 568, 280);
  }
  
  
  
  //------ANGLES---------------------------------------------------------
  fill(255, 204, 0);
  text(angleC, 120, 220);
  text(angleV, 248, 220);
  text(angleH, 376, 220);
  text(angleF, 504, 220);
}
/*
* Sende gesammelte Daten \u00fcber serial COM zum Arduino
*
* SSS als Startbit
* C oder V oder H oder F als Servofestlegung
* 0 bis 180 als Gradzahl ODER
* 777 f\u00fcr kontinuierlich links
* 888 zum Stoppen
* 999 f\u00fcr kontinuerlich rechts
*
*/
public void sendToArduino(char servo, int servoValue) {
  //startBytes, damit der Arduino Folgendes ausf\u00fchrt
  command = "SSS";  
  command += servo;
    
  //Ist servoValue nur zwei Digits lang, h\u00e4nge vorne ein 'X' vor, da der Arduino immer von drei Digits ausgeht
  //Ist servoValue nur ein Digit lang, h\u00e4nge vorne ein 'XX' vor, da der Arduino immer von drei Digits ausgeht
  lengthOfServoValue = String.valueOf(servoValue).length();
  for(int i = lengthOfServoValue; i < 3; i++) {
    command += 'X';
  }
  
  //H\u00e4nge servoValue an
  command += servoValue; 
  
  //Schicke den entg\u00fcltigen Wert, wenn dieser sich unterscheidet vom vorherigen
  if(!command.equals(prevCommand)) {
    myPort.write(command);
    print("Command sent: ");
    println(command);
    delay(50);
    prevCommand = command;
  }
}






/*
* Update der Lage der Maus
*/
public void updateMouseLocation() {
   rectOverO = false;
   rectOverC = false;
   rectOverR = false;
   rectOverCon = false;
   rectOverPos1 = false;
   rectOverPos2 = false;
   rectOverPos3 = false;
   rectOverSetPos = false;
   switchOverC1 = false;
   switchOverC2 = false;
   switchOverV1 = false;
   switchOverV2 = false;
   switchOverH1 = false;
   switchOverH2 = false;
   switchOverF1 = false;
   switchOverF2 = false;
   
  if (overRect(rectOX, rectOY, rectSizeX, rectSizeY) ) {
    rectOverO = true;
  }
  else if (overRect(rectCX, rectCY,rectSizeX, rectSizeY) ) {
    rectOverC = true;
  }
  else if (overRect(rectRX, rectRY, rectSizeX, rectSizeY) ) {
    rectOverR = true;
  }
  else if (overRect(rectConX, rectConY, rectSizeX, rectSizeY) ) {
    rectOverCon = true;
  }
  else if (overRect(260, rectOY, rectSizeX, rectSizeY) ) {
    rectOverPos1 = true;
  }
  else if (overRect(220, rectCY , rectSizeX, rectSizeY) ) {
    rectOverPos2 = true;
  }
  else if (overRect(260, rectRY, rectSizeX, rectSizeY) ) {
    rectOverPos3 = true;
  }
  else if (overRect(300, rectCY, rectSizeX, rectSizeY) ) {
    rectOverSetPos = true;
  }
  else if (overCircle(circleCX, circleCY + 40, 30)) {
    switchOverC1 = true;
  }
  else if (overCircle(circleCX, circleCY + 70, 30)) {
    switchOverC2 = true;
  }
  else if (overCircle(circleVX, circleVY + 40, 30)) {
    switchOverV1 = true;
  }
  else if (overCircle(circleVX, circleVY + 70, 30)) {
    switchOverV2 = true;
  }
  else if (overCircle(circleHX - 15, circleHY + 55, 30)) {
    switchOverH1 = true;
  }
  else if (overCircle(circleHX + 15, circleHY + 55, 30)) {
    switchOverH2 = true;
  }
  else if (overCircle(circleFX, circleFY + 40, 30)) {
    switchOverF1 = true;
  }
  else if (overCircle(circleFX, circleFY + 70, 30)) {
    switchOverF2 = true;
  }
}


/*
* Findet heraus, ob Maus \u00fcber Kreis ist
*/
public boolean overCircle(int x, int y, int diameter) {
  float disX = x - mouseX;
  float disY = y - mouseY;
  if (sqrt(sq(disX) + sq(disY)) < diameter/2 ) {
    return true;
  } 
  else {
    return false;
  }
}


/*
* Findet heraus, ob Maus \u00fcber Rechteck ist
*/
public boolean overRect(int x, int y, int width, int height)  {
  if (mouseX >= x && mouseX <= x+width && 
      mouseY >= y && mouseY <= y+height) {
    return true;
  } else {
    return false;
  }
}



public void mousePressed() {
  if(rectOverO) {
    println("stateOpen initiated");
    stateOpen = true;
    sendToArduino('C', 35);
    println("stateOpen terminated");
    println("");
  }
  if(rectOverC) {
    println("stateClose initiated");
    stateClose = true;
    sendToArduino('C', 165);
    println("stateClose terminated");
    println("");
  }
  if(rectOverR) {
    println("stateReset initiated");
    stateReset = true;
    sendToArduino('C', 35);
    sendToArduino('H', 90);
    sendToArduino('V', 90);
    sendToArduino('F', 90);
    println("stateReset terminated");
    println("");
  }
  if(rectOverCon) {
    stateConnect = true;
    println("stateConnect initiated");
  }
  if(rectOverPos1) {
    statePos1 = true;
    if(stateSetPos) {
      pos1C = angleC;
      pos1H = angleH;
      pos1V = angleV;
      pos1F = angleF;
      println("Pos1 set");
    }
    else {
      sendToArduino('C', pos1C);
      sendToArduino('V', pos1V);
      sendToArduino('H', pos1H);
      sendToArduino('F', pos1F);
    }
  }
  if(rectOverPos2) {
    statePos2 = true;
    if(stateSetPos) {
      pos2C = angleC;
      pos2H = angleH;
      pos2V = angleV;
      pos2F = angleF;
      println("Pos2 set");
    }
    else {
      sendToArduino('C', pos2C);
      sendToArduino('V', pos2V);
      sendToArduino('H', pos2H);
      sendToArduino('F', pos2F);
    }
  }
  if(rectOverPos3) {
    statePos3 = true;
    if(stateSetPos) {
      pos3C = angleC;
      pos3H = angleH;
      pos3V = angleV;
      pos3F = angleF;
      println("Pos3 set");
    }
    else {
      sendToArduino('C', pos3C);
      sendToArduino('V', pos3V);
      sendToArduino('H', pos3H);
      sendToArduino('F', pos3F);
    }
  }
  if(rectOverSetPos) {
    stateSetPos = !stateSetPos;
    
  }
  if(switchOverC1) {
    stateC1 = true;
    println("stateC1 initiated");
    sendToArduino('C', 999);
    println("SERVO MOVEMENT!");
  }
  if(switchOverC2) {
    stateC2 = true;
    println("stateC2 initiated");
    sendToArduino('C', 777);
    println("SERVO MOVEMENT!");
  }
  if(switchOverV1) {
    stateV1 = true;
    println("stateV1 initiated");
    sendToArduino('V', 999);
    println("SERVO MOVEMENT!");
  }
  if(switchOverV2) {
    stateV2 = true;
    println("stateV2 initiated");
    sendToArduino('V', 777);
    println("SERVO MOVEMENT!");
  }
  if(switchOverH1) {
    stateH1 = true;
    println("stateH1 initiated");
    sendToArduino('H', 999);
    println("SERVO MOVEMENT!");
  }
  if(switchOverH2) {
    stateH2 = true;
    println("stateH2 initiated");
    sendToArduino('H', 777);
    println("SERVO MOVEMENT!");
  }
  if(switchOverF1) {
    stateF1 = true;
    println("stateF1 initiated");
    sendToArduino('F', 999);
    println("SERVO MOVEMENT!");
  }
  if(switchOverF2) {
    stateF2 = true;
    println("stateF2 initiated");
    sendToArduino('F', 777);
    println("SERVO MOVEMENT!");
  }
}

public void mouseReleased() {
   stateOpen = false;
   stateClose = false;
   stateReset = false;
   statePos1 = false;
   statePos2 = false;
   statePos3 = false;
   
   if(stateC1) {
     stateC1 = false;
     sendToArduino('C', 888);
     println("stateC1 terminated");
     println("");
   }
   if(stateC2) {
     stateC2 = false;
     sendToArduino('C', 888);
     println("stateC2 terminated");
     println("");
   }
   if(stateV1) {
     stateV1 = false;
     sendToArduino('V', 888);
     println("stateV1 terminated");
     println("");
   }
   if(stateV2) {
     stateV2 = false;
     sendToArduino('V', 888);
     println("stateV2 terminated");
     println("");
   }
   if(stateH1) {
     stateH1 = false;
     sendToArduino('H', 888);
     println("stateH1 terminated");
     println("");
   }
   if(stateH2) {
     stateH2 = false;
     sendToArduino('H', 888);
     println("stateH2 terminated");
     println("");
   }
   if(stateF1) {
     stateF1 = false;
     sendToArduino('F', 888);
     println("stateF1 terminated");
     println("");
   }
   if(stateF2) {
     stateF2 = false;
     sendToArduino('F', 888);
     println("stateF2 terminated");
     println("");
   }
}

//F\u00fcr GUI
public void controlEvent(ControlEvent theEvent) {
  if(theEvent.isAssignableFrom(Textfield.class)) {
    println("controlEvent: accessing a string from controller '"
            +theEvent.getName()+"': "
            +theEvent.getStringValue()
            );
  } 
}

public void manualInput() {
  if(clawInput != null && !clawInput.isEmpty() && prevClawInput != clawInput) {
    try {
      Integer.parseInt(clawInput);
    } catch (NumberFormatException e) {
      clawInput = null;
      System.err.println("No number at manual claw input!");
    }
    
    if(clawInput != null) {
      servoValueC = Integer.parseInt(clawInput);
      
      //Max und min Werte f\u00fcr Servos
      servoValueC = (servoValueC > clawMin) ? servoValueC : clawMin;
      servoValueC = (servoValueC < clawMax) ? servoValueC : clawMax;
      if(servoValueC != Integer.parseInt(clawInput)) {
        System.err.println("ServoValue out of range, corrected to nearest valid value");
      }
  
      sendToArduino('C', servoValueC);
      print("Manually updated Claw ServoValue to: ");
      println(servoValueC);
      println("");
      prevClawInput = clawInput;
    }
  }
  
  
  
  if(verticalInput != null && !verticalInput.isEmpty() && prevVerticalInput != verticalInput) {
    try {
      Integer.parseInt(verticalInput);
    } catch (NumberFormatException e) {
      verticalInput = null;
      System.err.println("No number at manual vertical input!");
    }
    
    if(verticalInput != null) {
      servoValueV = Integer.parseInt(verticalInput);
      
      //Max und min Werte f\u00fcr Servos
      servoValueV = (servoValueV > verticalMin) ? servoValueV : verticalMin;
      servoValueV = (servoValueV < verticalMax) ? servoValueV : verticalMax;
      if(servoValueV != Integer.parseInt(verticalInput)) {
        System.err.println("ServoValue out of range, corrected to nearest valid value");
      }
      
      sendToArduino('V', servoValueV);
      print("Manually updated Vertical ServoValue to: ");
      println(servoValueV);
      println("");
      prevVerticalInput = verticalInput;
    }
  }
  
  
  
  if(horizontalInput != null && !horizontalInput.isEmpty() && prevHorizontalInput != horizontalInput) {
    try {
      Integer.parseInt(horizontalInput);
    } catch (NumberFormatException e) {
      horizontalInput = null;
      System.err.println("No number at manual horizontal input!");
    }
    
    if(horizontalInput != null) {
      servoValueH = Integer.parseInt(horizontalInput);
      
      //Max und min Werte f\u00fcr Servos
      servoValueH = (servoValueH > horizontalMin) ? servoValueH : horizontalMin;
      servoValueH = (servoValueH < horizontalMax) ? servoValueH : horizontalMax;
      if(servoValueH != Integer.parseInt(horizontalInput)) {
        System.err.println("ServoValue out of range, corrected to nearest valid value");
      }
      
      sendToArduino('H', servoValueH);
      print("Manually updated Horizontal ServoValue to: ");
      println(servoValueH);
      println("");
      prevHorizontalInput = horizontalInput;
    }
  }
  
  
  
  if(forwardInput != null && !forwardInput.isEmpty() && prevForwardInput != forwardInput) {
    try {
      Integer.parseInt(forwardInput);
    } catch (NumberFormatException e) {
      forwardInput = null;
      System.err.println("No number at manual forward input!");
    }
    
    if(forwardInput != null) {
      servoValueF = Integer.parseInt(forwardInput);
      
      //Max und min Werte f\u00fcr Servos
      servoValueF = (servoValueF > forwardMin) ? servoValueF : forwardMin;
      servoValueF = (servoValueF < forwardMax) ? servoValueF : forwardMax;
      if(servoValueF != Integer.parseInt(forwardInput)) {
        System.err.println("ServoValue out of range, corrected to nearest valid value");
      }
      
      sendToArduino('F', servoValueF);
      print("Manually updated Forward ServoValue to: ");
      println(servoValueF);
      println("");
      prevForwardInput = forwardInput;
    }
  }
}
  public void settings() {  size(640, 360); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Steuerung_Roboterarm" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
