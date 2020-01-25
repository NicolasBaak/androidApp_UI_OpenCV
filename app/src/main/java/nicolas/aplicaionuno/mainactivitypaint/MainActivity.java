package nicolas.aplicaionuno.mainactivitypaint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton rojo;
    ImageButton azul;
    Lienzo lienzo;

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
                importar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lienzo.InsertarImagen(lienzo.getContext());
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
}
