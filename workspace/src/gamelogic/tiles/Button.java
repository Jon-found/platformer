package gamelogic.tiles;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gameengine.hitbox.RectHitbox;
import gamelogic.GameResources;
import gamelogic.level.Level;

public class Button extends Tile {

    private boolean pressed = false;
    private ArrayList<Tile> doors;
// Before: image must not be null, and level must be set.
    // After: makes a button at the given spot that is not solid.
    public Button(float x, float y, int size, BufferedImage image, Level level) {
        super(x, y, size, image, false, level);
        hitbox = new RectHitbox(x*size, y*size, 0, 0, size, size); 
        this.doors = null;
        
    }
// Before: doors should be a list of tiles (usually door tiles).
    // After: sets this button’s door list to that list.
    public void setDoors(ArrayList<Tile> doors){
        this.doors = doors;
    }
  // Before: button should not already be pressed.
    // After: switches each door’s image and collision, and marks button as pressed.
    @Override
    public void update(float tslf) {
        if (hitbox != null && level.getPlayer().getHitbox().isIntersecting(hitbox)) {
            if (!pressed) {
                pressed = true;
               
                System.out.println("Button pressed!");
               
                if (doors != null) {
                for (Tile door : doors) {
                    if (door.isSolid()) {
                        door.makeNotSolid();
                        door.setImage(GameResources.tileset.getImage("doorO"));
                    } else {
                        door.makeSolid();
                        door.setImage(GameResources.tileset.getImage("doorC"));
                    }
                }
            }
                
                  this.image = null;
        this.hitbox = null;
                       

       



            }
        } else {
            pressed = false;
        }
    }

    
}
