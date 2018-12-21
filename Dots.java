package com.company;
import java.awt.Graphics2D;

enum rowActions{
    MERGELEFT, MERGERIGHT, SEPARATELEFT, SEPARATERIGHT, STRAIGHT
}

public class Dots {
    private int x;
    private int y;
    private int curve;
    private int parentIndex;
    public rowActions action = rowActions.STRAIGHT;

    public Dots(int x_arg, int y_arg, int curveNum, int parent_index){
        x = x_arg;
        y = y_arg;
        curve = curveNum;
        parentIndex = parent_index;
    }

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getCurve() { return curve;}
    public rowActions getAction() { return action;}
    public int getParentIndex() { return parentIndex;}
    public void setX(int newX){
        x = newX;
    }
    public void setY(int newY){
        y = newY;
}
    public void setAction(rowActions newAction) { action = newAction;}


}
