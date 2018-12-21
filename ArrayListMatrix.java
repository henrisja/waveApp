package com.company;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.lang.Math;

public class ArrayListMatrix {

    private ArrayList<row> rowArray = new ArrayList<>();
    private ArrayList<WeightedObservedPoints> obs = new ArrayList<>();

    public ArrayListMatrix(){
        for (int i = 0; i < 3; ++i){
            this.rowArray.add(new row());
        }
    }

    public void generateFirstThreeRows(){
        for (int i = 0; i < 10; ++i) {

            obs.add(new WeightedObservedPoints());

            //generate first row using random starting locations
            int newX = i * 100 + ThreadLocalRandom.current().nextInt(-50, 51);
            int newY = ThreadLocalRandom.current().nextInt(-   100, 100);
            rowArray.get(0).rowDots.add(new Dots(newX, newY, i, i));
            WeightedObservedPoint temp = new WeightedObservedPoint(1.0, (((double)(newY))/100), (double)(newX));
            obs.get(i).add(temp);

            //generate second row based off first
            newX = rowArray.get(0).rowDots.get(i).getX() + ThreadLocalRandom.current().nextInt(-1,2);
            newY = rowArray.get(0).rowDots.get(i).getY() + 30;
            rowArray.get(1).rowDots.add(new Dots(newX, newY, i, i));
            temp = new WeightedObservedPoint(1.0, (((double)(newY))/1000), (double)(newX));
            obs.get(i).add(temp);

            //generate third row based off second
            newX = rowArray.get(1).rowDots.get(i).getX() + ThreadLocalRandom.current().nextInt(-1,2);
            newY = rowArray.get(1).rowDots.get(i).getY() + 30;
            rowArray.get(2).rowDots.add(new Dots(newX, newY, i, i));
            temp = new WeightedObservedPoint(1.0, (((double)(newY))/1000), (double)(newX));
            obs.get(i).add(temp);
        }
    }


    //in a perfect world this would have been broken up into several functions, but without pass by reference
      // I just decided to do it all in here

    public void createNewRows(){

        //uses polynomial curve fitter to generate 'newRow' object based off of the curves each is following
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);
        for(int i = 3; i < 20; ++i){
            row newRow = new row();

            //start by generating a row of dots based off the previous, then adjust from there
            for(int j = 0; j < rowArray.get(i-1).rowDots.size(); ++j){

                //use polynomial fitter to generate array of coefficients for curve equations
                double[] coeff = fitter.fit(obs.get(rowArray.get(i-1).rowDots.get(j).getCurve()).toList());

                //calculate new X value for dot based off y-position and curve
                int newY = rowArray.get(i-1).rowDots.get(j).getY() + 30;
                double calcY = (double)(newY)/1000;
                int newX = (int)(coeff[0]+coeff[1]*calcY+coeff[2]*calcY*calcY+coeff[3]*calcY*calcY*calcY);

                //assign the dots curve to the same as the dot it's position is based off
                newRow.rowDots.add(new Dots(newX, newY, rowArray.get(i-1).rowDots.get(j).getCurve(), j));
                newRow.rowDots.get(j).setAction(rowArray.get(i-1).rowDots.get(j).getAction());
            }

            //adjust this row based off of the other values in it's row to avoid crossovers and keep it smooth
            for (int r = 1; r < newRow.rowDots.size(); r += 2){
                boolean closeLeft = false;
                boolean closeRight = false;
                boolean crossoverLeft = false;
                boolean crossoverRight = false;
                int distLeft, distRight = 200;

                distLeft = newRow.rowDots.get(r).getX() - newRow.rowDots.get(r-1).getX();
                if (r < newRow.rowDots.size()-1) {
                    distRight = newRow.rowDots.get(r + 1).getX() - newRow.rowDots.get(r).getX();
                }
                /*
                //THIS IS WHERE I'M CHECKING FOR INTERSECTIONS:
                //should I be accessing curves instead of r numbers?
                int y1 = rowArray.get(i-1).rowDots.get(r-1).getY();
                int y2 = rowArray.get(i-1).rowDots.get(r).getY();
                int x1 = rowArray.get(i-1).rowDots.get(r-1).getX();
                int x2 = rowArray.get(i-1).rowDots.get(r).getX();

                if(newRow.rowDots.get(r).getY() < newRow.rowDots.get(r-1).getY()) {
                    if (newRow.rowDots.get(r).getY() < (((y2 - y1) / (x2 - x1)) * (newRow.rowDots.get(r).getX() - x1)) + y1) {
                        closeLeft = true;
                    }
                    else if (newRow.rowDots.get(r - 1).getY() < (((y2 - y1) / (x2 - x1)) * (newRow.rowDots.get(r).getX() - x1)) + y1) {
                        closeLeft = true;
                    }
                }
                else{
                    if(newRow.rowDots.get(r).getY() > (((y2-y1)/(x2-x1))*(newRow.rowDots.get(r).getX() - x1)) + y1){
                        closeLeft = true;
                    }
                    else if(newRow.rowDots.get(r-1).getY() > (((y2-y1)/(x2-x1))*(newRow.rowDots.get(r).getX() - x1)) + y1){
                        closeLeft = true;
                    }
                }
                if(r < newRow.rowDots.size()-1) {
                    int x3 = rowArray.get(i - 1).rowDots.get(r + 1).getX();
                    int y3 = rowArray.get(i - 1).rowDots.get(r + 1).getY();
                    if (newRow.rowDots.get(r).getY() < newRow.rowDots.get(r + 1).getY()) {
                        if (newRow.rowDots.get(r).getY() < (((y3 - y2) / (x3 - x2)) * (newRow.rowDots.get(r).getX() - x2)) + y2) {
                            closeRight = true;
                        } else if (newRow.rowDots.get(r + 1).getY() < (((y3 - y2) / (x2 - x1)) * (newRow.rowDots.get(r).getX() - x2)) + y2) {
                            closeRight = true;
                        }
                    }
                    else {
                        if (newRow.rowDots.get(r).getY() > (((y3 - y2) / (x3 - x2)) * (newRow.rowDots.get(r).getX() - x2)) + y2) {
                            closeRight = true;
                        }
                        else if (newRow.rowDots.get(r + 1).getY() > (((y3 - y2) / (x3 - x2)) * (newRow.rowDots.get(r).getX() - x2)) + y2) {
                            closeRight = true;
                        }
                    }
                }*/

                if (distLeft < 40) {
                    if (distLeft < 0) {
                        crossoverLeft = true;
                    }
                    else {
                        closeLeft = true;
                    }
                }
                if (distRight < 40) {
                    if (distRight < 0) {
                        crossoverRight = true;
                    }
                    else {
                        closeRight = true;
                    }
                }

                if(crossoverLeft) {
                    int temp = newRow.rowDots.get(r).getX();
                    newRow.rowDots.get(r).setX(newRow.rowDots.get(r-1).getX());
                    newRow.rowDots.get(r-1).setX(temp);
                }
                //NEED TO REVISE THIS, THIS IS WHERE I SHOULD ADJUST OBS
                else if(crossoverRight) {
                    int temp = newRow.rowDots.get(r).getX();
                    newRow.rowDots.get(r).setX(newRow.rowDots.get(r+1).getX());
                    newRow.rowDots.get(r+1).setX(temp);
                }
                else if (closeLeft && closeRight) {
                    if(newRow.rowDots.get(r).getAction() == rowActions.MERGELEFT){
                        newRow.rowDots.get(r+1).setAction(rowActions.SEPARATERIGHT);
                    }
                    if(newRow.rowDots.get(r).getAction() == rowActions.MERGERIGHT){
                        newRow.rowDots.get(r-1).setAction(rowActions.SEPARATELEFT);
                    }
                    else{
                        newRow.rowDots.get(r-1).setAction(rowActions.SEPARATELEFT);
                        newRow.rowDots.get(r+1).setAction(rowActions.SEPARATERIGHT);
                    }
                }
                else if(closeLeft && newRow.rowDots.get(r).getAction() == rowActions.STRAIGHT) {
                    if (Math.abs(newRow.rowDots.get(r).getY() - newRow.rowDots.get(r-1).getY()) < 50) {
                        //50/50 odds of even vs odd
                        int random = ThreadLocalRandom.current().nextInt(1,3);
                        if(random % 2 == 0) {
                            newRow.rowDots.get(r-1).setAction(rowActions.MERGERIGHT);
                            newRow.rowDots.get(r).setAction(rowActions.MERGELEFT);
                        }
                        else {
                            newRow.rowDots.get(r-1).setAction(rowActions.SEPARATELEFT);
                            newRow.rowDots.get(r).setAction(rowActions.SEPARATERIGHT);
                        }
                        //just merge if they're close
                        newRow.rowDots.get(r-1).setAction(rowActions.MERGERIGHT);
                        newRow.rowDots.get(r).setAction(rowActions.MERGELEFT);
                    }
                    else {
                        newRow.rowDots.get(r-1).setAction(rowActions.SEPARATELEFT);
                        newRow.rowDots.get(r).setAction(rowActions.SEPARATERIGHT);
                    }
                }
                else if(closeRight && newRow.rowDots.get(r).getAction() == rowActions.STRAIGHT) {
                    if (Math.abs(newRow.rowDots.get(r+1).getY() - newRow.rowDots.get(r).getY()) < 50) {
                        //50/50 odds of even vs odd

                        int random = ThreadLocalRandom.current().nextInt(1,3);
                        if(random % 2 == 0) {
                            newRow.rowDots.get(r).setAction(rowActions.MERGERIGHT);
                            newRow.rowDots.get(r+1).setAction(rowActions.MERGELEFT);
                        }
                        else {
                            newRow.rowDots.get(r).setAction(rowActions.SEPARATELEFT);
                            newRow.rowDots.get(r+1).setAction(rowActions.SEPARATERIGHT);
                        }
                        newRow.rowDots.get(r).setAction(rowActions.MERGERIGHT);
                        newRow.rowDots.get(r+1).setAction(rowActions.MERGELEFT);
                    }
                    else {
                        newRow.rowDots.get(r).setAction(rowActions.SEPARATELEFT);
                        newRow.rowDots.get(r+1).setAction(rowActions.SEPARATERIGHT);
                    }
                }
            }

            for(int r = 0; r < newRow.rowDots.size(); ++r){
                switch(newRow.rowDots.get(r).getAction()) {
                    case STRAIGHT:
                        break;
                    //going left to right it'll always hit a mergeright before a merge left, adjust both in one function
                    case MERGERIGHT:
                        double x_dist = Math.abs(newRow.rowDots.get(r).getX() - newRow.rowDots.get(r + 1).getX());
                        double y_dist = Math.abs(newRow.rowDots.get(r + 1).getY() - newRow.rowDots.get(r).getY());
                        double dist = Math.hypot(x_dist, y_dist);
                        //removes one of the dots in the merge
                        if (dist < 10){
                            int random = ThreadLocalRandom.current().nextInt(1,3);
                            if(random % 2 == 0){
                                newRow.rowDots.remove(r);
                                newRow.rowDots.get(r).setAction(rowActions.STRAIGHT);
                            }
                            else {
                                newRow.rowDots.remove(r + 1);
                                newRow.rowDots.get(r).setAction(rowActions.STRAIGHT);
                            }
                        }
                        else {
                            newRow.rowDots.get(r).setX(newRow.rowDots.get(r).getX() + (int) (x_dist * .2));
                            newRow.rowDots.get(r+1).setX(newRow.rowDots.get(r+1).getX() - (int)(x_dist * .2));
                            if (newRow.rowDots.get(r).getY() < newRow.rowDots.get(r + 1).getY()) {
                                newRow.rowDots.get(r).setY(newRow.rowDots.get(r).getY() + (int) (y_dist * .2));
                                newRow.rowDots.get(r+1).setY(newRow.rowDots.get(r+1).getY() - (int)(y_dist*.2));
                            } else {
                                newRow.rowDots.get(r).setY(newRow.rowDots.get(r).getY() - (int) (y_dist * .2));
                                newRow.rowDots.get(r+1).setY(newRow.rowDots.get(r+1).getY() + (int)(y_dist*.2));
                            }
                        }
                        break;
                    case SEPARATELEFT:
                        //seperate left = moving to the left
                        int dif = newRow.rowDots.get(r+1).getX() - newRow.rowDots.get(r).getX();
                        newRow.rowDots.get(r).setX(newRow.rowDots.get(r).getX() - (int)(.3*dif));
                        newRow.rowDots.get(r).setAction(rowActions.STRAIGHT);
                        /* USE W/ DIF FACTOR OF .03
                        int one_up_index = newRow.rowDots.get(r).getParentIndex();
                        int two_up_index = rowArray.get(i-1).rowDots.get(one_up_index).getParentIndex();
                        int three_up_index = rowArray.get(i-2).rowDots.get(two_up_index).getParentIndex();
                        WeightedObservedPoint three_up = new WeightedObservedPoint(1.0, (double)(rowArray.get(i-2).rowDots.get(three_up_index).getY()/1000), rowArray.get(i-3).rowDots.get(three_up_index).getX());
                        WeightedObservedPoint two_up = new WeightedObservedPoint(1.0, (double)(rowArray.get(i-2).rowDots.get(two_up_index).getY()/1000), rowArray.get(i-2).rowDots.get(two_up_index).getX());
                        WeightedObservedPoint one_up = new WeightedObservedPoint(1.0, (double)(rowArray.get(i-1).rowDots.get(one_up_index).getY()/1000), rowArray.get(i-1).rowDots.get(one_up_index).getX());
                        obs.get(newRow.rowDots.get(r).getCurve()).clear();
                        obs.get(newRow.rowDots.get(r).getCurve()).add(three_up);
                        obs.get(newRow.rowDots.get(r).getCurve()).add(two_up);
                        obs.get(newRow.rowDots.get(r).getCurve()).add(one_up);
                        */
                        break;
                    case SEPARATERIGHT:
                        //seperate right = moving to the right
                        dif = newRow.rowDots.get(r).getX() - newRow.rowDots.get(r-1).getX();
                        newRow.rowDots.get(r).setX(newRow.rowDots.get(r).getX() + (int)(.03*dif));
                        newRow.rowDots.get(r).setAction(rowActions.STRAIGHT);
                        /* USE W/ Difference factor of .03
                        int one_up1 = newRow.rowDots.get(r).getParentIndex();
                        int two_up1 = rowArray.get(i-1).rowDots.get(one_up1).getParentIndex();
                        int three_up1 = rowArray.get(i-2).rowDots.get(two_up1).getParentIndex();
                        WeightedObservedPoint three_up_point = new WeightedObservedPoint(1.0, (double)(rowArray.get(i-2).rowDots.get(three_up1).getY()/1000), rowArray.get(i-3).rowDots.get(three_up1).getX());
                        WeightedObservedPoint two_up_point = new WeightedObservedPoint(1.0, (double)(rowArray.get(i-2).rowDots.get(two_up1).getY()/1000), rowArray.get(i-2).rowDots.get(two_up1).getX());
                        WeightedObservedPoint one_up_point = new WeightedObservedPoint(1.0, (double)(rowArray.get(i-1).rowDots.get(one_up1).getY()/1000), rowArray.get(i-1).rowDots.get(one_up1).getX());
                        obs.get(newRow.rowDots.get(r).getCurve()).clear();
                        obs.get(newRow.rowDots.get(r).getCurve()).add(three_up_point);
                        obs.get(newRow.rowDots.get(r).getCurve()).add(two_up_point);
                        obs.get(newRow.rowDots.get(r).getCurve()).add(one_up_point);
                        */
                        break;
                }
            }

            //add in the weighted observed point w/ degree of randomness to appropriate curve

            for (int r = 0; r < newRow.rowDots.size(); ++r) {
                double calcY = (double)(newRow.rowDots.get(r).getY())/1000;
                WeightedObservedPoint temp = new WeightedObservedPoint(1.0, calcY, newRow.rowDots.get(r).getX());
                obs.get(newRow.rowDots.get(r).getCurve()).add(temp);
            }

            rowArray.add(newRow);
        }
    }


    public void drawLines(Graphics2D g){

        for(int i = 0; i < rowArray.size(); ++i){
            for(int j = 0; j < rowArray.get(i).rowDots.size()-1; ++j){
                g.drawLine(rowArray.get(i).rowDots.get(j).getX(), rowArray.get(i).rowDots.get(j).getY(),
                        rowArray.get(i).rowDots.get(j+1).getX(), rowArray.get(i).rowDots.get(j+1).getY());
            }
        }
    }



}

