import apexstore.*;
import java.util.*;

public class MobileApp {

    public void ejecutarCompra(ServicioCheckoutPrx checkout) {
        realizarCompra(checkout, "STRIPE", 89.99, "USD",
                      Map.of("token", "tok_mastercard"));

        realizarCompra(checkout, "PSE", 250000.00, "COP",
                      Map.of("banco", "007"));

        realizarCompra(checkout, "CRIPTO", 0.5, "ETH",
                      Map.of("wallet", "0x742d35Cc6634C0532925a3b844Bc2e7595f5"));
    }

    private void realizarCompra(ServicioCheckoutPrx checkout, String medio,
                               double monto, String moneda, Map<String, String> datos) {
        try {
            SolicitudCompra solicitud = new SolicitudCompra();
            solicitud.claveIdempotencia = UUID.randomUUID().toString();
            solicitud.monto = monto;
            solicitud.moneda = moneda;
            solicitud.medioPago = medio;
            solicitud.datos = datos;

            String idTransaccion = checkout.gestionarComprasHttp(solicitud);

            Thread.sleep(2500);
            checkout.solicitarEstadoPago(idTransaccion);

        } catch (ErrorPago e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
