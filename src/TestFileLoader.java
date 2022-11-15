import org.apache.commons.csv.CSVFormat;//apache commons to read csv files
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

class TestFileLoader {

    ArrayList<String> vinArray;

    String file;

    public TestFileLoader (String f) throws IOException {
        this.file = f;
        load();
    }

    public void load() throws IOException {//this is essentially the same loader as "VINLoader.java", the difference is that we are only interested in the VIN portion of the test file
        String fileReader = file;
        vinArray = new ArrayList<>();
        try(Reader reader = Files.newBufferedReader(Paths.get(fileReader));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());){
            for(CSVRecord record : csvParser){
                String vin = record.get("VIN");
                vinArray.add(vin);
            }
        }
    }

    ArrayList<String> getTestMap(){
        return vinArray;
    }//returns only the VINs in the test file to be used

}
