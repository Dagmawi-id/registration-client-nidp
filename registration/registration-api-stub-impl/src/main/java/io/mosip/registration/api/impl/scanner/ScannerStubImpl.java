package io.mosip.registration.api.impl.scanner;

import com.github.sarxos.webcam.Webcam;
import io.mosip.registration.api.docscanner.DeviceType;
import io.mosip.registration.api.docscanner.DocScannerService;
import io.mosip.registration.api.docscanner.dto.DocScanDevice;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class ScannerStubImpl implements DocScannerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerStubImpl.class);
    private static final String SERVICE_NAME = "Fayda Document Scanner";
    private static final String DEVICE_NAME = "WebCam";


    @Override
    public String getServiceName() {
        return SERVICE_NAME;
    }

    @Override
    public BufferedImage scan(DocScanDevice docScanDevice) {
        return getImageFromCamera(docScanDevice);
    }


    public BufferedImage getImageFromCamera(DocScanDevice cam) {

        VideoCapture capture = new VideoCapture(cam.getDeviceIndex());

        Mat image = new Mat();
        capture.read(image);
        final MatOfByte buf = new MatOfByte();
        Imgcodecs.imencode(".png", image, buf);
        byte ba[]=buf.toArray();
        BufferedImage bi= null;
        try {
            bi = ImageIO.read(new ByteArrayInputStream(ba));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bi;
    }


    @Override
    public List<DocScanDevice> getConnectedDevices() {

        List<Webcam> list= Webcam.getWebcams();
        List<DocScanDevice> scanDevices=new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            try {
                Webcam cam = list.get(i);

                DocScanDevice docScanDevice = new DocScanDevice();

                docScanDevice.setServiceName(getServiceName());
                docScanDevice.setDeviceType(DeviceType.CAMERA);
                docScanDevice.setName(cam.getName());

                docScanDevice.setId(String.valueOf(i));
                docScanDevice.setDeviceIndex(i);

                scanDevices.add(docScanDevice);

            } catch (Exception e) {
                System.out.println("Exception in cam : " + i);
            }
        }

        return scanDevices;
    }

    @Override
    public void stop(DocScanDevice docScanDevice) {
        //Do nothing
    }

}
