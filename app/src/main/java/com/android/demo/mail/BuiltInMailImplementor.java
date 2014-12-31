package com.android.demo.mail;

import android.app.Activity;
import android.content.Intent;

/** Concrete implementor utilizando aplicaci�n por defecto de Android para gestionar mail. No funciona en el emulador si no se ha configurado previamente el mail */
public class BuiltInMailImplementor implements MailImplementor{
	
   /** actividad desde la cual se abrir� la actividad de gesti�n de correo */
   private Activity sourceActivity;
   
   /** Constructor
    * @param source actividad desde la cual se abrir� la actividad de gesti�n de correo
    */
   public BuiltInMailImplementor(Activity source){
	   setSourceActivity(source);
   }

   /**  Actualiza la actividad desde la cual se abrir� la actividad de gesti�n de correo */
   public void setSourceActivity(Activity source) {
	   sourceActivity = source;
   }

   /** 
    * Implementaci�n del m�todo send utilizando la aplicaci�n de gesti�n de correo de Android
    * Solo se copia el asunto y el cuerpo
    * @param subject asunto
    * @param body cuerpo del mensaje
    */
   public void send (String subject, String body) {
	   
	Intent emailIntent = new Intent(Intent.ACTION_SEND);

	// String aEmailList[] = { "user1@gmail.com", "user2@gmail.com" };  
	// String aEmailCCList[] = { "user3@gmail.com","user4@gmail.com"};  
	// String aEmailBCCList[] = { "user5@gmail.com" };  
    // emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);  
    // emailIntent.putExtra(android.content.Intent.EXTRA_CC, aEmailCCList);  
    // emailIntent.putExtra(android.content.Intent.EXTRA_BCC, aEmailBCCList);  
	emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
	emailIntent.setType("plain/text");  
	emailIntent.putExtra(Intent.EXTRA_TEXT, body);
	
	sourceActivity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
   }

}
