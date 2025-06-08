package de.jessestricker.opentelemetry.sdk.extension.autoconfigure.aws;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

public class AwsPropertiesCustomizer {
  private static final String SECRET_NAME_PROPERTY = "otel.config.aws.secret-name";
  private static final Gson GSON = new Gson();

  private final Supplier<SecretsManagerClient> secretsManagerClientSupplier;

  public AwsPropertiesCustomizer(Supplier<SecretsManagerClient> secretsManagerClientSupplier) {
    this.secretsManagerClientSupplier = secretsManagerClientSupplier;
  }

  public Map<String, String> customize(ConfigProperties configProperties) {
    final var secretName = getSecretName(configProperties);
    if (secretName == null) {
      return Map.of();
    }
    final var secretValue = getSecretValue(secretName);
    return parseProperties(secretValue);
  }

  private String getSecretName(ConfigProperties configProperties) {
    return configProperties.getString(SECRET_NAME_PROPERTY);
  }

  private String getSecretValue(String secretName) {
    try (var client = secretsManagerClientSupplier.get()) {
      final var request = GetSecretValueRequest.builder().secretId(secretName).build();
      final var response = client.getSecretValue(request);
      return response.secretString();
    }
  }

  private Map<String, String> parseProperties(String secretValue) {
    final var propertiesType = new TypeToken<Map<String, String>>() {};
    final var properties = GSON.fromJson(secretValue, propertiesType);
    return Objects.requireNonNullElseGet(properties, Map::of);
  }
}
