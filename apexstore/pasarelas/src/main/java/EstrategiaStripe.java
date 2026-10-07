import apexstore.*;

public class EstrategiaStripe implements apexstore.EstrategiaPago {
    private ControladorNotificacion controlador;

    public EstrategiaStripe(ControladorNotificacion controlador) {
        this.controlador = controlador;
    }

    @Override
    public AcuseCobro procesarPago(SolicitudPago solicitud, com.zeroc.Ice.Current current) {
        if (!solicitud.datos.containsKey("token")) {
            return new AcuseCobro(false, "Token de tarjeta no proporcionado");
        }

        AcuseCobro acuse = new AcuseCobro(true, "Autorizado");

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                ResultadoPago resultado = new ResultadoPago();
                resultado.idTransaccion = solicitud.idTransaccion;
                resultado.estado = EstadoPago.CONFIRMADA;
                resultado.idExterno = "ch_stripe_" + System.nanoTime();
                resultado.motivo = "Transacción procesada exitosamente";

                controlador.notificarResultadoPago(resultado);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return acuse;
    }
}
