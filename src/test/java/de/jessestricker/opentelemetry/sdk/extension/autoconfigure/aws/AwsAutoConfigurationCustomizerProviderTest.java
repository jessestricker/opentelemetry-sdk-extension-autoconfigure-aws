package de.jessestricker.opentelemetry.sdk.extension.autoconfigure.aws;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AwsAutoConfigurationCustomizerProviderTest {
  @Mock private AutoConfigurationCustomizer autoConfiguration;

  @Test
  void callsAddPropertiesCustomizer() {
    final var autoConfigurationCustomizerProvider = new AwsAutoConfigurationCustomizerProvider();
    autoConfigurationCustomizerProvider.customize(autoConfiguration);

    verify(autoConfiguration).addPropertiesCustomizer(any());
  }
}
