package HospiatalManagementSystem;

import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagement {

    private static final String url="jdbc:mysql://localhost:3306/hospital";
    private static final String username="root";
    private static final String password="23sep";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        Scanner scanner=new Scanner(System.in);
        try {
            Connection connection= DriverManager.getConnection(url,username,password);
            Pateints pateints=new Pateints(connection,scanner);
            Doctors doctors=new Doctors(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. ADD PATIENTS ");
                System.out.println("2. VIEW PATIENTS ");
                System.out.println("3. VIEW DOCTORS ");
                System.out.println("4. BOOK APPOINTMENTS ");
                System.out.println("5. EXIT ");
                System.out.println("Enter Your Choice : ");
                int choice=scanner.nextInt();

                switch (choice){
                    case 1:
                        pateints.addPatient();
                        System.out.println();
                        break;

                    case 2:
                        pateints.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        doctors.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        bookAppointement(pateints,doctors,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Enter valid Choice !!!! ");
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointement(Pateints pateints,Doctors doctors,Connection connection,Scanner scanner){
        System.out.println("Enter Patients_ID : ");
        int patients_id=scanner.nextInt();
        System.out.println("Enter Doctors_ID : ");
        int doctors_id=scanner.nextInt();
        System.out.println("Enter Appointment Date (YYYY-MM-DD) : ");
        String appointmentDates=scanner.next();
        if(pateints.getPatientById(patients_id) && doctors.getdoctorsById(doctors_id)){

            if(checkavailability(doctors_id,appointmentDates,connection)){

                String appontmentquery="insert into  appointments(patients_id,doctors_id,appointment_date) values(?,?,?)";

                try {
                    PreparedStatement preparedStatement=connection.prepareStatement(appontmentquery);
                    preparedStatement.setInt(1,patients_id);
                    preparedStatement.setInt(2,doctors_id);
                    preparedStatement.setString(3,appointmentDates);

                    int rowseffected=preparedStatement.executeUpdate();

                    if(rowseffected>0){
                        System.out.println("Appointment Booked ! ");
                    }
                    else{
                        System.out.println("falid To get Appontments ");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor Not available on this Date");
            }
        }else {
            System.out.println("Either Patients or Doctors are not available ! ");
        }
    }

    public static boolean checkavailability(int doctors_id,String appointmentdate,Connection connection ){

        String query="select count(*) from appointments where doctors_id= ? and  appointment_date=? ";

        try{
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1,doctors_id);
            preparedStatement.setString(2,appointmentdate);

            ResultSet resultSet=preparedStatement.executeQuery();

            if(resultSet.next()){
                int count= resultSet.getInt(1);
                if(count==0){
                    return true;
                }
                else{
                    return false;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
