# Deploy API as a Windows Service with Winsw

We are going to use Winsw to run an API developed in Spring Boot. For this example our API offers the CRUD operations on champions from the database. Let's assume that by requirements 
the delete operation is performed as a logic delet, which means that the database keeps the record and only changes the attribute "active" to false, thus it's not allowed to remove records
from the database.
The API counts with a service that creates backups on every update, checks periodically the database state and if it detects that the database was corrupted under the statement 
mentioned before, it will fully restore the database.

In this occation let's see the most relevant methods (you can check all the project in the repository). Also remember that configuring the environment variables will help you
avoid absolute paths

Create backup:
```java
public void createBackup(){
        String mysqldump = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump";
        ProcessBuilder processBuilder = getProcessBuilder(mysqldump, OUTPUT_REDIRECT);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Backup successful!");
            } else {
                System.out.println("Backup failed with exit code " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error occurred during backup: " + e.getMessage());;
        }
    }
```

Restore database:
```java
public void restoreDB(){
        String mysql = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql";
        ProcessBuilder processBuilder = getProcessBuilder(mysql, INPUT_REDIRECT);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Database restored successfully!");
            } else {
                System.out.println("Restore failed with exit code " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Error occurred during restore: " + e.getMessage());;
        }
    }
```

Process builder:
```java
private static ProcessBuilder getProcessBuilder(String commandToExecute, int redirectionType) {

        String dbUser = "root";
        String dbPassword = "1234";
        String dbName = "status_service_db";
        String backupPath = "C:\\Users\\kim_j\\Desktop\\service_backup.sql";

        List<String> command = new ArrayList<>();

        command.add(commandToExecute);
        command.add("-u" + dbUser);
        command.add("-p" + dbPassword);
        command.add(dbName);

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        if(redirectionType == OUTPUT_REDIRECT){
            processBuilder.redirectOutput(new File(backupPath));
        }
        else{
            processBuilder.redirectInput(new File(backupPath));
        }

        return processBuilder;
    }
```

Scheduled database state check
```java
@Scheduled(fixedDelay =  5000)
    public void monitoringDatabase(){

        if(dbCorrupted()){
            restoreDB();
            System.out.println(LocalDateTime.now() + " - database restored succesfully!");
        }
    }
```

We also need to configure the pom.xml by adding the tags `finalName` and `executable` the next way:

![pom tags](https://github.com/CristopherLodbrok117/api-deployment-as-windows-service/blob/23817d66a4a38dda3a96299d1a0d562187d95983/Screenshots/0%20-%20define%20app%20name%20and%20executable.png)

<br>

## Download 
Once you have your application we can continue to prepare all the files we require. Firstly we download the last release of Winsw (in this example we use v2.12.0), check all the 
[releases](https://github.com/winsw/winsw/releases "Winsw releases"). Only the next two files are required to download:

![download winsw](https://github.com/CristopherLodbrok117/api-deployment-as-windows-service/blob/23817d66a4a38dda3a96299d1a0d562187d95983/Screenshots/3%20-%20download%20winsw.png)

For more configuration options or information you can check the Winsw repository [here](https://github.com/winsw/winsw "Winsw repository")

Now lets obtain the jar file. Firstly we execute the Maven tasks `clean` and `install`:

![Maven tasks](https://github.com/CristopherLodbrok117/api-deployment-as-windows-service/blob/23817d66a4a38dda3a96299d1a0d562187d95983/Screenshots/1%20-%20Maven%20tasks.png)

Then we go to the target folder and copy the jar file:

![jar file](https://github.com/CristopherLodbrok117/api-deployment-as-windows-service/blob/23817d66a4a38dda3a96299d1a0d562187d95983/Screenshots/2%20-%20Copy%20jar%20file.png)

<br>

## Configuration

Now we have all the files required to continue. The next step is to create a folder in your file explorer, in this example our route is the next one "".
Here we paste our jar file and the downloaded files. As you can see there are more files, but they are generated once we configure and install the service. Initially we only have the 
next ones:

* `windows_service_spring.jar`
* `WinSW.NET4.exe`
* `GameControl` (originally it was sample-minimal.xml, but we need to rename the same as the exe)


![Captura 1](the link here)
Create backup
```java
```
