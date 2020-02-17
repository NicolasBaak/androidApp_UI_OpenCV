package nicolas.aplicaionuno.mainactivitypaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class Lienzo extends View {
    //Path que se utiliza par ir pintando las lineas
    private Path drawPath;
    //Paint de dibujar  y Paint de Canvas
    private static Paint drawPaint;
    private Paint canvasPaint;
    //Color inicial
    private static int paintColor = 0xFFFF0000;
    //canvas
    private Canvas drawCanvas;
    //canvas para guardar
    private Bitmap canvasBitmap;
    //Borrador
    private static boolean borrado=false;
    //Imagen de fondo
    Drawable imagen;

    Bitmap icon = BitmapFactory.decodeResource(getResources(),
            R.drawable.fondo1);
    public Lienzo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setupDrawing();
    }

    private void setupDrawing(){
        //Configuracion del area sobre la que pintar
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);


        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void AreaDibujo(float altoT, float anchoT, float alto, float altoOriginal){

        float emp = (altoT-alto)/2;

        RectF areaRectangulo = new RectF(0, 0, anchoT, alto);
       // drawCanvas.clipRect(0f, 0f, ancho, alto, Region.Op.UNION);
        System.out.println("dezplasamiento: "+emp);

        drawCanvas.drawRect(0,0,anchoT, alto, drawPaint);
        drawCanvas.clipRect(areaRectangulo);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }


    //Pinta la vista y es llamado desde el Ontouch con el invalidate() para repintar el canvas
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        canvas.drawColor(Color.TRANSPARENT);
        System.out.println("Wight: "+getWidth()+" Heiht"+getHeight());
    }


    //Registra los touch de usuario

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                break;
             default:
                 return false;
        }
        //repintar el canvas, para actualizar los trazos
        invalidate();
        return true;
    }

    public void setColor(String newColor){
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
    }

    public static void setTamaPunto(float nuevoTama){
        drawPaint.setStrokeWidth( nuevoTama);
    }

    //set borrado true or false
    public static void setBorrado(boolean estaborrado){
        borrado=estaborrado;

        if(borrado) {
            //drawPaint.setColor(Color.GREEN);
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else {
            drawPaint.setXfermode(null);
            drawPaint.setColor(paintColor);

        }
    }

    public void NuevoDibujo(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();

    }
}
