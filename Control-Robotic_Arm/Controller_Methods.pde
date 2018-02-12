void setupController() {
  // Initialise die ControlIO
  control = ControlIO.getInstance(this);
  // Finde Gerät, dass zur Beschreibung passt
  gpad = control.getMatchedDevice("XBOX Controller");
  if (gpad == null) {
    println("No suitable device configured");
    System.exit(-1);
  }
}



void controllerInput() {
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

  if (conXPos < -0.2 && !controllerX1Active) {
    stateH1 = true;
    controllerX1Active = true;
    println("stateH1 initiated (Controller)");
    sendToArduino('H', 999);
    println("SERVO MOVEMENT!");
  }
  if (conXPos > -0.2 && controllerX1Active) {
    stateH1 = false;
    controllerX1Active = false;
    sendToArduino('H', 888);
    println("stateH1 terminated");
    println("");
  }
  if (conXPos > 0.2 && !controllerX2Active) {
    stateH2 = true;
    controllerX2Active = true;
    println("stateH2 initiated (Controller)");
    sendToArduino('H', 777);
    println("SERVO MOVEMENT!");
  }
  if (conXPos < 0.2 && controllerX2Active) {
    stateH2 = false;
    controllerX2Active = false;
    sendToArduino('H', 888);
    println("stateH2 terminated");
    println("");
  }

  if (conYPos < -0.2 && !controllerY1Active) {
    stateF1 = true;
    controllerY1Active = true;
    println("stateF1 initiated (Controller)");
    sendToArduino('F', 999);
    println("SERVO MOVEMENT!");
  }
  if (conYPos > -0.2 && controllerY1Active) {
    stateF1 = false;
    controllerY1Active = false;
    sendToArduino('F', 888);
    println("stateF1 terminated");
    println("");
  }
  if (conYPos > 0.2 && !controllerY2Active) {
    stateF2 = true;
    controllerY2Active = true;
    println("stateF2 initiated (Controller)");
    sendToArduino('F', 777);
    println("SERVO MOVEMENT!");
  }
  if (conYPos < 0.2 && controllerY2Active) {
    stateF2 = false;
    controllerY2Active = false;
    sendToArduino('F', 888);
    println("stateF2 terminated");
    println("");
  }

  if (conZPos < -0.2 && !controllerZ1Active) {
    stateV1 = true;
    controllerZ1Active = true;
    println("stateV1 initiated (Controller)");
    sendToArduino('V', 999);
    println("SERVO MOVEMENT!");
  }
  if (conZPos > -0.2 && controllerZ1Active) {
    stateV1 = false;
    controllerZ1Active = false;
    sendToArduino('V', 888);
    println("stateV1 terminated");
    println("");
  }
  if (conZPos > 0.2 && !controllerZ2Active) {
    stateV2 = true;
    controllerZ2Active = true;
    println("stateV2 initiated (Controller)");
    sendToArduino('V', 777);
    println("SERVO MOVEMENT!");
  }
  if (conZPos < 0.2 && controllerZ2Active) {
    stateV2 = false;
    controllerZ2Active = false;
    sendToArduino('V', 888);
    println("stateV2 terminated");
    println("");
  }

  if (conTriPos < -0.2 && !controllerC1Active) {
    stateC1 = true;
    controllerC1Active = true;
    println("stateC1 initiated (Controller)");
    sendToArduino('C', 999);
    println("SERVO MOVEMENT!");
  }
  if (conTriPos > -0.2 && controllerC1Active) {
    stateC1 = false;
    controllerC1Active = false;
    sendToArduino('C', 888);
    println("stateC1 terminated");
    println("");
  }
  if (conTriPos > 0.2 && !controllerC2Active) {
    stateC2 = true;
    controllerC2Active = true;
    println("stateC2 initiated (Controller)");
    sendToArduino('C', 777);
    println("SERVO MOVEMENT!");
  }
  if (conTriPos < 0.2 && controllerC2Active) {
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