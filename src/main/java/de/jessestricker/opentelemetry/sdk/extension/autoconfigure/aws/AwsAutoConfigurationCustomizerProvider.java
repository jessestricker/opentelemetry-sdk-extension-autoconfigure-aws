package de.jessestricker.opentelemetry.sdk.extension.autoconfigure.aws;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

public class AwsAutoConfigurationCustomizerProvider implements AutoConfigurationCustomizerProvider {
  @Override
  public void customize(AutoConfigurationCustomizer autoConfiguration) {
    final var propertiesCustomizer = new AwsPropertiesCustomizer(SecretsManagerClient::create);
    autoConfiguration.addPropertiesCustomizer(propertiesCustomizer::customize);
  }
}
