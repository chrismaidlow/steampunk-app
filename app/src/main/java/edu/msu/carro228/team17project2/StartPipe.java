package edu.msu.carro228.team17project2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import edu.msu.carro228.team17project2.Data.PipeData;

public class StartPipe extends Pipe {

    /**
     * Handle image
     */
    Bitmap handle;

    /**
     * Is the valve open or not
     */
    boolean open = false;

    /**
     * Constructor
     *
     * @param context
     */
    public StartPipe(Context context) {
        super(context, R.drawable.straight, true, false, false, false);
        this.setAngle(90);

        handle = BitmapFactory.decodeResource(context.getResources(), R.drawable.handle);
        handle = Bitmap.createScaledBitmap(handle, 100, 100, true);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY, float pipeSize, float angle) {
        super.draw(canvas, posX, posY, pipeSize, angle);
        canvas.save();

        // Position
        canvas.translate(posX , posY);

        // Scale
        canvas.scale( pipeSize / handle.getWidth(), pipeSize / handle.getHeight());

        if (open) {
            // Rotation
            canvas.rotate(angle);
        }

        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-handle.getWidth() / 2f, -handle.getHeight() / 2f);

        // Draw the bitmap
        canvas.drawBitmap(handle, 0, 0, null);

        canvas.restore();
    }

    /**
     * Setter for open
     * @param open
     */
    public void setOpen(boolean open){
        this.open = open;
    }

    @Override
    public PipeData toData(){
        PipeData data = super.toData();
        data.uid = 0;
        return data;
    }
}
