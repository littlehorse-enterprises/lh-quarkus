package io.littlehorse.quarkus.config;

import io.quarkus.arc.Unremovable;
import io.smallrye.common.expression.Expression;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.Config;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@ApplicationScoped
@Unremovable
public class ConfigEvaluator {

    private final Config config;

    public ConfigEvaluator(Config config) {
        this.config = config;
    }

    public static class ConfigExpression {
        private final String expression;
        private final String result;
        private final Map<String, String> members;
        private final int membersCount;

        private ConfigExpression(String expression, String result, Map<String, String> members) {
            this.expression = expression;
            this.result = result;
            this.members = members;
            this.membersCount = members != null ? members.size() : 0;
        }

        public String getExpression() {
            return expression;
        }

        public boolean isExpression() {
            return members != null && !members.isEmpty();
        }

        public int getMembersCount() {
            return membersCount;
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

    public ConfigExpression expand(String expression) {
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

            return new ConfigExpression(expression, result, Map.copyOf(configs));
        }

        return new ConfigExpression(expression, expression, Map.of());
    }
}
