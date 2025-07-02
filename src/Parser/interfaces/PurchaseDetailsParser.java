package Parser.interfaces;

import Parser.implementations.Parser44.PurchaseParser44;

import java.util.List;
import java.util.function.Consumer;

public interface PurchaseDetailsParser {
    void parseUrlsParallel(List<String> urls, Consumer<PurchaseParser44.ParseResult> callback, int threadCount, Consumer<Integer> progressCallback);

    void pauseParser();

    void resumeParser();

    void stopParser();

    void parseSupplierLitigations();
    void parseSupplierStatuses();
    void cleanupDownloadDirectory();

    void parseSupplierStatusesParallel(int selectedThreadCount);

    void parseSupplierLitigationsParallel(int selectedThreadCount);
}