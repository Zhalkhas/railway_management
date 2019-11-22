package org.yoptascript.inc;


import org.yoptascript.inc.sql.Statements;

import java.io.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        Statements s = new Statements();
        s.createRouteManager("Almaty", "Astana", "2019-01-01 12-34-45", "2019-01-01 02-34-45", 300);


    }
}