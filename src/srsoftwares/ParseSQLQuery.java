package srsoftwares;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Sumit Roy
 * Date: 4/16/12
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ParseSQLQuery {
    public List<String> parseSQL(String sql) {
        int countSingleQt = 0, start = 0, end = 0, j = 0;
        if (!sql.endsWith(";")) {
            sql = sql.concat(";");
        }
        String[] multipleQuery = new String[10];
        List arrs = new ArrayList(0);

        for (int i = 0; i < sql.length(); i++) {
            char ct = sql.charAt(i);
            if (ct == '\'') {
                countSingleQt++;
                if (countSingleQt == 2) {
                    countSingleQt = 0;
                }
            }
            if (ct == ';') {
                if (countSingleQt == 1) {

                } else {
                    end = i;
                    String s = sql.substring(start, end);
                    //multipleQuery[j++]=s;
                    arrs.add(s);

                    start = end + 1;
                }
            }
        }
        return arrs;
    }

    public static void main(String[] args) {
        ParseSQLQuery obj = new ParseSQLQuery();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter a SQL= ");
        try {
            String sql = br.readLine();
            if (DataBaseUtility.isValidSQLQuery(sql)) {
                List<String>  arrs=obj.parseSQL(sql);
                for (String arr : arrs) {
                    System.out.println(arr);
                }
            } else
                System.out.println("NOT A VALID SQL");
        } catch (IOException e) {

        }

    }
}
