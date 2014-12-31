package com.android.demo.mail;

import java.util.Date; 
import java.util.Properties; 
import javax.activation.CommandMap; 
import javax.activation.DataHandler; 
import javax.activation.DataSource; 
import javax.activation.FileDataSource; 
import javax.activation.MailcapCommandMap; 
import javax.mail.BodyPart; 
import javax.mail.Multipart; 
import javax.mail.PasswordAuthentication; 
import javax.mail.Session; 
import javax.mail.Transport; 
import javax.mail.internet.InternetAddress; 
import javax.mail.internet.MimeBodyPart; 
import javax.mail.internet.MimeMessage; 
import javax.mail.internet.MimeMultipart; 
 
/** 
    * Class that enables sending an e-mail by means of Java Mail library adapted to the Android enviornment
    * Implementation adapted from that available at Jon Simon's blog: http://www.jondev.net/articles/Sending_Emails_without_User_Intervention_%28no_Intents%29_in_Android
    */ 
public class JavaMail extends javax.mail.Authenticator { 

  /** email sent from */
  private String _from; 

  /** email sent to */
  private String[] _to; 
	
  /** username for the smtp server*/
  private String _user; 

  /** password corresponding to the user name in the smtp server */
  private String _pass; 
 
  /** smtp server */
  private String _host; 
 
  /** smtp port */
  private String _port; 

  /** socketfactory port */
  private String _sport; 
 
  /** subject of the email */
  private String _subject; 

  /** body of the email */
  private String _body; 
 
  /** smtp authentication */
  private boolean _auth; 
   
  /** debug mode */
  private boolean _debuggable; 
 
  private Multipart _multipart; 
 
  /** 
   * Detailed constuctor of the class
   * @param from email sent from
   * @param to email sent to
   * @param user username for the smtp server 
   * @param password password
   * @param host smtp server
   * @param port smtp port
   * @param sport socketfactory port
   * @param subject subject of the email
   * @param body body of the email
   */
  public JavaMail(String from, String[] to, String user, String password 
		  , String host, String port, String sport
		  , String subject, String body) {
    
	setFrom(from);
	setTo(to);
    _user = user; // username 
    _pass = password; // password 

    setHost(host); 
    setPort(port); 
    setSPort(sport);  
 
    setSubject(subject); 
    setBody(body); 
 
    _debuggable = false; // debug mode on or off - default off 
    _auth = true; // smtp authentication - default on 
 
    _multipart = new MimeMultipart(); 
 
    // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added. 
    MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
    CommandMap.setDefaultCommandMap(mc); 
  } 
 
  /** Simple constructor of the class (user and password are required)
   * @param user username at smtp
   * @param password password corresponding to the user name in the smtp server
   */
  public JavaMail(String user, String password) { 
    this(user,null,user,password
    		,"smtp.gmail.com","465","465"
    		,"",""); 
  } 
 
  /** sends the message according to the values of attributes */
  public boolean send() throws Exception { 
    Properties props = _setProperties(); 
 
    if(!_user.equals("") && !_pass.equals("") 
    		&& getTo().length > 0 && !getFrom().equals("") 
    		&& !getSubject().equals("") && !getBody().equals("")) { 
      Session session = Session.getInstance(props, this); 
 
      MimeMessage msg = new MimeMessage(session); 
 
      msg.setFrom(new InternetAddress(getFrom())); 
       
      InternetAddress[] addressTo = new InternetAddress[getTo().length]; 
      for (int i = 0; i < getTo().length; i++) { 
        addressTo[i] = new InternetAddress(getTo()[i]); 
      } 
        msg.setRecipients(MimeMessage.RecipientType.TO, addressTo); 
 
      msg.setSubject(getSubject()); 
      msg.setSentDate(new Date());

      // setup message body
      BodyPart messageBodyPart = new MimeBodyPart(); 
      messageBodyPart.setText(getBody()); 
      _multipart.addBodyPart(messageBodyPart);
 
      // Put parts in message 
      msg.setContent(_multipart);
 
      // send email 
      Transport.send(msg); 
 
      return true; 
    } else { 
      return false; 
    } 
  } 
 
  /** allows the inclusion of attachments */
  public void addAttachment(String filename) throws Exception { 
    BodyPart messageBodyPart = new MimeBodyPart(); 
    DataSource source = new FileDataSource(filename); 
    messageBodyPart.setDataHandler(new DataHandler(source)); 
    messageBodyPart.setFileName(filename); 
 
    _multipart.addBodyPart(messageBodyPart); 
  } 
 
  /** returns an object representing the password authentication */
  public PasswordAuthentication getPasswordAuthentication() { 
    return new PasswordAuthentication(_user, _pass); 
  } 
 
  /** establishes the properties of the message */
  private Properties _setProperties() { 
    Properties props = new Properties(); 
 
    props.put("mail.smtp.host", getHost()); 
 
    if(_debuggable) { 
      props.put("mail.debug", "true"); 
    } 
 
    if(_auth) { 
      props.put("mail.smtp.auth", "true"); 
    } 
 
    props.put("mail.smtp.port",getPort()); 
    props.put("mail.smtp.socketFactory.port", getSPort()); 
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
    props.put("mail.smtp.socketFactory.fallback", "false"); 
 
    return props; 
  } 
 
  public String getBody() { 
    return _body; 
  } 
 
  public void setBody(String _body) { 
    this._body = _body; 
  } 
 
  public String[] getTo() { 
    return _to; 
  } 
 
  public void setTo(String[] to) { 
    _to = to; 
  } 

  public String getFrom() { 
	    return _from; 
	  } 
	 
  public void setFrom(String from) { 
	_from = from; 
  } 
  public String getSubject() { 
	    return _subject; 
	  } 
	 
  public void setSubject(String subject) { 
	_subject = subject; 
  } 
  
  public String getHost() { 
	return _host; 
  } 
 
  public void setHost(String host) { 
	_host = host; 
  } 

  public String getPort() { 
	return _port; 
  } 
	 
  public void setPort(String port) { 
	_port = port; 
  } 
  public String getSPort() { 
	return _sport; 
  } 
  public void setSPort(String sport) { 
	_sport = sport; 
  } 
}