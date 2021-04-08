package io.vlingo.xoom.symbio.store.gap;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.symbio.Entry;

import java.util.List;

public interface Reader {
    Completes<Entry<String>> readOne();
    Completes<List<Entry<String>>> readNext(int count);
}
