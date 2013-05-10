package srsoftwares;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * Author: Sumit Roy
 * Date: 12/6/11
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetworkUtility {
    public boolean isHostReachable(String host, int maxTry) {
        try {
            if (Inet4Address.getByName(host).isReachable(maxTry*1000)) {

            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return true;
    }

    public boolean portScan(String host, int port) {
        try {
            Socket serverSocket = new Socket(host, port);
            serverSocket.close();
        } catch (IOException e) {
           return false;
        }
        return true;
    }

    public static void main(String[] args) {
       NetworkUtility obj=new NetworkUtility();
        obj.portScan("127.0.0.1",1521);
    }
}
