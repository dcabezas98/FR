package ejercicio4;
//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class YodafyClienteUDP {

	public static void main(String[] args) {
		
		byte []buferEnvio;
		byte []buferRecepcion=new byte[256];
		int bytesLeidos=0;
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
                InetAddress direccion; //Inetsocketaddress con puerto y direccion
                DatagramPacket paquete;
                DatagramSocket socket;
		
		try {
			// Creamos un socket
			//////////////////////////////////////////////////////
                        socket=new DatagramSocket();
			//////////////////////////////////////////////////////			
                        direccion = InetAddress.getByName(host);
			// Si queremos enviar una cadena de caracteres por un OutputStream, hay que pasarla primero
			// a un array de bytes:
			buferEnvio="Al monte del volcan debes ir sin demora".getBytes();
			
                        paquete = new DatagramPacket(buferEnvio, buferEnvio.length, direccion, port);
			// Enviamos el array;
			//////////////////////////////////////////////////////
			socket.send(paquete);
			//////////////////////////////////////////////////////
			
			paquete = new DatagramPacket(buferRecepcion, buferRecepcion.length);
                        socket.receive(paquete);
                        buferRecepcion = paquete.getData();
                        bytesLeidos = paquete.getLength();
			//////////////////////////////////////////////////////
			
			// Mostremos la cadena de caracteres recibidos:
			System.out.println("Recibido: ");
                        System.out.println(new String(buferRecepcion,0,bytesLeidos));
//			for(int i=0;i<bytesLeidos;i++){
//				System.out.print((char)buferRecepcion[i]);
//			}
			
			// Una vez terminado el servicio, cerramos el socket (automáticamente se cierran
			// el inpuStream  y el outputStream)
			//////////////////////////////////////////////////////
			socket.close();
			//////////////////////////////////////////////////////
			
			// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}