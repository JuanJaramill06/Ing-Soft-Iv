module apexstore {

    dictionary<string, string> DatosMedio;   // token, banco, wallet... según el medio

    enum EstadoPago { PENDIENTE, AUTORIZADA, CONFIRMADA, FALLIDA, EXPIRADA };

    // Lo que envía el cliente
    struct SolicitudCompra {
        string claveIdempotencia;
        double monto;
        string moneda;
        string medioPago;        // "STRIPE", "PSE", "CRIPTO"
        DatosMedio datos;
    };

    // Lo que recibe cada estrategia (igual para las tres)
    struct SolicitudPago {
        string idTransaccion;
        string claveIdempotencia;
        double monto;
        string moneda;
        DatosMedio datos;
    };

    struct AcuseCobro {
        bool aceptado;
        string motivo;
    };

    struct ResultadoPago {
        string idTransaccion;
        EstadoPago estado;
        string idExterno;
        string motivo;
    };

    // Registro que se guarda en la BD
    struct Transaccion {
        string idTransaccion;
        double monto;
        string moneda;
        string medioPago;
        EstadoPago estado;
        string idExterno;
        string motivo;
    };

    exception ErrorPago {
        string motivo;
    };

    // Nodo 2: lo consumen WebApp y MobileApp
    interface ServicioCheckout {
        string gestionarComprasHttp(SolicitudCompra solicitud) throws ErrorPago;  // devuelve idTransaccion
        EstadoPago solicitarEstadoPago(string idTransaccion);
    };

    // Nodo 3: la implementan las tres estrategias (una instancia por medio de pago)
    interface EstrategiaPago {
        AcuseCobro procesarPago(SolicitudPago solicitud);
    };

    // Nodo 2: la llama ControladorNotificacion (Nodo 3)
    interface ServicioRegistroTransacciones {
        void registrarResultado(ResultadoPago resultado);
    };

    // Nodo 4: la llama solo ServicioRegistroTransacciones
    interface RepositorioTransacciones {
        void persistirTransaccion(Transaccion transaccion);
    };
};