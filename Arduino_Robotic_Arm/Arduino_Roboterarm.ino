/* Steuerung des MeArms durch Processing
   author Christopher Katins
   version 0.4.0
*/

#include <Servo.h>

Servo middle, left, right, claw;

//Winkel für Servos
int angleV;  //Vertikal, Hoch & Runter (Servo Left)
int angleH;  //Horizontal, Links & Rechts (Servo Middle)
int angleF; //Forward, Vor & Zurück (Servo Rechts)
int angleC;  //Claw (Servo Klaue)
int prevAngleV;
int prevAngleH;
int prevAngleF;
int prevAngleC;

//Wert, der durch den Seriellen Monitor eingeht
int incomingByte;

//Variable, damit die Ausgangsstellung nur einmal eingenommen wird
boolean onlyOnce = true;

//Variable, die überprüft, wann der Startbit 'SSS' gesendet wurde
int startByteCounter;

//Variable, die überprüft, wie viele Digits von angleSent schon gesetzt wurden
int angleSetCounter;

//Variablen, die die nächste Anweisung bestimmen
char servoSent;
int angleSent;

//1 für rechts, 0 für links
int dirC, dirV, dirH, dirF;
boolean contC, contV, contH, contF;

int lengthOfServoValue = 0;

void setup() {
  Serial.begin(9600);
  middle.attach(11);
  left.attach(10);
  right.attach(9);
  claw.attach(6);

  //Serial.println("Ready! Servos are attached and aligned. Waiting for input!");
  //Serial.println("");
  //Serial.println("A valid command needs a start sequence, a servo character and an angle value:");
  //Serial.println("Start sequence: 'SSS'");
  //Serial.println("Servo character: 'C' & 'H' & 'V' & 'F'");
  //Serial.println("Angle value: 0 - 180");
  //Serial.println("Attention: Number has to be 3 digits, use 'X' for free digits  (Example: X35)");
  //Serial.println("Example: SSSC165                                           (Closing the claw)");
  //Serial.println("");
}

void loop() {

  //Ausgangsstellung, wird nur einmal ausgeführt
  if (onlyOnce) {
    angleV = 90;
    angleH = 90;
    angleF = 90;
    angleC = 35;

    update('V', angleV);
    update('H', angleH);
    update('F', angleF);
    update('C', angleC);
    
    onlyOnce = false;
  }

  //Zahl im Seriellen Monitor eingeben und senden
  if (Serial.available() > 0) {
    incomingByte = Serial.read();


    //   [3]
    //Wenn servoSent feststeht, werden die nächsten drei incomingBytes als angleSent gesetzt
    if (servoSent == 'C' || servoSent == 'H' || servoSent == 'V' || servoSent == 'F') {
      if (incomingByte != 'X') { //Zweistellige Zahlen bekommen ein 'X' vor, Einstellige Zahlen 'XX', diese werden hier ignoriert
        angleSent = angleSent * 10 + (incomingByte - '0');
        //Serial.print("angleSent Digit ");
        //Serial.print(angleSetCounter);
        //Serial.print(" saved at: ");
        //Serial.println(angleSent);
      }
      angleSetCounter++;
    }

    //   [4]
    //Update nur die Servos, wenn servoSent gesetzt wurde und alle drei Digits von angleSent feststehen
    if (servoSent != 'X' && angleSetCounter == 3) {
      update(servoSent, angleSent);
    }

    //   [2]
    //Wurden drei startBytes erkannt, setze servoSent mit nächstem incomingByte
    if (startByteCounter == 3) {
      servoSent = incomingByte;
      //Serial.print("servoSent saved at: ");
      //Serial.println(servoSent);
      startByteCounter = 0;
      //Serial.println("startByteCounter Reset (servoSent set)");
    }

    //   [1]
    //Zählt die incomingBytes nach 'S', dem Startbyte, ab
    if (incomingByte == 'S') {
      startByteCounter++;
      //Serial.print("startByteCounter at: ");
      //Serial.println(startByteCounter);
    }
    else {
      startByteCounter = 0;
    }
  }  

  if(contC) {
    //Serial.println("Kontinuierliche Bewegung der Claw läuft!");
    if(dirC == 1) {
      angleC++;  
    } 
    else {
      angleC--;  
    }
    angleC = (angleC > 35) ? angleC : 35;
    angleC = (angleC < 165) ? angleC : 165;
    if(angleC <= 35 || angleC >= 165) {
      contC = false;  
      //Serial.println("Kontinuerliche Bewegung beendet, Endwert erreicht");
    }
    //Serial.print("angleC updated with: ");
    //Serial.println(angleC); 
    claw.write(angleC);
    delay(30);
    //Serial.println("Claw updated");
  }

  if(contV) {
    //Serial.println("Kontinuierliche Bewegung der Vertical läuft!");
    if(dirV == 1) {
      angleV++;  
    } 
    else {
      angleV--;  
    }
    angleV = (angleV > 0) ? angleV : 0;
    angleV = (angleV < 180) ? angleV : 135;
    if(angleV <= 0 || angleV >= 180) {
      contV = false;  
      //Serial.println("Kontinuerliche Bewegung beendet, Endwert erreicht");
    }
    //Serial.print("angleV updated with: ");
    //Serial.println(angleV); 
    left.write(angleV);
    delay(30);
    //Serial.println("Vertical updated");
  }

  if(contH) {
    //Serial.println("Kontinuierliche Bewegung der Horizontal läuft!");
    if(dirH == 1) {
      angleH++;  
    } 
    else {
      angleH--;  
    }
    angleH = (angleH > 0) ? angleH : 0;
    angleH = (angleH < 180) ? angleH : 180;
    if(angleH <= 0 || angleH >= 180) {
      contH = false;  
      //Serial.println("Kontinuerliche Bewegung beendet, Endwert erreicht");
    }
    //Serial.print("angleH updated with: ");
    //Serial.println(angleH); 
    middle.write(angleH);
    delay(30);
    //Serial.println("Horizontal updated");
  }

  if(contF) {
    //Serial.println("Kontinuierliche Bewegung der Forward läuft!");
    if(dirF == 1) {
      angleF++;  
    } 
    else {
      angleF--;  
    }
    angleF = (angleF > 0) ? angleF : 55;
    angleF = (angleF < 180) ? angleF : 180;
    if(angleF <= 0 || angleC >= 180) {
      contF = false;  
      //Serial.println("Kontinuerliche Bewegung beendet, Endwert erreicht");
    }
    //Serial.print("angleF updated with: ");
    //Serial.println(angleF); 
    right.write(angleF);
    delay(30);
    //Serial.println("Forward updated");
  }
  
}




//Funktion für die die Ausgabe
//char und String konnte nicht in einem //Serial.print() ausgegeben werden
//Also char-Array erstellen, in dem Text steht und Stelle, an dem der char stehen soll, austauschen mit char
void Ausgabe(int angle, char c) {
  char buf1[20] = " angleVal   : ";
  buf1[9] = c;
  //Serial.println("");
  //Serial.print(buf1);
  //Serial.println(angle);
  //Serial.println("");
}

void update(int servo, int angle) {
  if (servo == 'C') {
    if(angle == 777) {
      contC = true;
      dirC = 0;
    }
    else if(angle == 888) {
      contC = false;
    }
    else if(angle == 999) {
      contC = true;
      dirC = 1;
    }
    else {   
      angleC = angle;
      //Serial.print("angleC updated with: ");
      //Serial.println(angleC);
    }
  }
  else if (servo == 'H') {
    if(angle == 777) {
      contH = true;
      dirH = 0;
    }
    else if(angle == 888) {
      contH = false;
    }
    else if(angle == 999) {
      contH = true;
      dirH = 1;
    }
    else {
      angleH = angle;
      //Serial.print("angleH updated with: ");
      //Serial.println(angleH);
    }
  }
  else if (servo == 'V') {
    if(angle == 777) {
      contV = true;
      dirV = 0;
    }
    else if(angle == 888) {
      contV = false;
    }
    else if(angle == 999) {
      contV = true;
      dirV = 1;
    }
    else {
      angleV = angle;
      //Serial.print("angleV updated with: ");
      //Serial.println(angleV);
    }
  }
  else if (servo == 'F') {
    if(angle == 777) {
      contF = true;
      dirF = 0;
    }
    else if(angle == 888) {
      contF = false;
    }
    else if(angle == 999) {
      contF = true;
      dirF = 1;
    }
    else {
      angleF = angle;
      //Serial.print("angleF updated with: ");
      //Serial.println(angleF);
    }
  }

  //Begrenzung der Claw, damit sie nicht überdreht oder zu stark zufasst
  angleC = (angleC > 35) ? angleC : 35;
  angleC = (angleC < 165) ? angleC : 165;

  //Begrenzung der anderen Servos
  angleV = (angleV > 0) ? angleV : 0;
  angleV = (angleV < 135) ? angleV : 135;
  angleH = (angleH > 0) ? angleH : 0;
  angleH = (angleH < 180) ? angleH : 180;
  angleF = (angleF > 55) ? angleF : 55;
  angleF = (angleF < 180) ? angleF : 180;
  
  //Serieller Monitor Output fürs Debuggen
  Ausgabe(angleH, 'H');
  Ausgabe(angleV, 'V');
  Ausgabe(angleC, 'C');
  Ausgabe(angleF, 'F');


  //Sende Winkel an Servos
  if(angleH != prevAngleH) {
    middle.write(angleH);
    prevAngleH = angleH;
    sendToProcessing('H', angleH);
  }
  if(angleV != prevAngleV) {
    left.write(angleV);
    prevAngleV = angleV;
    sendToProcessing('V', angleV);
  }
  if(angleF != prevAngleF) {
    right.write(angleF);
    prevAngleF = angleF;
    sendToProcessing('F', angleF);
  }
  if(angleC != prevAngleC) {
    claw.write(angleC);
    prevAngleC = angleC;
    sendToProcessing('C', angleC);
  }
  delay(30);
  //Serial.println("Servos updated!");

  servoSent = 'X';
  angleSent = 0;
  angleSetCounter = 0;
  startByteCounter = 0;
  //Serial.println("startByteCounter Reset, servoSent Reset, angleSent Reset, angleSetCounter Reset");
  //Serial.println("______________________________________");
}




void sendToProcessing(char servo, int servoValue) { 
  String command = "";
  command += servo;

  //Ist servoValue nur zwei Digits lang, hänge vorne ein 'X' vor, da der Arduino immer von drei Digits ausgeht
  //Ist servoValue nur ein Digit lang, hänge vorne ein 'XX' vor, da der Arduino immer von drei Digits ausgeht

  if((servoValue / 10) < 10) {
    command += 'X';
  }
  if(servoValue < 10) {
    command += 'X';  
  }
  
  //Hänge servoValue an
  command += servoValue; 
  
  //Schicke den entgültigen Wert
  Serial.println(command);
}



