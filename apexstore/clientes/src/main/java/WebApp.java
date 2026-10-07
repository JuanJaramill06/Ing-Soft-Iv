import apexstore.*;
import java.util.*;

public class WebApp {

    public void ejecutarCompra(ServicioCheckoutPrx checkout) {
        realizarCompra(checkout, "STRIPE", 150.00, "USD",
                      Map.of("token", "tok_visa_4242"));

        realizarCompra(checkout, "PSE", 450000.00, "COP",
                      Map.of("banco", "001"));

        realizarCompra(checkout, "CRIPTO", 0.025, "BTC",
                      Map.of("wallet", "1A1z7agoat2CYWFSGT5QJkmjVxHCXxvCXn"));
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
