import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;
import apexstore.*;
import java.util.*;

public class Main {
    static ServicioCheckoutPrx checkout;
    static Scanner scanner;

    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            String checkoutProxy = "ServicioCheckout:tcp -h localhost -p 10000";
            checkout = ServicioCheckoutPrx.uncheckedCast(
                communicator.stringToProxy(checkoutProxy));

            scanner = new Scanner(System.in);
            System.out.flush();

            boolean continuar = true;
            while (continuar) {
                mostrarMenu();
                System.out.flush();

                if (!scanner.hasNextInt()) {
                    scanner.next();
                    continue;
                }

                int opcion = scanner.nextInt();
                scanner.nextLine();

                if (opcion == 1) {
                    procesarCompra();
                } else if (opcion == 2) {
                    consultarEstado();
                } else if (opcion == 3) {
                    System.out.println("Saliendo...");
                    continuar = false;
                } else {
                    System.out.println("Opcion invalida");
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    static void mostrarMenu() {
        System.out.println("\n========== APEXSTORE ==========");
        System.out.println("1. Procesar compra");
        System.out.println("2. Consultar estado de transaccion");
        System.out.println("3. Salir");
        System.out.print("Selecciona opcion: ");
    }

    static void procesarCompra() {
        try {
            System.out.println("\n--- Nueva Compra ---");
            System.out.println("Medios de pago disponibles:");
            System.out.println("1. STRIPE (Tarjeta Credito)");
            System.out.println("2. PSE (Debito Bancario)");
            System.out.println("3. CRIPTO (Blockchain)");
            System.out.print("Selecciona medio de pago: ");
            System.out.flush();

            int medioPagoOp = scanner.nextInt();
            scanner.nextLine();
            String medioPago = "";
            String dato = "";

            switch (medioPagoOp) {
                case 1:
                    medioPago = "STRIPE";
                    dato = "token";
                    break;
                case 2:
                    medioPago = "PSE";
                    dato = "banco";
                    break;
                case 3:
                    medioPago = "CRIPTO";
                    dato = "wallet";
                    break;
                default:
                    System.out.println("Medio invalido");
                    return;
            }

            System.out.print("Ingresa el monto: ");
            System.out.flush();
            double monto = scanner.nextDouble();
            scanner.nextLine();

            System.out.print("Ingresa la moneda (USD/COP/BTC/ETH): ");
            System.out.flush();
            String moneda = scanner.nextLine();

            System.out.print("Ingresa " + dato + ": ");
            System.out.flush();
            String datoDato = scanner.nextLine();

            SolicitudCompra solicitud = new SolicitudCompra();
            solicitud.claveIdempotencia = UUID.randomUUID().toString();
            solicitud.monto = monto;
            solicitud.moneda = moneda;
            solicitud.medioPago = medioPago;
            solicitud.datos = new HashMap<>();
            solicitud.datos.put(dato, datoDato);

            String idTransaccion = checkout.gestionarComprasHttp(solicitud);
            System.out.println("\nCompra procesada");
            System.out.println("ID Transaccion: " + idTransaccion);

            System.out.print("Consultar estado ahora? (s/n): ");
            System.out.flush();
            String resp = scanner.nextLine();
            if (resp.equalsIgnoreCase("s")) {
                Thread.sleep(2000);
                EstadoPago estado = checkout.solicitarEstadoPago(idTransaccion);
                System.out.println("Estado: " + estado);
            }

        } catch (ErrorPago e) {
            System.out.println("Error: " + e.motivo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void consultarEstado() {
        try {
            System.out.print("\nIngresa ID de transaccion: ");
            System.out.flush();
            String id = scanner.nextLine();
            EstadoPago estado = checkout.solicitarEstadoPago(id);
            System.out.println("Estado: " + estado);
        } catch (Exception e) {
            System.out.println("Error al consultar estado");
        }
    }
}
