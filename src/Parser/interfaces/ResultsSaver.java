package Parser.interfaces;

import java.util.List;
import java.util.Set;

public interface ResultsSaver<T> {
    void save(Set<T> items);
    void setOutputPath(String path);
    String getOutputPath();
}