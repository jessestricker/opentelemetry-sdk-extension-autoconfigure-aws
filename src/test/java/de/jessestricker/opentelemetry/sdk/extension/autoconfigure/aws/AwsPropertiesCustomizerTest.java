package de.jessestricker.opentelemetry.sdk.extension.autoconfigure.aws;

import static de.jessestricker.opentelemetry.sdk.extension.autoconfigure.aws.AwsPropertiesCustomizer.SECRET_NAME_PROPERTY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.google.gson.JsonSyntaxException;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AwsPropertiesCustomizerTest {
  private static final String SECRET_NAME = "theSecretName";

  @Mock private ConfigProperties configProperties;
  @Mock private SecretsManagerClient secretsManagerClient;
  private final AwsPropertiesCustomizer propertiesCustomizer =
      new AwsPropertiesCustomizer(() -> secretsManagerClient);

  private void whenConfigPropertiesGetStringThenReturnSecretName() {
    when(configProperties.getString(SECRET_NAME_PROPERTY)).thenReturn(SECRET_NAME);
  }

  private void whenSecretManagerClientGetSecretValueThenReturn(String secretString) {
    when(secretsManagerClient.getSecretValue(
            argThat((GetSecretValueRequest request) -> request.secretId().equals(SECRET_NAME))))
        .thenReturn(GetSecretValueResponse.builder().secretString(secretString).build());
  }

  @Test
  void returnsEmptyMapWhenSecretNameUnset() {
    final var properties = propertiesCustomizer.customize(configProperties);

    assertEquals(Map.of(), properties);
  }

  @Test
  void throwsWhenSecretNotFound() {
    whenConfigPropertiesGetStringThenReturnSecretName();
    when(secretsManagerClient.getSecretValue(any(GetSecretValueRequest.class)))
        .thenThrow(ResourceNotFoundException.class);

    assertThrows(
        ResourceNotFoundException.class, () -> propertiesCustomizer.customize(configProperties));
  }

  @Test
  void returnsEmptyMapWhenSecretValueIsEmpty() {
    whenConfigPropertiesGetStringThenReturnSecretName();
    whenSecretManagerClientGetSecretValueThenReturn("");

    final var properties = propertiesCustomizer.customize(configProperties);

    assertEquals(Map.of(), properties);
  }

  @Test
  void returnsPropertiesFromSecret() {
    whenConfigPropertiesGetStringThenReturnSecretName();
    whenSecretManagerClientGetSecretValueThenReturn("{ \"foo\": \"bar\" }");

    final var properties = propertiesCustomizer.customize(configProperties);

    assertEquals(Map.of("foo", "bar"), properties);
  }

  @Test
  void throwsWhenSecretContainsInvalidJson() {
    whenConfigPropertiesGetStringThenReturnSecretName();
    whenSecretManagerClientGetSecretValueThenReturn("-invalid json-");

    assertThrows(JsonSyntaxException.class, () -> propertiesCustomizer.customize(configProperties));
  }
}
