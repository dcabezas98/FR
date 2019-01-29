package ejercicio5;
//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Patricia Cordoba Hidalgo y David Cabezas Berrido
 */

public class BattleshipCliente {

	public static void main(String[] args) {
	
		String buferRecepcion=new String();
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión TCP
		Socket socketServicio=null;
                
                String coords, opcion;
                
                Scanner s = new Scanner(System.in);
                
                boolean fin = false;
                
                String estado;
		
		try {
			// Creamos un socket que se conecte a "host" y "port":
			//////////////////////////////////////////////////////
                        try {
                            socketServicio=new Socket (host,port);
                        } catch (UnknownHostException e) {
                            System.err.println("Error: equipo desconocido");
                        } catch (IOException e) {
                            System.err.println("Error: no se pudo establecer la conexión");
                        }
			//////////////////////////////////////////////////////			
			
                        BufferedReader inReader;
			PrintWriter outPrinter;
			
                        inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
                        outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
                            
                        while(!fin){              
                            
                            //System.out.println("#######"+buferRecepcion+"########");
                            //System.out.flush();
                            // Leemos menú y lo mostramos en pantalla
                            for( int i=0; i<2; i++){
                                buferRecepcion = inReader.readLine();
                                System.out.println(buferRecepcion);
                            }
                            
                            // Leemos opción y la enviamos
                            opcion = s.nextLine();
                            outPrinter.println(opcion);
                            
                            switch(opcion){
                             
                                case "1":       // Jugar
                                    
                                    estado = "";
                                    while(!estado.equals("33") && !estado.equals("99")){
                                        
                                        // Leemos el tablero y lo sacamos por pantalla
                                        for( int i=0; i<6; i++){
                                            buferRecepcion = inReader.readLine();
                                            System.out.println(buferRecepcion);
                                        }
                                       
                                        // Leemos y mostramos instrucciones
                                        buferRecepcion = inReader.readLine();
                                        System.out.println(buferRecepcion);
                                        
                                        // Leemos coordenadas del teclado y las enviamos
                                        coords = s.nextLine();
                                        outPrinter.println(coords);
                                        outPrinter.flush();
                                        
                                        // Leemos la respuesta del servidor y la mostramos
                                        buferRecepcion = inReader.readLine();
                                        estado = buferRecepcion.substring(0,2);
                                        System.out.println(buferRecepcion.substring(3));
                                    }
                                    
                                    // Leemos el tablero totalmente visible y lo sacamos por pantalla
                                    for( int i=0; i<6; i++){
                                        buferRecepcion = inReader.readLine();
                                        System.out.println(buferRecepcion);
                                    }
                                                                      
                                break;
                                
                                case "2": // Puntuaciones
                                    
                                    // Leemos y mostramos puntuaciones
                                    for( int i=0; i<2; i++){
                                        buferRecepcion = inReader.readLine();
                                        System.out.println(buferRecepcion);
                                    }
                                    
                                break;
                                
                                case "3": // Salir
                                    
                                    // Leemos mensaje de despedida
                                    buferRecepcion = inReader.readLine();
                                    System.out.println(buferRecepcion);
                                    System.out.flush();
                                    fin = true;
                                
                                break;
                            }
                            
                            
                        }
			// Una vez terminado el servicio, cerramos el socket (automáticamente se cierran
			// el inpuStream  y el outputStream)
			//////////////////////////////////////////////////////
			socketServicio.close();
			//////////////////////////////////////////////////////
			
			// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}

