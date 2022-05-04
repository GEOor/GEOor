package geo.hs.model.shp;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Shp {
    private File file; // TestDraw 호출 시 사용
    private DataStore dataStore;
    private FeatureSource<SimpleFeatureType, SimpleFeature> source;

    public Shp(String filePath) {
        file = new File(filePath);
        setDataStore(file);
        setSource();
    }

    private void setDataStore(File shpFile) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("url", shpFile.toURI().toURL());
            map.put("charset", "EUC-KR");
            dataStore = DataStoreFinder.getDataStore(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setSource() {
        try {
            source = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printColumns() throws IOException {
        SimpleFeatureType schema = source.getSchema();
        Query query = new Query(schema.getTypeName());
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(query);
        System.out.println(file.getName());
        try (FeatureIterator<SimpleFeature> features = collection.features()) {
            SimpleFeature feature = features.next();
            for (Property attribute : feature.getProperties()) {
                System.out.println(attribute.getName() + ":" + attribute.getValue());
            }
        }
    }

    public DataStore getDataStore() {
        return dataStore;
    }

    public FeatureSource<SimpleFeatureType, SimpleFeature> getSource() {
        return source;
    }

    public FeatureIterator<SimpleFeature> getFeature() {
        Filter filter = Filter.INCLUDE;
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = null;
        try {
            collection = source.getFeatures(filter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return collection.features();
    }

    public File getFile() {
        return file;
    }
}
