import org.apache.commons.csv.CSVFormat;//apache commons to read csv files
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

class VINLoader {
    HashMap<String,String> vinMap;//hashmap is used to store values

    String file;

    public VINLoader (String f) throws IOException {//when called, this method takes the file and runs load
        this.file = f;
        load();
    }

    public void load() throws IOException {//load reads through the file and puts each part in wmi and manufacturer accordingly
        String fileReader = file;
        vinMap = new HashMap<>();
        try(Reader reader = Files.newBufferedReader(Paths.get(fileReader));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());){
            for(CSVRecord record : csvParser){
                String wmi = record.get("VIN");
                String manufacturer = record.get("Output");
                vinMap.put(wmi, manufacturer);
            }
        }
    }

    HashMap<String,String> getVinMap(){
        return vinMap;
    }//returns our finished vinMap with all information

}
