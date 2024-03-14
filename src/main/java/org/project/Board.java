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
import java.util.Random;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private Point[][] points;
	private List<Vector2d>[][] directions;
	private Boolean[][] blocked;
	Generator generator1;
	Generator generator4;
	Generator generator10;
	Generator generator11;
	Generator generator14;
	private int[] destinationFrequency = new int[]{0,0,0};

	private List<Vector2d> street1 = new ArrayList<>();
	private List<Vector2d> street2 = new ArrayList<>();
	private List<Vector2d> street3 = new ArrayList<>();
	private List<Vector2d> street4 = new ArrayList<>();
	private List<Vector2d> street6 = new ArrayList<>();
	private List<Vector2d> street7 = new ArrayList<>();
	private List<Vector2d> street8 = new ArrayList<>();
	private List<Vector2d> street9 = new ArrayList<>();
	private List<Vector2d> street10 = new ArrayList<>();
	private List<Vector2d> street11 = new ArrayList<>();
	private List<Vector2d> street12 = new ArrayList<>();
	private List<Vector2d> street13 = new ArrayList<>();
	private List<Vector2d> street14 = new ArrayList<>();
	private List<Vector2d> leftCrossroads = new ArrayList<>();
	private List<Vector2d> rightCrossroads = new ArrayList<>();
	private List<Vector2d> reduceMaxSpeedVectors = new ArrayList<>();
	private List<Vector2d> IncreaseMaxSpeedPoints = new ArrayList<>();
	private int time = 0;
	private int size = 12;
	public static Integer []types ={0,1,2,3,4,5,6,7,8,9};
	public int editType = 0;
	public int[] vehicleStatistics = {0, 0, 0, 0, 0};

	public Board(int length, int height) {
		initialize(length, height);
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
	}

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
			else { iter = 0;}
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

		clearVehicle(x, y, false);
		setBlocked(vector.getX(),vector.getY(),true);

	}

	public void enteringTheCrossroads(Vector2d vector1, Vector2d vector2, Vector2d vector3, Vector2d vector4, List<Vector2d> crossroads) {
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

			if(points[current.getX()][current.getY()].getLength() > 0){
				if(points[current.getX()][current.getY()].getDestination() == 0){
					destinationFrequency[0]++;
				} else if (points[current.getX()][current.getY()].getDestination() == 1 ){
					destinationFrequency[1]++;
				}
				else
				{destinationFrequency[2]++;}
			}

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
			// naprzeciw (destination == 11 oznacza skret w lewo)
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


	//single iteration
	public void iteration() {
		// ruch aut
		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y) {
				if(points[x][y].getLength() > 1 && points[x][y].getLength() <4 ){
					setBlocked(x,y,true);
				}
			}
		}

		for (int x = 0; x < points.length; ++x) {
			for ( int y = 0; y < points[x].length; ++y){
				if(points[x][y].getLength() > 0 ){
					points[x][y].moved = false;
					speedChanges(x,y);
				}
			}
		}

		for(int i = 0; i < reduceMaxSpeedVectors.size(); i++){
			Vector2d vector = reduceMaxSpeedVectors.get(i);
			if(points[vector.getX()][vector.getY()].getLength() > 1){
				points[vector.getX()][vector.getY()].setMaxSpeed(1);
			}
		}

		for(int i = 0; i < IncreaseMaxSpeedPoints.size(); i++){
			Vector2d vector = IncreaseMaxSpeedPoints.get(i);
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

		enteringTheCrossroads(new Vector2d(9, 18),new Vector2d(11, 19),
				new Vector2d(12, 17),new Vector2d(10, 16),leftCrossroads);

		enteringTheCrossroads(new Vector2d(68, 18), new Vector2d(70, 19),
				new Vector2d(71, 17), new Vector2d(69, 16), rightCrossroads);

		TrafficOnTheRoad(street1);
		TrafficOnTheRoad(street2);
		TrafficOnTheRoad(street3);
		TrafficOnTheRoad(street4);
		TrafficOnTheRoad(street6);
		TrafficOnTheRoad(street7);
		TrafficOnTheRoad(street8);
		TrafficOnTheRoad(street9);
		TrafficOnTheRoad(street10);
		TrafficOnTheRoad(street11);
		TrafficOnTheRoad(street12);
		TrafficOnTheRoad(street13);
		TrafficOnTheRoad(street14);
		TrafficOnTheRoad(leftCrossroads);
		TrafficOnTheRoad(rightCrossroads);

		addVehicle(generator1, 15);
		addVehicle(generator10, 20);
		addVehicle(generator4, 18);
		addVehicle(generator14, 17);
		addVehicle(generator11, 13);

		clearVehicle(10, 35, true);
		clearVehicle(69,35, true);
		clearVehicle(75, 18, true);
		clearVehicle(70, 2, true);
		clearVehicle(2, 2, true);

		points[68][18].addNewDestination();
		points[12][17].addNewDestination();

		time++;
		repaint();
	}

	public void clearVehicle(int x,int y, boolean toStatistics){
		if (blocked[x][y] && toStatistics){
			switch (x){
				case 2:
					vehicleStatistics[0] += 1;
					break;
				case 10:
					vehicleStatistics[1] += 1;
					break;
				case 69:
					vehicleStatistics[2] += 1;
					break;
				case 70:
					vehicleStatistics[3] += 1;
					break;
				case 75:
					vehicleStatistics[4] += 1;
					break;
			}
		}
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
		points[x][y].setTail();
		blocked[x][y] = false;
	}

	// clearing board
	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				clearVehicle(x, y, false);
			}
		this.repaint();
	}

	private void initialize(int length, int height) {
		points = new Point[length][height];
		directions = new List[length][height];
		blocked = new Boolean[length][height];

		generator1 = new Generator(new Vector2d(2,3),new Vector2d(-1,0));
		generator4 = new Generator(new Vector2d(11,35),new Vector2d(-1,0));
		generator10 = new Generator(new Vector2d(70, 35),new Vector2d(-1,0));
		generator11 = new Generator(new Vector2d(69, 2),new Vector2d(-1,0));
		generator14 = new Generator(new Vector2d(75, 17),new Vector2d(-1,0));

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y){
				points[x][y] = new Point(0,0,0,0,
						new Vector2d(-1,-1),new Vector2d(0,0));
				directions[x][y] = new ArrayList<>();
				blocked[x][y] = false;
			}

		directions[10][18].add(new Vector2d(10,19));
		directions[10][18].add(new Vector2d(11,18));

		directions[11][18].add(new Vector2d(12,18));
		directions[11][18].add(new Vector2d(11,17));

		directions[11][17].add(new Vector2d(11,16));
		directions[11][17].add(new Vector2d(10,17));

		directions[10][17].add(new Vector2d(9,17));
		directions[10][17].add(new Vector2d(10,18));

		directions[69][18].add(new Vector2d(69,19));
		directions[69][18].add(new Vector2d(70,18));

		directions[70][18].add(new Vector2d(71,18));
		directions[70][18].add(new Vector2d(70,17));

		directions[70][17].add(new Vector2d(70,16));
		directions[70][17].add(new Vector2d(69,17));

		directions[69][17].add(new Vector2d(68,17));
		directions[69][17].add(new Vector2d(69,18));

		directions[11][2].add(new Vector2d(10,2));

		leftCrossroads.add(new Vector2d(10,18));
		leftCrossroads.add(new Vector2d(11,18));
		leftCrossroads.add(new Vector2d(11,17));
		leftCrossroads.add(new Vector2d(10,17));
		rightCrossroads.add(new Vector2d(69,18));
		rightCrossroads.add(new Vector2d(70,18));
		rightCrossroads.add(new Vector2d(70,17));
		rightCrossroads.add(new Vector2d(69,17));

		String path = "\\src\\main\\java\\points_distribution\\";

		String localization = System.getProperty("user.dir");
		readFileAndAddDirections(localization + path + "street1.txt", street1);
		readFileAndAddDirections(localization + path + "street2.txt", street2);
		readFileAndAddDirections(localization + path + "street3.txt", street3);
		readFileAndAddDirections(localization + path + "street4.txt", street4);
		readFileAndAddDirections(localization + path + "street6.txt", street6);
		readFileAndAddDirections(localization + path + "street7.txt", street7);
		readFileAndAddDirections(localization + path + "street8.txt", street8);
		readFileAndAddDirections(localization + path + "street9.txt", street9);
		readFileAndAddDirections(localization + path + "street10.txt", street10);
		readFileAndAddDirections(localization + path + "street11.txt", street11);
		readFileAndAddDirections(localization + path + "street12.txt", street12);
		readFileAndAddDirections(localization + path + "street13.txt", street13);
		readFileAndAddDirections(localization + path + "street14.txt", street14);
		readFileWithoutDirections(localization + path + "ReduceMaxSpeedPoints.txt",
				reduceMaxSpeedVectors);
		readFileWithoutDirections(localization + path + "IncreaseMaxSpeedPoints.txt",
				IncreaseMaxSpeedPoints);


		String borderPath = path + "borders.txt";
		String buildingsPath = path + "buildings.txt";

		String border = localization + borderPath;
		String buildings = localization + buildingsPath;

		drawFromFile(border, 9, 0);
		drawFromFile(buildings, 8, 0);
	}

	public void readFileAndAddDirections(String fileName, List<Vector2d> list) {
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

	public void readFileWithoutDirections(String fileName, List<Vector2d> list) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] numbers = line.split(" ");
				int x = Integer.parseInt(numbers[0]);
				int y = Integer.parseInt(numbers[1]);
				list.add(new Vector2d(x,y));
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

	//paint background and separators between cells
	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.GRAY);
		drawNetting(g, size);
	}

	private void drawRoads(Graphics g, int gridSpace,List<Vector2d> street, Color color){
		Vector2d vector;
		for(int i = 0; i < street.size(); i++){
			vector = street.get(i);
			g.setColor(color);
			g.fillRect((vector.getX() * size) + 1, (vector.getY() * size) + 1, (size - 1), (size - 1));
		}
	}

	// draws the background netting
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

		drawRoads(g,gridSpace,street1, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street2, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street3, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street4, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street6, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street7, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street8, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street9, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street10, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street11, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street12, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street13, new Color(0x81202020, true));
		drawRoads(g,gridSpace,street14, new Color(0x81202020, true));
		drawRoads(g,gridSpace,leftCrossroads, new Color(0x81202020, true));
		drawRoads(g,gridSpace,rightCrossroads, new Color(0x81202020, true));


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
							case 4 -> g.setColor(new Color(0xFFA40EF0, true));
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

	}

	public void mouseClicked(MouseEvent e) {
	}
	public void componentResized(ComponentEvent e) {
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);
	}

	public void mouseDragged(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
