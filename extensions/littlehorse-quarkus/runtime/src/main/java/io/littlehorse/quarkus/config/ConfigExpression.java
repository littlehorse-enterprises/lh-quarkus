package io.littlehorse.quarkus.config;

import io.smallrye.common.expression.Expression;
import io.smallrye.config.SmallRyeConfig;

import org.eclipse.microprofile.config.ConfigProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ConfigExpression {
    private final String expression;
    private final SmallRyeConfig config;
    private final Expression compiledExpression;

    private ConfigExpression(String expression) {
        this.config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
        this.expression = expression;
        this.compiledExpression = Expression.compile(
                expression, Expression.Flag.LENIENT_SYNTAX, Expression.Flag.NO_TRIM);
    }

    public static ConfigExpressionResult expand(String expression) {
        ConfigExpression configExpression = new ConfigExpression(expression);
        return configExpression.expand();
    }

    public boolean hasConfig() {
        if (this.expression == null) {
            return false;
        }
        return expression.matches(".*\\$\\{.*}.*");
    }

    public ConfigExpressionResult expand() {
        if (hasConfig()) {
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

            return new ConfigExpressionResult(result, Map.copyOf(configs));
        }

        return new ConfigExpressionResult(expression, Map.of());
    }

    public static final class ConfigExpressionResult {
        private final String result;
        private final Map<String, String> members;

        private ConfigExpressionResult(String result, Map<String, String> members) {
            this.result = result;
            this.members = members;
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

        public Map<String, String> members() {
            return members;
        }
    }
}
