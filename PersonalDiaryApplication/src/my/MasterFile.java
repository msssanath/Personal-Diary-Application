package my;
import java.util.*;
import java.io.*;
import java.text.DateFormat;
class FileStructure
{
  Scanner in = new Scanner(System.in);

  String name,time,title,content;                  // class variables
  String keywords;
  int count=0;                                     //no of records
  

  public FileStructure(String name)
  {
    this.name = name;
    try
    {
        RandomAccessFile raf = new RandomAccessFile("MasterFile.txt", "rw");
        String line;
        while(true)                                 //to get the no of records
        {
            line = raf.readLine();
            if(line == null)
                break;
            this.count++;                           
            System.out.println(this.count);
        }
    }
    catch(IOException e)
    {
        System.out.println("Exception");
    }
    
  }
  public void getUserDetails(String title, String content, String keywords)
  {
    this.time = getSystemTime();                  // will come from text field
    //System.out.println("Title: ");
    this.title = title;
    //System.out.println("Content: ");
    this.content = content;
    this.keywords = keywords;
    System.out.println("Keywords : " + keywords);
  }

  public String getSystemTime()
  {
    Date currentDate = new Date();
    return currentDate.toString();
  }

  public void printUserDetails()
  {
    System.out.println("****************************");
    System.out.println(this.name);
    System.out.println(this.time);
    System.out.println(this.title);
    System.out.println(this.content);
  }
  // ***************************** enter details in main/master file ***************************//
  public void writeToMasterFile()
  {
    try
    {
      RandomAccessFile fout = new RandomAccessFile("MasterFile.txt","rw");
      fout.seek(fout.length());
      long size = fout.getFilePointer();    
             
      fout.writeBytes(this.count+"|"+this.name+"|"+this.time+"|"+this.title+"|"+this.content+"|"+this.keywords+"\n");         //primary key format
      writeToPrimaryIndexFile(String.valueOf(this.count),String.valueOf(size));
      writeToSecondaryIndexFile(this.time,String.valueOf(this.count),this.name);
      fout.close();
    }
    catch(IOException e)
    {
      System.out.println("Error!");
    }
  }

  //***************************** enter details in primary index file ************************//

  public void writeToPrimaryIndexFile(String primaryKey,String offset)
  {
    try
    {
      
      RandomAccessFile fout = new RandomAccessFile("PrimaryIndexFile.txt","rw");
      fout.seek(fout.length());
      fout.writeBytes(primaryKey+"|"+offset+"\n");
      fout.close();
    }
    catch(IOException e)
    {
      System.out.println("Error!");
    }
  }


  //************************ write into secondary index file ***************//
  public void writeToSecondaryIndexFile(String date,String primaryKey,String name)
  {
    try
    {
      RandomAccessFile fout = new RandomAccessFile("SecondaryIndexFile.txt","rw");
      fout.seek(fout.length());
      fout.writeBytes(date+"|"+primaryKey+"|"+name+"\n");
      fout.close();

    }
    catch(IOException e)
    {
      System.out.println("Error!");
    }
  }
}

//***************************************** main *************************************//
class MasterFile
{
  public static void main(String[] args) {
    //FileStructure user = new FileStructure("Sha");
    /*user.getUserDetails("Dead","Might die","Death,End");
    user.printUserDetails();
    user.writeToMasterFile();*/
    /*String name;
      System.out.println("Name: ");
      name = in.nextLine();
      FileStructure user = new FileStructure(name);
      user.getUserDetails();
      user.printUserDetails();
      user.writeToMasterFile();*/
    
  }
}
