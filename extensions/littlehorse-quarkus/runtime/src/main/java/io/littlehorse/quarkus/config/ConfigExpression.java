package io.littlehorse.quarkus.config;

import io.smallrye.common.expression.Expression;
import io.smallrye.config.SmallRyeConfig;

import org.eclipse.microprofile.config.ConfigProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class ConfigExpression {

    private final String result;
    private final Map<String, String> members;

    private ConfigExpression(String result, Map<String, String> members) {
        this.result = result;
        this.members = members;
    }

    public static ConfigExpression expand(String expression) {
        SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
        Expression compiledExpression = Expression.compile(
                expression, Expression.Flag.LENIENT_SYNTAX, Expression.Flag.NO_TRIM);

        if (expression.matches(".*\\$\\{.*}.*")) {
            Map<String, String> configs = new HashMap<>();
            String result = compiledExpression.evaluate((resolveContext, stringBuilder) -> {
                Optional<String> resolve =
                        config.getOptionalValue(resolveContext.getKey(), String.class);
                if (resolve.isPresent()) {
                    stringBuilder.append(resolve.get());
                    configs.put(resolveContext.getKey(), resolve.get());
                } else if (resolveContext.hasDefault()) {
                    resolveContext.expandDefault();
                    configs.put(resolveContext.getKey(), resolveContext.getExpandedDefault());
                } else {
                    throw new NoSuchElementException("Could not expand value %s in property %s"
                            .formatted(resolveContext.getKey(), expression));
                }
            });

            return new ConfigExpression(result, Map.copyOf(configs));
        }

        return new ConfigExpression(expression, Map.of());
    }

    public boolean isExpression() {
        return members != null && !members.isEmpty();
    }

    public String asString() {
        return result;
    }

    public int asInt() {
        return Integer.parseInt(result);
    }

    public float asFloat() {
        return Float.parseFloat(result);
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public long asLong() {
        return Long.parseLong(result);
    }
}
