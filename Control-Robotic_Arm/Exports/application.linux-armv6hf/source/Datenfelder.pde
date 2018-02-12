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

//Datenfelder für Buttons
int circleSize = 20;
int rectSizeX = 60;
int rectSizeY = 25;
color circleColor, rectColor, triangleColor;
color circleHighlight, rectHighlight, triangleHighlight;
color chosenColor, resetColor;

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

//Entgültige Daten, die an den Arduino gesendet werden
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

//Für serielle Kommunikation
String incomingByte;
boolean start = true;
char servoSent;
int angleSent;
int angleC = -1;
int angleV = -1;
int angleH = -1;
int angleF = -1;