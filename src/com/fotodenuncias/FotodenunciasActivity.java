package com.fotodenuncias;

//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.math.BigInteger;
import java.net.HttpURLConnection;
//import java.net.URI;
//import java.net.URI;
import java.net.URL;
//import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
//import android.widget.Toast;
import android.widget.ProgressBar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
//import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
//import org.apache.http.util.EntityUtils;

//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity; 
//import org.apache.http.entity.mime.content.FileBody;

//import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder; 
//import android.os.Bundle;

public class FotodenunciasActivity extends Activity {
    /** Called when the activity is first created. */
    
	private EditText cajaDeTexto;
	private Button boton;
	private Button boton2;
	private ProgressBar mProgress;
	//private HttpClient client;
	private String nombreImagen;
	private TextView logText;
	private TextView joan16v;
	private ImageView logo;
	
	@Override    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TextView tv = new TextView(this);
        //tv.setText("FotoDenuncias.net");
        //tv.setBackgroundColor(453);
        //setContentView(tv);
        //setContentView(R.layout.main);
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
        
        //cajaDeTexto.setText("Descripcion");

        boton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            	//cajaDeTexto.setText("equisssssss");
            	
            	Intent intent = new Intent(Intent.ACTION_PICK,
            		     android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            	startActivityForResult(intent, 0);            	
            	
            }
        });
        
        boton2.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {            	

            	if( cajaDeTexto.getText().toString().trim().equals("") ) {
            		
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
                	
                	//SecureRandom random = new SecureRandom();
                	//String android_id=new BigInteger(130, random).toString(32);
                	
                	AlertDialog.Builder builder = new AlertDialog.Builder(FotodenunciasActivity.this);
                	builder.setMessage("Se va a subir la foto-denuncia a la web. Puede tardar un poco... ¿Adelante?")
                	       .setCancelable(false)
                	       .setPositiveButton("SI!", new DialogInterface.OnClickListener() {
                	           public void onClick(DialogInterface dialog, int id) {
                	                
                	        	    subirFichero();
                	        	    
                	                Builder builder2 = new AlertDialog.Builder(FotodenunciasActivity.this);                
                	                builder2.setTitle("fotodenuncias.net"); 
                	                //builder.setIcon(R.drawable.beer); 
                	                builder2.setMessage("Se ha subido correctamente la fotodenuncia a la web.");
                	                //builder.setMessage(EntityUtils.toString(resEntity));
                	                builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                	                    public void onClick(DialogInterface dialog, int id) {
                	                    	resetForm();
                	                   }
                	               }); 
                	               //builder.setNegativeButton("cancel", null); 
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
                
            	/*try {
            		Thread.sleep(2000);
            	} catch(InterruptedException e){ }*/                      
                
                //resetForm();      	
            	
            }
        });        
        
        
    }
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 // TODO Auto-generated method stub
		 super.onActivityResult(requestCode, resultCode, data);

		 if (resultCode == RESULT_OK){
		     
			 Uri targetUri = data.getData();
		     //textTargetUri.setText(targetUri.toString());
			 
		     String pathReal = getRealPathFromURI(targetUri);
		    
			 //nombreImagen=data.getData().toString();
		     nombreImagen=pathReal;
			 logText.setText(nombreImagen);
			 
			 //decodeFile(new File(nombreImagen));
			 
			 //cajaDeTexto.setText(nombreImagen);
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

    	//SUBIR IMAGEN
    	HttpURLConnection connection = null;
    	DataOutputStream outputStream = null;
    	//DataInputStream inputStream = null;

    	//String pathToOurFile = "/data/file_to_send.mp3";
    	String pathToOurFile = nombreImagen;
    	String urlServer = "http://www.fotodenuncias.net/android/android_file.php";
    	String lineEnd = "\r\n";
    	String twoHyphens = "--";
    	String boundary =  "*****";

    	int bytesRead, bytesAvailable, bufferSize;
    	byte[] buffer;
    	int maxBufferSize = 1*1024*1024;

    	try
    	{            		
    		//Thread.sleep(2000);	
    	FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

    	URL url = new URL(urlServer);
    	connection = (HttpURLConnection) url.openConnection();

    	// Allow Inputs & Outputs
    	connection.setDoInput(true);
    	connection.setDoOutput(true);
    	connection.setUseCaches(false);

    	// Enable POST method
    	connection.setRequestMethod("POST");

    	connection.setRequestProperty("Connection", "Keep-Alive");
    	connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
    	
    	//String newname=pathToOurFile+"_"+android_id;
    	//String newname=android_id;
    	
    	outputStream = new DataOutputStream( connection.getOutputStream() );
    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
    	outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
    	//outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + newname +"\"" + lineEnd);
    	outputStream.writeBytes(lineEnd);

    	bytesAvailable = fileInputStream.available();
    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
    	buffer = new byte[bufferSize];

    	// Read file
    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);

    	while (bytesRead > 0)
    	{
    	outputStream.write(buffer, 0, bufferSize);
    	bytesAvailable = fileInputStream.available();
    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
    	}

    	outputStream.writeBytes(lineEnd);
    	outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

    	// Responses from the server (code and message)
    	Integer serverResponseCode = connection.getResponseCode();
    	String serverResponseMessage = connection.getResponseMessage();

    	fileInputStream.close();
    	outputStream.flush();
    	outputStream.close();
    	}
    	catch (Exception ex)
    	{
    	//Exception handling
    	}                	
    	//FIN DE SUBIR IMAGEN
    	
    	//SUBIR DESCRIPCION
        try {
        HttpClient client = new DefaultHttpClient();  
        String postURL = "http://www.fotodenuncias.net/android/android.php";
        HttpPost post = new HttpPost(postURL); 
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("descripcion", cajaDeTexto.getText().toString() ));
            params.add(new BasicNameValuePair("nombreImagen", nombreImagen ));
            //params.add(new BasicNameValuePair("android_id", android_id ));
            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
        	//UrlEncodedFormEntity ent = new UrlEncodedFormEntity
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);  
            HttpEntity resEntity = responsePOST.getEntity();  
            if (resEntity != null) {    
                //Log.i("RESPONSE",EntityUtils.toString(resEntity));
            	//cajaDeTexto.setText(EntityUtils.toString(resEntity));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }   
        //SUBIR DESCRIPCION		
		
	}	
	
	public String getRealPathFromURI(Uri contentUri) {

        // can post image
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery( contentUri,
                        proj, // Which columns to return
                        null,       // WHERE clause; which rows to return (all rows)
                        null,       // WHERE clause selection arguments (none)
                        null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
	}
	
	/*private Bitmap decodeFile(File f){
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=70;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}*/	
	
	/*
	 public void httpPostFileUpload( 
             HttpClient client, 
             String filePath, 
             String uploadUri, 
             String inputNameAttr) throws ClientProtocolException, 
IOException { 
     HttpUriRequest  request         = new HttpPost(uploadUri); 
     MultipartEntity form            = new MultipartEntity(); 
     // disable expect-continue handshake (lighttpd doesn't support it) 
     client.getParams().setBooleanParameter( 
                     "http.protocol.expect-continue", false); 
     form.addPart(inputNameAttr, new FileBody(new File(filePath))); 
             ((HttpEntityEnclosingRequestBase) request).setEntity(form); 
             try { 
                     client.execute(request); 
             } catch (ClientProtocolException e) { 
                     throw e; 
             } catch (IOException ee) { 
                     throw ee; 
             } 
	 } 	*/
	

}

/*try {
Thread.sleep(2000);
} catch(InterruptedException e){ }*/            	

/*
try {
HttpClient client = new DefaultHttpClient();
HttpGet request = new HttpGet();
request.setURI(new URI("http://www.fotodenuncias.net/android/android.php"));
HttpResponse httpresponse = client.execute(request);
cajaDeTexto.setText(httpresponse.toString());
} catch (Exception e) {
e.printStackTrace();
} */

//enviar el tema
/*try {
HttpClient client = new DefaultHttpClient();  
String postURL = "http://www.fotodenuncias.net/android/android.php";
HttpPost post = new HttpPost(postURL); 
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("descripcion", cajaDeTexto.getText().toString() ));
    params.add(new BasicNameValuePair("imagen", nombreImagen ));
    UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
	//UrlEncodedFormEntity ent = new UrlEncodedFormEntity
    post.setEntity(ent);
    HttpResponse responsePOST = client.execute(post);  
    HttpEntity resEntity = responsePOST.getEntity();  
    if (resEntity != null) {    
        //Log.i("RESPONSE",EntityUtils.toString(resEntity));
    	cajaDeTexto.setText(EntityUtils.toString(resEntity));
    }
} catch (Exception e) {
e.printStackTrace();
} */

/*try {
HttpClient client = new DefaultHttpClient();  
String getURL = "http://www.fotodenuncias.net/android/android.php";
HttpGet get = new HttpGet(getURL);
HttpResponse responseGet = client.execute(get);  
HttpEntity resEntityGet = responseGet.getEntity();  
if (resEntityGet != null) {                                
	cajaDeTexto.setText(EntityUtils.toString(resEntityGet));
}
} catch (Exception e) {
e.printStackTrace();
} */

/*File file = new File(nombreImagen);
try {
 HttpClient client = new DefaultHttpClient();  
 String postURL = "http://www.fotodenuncias.net/android/android_file.php";
 HttpPost post = new HttpPost(postURL); 
 FileBody bin = new FileBody(file);
 MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
 reqEntity.addPart("myFile", bin);
 post.setEntity(reqEntity);  
 HttpResponse response = client.execute(post);  
 HttpEntity resEntity = response.getEntity();  
 if (resEntity != null) {    
     Log.i("RESPONSE",EntityUtils.toString(resEntity));
 }
} catch (Exception e) {
e.printStackTrace();
} */

/*File f = new File(nombreImagen); 
try { 
f.createNewFile(); 
Date d = new Date(); 
PrintWriter writer = new PrintWriter(f); 
writer.println(d.toString()); 
writer.close(); 
HttpClient client = new DefaultHttpClient(); 
httpPostFileUpload(client, nombreImagen, "http://www.fotodenuncias.net/android/android_file.php", "uploadedfile"); 
} catch (Exception e) { 
// TODO Auto-generated catch block 
e.printStackTrace(); 
} */