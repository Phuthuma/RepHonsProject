package sample;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javax.xml.validation.Validator;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class newModuleController implements Initializable {
    //variables
    private Connection con=null;

    @FXML private JFXTextField txtCode;
    @FXML private JFXTextField txtName;

    //methods
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RequiredFieldValidator valReq=new RequiredFieldValidator();
        valReq.setMessage("Required field");
        txtCode.getValidators().add(valReq);



        txtCode.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                txtCode.validate();
            }

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
