//Zeichne Kreise für Claw, Vertical, Horizontal und Forward State
void circleDraw(boolean state, int x, int y) {
  if (state) {
    fill(chosenColor);
  }
  else {
    fill(circleColor);
  }
  stroke(255);
  ellipse(x, y, circleSize, circleSize);
}




//Zeichne Rechtecke für Buttons Open, Close und Reset
void rectDraw(boolean state, boolean over, int x, int y) {
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
void buttonDraw(boolean pressed, color colorFill, color colorChosen, int x, int y) {
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
void triangleDraw(boolean pressed, boolean over, int x1, int y1, int x2, int y2, int x3, int y3) {
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







  
  