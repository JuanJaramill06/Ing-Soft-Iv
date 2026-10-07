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

    interface ServicioCheckout {
        string gestionarComprasHttp(SolicitudCompra solicitud) throws ErrorPago; 
        EstadoPago solicitarEstadoPago(string idTransaccion);
        void actualizarEstadoPago(string idTransaccion, EstadoPago estado, string idExterno, string motivo);
    };

    interface EstrategiaPago {
        AcuseCobro procesarPago(SolicitudPago solicitud);
    };

    interface ServicioRegistroTransacciones {
        void registrarResultado(ResultadoPago resultado);
    };

    interface RepositorioTransacciones {
        void persistirTransaccion(Transaccion transaccion);
    };
};