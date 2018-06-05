import java.sql.*;
import oracle.jdbc.*;
import java.math.*;
import java.io.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class MainClass {
	public static String sid=null;
	public static String firstname=null;
	public static String lastname=null;
	public static String email =null;
	public static String semester =null;
	public static int year=0;
	public static float gpa = 0;
	public static String status =null;
	public static String dept_code =null;
	public static String class_id =null;
	public static int vcount1=0;
	public static int vcount2=0;
	public static int course_no =0;
	public static boolean hasResult = false;
	public static String option=null;

	public static Scanner sc = new Scanner(System.in);
	public static void main (String args []) throws SQLException
	  {
		  try
			{
			  	boolean continue_flag = true;
				while (continue_flag)
				{
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "pass1234");
				System.out.println("Connected to Student Database");
				//Menu
				System.out.println("\n\n__________________________________________________________");
			 	System.out.println("Menu Options:");	
	   			System.out.println("1. Show all students");
				System.out.println("2. Show all courses");
				System.out.println("3. Show all prerequisites");
				System.out.println("4. Show all classes");
	      		System.out.println("5. Show all enrollments");
				System.out.println("6. Add Student");
				System.out.println("7. Show student's courses");
				System.out.println("8. Show prerequisite");
	      		System.out.println("9. Show students in class");
				System.out.println("10. Enroll student");
				System.out.println("11. Drop student");
	      		System.out.println("12. Delete student");
				System.out.println("13. See logs");
				System.out.println("Enter your selection and press ENTER");
				System.out.println("_________________________________________________________");

				//Read data
				BufferedReader readOption;
				String option_number;
				int number;
				readOption = new BufferedReader(new InputStreamReader(System.in));
				option_number = readOption.readLine();
				number = Integer.parseInt(option_number);
				
				CallableStatement cs = null;
				ResultSet rs = null;
				Statement stmt = null;
				PreparedStatement insert=null,insert1 =null,insert2=null;
				
				//Parition to approporate section
				switch (number)
				{
					case 1:
					 cs = conn.prepareCall("begin pack_display.show_students(?); end;");
					 cs.registerOutParameter(1, OracleTypes.CURSOR);
					 cs.execute();
				     rs = (ResultSet)cs.getObject(1);
				     System.out.println("sid"+"\t"+"firstname"+"\t"+"lastname" +  "\tstatus"  + "\t\tgpa" + "\t\temail");
				     System.out.println("================================================================================");
				        // print the results
				        while (rs.next()) {
				            System.out.println(rs.getString(1) + "\t" +
				                rs.getString(2) + "\t\t" + rs.getString(3) + "\t\t"+
				                rs.getString(4) + 
				                "\t\t" + rs.getFloat(5) + "\t\t" +
				                rs.getString(6));
				        }
					 	rs.close();
						cs.close();
						break;
					case 2:
						 cs = conn.prepareCall("begin pack_display.show_courses(?); end;");
						 cs.registerOutParameter(1, OracleTypes.CURSOR);
						 cs.execute();
					     rs = (ResultSet)cs.getObject(1);
					     System.out.println("Dept Code"+"\t"+"Course No"+"\t"+"Title");
					        // print the results
					     System.out.println("===================================================================");
					        while (rs.next()) {
					            System.out.println(rs.getString(1) + "\t\t" +
					                rs.getInt(2) + "\t\t" + rs.getString(3));
					        }
						 	rs.close();
							cs.close();
						break;
					case 3:
						 cs = conn.prepareCall("begin pack_display.show_prerequisites(?); end;");
						 cs.registerOutParameter(1, OracleTypes.CURSOR);
						 cs.execute();
					     rs = (ResultSet)cs.getObject(1);
					     System.out.println("dept_code "+"\tcourse_no "+ "\t" + "pre_dept_code" + "\t"+"pre_course_no ");
					        // print the results
					     System.out.println("===================================================================");
					        while (rs.next()) {
					            System.out.println(rs.getString(1) + "\t\t" +
					                rs.getInt(2) + "\t\t" + rs.getString(3) + "\t\t"
					                + rs.getInt(4));
					        }
						 	rs.close();
							cs.close();
						break;
					case 4:
						 cs = conn.prepareCall("begin pack_display.show_classes(?); end;");
						 cs.registerOutParameter(1, OracleTypes.CURSOR);
						 cs.execute();
					     rs = (ResultSet)cs.getObject(1);
					     System.out.println("classid"+"\t\tdept_code"+"\tcourse_no" + "\t" +"\tsect_no" +"\t\t" + "\tyear" + "\t\t" +"\tsemester" + "\t\t" +"limit" + "\t\t" +"class_size");
					        // print the results
					     System.out.println("===========================================================================================================================================================");
					        while (rs.next()) {
					            System.out.println(rs.getString(1) + "\t\t" +
					                rs.getString(2) + "\t\t" + rs.getInt(3) + "\t\t\t" +
					                rs.getInt(4) + "\t\t\t" +rs.getInt(5) +"\t\t\t" + rs.getString(6)+"\t\t\t" + rs.getInt(7) + "\t\t\t" +rs.getInt(8));
					        }
						 	rs.close();
							cs.close();
						break;
					case 5:
						 cs = conn.prepareCall("begin pack_display.show_enrollments(?); end;");
						 cs.registerOutParameter(1, OracleTypes.CURSOR);
						 cs.execute();
					     rs = (ResultSet)cs.getObject(1);
					     System.out.println("sid"+"\t\t"+"classid"+"\t\tlgrade");
					        // print the results
					     System.out.println("========================================");
					        while (rs.next()) {
					            System.out.println(rs.getString(1) + "\t\t" +
					                rs.getString(2) + "\t\t" + rs.getString(3));
					        }
						 	rs.close();
							cs.close();
						break;
					case 6:
						cs = conn.prepareCall("begin pack_display.add_student(?,?,?,?,?,?); end;");
						System.out.println("Enter sid");
						 sid = sc.nextLine();
						 System.out.println("Enter firstname");
						  firstname = sc.nextLine();
						 System.out.println("Enter lastname");
						  lastname = sc.nextLine();
						 System.out.println("Enter status");
						  status = sc.nextLine();
						 System.out.println("Enter gpa");
						  gpa = Float.parseFloat(sc.nextLine());
						 System.out.println("Enter email");
						  email = sc.nextLine();
						cs.setString(1, sid);
						cs.setString(2, firstname);
						cs.setString(3,lastname);
						cs.setString(4,status);
						cs.setFloat(5,gpa);
						cs.setString(6,email);
						cs.execute();
						System.out.println("Student Added Successfully");
						cs.close();
						break;
					case 7:	
						 System.out.println("Enter sid");
						 sid = sc.nextLine();
						 insert1=conn.prepareStatement("select sid from students where sid= ?");
						 insert1.setString(1, sid);
						 rs=insert1.executeQuery();
						 hasResult=rs.next();
						 if(hasResult==false)
						 {
							 System.out.println("The sid is invalid");
							 rs.close();
							 break;
						 }
						 insert =conn.prepareStatement("select sid from Enrollments where sid= ?");
						 insert.setString(1, sid);
						 rs = insert.executeQuery();
						 hasResult = rs.next();
						 if(hasResult==false)
						 {
							 System.out.println("The student has not taken any course");
							 rs.close();
							 break;
						 }
						 else{
							 hasResult=true;
							cs = conn.prepareCall("begin pack_display.stud_reg(?,?); end;");
							cs.setString(1, sid);
							cs.registerOutParameter(2, OracleTypes.CURSOR);
							cs.execute();
							rs = (ResultSet)cs.getObject(2);
							 while (rs.next()) {
						            System.out.println(rs.getString(1) + "\t" +
						                rs.getString(2) + "\t" + rs.getString(3) + 
						                rs.getString(4) + 
						                "\t" + rs.getString(5) + "\t"  + rs.getString(6)+ rs.getInt(7)+
						                rs.getString(8));
						        }
						 }
					     //cs.registerOutParameter(2, OracleTypes.CURSOR);
					    // cs.execute();
					     //rs = (ResultSet)cs.getObject(2);
					    

					     //Close connections
					     	
							rs.close();
							cs.close();
						
						break;
					case 8:
						cs = conn.prepareCall("begin pack_display.ret_pre_reqs(?,?,?); end;");
						 System.out.println("Enter DEPT CODE");
						 dept_code = sc.nextLine();
						 System.out.println("Enter Course No");
						 course_no = sc.nextInt();
						 cs.setString(1, dept_code);
						 cs.setInt(2, course_no);
					     cs.registerOutParameter(3, OracleTypes.CURSOR);
					     cs.execute();
					     rs = (ResultSet)cs.getObject(3);
					     System.out.println("pre_code" +"\t" + "\tpre_course_no");
					     System.out.println("===================================");
					     while (rs.next()) {
					            System.out.println(rs.getString(1) + "\t\t\t" +
					                rs.getInt(2));
					        }

					     //Close connections
					     	
							rs.close();
							cs.close();
						break;
					case 9:
						 System.out.println("Enter Class ID");
						 class_id = sc.nextLine();
						 insert=conn.prepareStatement("select classid,class_size from classes where classid= ?");
						 insert.setString(1, class_id);
						 rs=insert.executeQuery();
						 hasResult=rs.next();
						 if(hasResult==false)
						 {
							 System.out.println("The class id is invalid");
							 rs.close();
							 break;
						 }
						 else
						 {
							 if(rs.getInt(2)==0){
								 System.out.println("No student is enrolled in the class.");
								 rs.close();
								 break;
							 }
							 else{
								 cs = conn.prepareCall("begin pack_display.list_class(?,?); end;");
								 cs.setString(1, class_id);
							     cs.registerOutParameter(2, OracleTypes.CURSOR);
							     cs.execute();
							     rs = (ResultSet)cs.getObject(2);
							     while (rs.next()) {
							            System.out.println(rs.getString(1) + "\t" +
								                rs.getString(2) + "\t" + rs.getString(3) + 
								                rs.getString(4) + 
								                "\t" + rs.getInt(5) + "\t"  + rs.getString(6));
							        }
							     rs.close();
							     cs.close();
							 }
						 }						

					     //Close connections
					     	
//							rs.close();
//							cs.close();
						break;
					case 10:
						System.out.println("Enter SID");
						sid=sc.nextLine();
						insert=conn.prepareStatement("select sid from students where sid= ?");
						insert.setString(1, sid);
						rs=insert.executeQuery();
						hasResult=rs.next();
						 if(hasResult==false)
						 {
							 System.out.println("The sid is invalid");
							 rs.close();
							 break;
						 }
						System.out.println("Enter Class ID");
						 class_id = sc.nextLine();
						 insert=conn.prepareStatement("select classid,class_size,limit from classes where classid= ?");
						 insert.setString(1, class_id);
						 rs=insert.executeQuery();
						 hasResult=rs.next();
						 if(hasResult==false)
						 {
							 System.out.println("The class id is invalid");
							 rs.close();
							 break;
						 }
						 else{
							 if(rs.getInt(2)==rs.getInt(3))
							 {
								 System.out.println("The class is closed");
								 break;
							 }
							 insert=conn.prepareStatement("select sid from enrollments where sid= ? and classid= ?");
							 insert.setString(1, sid);
							 insert.setString(2, class_id);
							 rs=insert.executeQuery();
							 hasResult=rs.next();
							 if(hasResult==true)
							 {
								 System.out.println("The student is already enrolled in the class.");
								 rs.close();
								 break;
							 }
							 insert=conn.prepareStatement("select year from classes where classid = ?");
							 insert.setString(1, class_id);
							 rs=insert.executeQuery();
							 hasResult=rs.next();
							 year=rs.getInt(1);
							 insert=conn.prepareStatement("select semester from classes where classid = ?");
							 insert.setString(1, class_id);
							 rs=insert.executeQuery();
							 hasResult=rs.next();
							 semester=rs.getString(1);
							 insert=conn.prepareStatement("select count(*) as count from enrollments e join classes c on c.classid = e.classid where e.sid = ? and c.semester = ? and c.year = ?");
							 insert.setString(1, sid);
							 insert.setString(2, semester);
							 insert.setInt(3, year);
							 rs=insert.executeQuery();
							 hasResult=rs.next();
							 vcount1=rs.getInt(1);
								 if(vcount1==1)
								 {
								 }
									 if(vcount1==2) {
										 System.out.println("You are overloaded");
									 }
										 if(vcount1==3){
											 System.out.println("Students cannot be enrolled in more than three classes in the same semester.");
											 break;
										 }
							 			cs = conn.prepareCall("begin pack_display.enroll_student(?,?,?); end;");
										cs.setString(1, sid);
										cs.setString(2, class_id);
										cs.registerOutParameter(3, OracleTypes.CURSOR);
								        cs.execute();
								        rs=(ResultSet)cs.getObject(3);
								        hasResult=rs.next();
									    if(rs.getInt(1)==0)
									        {
									        	hasResult=false;
									        	System.out.println("Prerequisite courses have not been completed.");
									        	rs.close();
									        	cs.close();
									        	break;
									        }
								        else{
								        	 	System.out.println(sid+" has been successfully enrolled to " + class_id);
										        rs.close();
										        cs.close();
										        break;
								        }
						 			}
					case 11:
						System.out.println("Enter SID");
						sid=sc.nextLine();
						insert=conn.prepareStatement("select sid from students where sid= ?");
						insert.setString(1, sid);
						rs=insert.executeQuery();
						hasResult=rs.next();
						 if(hasResult==false)
						 {
							 System.out.println("The sid is invalid");
							 rs.close();
							 break;
						 }
						System.out.println("Enter Class ID");
						 class_id = sc.nextLine();
						 insert=conn.prepareStatement("select class_size,classid from classes where classid= ?");
						 insert.setString(1, class_id);
						 rs=insert.executeQuery();
						 hasResult=rs.next();
						 if(hasResult==false)
						 {
							 System.out.println("The class id is invalid");
							 rs.close();
							 break;
						 }
						 else{
							 vcount1=rs.getInt(1);
							 rs.close();
							 insert=conn.prepareStatement("select sid from enrollments where sid= ? and classid= ?");
							 insert.setString(1, sid);
							 insert.setString(2, class_id);
							 rs=insert.executeQuery();
							 hasResult=rs.next();
							 if(hasResult==false)
							 {
								 System.out.println("The student is not enrolled in the class.");
								 rs.close();
								 break;
							 }
							 else{
								 hasResult=false;
								 rs.close();
								 insert=conn.prepareStatement("select count(*) from enrollments where sid=?");
								 insert.setString(1, sid);
								 rs=insert.executeQuery();
								 hasResult=rs.next();
								 if(hasResult==true)
								 {
										vcount2=rs.getInt(1);
									 	cs = conn.prepareCall("begin pack_display.drop_student(?,?,?); end;");
										cs.setString(1, sid);
										cs.setString(2, class_id);
										cs.registerOutParameter(3, OracleTypes.CURSOR);
								        cs.execute();
								        rs=(ResultSet)cs.getObject(3);
								        hasResult=rs.next();
								        if(rs.getInt(1)==1)
								        {
								        	hasResult=false;
								        	System.out.println("The drop is not permitted because another class uses it as a prerequisite");
								        	rs.close();
								        	cs.close();
								        	break;
								        }
								        else{
								        	 System.out.println("SID :"+sid+"has been removed successfully");
										        if(vcount1==1){
										        	vcount1=0;
										        	System.out.println("The class now has no students");
										        }
										        if(vcount2==1)
										        {
										        	vcount2=0;
										        	System.out.println("This student is not enrolled in any classes");
										        }
										        rs.close();
										        cs.close();
								        }
								       
								 }
								
							 }
							 
						 }
						break;
					case 12:
						System.out.println("Enter SID");
						sid=sc.nextLine();
						insert=conn.prepareStatement("select sid from students where sid= ?");
						insert.setString(1, sid);
						rs=insert.executeQuery();
						hasResult=rs.next();
						 if(hasResult==false)
						 {
							 System.out.println("The sid is invalid");
							 rs.close();
							 break;
						 }
						 else{
								cs = conn.prepareCall("begin pack_display.delete_student(?); end;");
								cs.setString(1, sid);
						        cs.execute();
						        System.out.println("SID :"+sid+"has been removed successfully");
						        rs.close();
						        cs.close();
						 }
						
						break;
					case 13:
						 cs = conn.prepareCall("begin pack_display.show_logs(?); end;");
						 cs.registerOutParameter(1, OracleTypes.CURSOR);
						 cs.execute();
					     rs = (ResultSet)cs.getObject(1);
					     System.out.println("logid"+"\t"+"who"+"\t"+"time" +"\t\t"+  "\ttable_name"  +"\t"+ "\toperation" +"\t "+ "key_value");
					        // print the results
					        while (rs.next()) {
					            System.out.println(rs.getInt(1) + "\t" +
					                rs.getString(2) + "\t" + rs.getString(3) + "\t"+
					                rs.getString(4)  +"\t\t"+ rs.getString(5) + "\t\t "+rs.getString(6));
					        }
					        conn.close();
						 	rs.close();
							cs.close();
						break;
					default:
						System.out.println("\n Invalid Selection,Please Try again.");
						break;
				}	
			//Close connection to Oracle server
			conn.close();
			boolean x=true;
			while(x){
				System.out.println("Do you want to continue (y/n)");
				option=sc.nextLine();
				if(option.contains("y")||option.contains("Y")){
					continue_flag=true;
					x=false;
				}
				else if(option.contains("n")||option.contains("N")){
					continue_flag=false;
					x=false;
				}
				else{
					System.out.println("Try Again!");
					x=true;
				}
			}
			
			}
			}
			catch (SQLException ex) { System.out.println ("\n*** SQLException caught ***\n"); System.out.println(ex.getMessage());}
			catch (Exception e) {System.out.println ("\n*** other Exception caught ***\n");}
	  }
}
