package pf.resource;

import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Rodrigo Almeida de Amorim
 * @version 0.1
 */

/**
 * Class that contains a unique instance of a Message Property.
 */
public class MessageProperties {

	/**
	 * Attribute that holds the message property file.
	 */
	private static Properties messageProperties = null;
	
	/**
	 * A Static Method that returns a instance of the message property file.
	 * @return - return the messages properties.
	 */
	public static Properties getInstance() {
		if(messageProperties == null) {
			synchronized (MessageProperties.class) {
				if (messageProperties == null) {
					try {
						FileInputStream file = new FileInputStream("C:\\Documents and Settings\\Administrador\\Desktop\\ÁREA DE TRABALHO\\ic\\OwlsDiscovery\\messages.properties");
						messageProperties = new Properties();
						messageProperties.load(file);
						file.close();
						return messageProperties;
					}catch(IOException e) {
                                            //efraim colocou a linha de baixo
                                            e.printStackTrace();
					}
				}
			}
		}
		return messageProperties;
	}
}
