/**
 * 
 */
package servidor;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.Security;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;


/**
 * Esta clase implementa el servidor que atiende a los clientes. El servidor 
 * esta implemntado como un pool de threads. Cada vez que un cliente crea
 * una conexion al servidor, un thread se encarga de atenderlo el tiempo que
 * dure la sesion. 
 * Infraestructura Computacional Universidad de los Andes. 
 * Las tildes han sido eliminadas por cuestiones de compatibilidad.
 * 
 * @author Michael Andres Carrillo Pinzon 	-  201320.
 * @author José Miguel Suárez Lopera 		-  201510
 */
public class Servidor extends Thread {

	private static Estadistica estadistica ;
	
	/**
	 * Constante que especifica el tiempo máximo en milisegundos que se esperara 
	 * por la respuesta de un cliente en cada una de las partes de la comunicación
	 */
	private static final int TIME_OUT = 10000;

	/**
	 * Constante que especifica el numero de threads que se usan en el pool de conexiones.
	 */
	public static final int N_THREADS = 4;

	/**
	 * Puerto en el cual escucha el servidor.
	 */
	public static final int PUERTO = 8081;

	/**
	 * El socket que permite recibir requerimientos por parte de clientes.
	 */
	private static ServerSocket socket;
	
	private static Socket socketNormi;

	/**
	 * El semaforo que permite tomar turnos para atender las solicitudes.
	 */
	private Semaphore semaphore;

	/**
	 * El id del Thread
	 */
	private int id;

	/**
	 * Metodo main del servidor con seguridad que inicializa un 
	 * pool de threads determinado por la constante nThreads.
	 * @param args Los argumentos del metodo main (vacios para este ejemplo).
	 * @throws IOException Si el socket no pudo ser creado.
	 */
	public static void main(String[] args) throws IOException {

		Estadistica estadistica = new Estadistica();
		

		// Adiciona la libreria como un proveedor de seguridad.
		// Necesario para crear llaves.
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());		

		// Crea el socket que escucha en el puerto seleccionado.
		socket = new ServerSocket(PUERTO);

		// Crea un semaforo que da turnos para usar el socket.
		//Semaphore semaphore = new Semaphore(1);

		// Pool
		ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
		//Servidor [] threads = new Servidor[N_THREADS];
		int i = 1;
		System.out.println("El servidor esta listo para aceptar conexiones.");
		while (true) {
			Socket s = socket.accept();
			executorService.execute( new Servidor(i, s,estadistica));
			i++;
		}
		

		
	
		//executorService.shutdown();
		
		
		// Genera n threads que correran durante la sesion.
//		Servidor [] threads = new Servidor[N_THREADS];
//		for ( int i = 0 ; i < N_THREADS ; i++) {
//			threads[i] = new Servidor(i, semaphore);
//		}
		
	}

	/**
	 * Metodo que inicializa un thread y lo pone a correr.
	 * 
	 * @param socket
	 *            El socket por el cual llegan las conexiones.
	 * @param semaphore
	 *            Un semaforo que permite dar turnos para usar el socket.
	 * @throws InterruptedException
	 *             Si hubo un problema con el semaforo.
	 * @throws SocketException 
	 */
	public Servidor(int id, Socket sock, Estadistica esta) throws  SocketException {
		this.id = id;
		socketNormi = sock;
		estadistica = esta;
		

		//this.start();
	}

	/**
	 * Metodo que atiende a los usuarios.
	 */
	@Override
	public void run() {
		
			System.out.println("Cliente  " + id + " atendido");
			ProtocoloSinSeguridad.atenderCliente(socketNormi, estadistica);
		
	}



}
