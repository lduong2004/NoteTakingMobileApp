package com.example.notetakingmobileapp.Function;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {
    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private Bitmap loadedBitmap;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0) return;
        
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        
        // Fill background with white to avoid black background when saving as JPEG
        drawCanvas.drawColor(Color.WHITE);
        
        if (loadedBitmap != null) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedBitmap, w, h, true);
            drawCanvas.drawBitmap(scaledBitmap, 0, 0, canvasPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (canvasBitmap != null) {
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        }
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: 
                drawPath.moveTo(touchX, touchY); 
                break;
            case MotionEvent.ACTION_MOVE: 
                drawPath.lineTo(touchX, touchY); 
                break;
            case MotionEvent.ACTION_UP:
                if (drawCanvas != null) {
                    drawCanvas.drawPath(drawPath, drawPaint);
                }
                drawPath.reset();
                break;
        }
        invalidate();
        return true;
    }

    public Bitmap getDrawingBitmap() { 
        return canvasBitmap; 
    }

    public void loadExistingImageFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            this.loadedBitmap = bitmap;
            if (drawCanvas != null && getWidth() > 0 && getHeight() > 0) {
                // Ensure the background is white before drawing the loaded bitmap
                drawCanvas.drawColor(Color.WHITE);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedBitmap, getWidth(), getHeight(), true);
                drawCanvas.drawBitmap(scaledBitmap, 0, 0, canvasPaint);
            }
            invalidate();
        }
    }
}