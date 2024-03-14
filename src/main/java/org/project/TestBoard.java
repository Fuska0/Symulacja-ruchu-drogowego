package org.project;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestBoard extends JComponent implements MouseInputListener, ComponentListener {

    private Point[][] points;
    private List<Vector2d>[][] directions;
    private List<Vector2d> lowerMaxSpeedVectors = new ArrayList<>();
    private List<Vector2d> increaseMaxSpeedVectors = new ArrayList<>();

    private List<Vector2d> crossroads = new ArrayList<>();
    private List<Vector2d> street1 = new ArrayList<>();

    private List<Vector2d> street2 = new ArrayList<>();
    private List<Vector2d> street3 = new ArrayList<>();
    private List<Vector2d> street4 = new ArrayList<>();
    private List<Vector2d> street5 = new ArrayList<>();
    private List<Vector2d> street6 = new ArrayList<>();
    private List<Vector2d> street7 = new ArrayList<>();
    private List<Vector2d> street8 = new ArrayList<>();
    private Boolean[][] blocked;
    Generator generator1;
    Generator generator2;
    Generator generator3;
    Generator generator4;
    private int size = 12;
    public static Integer []types ={0,1,2,3,4,5,6,7,8,9};
    public int editType = 0;
    private int time = 0;


    public int carInFront(int x, int y, int maxSpeed){
        int sum = 0;
        int i = 0;
        int iter;
        int newX = x;
        int newY = y;
        while(i < maxSpeed+1 && directions[x][y].size() > 0){
            if(directions[newX][newY].size() > 1){
                iter = points[x][y].getDestination() % 10;

            }
            else{
                iter = 0;

            }
            newX = directions[x][y].get(iter).getX();
            newY = directions[x][y].get(iter).getY();


            if(!blocked[newX][newY]){
                sum++;
                x = newX;
                y = newY;
                i++;
            }
            else return sum;
        }
        return sum;
    }


    public void TrafficOnTheRoad(List<Vector2d> streetName){
        for(int i = 0; i < streetName.size(); i ++){
            Vector2d vector = streetName.get(i);
            if(points[vector.getX()][vector.getY()].getLength() > 0 && !points[vector.getX()][vector.getY()].moved
                    && points[vector.getX()][vector.getY()].getSpeed() > 0)
            {
                speedChanges(vector.getX(), vector.getY());
                moveVehicles(vector.getX(), vector.getY());}
        }
    }

    public void addVehicle(Generator generator, int frequency){
        if(time % frequency == 0){
            Point vehicle = generator.generateVehicle();
            points[generator.getPosition().getX()][generator.getPosition().getY()] = vehicle;
            blocked[generator.getPosition().getX()][generator.getPosition().getY()] = true;
        }
    }
    public void speedChanges(int x, int y){
        int obstacle = carInFront(x,y,points[x][y].getMaxSpeed());
        points[x][y].speedBoost();
        points[x][y].speedReduction(obstacle);

    }

    public void iteration() {
        for (int x = 0; x < points.length; ++x) {
            for ( int y = 0; y < points[x].length; ++y){
                if(points[x][y].getLength() > 0 ){
                    points[x][y].moved = false;
                    speedChanges(x,y);
                }
            }
        }

        for(int i = 0; i < lowerMaxSpeedVectors.size(); i++){
            Vector2d vector = lowerMaxSpeedVectors.get(i);
            if(points[vector.getX()][vector.getY()].getLength() > 1){
                points[vector.getX()][vector.getY()].setMaxSpeed(1);
            }
        }

        for(int i = 0; i < increaseMaxSpeedVectors.size(); i++){
            Vector2d vector = increaseMaxSpeedVectors.get(i);
            switch (points[vector.getX()][vector.getY()].getLength()) {
                case 2:
                    points[vector.getX()][vector.getY()].setMaxSpeed(2);
                    break;
                case 3:
                    points[vector.getX()][vector.getY()].setMaxSpeed(2);
                    break;
                default:
                    break;
            }
        }

        enterningTheCrossroads(new Vector2d(41,22), new Vector2d(42,20), new Vector2d(40,19), new Vector2d(39,21), crossroads);


        TrafficOnTheRoad(street1);
        TrafficOnTheRoad(street2);
        TrafficOnTheRoad(street3);
        TrafficOnTheRoad(street4);
        TrafficOnTheRoad(street5);
        TrafficOnTheRoad(street6);
        TrafficOnTheRoad(street7);
        TrafficOnTheRoad(street8);
        TrafficOnTheRoad(crossroads);

        addVehicle(generator1, 20);
        addVehicle(generator2, 15);
        addVehicle(generator3, 23);
        addVehicle(generator4, 25);
        clearVehicle(57, 21);
        clearVehicle(41,4);
        clearVehicle(24, 20);
        clearVehicle(40, 37);

        time++;
        this.repaint();
    }

    public void setBlocked(int x,int y, boolean value){
        if(points[x][y].getLength() > 1){
            for (int j = 0; j < points[x][y].getTail().length; j++){
                blocked[points[x][y].getTail()[j].getX()][points[x][y].getTail()[j].getY()] = value;
            }
        }
    }
    public void moveVehicles(int x, int y) {
        int speed = points[x][y].getSpeed();
        int i = 0;
        int iter;
        Vector2d vector = new Vector2d(x, y);

        setBlocked(x,y,false);

        while (i < speed && directions[vector.getX()][vector.getY()].size() > 0) {
            if(directions[vector.getX()][vector.getY()].size() > 1){
                iter = points[x][y].getDestination() % 10;
                points[x][y].setDestination();

            }
            else{
                iter = 0;
            }
            points[x][y].moveTail(vector);
            vector = directions[vector.getX()][vector.getY()].get(iter);

            i++;
        }

        points[vector.getX()][vector.getY()].copyVehicleToAnotherTile(points[x][y]);
        blocked[vector.getX()][vector.getY()] = true;
        points[vector.getX()][vector.getY()].moved = true;
        points[vector.getX()][vector.getY()].setPosition(vector);

        clearVehicle(x, y);
        setBlocked(vector.getX(),vector.getY(),true);

    }


    public void clearVehicle(int x,int y){
        if(points[x][y].getLength() > 1){
            for(int i = 0 ; i < points[x][y].getTail().length; i ++){
                blocked[points[x][y].getTail()[i].getX()][points[x][y].getTail()[i].getY()] = false;
            }
        }
        points[x][y].setLength(0);
        points[x][y].setSpeed(0);
        points[x][y].setMaxSpeed(0);
        points[x][y].setAcceleration(0);
        points[x][y].setDeceleration(0);
        points[x][y].addNewDestination(0);
        points[x][y].setPosition(new Vector2d(-1,-1));
        points[x][y].setTail(); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        blocked[x][y] = false;
    }

    public void enterningTheCrossroads(Vector2d vector1, Vector2d vector2, Vector2d vector3, Vector2d vector4, List<Vector2d> crossroads) {
        List<Vector2d> vectorsList = new ArrayList<>();
        vectorsList.add(vector1);
        vectorsList.add(vector2);
        vectorsList.add(vector3);
        vectorsList.add(vector4);
        Vector2d current;
        Vector2d right;
        Vector2d straight;
        boolean flag = true;

        for(int i = 0; i < 4; i++){
            current = vectorsList.get(i);
            right = vectorsList.get((i+1)%4);
            straight = vectorsList.get((i+2)%4);

            //sprawdzanie czy ktos jest po prawej stronie
            if(points[right.getX()][right.getY()].getLength() != 0){
                points[current.getX()][current.getY()].setSpeed(0);

                //jesli jest po prawej stronie ale sb nie kolidują to obaj jadą
                if(points[current.getX()][current.getY()].getDestination() < points[right.getX()][right.getY()].getDestination()){
                    points[current.getX()][current.getY()].setSpeed(1);
                }
                else if(points[current.getX()][current.getY()].getDestination() == points[right.getX()][right.getY()].getDestination()
                        && points[current.getX()][current.getY()].getDestination() == 0){
                    points[current.getX()][current.getY()].setSpeed(1);
                }
            }
            //jesli nie ma po prawej stronie a auto skreca w lewo to patrzymy czy jedzie auto z
            // naprzeciw (destinantion == 11 oznacza skret w lewo)
            else if (points[current.getX()][current.getY()].getDestination()==11 && points[straight.getX()][straight.getY()].getLength() != 0)  {
                points[current.getX()][current.getY()].setSpeed(0);
            }

        }

        // jesli jakies auto jest na skrzyzowaniu to reszta na nie nie wjezdza
        for(int i = 0; i < crossroads.size(); i++){
            Vector2d tmp = crossroads.get(i);
            if(blocked[tmp.getX()][tmp.getY()]){
                for(int j = 0; j < 4; j++){
                    current = vectorsList.get(j);
                    points[current.getX()][current.getY()].moved = true;
                }
            }
        }

        // jesli wszystkie auta maja ustawiona predkosc 0 to rusza pierwsze auto z listy wektorów
        for(int j = 0; j < 4; j++){
            current = vectorsList.get(j);
            if(points[current.getX()][current.getY()].getSpeed() != 0 && points[current.getX()][current.getY()].getLength() != 0){
                flag = false;
                break;
            }
        }
        if(flag){
            for(int i =0; i < 4; i++){
                current = vectorsList.get(i);
                if(points[current.getX()][current.getY()].getLength() != 0){
                    points[current.getX()][current.getY()].setSpeed(1);
                    break;
                }
            }
        }
    }

    public TestBoard(int length, int height) {
        initialize(length, height);
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    private void initialize(int length, int height) {
        points = new Point[length][height];
        directions = new List[length][height];
        blocked = new Boolean[length][height];

        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y){
                points[x][y] = new Point(0,0,0,0,
                        new Vector2d(-1,-1),new Vector2d(0,0));
                directions[x][y] = new ArrayList<>();
                blocked[x][y] = false;
            }
        String localization = System.getProperty("user.dir");

        readFile(localization + "\\src\\main\\java\\org\\project\\testStreet1.txt", street1);
        readFile(localization + "\\src\\main\\java\\org\\project\\testStreet2.txt", street2);
        readFile(localization + "\\src\\main\\java\\org\\project\\testStreet3.txt", street3);
        readFile(localization + "\\src\\main\\java\\org\\project\\testStreet4.txt", street4);
        readFile(localization + "\\src\\main\\java\\org\\project\\testStreet5.txt", street5);
        readFile(localization + "\\src\\main\\java\\org\\project\\testStreet6.txt", street6);
        readFile(localization + "\\src\\main\\java\\org\\project\\testStreet7.txt", street7);
        readFile(localization + "\\src\\main\\java\\org\\project\\testStreet8.txt", street8);

        Vector2d vector1 = new Vector2d(41,21);
        Vector2d vector2= new Vector2d(41, 20);
        Vector2d vector3 = new Vector2d(40, 21);
        Vector2d vector4 = new Vector2d(40,20);
        directions[41][22].add(vector1);
        directions[42][20].add(vector2);
        directions[40][19].add(vector4);
        directions[39][21].add(vector3);

        directions[41][21].add(new Vector2d(42,21));
        directions[41][21].add(vector2);

        directions[41][20].add(new Vector2d(41,19));
        directions[41][20].add(vector4);

        directions[40][20].add(new Vector2d(39,20));
        directions[40][20].add(vector3);

        directions[40][21].add(new Vector2d(40,22));
        directions[40][21].add(vector1);

        generator1 = new Generator(new Vector2d(41,37),new Vector2d(-1,0));
        generator2 = new Generator(new Vector2d(57,20),new Vector2d(-1,0));
        generator3 = new Generator(new Vector2d(40,4),new Vector2d(-1,0));
        generator4 = new Generator(new Vector2d(24,21),new Vector2d(-1,0));
        Vector2d tmp = new Vector2d(57,20);




        crossroads.add(vector1);
        crossroads.add(vector2);
        crossroads.add(vector3);
        crossroads.add(vector4);
        street3.add(new Vector2d(42,20));
        street1.add(new Vector2d(41,22));
        street5.add(new Vector2d(40,19));
        street7.add(new Vector2d(39,21));

        lowerMaxSpeedVectors.add(new Vector2d(41, 26));
        lowerMaxSpeedVectors.add(new Vector2d(41, 25));
        lowerMaxSpeedVectors.add(new Vector2d(41, 27));
        lowerMaxSpeedVectors.add(new Vector2d(46, 20));
        lowerMaxSpeedVectors.add(new Vector2d(45, 20));
        lowerMaxSpeedVectors.add(new Vector2d(47, 20));
        lowerMaxSpeedVectors.add(new Vector2d(40, 15));
        lowerMaxSpeedVectors.add(new Vector2d(40, 16));
        lowerMaxSpeedVectors.add(new Vector2d(40, 14));
        lowerMaxSpeedVectors.add(new Vector2d(35, 21));
        lowerMaxSpeedVectors.add(new Vector2d(36, 21));
        lowerMaxSpeedVectors.add(new Vector2d(34, 21));

        increaseMaxSpeedVectors.add(new Vector2d(42, 21));
        increaseMaxSpeedVectors.add(new Vector2d(41,19));
        increaseMaxSpeedVectors.add(new Vector2d(39,20));
        increaseMaxSpeedVectors.add(new Vector2d(40,22));


    }


    public void clear() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y) {
                clearVehicle(x, y);
            }
        this.repaint();
    }

    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(Color.GRAY);
        drawNetting(g, size);
    }

    private void drawRoads(Graphics g, int gridSpace,List<Vector2d> street){
        for(int i = 0; i < street.size(); i++){
            Vector2d vector = street.get(i);
            g.fillRect((vector.getX() * size) + 1, (vector.getY() * size) + 1, (size - 1), (size - 1));
        }
    }
    private void drawNetting(Graphics g, int gridSpace) {
        Insets insets = getInsets();
        int firstX = insets.left;
        int firstY = insets.top;
        int lastX = this.getWidth() - insets.right;
        int lastY = this.getHeight() - insets.bottom;

        int x = firstX;
        while (x < lastX) {
            g.drawLine(x, firstY, x, lastY);
            x += gridSpace;
        }

        int y = firstY;
        while (y < lastY) {
            g.drawLine(firstX, y, lastX, y);
            y += gridSpace;
        }

        drawRoads(g,gridSpace,street1);
        drawRoads(g,gridSpace,street2);
        drawRoads(g,gridSpace,street3);
        drawRoads(g,gridSpace,street4);
        drawRoads(g,gridSpace,street5);
        drawRoads(g,gridSpace,street6);
        drawRoads(g,gridSpace,street7);
        drawRoads(g,gridSpace,street8);
        drawRoads(g,gridSpace,crossroads);


        for (x = 0; x < points.length; ++x) {
            for (y = 0; y < points[x].length; ++y) {
                int type = points[x][y].getLength();
                if (type != 0) {
                    switch (points[x][y].getLength()) {
                        case 1 -> g.setColor(new Color(0xFFEA00));
                        case 2 -> {switch (points[x][y].color){
                            case 0 -> g.setColor(new Color(0xFFA40EF6, true));
                            case 1 -> g.setColor(new Color(0xA45F0E));
                            case 2 -> g.setColor(new Color(0x7BFF00));
                            case 3 -> g.setColor(new Color(0x03D7B7));
                            case 4 -> g.setColor(new Color(0xFFA40EF6, true));
                            case 5 -> g.setColor(new Color(0x6B5D0A));
                            case 6 -> g.setColor(new Color(0x980F6D));
                            case 7 -> g.setColor(new Color(0xFF659A));
                        }}
                        case 3 -> g.setColor(new Color(0xff0000));
                        case 5 -> g.setColor(new Color(0xff00ff));
                        case 8 -> g.setColor(new Color(0x6F7DA1));
                        case 9 -> g.setColor(new Color(0x000000));
                    }
                    g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
                    if(type == 2 || type == 3){
                        Vector2d[] tail = points[x][y].getTail();
                        for(int j = 0; j < type- 1; j++ ){
                            g.fillRect((tail[j].getX() * size) + 1, (tail[j].getY() * size) + 1, (size - 1), (size - 1));
                        }
                    }

                }
            }
        }
        g.setColor(new Color(0x000000));
//        g.fillRect((41 * size) + 1, (21 * size) + 1, (size - 1), (size - 1));
//        g.fillRect((41 * size) + 1, (20 * size) + 1, (size - 1), (size - 1));
//        g.fillRect((40 * size) + 1, (21 * size) + 1, (size - 1), (size - 1));
//        g.fillRect((40 * size) + 1, (20 * size) + 1, (size - 1), (size - 1));

    }

    public void readFile(String fileName, List<Vector2d> list) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            Vector2d prev = new Vector2d(-1,-1);
            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(" ");
                int x = Integer.parseInt(numbers[0]);
                int y = Integer.parseInt(numbers[1]);
                if(!prev.equals(new Vector2d(-1,-1))){
                    directions[prev.getX()][prev.getY()].add(new Vector2d(x,y));
                    list.add(prev);
                }
                prev = new Vector2d(x,y);
            }
        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku: " + e.getMessage());
        }
    }

    public void drawFromFile(String fileName, int typetoSet, int mode){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] numbers = line.split(" ");
                int x = Integer.parseInt(numbers[0]);
                int y = Integer.parseInt(numbers[1]);
                if (mode == 0){
                    points[x][y].setLength(typetoSet);
                }
            }
        } catch (IOException e) {
            System.err.println("Błąd odczytu pliku: " + e.getMessage());
        }
    }
    @Override
    public void componentResized(ComponentEvent componentEvent) {

    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {

    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {

    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            if(editType<4) {
                points[x][y].setLength(editType);
                System.out.println(String.valueOf(x) + " " + String.valueOf(y));
                this.repaint();
            }

        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {

    }
}