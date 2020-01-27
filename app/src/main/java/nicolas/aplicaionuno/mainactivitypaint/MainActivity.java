package nicolas.aplicaionuno.mainactivitypaint;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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


    private final int PHONE_READ_CODE = 100;
    ImageView Fondo;
    Bitmap bmp;

    ImageButton pincel;
    ImageButton nuevoCanvas;
    ImageButton borrador;
    ImageButton guardar;

    private SeekBar tama;
    private TextView mostrarTama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        rojo = (ImageButton)findViewById(R.id.ColorRojo);
        azul = (ImageButton)findViewById(R.id.ColorAzul);
        pincel = (ImageButton)findViewById(R.id.buttonPincel);
        nuevoCanvas = (ImageButton) findViewById(R.id.buttonNuevo);
        borrador = (ImageButton) findViewById(R.id.buttonBorrador);
        guardar = (ImageButton) findViewById( R.id.buttonGuardar);


        Fondo = (ImageView) findViewById(R.id.imageViewFondo);
        rojo.setOnClickListener(this);
        azul.setOnClickListener(this);
        pincel.setOnClickListener(this);
        nuevoCanvas.setOnClickListener(this);
        borrador.setOnClickListener(this);
        guardar.setOnClickListener(this);

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
        String color = null;
        switch (v.getId()){
            case R.id.ColorRojo:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.ColorAzul:
                color = v.getTag().toString();
                lienzo.setColor(color);
                break;
            case R.id.buttonPincel:
                final Dialog tamaPunto = new Dialog(this);
                tamaPunto.setTitle("Tamaño del punto");
                tamaPunto.setContentView(R.layout.tamano_pincel);

                tama =(SeekBar)tamaPunto.findViewById(R.id.seekBarTama);
                mostrarTama = (TextView)tamaPunto.findViewById(R.id.editTextTama);

                mostrarTama.setText("Cantidad: "+ tama.getProgress()+ "/"+ tama.getMax());

                tama.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mostrarTama.setText("Tamaño del pincel: "+ progress + "/"+ tama.getMax());
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
                tamaPunto.show();

                break;
            case R.id.buttonNuevo:
                final Dialog opc = new Dialog(this);
                opc.setTitle("Guardar");
                opc.setContentView(R.layout.guardar_opc);

                ImageButton anadir;
                anadir =  opc.findViewById(R.id.imageButtonNuevo);
                anadir.setOnClickListener(new View.OnClickListener(){
                    @Override

                    public void onClick(View v) {
                        AlertDialog.Builder newDialog = new AlertDialog.Builder(opc.getContext());

                        newDialog.setTitle("Nuevo Dibujo");

                        newDialog.setMessage("¿Comenzar nuevo dibujo (perderás el dibujo actual)?");

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

                ImageButton importar;
                importar = opc.findViewById(R.id.imageButtonImportar);
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

                opc.show();
                break;


            case R.id.buttonBorrador:
                final Dialog tamaBorrador= new Dialog(this);
                tamaBorrador.setTitle("Tamaño de borrado");
                tamaBorrador.setContentView(R.layout.tamano_pincel);
                tama =(SeekBar)tamaBorrador.findViewById(R.id.seekBarTama);
                mostrarTama = (TextView)tamaBorrador.findViewById(R.id.editTextTama);

                mostrarTama.setText("Cantidad: "+ tama.getProgress()+ "/"+ tama.getMax());

                tama.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        mostrarTama.setText("Tamaño del borrador: "+ progress + "/"+ tama.getMax());
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

                    //BitmapHelper.getInstance().setBitmap(bmp);
                } catch (FileNotFoundException e) {
                    Log.v("ERROR", e.toString()); }
            }catch(Exception ex){
                Log.v("ERROR", ex.toString());
            }
        }
    }

}
