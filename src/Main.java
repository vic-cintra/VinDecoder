import java.io.*;
import org.apache.commons.codec.digest.DigestUtils;//used for SHA-256 Encryption
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.regex.Pattern;//used to decipher VDS keys

public class Main {
    public static String WMI(String VIN) throws IOException {
        VINLoader wmiID = new VINLoader("src/ResourceFolder/wmiID.csv");//creating a new object for each file
        VINLoader wmiMakeID = new VINLoader("src/ResourceFolder/wmiMakeId.csv");
        VINLoader makeMap = new VINLoader("src/ResourceFolder/Make.csv");
        String makeCode = VIN.substring(0, 3);//the make code, or wmi, is the first 3 digits of a 17 digit vin
        if(wmiID.getVinMap().containsKey(makeCode)) {//checking for wmi code
            String wmi = wmiID.getVinMap().get(makeCode);//gets correct make code from "wmiID.csv"
            String wmiIDtoMake = wmiMakeID.getVinMap().get(wmi);//maps "wmiID.csv" code to the correct make ID in "wmiMakeID.csv"
            String make = makeMap.getVinMap().get(wmiIDtoMake);//maps the correct make ID in "wmiMakeID.csv" to the correct make in"Make.csv"
            return make;
        }else{
            String noWMI = "There is no WMI matching your vin.";//without a wmi, the rest of the code becomes obsolete, so we return a message
            return noWMI;
        }
    }

    public static String VDS(String VIN, String MAKE) throws IOException {//The variable MAKE here is taken from the WMI class
        String model;
        VINLoader Model = new VINLoader("src/ResourceFolder/Model.csv");
        NestedNHTSAFileLoader MakeModel = new NestedNHTSAFileLoader("src/ResourceFolder/MakeVINModel.csv");//nested loader
        for (HashMap.Entry<String, HashMap<String, String>> vehicleNestedMap : MakeModel.getFileMap().entrySet()) {//this loops through the outer hashmap of make
            if (vehicleNestedMap.getKey().equals(MAKE)) {//if make is found, then we choose that values for the outer hashmap
                for (HashMap.Entry<String, String> makeMap : vehicleNestedMap.getValue().entrySet()) {//this then enters the hashmap of the specific make id and accesses all of its appropriate VDS codes. This is important as it avoids comparing different codes across different manufacturers.
                    if (Pattern.matches(makeMap.getKey(), VIN.substring(3, 8))//Some manufacturers use different lengths of VDS to map to their models, so this ensures that each option is searched. A counter does not work here because all options need to be checked, and sometimes a larger VDS length is the correct choice.
                            || Pattern.matches(makeMap.getKey(), VIN.substring(3, 7))
                            || Pattern.matches(makeMap.getKey(), VIN.substring(3, 6))
                            || Pattern.matches(makeMap.getKey(), VIN.substring(3, 5))) {
                        String modelID = makeMap.getValue();
                        model = Model.getVinMap().get(modelID);//maps value taken from "MakeVINModel.csv" to "Model.csv"
                        return model;
                    }
                }
            }
        }
        return null;
    }

    public static String Year(String VIN) throws IOException {
        VINLoader year = new VINLoader("src/ResourceFolder/VehicleYear.csv");
        String yearCode = VIN.substring(9, 10);//the year is determined by one digit and is a very simple map
        String modelYear = year.getVinMap().get(yearCode);
        return modelYear;
    }

    public static void main(String[] args) throws IOException {//this allows the user to choose large batch or small batch decoding
        Scanner input = new Scanner(System.in);
        System.out.println("input or file");
        String console = input.next();
        if(console.equals("input")){
            processFromInput();
        }else{
            processFromFile();
        }
    }

    private static void processFromFile() throws IOException {
        Scanner testFile = new Scanner(new File("src/ResourceFolder/VINTestFile.csv"));//loads vin test file
        testFile.useDelimiter(",");
        System.out.println("This is the original text\n");
        while(testFile.hasNext()) {
            System.out.print(testFile.next());//prints out each line of file
        }
        testFile.close();
        System.out.println("\n");
        System.out.println("Year Make Model VIN Hash");//header for the output
        TestFileLoader toRead = new TestFileLoader("src/ResourceFolder/VINTestFile.csv");
        ArrayList<String> testVals = toRead.getTestMap();
        for(int i = 0; i < testVals.size(); i++){
            String vin = testVals.get(i);
            String sha256hex = DigestUtils.sha256Hex(vin);//hashing for vin
            System.out.println(Year(vin) + " " + WMI(vin) + " " + VDS(vin, WMI(vin)) + " " + vin + " " + sha256hex);
        }
    }

    private static void processFromInput() throws IOException {//this is used for one VIN at a time
        String vin;
        Scanner input = new Scanner(System.in);
        System.out.print("What is your VIN? ");
        vin = input.next();
        String sha256hex = DigestUtils.sha256Hex(vin);//hashing for VIN
        while (vin.length() >= 11) {
            try {
                System.out.println("Your make is: " + WMI(vin));
            } catch (IOException e) {
                System.out.println("File not found");
            }
            try {
                System.out.println("Your car model is: " + VDS(vin, WMI(vin)));//uses WMI(vin) as a make variable
            } catch (IOException e) {
                System.out.println("File not found");
            }
            System.out.println("Your model year is: " + Year(vin));
            if(vin.equals("exit")){
                break;
            }
            System.out.println("Hash code is: " + sha256hex);
            System.out.print("What is your VIN? ");
            vin = input.next();
        }
    }
}