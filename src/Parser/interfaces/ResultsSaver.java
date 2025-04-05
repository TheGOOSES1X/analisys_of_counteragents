package Parser.interfaces;

import java.util.List;

public interface ResultsSaver<T> {
    void save(List<T> items);
    void setOutputPath(String path);
    String getOutputPath();
}