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
  public static TreeMap<String, String> keywordIndexmap = new TreeMap<String,String>(); // buffer for secondary index with keywords

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
            //System.out.println(this.count);
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
      writeToKeywordIndexFile(this.keywords,this.name);
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
      else if(fileType.equals("secondary"))
        fin = new RandomAccessFile("SecondaryIndexFile.txt","r");
      else
        fin = new RandomAccessFile("KeywordIndexFile.txt","r");
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
  
//******************************write into keyword index file*************************//
    public void writeToKeywordIndexFile(String keywords, String name)
    {
        try
        {
            readIndexFileContents("keywordIndex", keywordIndexmap);
            System.out.println(keywordIndexmap);
            String Keywords[] = keywords.split(",");
            int noOfKeywords = Keywords.length;
            for(int i=0;i<noOfKeywords;i++)
            {
                keywordIndexmap.put(Keywords[i], name);
            }
            RandomAccessFile fout = new RandomAccessFile("KeywordIndexFile.txt", "rw");
            for(String key : keywordIndexmap.keySet())
            {
                fout.writeBytes(key+"|"+keywordIndexmap.get(key)+"\n");
            }
            fout.close();
        }
        catch(IOException e)
        {
            System.out.println("Exception");
        }
    }
    
    public void SearchByDate(String monthI, String dayI)
    {
        /*String Date[] = date.split(" ");
        String monthI = Date[0];
        String dayI = Date[1];*/
        String monthF = "";
        String dayF = "";
        try
        {
            RandomAccessFile raf = new RandomAccessFile("SecondaryIndexFile.txt","rw");
            String line;
            while(true)
            {
                line = raf.readLine();
                if(line == null)
                    break;
                String record[] = line.split(("\\|"));
                String NameF = record[1];
                //System.out.println(NameF);
                String nameF = "";
                for(char c : NameF.toCharArray())
                {
                    if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
                        nameF += c;
                    else
                    {
                        break;
                    }
                }
                if(nameF.equals(this.name))                //To match only the entries made by the same person
                {
                    String DateF[] = line.split(" ");
                    String MonthF = DateF[1];
                    dayF = DateF[2];
                    monthF = assignNumbersToMonths(MonthF);
                    if(monthI.equals(monthF) && dayI.equals((dayF)))
                    {
                        //String record[] = line.split("\\|");
                        //String name = record[1];
                        readIndexFileContents("primary", primaryIndexmap);
                        int offset = Integer.parseInt(primaryIndexmap.get(NameF));
                        //System.out.println(offset);
                        RandomAccessFile index = new RandomAccessFile("MasterFile.txt","rw");
                        index.seek(offset);
                        String Line = index.readLine();
                        System.out.println(Line);
                    }
                }
            }
            
            /*while(true)
            {
                int pos = BinarySearchOnIndex(buffer, 0, noOfRecords -1, monthI, dayI);
                System.out.println(pos);
                if(pos != -1)
                {
                    String record[] = buffer[pos].split("\\|");
                    String name = record[1];
                    readIndexFileContents("primary", primaryIndexmap);
                    int offset = Integer.parseInt(primaryIndexmap.get(name));
                    System.out.println(offset);
                    RandomAccessFile index = new RandomAccessFile("MasterFile.txt","rw");
                    index.seek(offset);
                    String Line = index.readLine();
                    System.out.println(Line);
                    for(int j=pos;j<noOfRecords-1;j++)
                    {
                        buffer[j] = buffer[j+1];
                    }
                    noOfRecords--;
                }
                else
                {
                    break;
                }
            }*/
        }
        catch(IOException e)
        {
            System.out.println("Exception");
        }
        
    }
    
    public int BinarySearchOnIndex(String buffer[], int low, int high, String monthI, String dayI)
    {
        String MonthF,dayF;
        int flag = 0;
        while(low<=high)
        {
            int mid = (low+high)/2;
            String Date[] = buffer[mid].split(" ");
            MonthF = Date[1];
            dayF = Date[2];
            /*Time = Date[3];
            String hm[] = Time.split(":");
            timeF = hm[0] + hm[1];*/
            String monthF = assignNumbersToMonths(MonthF);
            if(monthI.equals(monthF) && dayI.equals(dayF))
            {
                flag = 1;
                return mid;
            }
            else if(monthI.equals(monthF) && dayI.compareTo(dayF)>0)
            {
                high = mid-1;
            }
            else
            {
                low = mid+1;
            }
        }
        return -1;
    }
    
    public String assignNumbersToMonths(String MonthF)
    {
        String monthf = "";
        if(MonthF.equals("Jan"))
            monthf = "1";
        else if(MonthF.equals("Feb"))
            monthf = "2";
        else if(MonthF.equals("Mar"))
            monthf = "3";
        else if(MonthF.equals("Apr"))
            monthf = "4";
        else if(MonthF.equals("May"))
            monthf = "5";
        else if(MonthF.equals("Jun"))
            monthf = "6";
        else if(MonthF.equals("July"))
            monthf = "7";
        else if(MonthF.equals("Aug"))
            monthf = "8";
        else if(MonthF.equals("Sept"))
            monthf = "9";
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
    //Scanner in = new Scanner(System.in);
    FileStructure user = new FileStructure("Sanath");
    //System.out.println(user.count);
    user.SearchByDate("4","12");
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
