package Parser.interfaces;

import Parser.Database.models.Purchase;
import Parser.implementations.Parser44.PurchaseParser44;

import java.util.List;
import java.util.function.Consumer;

public interface Parser {


    void pauseParser();

    void parseUrlsParallel(List<String> urls, Consumer<PurchaseParser44.ParseResult> callback, int threadCount, Consumer<Integer> progressCallback);

    void stopParser();

    void parse();

    void resumeParser();

    void parseSupplierLitigations();


    class ParseResult {
        public final String url;
        public final Purchase purchaseData;
        public final Exception error;

        public ParseResult(String url, Purchase purchaseData, Exception error) {
            this.url = url;
            this.purchaseData = purchaseData;
            this.error = error;
        }
    }
}