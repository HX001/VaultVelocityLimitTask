package com.coding.vaulthometask.strategy;

import com.coding.vaulthometask.exception.DailyCountExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.coding.vaulthometask.util.Constant.DAILY_COUNT_LIMIT;

@Component
public class DailyCountCheckStrategy implements LimitCheckStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(DailyCountCheckStrategy.class);

    @Override
    public void checkLimit(LimitCheckContext context) {
        int newCount = context.getStats().getDailyCount() + 1;
        if (newCount > DAILY_COUNT_LIMIT) {
            LOGGER.warn("Daily count exceeded: current daily count = {} + 1 > {}", context.getStats().getDailyCount(), DAILY_COUNT_LIMIT);
            throw new DailyCountExceededException(
                    String.format("Daily count exceeded: current daily count = %d + 1 > %d",
                            context.getStats().getDailyCount(), DAILY_COUNT_LIMIT)
            );
        }
    }
}

