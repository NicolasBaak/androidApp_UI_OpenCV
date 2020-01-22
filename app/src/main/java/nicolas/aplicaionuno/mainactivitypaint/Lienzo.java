package nicolas.aplicaionuno.mainactivitypaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
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
    public Lienzo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        imagen = context.getResources().getDrawable(R.drawable.fondo1);
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
            drawPaint.setColor(Color.WHITE);
            //drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        else {
            drawPaint.setColor(paintColor);
            //drawPaint.setXfermode(null);
        }

    }



    public void NuevoDibujo(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();

    }

    public void InsertarImagen(Context context){

        imagen = context.getResources().getDrawable(R.drawable.fondo1);
        //Dimensiones del canvas
        int altoCa = getBottom();
        int anchoCa = getRight();
        float medioCa = (float)altoCa/anchoCa;

        //Dimensiones canvas
        int anchoIm = imagen.getIntrinsicWidth();
        int altoIm = imagen.getIntrinsicHeight();
        float medioIn = (float)altoIm/anchoIm;
        //Ajustar tamaño
        int alto, ancho;
        if(medioCa < medioIn){
            ancho = anchoCa;
            alto = (int) (medioCa * medioIn);
        }
        else{
            alto = altoCa;
            ancho = (int)(alto/medioIn);
        }

        //Insertar imagen
        imagen.setBounds(0, 0, ancho, alto);
        imagen.draw(drawCanvas);
        invalidate();
    }


}
