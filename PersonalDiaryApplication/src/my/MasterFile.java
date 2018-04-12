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
  public static TreeMap<String,String> primaryIndexmap = new TreeMap<String,String>();    // buffer for primaryIndex
  public static TreeMap<String,String> secondaryIndexmap = new TreeMap<String,String>();  // buffer for secondaryIndex

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
      //count++;
      this.name = this.name + count;        //Primary Key format
      fout.writeBytes(this.name+"|"+this.time+"|"+this.title+"|"+this.content+"|"+this.keywords+"\n");
      writeToPrimaryIndexFile(this.name,size);
      writeToSecondaryIndexFile(this.time,this.name);
      fout.close();
    }
    catch(IOException e)
    {
      System.out.println("Error!");
    }
  }

  //***************************** enter details in primary index file ************************//

  public void writeToPrimaryIndexFile(String name,long offset)
  {
    try
    {
      readIndexFileContents("primary",primaryIndexmap);            // for sorting it
      System.out.println(primaryIndexmap);
      primaryIndexmap.put(name,String.valueOf(offset));
      RandomAccessFile fout = new RandomAccessFile("PrimaryIndexFile.txt","rw");
      for(String i : primaryIndexmap.keySet())
      {
        fout.writeBytes(i+"|"+primaryIndexmap.get(i)+"\n");
      }
      fout.close();

    }
    catch(IOException e)
    {
      System.out.println("Error!");
    }
  }

  //***************************** load the buffer with values ************************//
  public void readIndexFileContents(String fileType,TreeMap<String,String> mapType)
  {

    try
    {
      RandomAccessFile fin;
      String pairs[] = new String[2];
      if(fileType.equals("primary"))
        fin = new RandomAccessFile("PrimaryIndexFile.txt","r");
      else
        fin = new RandomAccessFile("SecondaryIndexFile.txt","r");
      String line = fin.readLine();
      while(line != null)
      {
        pairs = line.split("\\|");
        mapType.put(pairs[0],pairs[1]);
        line = fin.readLine();
      }
    }
    catch(IOException e)
    {
      System.out.println("Error!");
    }
  }

  //************************ write into secondary index file ***************//
  public void writeToSecondaryIndexFile(String date,String name)
  {
    try
    {
      readIndexFileContents("secondary",secondaryIndexmap);
      System.out.println(secondaryIndexmap);
      secondaryIndexmap.put(date,name);
      RandomAccessFile fout = new RandomAccessFile("SecondaryIndexFile.txt","rw");
      for(String i : secondaryIndexmap.keySet())
      {
        fout.writeBytes(i+"|"+secondaryIndexmap.get(i)+"\n");
      }
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
    //Scanner in = new Scanner(System.in);
    FileStructure user = new FileStructure("Sha");
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
