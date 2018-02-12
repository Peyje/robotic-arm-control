void label() {
  //---TEXT------------------------------------------------------------------
  //Label für Klaue
  fill(0, 102, 153);
  text("Claw", 115, 50);
  
  //Label für Vertical
  fill(0, 102, 153);
  text("Vertical", 236, 50);
  
  //Label für Horizontal
  fill(0, 102, 153);
  text("Horizontal", 356, 50);
  
  //Label für Forward
  fill(0, 102, 153);
  text("Forward", 490, 50);
  
  //Label für Controller Button A
  fill(255);
  text("B", rectOX + 87, rectOY + 15);
  
  //Label für Controller Button B
  fill(255);
  text("A", rectCX + 87, rectCY + 15);
  
  //Label für Controller Button Y
  fill(255);
  text("Y", rectRX + 87, rectRY + 15);
  
  //Label für Open
  fill(255);
  text("OPEN", 119, 258);
  
  //Label für Close
  fill(255);
  text("CLOSE", 116, 298);
  
  //Label für Reset
  fill(255);
  text("RESET", 116, 338);
  
  //Label für Connect
  fill(255);
  text("CONNECT", 562, 258);
  
  //Label für Pos1
  fill(255);
  text("POS 1", 274, 258);
  
  //Label für Pos2
  fill(255);
  text("POS 2", 235, 298);
  
  //Label für Pos3
  fill(255);
  text("POS 3", 274, 338);
  
  //Label für SetPos
  fill(255);
  text("SET", 321, 298);
  
  //Label für Name
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