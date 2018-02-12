/* //<>//
* Roboterarm Steuerung
 *
 * Steuere den MeArm (o.Ä.) über die GUI oder den XBOX Controller
 *
 * In der Gui kann der Sollwert der Servos im Textfeld eingegeben werden
 * Dazu gibt es Tasten, die die Claw öffnen, schließen und alle Servos resetten 
 * Ebenso kann mit der Connect Taste der Controller verbunden werden
 * Werden die Pfeil Buttons gehalten, bewegt sich der jeweilige Servo bis die Maus wieder losgelassen wird
 * Selbiges gilt für die Sticks des Cotrollers
 * 
 * Kommuniziert wird über den serial Port, mit folgendem Protokoll:
 * 'SSS' als Startbit
 * 'C' oder 'H' oder 'V' oder 'F' für den Servo, dann '0' bis '180' für den Winkel
 *  oder '777' für kontinuierlich links, '888' für Stopp, '999' für kontinuierlich rechts
 * 
 *
 *
 */


import processing.serial.*;

//Für Controllerverwendung
import org.gamecontrolplus.gui.*;
import org.gamecontrolplus.*;
import net.java.games.input.*;

//Für GUI
import controlP5.*;

Serial myPort;

//Für Controllerverwendung
ControlIO control;
Configuration config;
ControlDevice gpad;

//Für GUI
ControlP5 cp5;




void setup() {
  //Zeige alle Serial Ports, um den passenden zu finden
  //printArray(Serial.list());

  //Setze passenden Port mit einer Rate von 9600
  myPort = new Serial(this, Serial.list()[1], 9600);
  println("Bereit für Eingabe");

  //Interface erstellen und Farben festlegen
  size(640, 360);

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











void draw() {
  //Mausposition bestimmen und Hintergrund zeichnen
  updateMouseLocation();
  background(167);

  //Zeichne Kreise für Claw, Vertical, Horizontal und Forward  
  circleDraw((stateC1 || stateC2 || stateOpen || stateClose || stateReset), circleCX, circleCY);
  circleDraw((stateV1 || stateV2 || stateReset), circleVX, circleVY);
  circleDraw((stateH1 || stateH2 || stateReset), circleHX, circleHY);
  circleDraw((stateF1 || stateF2 || stateReset), circleFX, circleFY);

  //Zeichne Rechtecke für Buttons
  rectDraw(stateOpen, rectOverO, rectOX, rectOY);
  rectDraw(stateClose, rectOverC, rectCX, rectCY);
  rectDraw(stateReset, rectOverR, rectRX, rectRY);
  rectDraw(stateConnect, rectOverCon, rectConX, rectConY);
  
  //Zeichne Rechtecke für Positionsbuttons
  rectDraw(statePos1, rectOverPos1, 260, rectOY);
  rectDraw(statePos2, rectOverPos2, 220, rectCY);
  rectDraw(statePos3, rectOverPos3, 260, rectRY);
  rectDraw(stateSetPos, rectOverSetPos, 300, rectCY);

  //Controller Buttons zeichnen  
  buttonDraw(APressed, color(60, 245, 44), chosenColor, rectCX, rectCY);
  buttonDraw(BPressed, color(245, 44, 85), chosenColor, rectOX, rectOY);
  buttonDraw(YPressed, chosenColor, color(245, 44, 85), rectRX, rectRY);

  //Zeichne Dreiecke für Steuerung
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