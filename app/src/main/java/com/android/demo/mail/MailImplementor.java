package com.android.demo.mail;

import android.app.Activity;

/** 
 * Define la interfaz para las clases de la implementaci�n. 
 * La interfaz no se tiene que corresponder directamente con la interfaz de la abstracci�n.
 *  
 */
public interface MailImplementor {
	   
   /**  Actualiza la actividad desde la cual se abrir� la actividad de gesti�n de correo */
   public void setSourceActivity(Activity source);
   
   /** Permite lanzar la actividad encargada de gestionar el correo enviado */
   public void send(String subject, String body);

}
