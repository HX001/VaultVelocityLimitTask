package com.coding.vaulthometask.strategy;

import com.coding.vaulthometask.exception.WeeklyLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.coding.vaulthometask.util.Constant.WEEKLY_AMOUNT_LIMIT;

@Component
public class WeeklyLimitCheckStrategy implements LimitCheckStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeeklyLimitCheckStrategy.class);
    private static final BigDecimal WEEKLY_LIMIT = new BigDecimal(WEEKLY_AMOUNT_LIMIT);

    @Override
    public void checkLimit(LimitCheckContext context) {
        BigDecimal newWeeklyTotal = context.getStats().getWeeklyTotal().add(context.getLoadAmount());
        if (newWeeklyTotal.compareTo(WEEKLY_LIMIT) > 0) {
            LOGGER.warn("Weekly limit exceeded: current weekly total = {} + load amount({}) > {}",
                    context.getStats().getWeeklyTotal(), context.getLoadAmount(), WEEKLY_LIMIT);
            throw new WeeklyLimitExceededException(
                    String.format("Weekly limit exceeded: current weekly total = %s + load amount(%s) > %s",
                            context.getStats().getWeeklyTotal(), context.getLoadAmount(), WEEKLY_LIMIT)
            );
        }
    }
}

