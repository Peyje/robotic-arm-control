/*
* Sende gesammelte Daten über serial COM zum Arduino
*
* SSS als Startbit
* C oder V oder H oder F als Servofestlegung
* 0 bis 180 als Gradzahl ODER
* 777 für kontinuierlich links
* 888 zum Stoppen
* 999 für kontinuerlich rechts
*
*/
void sendToArduino(char servo, int servoValue) {
  //startBytes, damit der Arduino Folgendes ausführt
  command = "SSS";  
  command += servo;
    
  //Ist servoValue nur zwei Digits lang, hänge vorne ein 'X' vor, da der Arduino immer von drei Digits ausgeht
  //Ist servoValue nur ein Digit lang, hänge vorne ein 'XX' vor, da der Arduino immer von drei Digits ausgeht
  lengthOfServoValue = String.valueOf(servoValue).length();
  for(int i = lengthOfServoValue; i < 3; i++) {
    command += 'X';
  }
  
  //Hänge servoValue an
  command += servoValue; 
  
  //Schicke den entgültigen Wert, wenn dieser sich unterscheidet vom vorherigen
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
void updateMouseLocation() {
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
* Findet heraus, ob Maus über Kreis ist
*/
boolean overCircle(int x, int y, int diameter) {
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
* Findet heraus, ob Maus über Rechteck ist
*/
boolean overRect(int x, int y, int width, int height)  {
  if (mouseX >= x && mouseX <= x+width && 
      mouseY >= y && mouseY <= y+height) {
    return true;
  } else {
    return false;
  }
}



void mousePressed() {
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

void mouseReleased() {
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

//Für GUI
void controlEvent(ControlEvent theEvent) {
  if(theEvent.isAssignableFrom(Textfield.class)) {
    println("controlEvent: accessing a string from controller '"
            +theEvent.getName()+"': "
            +theEvent.getStringValue()
            );
  } 
}

void manualInput() {
  if(clawInput != null && !clawInput.isEmpty() && prevClawInput != clawInput) {
    try {
      Integer.parseInt(clawInput);
    } catch (NumberFormatException e) {
      clawInput = null;
      System.err.println("No number at manual claw input!");
    }
    
    if(clawInput != null) {
      servoValueC = Integer.parseInt(clawInput);
      
      //Max und min Werte für Servos
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
      
      //Max und min Werte für Servos
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
      
      //Max und min Werte für Servos
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
      
      //Max und min Werte für Servos
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