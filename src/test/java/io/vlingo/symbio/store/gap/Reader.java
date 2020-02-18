package io.vlingo.symbio.store.gap;

import io.vlingo.common.Completes;
import io.vlingo.symbio.Entry;

import java.util.List;

public interface Reader {
    Completes<Entry<String>> readOne();
    Completes<List<Entry<String>>> readNext(int count);
}
