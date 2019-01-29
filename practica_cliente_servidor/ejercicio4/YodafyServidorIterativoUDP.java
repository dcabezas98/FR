package ejercicio4;

import java.net.DatagramSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
public class YodafyServidorIterativoUDP {

	public static void main(String[] args) {
	
		// Puerto de escucha
		int port=8989;
                DatagramSocket serverSocket= null; 
		// array de bytes auxiliar para recibir o enviar datos.
		//byte []buffer=new byte[256];
		// Número de bytes leídos
		//int bytesLeidos=0;
		
		//try {                    
			// Mientras ... siempre!
			do {
                            
                                try{
                                   serverSocket= new DatagramSocket(port);
                                } catch(IOException e){
                                    System.out.println("Error: no se pudo atender en el puerto "+port);
                                }
				// Creamos un objeto de la clase ProcesadorYodafy, pasándole como 
				// argumento el nuevo socket, para que realice el procesamiento
				// Este esquema permite que se puedan usar hebras más fácilmente.
				ProcesadorYodafyUDP procesador = new ProcesadorYodafyUDP(serverSocket);
				procesador.procesa();
				
			} while (true);
			
		//} catch (IOException e) {
		//	System.err.println("Error al escuchar en el puerto "+port);
		//}

	}

}