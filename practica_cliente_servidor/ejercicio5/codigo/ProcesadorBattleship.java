package ejercicio5;
//
// (CC) jjramos, 2012
//
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Patricia Cordoba Hidalgo y David Cabezas Berrido
 */

public class ProcesadorBattleship extends Thread{
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private BufferedReader inReader;
	// stream de escritura (por aquí se envía los datos al cliente)
	private PrintWriter outPrinter;
	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;
        
        //Un 0 es agua, si es 1 es un barco
        int[][] tablero;
        int[][] tablero_visible;
        int barcos;
        int puntuacion;
        
        ArrayList<Integer> puntuaciones;
        
	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public ProcesadorBattleship(Socket socketServicio) {
		this.socketServicio=socketServicio;
		random=new Random();
                
                tablero = new int[6][6];
                tablero_visible = new int[6][6];
                
                puntuaciones = new ArrayList<>();
                
                barcos = 0;
                
                tablero[0][2] = 1; // Primer barco
                tablero[0][3] = 1;
                tablero[2][3] = 1; // Segundo barco
                tablero[3][3] = 1;
                tablero[5][0] = 1; // Tercer barco
                tablero[5][1] = 1;
	}
	
	
	// Aquí es donde se realiza el procesamiento realmente:
        @Override
	public void run(){
		
		// Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
		String datosRecibidos=new String();
		
		// Array de bytes para enviar la respuesta. Podemos reservar memoria cuando vayamos a enviar:
		String datosEnviar;
		
		int estado;
                boolean fin = false;
                String coordenadas;
                String opcion;
                
		try {
                        // Obtiene los flujos de escritura/lectura
                        inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
                        outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
                        int coord;
                                
                        while(!fin){
                            
                            // Mostrar menú
                            datosEnviar = menu();
                            outPrinter.flush();
                            outPrinter.println(datosEnviar);
                            outPrinter.flush();
                            
                            // Recibir respuesta
                            datosRecibidos = inReader.readLine();
                            opcion = new String(datosRecibidos);
                            
                            switch(opcion){
                                
                                case "1":          // Jugar
                                    initGame();
                                    
                                    while(barcos > 0){

                                        //Envío el tablero al jugador
                                        datosEnviar = printTablero();
                                        outPrinter.println(datosEnviar);
                                        outPrinter.println("¿A qué casilla quieres disparar? (xy donde x es la fila e y la columna). -1 para rendirse.");
                                        outPrinter.flush();

                                        //Recibo las coordenadas del disparo
                                        datosRecibidos = inReader.readLine();
                                        coordenadas = new String(datosRecibidos);

                                        //Recibo el disparo
                                        coord = Integer.parseInt(coordenadas);
                                        
                                        if(coord == -1){ // Rendirse
                                            estado = 99;
                                            barcos = 0;
                                            puntuacion = 0;
                                        } else{
                                            estado = receiveShot(coord/10,coord%10);
                                        }

                                        //Mando el estado al jugador
                                        datosEnviar = message(estado);
                                        outPrinter.println(datosEnviar);
                                        outPrinter.flush();
                                    }
                                    
                                    // Tablero totalmente visible
                                    datosEnviar = printTableroFin();
                                    outPrinter.println(datosEnviar);
                                    outPrinter.flush();
                                    
                                    // Guardamos puntuación
                                    puntuaciones.add(puntuacion);
                                break;
                                
                                case "2":       // Puntuaciones
                                    datosEnviar = "Puntuaciones: \n" + printPuntuaciones();
                                    outPrinter.println(datosEnviar);
                                    outPrinter.flush();
                                break;
                                
                                case "3":       // Salir
                                    datosEnviar = "¡Adiós! ¡Hasta la próxima!";
                                    outPrinter.println(datosEnviar);
                                    outPrinter.flush();
                                                                        
                                    fin = true;
                                break; 
                            }        
                        }
                        
                        socketServicio.close();
			
		} catch (IOException e) {
			System.err.println("Error al obtener los flujo de entrada/salida.");
		}

	}
        
        private String printTableroFin() {
            
                String s = new String();
                
                for(int i = 0; i < 5; i++){
                    for(int j = 0; j < 6; j++){
                        if(tablero[i][j]==1)
                            s+="X "; // Barco
                        else s+="0 "; // Agua
                    }
                    s+="\n";
                }
                
                for(int j = 0; j < 6; j++){
                    if(tablero[5][j]==1)
                        s+="X "; // Barco
                    else s+="0 "; // Agua  
                }    
                
                return s;            
        }
                
        /* Devuelve el tablero pasado a String para poder visualizarlo */
	private String printTablero() {
                String s = new String();
                
                for(int i = 0; i < 5; i++){
                    for(int j = 0; j < 6; j++){
                        if(tablero_visible[i][j]==1){ // Donde ya se ha disparado
                            if(tablero[i][j]==1)
                                s+="X "; // Acierto (barco tocado / hundido) 
                            else s+="0 "; // Agua  
                        }else
                            s+="- "; // Desconocido
                    }
                    s+="\n";
                }
                
                for(int j = 0; j < 6; j++){
                    if(tablero_visible[5][j]==1){ // Donde ya se ha disparado
                        if(tablero[5][j]==1)
                            s+="X "; // Acierto (barco tocado / hundido) 
                        else s+="0 "; // Agua  
                    }else
                        s+="- "; // Desconocido
                }    
                
                return s;
	}
        
        private int receiveShot(int i, int j){
            
                puntuacion--;
            
                if(i > 5 || j > 5 || i < 0 || j < 0)    //Si disparo fuera del tablero
                    return 92; //(nm): n: fallo m: fuera
                
                if(tablero_visible[i][j]==1) //Si ya has disparado en la casilla
                    return 91;  //(nm): n: fallo m: repetido
                                
                tablero_visible[i][j]=1; //Hace la casilla visible
                
                if(tablero[i][j]==1){ //Si hay un barco
                    puntuacion++;
                    if(i == 0 && (j == 2 || j == 3))    //Tocado el barco 1
                        return 11; //(nm): n: tocado m: barco 1
                    if((i == 2 || i == 3) && j == 3)    //Tocado el barco 2
                        return 12; //(nm): n: tocado m: barco 2
                    if(i == 5 && (j == 0 || j == 1))    //Tocado el barco 3
                        return 13; //(nm): n: tocado m: barco 3
                }
                
                return 90; //(nm): n: fallo m: agua       
        }
     
        private String message(int estado){
            
            String datosEnviar = null;
            
            if(estado == 90)
                datosEnviar = "90#¡Agua!"; 
            else if(estado == 91)
                datosEnviar = "91#Ya has disparado a esa casilla, prueba con otra.";                         
            else if(estado == 92)
                datosEnviar = "92#¡Te has salido del tablero!";
            else if(estado == 99)
                datosEnviar = "99#¡Te has rendido! Más suerte la próxima.";
            // Casos con barco tocado
            else if(estado == 11){ // Barco 1 tocado
                if(tablero_visible[0][2] == 1 && tablero_visible[0][3] == 1){ // Hundido
                    datosEnviar = "21#¡Has hundido un barco!";
                    barcos--;
                    if(barcos == 0)
                        datosEnviar = "33#!Has ganado!";
                }else
                    datosEnviar = "11#¡Has tocado un barco!";
            } else if(estado == 12){ // Barco 2 tocado
                if(tablero_visible[2][3] == 1 && tablero_visible[3][3] == 1){ // Hundido
                    datosEnviar = "22#¡Has hundido un barco!";
                    barcos--;
                    if(barcos == 0)
                        datosEnviar = "33#!Has ganado!";
                }else
                    datosEnviar = "12#¡Has tocado un barco!";
            } else if(estado == 13){ // Barco 4 tocado
                if(tablero_visible[5][0] == 1 && tablero_visible[5][1] == 1){ // Hundido
                    datosEnviar = "23#¡Has hundido un barco!";
                    barcos--;
                    if(barcos == 0)
                        datosEnviar = "33#!Has ganado!";
                }else
                    datosEnviar = "13#¡Has tocado un barco!";
            }
            
            return datosEnviar;
        }
        
        private String menu(){
            return "BATTLESHIP!\n1: Jugar, 2: Puntuaciones, 3: Salir";
        }
        
        private void initGame(){
            
            barcos = 3;
            
            puntuacion = 100;
            
            for(int i = 0; i < 6; i++)
                for(int j = 0; j < 6; j++)
                    tablero_visible[i][j]=0;
        }
        
        private String printPuntuaciones(){
            
            String s = "";
            
            Collections.sort(puntuaciones);
                                    
            for(int i = puntuaciones.size()-1; i > 0; i--)
                s += Integer.toString(puntuaciones.get(i)) + ", ";
            
            if(!puntuaciones.isEmpty())
                s += Integer.toString(puntuaciones.get(0));
            
            return s;
        }
}