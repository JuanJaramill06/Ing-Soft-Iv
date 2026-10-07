import apexstore.*;

public class EstrategiaPSE implements apexstore.EstrategiaPago {
    private ControladorNotificacion controlador;

    public EstrategiaPSE(ControladorNotificacion controlador) {
        this.controlador = controlador;
    }

    @Override
    public AcuseCobro procesarPago(SolicitudPago solicitud, com.zeroc.Ice.Current current) {
        if (!solicitud.datos.containsKey("banco")) {
            return new AcuseCobro(false, "Código de banco no proporcionado");
        }

        AcuseCobro acuse = new AcuseCobro(true, "Autorizado");

        new Thread(() -> {
            try {
                Thread.sleep(1500);
                ResultadoPago resultado = new ResultadoPago();
                resultado.idTransaccion = solicitud.idTransaccion;
                resultado.estado = EstadoPago.CONFIRMADA;
                resultado.idExterno = "pse_" + System.nanoTime();
                resultado.motivo = "Transacción PSE procesada";

                controlador.notificarResultadoPago(resultado);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return acuse;
    }
}
