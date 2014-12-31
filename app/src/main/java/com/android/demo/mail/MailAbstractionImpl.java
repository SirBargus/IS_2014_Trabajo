package com.android.demo.mail;

import android.app.Activity;
import android.os.Build;

/** Implementa la interfaz de la abstracci�n utilizando (delegando a) una referencia a un objeto de tipo implementor  */
public class MailAbstractionImpl implements MailAbstraction {
	
	/** objeto delegado que facilita la implementaci�n del m�todo send */
	private MailImplementor implementor;
	
	/** Constructor de la clase. Inicaliza el objeto delegado seg�n el entorno de ejecuci�n de la aplicaci�n Android
	 * @param sourceActiviy actividad desde la cual se abrir� la actividad encargada de gestionar el correo
	 */
	public MailAbstractionImpl(Activity sourceActivity) {
		String brand = Build.BRAND;
        if(brand.compareTo("generic") == 0){
            implementor = new JavaMailImplementor(sourceActivity);
            android.util.Log.d("MailAbstractionImpl", "Ejecutándose en el emulador");
        } else{
            implementor = new BuiltInMailImplementor(sourceActivity);
            android.util.Log.d("MailAbstractionImpl", "Ejecutándose en un disositivo real");
        }
	}

	/** Env�a el correo con el asunto (subject) y cuerpo (body) que se reciben como par�metros a trav�s de un objeto delegado
     * @param subject asunto
     * @param body cuerpo del mensaje
     */
	public void send(String subject, String body) {
		implementor.send(subject, body);
	}
}