package Gui;

import DB.USER_DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class main {
    public static void main(String[] args) {

            USER_DB.Register("123", Integer.parseInt("2"), "john1");


    }


}