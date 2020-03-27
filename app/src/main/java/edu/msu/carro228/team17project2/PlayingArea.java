package edu.msu.carro228.team17project2;

import android.content.Context;
import android.graphics.Canvas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import edu.msu.carro228.team17project2.Data.PipeData;
import edu.msu.carro228.team17project2.Data.Properties;

/**
 * A representation of the playing area
 */
public class PlayingArea extends PipeContainer {

    enum Turn{
        P1, P2
    }

    /**
     * Current turn
     */
    private Turn turn = Turn.P1;

    private Map<Turn, Pipe[]> specialPipes = new HashMap<Turn, Pipe[]>(){{
        put(Turn.P1, new Pipe[2]);
        put(Turn.P2, new Pipe[2]);
    }};

    /**
     * Construct a playing area
     * @param width Width (integer number of cells)
     * @param height Height (integer number of cells)
     */
    public PlayingArea(int width, int height, Context context) {
        super(width, height, context);
    }

    /**
     * Gets the current turn
     * @return turn
     */
    public Turn getTurn(){
        return turn;
    }

    /**
     * Changes the current turn
     */
    public void nextTurn(){
        if (turn == Turn.P1){
            turn = Turn.P2;
        }else{
            turn = Turn.P1;
        }
    }

    public Pipe getSpecialPipe(Turn turn, int index){
        return specialPipes.get(turn)[index];
    }

    public void addSpecialPipe(Turn turn, int index, Pipe pipe, int row, int column){
        specialPipes.get(turn)[index] = pipe;
        add(pipe, row, column);
    }

    /**
     * Search to determine if this pipe has no leaks
     * @param start Starting pipe to search from
     * @return true if no leaks
     */
    public boolean search(Pipe start) {
        ((StartPipe)start).setOpen(true);
        /*
         * Set the visited flags to false
         */
        for(Pipe[] row: pipes) {
            for(Pipe pipe : row) {
                if (pipe != null) {
                    pipe.setVisited(false);
                }
            }
        }

        /*
         * The pipe itself does the actual search
         */
        return start.search();
    }

    public void draw(Canvas canvas, Properties drawProperties){
        updateDim((int)drawProperties.coordinate.x, (int)drawProperties.coordinate.y);
        // Determine the minimum of the two dimensions
        float minDim = drawProperties.dimensions.width < drawProperties.dimensions.height ? drawProperties.dimensions.width : drawProperties.dimensions.height;

        float areaSize = minDim * drawProperties.scale;

        // Compute the margins so we center the area
        float marginX = (drawProperties.dimensions.width - areaSize) / 2;
        float marginY = (drawProperties.dimensions.height - areaSize) / 2;

        super.draw(canvas, drawProperties.coordinate.x, drawProperties.coordinate.y, areaSize, areaSize);
    }

    @Override
    public void FromData(PipeData[][] data){
        PipeFactory factory = new PipeFactory(context);
        int s = 0;
        int e = 0;
        int r = -1;
        int c;
        for(Pipe[] row: pipes) {
            r ++;
            c = 0;
            for(Pipe pipe : row) {
                if(data[r][c] != null){
                    Pipe p = factory.build(data[r][c].uid);
                    if (data[r][c].uid == 0){
                        if (s == 0){
                            addSpecialPipe(Turn.P1, GameView.START_PIPE_INDEX, p, r, c);
                            s ++;
                        }else if (s == 1){
                            addSpecialPipe(Turn.P2, GameView.START_PIPE_INDEX, p, r, c);
                        }
                    }else if (data[r][c].uid == 1) {
                        if (e == 0) {
                            addSpecialPipe(Turn.P1, GameView.END_PIPE_INDEX, p, r, c);
                            e++;
                        } else if (e == 1) {
                            addSpecialPipe(Turn.P2, GameView.END_PIPE_INDEX, p, r, c);
                        }
                    }else{
                        p.setAngle(data[r][c].rotation);
                    }


                    pipes[r][c] = p;
                    p.set(this, r, c);
                }else{
                    pipes[r][c] = null;
                }


                c ++;
            }
        }
    }


}
