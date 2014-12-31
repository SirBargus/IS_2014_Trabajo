package com.android.demo.mail;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.demo.notepad3.*;

/** 
 * Actividad para enviar un mensaje a travs de las libreras JavaMail adaptadas para Android
 * Se asume que la direccin desde la que se envia el mail (from) se puede utilizar tambi�n como usuario
 * en el servidor SMTP. Si el usuario fuese distinto, modifquese la actividad apropiadamente.
 */
public class JavaMailActivity extends Activity {

    /** Clave utilizada para recibir el asunto (subject) del correo a travs del bundle del intent que inicia la actividad. ase mtodo populateFields. */
    public static final String KEY_SUBJECT ="SUBJECT";

    /** clave utilizada para recibir el cuerpo (body) del correo a travs del bundle del intent que inicia la actividad. Vase mtodo populateFields. */
    public static final String KEY_BODY="BODY";

    /** caja de texto para indicar el remitente */
	private EditText mFromText;
	
	/** caja de texto para indicar el destinatario */
    private EditText mToText;
    
    /** caja de texto con el asunto */
    private EditText mSubjectText;

    /** caja de texto con el cuerpo del mensaje */
    private EditText mBodyText;

    /** caja de texto para introducir la clave */
    private EditText mPasswordText;

    /** caja de texto para introducir la IP del servidor SMTP */
    private EditText mHostText;

    /** caja de texto para introducir el puerto del servidor SMTP */
    private EditText mPortText;

    /** caja de texto para introducir el puerto del socket factory del servidor SMTP */
    private EditText mSPortText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.java_mail_form);
        setTitle(R.string.java_mail_form);

        mFromText = (EditText) findViewById(R.id.from);
        mToText = (EditText) findViewById(R.id.to);
        mSubjectText = (EditText) findViewById(R.id.subject);
        mBodyText = (EditText) findViewById(R.id.body);
        mPasswordText = (EditText) findViewById(R.id.password);
        mHostText = (EditText) findViewById(R.id.host);
        mPortText = (EditText) findViewById(R.id.port);
        mSPortText = (EditText) findViewById(R.id.sport);
        

        Button sendButton = (Button) findViewById(R.id.send);

		populateFields();

        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                send();
                finish();
            }
        });
    }

    /** Actualiza los valores de las cajas de texto. 
      * En particular se toman los valores del subject y del body a partir del bundle del Intent de la actividad
      */
    private void populateFields() {
    	
    	String from = "modificame@gmail.com";
    	String to = "modificame@unizar.es";
		Bundle extras = getIntent().getExtras();
		mSubjectText.setText(extras.getString(KEY_SUBJECT));
		mBodyText.setText(extras.getString(KEY_BODY));
		mFromText.setText(from);
		mToText.setText(to);
		
    }


    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    /** metodo que se encarga de realizar el eno a travs de la librera JavaMail */
    private void send(){
    	
    	String from = mFromText.getText().toString();
    	String to = mToText.getText().toString();
        String[] toArr = {to}; 
    	String password = mPasswordText.getText().toString();
    	String host = mHostText.getText().toString();
    	String port = mPortText.getText().toString();
    	String sport = mSPortText.getText().toString();

        String subject = mSubjectText.getText().toString();
    	String body = mBodyText.getText().toString();
    	
        JavaMail m = new JavaMail(from, toArr, from, password
        		,host, port, sport
        		,subject, body); 
        
        try { 
   
          if(m.send()) { 
        	//A toast is a view containing a quick little message for the user
        	Toast.makeText(this, "Mensaje enviado correctamente", Toast.LENGTH_SHORT).show();
          } else { 
          	Toast.makeText(this, "Mensaje no enviado", Toast.LENGTH_SHORT).show();
          } 
        } catch(Exception e) { 
          	Toast.makeText(this, "Se produjo una excepci�n al enviar mensaje", Toast.LENGTH_SHORT).show();
            android.util.Log.d("JavaMailActivity", "Se produjo una excepci�n: " + e.getMessage());
        } 
    }

}