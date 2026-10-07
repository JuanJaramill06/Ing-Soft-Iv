import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import apexstore.*;

public class Main {
    public static void main(String[] args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter adapter = communicator.createObjectAdapterWithEndpoints(
                "Pasarelas", "tcp -h localhost -p 10001");

            String registroProxy = "ServicioRegistroTransacciones:tcp -h localhost -p 10000";
            ServicioRegistroTransaccionesPrx registroPrx =
                ServicioRegistroTransaccionesPrx.uncheckedCast(
                    communicator.stringToProxy(registroProxy));

            ControladorNotificacion controlador = new ControladorNotificacion(registroPrx);

            adapter.add(new EstrategiaStripe(controlador),
                Util.stringToIdentity("estrategiaStripe"));
            adapter.add(new EstrategiaPSE(controlador),
                Util.stringToIdentity("estrategiaPSE"));
            adapter.add(new EstrategiaCripto(controlador),
                Util.stringToIdentity("estrategiaCripto"));

            adapter.activate();

            communicator.waitForShutdown();
        }
    }
}
