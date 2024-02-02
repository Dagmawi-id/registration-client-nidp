package io.mosip.registration.controller.device;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class DefaultDeviceDTO {
    static String fileName="default_device.json";
    public String documentScanner="";
    public String fingerSBI="";
    public String faceSBI="";
    public String irisSBI="";
    public String exceptionCapture="";
    public String gpsDevice="";


    public static void saveDefaultDevice(DefaultDeviceDTO device){
        try (FileWriter file = new FileWriter("default_device.json",false)) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString( device );

            file.write(jsonString);
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DefaultDeviceDTO loadDefaultDevice()  {
        DefaultDeviceDTO dto = new DefaultDeviceDTO();
        try {
            File file = new File(fileName);
            if(file.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                dto = mapper.readValue(file, DefaultDeviceDTO.class);
            }
        }catch (Exception e){e.printStackTrace();}

        return dto;
    }
}
