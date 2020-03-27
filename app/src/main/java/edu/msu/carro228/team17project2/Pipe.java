package edu.msu.carro228.team17project2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.FactoryConfigurationError;

import edu.msu.carro228.team17project2.Data.PipeData;

/**
 * An example of how a pipe might be represented.
 */
public class Pipe implements Serializable {
    /**
     * Playing area this pipe is a member of
     */
    private PipeContainer playingArea = null;

    private PipeFactory factory;

    /**
     * We consider a piece to be in the right location if within
     * this distance.
     */
    final static float SNAP_DISTANCE = 0.05f;

    /**
     * Array that indicates which sides of this pipe
     * has flanges. The order is north, east, south, west.
     *
     * As an example, a T that has a horizontal pipe
     * with the T open to the bottom would be:
     *
     * false, true, true, true
     */
    protected boolean[] connect = {false, false, false, false};

    /**
     * column in the playing area (index into array)
     */
    private int c = 0;

    /**
     * row in the playing area (index into array)
     */
    private int r = 0;

    /**
     * Angle of the current pipe piece
     */
    private float angle;

    /**
     * X,Y location in canvas
     */
    protected Coordinate coord = new Coordinate(0, 0, 0, 0);

    /**
     * Depth-first visited visited
     */
    private boolean visited = false;

    /**
     * The image for the actual pipe
     */
    protected Bitmap pipe;

    private int id = 0;


    /**
     * Constructor
     * @param north True if connected north
     * @param east True if connected east
     * @param south True if connected south
     * @param west True if connected west
     */
    public Pipe(Context context, int id, boolean north, boolean east, boolean south, boolean west) {
        connect[0] = north;
        connect[1] = east;
        connect[2] = south;
        connect[3] = west;
        this.id = id;

        factory = new PipeFactory(context);
        pipe = BitmapFactory.decodeResource(context.getResources(), id);
        pipe = Bitmap.createScaledBitmap(pipe, 100, 100, true);
    }

    public Bitmap getPipe(){
        return pipe;
    }

    /**
     * Search to see if there are any downstream of this pipe
     *
     * This does a simple depth-first search to find any connections
     * that are not, in turn, connected to another pipe. It also
     * set the visited flag in all pipes it does visit, so you can
     * tell if a pipe is reachable from this pipe by checking that flag.
     * @return True if no leaks in the pipe
     */
    public boolean search() {
        visited = true;

        for(int d=0; d<4; d++) {
            /*
             * If no connection this direction, ignore
             */
            if(!connect[d]) {
                //Log.e("Dead end", "A dead end was found at row: " + this.r + " | column: " + this.c + " | in the direction " + d);
                continue;
            }

            Pipe n = neighbor(d);
            if(n == null) {
               //Log.e("Leak found", "A leak occurred at row: " + this.r + " | column: " + this.c + " | in the direction " + d);
                Pipe leak = factory.build(R.drawable.leak);
                switch(d){
                    case 0:
                        playingArea.add(leak, r - 1, c);
                        break;
                    case 1:
                        leak.setAngle(90);
                        playingArea.add(leak, r, c + 1);
                        break;
                    case 2:
                        leak.setAngle(180);
                        playingArea.add(leak, r + 1, c);
                        break;
                    case 3:
                        leak.setAngle(-90);
                        playingArea.add(leak, r, c - 1);
                        break;
                }

                // We leak
                // We have a connection with nothing on the other side
                return false;
            }

            // What is the matching location on
            // the other pipe. For example, if
            // we are looking in direction 1 (east),
            // the other pipe must have a connection
            // in direction 3 (west)
            int dp = (d + 2) % 4;
            if(!n.connect[dp]) {
                // We have a bad connection, the other side is not
                // a flange to connect to
                return false;
            }

            if(n.visited) {
                // Already visited this one, so no leaks this way
                continue;
            } else {
                // Is there a lead in that direction
                if(!n.search()) {
                    // We found a leak downstream of this pipe
                    return false;
                }
            }
        }

        // Yah, no leaks
        return true;
    }

    /**
     * Find the neighbor of this pipe
     * @param d Index (north=0, east=1, south=2, west=3)
     * @return Pipe object or null if no neighbor
     */
    private Pipe neighbor(int d) {
        switch(d) {
            case 0:
                return playingArea.getPipe(r-1, c);

            case 1:
                return playingArea.getPipe(r, c+1);

            case 2:
                return playingArea.getPipe(r+1, c);

            case 3:
                return playingArea.getPipe(r, c-1);
        }

        return null;
    }

    /**
     * Get the playing area
     * @return Playing area object
     */
    public PipeContainer getPlayingArea() {
        return playingArea;
    }

    /**
     * Set the playing area and location for this pipe
     * @param playingArea Playing area we are a member of
     * @param r row
     * @param c col
     */
    public void set(PipeContainer playingArea, int r, int c) {
        this.playingArea = playingArea;
        this.r = r;
        this.c = c;
    }

    /**
     * Has this pipe been visited by a search?
     * @return True if yes
     */
    public boolean beenVisited() {
        return visited;
    }

    /**
     * Set the visited flag for this pipe
     * @param visited Value to set
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void draw(Canvas canvas, float posX, float posY, float pipeSize, float angle) {
        coord.x = posX;
        coord.y = posY;
        coord.size = pipeSize;
        coord.angle = angle;

        canvas.save();

        // Position
        canvas.translate(posX , posY);

        // Scale
        canvas.scale( pipeSize / pipe.getWidth(), pipeSize / pipe.getHeight());

        // Rotation
        canvas.rotate(angle);

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-pipe.getWidth() / 2f, -pipe.getHeight() / 2f);

        // Draw the bitmap
        canvas.drawBitmap(pipe, 0, 0, null);

        canvas.restore();
    }

    /**
     * Test to see if we have touched a puzzle piece
     * @param testX X location
     * @param testY Y location
     * @return true if we hit the piece
     */
    public boolean hit(float testX, float testY) {

        float x1 = coord.x - coord.size / 2;
        float x2 = coord.x + coord.size / 2;
        float y1 = coord.y - coord.size / 2;
        float y2 = coord.y + coord.size / 2;

        //Log.e("X Coordinates", "x1: " + x1 + " | testX: " + testX + " | x2: " + x2);

        if(x1 <= testX && x2 >= testX && y1 <= testY && y2 >= testY ) {
            return true;
        }

        // We are within the rectangle of the piece.
        return false;
    }

    public void setCoordinate(float x, float y){
        coord.x += x;
        coord.y += y;

    }

    public void setAngle(float angle){
        int quarterTurns = (int)((angle - coord.angle) / 90) % 3;
        if (quarterTurns < 0){
            quarterTurns += 4;
        }

        for (int i = 0; i < quarterTurns; i++){
            connect = new boolean[]{connect[3], connect[0], connect[1], connect[2]};
        }

        //Log.e("Rotation", "A piece has been rotated " + quarterTurns + " quarter turns. N: " + connect[0] + " | E: " + connect[1] + " | S: " + connect[2] + " | W: " + connect[3]);

        coord.angle = angle;
    }

    public float getAngle(){
        return coord.angle;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public class Coordinate implements Serializable{
        public float x;
        public float y;
        public float size;
        public float angle;

        public Coordinate(float x, float y, float size, float angle){
            this.x = x;
            this.y = y;
            this.size = size;
            this.angle = angle;

        }
    }

    public PipeData toData(){
        PipeData data = new PipeData();
        data.rotation = coord.angle;
        data.uid = id;
        return data;
    }
}
