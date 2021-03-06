package my;
import java.util.*;
import java.io.*;
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
  public FileStructure()
  {
      
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
      writeToKeywordIndexFile(this.keywords, String.valueOf(this.count), this.name);
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


  //************************ write into Secondary index file indexed by Date ***************//
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
  //*************************write into Secondary index file indexed by Keywords*************************//
    public void writeToKeywordIndexFile(String keywords, String primaryKey, String name)
    {
        try
        {
            RandomAccessFile fout = new RandomAccessFile("KeywordIndexFile.txt","rw");
            fout.seek(fout.length());
            fout.writeBytes(keywords+"|"+primaryKey+"|"+name+"\n");
            fout.close();
        }
        catch(IOException e)
        {
            System.out.println("Exception");
        }
    }
  //******************************Search By Date***************************************//
  public String SearchByDate(String monthI, String dayI)
  {
      
      String buffer[] = new String[this.count+1];
      String entry="";
      int k=0;
      try
      {
          RandomAccessFile master = new RandomAccessFile("MasterFile.txt","rw");
          RandomAccessFile rab = new RandomAccessFile("PrimaryIndexFile.txt","rw");
          while(true)
          {
              buffer[k] = rab.readLine();       //store all records of primary index
              if(buffer[k] == null)
                  break;
              k++;
          }
          System.out.println("K : " + k);
          System.out.println("count : " + this.count);
          RandomAccessFile raf = new RandomAccessFile("SecondaryIndexFile.txt","rw");
          String line;
          while(true)
          {
              line = raf.readLine();
              if(line == null)
                  break;
              String record[] = line.split("\\|");
              String Date = record[0];         //Assign Date, pk and name from secondary index
              String primaryKey = record[1];
              String nameF = record[2];
              if(nameF.equals(this.name))
              {
                  System.out.println(line);
                  String date[] = Date.split(" ");      //Assign day and month from Date
                  String MonthF = date[1];
                  String dayF = date[2];
                  String monthF = assignNumbersToMonths(MonthF);    //Eg:- Jan-->1 Feb-->2
                  if(monthI.equals(monthF) && dayI.equals(dayF))
                  {
                      int pos = BinarySearchOnIndex(buffer, 0, this.count-1, primaryKey); //Search buffer[] for the primary key
                      System.out.println(pos);
                      String pIndexRecord[] = buffer[pos].split("\\|");
                      int offset = Integer.parseInt(pIndexRecord[1]);       //Get offset of the primary key
                      master.seek(offset);                                  //Seek to the offset and print it
                      entry = entry.concat(master.readLine()+"\n");
                      System.out.println(entry);
                  }
              }
          }
          if(entry.length() == 0)
          {
            return "No entry on that day";
          }
      }
      
      catch(IOException e)
      {
          System.out.println("Exception");
      }
      
      return entry;
  }
  //*********************Search By Keywords****************************************//
  public String SearchByKeyword(String keyword)
  {
      String buffer[] = new String[this.count+1];
      int k=0;
      String entry = "";
      try
      {
          RandomAccessFile master = new RandomAccessFile("MasterFile.txt","rw");
          RandomAccessFile pIndex = new RandomAccessFile("PrimaryIndexFile.txt","rw");
          RandomAccessFile sIndex = new RandomAccessFile("KeywordIndexFile.txt","rw");
          while(true)
          {
              buffer[k] = pIndex.readLine();
              if(buffer[k] == null)
                  break;
              k++;
          }
          String line;
          while(true)
          {
              line = sIndex.readLine();
              if(line == null)
                  break;
              String record[] = line.split("\\|");
              String allKeywords = record[0];
              String primaryKey = record[1];
              String nameF = record[2];
              System.out.println(allKeywords);
              StringTokenizer st = new StringTokenizer(allKeywords, ",");
              int noOfKeywords = st.countTokens();
              System.out.println(noOfKeywords);
              String keywordsF[] = new String[noOfKeywords];
              for(int i=0;i<noOfKeywords;i++)
              {
                  keywordsF[i] = st.nextToken();
                  System.out.println(keywordsF[i]);
              }
              if(nameF.equals(this.name))
              {
                  for(int i=0;i<noOfKeywords;i++)
                  {
                      if(keyword.equals(keywordsF[i]))
                      {
                          int pos = BinarySearchOnIndex(buffer, 0, this.count-1, primaryKey); //Search buffer[] for the primary key
                          String pIndexRecord[] = buffer[pos].split("\\|");
                          int offset = Integer.parseInt(pIndexRecord[1]);       //Get offset of the primary key
                          master.seek(offset);                                  //Seek to the offset and print it
                          entry = entry.concat(master.readLine()+"\n");
                          System.out.println(entry);
                      }
                  }
              }
              
          }
          if(entry.length() == 0)
          {
              return "No entry on that day";
          }
          
      }
      catch(IOException e)
      {
          System.out.println("Exception");
      }
      return entry;
  }
  //*****************Binary Search on Primary Index File***************************//
  public int BinarySearchOnIndex(String buffer[], int low, int high, String key)
  {
      int flag = 0;
      int iKey = Integer.parseInt(key);
      while(low<=high)
      {
        int mid = (low+high)/2;
        String record[] = buffer[mid].split("\\|");
        String primaryKey = record[0];
        System.out.println("Low : " + low + "\t" + "High : " + high);
        int pKey = Integer.parseInt(primaryKey);
        if(iKey == pKey)
        {
            flag = 1;
            return mid;
        }
        else if(iKey < pKey)
            high = mid-1;
        else
            low = mid+1;
      }
      return -1;
  }
    //****************Assign Numbers to Months.Jan-->1 etc*************************//
    public String assignNumbersToMonths(String MonthF)
    {
        String monthf = "";
        if(MonthF.equals("Jan"))
            monthf = "01";
        else if(MonthF.equals("Feb"))
            monthf = "02";
        else if(MonthF.equals("Mar"))
            monthf = "03";
        else if(MonthF.equals("Apr"))
            monthf = "04";
        else if(MonthF.equals("May"))
            monthf = "05";
        else if(MonthF.equals("Jun"))
            monthf = "06";
        else if(MonthF.equals("July"))
            monthf = "07";
        else if(MonthF.equals("Aug"))
            monthf = "08";
        else if(MonthF.equals("Sept"))
            monthf = "09";
        else if(MonthF.equals("Oct"))
            monthf = "10";
        else if(MonthF.equals("Nov"))
            monthf = "11";
        else if(MonthF.equals("Dec"))
            monthf = "12";
        return monthf;
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
    FileStructure user = new FileStructure("Pawan");
    System.out.println(user.SearchByKeyword("Jai"));
    //user.SearchByDate("04", "16");
    /*String name;
      System.out.println("Name: ");
      name = in.nextLine();
      FileStructure user = new FileStructure(name);
      user.getUserDetails();
      user.printUserDetails();
      user.writeToMasterFile();*/
    
  }
}
