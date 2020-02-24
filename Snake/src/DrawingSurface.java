import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;

public class DrawingSurface extends PApplet {
	
	private int columns = 20;
	private int rows = 20;
	private float dx, dy;
	private Snake player1;
	private Snake player2;
	
	private int timer = 0;
	private int highScore = 0;
	private int numApples = 1;
	private int framesPerTick = 200;
	
	private boolean eat1, eat2;
	private PVector[] apples;
	
	private boolean secondPlayer;
	private boolean drawGrid;
	
	
	public static void main(String[] args) {
		PApplet.main("DrawingSurface");
	}
	
	public void settings() {
		size(800,800);
	}
	public void setup() {
		stroke(255);
		//surface.setResizable(true);
		player1 = new Snake(0,0);
		secondPlayer = true;
		drawGrid = true;
		
		if(secondPlayer) {
			player2 = new Snake(columns-1,rows-1);
			player2.dir = new PVector(-1,0);
		}

		apples = new PVector[numApples];
		
		for(int i = 0; i < apples.length; i++) {
			apples[i] = makeApple();
		}

	}
	
	public void draw() {
		background(0);
		dx = width/columns;
		dy = height/rows;

		// draw snake
		fill(0,255,0);
		player1.draw();
		if(secondPlayer) {
			fill(64,224,208);
			player2.draw();
		}

		// draw apple
		noStroke();
		drawApple();
		stroke(255);
		if(drawGrid) drawGrid();
		
		// high score
		fill(255);
		
		if(secondPlayer) {
			if(player2.getLength() > highScore) {
				highScore = player2.getLength();
			}
			text("Score player1: " + player1.getLength() + " Score player2: " + player2.getLength()+  " High Score: " + highScore,0,15);
			
			//eats apple
			for(int i = 0; i < apples.length; i++) {
				if(player1.getHead().x == apples[i].x && player1.getHead().y == apples[i].y) {
					eat1 = true;
					apples[i] = makeApple();
				}
				else if(player2.getHead().x == apples[i].x && player2.getHead().y == apples[i].y) {
					eat2 = true;
					apples[i] = makeApple();
				}
			}
			
		} else { // only one player
			if(player1.getLength() > highScore) {
				highScore = player1.getLength();
			}
			text("Score player1: " + player1.getLength() + " Score player2: " +  " High Score: " + highScore,0,15);
		
			//eats apple
			for(int i = 0; i < apples.length; i++) {
				if(player1.getHead().x == apples[i].x && player1.getHead().y == apples[i].y) {
					eat1 = true;
					apples[i] = makeApple();
				}
			}
		}
		
		// updates
		if(millis() - timer > framesPerTick) {
			player1.update(eat1);
			if(secondPlayer) player2.update(eat2);
			eat1 = eat2 = false;
			timer = millis();
		}
		
		// death
		if(player1.checkCollision()) {
			player1.reset(0,0);
		}
		if(secondPlayer && player2.checkCollision()) {
			player2.reset(columns-1,rows-1);
		}
		
		if(secondPlayer) {
			if(player1.checkHead(player2.head) && player2.checkHead(player1.head)) {
				player2.reset(columns-1,rows-1);
				player1.reset(0,0);
			}
			else if(player1.checkHead(player2.head)) {
				player2.reset(columns-1,rows-1);
			}
			else if(player2.checkHead(player1.head)) {
				player1.reset(0,0);
			}	
		}
		
	
	}
	
	
	private void drawGrid() {
		int i;
		for(i = 1; i < columns; i++) { //up and down lines
			line(i * dx, 0, i * dx, height);
		}
		for(i = 1; i < rows; i++) { //up and down lines
			line(0, i * dy, width, i * dy);
		}
	}
	
	public void drawApple() {
		fill(255,0,0);
		
		for(int i = 0; i < apples.length; i++) {
			rect(apples[i].x * dx, apples[i].y * dy, dx, dy);
		}
		
	}
	
	public PVector makeApple() {
		PVector apple = new PVector();
		
		boolean condition;
		do {
			apple.x = (int)(Math.random()*columns);
			apple.y = (int)(Math.random()*rows);
			if(secondPlayer)
				condition = player1.insideSnakeBody(apple) || (player1.getHead().x == apple.x && player1.getHead().y == apple.y)
						|| player2.insideSnakeBody(apple) || (player2.getHead().x == apple.x && player2.getHead().y == apple.y);
			else
				condition = player1.insideSnakeBody(apple) || (player1.getHead().x == apple.x && player1.getHead().y == apple.y);
		} while(condition);
		return apple;
	}
	
	public void keyPressed() {
		if(keyCode == UP) {
			player1.dir.set(0, -1);
		}
		if(keyCode == DOWN) {
			player1.dir.set(0, 1);
		}
		if(keyCode == LEFT) {
			player1.dir.set(-1, 0);
		}
		if(keyCode == RIGHT) {
			player1.dir.set(1, 0);
		}
		
		if(secondPlayer) {
			
			if(key == 'w') {
				player2.dir.set(0, -1);
			}
			if(key == 's') {
				player2.dir.set(0, 1);
			}
			if(key == 'a') {
				player2.dir.set(-1, 0);
			}
			if(key == 'd') {
				player2.dir.set(1, 0);
			}
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	class Snake {
		private PVector head;
		public PVector dir;
		private ArrayList<PVector> snake;


		public Snake(int x, int y) {
			head = new PVector(x, y);
			dir = new PVector(1, 0); //right
			snake = new ArrayList<PVector>();
			snake.add(head);
		}
				
		public void update(boolean eat) {

			if(eat) {
				snake.add(new PVector());
			}

			PVector last;
			PVector secondLast;
			for(int i = snake.size() - 1; i >= 1; i--) { // not including head
				last = snake.get(i);
				secondLast = snake.get(i - 1);
				last.x = secondLast.x;
				last.y = secondLast.y;
			}
				
			head.add(dir);
			if(head.x >= columns)
				head.x = 0;
			else if(head.x < 0)
				head.x = columns - 1;
			if(head.y >= rows)
				head.y = 0;
			else if(head.y < 0)
				head.y = rows - 1;
			
		}
		
		public void draw() {
			for(PVector piece : snake) {
				rect(piece.x * dx, piece.y * dy, dx, dy);
			}
		}
		
		public PVector getHead() {
			return head;
		}
		
		public boolean insideSnakeBody(PVector pos) {
			for(int i = 1; i < snake.size(); i++) { // not including head
				if(snake.get(i).x == pos.x && snake.get(i).y == pos.y)
					return true;
			}
			return false;
		}
		
		public boolean checkCollision() {
			for(int i = 1; i < snake.size(); i++) { // not including head
				if(snake.get(i).x == head.x && snake.get(i).y == head.y)
					return true;
			}
			return false;
		}
		
		public boolean checkHead(PVector head2) {
			for(int i = 0; i < snake.size(); i++) { // including head
				if(snake.get(i).x == head2.x && snake.get(i).y == head2.y)
					return true;
			}
			return false;
		}
		
		public void reset(int x, int y) {
			head.x = x;
			head.y = y;
			//dir is the same
			snake = new ArrayList<PVector>();
			snake.add(head);
		}
		
		public int getLength() {
			return snake.size();
		}
		
	}
}
