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
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
        // Nếu có ảnh cũ được truyền vào, vẽ nó lên canvas luôn để có thể vẽ đè lên
        if (loadedBitmap != null) {
            drawCanvas.drawBitmap(loadedBitmap, 0, 0, canvasPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: drawPath.moveTo(touchX, touchY); break;
            case MotionEvent.ACTION_MOVE: drawPath.lineTo(touchX, touchY); break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
        }
        invalidate();
        return true;
    }

    public Bitmap getDrawingBitmap() { return canvasBitmap; }

    public void loadExistingImage(String imagePath) {
        if (imagePath != null) {
            // Load file ảnh cũ thành Bitmap
            loadedBitmap = BitmapFactory.decodeFile(imagePath);
            invalidate(); // Yêu cầu vẽ lại view
        }
    }
}