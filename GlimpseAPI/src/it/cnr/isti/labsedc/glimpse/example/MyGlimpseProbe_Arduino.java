package it.cnr.isti.labsedc.glimpse.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.naming.NamingException;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEventArduino;
import it.cnr.isti.labsedc.glimpse.event.GlimpseBaseEvent;
import it.cnr.isti.labsedc.glimpse.probe.GlimpseAbstractProbe;
import it.cnr.isti.labsedc.glimpse.utils.Manager;

public class MyGlimpseProbe_Arduino extends GlimpseAbstractProbe {

	//example of glimpse probe able to read data
	//from an arduino serial port (ttyACM3)
	//the structure of the message sent from arduino is
	//tempFLOATVALUE-humFLOATVALUE
	//where FLOATVALUE is the value read from an humidity and temperature sensor
	//and its format is XX.XX
	//example: temp25.00-hum-40.00
	
	
    static InputStream inputStream;
    static OutputStream outputStream;
    float temperature = 0;
    float humidity = 0;
    
	public MyGlimpseProbe_Arduino(Properties settings) {
		super(settings);
		// TODO Auto-generated constructor stub
	}


	private void readGenerateAndSend() {

        CommPortIdentifier portId;
        SerialPort port;
        BufferedReader input = null;
        String result = "";
        
        GlimpseBaseEventArduino<Float> temperatureMessage;        
        GlimpseBaseEventArduino<Float> humidityMessage;        
        
		try {
            portId = CommPortIdentifier.getPortIdentifier("/dev/ttyACM3");
            port = (SerialPort)portId.open("serial talk", 4000);
            
            inputStream = port.getInputStream();
            outputStream = port.getOutputStream();
            
            input = new BufferedReader(new InputStreamReader(inputStream));
            
            port.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
        } catch (PortInUseException | NoSuchPortException | IOException | UnsupportedCommOperationException ex) {
            Logger.getLogger(MyGlimpseProbe_Arduino.class.getName()).log(Level.SEVERE, null, ex);
        }        
        while(true){
            try {            	
                while(input.ready()) {    
                	result = input.readLine();
                }
                
                if (result.trim().length()>0 && !result.startsWith("tempnan")) {
                	temperature = Float.parseFloat(result.substring(4,8));
                	humidity = Float.parseFloat(result.substring(13,result.length()));

                	temperatureMessage = new GlimpseBaseEventArduino<Float>(
                			temperature,
              				"TemperatureSensor1",
              				System.currentTimeMillis(),
              				"Temperature sensor value",false,"TemperatureSensor");
                	
              		this.sendEventMessage(temperatureMessage, false);
                
              		System.out.println("Message Sent: " + temperatureMessage.getEventName() + "->" + temperatureMessage.getEventData());
              		
                	humidityMessage = new GlimpseBaseEventArduino<Float>(
                			humidity,
              				"HumiditySensor1",
              				System.currentTimeMillis(),
              				"Humidity sensor value",false,"HumiditySensor");
                	
              		this.sendEventMessage(humidityMessage, false);
              		
              		System.out.println("Message Sent: " + humidityMessage.getEventName() + "->" + humidityMessage.getEventData());
                }    
                Thread.sleep(20000);
            } catch (IOException | InterruptedException | JMSException | NamingException ex) {
                Logger.getLogger(MyGlimpseProbe_Arduino.class.getName()).log(Level.SEVERE, null, ex);
			}
        }
	}
	

	public static void main(String[] args) {
		MyGlimpseProbe_Arduino arduinoProbe;
		arduinoProbe = new MyGlimpseProbe_Arduino(
                Manager.createProbeSettingsPropertiesObject(
                        "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                        "tcp://atlantis.isti.cnr.it:61616",
                        "system","manager","TopicCF","jms.probeTopic",
                        false,"probeName","probeTopic"));
		arduinoProbe.readGenerateAndSend();
	}


	@Override
	public void sendMessage(GlimpseBaseEvent<?> event, boolean debug) {
		// TODO Auto-generated method stub
		
	}
	

}
