import org.apache.commons.csv.CSVFormat;//apache commons to read csv files
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

class NestedNHTSAFileLoader {
    HashMap<String, HashMap<String,String>> nestedMap;//a car make will go in the string, on the inner hashmap we put the regex value for the VIN and the VDS number that corresponds
    String file;

    public NestedNHTSAFileLoader (String f) throws IOException {
        this.file = f;
        load();
    }

    public void load() throws IOException {
        HashMap<String,String> modelMap;
        String fileReader = file;
        nestedMap = new HashMap<>();
        try(Reader reader = Files.newBufferedReader(Paths.get(fileReader));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());){
            for(CSVRecord record : csvParser){
                String make = record.get("Make");
                String VIN = record.get("VIN");
                String output = record.get("Output");
                if(nestedMap.containsKey(make)){//if the outer part of the nested hashmap contains the make already, then we know to fill the already created hashmap with VDS codes
                    modelMap = nestedMap.get(make);
                }else{
                    modelMap = new HashMap<>();//creates a new model map, which is the nested portion of the hashmap, for a make
                    nestedMap.put(make,modelMap);//matches a make to a specific model map
                }
                modelMap.put(VIN, output);//fills appropriate nested map with corresponding VINs and VDS
            }
        }
    }

    HashMap<String,HashMap<String,String>> getFileMap(){
        return nestedMap;
    }//returns complete nested map

}
