/*
This file is used to bulk upload files in a certain folder to Filenet usng java API. 
The script will pickup all files in a folder and upload to a certain folder in Filenet.
To run, it requires one argument. It'll be the transfer user. Based on the transfer user, it'll decide what folders need to be checked and where to upload. 
java -classpath ".:/data/Jace.jar:/data/log4j.jar:/opt/software/upload:" BulkUploading asp2
Required fields:
a) URL for establishing connection. Get this from the Navigator Admin Area -> Repositories.  It will look like this
http://blogs1.fyre.ibm.com:9080/wsi/FNCEWS40MTOM
b) Object Store name. Get this from the Repositories config area as well.
P8ObjectStore
c) A login id and password to connect to Filenet Navigator. I used P8Admin. But I think can create others and use.
d) The  top level folder to where to upload files. This will be the folder created in the top level in the repository. 
Folder1

*/
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.*;
import com.filenet.api.util.UserContext;
import javax.security.auth.Subject;

class AllVar {
	public static String url="http://blogs1.fyre.ibm.com:9080/wsi/FNCEWS40MTOM";
	public static String login="P8Admin";
	public static String passwd="Admin123";
	public static String ObjectStore="P8ObjectStore";
//	public static String TopFolder="/Folder1";
//	public static String LocalFolder="/data1";
//	Change Folder details in the CheckUserFolder function.
}

class EngineConnection {
    Connection giveConnection()
    {
        Connection connection = Factory.Connection.getConnection(AllVar.url);
        Subject subject =UserContext.createSubject(connection, AllVar.login, AllVar.passwd, null);
        UserContext context=UserContext.get();
        context.pushSubject(subject);
        if(connection!=null)
            return connection;
        return connection;
    }
}

public class BulkUploading {
public static String TopFolder="";
public static String LocalFolder="";

static void CheckUserFolder(String user) {
	if (user.equals("asp1")) {
	   TopFolder="/Folder1";
	   LocalFolder="/data1/asp1";
	} else if (user.equals("asp2")) {
           TopFolder="/Folder2";
           LocalFolder="/data1/asp2";
	} else if (user.equals("asp3")) {
           TopFolder="/Folder3";
           LocalFolder="/data1/asp3";
        }
}

    public static void main(String args[])
    {
        //connect to the server
	CheckUserFolder(args[0]);
        System.out.println("Start: ");
        System.out.println(java.time.LocalTime.now());
	EngineConnection mine=new EngineConnection();
        Connection connection = mine.giveConnection();
        if(connection!=null)
        {
            //give a folder name from there it takes all the data and uploads it into the folder given by you
	     File file=new File(LocalFolder);           
             //System.out.println(file.isDirectory());
             File files_array[]=file.listFiles();
             Folder current_folder=null;
             Domain domain=Factory.Domain.fetchInstance(connection, null, null);
            ObjectStore object_store=Factory.ObjectStore.fetchInstance(domain, AllVar.ObjectStore, null);
	    Folder selected_folder = Factory.Folder.getInstance(object_store,"Folder",TopFolder);
	     //System.out.println(TopFolder);
	     //System.out.println(LocalFolder);
             ContentTransfer content_transfer =null;
             FileInputStream input_data=null;
             Document doc=null;
             ContentElementList content_element_list=null;
             ReferentialContainmentRelationship file2=null;
            for(File f:files_array)
            {
             File file1=new File(f.getAbsolutePath());
            try
            {
              input_data =new FileInputStream(file1);
                //get the instance for documnet to store above input data
              doc =Factory.Document.createInstance(object_store, null);
                //set trasfering method
              content_transfer=Factory.ContentTransfer.createInstance();
                //set the data to this content transfer
              content_transfer.setCaptureSource(input_data);
                //set the mime type
              content_transfer.set_ContentType("application/txt");
                //set a name for document
              content_transfer.set_RetrievalName("Test Practise");
                //create a content element list and add this content transfer data to the list
              content_element_list=Factory.ContentElement.createList();
	     	content_element_list.add(content_transfer);
	      //add the content elements to the current document
              doc.set_ContentElements(content_element_list);
              doc.getProperties().putValue("DocumentTitle",f.getName() );
              doc.set_MimeType("application/txt");
                //check in document
              doc.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MINOR_VERSION);
              doc.save(RefreshMode.REFRESH);
                //set the current document under the sub folder
              file2= selected_folder.file(doc, AutoUniqueName.AUTO_UNIQUE, f.getName(), DefineSecurityParentage.DEFINE_SECURITY_PARENTAGE);
              file2.save(RefreshMode.REFRESH);
	      file1.delete();
	      System.out.println("End: ");
	      System.out.println(java.time.LocalTime.now()); 
            }
            catch(Exception e)
            {
                System.out.println(""+e.getLocalizedMessage());
            }
           
          }   
        }
       
    }
   
}
