package sample;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javax.swing.text.html.ListView;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ModRegCont implements Initializable {
    //variables
    private Connection con=null;
    private ArrayList<Module>mods;
    private ObservableList<Module>obsMods;

    @FXML private JFXListView<Module> lstMods;
    @FXML private JFXTextField txtModCode;
    @FXML private JFXTextField txtModName;
    @FXML private JFXTextField txtLectCode;
    @FXML private JFXTextField txtLectName;
    @FXML private JFXTextField txtLectEmail;
    @FXML private Spinner<Integer> spnModLev;

    //methods
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mods=new ArrayList<Module>();
        obsMods=FXCollections.observableArrayList();
        obsMods.addAll(mods);
        setUpMods();


        lstMods.setItems(obsMods);
        lstMods.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if(oldValue!=null){
                txtModCode.textProperty().unbindBidirectional(oldValue.modCodeProperty());
                txtModName.textProperty().unbindBidirectional(oldValue.modNameProperty());

                //changes in lecturer
                connect();
                try {

                    String sql="select * from Lecturer where LectCode = ?";
                    PreparedStatement stmt=con.prepareStatement(sql);
                    stmt.setString(1,oldValue.lectCodeProperty().get());
                    ResultSet result=stmt.executeQuery();
                    while (result.next()){
                        String lectCode=result.getString("LectCode");
                        String lectName=result.getString("Name");
                        String lectEmail=result.getString("Email");
                        Lecturer curLect=new Lecturer(lectCode,lectName,lectEmail);
                        txtLectCode.textProperty().unbindBidirectional(curLect.lectCodeProperty());
                        txtLectName.textProperty().unbindBidirectional(curLect.lectNameProperty());
                        txtLectEmail.textProperty().unbindBidirectional(curLect.lectEmailProperty());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                disconnect();
            }
            if(newValue!=null){
                txtModCode.textProperty().bindBidirectional(newValue.modCodeProperty());
                txtModName.textProperty().bindBidirectional(newValue.modNameProperty());

                //changes in lecturer
                connect();
                try {
                    String sql="select * from Lecturer where LectCode = ?";
                    PreparedStatement stmt=con.prepareStatement(sql);
                    stmt.setString(1,newValue.lectCodeProperty().get());
                    ResultSet result=stmt.executeQuery();
                    while (result.next()){
                        String lectCode=result.getString("LectCode");
                        String lectName=result.getString("Name");
                        String lectEmail=result.getString("Email");
                        Lecturer curLect=new Lecturer(lectCode,lectName,lectEmail);
                        txtLectCode.textProperty().bindBidirectional(curLect.lectCodeProperty());
                        txtLectName.textProperty().bindBidirectional(curLect.lectNameProperty());
                        txtLectEmail.textProperty().bindBidirectional(curLect.lectEmailProperty());
                        spnModLev.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,3,newValue.modLevelProperty().getValue()));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                disconnect();
            }
        }));

        lstMods.getSelectionModel().selectFirst();
    }
    private void setUpMods(){
        connect();
        Statement stmt=null;
        try {
            stmt=con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql="select * from Module";
        try {
            ResultSet result=stmt.executeQuery(sql);
            while (result.next()){
                String modCode=result.getString("ModCode");
                String modName=result.getString("ModName");
                Integer modLevel=result.getInt("ModLevel");
                String lectCode=result.getString("LectCode");
                Module newMod=new Module(modCode,modName,modLevel,lectCode);
                obsMods.add(newMod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        disconnect();
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
