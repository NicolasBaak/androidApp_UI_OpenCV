package nicolas.aplicaionuno.mainactivitypaint;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton rojo;
    ImageButton azul;
    Lienzo lienzo;
    String color = null;
    private final int PHONE_READ_CODE = 100;
    ImageView Fondo;
    Bitmap bmp;

    ImageButton pincel;
    ImageButton nuevoCanvas;
    ImageButton borrador;
    ImageButton eliminar;
    ImageButton guardar;
    ImageButton importar;

    private SeekBar tama;
    private TextView mostrarTama;


    private int tamaPincel = 50;
    private int tamaGoma = 50;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        pincel = (ImageButton)findViewById(R.id.buttonPincel);
        nuevoCanvas = (ImageButton) findViewById(R.id.buttonImportar);
        borrador = (ImageButton) findViewById(R.id.buttonBorrador);
        guardar = (ImageButton) findViewById( R.id.buttonGuardar);
        eliminar = (ImageButton) findViewById(R.id.buttonElimiar);
        importar = (ImageButton) findViewById(R.id.buttonImportar);

        Fondo = (ImageView) findViewById(R.id.imageViewFondo);

        pincel.setOnClickListener(this);
        nuevoCanvas.setOnClickListener(this);
        borrador.setOnClickListener(this);
        guardar.setOnClickListener(this);
        eliminar.setOnClickListener(this);
        importar.setOnClickListener(this);

        lienzo = (Lienzo)findViewById(R.id.lienzo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.buttonPincel:
                final Dialog tamaPunto = new Dialog(this);
                tamaPunto.setTitle("Tamaño del punto");
                tamaPunto.setContentView(R.layout.tamano_pincel);

                tama =(SeekBar)tamaPunto.findViewById(R.id.seekBarTama);
                mostrarTama = (TextView)tamaPunto.findViewById(R.id.editTextTama);
                tama.setProgress(tamaPincel);
                mostrarTama.setText("Tamaño: "+ tamaPincel + "/"+ tama.getMax());

                tama.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mostrarTama.setText("Tamaño: "+ progress + "/"+ tama.getMax());
                        tamaPincel = progress;
                        Lienzo.setBorrado(false);
                        Lienzo.setTamaPunto((float) progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                rojo = (ImageButton)tamaPunto.findViewById(R.id.ColorRojo);
                azul = (ImageButton)tamaPunto.findViewById(R.id.ColorAzul);

                rojo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        color = v.getTag().toString();
                        lienzo.setColor(color);
                    }
                });

                azul.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        color = v.getTag().toString();
                        lienzo.setColor(color);
                    }
                });



                tamaPunto.show();
                break;

            case R.id.buttonElimiar:
                eliminar.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder newDialog = new AlertDialog.Builder(eliminar.getContext());

                        newDialog.setTitle("¿Borrar los trazos actuales?");

                        //newDialog.setMessage("No se elimina la imagen seleccionada");

                        newDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){

                            public void onClick(DialogInterface dialog, int which){
                                lienzo.NuevoDibujo();
                                dialog.dismiss();
                            }

                        });

                        newDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                dialog.cancel();
                            }
                        });
                        newDialog.show();
                    }
                });
                break;
            case R.id.buttonImportar:
                importar.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},  PHONE_READ_CODE);
                        }
                        else{
                            OlderVersions();
                        }
                    }

                    private void OlderVersions(){
                        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        if( CheckPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                            startActivityForResult(choosePictureIntent, 0);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "You declined the access", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;


            case R.id.buttonBorrador:
                final Dialog tamaBorrador= new Dialog(this);
                tamaBorrador.setTitle("Tamaño de borrado");
                tamaBorrador.setContentView(R.layout.tamano_borrador);
                tama =(SeekBar)tamaBorrador.findViewById(R.id.seekBarTama);
                mostrarTama = (TextView)tamaBorrador.findViewById(R.id.editTextTama);
                tama.setProgress(tamaGoma);
                mostrarTama.setText("Tamaño: "+ tamaGoma + "/"+ tama.getMax());

                tama.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mostrarTama.setText("Tamaño: "+ progress + "/"+ tama.getMax());
                        tamaGoma = progress;
                        Lienzo.setBorrado(true);
                        Lienzo.setTamaPunto((float) progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                tamaBorrador.show();
                break;


             default:
                 break;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //ESTAMOS EN EL CODIGO DE LA LECTURA DE LA GALERIA
        switch (requestCode){
            case PHONE_READ_CODE:
                String permission = permissions[0];
                int result = grantResults[0];

                if(permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //Comprobar si ha sido aceptado o denegado la peticion del permiso
                    if(result == PackageManager.PERMISSION_GRANTED){
                        //Consedio el permiso
                        Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(choosePictureIntent, 0);
                    }else{
                        //No concedio su permiso
                        Toast.makeText(MainActivity.this, "You declined the access", Toast.LENGTH_LONG).show();
                    }
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }

    private boolean CheckPermission(String permission){
        int result = this.checkCallingOrSelfPermission((permission));
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //Obtener la imagen de la galeria
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            try { Uri imageFileUri = intent.getData();
                try { BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
                    bmpFactoryOptions.inJustDecodeBounds = true;
                    bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream( imageFileUri), null, bmpFactoryOptions);
                    bmpFactoryOptions.inSampleSize = 2;
                    bmpFactoryOptions.inJustDecodeBounds = false;
                    bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream( imageFileUri), null, bmpFactoryOptions);
                    Bitmap alteredBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp .getHeight(), bmp.getConfig());
                    Canvas canvas = new Canvas(alteredBitmap);
                    Fondo.setImageBitmap(bmp);


                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);

                   // float height = convertirPxToDp(this, Fondo.getHeight());
                   // float width = convertirPxToDp(this, Fondo.getWidth());
                    System.out.println("--------");
                    System.out.println("alto de la pantalla: "+metrics.heightPixels);
                    System.out.println("ancho de la pantalla:"+metrics.widthPixels);

                    System.out.println("altura de la imagen: "+alteredBitmap.getHeight());
                    System.out.println("ancho de la imagen: "+alteredBitmap.getWidth());
                    float  proporcion = metrics.widthPixels/bmp.getWidth(); // ancho absoluto en pixels
                    System.out.println("proporcion: "+proporcion);
                    System.out.println("--------");
                    float  height = bmp.getHeight() * proporcion; // alto absoluto en pixels

                    lienzo.AreaDibujo(metrics.heightPixels, metrics.widthPixels, height, bmp.getHeight());

                    // lienzo.NuevoDibujo();
                    //BitmapHelper.getInstance().setBitmap(bmp);
                } catch (FileNotFoundException e) {
                    Log.v("ERROR", e.toString()); }
            }catch(Exception ex){
                Log.v("ERROR", ex.toString());
            }
        }
    }

    public float convertirPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

}
