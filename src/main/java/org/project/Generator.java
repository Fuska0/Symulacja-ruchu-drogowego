package org.project;

import java.util.Random;

public class Generator {

    private Vector2d position;
    private Vector2d tailVector;


    public Generator(Vector2d initPosition,Vector2d tailVector){
        position = initPosition;
        this.tailVector = tailVector;

    }

    public Vector2d getPosition() {
        return position;
    }

    public Point generateVehicle(){
        Random r = new Random();

        Point bike = new Point(1,1,1,1, position, tailVector);
        Point car = new Point(2,4,2,2,position,tailVector);
        Point bigCar = new Point(3,3,1,2,position, tailVector);

        int draw = r.nextInt(0,100);
        if(draw < 15) return bike;
        else if (draw < 98) return  car;
        else return bigCar;
    }
}

