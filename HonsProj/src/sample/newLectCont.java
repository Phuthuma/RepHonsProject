package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class newLectCont implements Initializable {
    //variables
    private Connection con=null;

    @FXML private JFXTextField txtCode;
    @FXML private JFXTextField txtName;
    @FXML private JFXTextField txtEmail;
    @FXML private JFXPasswordField passOne;
    @FXML private JFXButton btnSaveLect;
    @FXML private JFXButton btnCancelLect;

    //Initialize
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RequiredFieldValidator val=new RequiredFieldValidator();
        val.setMessage("Required Field");
        txtCode.getValidators().add(val);
        txtCode.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue)
                txtCode.validate();
        });
        txtName.getValidators().add(val);
        txtName.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(oldValue)
                txtName.validate();
        });
        txtEmail.getValidators().add(val);
        txtEmail.focusedProperty().addListener((observable, oldValue, newValue) -> {
            txtEmail.validate();
        });
        passOne.getValidators().add(val);
        passOne.focusedProperty().addListener((observable, oldValue, newValue) -> {passOne.validate();});

        btnSaveLect.setOnAction(event -> {
            connect();
            String sql="select LectCode from Lecturer where LectCode = ?";
            try {
                PreparedStatement stmt=con.prepareStatement(sql);
                stmt.setString(1,txtCode.getText());
                ResultSet result=stmt.executeQuery();
                if (result.next()){
                    String foundModCode=result.getString("LectCode");
                    Alert alert=new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("DB Primary Key Error");
                    alert.setContentText(foundModCode+" already exists in list!");
                    alert.showAndWait();
                }
                else {
                    sql="insert into Lecturer values (?, ?,?,?)";
                    stmt=con.prepareStatement(sql);
                    stmt.setString(1,txtCode.getText());
                    stmt.setString(2,txtName.getText());
                    stmt.setString(3,txtEmail.getText());
                    stmt.setString(4,passOne.getText());
                    stmt.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            disconnect();

            HtmlEmail email=new HtmlEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(465);
            email.setSSLOnConnect(true);
            email.setAuthentication("phuthumaloyisopetse@gmail.com","sweleba88");

            try {
                email.setFrom("phuthumaloyisopetse@gmail.com");
            } catch (EmailException e) {
                e.printStackTrace();
            }
            try {
                email.addTo(txtEmail.getText());
            } catch (EmailException e) {
                e.printStackTrace();
            }
            email.setSubject("SolAssist log in credentials");
            try {
                email.setHtmlMsg("Hi Phuthuma an account has been created in solAssist for username: "+txtCode.getText()+" Password: "+passOne.getText());
            } catch (EmailException e) {
                e.printStackTrace();
            }
            try {
                email.send();
            } catch (EmailException e) {
                e.printStackTrace();
            }

            Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();

        });
        btnCancelLect.setOnAction(event -> {
            Stage stage= (Stage) ((Node)event.getSource()).getScene().getWindow();
            stage.close();
        });
    }

    private void connect(){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(true){
            String connectionString="jdbc:sqlserver://postgrad.nmmu.ac.za;database=SolAssist";

            try {
                con=DriverManager.getConnection(connectionString,"solassistuser","Dfjf8d02fdjjJ");

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private void disconnect(){
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
