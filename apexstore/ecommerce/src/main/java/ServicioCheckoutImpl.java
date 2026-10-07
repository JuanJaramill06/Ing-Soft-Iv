import apexstore.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServicioCheckoutImpl implements apexstore.ServicioCheckout {
    private Map<String, EstadoPago> estadoTransacciones = new ConcurrentHashMap<>();
    private Map<String, SolicitudCompra> compras = new ConcurrentHashMap<>();
    private EstrategiaPagoPrx stripe;
    private EstrategiaPagoPrx pse;
    private EstrategiaPagoPrx cripto;

    public ServicioCheckoutImpl(EstrategiaPagoPrx stripe, EstrategiaPagoPrx pse, EstrategiaPagoPrx cripto) {
        this.stripe = stripe;
        this.pse = pse;
        this.cripto = cripto;
    }

    @Override
    public String gestionarComprasHttp(SolicitudCompra solicitud, com.zeroc.Ice.Current current) throws ErrorPago {
        if (solicitud.medioPago == null || (!solicitud.medioPago.equals("STRIPE") &&
            !solicitud.medioPago.equals("PSE") && !solicitud.medioPago.equals("CRIPTO"))) {
            ErrorPago error = new ErrorPago();
            error.motivo = "Medio no soportado";
            throw error;
        }

        String id = UUID.randomUUID().toString();
        estadoTransacciones.put(id, EstadoPago.PENDIENTE);
        compras.put(id, solicitud);
        guardarEnBD(id, solicitud.monto, solicitud.moneda, solicitud.medioPago, "PENDIENTE", "", "Pendiente");

        new Thread(() -> procesarConPasarela(id, solicitud)).start();

        return id;
    }

    @Override
    public EstadoPago solicitarEstadoPago(String idTransaccion, com.zeroc.Ice.Current current) {
        EstadoPago estado = estadoTransacciones.get(idTransaccion);
        return estado != null ? estado : EstadoPago.PENDIENTE;
    }

    public void actualizarEstadoPago(String id, EstadoPago estado, String idExterno, String motivo, com.zeroc.Ice.Current current) {
        SolicitudCompra solicitud = compras.get(id);
        if (solicitud != null) {
            estadoTransacciones.put(id, estado);
            guardarEnBD(id, solicitud.monto, solicitud.moneda, solicitud.medioPago, estado.toString(), idExterno, motivo);
        }
    }

    private void procesarConPasarela(String id, SolicitudCompra solicitud) {
        try {
            SolicitudPago solicitudPago = new SolicitudPago();
            solicitudPago.idTransaccion = id;
            solicitudPago.claveIdempotencia = solicitud.claveIdempotencia;
            solicitudPago.monto = solicitud.monto;
            solicitudPago.moneda = solicitud.moneda;
            solicitudPago.datos = solicitud.datos;

            EstrategiaPagoPrx estrategia = null;
            if (solicitud.medioPago.equals("STRIPE")) estrategia = stripe;
            else if (solicitud.medioPago.equals("PSE")) estrategia = pse;
            else if (solicitud.medioPago.equals("CRIPTO")) estrategia = cripto;

            if (estrategia != null) {
                AcuseCobro acuse = estrategia.procesarPago(solicitudPago);
                if (!acuse.aceptado) {
                    estadoTransacciones.put(id, EstadoPago.FALLIDA);
                    guardarEnBD(id, solicitud.monto, solicitud.moneda, solicitud.medioPago, "FALLIDA", "", acuse.motivo);
                }
            }
        } catch (Exception e) {
        }
    }

    private void guardarEnBD(String id, double monto, String moneda, String medio, String estado, String externo, String motivo) {
        try {
            Class.forName("org.postgresql.Driver");
            java.sql.Connection conn = java.sql.DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/apexstore_db", "apexstore_user", "apexstore_pass123");
            String sql = "INSERT INTO transacciones (id_transaccion, monto, moneda, medio_pago, estado, id_externo, motivo) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id_transaccion) DO UPDATE SET " +
                         "estado = EXCLUDED.estado, id_externo = EXCLUDED.id_externo, motivo = EXCLUDED.motivo";
            java.sql.PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setDouble(2, monto);
            pstmt.setString(3, moneda);
            pstmt.setString(4, medio);
            pstmt.setString(5, estado);
            pstmt.setString(6, externo);
            pstmt.setString(7, motivo);
            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
        }
    }
}
