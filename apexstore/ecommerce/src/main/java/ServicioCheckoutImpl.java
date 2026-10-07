import apexstore.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ServicioCheckoutImpl implements apexstore.ServicioCheckout {
    private Map<String, ResultadoPago> estadoTransacciones = new ConcurrentHashMap<String, ResultadoPago>();
    private ProcesadorPagosContexto procesador;

    public ServicioCheckoutImpl(ProcesadorPagosContexto procesador) {
        this.procesador = procesador;
    }

    @Override
    public String gestionarComprasHttp(SolicitudCompra solicitud, com.zeroc.Ice.Current current)
            throws ErrorPago {
        if (!validarMedioPago(solicitud.medioPago)) {
            ErrorPago error = new ErrorPago();
            error.motivo = "Medio de pago no soportado: " + solicitud.medioPago;
            throw error;
        }

        String idTransaccion = UUID.randomUUID().toString();

        SolicitudPago solicitudPago = new SolicitudPago();
        solicitudPago.idTransaccion = idTransaccion;
        solicitudPago.claveIdempotencia = solicitud.claveIdempotencia;
        solicitudPago.monto = solicitud.monto;
        solicitudPago.moneda = solicitud.moneda;
        solicitudPago.datos = solicitud.datos;

        ResultadoPago inicial = new ResultadoPago();
        inicial.idTransaccion = idTransaccion;
        inicial.estado = EstadoPago.PENDIENTE;
        inicial.motivo = "Transacción iniciada";
        estadoTransacciones.put(idTransaccion, inicial);

        procesador.iniciarPagoOrden(solicitudPago, solicitud.medioPago);

        return idTransaccion;
    }

    @Override
    public EstadoPago solicitarEstadoPago(String idTransaccion, com.zeroc.Ice.Current current) {
        ResultadoPago resultado = estadoTransacciones.getOrDefault(idTransaccion, null);
        if (resultado == null) {
            return EstadoPago.PENDIENTE;
        }
        return resultado.estado;
    }

    public void actualizarEstadoPago(ResultadoPago resultado) {
        estadoTransacciones.put(resultado.idTransaccion, resultado);
    }

    private boolean validarMedioPago(String medio) {
        return medio != null && (medio.equals("STRIPE") || medio.equals("PSE") || medio.equals("CRIPTO"));
    }
}
