package co.edu.uniquindio.banco.controlador;

import co.edu.uniquindio.banco.modelo.*;
import co.edu.uniquindio.banco.controlador.observador.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PanelClienteControlador implements Initializable, Observable {

    // Variables de instancia
    private Usuario usuario;
    private final Banco banco = Banco.getInstancia();

    // Elementos FXML
    @FXML private Label usuarioLabel;
    @FXML private Label cuentaLabel;
    @FXML private TableView<Transaccion> tableAdministracion;
    @FXML private TableColumn<Transaccion, String> tcTipo;
    @FXML private TableColumn<Transaccion, String> tcFecha;
    @FXML private TableColumn<Transaccion, String> tcValor;
    @FXML private TableColumn<Transaccion, String> tcUsuario;
    @FXML private TableColumn<Transaccion, String> tcCategoria;

    // Métodos públicos
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inicializarUsuario();
        mostrarUsuario();
        configurarColumnasTabla();
    }

    @FXML
    public void cerrarSesion() {
        Sesion.getInstancia().cerrarSesion();
        tableAdministracion.getScene().getWindow().hide();
    }

    @FXML
    public void consultar() {
        try {
            if (usuario != null) {
                List<CuentaAhorros> cuentas = banco.consultarCuentasUsario(usuario.getNumeroIdentificacion(), usuario.getContrasena());
                if (!cuentas.isEmpty()) {
                    float valor = cuentas.get(0).getSaldo();
                    String mensaje = "Su saldo actual es: " + valor;
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Información", null, mensaje);
                }
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El usuario no tiene saldo", e.getMessage());
        }
    }

    public void irTransferir(ActionEvent actionEvent){
        navegarTransferir("/transferir.fxml", "Transferencia");
    }
    public void irActualizar(ActionEvent actionEvent){
        navegarActualizar("/actualizar.fxml", "Actualización de datos");
    }


    @FXML
    public void navegarTransferir(String nombreArchivoFxml, String tituloVentana) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nombreArchivoFxml));
            Parent root = loader.load();
            TransferirControlador controlador = loader.getController();
            controlador.inicializarObservable(this);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(tituloVentana);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void navegarActualizar(String nombreArchivoFxml, String tituloVentana){
        try {
            // Cargar la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nombreArchivoFxml));
            Parent root = loader.load();
            // Crear la escena
            Scene scene = new Scene(root);
            // Crear un nuevo escenario (ventana)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(tituloVentana);
            // Mostrar la nueva ventana
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notificar() {
        consultarCuentas();
    }

    // Métodos privados
    private void inicializarUsuario() {
        usuario = Sesion.getInstancia().getUsuario();
    }

    private void mostrarUsuario() {
        if (usuario != null) {
            usuarioLabel.setText(usuario.getNombre() + " bienvenido a su banco, aquí podrá ver sus transacciones");
            consultarCuentas();
        }
    }

    private void consultarCuentas() {
        try {
            List<CuentaAhorros> cuentas = banco.consultarCuentasUsario(usuario.getNumeroIdentificacion(), usuario.getContrasena());
            if (!cuentas.isEmpty()) {
                cuentaLabel.setText(cuentas.get(0).getNumeroCuenta());
                consultarTransacciones(cuentas.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void consultarTransacciones(CuentaAhorros cuenta) {
        tableAdministracion.setItems(FXCollections.observableArrayList(cuenta.getTransacciones()));
    }

    private void configurarColumnasTabla() {
        tcTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipo().toString()));
        tcFecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFecha().toString()));
        tcValor.setCellValueFactory(cellData -> new SimpleStringProperty("" + cellData.getValue().getMonto()));
        tcUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsuario().getNombre()));
        tcCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().toString()));
    }

    private void mostrarAlerta(Alert.AlertType tipoAlerta, String titulo, String encabezado, String mensaje) {
        Alert alert = new Alert(tipoAlerta);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
