package com.coding.vaulthometask.strategy;

import com.coding.vaulthometask.exception.DailyLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.coding.vaulthometask.util.Constant.DAILY_AMOUNT_LIMIT;

@Component
public class DailyLimitCheckStrategy implements LimitCheckStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(DailyLimitCheckStrategy.class);

    private static final BigDecimal DAILY_LIMIT = new BigDecimal(DAILY_AMOUNT_LIMIT);

    @Override
    public void checkLimit(LimitCheckContext context) {
        BigDecimal newDailyTotal = context.getStats().getDailyTotal().add(context.getLoadAmount());
        if (newDailyTotal.compareTo(DAILY_LIMIT) > 0) {
            LOGGER.warn("Daily limit exceeded: current daily total = {} + load amount({}) > {}",
                    context.getStats().getDailyTotal(), context.getLoadAmount(), DAILY_LIMIT);
            throw new DailyLimitExceededException(
                    String.format("Daily limit exceeded: current daily total = %s + load amount(%s) > %s",
                            context.getStats().getDailyTotal(), context.getLoadAmount(), DAILY_LIMIT)
            );
        }
    }
}
