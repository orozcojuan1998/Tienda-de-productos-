package Cliente;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import Coordinador.CoordinadorInterface;
import Coordinador.TransaccionCoordinador;
import Cuenta.Cuenta;
import Cuenta.CuentaRMII;
import Producto.Producto;
import Producto.ProductoRMII;

public class Cliente extends UnicastRemoteObject{

	private ArrayList<Cuenta> cuentas = new ArrayList<Cuenta>(); 
	private ArrayList<Producto> productos = new ArrayList<Producto>(); 
	private ArrayList<ProductoCarrito> carrito = new ArrayList<ProductoCarrito>();
	private CoordinadorInterface coordinador;
	private int portCliente =3003;
	private int portCoordinador = 3000;
	private static int portCuentas = 3001;
	private static int portProductos = 3002;
	private static CuentaRMII j;
	private static ProductoRMII i;

	private static String URIProductos= "rmi://"+"10.192.101.15:3002"+"/Productos";
	private static String URICuentas = "rmi://"+"10.192.101.31:3001"+"/Cuentas";

	protected Cliente() throws Exception {
		super();

		iniciarSesion();

	}
	private void iniciarSesion() throws Exception {
		// TODO Auto-generated method stub
		String value = "0",tarjeta,contrasena,usuario, tarj, saldo;
		while(!value.equals("3")) {
			value =  JOptionPane.showInputDialog("Seleccione una opción: \n" + "1.  Ingresar con cuenta existente \n" + "2. Ingresar por primera vez \n" + "3. Salir");

			switch(value) {
			case "1":{
				tarjeta = JOptionPane.showInputDialog("Escribe tu tarjeta");
				contrasena = JOptionPane.showInputDialog("Escribe tu contraseña");
				if(tarjeta.equals("0000")&&contrasena.equals("0000")) {//ADMIN

					String opcion = JOptionPane.showInputDialog("Seleccione una opción: \n" + "1. Agregar saldo \n" + "2. Agregar producto \n" + "3. Salir");
					switch(opcion) {
					case "1":{						
						tarj = JOptionPane.showInputDialog("Ingrese la tarjeta que va a recargar");
						saldo = JOptionPane.showInputDialog("Ingrese el saldo que va a recargar");
						recargarTarjeta(tarj,saldo);
						JOptionPane.showMessageDialog(null, "Se ha recargado la tarjeta correctamente");
						break;
					}case "2":{
						String nombre,cantidad,precio="0";
						nombre = JOptionPane.showInputDialog("Ingrese el nombre del producto");
						cantidad = JOptionPane.showInputDialog("Ingrese la cantidad ");
						Producto p = null;
						while(true) {
							try {
								p = i.getProductoNombre(nombre);
								break;
							} catch (java.rmi.ConnectException e) {
								reconectarServidorProductos();
							}
						}
						
						if(p == null){
							precio = JOptionPane.showInputDialog("Ingrese el precio del producto");
							JOptionPane.showMessageDialog(null, "Se ha agregado el producto correctamente");
						}
						JOptionPane.showMessageDialog(null, "Se ha agregado el producto correctamente");
						while(true) {
							try {
								productos = i.getProductos();
								break;
							} catch (java.rmi.ConnectException e) {
								reconectarServidorProductos();
							}
						}
						agregarProducto(nombre,cantidad,precio);
						break;
					}
					case "3":{
						break;
					}
					}
				}
				else if(autenticarUsuario(contrasena, tarjeta)){
					Cuenta cuenta = new Cuenta();
					while(true) {
						try {
							cuenta=j.getCuenta(tarjeta);
							break;
						} catch (java.rmi.ConnectException e) {
							reconectarServidorCuentas();
						}
					}
					
					JOptionPane.showMessageDialog(null, "Ingresó "+cuenta.getUsuario()+" con saldo: "+cuenta.getSaldo());
					carrito = new ArrayList<>();
					while(true) {
						try {
							productos = i.getProductos();
							break;
						} catch (java.rmi.ConnectException e) {
							reconectarServidorProductos();
						}
					}
					System.out.println("CATÁLOGO DE PRODUCTOS");
					System.out.println("Numero     Item        Precio        Disponibles");
					int k=0;
					for (Producto producto : productos) {
						System.out.println(k+"  "+producto.toString());
						k++;
					}

					Boolean nextItem = true;
					while(nextItem){

						String itemS = JOptionPane.showInputDialog("Ingrese el número del producto para agregarlo al carrito de compras...");
						int item = Integer.parseInt(itemS);
						if(item<productos.size()){
							if(!(productos.get(item).getCantidadDisponible()==0)){
								boolean addItem = true;
								while(addItem){

									String numS = JOptionPane.showInputDialog("Ingrese la cantidad");
									int num = Integer.parseInt(numS);

									if(num <= productos.get(item).getCantidadDisponible()){
										addItem = false;
										carrito.add(new ProductoCarrito(productos.get(item), num));
										productos.get(item).setCantidadDisponible(productos.get(item).getCantidadDisponible()-num);
										String next = JOptionPane.showInputDialog(null, "¿Desea agregar más productos? 1.Sí  2. No" ,"Producto agreado al carrito", JOptionPane.QUESTION_MESSAGE);

										if(next.equals("2")){
											nextItem = false;
										}
									}else{
										JOptionPane.showMessageDialog(null, "Error", "Ingrese una cantidad menor a " + productos.get(item).getCantidadDisponible(), JOptionPane.WARNING_MESSAGE);

									}


								}
							}else{
								JOptionPane.showMessageDialog(null, "Producto agotado", "Producto agotado", JOptionPane.WARNING_MESSAGE);
							}
						}
						else{
							JOptionPane.showMessageDialog(null, "El número del producto no existe", "Producto inexistente", JOptionPane.WARNING_MESSAGE);
						}
					}
					transaccionDeCompra(tarjeta);
				}else {
					JOptionPane.showMessageDialog(null, "Error", "Información invalida", JOptionPane.WARNING_MESSAGE);

				}
				break;
			}
			case "2":{
				tarjeta = JOptionPane.showInputDialog("Escribe tu tarjeta");
				while(true) {
					try {
						if(j.verificarRegistro(tarjeta)) {
							String contra = JOptionPane.showInputDialog("Escribe tu contraseña");
							while(true) {
								try {
									j.setContrasena(tarjeta,contra);
									break;
								} catch (java.rmi.ConnectException e) {
									reconectarServidorCuentas();
								}
							}
							JOptionPane.showMessageDialog(null, "Se ha registrado correctamente");
						}else {
							JOptionPane.showMessageDialog(null, "Error", "Información invalida", JOptionPane.ERROR_MESSAGE);
						}
						break;
					} catch (java.rmi.ConnectException e) {
						reconectarServidorCuentas();
					}
				}
				
				break;
			}
			}
		}

	}



	private boolean autenticarUsuario(String contrasena, String tarjeta) throws Exception {
		
		boolean resultado = false;
		while(true) {
			try {
				resultado = j.autenticarUsuario(contrasena,tarjeta);
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorCuentas();
			}
		}
		return resultado;
	}
	private static void reconectarServidorProductos() throws Exception  {
		int maxTries = 20;
		int count = 0;
		while(true){
			try {
				i = (ProductoRMII) Naming.lookup(URIProductos);
				break;
			} catch (MalformedURLException | RemoteException | NotBoundException e) {
				Thread.sleep(1000);
				if (++count == maxTries) throw e;
			}
		}
	}
	private static void reconectarServidorCuentas() throws Exception  {
		int maxTries = 20;
		int count = 0;
		while(true){
			try {
				j = (CuentaRMII) Naming.lookup(URICuentas);
				break;
			} catch (MalformedURLException | RemoteException | NotBoundException e) {
				Thread.sleep(1000);
				if (++count == maxTries) throw e;
			}
		}
	}
	
	private void recargarTarjeta(String tarj, String saldo) throws Exception {
		Transaccion tvRecarga = null;
		while(true) {
			try {
				tvRecarga = j.solicitarTransaccion();
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorCuentas();
			}
		}
		while(true) {
			try {
				Cuenta cuenta = j.getCuenta(tarj);
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorCuentas();
			}
		}
		
		tvRecarga.adicionarObjetoEscritura(j.getCuenta(tarj));
		tvRecarga.adicionarObjetoLectura(j.getCuenta(tarj));
		while(true) {
			try {
				tvRecarga = j.iniciarTransaccion(tvRecarga);
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorCuentas();
			}
		}
		
		if(tvRecarga.getEstado()==2) {
			while(true) {
				try {
					j.recargarTarjeta(tarj,saldo);
					break;
				} catch (java.rmi.ConnectException e) {
					reconectarServidorCuentas();
				}
			}
			
		}else {
			JOptionPane.showMessageDialog(null, "Error", "Transacción abortada", JOptionPane.ERROR_MESSAGE);
		}
		while(true) {
			try {
				j.finalizarTransaccion(tvRecarga);
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorCuentas();
			}
		}
		
	}
	private void agregarProducto(String nombre, String cantidadD, String precio) throws Exception {
		Transaccion tvAgregar = null;
		while(true) {
			try {
				tvAgregar = i.solicitarTransaccion();
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorProductos();
			}
		}
		this.productos=i.getProductos();
		Producto p = i.getProductoNombre(nombre);
		if(p==null){
			tvAgregar.adicionarObjetoLectura(productos.get(productos.size()-1));
		}
		else{
			tvAgregar.adicionarObjetoEscritura(p);
			tvAgregar.adicionarObjetoLectura(p);
		}
		while(true) {
			try {
				tvAgregar = i.iniciarTransaccion(tvAgregar);
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorProductos();
			}
		}
		if(tvAgregar.getEstado()==2) {
			int id = productos.size();
			if(p==null){
				p = new Producto(id, nombre, Integer.parseInt(cantidadD), Float.parseFloat(precio));
				while(true) {
					try {
						i.agregarProducto(p);
						break;
					} catch (java.rmi.ConnectException e) {
						reconectarServidorProductos();
					}
				}
			}else{
				while(true) {
					try {
						i.aumentarCantidadProducto(p.getID(), Integer.parseInt(cantidadD));
						break;
					} catch (java.rmi.ConnectException e) {
						reconectarServidorProductos();
					}
				}
			}

		}else {
			JOptionPane.showMessageDialog(null, "Error", "Transacción abortada", JOptionPane.ERROR_MESSAGE);
		}
		while(true) {
			try {
				i.finalizarTransaccion(tvAgregar);
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorProductos();
			}
		}
	}
	private void transaccionDeCompra(String tarjeta) throws Exception {


		float total = 0;
		

		Transaccion tvCuenta = null;
		while(true) {
			try {
				tvCuenta = j.solicitarTransaccion();
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorCuentas();
			}
		}
		
		for (ProductoCarrito productoCarrito : carrito) {
			total+= productoCarrito.getP().getPrecio()*productoCarrito.getCantidad();
		}
		tvCuenta.adicionarObjetoEscritura(j.getCuenta(tarjeta));
		tvCuenta.adicionarObjetoLectura(j.getCuenta(tarjeta));
		
		while(true) {
			try {
				tvCuenta = j.iniciarTransaccion(tvCuenta);
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorCuentas();
			}
		}
		Cuenta cuenta  = null;
		while(true) {
			try {
				cuenta = j.getCuenta(tarjeta);
				break;
			} catch (java.rmi.ConnectException e) {
				reconectarServidorCuentas();
			}
		}

		if(cuenta.getSaldo()>=total&&tvCuenta.getEstado()!=3){
			Transaccion tvProductosALeer = null;
			while(true) {
				try {
					tvProductosALeer = i.solicitarTransaccion();
					break;
				} catch (java.rmi.ConnectException e) {
					reconectarServidorProductos();
				}
			}
			
			for (ProductoCarrito productoCarrito : carrito) {
				tvProductosALeer.adicionarObjetoLectura(productoCarrito.getP());
			}
			while(true) {
				try {
					tvProductosALeer = i.iniciarTransaccion(tvProductosALeer);
					break;
				} catch (java.rmi.ConnectException e) {
					reconectarServidorProductos();
				}
			}
			
			

			if(tvProductosALeer.getEstado()==2){
				ArrayList<ProductoCarrito> productosComprados = new ArrayList<ProductoCarrito>();
				for (ProductoCarrito productoCarrito : carrito) {
					while(true) {
						try {
							if(i.getProducto(productoCarrito.getP().getID()).getCantidadDisponible()>=productoCarrito.getCantidad()){
								productosComprados.add(productoCarrito);
							}else{
								JOptionPane.showMessageDialog(null, "Error", "El producto "+productoCarrito.getP().getNombre()+" se ha agotado", JOptionPane.ERROR_MESSAGE);

							}
							break;
						} catch (java.rmi.ConnectException e) {
							reconectarServidorProductos();
						}
					}
					
				}
				while(true) {
					try {
						i.finalizarTransaccion(tvProductosALeer);
						break;
					} catch (java.rmi.ConnectException e) {
						reconectarServidorProductos();
					}
				}
				Transaccion tvProductosAComprar = null;
				while(true) {
					try {
						tvProductosAComprar = i.solicitarTransaccion();
						break;
					} catch (java.rmi.ConnectException e) {
						reconectarServidorProductos();
					}
				}
				
				for (ProductoCarrito productoCarrito : productosComprados) {
					tvProductosAComprar.adicionarObjetoEscritura(productoCarrito.getP());
				}
				while(true) {
					try {
						tvProductosAComprar = i.iniciarTransaccion(tvProductosAComprar);
						break;
					} catch (java.rmi.ConnectException e) {
						reconectarServidorProductos();
					}
				}
				
				if(tvProductosAComprar.getEstado()==2){
					total = 0;
					for (ProductoCarrito productoCarrito : productosComprados) {
						while(true) {
							try {
								i.disminuirCantidadDisponible(productoCarrito.getP().getID(), productoCarrito.getCantidad());
								break;
							} catch (java.rmi.ConnectException e) {
								reconectarServidorProductos();
							}
						}
						
						total+=productoCarrito.getP().getPrecio()*productoCarrito.getCantidad();
					}
					try {
						while(true) {
							try {
								i.finalizarTransaccion(tvProductosAComprar);
								break;
							} catch (java.rmi.ConnectException e) {
								reconectarServidorProductos();
							}
						}
						
						JOptionPane.showMessageDialog(null, "Compra realizada con éxito, su nuevo saldo: "+ (cuenta.getSaldo()-total));
						
						while(true) {
							try {
								j.setSaldo(tarjeta, cuenta.getSaldo()-total);
								break;
							} catch (java.rmi.ConnectException e) {
								reconectarServidorCuentas();
							}
						}
						while(true) {
							try {
								j.finalizarTransaccion(tvCuenta);
								break;
							} catch (java.rmi.ConnectException e) {
								reconectarServidorCuentas();
							}
						}
						
					}
					catch(java.rmi.ConnectException e) {

					}


				}else{
					JOptionPane.showMessageDialog(null, "Error", "Transacción abortada (COMPRA "+tvProductosAComprar.getEstado() +") ... intente de nuevo ", JOptionPane.ERROR_MESSAGE);
					while(true) {
						try {
							i.finalizarTransaccion(tvProductosALeer);
							break;
						} catch (java.rmi.ConnectException e) {
							reconectarServidorProductos();
						}
					}
					
					
					while(true) {
						try {
							j.finalizarTransaccion(tvCuenta);
							break;
						} catch (java.rmi.ConnectException e) {
							reconectarServidorCuentas();
						}
					}
				}


			}else{
				JOptionPane.showMessageDialog(null, "Error", "Transacción abortada (LECTURA-COMPRA) ... intente de nuevo", JOptionPane.ERROR_MESSAGE);
				while(true) {
					try {
						i.finalizarTransaccion(tvProductosALeer);
						break;
					} catch (java.rmi.ConnectException e) {
						reconectarServidorProductos();
					}
				}
				while(true) {
					try {
						j.finalizarTransaccion(tvCuenta);
						break;
					} catch (java.rmi.ConnectException e) {
						reconectarServidorCuentas();
					}
				}
				
				
				

			}
		}else if(tvCuenta.getEstado()==3){
			JOptionPane.showMessageDialog(null, "Error", "Transacción abortada (CUENTA)... intente de nuevo", JOptionPane.ERROR_MESSAGE);
			while(true) {
				try {
					j.finalizarTransaccion(tvCuenta);
					break;
				} catch (java.rmi.ConnectException e) {
					reconectarServidorCuentas();
				}
			}
				
			

			
		}else{
			JOptionPane.showMessageDialog(null, "Error", "Saldo insuficienta", JOptionPane.ERROR_MESSAGE);
			while(true) {
				try {
					j.finalizarTransaccion(tvCuenta);
					break;
				} catch (java.rmi.ConnectException e) {
					reconectarServidorCuentas();
				}
			}

		}



	}
	public static void main(String[] args) throws Exception {


		System.setProperty("java.security.policy", "./cliente.policy");



		//coordinador = (CoordinadorInterface) registry.lookup("//127.0.0.1/Coordinador");

		j = null;
		try {
			//Registry registry = LocateRegistry.getRegistry(portCuentas);
			try {
				j = (CuentaRMII) Naming.lookup(URICuentas);
			}
			catch (java.rmi.ConnectException e) {

			}
			//j = (CuentaRMII) registry.lookup("10.192.101.31/Cuentas");
		}  catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		i = null;
		try {
			//Registry registry = LocateRegistry.getRegistry(portProductos);
			try {
				i = (ProductoRMII) Naming.lookup(URIProductos);
			}
			catch (java.rmi.ConnectException e) {

			}
			//i = (ProductoRMII) registry.lookup("10.192.101.31/Productos");
		}  catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Cliente c = new Cliente();
	}
	public static void imprimirProductos(ArrayList<Producto> productos) {
		int i=1;
		for (Producto producto : productos) {
			System.out.println(i+"  "+producto.toString());
			i++;
		}
	}



}
