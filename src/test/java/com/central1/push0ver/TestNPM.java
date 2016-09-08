package com.central1.push0ver;

import jdk.nashorn.internal.parser.JSONParser;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;

/**
 * Created by mhanna on 2016-06-30.
 */
public class TestNPM {


    public void parseNpms(String search, String replace, String localRepo) {
        File folder = new File( localRepo );
        File[] listOfFiles = folder.listFiles();
        if ( listOfFiles != null )
        {
            if ( listOfFiles.length < 1 )
            {
                // "No files in " + folder.getPath() + "  Maven Build may not have been ran"
            }
            for ( File g : listOfFiles )
            {
                if ( g.isFile() && "package.json".equals(g.getName()) )
                {
                    try {
                        FileReader reader = new FileReader(localRepo + "/" + g.getName());

                        //JSONParser jsonParser = new JSONParser();
                        //JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

                        //String version = (String) jsonObject.get("version");
                        //JSONObject dependencies = jsonObject.get("dependencies");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            scriptCall( folder );
        }
    }

    public void scriptCall( File localRepo ) {
        //Call npm pack on localRepo
    }

    @Test
    public void testCases()
    {

    }
}
