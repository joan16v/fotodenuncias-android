package com.fotodenuncias;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

public class FotodenunciasActivity extends Activity {
    private EditText cajaDeTexto;
    private Button boton;
    private Button boton2;
    private ProgressBar mProgress;
    private String nombreImagen;
    private TextView logText;
    private TextView joan16v;
    private ImageView logo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotoden);
        cajaDeTexto = (EditText) findViewById(R.id.editText1);
        boton = (Button) findViewById(R.id.button1);
        boton2 = (Button) findViewById(R.id.button2);
        mProgress = (ProgressBar) findViewById(R.id.progressBar1);
        logText = (TextView) findViewById(R.id.logText);
        logo = (ImageView) findViewById(R.id.imageView1);
        boton2.setEnabled(false);
        mProgress.setVisibility(View.INVISIBLE);
        joan16v = (TextView) findViewById(R.id.textView5);

        joan16v.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String url = "http://www.twitter.com/joan16v";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        logo.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String url = "http://www.fotodenuncias.net";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        boton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                         android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        boton2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (cajaDeTexto.getText().toString().trim().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FotodenunciasActivity.this);
                    builder.setMessage("Tienes que escribir una descripción de tu foto-denuncia!")
                           .setCancelable(false)
                           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                               }
                           });
                    builder.show();
                } else {

                    cajaDeTexto.setEnabled(false);
                    boton2.setEnabled(false);
                    boton2.setText("Publicando...");
                    mProgress.setVisibility(View.VISIBLE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(FotodenunciasActivity.this);
                    builder.setMessage("Se va a subir la foto-denuncia a la web. Puede tardar un poco... ¿Adelante?")
                           .setCancelable(false)
                           .setPositiveButton("SI!", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                subirFichero();
                                    Builder builder2 = new AlertDialog.Builder(FotodenunciasActivity.this);
                                    builder2.setTitle("fotodenuncias.net");
                                    builder2.setMessage("Se ha subido correctamente la fotodenuncia a la web.");
                                    builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            resetForm();
                                       }
                                   });
                                   builder2.show();
                               }
                           })
                           .setNegativeButton("No...", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                    cajaDeTexto.setEnabled(true);
                                    boton2.setEnabled(true);
                                    boton2.setText("Publicar la fotodenuncia");
                                    mProgress.setVisibility(View.INVISIBLE);
                               }
                           });
                    builder.show();
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            String pathReal = getRealPathFromURI(targetUri);
            nombreImagen=pathReal;
            logText.setText(nombreImagen);
            boton.setText("Imagen elegida.");
            boton.setEnabled(false);
            boton2.setEnabled(true);
         }
    }

    protected void resetForm() {
         cajaDeTexto.setEnabled(true);
         cajaDeTexto.setText("");
         logText.setText("");
         boton.setText("Elegir imagen");
         boton.setEnabled(true);
         boton2.setText("Publicar la fotodenuncia");
         boton2.setEnabled(false);
         mProgress.setVisibility(View.INVISIBLE);
    }

    protected void subirFichero() {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        String pathToOurFile = nombreImagen;
        String urlServer = "http://www.fotodenuncias.net/android/android_file.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;

        try {
            FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));
            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            outputStream = new DataOutputStream( connection.getOutputStream() );
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            Integer serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
            e.printStackTrace();
        }

        try {
            HttpClient client = new DefaultHttpClient();
            String postURL = "http://www.fotodenuncias.net/android/android.php";
            HttpPost post = new HttpPost(postURL);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("descripcion", cajaDeTexto.getText().toString() ));
            params.add(new BasicNameValuePair("nombreImagen", nombreImagen ));
            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();
            if (resEntity != null) {
                Log.i("RESPONSE",EntityUtils.toString(resEntity));
                cajaDeTexto.setText(EntityUtils.toString(resEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
