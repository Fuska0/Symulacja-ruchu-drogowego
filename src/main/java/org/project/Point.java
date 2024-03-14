package org.project;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.max;


public class Point {
    // VEHICLE PARAMETERS
    private int length;
    Random random = new Random();    int color;
    private int maxSpeed;
    private int acceleration;
    private int deceleration;
    private int speed;
    private Vector2d position;
    private Vector2d tail[];
    public boolean moved;
    private int destination;
    int[] tab = {11,0,1};// 0 skręt w prawo, 1 prosto, 11 w lewo
    private int stopTime;
    // END OF VEHICLE PARAMETERS

    public static int[] statistics = {0, 0, 0, 0, 0, 0, 0, 0};

    public Point(int initLength, int initMaxSpeed, int initAcceleration,
                 int initDeceleration, Vector2d initPosition, Vector2d tailVector){
        Random r = new Random();
        length = initLength;
        maxSpeed = initMaxSpeed;
        acceleration = initAcceleration;
        position = initPosition;
        deceleration = initDeceleration;
        speed = 0;
        destination = tab[r.nextInt(tab.length)];
        color = r.nextInt(8);
        stopTime = r.nextInt(100,200);
        if (length > 1) {
            tail  = new Vector2d[length-1];
            for(int i = 0; i < length-1; i++){
                tail[i] = position;
            }
        }
    }

    public void addNewDestination(int... newDest) {
        Random r = new Random();
        if (newDest.length > 0) {
            destination = newDest[0];
        } else {
            destination = tab[r.nextInt(tab.length)];
        }
    }
    public int getDestination() {return destination;}
    public void setDestination(){destination = destination/10;}
    public Vector2d getPosition(){
        return position;
    }

    public int getDeceleration() {
        return deceleration;
    }

    public int getAcceleration() {
        return acceleration;
    }

    public Vector2d[] getTail() {return tail;}
    public int getSpeed() {return speed;}

    public int getLength() {
        return length;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void speedBoost(){ speed = max(maxSpeed, speed + acceleration);}
    public void setLength(int length){ this.length = length;}
    public void setMaxSpeed(int maxSpeed) {this.maxSpeed = maxSpeed;}
    public void setAcceleration(int acceleration) {this.acceleration = acceleration;}
    public void setDeceleration(int deceleration) {this.deceleration = deceleration;}
    public void setSpeed(int speed) {this.speed = speed;}
    public void setPosition(Vector2d position) {
        this.position = position;
    }
    public void setTail(/*Vector2d[] tail*/) {
        if (length > 1) {
            tail  = new Vector2d[length-1];
            for(int i = 0; i < length-1; i++){
                tail[i] = position;
            }
        }
    }

    public void copyVehicleToAnotherTile(Point other){
        length = other.length;
        maxSpeed = other.maxSpeed;
        acceleration = other.acceleration;
        deceleration = other.deceleration;
        speed = other.speed;
        position = other.position;
        tail = other.tail;
        destination = other.destination;
        color = other.color;
    }
    public void speedReduction(int obstacleDistance){  //przeszkoda jest cos nieruchomego badz z mniejsza predkoscia
        if(obstacleDistance < 1){
            speed = 0;
        }
        else {
            speed = (int) Math.max(1, Math.min(obstacleDistance - 1, Math.round(Math.sqrt(2 * obstacleDistance * deceleration))));
            speed = Math.min(speed,maxSpeed);

        }
    }

    public void moveTail(Vector2d vector) {
        if (length > 1) {
            for (int i = length - 2; i > 0; i--) {
                tail[i] = tail[i - 1];
            }
            tail[0] = vector;
        }

    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Position: ").append(position).append(", ");
        stringBuilder.append("Tail: ");
        if(length > 1){
            for (int i = 0; i < length-1; i++) {
                stringBuilder.append(tail[i]).append(" ");
            }
        }
        return stringBuilder.toString();
    }

    }
