package de.jessestricker.opentelemetry.sdk.extension.autoconfigure.aws;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

public class AwsAutoConfigurationCustomizerProvider implements AutoConfigurationCustomizerProvider {
  @Override
  public void customize(AutoConfigurationCustomizer autoConfiguration) {
    final var propertiesCustomizer = new AwsPropertiesCustomizer(this::createSecretsManagerClient);
    autoConfiguration.addPropertiesCustomizer(propertiesCustomizer::customize);
  }

  private SecretsManagerClient createSecretsManagerClient() {
    return SecretsManagerClient.builder()
        .httpClientBuilder(UrlConnectionHttpClient.builder())
        .build();
  }
}
