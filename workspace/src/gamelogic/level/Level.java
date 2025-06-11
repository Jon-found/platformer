package gamelogic.level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import gameengine.PhysicsObject;
import gameengine.graphics.Camera;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.enemies.Enemy;
import gamelogic.player.Player;
import gamelogic.tiledMap.Map;
import gamelogic.tiles.Flag;
import gamelogic.tiles.Flower;
import gamelogic.tiles.Gas;
import gamelogic.tiles.SolidTile;
import gamelogic.tiles.Spikes;
import gamelogic.tiles.Tile;
import gamelogic.tiles.Water;
import gamelogic.tiles.Button;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;
	public long gasEnterTime = -1;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();

	private ArrayList<Water> waters = new ArrayList<>();
	private ArrayList<Gas> GG = new ArrayList<>();
	


	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData(){
		return leveldata;
	}
//pre:no tiles must exisit
//post: makes an arraylist of all water and gas and door objects and also add them all into the game.
	public void restartLevel() {

		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];
		GG = new ArrayList();
		waters = new ArrayList();
		ArrayList<Tile> doorTiles = new ArrayList<>();
		Button savedButton = null;

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;
				//add an arraylist of doors 
				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition*tileSize, yPosition*tileSize, this)); // TODO: objects vs tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
				else if (values[x][y] == 19)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
				else if (values[x][y] == 20)
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
				else if (values[x][y] == 21){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
				}else if (values[x][y] == 22){//change solidTile
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, tileset.getImage("doorC"),true, this);
				 doorTiles.add(tiles[x][y]);
				 tiles[x][y].makeSolid();
				}else if (values[x][y] == 23){//change solidTile
					 savedButton = new Button(xPosition, yPosition, tileSize, tileset.getImage("but"), this);
    					tiles[x][y] = savedButton;
				}else if (values[x][y] == 24){//change solidTile
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, tileset.getImage("doorO"), false,this);
					 doorTiles.add(tiles[x][y]);
				}
			}
			if (savedButton != null) {
    savedButton.setDoors(doorTiles);
}


		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	
	
	
	public void update(float tslf) {
	
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();



			
			
			

			






			//checks for water hitbox with player
			//before: check if player is touching water or gas, and update speed or start gas timer
			// after: updated player speed and gas status based on collisions
				 boolean fast =false;
				 
			for(Water w : waters) {
				if(w.getHitbox().isIntersecting(player.getHitbox())){
					
					player.walkSpeed=700;
					  fast =true;
				}
			}
			if(fast==false){
			
			player.walkSpeed=400;
			}
			boolean inGas=false;
			//check for hitbox with gas and player
			for(Gas p: GG){
				if(p.getHitbox().isIntersecting(player.getHitbox())){
					inGas=true;
					        
					player.showTimer((System.currentTimeMillis()-gasEnterTime)/1000);
				}
				
			}
			

		if (inGas) {
   			 if (gasEnterTime == -1) {
       			 gasEnterTime = System.currentTimeMillis();
    		}
 			   player.showTimer((System.currentTimeMillis() - gasEnterTime) / 1000);
    			if ((System.currentTimeMillis() - gasEnterTime) > 5000) {
       		 onPlayerDeath();
   				 }
			} else {
   			 gasEnterTime = -1;
			}			








			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if(flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
						//change from 9 to 20
	else
					addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}

			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}
	// Pre: Called with a column, row, a map object, a positive number of squares to fill,
//      and an empty or partially-filled list to track gas placed this round.
//      The map must be initialized with valid tile bounds and tileSize, and tileset must be accessible.

// Post: Adds a Gas tile at the given position and continues spreading gas outward
//       in multiple directions (up, diagonal, sides, down) until numSquaresToFill is 0
//       or no more valid adjacent tiles are available.
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
    Gas g = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 0);
	GG.add(g);
    map.addTile(col, row, g);
    placedThisRound.add(g);
    numSquaresToFill--; 
    
    int index = 0; 
    
    
    while (index < placedThisRound.size() && numSquaresToFill > 0) {
        Gas current = placedThisRound.get(index);
        int c = current.getCol();
        int r = current.getRow();

      
        
        //up
        if (r - 1 >= 0 && numSquaresToFill > 0) {
            Tile up = map.getTiles()[c][r - 1];
            if (!(up instanceof Gas) && !up.isSolid()) {
                Gas newGas = new Gas(c, r - 1, tileSize, tileset.getImage("GasOne"), this, 0);
                map.addTile(c, r - 1, newGas);
                placedThisRound.add(newGas);
                numSquaresToFill--;
            }
        }
        //up right 
		 if (r - 1 >= 0 && numSquaresToFill > 0 && c+1 >=0) {
            Tile upTileR = map.getTiles()[c+1][r - 1];
            if (!(upTileR instanceof Gas) && !upTileR.isSolid()) {
                Gas newGas = new Gas(c+1, r - 1, tileSize, tileset.getImage("GasOne"), this, 0);
                map.addTile(c+1, r - 1, newGas);
                placedThisRound.add(newGas);
                numSquaresToFill--;
            }
        }

		 //up left 
		 if (r - 1 >= 0 && numSquaresToFill > 0 && c-1 >=0) {
            Tile upTileL = map.getTiles()[c-1][r - 1];
            if (!(upTileL instanceof Gas) && !upTileL.isSolid()) {
                Gas newGas = new Gas(c-1, r - 1, tileSize, tileset.getImage("GasOne"), this, 0);
                map.addTile(c-1, r - 1, newGas);
                placedThisRound.add(newGas);
                numSquaresToFill--;
            }
        }


		 
       //right
        if (c + 1 < map.getWidth() && numSquaresToFill > 0) {
            Tile rightTile = map.getTiles()[c + 1][r];
            if (!(rightTile instanceof Gas)&& !rightTile.isSolid()) {
                Gas newGas = new Gas(c + 1, r, tileSize, tileset.getImage("GasOne"), this, 0);
                map.addTile(c + 1, r, newGas);
                placedThisRound.add(newGas);
                numSquaresToFill--;
            }
        }

       //left
        if (c - 1 >= 0 && numSquaresToFill > 0) {
            Tile leftTile = map.getTiles()[c - 1][r];
            if (!(leftTile instanceof Gas) && !leftTile.isSolid()) {
                Gas newGas = new Gas(c - 1, r, tileSize, tileset.getImage("GasOne"), this, 0);
                map.addTile(c - 1, r, newGas);
                placedThisRound.add(newGas);
                numSquaresToFill--;
            }
        }
       
        
       //down
        if (r + 1 < map.getHeight() && numSquaresToFill > 0) {
            Tile downTile = map.getTiles()[c][r + 1];
            if (!(downTile instanceof Gas) && !downTile.isSolid()) {
                Gas newGas = new Gas(c, r + 1, tileSize, tileset.getImage("GasOne"), this, 0);
                map.addTile(c, r + 1, newGas);
                placedThisRound.add(newGas);
                numSquaresToFill--;
            }
        }


		//down right
        if (r + 1 < map.getHeight() && numSquaresToFill > 0 && c+ 1 < map.getHeight() ) {
            Tile downTileR = map.getTiles()[c+1][r + 1];
            if (!(downTileR instanceof Gas) && !downTileR.isSolid()) {
                Gas newGas = new Gas(c+1, r + 1, tileSize, tileset.getImage("GasOne"), this, 0);
                map.addTile(c+1, r + 1, newGas);
                placedThisRound.add(newGas);
                numSquaresToFill--;
            }
        }


		//down left
        if (r + 1 < map.getHeight() && numSquaresToFill > 0 && c- 1 >=0 ) {
            Tile downTileL = map.getTiles()[c-1][r + 1];
            if (!(downTileL instanceof Gas) && !downTileL.isSolid()) {
                Gas newGas = new Gas(c-1, r + 1, tileSize, tileset.getImage("GasOne"), this, 0);
                map.addTile(c-1, r + 1, newGas);
                placedThisRound.add(newGas);
                numSquaresToFill--;
            }
        }



        index++; 
    }
}

	//#############################################################################################################
	//Your code goes here! 
	//Please make sure you read the rubric/directions carefully and implement the solution recursively!
	
	// Pre: Called with a column, row, a map object, and a fullness level (0–3).
//      The map must be initialized and contain a grid of Tile objects. 
//      tileSize and tileset must be accessible .

// Post: Adds a Water tile at the given position if possible.
//       Attempts to flow water downwards first; if blocked, flows left and right
//       depending on remaining fullness. Prevents replacing existing water or solid tiles.


private void water(int col, int row, Map map, int fullness) {
    // Check bounds



    if (col < 0 || col >= map.getTiles().length || row < 0 || row >= map.getTiles()[0].length) return;

    Tile currentTile = map.getTiles()[col][row];
    if (currentTile instanceof Water) return; // Don't overwrite existing water

    // Select image name based on fullness
    String imageName = "";
    if (fullness == 3) imageName = "Full_water";
    else if (fullness == 2) imageName = "Half_water";
    else if (fullness == 1) imageName = "Quarter_water";
    else imageName = "Falling_water"; // fullness == 0

    // Create and add water tile
    Water w = new Water(col, row, tileSize, tileset.getImage(imageName), this, fullness);
	waters.add(w);
    map.addTile(col, row, w);

    // Try to flow down


	//added the and statemnt!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\
	

    if (row + 1 < map.getTiles()[0].length && row + 2 < map.getTiles()[0].length ) {
        Tile below = map.getTiles()[col][row + 1];
		Tile TBellow = map.getTiles()[col][row+2];
        if (!below.isSolid() && !(below instanceof Water)) {
			if(TBellow.isSolid()){      //consider the block two below you
				water(col, row+1, map,3);
				return; //same as bottom
			}
            water(col, row + 1, map, 0); // Falling water
            return; // Stop sideways flow if we can go down
        }
    }
	else{
		return;
	}

    // Sideways flow only if fullness > 0
   
        // Right
        if (col + 1 < map.getTiles().length 
            && !(map.getTiles()[col + 1][row] instanceof Water)
            && !map.getTiles()[col + 1][row].isSolid()) {

            // Check for air under
                water(col + 1, row, map, Math.max(1, fullness - 1));
            
        }

        // Left
        if (col - 1 >= 0 
            && !(map.getTiles()[col - 1][row] instanceof Water)
            && !map.getTiles()[col - 1][row].isSolid()) {

           
                water(col - 1, row, map, Math.max(1, fullness - 1));
            
        }
    
}



	



		public void draw(Graphics g) {
	   	 g.translate((int) -camera.getX(), (int) -camera.getY());
	   	 // Draw the map
	   	 for (int x = 0; x < map.getWidth(); x++) {
	   		 for (int y = 0; y < map.getHeight(); y++) {
	   			 Tile tile = map.getTiles()[x][y];
	   			 if (tile == null)
	   				 continue;
	   			 if(tile instanceof Gas) {
	   				
	   				 int adjacencyCount =0;
	   				 for(int i=-1; i<2; i++) {
	   					 for(int j =-1; j<2; j++) {
	   						 if(j!=0 || i!=0) {
	   							 if((x+i)>=0 && (x+i)<map.getTiles().length && (y+j)>=0 && (y+j)<map.getTiles()[x].length) {
	   								 if(map.getTiles()[x+i][y+j] instanceof Gas) {
	   									 adjacencyCount++;
	   								 }
	   							 }
	   						 }
	   					 }
	   				 }
	   				 if(adjacencyCount == 8) {
	   					 ((Gas)(tile)).setIntensity(2);
	   					 tile.setImage(tileset.getImage("GasThree"));
	   				 }
	   				 else if(adjacencyCount >5) {
	   					 ((Gas)(tile)).setIntensity(1);
	   					tile.setImage(tileset.getImage("GasTwo"));
	   				 }
	   				 else {
	   					 ((Gas)(tile)).setIntensity(0);
	   					tile.setImage(tileset.getImage("GasOne"));
	   				 }
	   			 }
	   			 if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
	   				 tile.draw(g);
	   		 }
	   	 }


	   	 // Draw the enemies
	   	 for (int i = 0; i < enemies.length; i++) {
	   		 enemies[i].draw(g);
	   	 }


	   	 // Draw the player
	   	 player.draw(g);




	   	 // used for debugging
	   	 if (Camera.SHOW_CAMERA)
	   		 camera.draw(g);
	   	 g.translate((int) +camera.getX(), (int) +camera.getY());
	    }


	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}