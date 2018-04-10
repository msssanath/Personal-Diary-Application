/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my;
//import java.util.*;
import java.io.*;
/**
 *
 * @author Sanath
 */
public class Account {
    /*String username;
    String password;
    String name;
    String buffer = " ";
    void pack()
    {
        buffer = username + "|" + password + "|" + name + "\n";
    }
    
    void unpack()
    {
        String b[];
        b = buffer.split("|");
        username = b[0];
        password = b[1];
        name = b[2];
    }
    
    public Account(String username, String password, String name)
    {
        this.username = username;
        this.password = password;
        this.name = name;
    }*/
    void addAccount(String username, String password, String name) throws IOException
    {
        try
        {
            RandomAccessFile raf = new RandomAccessFile("Accounts.txt","rw");
            File f = new File("Accounts.txt");  
            long flen = f.length();             //To find length of the file
            System.out.println(flen);
            raf.seek(flen);
            int hashedPassword = password.hashCode();
            String buffer = username + "|" + hashedPassword + "|" + name + "#\n"; //Pack
            System.out.println(buffer);
            raf.writeBytes(buffer);     //Writing to the file
            
            raf.seek(0);                //Reading from first line
            System.out.println("Reading: ");
            String line;
            while(true)
            {
                line = raf.readLine();
                if(line == null)
                    break;
                System.out.println(line);
            }
            raf.close();
            //return 1;
        }
        catch(IOException e)
        {
            System.out.println("Exception");
        }
        //return 0;
    }
    
    void login(String username, String password) throws IOException
    {
        try
        {
            RandomAccessFile raf = new RandomAccessFile("Accounts.txt", "rw");
            String line;
            int Password = password.hashCode();
            int flag = 0;
            while(true)
            {
                line = raf.readLine();
                if(line == null)
                    break;
                System.out.println(line);
                String b[];
                b = line.split("\\|");
                //System.out.println(b[0] + " " + b[1] + " " + b[2]);
                String user = b[0];
                if(username.equals(user))
                {
                    int hashedPassword = Integer.parseInt(b[1]);
                    if(Password == hashedPassword)
                    {
                        flag = 1;
                        System.out.println("Found");
                        System.out.println(Password);
                        break;
                        //return 1;
                    }
                }
            }
            //System.out.println("After loop : ");
            if(flag == 0)
            {
                System.out.println("Not found");
            }
            raf.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
        //return 0;
    }
    
    public static void main(String args[]) throws IOException
    {
        Account A = new Account();
        //int res = A.addAccount("pterdal", "cr7", "Pawan");
        
        //A.addAccount("sanath", "kumar", "Sanath");
        A.login("sanath","kumar");
    }
}
