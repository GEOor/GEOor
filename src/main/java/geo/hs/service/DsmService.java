package geo.hs.service;

import geo.hs.model.dsm.Dsm;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class DsmService {

    private String rootPath = "src/main/java/geo/hs/files";
    public ArrayList<Dsm> Dsms = new ArrayList<Dsm>();

    public ArrayList<Dsm> prepareDsm(){
        try (
                DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(rootPath))) {
            for (Path file: stream) {
                System.out.println(file.getFileName());
                Dsm Dsm = new Dsm(rootPath + "/" + file.getFileName().toString());
                Dsms.add(Dsm);
            }
        } catch (IOException | DirectoryIteratorException ex) {
            System.err.println(ex);
        }

        return Dsms;
    }
}
