package co.edu.uniquindio.banco.controlador;

import co.edu.uniquindio.banco.modelo.Banco;
import co.edu.uniquindio.banco.modelo.CuentaAhorros;
import co.edu.uniquindio.banco.modelo.Sesion;
import co.edu.uniquindio.banco.modelo.Usuario;
import co.edu.uniquindio.banco.modelo.enums.CategoriaTransaccion;
import co.edu.uniquindio.banco.controlador.observador.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class TransferirControlador {


    @FXML
    private ComboBox<String> txtCategoria;
    @FXML
    private TextField txtCuenta;
    @FXML
    private TextField txtTransferir;

    private Observable observable;
    private final Banco banco = Banco.getInstancia();
    private final Sesion sesion = Sesion.getInstancia();
    private final ObservableList<String> categoriaList = FXCollections.observableArrayList("Viajes", "Facturas", "Gasolina", "Ropa", "Pago", "Otros");

    public void realizarTransferencia(ActionEvent event) {
        try {
            Usuario usuario = sesion.getUsuario();
            List<CuentaAhorros> cuentas = banco.consultarCuentasUsario(usuario.getNumeroIdentificacion(), usuario.getContrasena());
            CategoriaTransaccion categoria = CategoriaTransaccion.valueOf(txtCategoria.getValue().toUpperCase());
            banco.realizarTransferencia(cuentas.get(0).getNumeroCuenta(), txtCuenta.getText(), Float.parseFloat(txtTransferir.getText()), categoria);
            observable.notificar();
            organizar();
            mostrarAlerta("Transferencia completada", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta(e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void organizar() {
        txtCuenta.clear();
        txtTransferir.clear();
        txtCategoria.setValue(null);
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();
    }

    public void initialize() {
        txtCategoria.setItems(categoriaList);
    }

    public void inicializarObservable(Observable observable) {
        this.observable = observable;
    }
}