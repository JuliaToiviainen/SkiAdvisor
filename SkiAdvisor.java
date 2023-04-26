import java.sql.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;


public class SkiAdvisor implements AutoCloseable {

    // Default connection information (most can be overridden with command-line arguments)
    private static final String DB_NAME = "jkt0107_SkiAdvisor";
    private static final String DB_USER = "token_bf60";
    private static final String DB_PASSWORD = "KpdrIqagAaOyscNs";

    private static final String QUERY = 
          "SELECT name, difficulty_level, slope_quantity, location FROM SkiArea;\n";

    private static final String QUERY2 = 
          "SELECT User.name, User.skill_level, User.age, SkiArea.name, Rating.rating, Rating.slope_rating, Rating.group_rating FROM User \n"
          +"INNER JOIN Rating ON Rating.user_name = User.name \n"
          +"INNER JOIN SkiArea ON SkiArea.id = Rating.skiArea_id \n"
          +"WHERE User.name LIKE ?;\n";
 
    private static final String QUERY3 = 
          "INSERT INTO Rating(skiArea_id, user_name, rating, slope_rating, group_rating)\n"
          +"VALUES(?, ?, ?, ?, ?); \n";
 
    private static final String QUERY4 = 
          "SELECT id, name FROM SkiArea;\n";
    
    private static final String QUERY5 = 
          "SELECT SkiArea.name, AVG(Rating.rating) as average, AVG(Rating.slope_rating) as slope FROM SkiArea \n"
          +"INNER JOIN Rating ON SkiArea.id = Rating.skiArea_id \n"
          +"WHERE SkiArea.name = ?;\n";
    
    private static final String QUERY6 =
          "INSERT INTO User(name, password, skill_level, age)\n"
          +"VALUES (?, ?, ?, ?); \n";
          
    private static final String QUERY7 =
          "DELETE FROM User\n"
          +"WHERE password = ?\n"
          +"AND name = ?;\n";
          
    private static final String QUERY8 =
          "SELECT group_rating FROM Rating \n"
          +"INNER JOIN SkiArea ON SkiArea.id = Rating.skiArea_id \n"
          +"WHERE SkiArea.name = ?; \n";
          
    private static final String QUERY9 =
          "SELECT name FROM User\n"
          +"WHERE password = ?; \n";
              
          
    // Connection information to use
    private final String dbHost;
    private final int dbPort;
    private final String dbName;
    private final String dbUser, dbPassword;

    private Connection connection;
    private PreparedStatement query;
    private PreparedStatement query2;
    private PreparedStatement query3;
    private PreparedStatement query4;
    private PreparedStatement query5;
    private PreparedStatement query6;
    private PreparedStatement query7;
    private PreparedStatement query8;
    private PreparedStatement query9;


    public SkiAdvisor(String dbHost, int dbPort, String dbName,
            String dbUser, String dbPassword) throws SQLException {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;

        connect();
    }

    private void connect() throws SQLException {
        final String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
                dbHost, dbPort, dbName,
                dbUser, dbPassword
        );

        this.connection = DriverManager.getConnection(url);

        this.query = this.connection.prepareStatement(QUERY);
        this.query2 = this.connection.prepareStatement(QUERY2);
        this.query3 = this.connection.prepareStatement(QUERY3);
        this.query4 = this.connection.prepareStatement(QUERY4);
        this.query5 = this.connection.prepareStatement(QUERY5);
        this.query6 = this.connection.prepareStatement(QUERY6);
        this.query7 = this.connection.prepareStatement(QUERY7);
        this.query8 = this.connection.prepareStatement(QUERY8);
        this.query9 = this.connection.prepareStatement(QUERY9);



    }


    public void runApp() throws SQLException {
        Scanner in = new Scanner(System.in);
        
        while (true) {
            System.out.println("-----------------------------");
            System.out.println(" Welcome to the Ski Advisor!");
            System.out.println("-----------------------------");
            System.out.println("Please write the corresponding letter of what you want to do:");
            System.out.println("Create user - press A");
            System.out.println("See all info - press B");
            System.out.println("Give a rating - press C");
            System.out.println("See ratings for a specific Ski Area - press D");
            System.out.println("Searh users - press E");
            System.out.println("Delete user - press F");

            
            
            String line = in.nextLine();
            System.out.println("");
            boolean count = true;
            
            if (line.equals("E") || line.equals("e")){
               while (count){
                  count = false;
                  System.out.print("\nSearh a username? (hit enter to go back):\n");
                  String listLine = in.nextLine();
               
                  if (listLine.isBlank())
                     break;
                       
                 searchUser(listLine);
               }
            }

            else if (line.equals("B") || line.equals("b")){
               while (count){
                  count = false;
                  allInfo(line);
                  String listLine = in.nextLine();
                        break;
               }

            }
            else if (line.equals("C") || line.equals("c")){
              ResultSet results = query4.executeQuery();
               while (count){
                  count = false;
                  System.out.print("\nWhat Ski Area would you like to rate press the id number of it? (hit Enter to go back)\n");
                  int resultCount = 0;
                  while (results.next()) {
                     ++resultCount;
                     String name = results.getString("SkiArea.name");
                     int id = results.getInt("SkiArea.id");
                     System.out.printf("id: %d, %s \n",id, name);
                  }
                  String listLine = in.nextLine();
                     
                     if (listLine.isBlank())
                        break;
                  addRating(listLine);
               
               
            }
           }
            else if (line.equals("D") || line.equals("d")){
               ResultSet results = query4.executeQuery();
               while (count){
                  count = false;
                  System.out.print("\nWhat Ski Area you would like to see? (hit enter to go back):\n");

                  int resultCount = 0;
                  while (results.next()) {
                     ++resultCount;
                     String name = results.getString("SkiArea.name");
                     System.out.printf("%s \n",name);
                  }
                  String listLine = in.nextLine();
               
                  if (listLine.isBlank())
                     break;
                       
                 showSkiArea(listLine);
               }
            }
           
           else if (line.equals("A") || line.equals("a")){
               while (count){
                  count = false;
                  System.out.print("\nGreat to have you as a new user!\n");
                  System.out.print("\n What would you like your username to be? (hit enter to go back):\n");

                  String listLine = in.nextLine();
               
                  if (listLine.isBlank())
                     break;
                       
                 createAccount(listLine);
               }
           }
          else if (line.equals("F") || line.equals("f")){
               while (count){
                  count = false;
                  System.out.print("\nI'm sorry to hear you want to delete your user\n");
                  System.out.print("\n To delete your user I need you username? (hit enter to go back):\n");

                  String listLine = in.nextLine();
               
                  if (listLine.isBlank())
                     break;
                       
                 deleteAccount(listLine);
               }
           } 
       }
    }


    public void searchUser(String name) throws SQLException {

        query2.setString(1, name);
        ResultSet results = query2.executeQuery();
    
        int resultCount = 0;
        while (results.next()) {
            ++resultCount;
            String uName = results.getString("User.name");
            String skill = results.getString("User.skill_level");
            int age = results.getInt("User.age");
            double rate = results.getDouble("Rating.rating");
            double slopes = results.getDouble("Rating.slope_rating");
            String name2 = results.getString("SkiArea.name");
            String group = results.getString("Rating.group_rating");
            if (resultCount == 1)
               System.out.printf("By user %s\n Skill level: %s\n Age: %d\n",uName, skill, age);
            System.out.printf("----\n For %s:\n General rating: %.1f\n Slope rating: %.1f\n Comments: %s\n\n", name2, rate, slopes, group);
        }

    }
    
    public void createAccount(String name) throws SQLException {
        Scanner in = new Scanner(System.in);
   
        System.out.println("Please give your password? \n");
        String password = in.nextLine();
        System.out.println("What is you skill level in skiing (expert/intermediate/beginner)? \n");
        String skill = in.nextLine();
        System.out.println("How old are you? \n");
        String age = in.nextLine();
        query6.setString(1, name);
        query6.setString(2, password);
        query6.setString(3, skill);
        query6.setString(4, age);
        query6.execute();
        System.out.println("Your account has been created.\n\n");
    }
    
    public void deleteAccount(String name) throws SQLException {
        Scanner in = new Scanner(System.in);
         
       
        System.out.println("And please give your password?\n");
        String password = in.nextLine();
        query7.setString(1, password);
        query7.setString(2, name);
        query7.execute();
        System.out.println("Your account has been deleted.\n\n");
    }

    public void allInfo(String name) throws SQLException {

        ResultSet results = query.executeQuery();

        int resultCount = 0;

        while (results.next()) {
            ++resultCount;
            String name2 = results.getString("SkiArea.name");
            int difficulty = results.getInt("SkiArea.difficulty_level");
            int slopes = results.getInt("SkiArea.slope_quantity");
            String location = results.getString("SkiArea.location");
            System.out.printf("----\nSki Area: %s\n Difficulty level: %d\n Slope quantity: %d\n Location: %s\n\n",name2, difficulty, slopes, location); 
        }
    }
    
    public void addRating(String name) throws SQLException {
        Scanner in = new Scanner(System.in);
   
        query3.setString(1, name);
        System.out.println("Please give your username? \n");
        String uName = in.nextLine();
        query3.setString(2, uName);
        System.out.println("And you password? \n");
        String password = in.nextLine();
               
        System.out.println("What general rating would you give (1-10, 10 being the best)? \n");
        String answer1 = in.nextLine();
        System.out.println("What slope rating would you give (1-10, 10 being the best)? \n");
        String answer2 = in.nextLine();
        System.out.println("Additional comments for this Ski Area? \n");
        String answer3 = in.nextLine();
        query3.setString(3, answer1);
        query3.setString(4, answer2);
        query3.setString(5, answer3);

        query3.execute();
    }
    
    public void showSkiArea(String name) throws SQLException {
        query5.setString(1, name);
        ResultSet results = query5.executeQuery();

        int resultCount = 0;

        while (results.next()) {
            ++resultCount;
            double average = results.getDouble("average");
            double slopes = results.getDouble("slope");
            String name2 = results.getString("SkiArea.name");
            System.out.printf("----\nFor %s:\n Average rating: %.1f\n Average rating for slopes: %.1f\n", name, average, slopes); 
        }
        
        System.out.printf(" Comments left by users: \n");
        query8.setString(1, name);
        ResultSet results2 = query8.executeQuery();
        while (results2.next()) {
            ++resultCount;
            String rating = results2.getString("Rating.group_rating");
            System.out.printf("  %s\n", rating); 
        } 
    }


    @Override
    public void close() throws SQLException {
        connection.close();
    }

    /**
     * Entry point of the application. Uses command-line parameters to override database
     * connection settings, then invokes runApp().
     */
    public static void main(String... args) {
        // Default connection parameters (can be overridden on command line)
        Map<String, String> params = new HashMap<>(Map.of(
            "dbname", "" + DB_NAME,
            "user", DB_USER,
            "password", DB_PASSWORD
        ));

        boolean printHelp = false;

        // Parse command-line arguments, overriding values in params
        for (int i = 0; i < args.length && !printHelp; ++i) {
            String arg = args[i];
            boolean isLast = (i + 1 == args.length);

            switch (arg) {
            case "-h":
            case "-help":
                printHelp = true;
                break;

            case "-dbname":
            case "-user":
            case "-password":
                if (isLast)
                    printHelp = true;
                else
                    params.put(arg.substring(1), args[++i]);
                break;

            default:
                System.err.println("Unrecognized option: " + arg);
                printHelp = true;
            }
        }

        // If help was requested, print it and exit
        if (printHelp) {
            printHelp();
            return;
        }

        // Connect to the database. This use of "try" ensures that the database connection
        // is closed, even if an exception occurs while running the app.
        try (DatabaseTunnel tunnel = new DatabaseTunnel();
             SkiAdvisor app = new SkiAdvisor(
                "localhost", tunnel.getForwardedPort(), params.get("dbname"),
                params.get("user"), params.get("password")
            )) {
            // Run the interactive mode of the application.
            app.runApp();
        } catch (IOException ex) {
            System.err.println("Error setting up ssh tunnel.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.err.println("Error communicating with the database (see full message below).");
            ex.printStackTrace();
            System.err.println("\nParameters used to connect to the database:");
            System.err.printf("\tSSH keyfile: %s\n\tDatabase name: %s\n\tUser: %s\n\tPassword: %s\n\n",
                    params.get("sshkeyfile"), params.get("dbname"),
                    params.get("user"), params.get("password")
            );
            System.err.println("(Is the MySQL connector .jar in the CLASSPATH?)");
            System.err.println("(Are the username and password correct?)");
        }
        
    }

    private static void printHelp() {
        System.out.println("Accepted command-line arguments:");
        System.out.println();
        System.out.println("\t-help, -h          display this help text");
        System.out.println("\t-dbname <text>     override name of database to connect to");
        System.out.printf( "\t                   (default: %s)\n", DB_NAME);
        System.out.println("\t-user <text>       override database user");
        System.out.printf( "\t                   (default: %s)\n", DB_USER);
        System.out.println("\t-password <text>   override database password");
        System.out.println();
    }
}
