import apexstore.*;
import java.util.Random;

public class EstrategiaCripto implements apexstore.EstrategiaPago {
    private ControladorNotificacion controlador;
    private Random random = new Random();

    public EstrategiaCripto(ControladorNotificacion controlador) {
        this.controlador = controlador;
    }

    @Override
    public AcuseCobro procesarPago(SolicitudPago solicitud, com.zeroc.Ice.Current current) {
        if (!solicitud.datos.containsKey("wallet")) {
            return new AcuseCobro(false, "Wallet no proporcionada");
        }

        AcuseCobro acuse = new AcuseCobro(true, "En proceso de confirmación blockchain");

        new Thread(() -> {
            try {
                Thread.sleep(2000 + random.nextInt(1000));
                ResultadoPago resultado = new ResultadoPago();
                resultado.idTransaccion = solicitud.idTransaccion;
                resultado.estado = random.nextDouble() > 0.2 ?
                    EstadoPago.CONFIRMADA : EstadoPago.FALLIDA;
                resultado.idExterno = "tx_" + Long.toHexString(System.nanoTime());
                resultado.motivo = resultado.estado == EstadoPago.CONFIRMADA ?
                    "Transacción confirmada en blockchain" :
                    "Rechazo simulado de red blockchain";

                controlador.notificarResultadoPago(resultado);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return acuse;
    }
}
