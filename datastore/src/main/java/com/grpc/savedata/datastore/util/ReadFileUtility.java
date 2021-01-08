package com.grpc.savedata.datastore.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utility Class to read CSV and XML files and return JSONArray
 */
@Component
public class ReadFileUtility {

   private static Logger log = LoggerFactory.getLogger(ReadFileUtility.class);

   /**
    * Reads the CSV and converts the data to JSONArray
    *
    * @param filePath Absolute file path
    * @return File data in JSONArray
    * @throws IOException
    */
   public static JSONArray readCsvFile(String filePath) throws IOException, JSONException {
      Path path = Paths.get(filePath);
      if (Files.exists(Paths.get(filePath))) {
         List<String> lines = Files.readAllLines(path);
         String dataString = join(lines, "\n");
         JSONArray jsonArray = CDL.toJSONArray(dataString);
         log.debug(String.valueOf(jsonArray));
         return jsonArray;
      } else {
         return new JSONArray();
      }
   }

   /**
    * Reads the XML and converts the data to JSONArray
    *
    * @param filePath Absolute file path
    * @return File data in JSONArray
    * @throws IOException
    */
   public static JSONArray readXmlFile(String filePath) throws IOException, JSONException {
      if (Files.exists(Paths.get(filePath))) {
         String data = FileUtils.readFileToString(new File(filePath));
         JSONObject jsonObject = XML.toJSONObject(data);
         Object dataObject = jsonObject.getJSONObject("root").get("data");
         JSONArray jsonArray;
         if (dataObject instanceof JSONArray) {
            jsonArray = (JSONArray) dataObject;
         } else {
            jsonArray = new JSONArray();
            jsonArray.put(dataObject);
         }
         log.debug(String.valueOf(jsonArray));
         return jsonArray;
      } else {
         return new JSONArray();
      }
   }

   /**
    * Join List of strings and return concatenated strings
    *
    * @param list  List of strings to be joined
    * @param delim Delimeter for joining list of strings
    * @return Concatenated strings
    */
   private static String join(List<String> list, String delim) {
      StringBuilder sb = new StringBuilder();
      String loopDelim = "";
      for (String s : list) {
         sb.append(loopDelim);
         sb.append(s);
         loopDelim = delim;
      }
      return sb.toString();
   }
}
